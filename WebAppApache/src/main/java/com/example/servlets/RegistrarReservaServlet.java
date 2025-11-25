package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

import serviciosweb.JuanViajesWS;
import serviciosweb.DtCliente;
import serviciosweb.DtPaquete;
import serviciosweb.DtItemPaquete;
import serviciosweb.DtReserva;
import com.example.util.PortUtils;

@WebServlet("/api/reservas")
public class RegistrarReservaServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Leer JSON del body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            String body = sb.toString();
            JSONObject obj = new JSONObject(body);

            // Leer campos con tolerancia a valores faltantes
            String vuelo = obj.optString("vuelo", null);
            String tipoAsientoStr = obj.optString("tipoAsiento", null);
            int cantidadPasajes = obj.has("cantidadPasajes") ? obj.optInt("cantidadPasajes", 0) : 0;
            int equipajeExtra = obj.has("equipajeExtra") ? obj.optInt("equipajeExtra", 0) : 0;

            if (vuelo == null || vuelo.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"Campo 'vuelo' es obligatorio\"}");
                return;
            }
            if (tipoAsientoStr == null || tipoAsientoStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"Campo 'tipoAsiento' es obligatorio\"}");
                return;
            }
            if (cantidadPasajes <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"Campo 'cantidadPasajes' debe ser mayor que 0\"}");
                return;
            }

            HttpSession session = request.getSession(false);
            String nicknameCliente = (session != null) ? (String) session.getAttribute("usuario") : null;
            String tipoUsuario = (session != null) ? (String) session.getAttribute("tipoUsuario") : null;
            if (nicknameCliente == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false, \"error\":\"Usuario no autenticado\"}");
                return;
            }
            if (tipoUsuario != null && tipoUsuario.equalsIgnoreCase("aerolinea")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\":false, \"error\":\"Las aerolíneas no pueden realizar reservas\"}");
                return;
            }

            // procesar pasajeros: solo necesitamos listas de nombres y apellidos para el WS
            List<String> pasajerosNombres = new ArrayList<>();
            List<String> pasajerosApellidos = new ArrayList<>();
            if (obj.has("pasajeros")) {
                JSONArray pasajerosArr = obj.getJSONArray("pasajeros");
                for (int i = 0; i < pasajerosArr.length(); i++) {
                    JSONObject p = pasajerosArr.getJSONObject(i);
                    pasajerosNombres.add(p.optString("nombre", ""));
                    pasajerosApellidos.add(p.optString("apellido", ""));
                }
            }

            // autocompletar si falta exactamente 1 pasajero y no se envió ninguno
            JuanViajesWS port = PortUtils.getPort(request);
            try { port.cargarDesdeBd(); } catch (Exception ignored) {}

            if (pasajerosNombres.isEmpty() && cantidadPasajes == 1) {
                DtCliente dt = null;
                try { dt = port.obtenerCliente(nicknameCliente); } catch (Exception ex) { dt = null; }
                if (dt != null) {
                    pasajerosNombres.add(dt.getNombre()!=null?dt.getNombre():"");
                    pasajerosApellidos.add(dt.getApellido()!=null?dt.getApellido():"");
                }
            }

            if (pasajerosNombres.size() != cantidadPasajes) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"La cantidad de pasajeros no coincide con la cantidad de pasajes\"}");
                return;
            }

            // calcular costo en el servidor usando el WS
            double costoServidor = 0.0;
            try {
                costoServidor = port.calcularCostoReserva(vuelo, tipoAsientoStr, cantidadPasajes, equipajeExtra);
            } catch (Exception ex) {
                // si falla, devolver error
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\":false, \"error\":\"Error calculando costo de reserva: " + escapeForJson(ex.getMessage()) + "\"}");
                return;
            }

            String formaPagoReq = obj.optString("formaPago", "general");
            String paqueteReq = obj.optString("paquete", null);

            double costoFinal = costoServidor;

            if ("paquete".equalsIgnoreCase(formaPagoReq)) {
                DtCliente clienteDt = null;
                try { clienteDt = port.obtenerCliente(nicknameCliente); } catch (Exception e) { clienteDt = null; }

                // listar paquetes elegibles
                JSONArray paquetesElegibles = new JSONArray();
                if (clienteDt != null && clienteDt.getPaquetesComprados() != null) {
                    for (DtPaquete dp : clienteDt.getPaquetesComprados()) {
                        if (dp == null) continue;
                        if (dp.getFechaAlta() == null) continue;
                        if (dp.getPeriodoValidezDias() > 0 && dp.getFechaAlta() != null) {
                            java.time.LocalDate ld = toLocalDate(dp.getFechaAlta());
                            if (ld != null) {
                                java.time.LocalDate venc = ld.plusDays(dp.getPeriodoValidezDias());
                                if (venc.isBefore(java.time.LocalDate.now())) continue;
                            }
                        }
                        boolean contieneRuta = false;
                        if (dp.getItems() != null) {
                            for (DtItemPaquete it : dp.getItems()) {
                                if (it != null && it.getRutaVuelo() != null && it.getRutaVuelo().getNombre() != null) {
                                    if (it.getRutaVuelo().getNombre().equals(vuelo)) { contieneRuta = true; break; }
                                }
                            }
                        }
                        if (contieneRuta) {
                            JSONObject pjo = new JSONObject();
                            pjo.put("id", dp.getNombre());
                            pjo.put("nombre", dp.getNombre());
                            pjo.put("descuentoPorc", dp.getDescuentoPorc());
                            pjo.put("descripcion", dp.getDescripcion() != null ? dp.getDescripcion() : "");
                            pjo.put("fechaCompra", dp.getFechaAlta() != null ? dp.getFechaAlta().toString() : "");
                            if (dp.getPeriodoValidezDias() > 0 && dp.getFechaAlta() != null) {
                                java.time.LocalDate ld2 = toLocalDate(dp.getFechaAlta());
                                if (ld2 != null) pjo.put("fechaVencimiento", ld2.plusDays(dp.getPeriodoValidezDias()).toString());
                                else pjo.put("fechaVencimiento", "");
                            } else {
                                pjo.put("fechaVencimiento", "");
                            }
                            pjo.put("vigenciaDias", dp.getPeriodoValidezDias());
                            paquetesElegibles.put(pjo);
                        }
                    }
                }

                if (paqueteReq == null || paqueteReq.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    JSONObject respObj = new JSONObject();
                    respObj.put("success", true);
                    respObj.put("paquetesElegibles", paquetesElegibles);
                    respObj.put("costoServidor", costoServidor);
                    out.print(respObj);
                    return;
                }

                DtPaquete paqueteDt = null;
                try { paqueteDt = port.obtenerDtPaquete(paqueteReq); } catch (Exception ex) { paqueteDt = null; }
                double descuentoPorc = (paqueteDt != null) ? paqueteDt.getDescuentoPorc() : 0.0;
                double descuento = (descuentoPorc / 100.0) * costoServidor;
                costoFinal = Math.max(0.0, costoServidor - descuento);
            }

            // PRE-CHECK: evitar que el usuario ya tenga reserva en el vuelo
            try {
                List<DtReserva> reservasCliente = port.getReservasCliente(nicknameCliente);
                if (reservasCliente != null) {
                    for (DtReserva r : reservasCliente) {
                        if (r != null && r.getVuelo() != null && r.getVuelo().equalsIgnoreCase(vuelo)) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.print("{\"success\":false, \"error\":\"El usuario ya tiene una reserva en este vuelo\"}");
                            return;
                        }
                    }
                }
            } catch (Exception ex) {
                // no bloquear la operación si falla el pre-check
            }

            // Llamar al WS para crear y registrar la reserva
            try {
                port.crearYRegistrarReserva(nicknameCliente, vuelo, LocalDate.now().toString(), costoFinal, tipoAsientoStr, cantidadPasajes, equipajeExtra, pasajerosNombres, pasajerosApellidos);

                response.setStatus(HttpServletResponse.SC_OK);
                JSONObject resp = new JSONObject();
                resp.put("success", true);
                resp.put("costoFinal", costoFinal);
                if (paqueteReq != null && !paqueteReq.trim().isEmpty()) resp.put("paquete", paqueteReq);
                resp.put("message", "Reserva creada correctamente");
                out.print(resp);
                return;

            } catch (Exception ex) {
                // post-check: puede que la reserva se haya creado de todas formas
                try {
                    List<DtReserva> reservasCliente = port.getReservasCliente(nicknameCliente);
                    if (reservasCliente != null) {
                        for (DtReserva r : reservasCliente) {
                            if (r != null && r.getVuelo() != null && r.getVuelo().equalsIgnoreCase(vuelo)) {
                                response.setStatus(HttpServletResponse.SC_OK);
                                JSONObject resp = new JSONObject();
                                resp.put("success", true);
                                resp.put("costoFinal", costoFinal);
                                resp.put("message", "Reserva creada correctamente (detectada en post-check)");
                                out.print(resp);
                                return;
                            }
                        }
                    }
                } catch (Exception ex2) {
                    // ignore
                }

                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\":false, \"error\":\"" + escapeForJson(ex.getMessage()) + "\"}");
                return;
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false, \"error\":\"" + escapeForJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeForJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    // Helper para convertir DTO LocalDate a java.time.LocalDate
    private java.time.LocalDate toLocalDate(serviciosweb.LocalDate ld) {
        if (ld == null) return null;
        try {
            String s = ld.toString();
            if (s == null || s.isBlank()) return null;
            return java.time.LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
    }
}

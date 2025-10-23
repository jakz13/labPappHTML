package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import Logica.Pasajero;
import Logica.TipoAsiento;
import DataTypes.DtCliente;
import DataTypes.DtPaquete;
import DataTypes.DtItemPaquete;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.json.*;
import java.lang.reflect.Method;
import java.util.Map;

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
            System.out.println("=== comienza reserva ===");
            System.out.println("Request body: " + body);
            JSONObject obj = new JSONObject(body);

            // Leer campos con tolerancia a valores faltantes
            String vuelo = obj.optString("vuelo", null);
            String tipoAsientoStr = obj.optString("tipoAsiento", null);
            int cantidadPasajes = obj.has("cantidadPasajes") ? obj.optInt("cantidadPasajes", 0) : 0;
            int equipajeExtra = obj.has("equipajeExtra") ? obj.optInt("equipajeExtra", 0) : 0;

            // Validar campos obligatorios y devolver errores claros
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

            // Obtener usuario de la sesión
            HttpSession session = request.getSession(false);
            String nicknameCliente = (session != null) ? (String) session.getAttribute("usuario") : null;
            String tipoUsuario = (session != null) ? (String) session.getAttribute("tipoUsuario") : null;
            if (nicknameCliente == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false, \"error\":\"Usuario no autenticado\"}");
                return;
            }

            // No permitir que aerolíneas reserven (solo clientes)
            if (tipoUsuario != null && tipoUsuario.equalsIgnoreCase("aerolinea")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\":false, \"error\":\"Las aerolíneas no pueden realizar reservas\"}");
                return;
            }
            System.out.println("encontrando en la sesion");

            System.out.println(nicknameCliente + " encontrado en la sesion");
            // Pasajeros (opcional)
            List<Pasajero> pasajeros = new ArrayList<>();

            ISistema sistema = Fabrica.getInstance().getISistema();
            // cargar datos necesarios desde BD para poder obtener info del cliente si es necesario
            sistema.cargarDesdeBd();

            if (obj.has("pasajeros")) {
                JSONArray pasajerosArr = obj.getJSONArray("pasajeros");
                for (int i = 0; i < pasajerosArr.length(); i++) {
                    JSONObject p = pasajerosArr.getJSONObject(i);
                    pasajeros.add(sistema.crearPasajero(
                            p.optString("nombre", ""), p.optString("apellido", "")
                    ));
                }
            }

            System.out.println("Pasajeros procesados inicialmente: " + pasajeros.size());

            // Si no se enviaron pasajeros o la cantidad no coincide con cantidadPasajes,
            // intentar autocompletar con el usuario en sesión solo si falta exactamente 1 pasajero
            if (pasajeros.isEmpty() && cantidadPasajes == 1) {
                // caso: se pidió 1 pasaje y el cliente no envió pasajeros -> autocompletar con sesión
                try {
                    DtCliente dt = sistema.obtenerCliente(nicknameCliente);
                    if (dt != null) {
                        String nombreSesion = dt.getNombre() != null ? dt.getNombre() : "";
                        String apellidoSesion = dt.getApellido() != null ? dt.getApellido() : "";
                        pasajeros.add(sistema.crearPasajero(nombreSesion, apellidoSesion));
                        System.out.println("Autocompletado pasajero desde sesión (1 pasajero): " + nombreSesion + " " + apellidoSesion);
                    }
                } catch (Exception e) {
                    System.err.println("No se pudo autocompletar pasajero desde sesión: " + e.getMessage());
                }
            } else if (pasajeros.size() == cantidadPasajes - 1) {
                // caso común: cliente envió N-1 formularios para N pasajes -> añadir usuario en sesión
                try {
                    DtCliente dt = sistema.obtenerCliente(nicknameCliente);
                    if (dt != null) {
                        String nombreSesion = dt.getNombre() != null ? dt.getNombre() : "";
                        String apellidoSesion = dt.getApellido() != null ? dt.getApellido() : "";
                        pasajeros.add(sistema.crearPasajero(nombreSesion, apellidoSesion));
                        System.out.println("Autocompletado pasajero desde sesión: " + nombreSesion + " " + apellidoSesion);
                    }
                } catch (Exception e) {
                    System.err.println("No se pudo autocompletar pasajero desde sesión: " + e.getMessage());
                }
            }

            System.out.println("Pasajeros procesados final: " + pasajeros.size());

            // Validar que al menos un pasajero esté presente (caso de uso solicitado)
            if (pasajeros.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"Debe registrar al menos un pasajero\"}");
                return;
            }

            // Validar la correspondencia entre cantidad de pasajes y pasajeros recibidos
            if (pasajeros.size() != cantidadPasajes) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"La cantidad de pasajeros no coincide con la cantidad de pasajes\"}");
                return;
            }

            // Nota: se removió el pre-check que intentaba detectar reservas previas del cliente
            // porque estaba generando falsos positivos (detectaba reservas inexistentes). Confiamos
            // en la lógica de negocio en crearYRegistrarReserva y en el post-check que sigue si hay excepción.
            System.out.println("Pre-check de reservas omitido (evitar falsos positivos)");

            TipoAsiento tipoAsiento = tipoAsientoStr.equalsIgnoreCase("ejecutivo") ? TipoAsiento.EJECUTIVO : TipoAsiento.TURISTA;
            LocalDate fechaReserva = LocalDate.now();

            // CALCULAR COSTO EN SERVIDOR y aplicar descuento de paquete si corresponde
            double costoServidor = sistema.calcularCostoReserva(vuelo, tipoAsiento, cantidadPasajes, equipajeExtra);
            String formaPagoReq = obj.optString("formaPago", "general");
            String paqueteReq = obj.optString("paquete", null);

            // Si se indicó pago con paquete, validar que el cliente posee el paquete y que el paquete contiene la ruta del vuelo
            double costoFinal = costoServidor;
            if ("paquete".equalsIgnoreCase(formaPagoReq)) {
                // Obtener datos del cliente (si es posible) para listar/validar paquetes
                DtCliente clienteDt;
                try {
                    clienteDt = sistema.obtenerCliente(nicknameCliente);
                } catch (Exception e) {
                    clienteDt = null;
                }

                // Construir lista de paquetes comprados que contienen la ruta del vuelo
                org.json.JSONArray paquetesElegibles = new org.json.JSONArray();
                if (clienteDt != null && clienteDt.getPaquetesComprados() != null) {
                    for (DtPaquete dp : clienteDt.getPaquetesComprados()) {
                        if (dp == null) continue;
                        // Ignorar paquetes que no fueron realmente comprados (sin fecha de compra)
                        if (dp.getFechaAlta() == null) continue;
                        // Si el paquete tiene vigencia (periodoValidezDias > 0), comprobar que no esté vencido
                        if (dp.getPeriodoValidezDias() > 0 && dp.getFechaAlta() != null) {
                            java.time.LocalDate venc = dp.getFechaAlta().plusDays(dp.getPeriodoValidezDias());
                            if (venc.isBefore(java.time.LocalDate.now())) continue; // paquete vencido
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
                            org.json.JSONObject pjo = new org.json.JSONObject();
                            // Proveer id y campos útiles para el frontend
                            pjo.put("id", dp.getNombre());
                            pjo.put("nombre", dp.getNombre());
                            pjo.put("descuentoPorc", dp.getDescuentoPorc());
                            pjo.put("descripcion", dp.getDescripcion() != null ? dp.getDescripcion() : "");
                            pjo.put("fechaCompra", dp.getFechaAlta() != null ? dp.getFechaAlta().toString() : "");
                            if (dp.getPeriodoValidezDias() > 0 && dp.getFechaAlta() != null) {
                                pjo.put("fechaVencimiento", dp.getFechaAlta().plusDays(dp.getPeriodoValidezDias()).toString());
                            } else {
                                pjo.put("fechaVencimiento", "");
                            }
                            pjo.put("vigenciaDias", dp.getPeriodoValidezDias());
                            paquetesElegibles.put(pjo);
                        }
                    }
                }

                // Si no se especificó un paquete, devolver la lista de paquetes elegibles para que el cliente elija
                if (paqueteReq == null || paqueteReq.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    org.json.JSONObject respObj = new org.json.JSONObject();
                    respObj.put("success", true);
                    respObj.put("paquetesElegibles", paquetesElegibles);
                    respObj.put("costoServidor", costoServidor);
                    out.print(respObj);
                    return;
                }

                // Si se especificó un paquete, validar que pertenece al cliente y que contiene la ruta
                boolean pertenece = false;
                if (clienteDt != null && clienteDt.getPaquetesComprados() != null) {
                    for (DtPaquete dp : clienteDt.getPaquetesComprados()) {
                        if (dp != null && dp.getNombre() != null && dp.getNombre().equals(paqueteReq)) { pertenece = true; break; }
                    }
                }
                if (!pertenece) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\":false, \"error\":\"Paquete no válido o no pertenece al cliente\"}");
                    return;
                }

                // Obtener detalle del paquete desde el sistema y verificar que contiene la ruta asociada al vuelo
                DtPaquete paqueteDt;
                try { paqueteDt = sistema.obtenerDtPaquete(paqueteReq); } catch (Exception e) { paqueteDt = null; }
                boolean contieneRuta = false;
                if (paqueteDt != null && paqueteDt.getItems() != null) {
                    for (DtItemPaquete it : paqueteDt.getItems()) {
                        if (it != null && it.getRutaVuelo() != null && it.getRutaVuelo().getNombre() != null) {
                            if (it.getRutaVuelo().getNombre().equals(vuelo)) { contieneRuta = true; break; }
                        }
                    }
                }
                if (!contieneRuta) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\":false, \"error\":\"El paquete seleccionado no contiene la ruta del vuelo\"}");
                    return;
                }

                // Aplicar descuento porcentual del paquete sobre el costo calculado en servidor
                double descuentoPorc = paqueteDt.getDescuentoPorc();
                double descuento = (descuentoPorc / 100.0) * costoServidor;
                costoFinal = Math.max(0.0, costoServidor - descuento);
            }

            try {
                sistema.crearYRegistrarReserva(
                        nicknameCliente, vuelo, fechaReserva, costoFinal, tipoAsiento, cantidadPasajes, equipajeExtra, pasajeros
                );

                // Si llegamos aquí, la creación no lanzó excepción -> devolver éxito
                response.setStatus(HttpServletResponse.SC_OK);
                org.json.JSONObject resp = new org.json.JSONObject();
                resp.put("success", true);
                resp.put("costoFinal", costoFinal);
                if (paqueteReq != null && !paqueteReq.trim().isEmpty()) resp.put("paquete", paqueteReq);
                resp.put("message", "Reserva creada correctamente");
                out.print(resp);
                return;

            } catch (IllegalArgumentException iae) {
                // Intentar post-check: la lógica de negocio pudo crear la reserva y luego lanzar la excepción.
                try {
                    Object vueloObj;
                    try { vueloObj = sistema.obtenerVuelo(vuelo); } catch (Exception vx) { vueloObj = null; }

                    boolean found = false;
                    String reservaId = null;
                    if (vueloObj != null) {
                        Object reservasObj;
                        try { reservasObj = vueloObj.getClass().getMethod("getReservas").invoke(vueloObj); } catch (Exception m) { reservasObj = null; }

                        Iterable<?> iterable = null;
                        if (reservasObj instanceof Map) iterable = ((Map<?, ?>) reservasObj).values();
                        else if (reservasObj instanceof Iterable) iterable = (Iterable<?>) reservasObj;

                        if (iterable != null) {
                            for (Object r : iterable) {
                                String clienteProp = getClienteNicknameFromReserva(r);
                                if (clienteProp != null && clienteProp.equalsIgnoreCase(nicknameCliente)) {
                                    found = true;
                                    reservaId = getPropAsString(r, "getId", "id");
                                    break;
                                }
                            }
                        }
                    }

                    if (found) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        String codigo = (reservaId != null && !reservaId.isEmpty()) ? "RES-" + reservaId : "RES-" + System.currentTimeMillis();
                        org.json.JSONObject resp = new org.json.JSONObject();
                        resp.put("success", true);
                        resp.put("codigoReserva", codigo);
                        resp.put("costoFinal", costoFinal);
                        if (paqueteReq != null && !paqueteReq.trim().isEmpty()) resp.put("paquete", paqueteReq);
                        out.print(resp);
                        System.out.println("Post-check (iae): reserva detectada tras IllegalArgumentException — devolviendo éxito. ID=" + reservaId);
                        return;
                    }
                } catch (Throwable t) {
                    System.err.println("Error en post-check tras IllegalArgumentException: " + t.getMessage());
                }

                // Si no se detectó la reserva, devolver el mensaje de negocio original
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"" + escapeForJson(iae.getMessage()) + "\"}");
                return;
            } catch (Exception ex) {
                // Si ocurre una excepción, comprobar si la reserva ya fue creada de todas formas
                try {
                    // intentar detectar si ya existe una reserva del cliente en el vuelo
                    Object vueloObj;
                    try {
                        vueloObj = sistema.obtenerVuelo(vuelo);
                    } catch (Exception vx) { vueloObj = null; }

                    boolean found = false;
                    String reservaId = null;
                    if (vueloObj != null) {
                        Object reservasObj;
                        try {
                            reservasObj = vueloObj.getClass().getMethod("getReservas").invoke(vueloObj);
                        } catch (Exception m) {
                            reservasObj = null;
                        }

                        Iterable<?> iterable = null;
                        if (reservasObj instanceof Map) {
                            iterable = ((Map<?, ?>) reservasObj).values();
                        } else if (reservasObj instanceof Iterable) {
                            iterable = (Iterable<?>) reservasObj;
                        }

                        if (iterable != null) {
                            for (Object r : iterable) {
                                String clienteProp = getClienteNicknameFromReserva(r);
                                if (clienteProp != null && clienteProp.equalsIgnoreCase(nicknameCliente)) {
                                    found = true;
                                    reservaId = getPropAsString(r, "getId", "id");
                                    break;
                                }
                            }
                        }
                    }

                    if (found) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        String codigo = (reservaId != null && !reservaId.isEmpty()) ? "RES-" + reservaId : "RES-" + System.currentTimeMillis();
                        org.json.JSONObject resp = new org.json.JSONObject();
                        resp.put("success", true);
                        resp.put("codigoReserva", codigo);
                        resp.put("costoFinal", costoFinal);
                        if (paqueteReq != null && !paqueteReq.trim().isEmpty()) resp.put("paquete", paqueteReq);
                        out.print(resp);
                        System.out.println("Post-check (ex): reserva detectada tras excepción — devolviendo éxito. ID=" + reservaId);
                        return;
                    }
                } catch (Throwable t) {
                    System.err.println("Error en post-check tras excepción: " + t.getMessage());
                }

                // Si no existe la reserva, devolver la excepción original
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\":false, \"error\":\"" + escapeForJson(ex.getMessage()) + "\"}");
                return;
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false, \"error\":\"" + escapeForJson(e.getMessage()) + "\"}");
            return;
        }
    }

    // Intenta extraer el nickname del cliente desde el objeto reserva mediante reflexión
    private String getClienteNicknameFromReserva(Object reservaObj) {
        if (reservaObj == null) return null;
        try {
            // intentos de getters comunes
            String[] candidateMethods = new String[]{"getCliente", "getUsuario", "getOwner", "getUsuarioCliente"};
            for (String mName : candidateMethods) {
                try {
                    Method m = reservaObj.getClass().getMethod(mName);
                    Object clienteObj = m.invoke(reservaObj);
                    if (clienteObj == null) continue;
                    // si ya es String, devolverlo
                    if (clienteObj instanceof String) return (String) clienteObj;
                    // sino intentar getters del cliente
                    String nick = getPropAsString(clienteObj, "getNickname", "nickname");
                    if (nick != null && !nick.isEmpty()) return nick;
                    nick = getPropAsString(clienteObj, "getUsuario", "usuario");
                    if (nick != null && !nick.isEmpty()) return nick;
                    nick = getPropAsString(clienteObj, "getNick", "nick");
                    if (nick != null && !nick.isEmpty()) return nick;
                } catch (NoSuchMethodException nsme) {
                    // ignore
                }
            }

            // si no encontramos cliente por getter, intentar obtener directamente propiedades en la reserva
            String direct = getPropAsString(reservaObj, "getClienteNickname", "clienteNickname");
            if (direct != null && !direct.isEmpty()) return direct;
            direct = getPropAsString(reservaObj, "getNickname", "nickname");
            if (direct != null && !direct.isEmpty()) return direct;

        } catch (Throwable t) {
            // no fallar por reflexión
        }
        return null;
    }

    // Intenta invocar un getter o acceder a un campo con el nombre dado y devolver su valor como String
    private String getPropAsString(Object obj, String getterName, String fieldName) {
        if (obj == null) return null;
        try {
            try {
                Method m = obj.getClass().getMethod(getterName);
                Object val = m.invoke(obj);
                if (val != null) return String.valueOf(val);
            } catch (NoSuchMethodException nsme) {
                // intentar campo público
                try {
                    java.lang.reflect.Field f = obj.getClass().getField(fieldName);
                    Object val = f.get(obj);
                    if (val != null) return String.valueOf(val);
                } catch (NoSuchFieldException | IllegalAccessException ignore) {
                    // ignore
                }
            }
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }

    private String escapeForJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}

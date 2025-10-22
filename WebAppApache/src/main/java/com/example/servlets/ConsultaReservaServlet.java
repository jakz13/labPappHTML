package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.*;
import Logica.Vuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

@WebServlet("/api/consulta-reserva")
public class ConsultaReservaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String usuario = request.getParameter("usuario");
        String vuelo = request.getParameter("vuelo");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            if ("reservas-cliente".equals(action) && usuario != null) {
                obtenerReservasCliente(sistema, usuario, out);
            } else if ("reservas-vuelo".equals(action) && vuelo != null) {
                obtenerReservasVuelo(sistema, vuelo, out);
            } else if ("reserva-cliente-vuelo".equals(action) && usuario != null && vuelo != null) {
                obtenerReservaClienteEnVuelo(sistema, usuario, vuelo, out);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Parámetros inválidos\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno: " + escapeJson(String.valueOf(e.getMessage())) + "\"}");
        }
    }

    private void obtenerReservasCliente(ISistema sistema, String usuario, PrintWriter out) {
        try {
            DtCliente cliente = sistema.obtenerCliente(usuario);
            if (cliente != null) {
                List<DtReserva> reservas = cliente.getReservas();
                if (reservas == null) reservas = Collections.emptyList();
                escribirReservasJSON(reservas, out);
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            out.print("[]");
        }
    }

    private void obtenerReservasVuelo(ISistema sistema, String nombreVuelo, PrintWriter out) {
        try {
            Vuelo vuelo = sistema.obtenerVuelo(nombreVuelo);
            if (vuelo != null) {
                Object reservasObj = vuelo.getReservas();
                Collection<?> reservasColl = null;
                if (reservasObj instanceof Collection) {
                    reservasColl = (Collection<?>) reservasObj;
                } else if (reservasObj instanceof Map) {
                    reservasColl = ((Map<?, ?>) reservasObj).values();
                }
                if (reservasColl == null) reservasColl = Collections.emptyList();

                List<Map<String, Object>> reservasCompletas = new ArrayList<>();

                for (Object reservaObj : reservasColl) {
                    Map<String, Object> reservaInfo = new HashMap<>();

                    // Obtener información básica de la reserva
                    String idReserva = getPropAsString(reservaObj, "getId", "id");
                    String tipoAsiento = getPropAsString(reservaObj, "getTipoAsiento", "getTipo", "tipoAsiento");
                    String cantidadPasajes = getPropAsString(reservaObj, "getCantidadPasajes", "cantidadPasajes");
                    String equipaje = getPropAsString(reservaObj, "getUnidadesEquipajeExtra", "getEquipajeExtra", "equipajeExtra");
                    String costo = getPropAsString(reservaObj, "getCosto", "costo");
                    String fecha = getPropAsString(reservaObj, "getFecha", "fecha");

                    // Obtener información del cliente - CORREGIDO
                    String clienteNickname = getPropAsString(reservaObj, "getCliente", "cliente");
                    String clienteNombre = "Cliente no disponible";

                    if (clienteNickname != null && !clienteNickname.isEmpty() && !clienteNickname.equals("N/A")) {
                        try {
                            // Intentar obtener el DtCliente para obtener nombre y apellido
                            DtCliente cliente = sistema.obtenerCliente(clienteNickname);
                            if (cliente != null) {
                                clienteNombre = cliente.getNombre() + " " + cliente.getApellido();
                            } else {
                                // Si no se puede obtener el cliente, usar el nickname
                                clienteNombre = clienteNickname;
                            }
                        } catch (Exception e) {
                            // Si hay error, usar el nickname
                            clienteNombre = clienteNickname;
                        }
                    }

                    reservaInfo.put("id", idReserva != null ? idReserva : "N/A");
                    reservaInfo.put("cliente", clienteNombre); // Nombre completo del cliente
                    reservaInfo.put("clienteNickname", clienteNickname != null ? clienteNickname : "N/A");
                    reservaInfo.put("tipoAsiento", tipoAsiento != null ? tipoAsiento : "N/A");
                    reservaInfo.put("cantidadPasajes", cantidadPasajes != null ? cantidadPasajes : "0");
                    reservaInfo.put("equipajeExtra", equipaje != null ? equipaje : "0");
                    reservaInfo.put("costo", costo != null ? costo : "0"); // Usar "costo" en lugar de "costoTotal"
                    reservaInfo.put("fechaReserva", fecha != null ? fecha : "N/A");

                    reservasCompletas.add(reservaInfo);
                }

                // Convertir a JSON
                out.print("[");
                for (int i = 0; i < reservasCompletas.size(); i++) {
                    Map<String, Object> reserva = reservasCompletas.get(i);
                    if (i > 0) out.print(",");
                    out.print("{");
                    out.print("\"id\":\"" + escapeJson(String.valueOf(reserva.get("id"))) + "\",");
                    out.print("\"cliente\":\"" + escapeJson(String.valueOf(reserva.get("cliente"))) + "\",");
                    out.print("\"clienteNickname\":\"" + escapeJson(String.valueOf(reserva.get("clienteNickname"))) + "\",");
                    out.print("\"tipoAsiento\":\"" + escapeJson(String.valueOf(reserva.get("tipoAsiento"))) + "\",");
                    out.print("\"cantidadPasajes\":" + reserva.get("cantidadPasajes") + ",");
                    out.print("\"equipajeExtra\":" + reserva.get("equipajeExtra") + ",");
                    out.print("\"costo\":" + reserva.get("costo") + ","); // Mantener como "costo"
                    out.print("\"fechaReserva\":\"" + escapeJson(String.valueOf(reserva.get("fechaReserva"))) + "\"");
                    out.print("}");
                }
                out.print("]");
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            out.print("[]");
        }
    }

    private void obtenerReservaClienteEnVuelo(ISistema sistema, String usuario, String nombreVuelo, PrintWriter out) {
        try {
            DtCliente cliente = sistema.obtenerCliente(usuario);
            if (cliente == null) { out.print("{\"error\":\"Reserva no encontrada\"}"); return; }

            List<DtReserva> reservasCliente = cliente.getReservas();
            if (reservasCliente == null) reservasCliente = Collections.emptyList();

            Vuelo vuelo = sistema.obtenerVuelo(nombreVuelo);
            if (vuelo == null) { out.print("{\"error\":\"Reserva no encontrada\"}"); return; }

            Object reservasObj = vuelo.getReservas();
            Collection<?> reservasVueloColl = reservasObj instanceof Map ? ((Map<?, ?>) reservasObj).values() : (reservasObj instanceof Collection ? (Collection<?>) reservasObj : Collections.emptyList());

            // comparar por id y otros campos
            for (DtReserva reservaCliente : reservasCliente) {
                for (Object rv : reservasVueloColl) {
                    String idRv = getPropAsString(rv, "getId", "id");
                    String idCliente = String.valueOf(reservaCliente.getId());
                    if (idRv != null && idRv.equals(idCliente)) {
                        escribirReservaDetalleJSON(reservaCliente, out);
                        return;
                    }
                }
            }

            out.print("{\"error\":\"Reserva no encontrada\"}");
        } catch (Exception e) {
            out.print("{\"error\":\"Error buscando reserva\"}");
        }
    }

    // serializar lista de DtReserva
    private void escribirReservasJSON(List<DtReserva> reservas, PrintWriter out) {
        out.print("[");
        for (int i = 0; i < reservas.size(); i++) {
            DtReserva r = reservas.get(i);
            if (i > 0) out.print(",");
            out.print("{");
            out.print("\"id\":\"" + escapeJson(String.valueOf(r.getId())) + "\",");
            out.print("\"tipoAsiento\":\"" + escapeJson(String.valueOf(r.getTipoAsiento())) + "\",");
            out.print("\"cantidadPasajes\":" + r.getCantidadPasajes() + ",");
            out.print("\"equipajeExtra\":" + r.getUnidadesEquipajeExtra() + ",");
            out.print("\"costoTotal\":" + r.getCosto() + ",");
            out.print("\"fechaReserva\":\"" + escapeJson(String.valueOf(r.getFecha())) + "\"");
            out.print("}");
        }
        out.print("]");
    }

    // serializar colección genérica de reservas (puede contener DtReserva o Reserva u otros)
    private void escribirReservasVueloGeneric(Collection<?> reservas, ISistema sistema, PrintWriter out) {
        out.print("[");
        boolean first = true;
        for (Object obj : reservas) {
            if (!first) out.print(",");
            first = false;

            String id = getPropAsString(obj, "getId", "id");
            String tipoAsiento = getPropAsString(obj, "getTipoAsiento", "getTipo", "tipoAsiento");
            String cantidadPasajes = getPropAsString(obj, "getCantidadPasajes", "cantidadPasajes");
            String equipaje = getPropAsString(obj, "getUnidadesEquipajeExtra", "getEquipajeExtra", "equipajeExtra");
            String costo = getPropAsString(obj, "getCosto", "costo");
            String fecha = getPropAsString(obj, "getFecha", "fecha");

            out.print("{");
            out.print("\"id\":\"" + escapeJson(String.valueOf(id)) + "\",");
            out.print("\"tipoAsiento\":\"" + escapeJson(String.valueOf(tipoAsiento)) + "\",");
            // cantidadPasajes and similar try to print as number if possible
            if (isInteger(cantidadPasajes)) out.print("\"cantidadPasajes\":" + cantidadPasajes + ","); else out.print("\"cantidadPasajes\":0,");
            if (isInteger(equipaje)) out.print("\"equipajeExtra\":" + equipaje + ","); else out.print("\"equipajeExtra\":0,");
            if (isDouble(costo)) out.print("\"costoTotal\":" + costo + ","); else out.print("\"costoTotal\":0,");
            out.print("\"fechaReserva\":\"" + escapeJson(String.valueOf(fecha)) + "\"");
            out.print("}");
        }
        out.print("]");
    }

    private void escribirReservaDetalleJSON(DtReserva reserva, PrintWriter out) {
        out.print("{");
        out.print("\"id\":\"" + escapeJson(String.valueOf(reserva.getId())) + "\",");
        out.print("\"tipoAsiento\":\"" + escapeJson(String.valueOf(reserva.getTipoAsiento())) + "\",");
        out.print("\"cantidadPasajes\":" + reserva.getCantidadPasajes() + ",");
        out.print("\"equipajeExtra\":" + reserva.getUnidadesEquipajeExtra() + ",");
        out.print("\"costoTotal\":" + reserva.getCosto() + ",");
        out.print("\"fechaReserva\":\"" + escapeJson(String.valueOf(reserva.getFecha())) + "\"");
        out.print("}");
    }

    // helpers reflexivos
    private String getPropAsString(Object obj, String... candidateMethods) {
        if (obj == null) return null;
        for (String mName : candidateMethods) {
            try {
                Method m = obj.getClass().getMethod(mName);
                Object val = m.invoke(obj);
                if (val != null) return String.valueOf(val);
            } catch (NoSuchMethodException nsme) {
                // ignore
            } catch (Exception ignore) {}
        }
        // try fields
        for (String fName : candidateMethods) {
            try {
                java.lang.reflect.Field f = obj.getClass().getDeclaredField(fName);
                f.setAccessible(true);
                Object val = f.get(obj);
                if (val != null) return String.valueOf(val);
            } catch (Exception ignore) {}
        }
        return null;
    }

    private boolean isInteger(String s) {
        if (s == null) return false;
        try { Integer.parseInt(s); return true; } catch (Exception e) { return false; }
    }
    private boolean isDouble(String s) {
        if (s == null) return false;
        try { Double.parseDouble(s); return true; } catch (Exception e) { return false; }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;

@WebServlet("/api/consulta-reserva")
public class ConsultaReservaServlet extends HttpServlet {

    private JuanViajesWS getPort(HttpServletRequest request) {
        try {
            Object o = request.getServletContext().getAttribute("port");
            if (o instanceof JuanViajesWS) return (JuanViajesWS) o;
        } catch (Exception ignored) {}
        WebServicesService svc = new WebServicesService();
        return svc.getJuanViajesWSPort();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String usuario = request.getParameter("usuario");
        String vuelo = request.getParameter("vuelo");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            JuanViajesWS port = getPort(request);
            try { port.cargarDesdeBd(); } catch (Exception ignored) {}

            if ("reservas-cliente".equals(action) && usuario != null) {
                obtenerReservasCliente(usuario, out, request);
            } else if ("reservas-vuelo".equals(action) && vuelo != null) {
                obtenerReservasVuelo(vuelo, out, request);
            } else if ("reserva-cliente-vuelo".equals(action) && usuario != null && vuelo != null) {
                obtenerReservaClienteEnVuelo(usuario, vuelo, out, request);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Parámetros inválidos\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno: " + escapeJson(String.valueOf(e.getMessage())) + "\"}");
        }
    }

    private void obtenerReservasCliente(String usuario, PrintWriter out, HttpServletRequest request) {
        try {
            JuanViajesWS port = getPort(request);
            List<?> reservas;
            try { reservas = port.getReservasCliente(usuario); } catch (Exception ex) { reservas = Collections.emptyList(); }
            if (reservas == null) reservas = Collections.emptyList();
            escribirReservasGeneric(reservas, out);
        } catch (Exception e) {
            out.print("[]");
        }
    }

    private void obtenerReservasVuelo(String nombreVuelo, PrintWriter out, HttpServletRequest request) {
        try {
            JuanViajesWS port = getPort(request);
            Object vuelo;
            try { vuelo = port.verInfoVueloDt(nombreVuelo); } catch (Exception ex) { vuelo = null; }
            if (vuelo != null) {
                Object reservasObj = getPropAsObject(vuelo, "getReservas", "reservas");
                Collection<?> reservasColl = null;
                if (reservasObj instanceof Collection) {
                    reservasColl = (Collection<?>) reservasObj;
                } else if (reservasObj instanceof Map) {
                    reservasColl = ((Map<?, ?>) reservasObj).values();
                }
                if (reservasColl == null) reservasColl = Collections.emptyList();

                escribirReservasGeneric(reservasColl, out);
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            out.print("[]");
        }
    }

    private void obtenerReservaClienteEnVuelo(String usuario, String nombreVuelo, PrintWriter out, HttpServletRequest request) {
        try {
            JuanViajesWS port = getPort(request);
            List<?> reservasCliente;
            try { reservasCliente = port.getReservasCliente(usuario); } catch (Exception ex) { reservasCliente = Collections.emptyList(); }

            Object vuelo;
            try { vuelo = port.verInfoVueloDt(nombreVuelo); } catch (Exception ex) { vuelo = null; }
            if (vuelo == null) { out.print("{\"error\":\"Reserva no encontrada\"}"); return; }

            Object reservasObj = getPropAsObject(vuelo, "getReservas", "reservas");
            Collection<?> reservasVueloColl = reservasObj instanceof Map ? ((Map<?, ?>) reservasObj).values() : (reservasObj instanceof Collection ? (Collection<?>) reservasObj : Collections.emptyList());

            for (Object reservaCliente : reservasCliente) {
                String idCliente = getPropAsString(reservaCliente, "getId", "id");
                for (Object rv : reservasVueloColl) {
                    String idRv = getPropAsString(rv, "getId", "id");
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

    // serializar lista/colección de reservas (genérico)
    private void escribirReservasGeneric(Collection<?> reservas, PrintWriter out) {
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
            if (isInteger(cantidadPasajes)) out.print("\"cantidadPasajes\":" + cantidadPasajes + ","); else out.print("\"cantidadPasajes\":0,");
            if (isInteger(equipaje)) out.print("\"equipajeExtra\":" + equipaje + ","); else out.print("\"equipajeExtra\":0,");
            if (isDouble(costo)) out.print("\"costoTotal\":" + costo + ","); else out.print("\"costoTotal\":0,");
            out.print("\"fechaReserva\":\"" + escapeJson(String.valueOf(fecha)) + "\"");
            out.print("}");
        }
        out.print("]");
    }

    private void escribirReservaDetalleJSON(Object reserva, PrintWriter out) {
        out.print("{");
        String id = getPropAsString(reserva, "getId", "id");
        String tipoAsiento = getPropAsString(reserva, "getTipoAsiento", "getTipo", "tipoAsiento");
        String cantidadPasajes = getPropAsString(reserva, "getCantidadPasajes", "cantidadPasajes");
        String equipaje = getPropAsString(reserva, "getUnidadesEquipajeExtra", "getEquipajeExtra", "equipajeExtra");
        String costo = getPropAsString(reserva, "getCosto", "costo");
        String fecha = getPropAsString(reserva, "getFecha", "fecha");

        out.print("\"id\":\"" + escapeJson(String.valueOf(id)) + "\",");
        out.print("\"tipoAsiento\":\"" + escapeJson(String.valueOf(tipoAsiento)) + "\",");
        if (isInteger(cantidadPasajes)) out.print("\"cantidadPasajes\":" + cantidadPasajes + ","); else out.print("\"cantidadPasajes\":0,");
        if (isInteger(equipaje)) out.print("\"equipajeExtra\":" + equipaje + ","); else out.print("\"equipajeExtra\":0,");
        if (isDouble(costo)) out.print("\"costoTotal\":" + costo + ","); else out.print("\"costoTotal\":0,");
        out.print("\"fechaReserva\":\"" + escapeJson(String.valueOf(fecha)) + "\"");
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

    private Object getPropAsObject(Object obj, String... candidateMethods) {
        if (obj == null) return null;
        for (String mName : candidateMethods) {
            try {
                Method m = obj.getClass().getMethod(mName);
                Object val = m.invoke(obj);
                if (val != null) return val;
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
                if (val != null) return val;
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

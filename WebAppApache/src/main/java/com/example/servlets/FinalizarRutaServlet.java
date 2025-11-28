//FINALIZARRUTAS

package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

import com.example.util.PortUtils;
import serviciosweb.JuanViajesWS;

@WebServlet("/api/finalizar-ruta")
public class FinalizarRutaServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreRuta = request.getParameter("nombreRuta");
        String idRuta = request.getParameter("id"); // opcional

        System.out.println("FinalizarRutaServlet: petición recibida. nombreRuta=" + nombreRuta + ", id=" + idRuta);

        if (nombreRuta == null || nombreRuta.trim().isEmpty()) {
            sendErrorResponse(response, "Parámetro 'nombreRuta' requerido", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType("application/json;charset=UTF-8");

        JuanViajesWS port = null;
        try {
            port = PortUtils.getPort(request);
        } catch (Exception e) {
            System.err.println("FinalizarRutaServlet: no se pudo obtener port SOAP: " + e.getMessage());
            sendErrorResponse(response, "No se pudo conectar con el servicio remoto", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        // En tu FinalizarRutaServlet, después de obtener el port, prueba:
        try {
            int codigo = port.puedeFinalizarRuta("JFK-LAX");
            System.out.println("Código de validación: " + codigo);

            if (codigo == 4) {
                port.finalizarRutaVuelo("JFK-LAX");
                System.out.println("Ruta finalizada exitosamente");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Intentar validar/finalizar por nombre; si falla, y se proporcionó 'id', intentar con id
            int codigo = -1;
            String puedeErrDetail = null; // capturar detalle de excepción si ocurre
            try {
                codigo = port.puedeFinalizarRuta(nombreRuta);
                System.out.println("FinalizarRutaServlet: puedeFinalizarRuta(nombre) devolvió: " + codigo);
            } catch (Exception e) {
                System.err.println("FinalizarRutaServlet: error al invocar puedeFinalizarRuta(nombre): " + e.getMessage());
                // capturar stacktrace completo para debug
                java.io.StringWriter sw = new java.io.StringWriter();
                e.printStackTrace(new java.io.PrintWriter(sw));
                puedeErrDetail = sw.toString();
                System.err.println(puedeErrDetail);
            }

            if (codigo <= 0 && idRuta != null && !idRuta.trim().isEmpty() && !idRuta.equals(nombreRuta)) {
                System.out.println("FinalizarRutaServlet: reintentando puedeFinalizarRuta con id=" + idRuta);
                try {
                    codigo = port.puedeFinalizarRuta(idRuta);
                    System.out.println("FinalizarRutaServlet: puedeFinalizarRuta(id) devolvió: " + codigo);
                } catch (Exception e) {
                    System.err.println("FinalizarRutaServlet: error al invocar puedeFinalizarRuta(id): " + e.getMessage());
                    java.io.StringWriter sw2 = new java.io.StringWriter();
                    e.printStackTrace(new java.io.PrintWriter(sw2));
                    puedeErrDetail = (puedeErrDetail == null ? "" : (puedeErrDetail + "\n---\n")) + sw2.toString();
                    System.err.println(sw2.toString());
                }
            }

            // Interpretación de códigos: asumir 1 = puede, 0 = no puede, otros = error (dependiente del servicio)
            // Interpretación de códigos CORREGIDA:
            if (codigo != 4) { // Solo 4 significa "puede finalizar"
                String msg = "La ruta no puede finalizarse. Código: " + codigo + " - ";
                switch(codigo) {
                    case 0: msg += "Ruta no encontrada"; break;
                    case 1: msg += "La ruta no está en estado CONFIRMADA"; break;
                    case 2: msg += "Tiene vuelos pendientes (fecha futura)"; break;
                    case 3: msg += "Está incluida en paquetes activos"; break;
                    case -1: msg += "Error interno del sistema"; break;
                    default: msg += "Razón desconocida";
                }

                String debugParam = request.getParameter("debug");
                if (debugParam != null && (debugParam.equals("1") || debugParam.equalsIgnoreCase("true"))) {
                    sendErrorResponseWithDetail(response, msg, (puedeErrDetail != null ? puedeErrDetail : "(sin detalle remota)"), HttpServletResponse.SC_CONFLICT);
                } else {
                    sendErrorResponse(response, msg, HttpServletResponse.SC_CONFLICT);
                }
                return;
            }

            // Invocar la operación de finalización: preferir nombreRuta, si fallara intentar con idRuta
            boolean finalized = false;
            try {
                port.finalizarRutaVuelo(nombreRuta);
                finalized = true;
            } catch (jakarta.xml.ws.WebServiceException wseInner) {
                System.err.println("FinalizarRutaServlet: fallo al finalizar por nombre, intentar con id si está disponible: " + wseInner.getMessage());
                if (idRuta != null && !idRuta.trim().isEmpty() && !idRuta.equals(nombreRuta)) {
                    try {
                        port.finalizarRutaVuelo(idRuta);
                        finalized = true;
                    } catch (Exception e2) {
                        System.err.println("FinalizarRutaServlet: fallo al finalizar por id: " + e2.getMessage());
                        throw wseInner; // rethrow original para que el manejo superior capture el SOAP fault
                    }
                } else {
                    throw wseInner;
                }
            }

            if (finalized) {
                try (PrintWriter out = response.getWriter()) {
                    out.print("{\"success\":true,\"message\":\"Ruta finalizada correctamente\"}");
                }
                return;
            }
            // Si no finalizó, lanzar excepción para entrar al catch y devolver error
            throw new Exception("No se pudo finalizar la ruta por razones desconocidas");

        } catch (jakarta.xml.ws.WebServiceException wse) {
            // SOAP fault or transport error
            System.err.println("FinalizarRutaServlet: WebServiceException al finalizar ruta: " + wse.getMessage());
            // Log detailed cause chain to help debugging (e.g. SoapFault -> SQL errors)
            wse.printStackTrace();
            Throwable cause = wse.getCause();
            StringBuilder detailSb = new StringBuilder();
            if (wse.getMessage() != null) {
                detailSb.append(wse.getMessage());
            }
            if (cause != null) {
                detailSb.append("; causa interna: ").append(cause.getClass().getName()).append(" -> ").append(cause.getMessage());
                cause.printStackTrace();
                // Si hay causas anidadas, añadirlas también
                Throwable inner = cause.getCause();
                int depth = 0;
                while (inner != null && depth < 10) {
                    detailSb.append("; causa nivel ").append(depth+1).append(": ").append(inner.getClass().getName()).append(" -> ").append(inner.getMessage());
                    inner.printStackTrace();
                    inner = inner.getCause();
                    depth++;
                }
            }
            String detail = detailSb.toString();

            // Si el mensaje del SOAP indica que la ruta no puede ser finalizada por reglas de negocio,
            // devolver 409 Conflict para que el cliente lo maneje como un error de negocio.
            String msg = safeMessage(wse.getMessage());
            boolean isBusiness = msg.toLowerCase().contains("no puede") || msg.toLowerCase().contains("no se puede") || msg.toLowerCase().contains("no puede ser finaliz");
            int statusCode = isBusiness ? HttpServletResponse.SC_CONFLICT : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

            // Si llega el parámetro debug=1 en la request, incluimos 'detail' en el JSON (útil para desarrollo)
            String debugParam = request.getParameter("debug");
            if (debugParam != null && (debugParam.equals("1") || debugParam.equalsIgnoreCase("true"))) {
                sendErrorResponseWithDetail(response, "Error remoto al finalizar ruta: " + msg, detail, statusCode);
            } else {
                sendErrorResponse(response, "Error remoto al finalizar ruta: " + msg, statusCode);
            }
             return;
        } catch (Exception ex) {
            System.err.println("FinalizarRutaServlet: error inesperado al finalizar ruta: " + ex.getMessage());
            ex.printStackTrace();
            sendErrorResponse(response, "Error interno al finalizar ruta: " + safeMessage(ex.getMessage()), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"success\":false,\"error\":\"" + escapeJson(message) + "\"}");
        }
    }

    // Dev-only helper to include internal detail in JSON when debugging
    private void sendErrorResponseWithDetail(HttpServletResponse response, String message, String detail, int statusCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"success\":false,\"error\":\"" + escapeJson(message) + "\",\"detail\":\"" + escapeJson(detail) + "\"}");
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String safeMessage(String s) {
        if (s == null) return "(sin detalle)";
        String t = s.replaceAll("[\n\r]"," ");
        if (t.length() > 300) return t.substring(0,300) + "...";
        return t;
    }
}
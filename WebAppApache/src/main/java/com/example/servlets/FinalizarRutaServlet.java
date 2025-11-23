//FINALIZARRUTAS

package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/api/finalizar-ruta")
public class FinalizarRutaServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreRuta = request.getParameter("nombreRuta");

        // Validar parámetro requerido
        if (nombreRuta == null || nombreRuta.trim().isEmpty()) {
            sendErrorResponse(response, "Parámetro 'nombreRuta' requerido", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ISistema sistema = Fabrica.getInstance().getISistema();
        sistema.cargarDesdeBd();

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            System.out.println("[DEBUG FinalizarRutaServlet] Intentando finalizar ruta: " + nombreRuta);

            // Verificar si la ruta puede ser finalizada
            boolean puedeFinalizar = sistema.puedeFinalizarRuta(nombreRuta);

            if (!puedeFinalizar) {
                sendErrorResponse(response, "La ruta no puede ser finalizada en este momento", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Finalizar la ruta
            sistema.finalizarRutaVuelo(nombreRuta);

            System.out.println("[DEBUG FinalizarRutaServlet] Ruta finalizada exitosamente: " + nombreRuta);

            // Enviar respuesta de éxito
            out.print("{\"success\":true,\"message\":\"Ruta finalizada exitosamente\"}");

        } catch (IllegalArgumentException e) {
            System.err.println("[ERROR FinalizarRutaServlet] Error de validación: " + e.getMessage());
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("[ERROR FinalizarRutaServlet] Error interno: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, "Error interno del servidor: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print("{\"success\":false,\"error\":\"" + escapeJson(message) + "\"}");
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r").replace("\t","\\t");
    }
}
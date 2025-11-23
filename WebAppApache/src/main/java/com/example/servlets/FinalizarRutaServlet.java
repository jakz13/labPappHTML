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
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreRuta = request.getParameter("nombreRuta");

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

            // \[1] Validar estado de la ruta (no confirmada)
            if (sistema.puedeFinalizarRuta(nombreRuta) == 1) {
                sendErrorResponse(response,
                        "La ruta no puede ser finalizada porque aún no está confirmada",
                        HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // \[2] Validar si tiene vuelos pendientes
            if (sistema.puedeFinalizarRuta(nombreRuta) == 2) {
                sendErrorResponse(response,
                        "La ruta no puede ser finalizada porque tiene vuelos pendientes",
                        HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // \[3] Validar si está en algún paquete
            if (sistema.puedeFinalizarRuta(nombreRuta) == 3) {
                sendErrorResponse(response,
                        "La ruta no puede ser finalizada porque está asociada a un paquete",
                        HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // \[4] Validación genérica extra por si mantienes `puedeFinalizarRuta`
            if (sistema.puedeFinalizarRuta(nombreRuta) == 0) {
                sendErrorResponse(response,
                        "La ruta no puede ser finalizada en este momento",
                        HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Finalizar la ruta
            sistema.finalizarRutaVuelo(nombreRuta);
            System.out.println("[DEBUG FinalizarRutaServlet] Ruta finalizada exitosamente: " + nombreRuta);

            out.print("{\"success\":true,\"message\":\"Ruta finalizada exitosamente\"}");

        } catch (IllegalArgumentException e) {
            System.err.println("[ERROR FinalizarRutaServlet] Error de validación: " + e.getMessage());
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("[ERROR FinalizarRutaServlet] Error interno: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response,
                    "Error interno del servidor: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
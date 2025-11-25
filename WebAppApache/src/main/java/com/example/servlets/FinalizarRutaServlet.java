//FINALIZARRUTAS

package com.example.servlets;

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

        // La lógica para finalizar rutas no está expuesta por el servicio web remoto.
        // Informamos al cliente que la operación no está implementada en la capa remota.
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        PrintWriter out = response.getWriter();
        out.print("{\"success\":false,\"error\":\"Operación no soportada: finalizar ruta debe ejecutarse en el servidor de negocio\"}");
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
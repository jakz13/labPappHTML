// src/main/java/com/example/servlets/BusquedaServlet.java
package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtRutaVuelo;
import DataTypes.DtPaquete;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.*;

@WebServlet("/busqueda")
public class BusquedaServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String query = request.getParameter("q");
        String orden = request.getParameter("orden");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            if ("suggestions".equals(action)) {
                // 🔥 SUGERENCIAS EN TIEMPO REAL
                handleSuggestions(sistema, query, out);
            } else {
                // 🔍 BÚSQUEDA COMPLETA
                handleFullSearch(sistema, query, orden, out);
            }

        } catch (Exception e) {
            System.err.println("[BÚSQUEDA] ❌ Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error en la búsqueda: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void handleSuggestions(ISistema sistema, String query, PrintWriter out) {
        if (query == null || query.trim().length() < 2) {
            out.print("[]");
            return;
        }

        List<Map<String, String>> suggestions = new ArrayList<>();

        // Buscar rutas
        List<DtRutaVuelo> rutas = sistema.buscarRutas(query, "relevancia");
        for (DtRutaVuelo ruta : rutas) {
            if (suggestions.size() >= 5) break;

            Map<String, String> suggestion = new HashMap<>();
            suggestion.put("tipo", "ruta");
            suggestion.put("nombre", ruta.getNombre());
            suggestion.put("descripcion", ruta.getDescripcionCorta());
            suggestions.add(suggestion);
        }

        // Buscar paquetes
        List<DtPaquete> paquetes = sistema.buscarPaquetes(query, "relevancia");
        for (DtPaquete paquete : paquetes) {
            if (suggestions.size() >= 5) break;

            Map<String, String> suggestion = new HashMap<>();
            suggestion.put("tipo", "paquete");
            suggestion.put("nombre", paquete.getNombre());
            suggestion.put("descripcion", paquete.getDescripcion());
            suggestions.add(suggestion);
        }

        // Convertir a JSON
        out.print("[");
        for (int i = 0; i < suggestions.size(); i++) {
            Map<String, String> item = suggestions.get(i);
            out.print("{");
            out.print("\"tipo\":\"" + escapeJson(item.get("tipo")) + "\",");
            out.print("\"nombre\":\"" + escapeJson(item.get("nombre")) + "\",");
            out.print("\"descripcion\":\"" + escapeJson(item.get("descripcion")) + "\"");
            out.print("}");
            if (i < suggestions.size() - 1) out.print(",");
        }
        out.print("]");
    }

    private void handleFullSearch(ISistema sistema, String query, String orden, PrintWriter out) {
        // Para búsqueda completa, redirigir a la página de resultados
        // O devolver JSON si es una petición AJAX
        // (Esto se implementará en el JSP de resultados)
        out.print("{\"message\":\"Búsqueda completa - usar página de resultados\"}");
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
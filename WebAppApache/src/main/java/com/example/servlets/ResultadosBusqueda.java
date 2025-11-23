// src/main/java/com/example/servlets/ResultadosBusqueda.java
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

@WebServlet("/ResultadosBusqueda")
public class ResultadosBusqueda extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String query = request.getParameter("q");
        String orden = request.getParameter("orden");
        String tipo = request.getParameter("tipo");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            if ("suggestions".equals(action)) {
                // 🔥 MODO SUGERENCIAS
                handleSuggestions(sistema, query, response);
            } else {
                // 🔍 MODO RESULTADOS COMPLETOS
                handleFullSearch(sistema, query, orden, tipo, request, response);
            }

        } catch (Exception e) {
            System.err.println("[BÚSQUEDA] ❌ Error: " + e.getMessage());
            e.printStackTrace();
            if (!"suggestions".equals(action)) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en la búsqueda: " + e.getMessage());
            } else {
                response.getWriter().print("[]");
            }
        }
    }

    private void handleSuggestions(ISistema sistema, String query, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        if (query == null || query.trim().length() < 2) {
            out.print("[]");
            return;
        }

        List<Map<String, String>> suggestions = new ArrayList<>();

        // Buscar rutas (incluyendo descripción corta)
        List<DtRutaVuelo> rutas = sistema.buscarRutas(query, "relevancia");
        for (DtRutaVuelo ruta : rutas) {
            if (suggestions.size() >= 5) break;

            Map<String, String> suggestion = new HashMap<>();
            suggestion.put("tipo", "ruta");
            suggestion.put("nombre", ruta.getNombre());
            suggestion.put("descripcion", ruta.getDescripcionCorta() != null ? ruta.getDescripcionCorta() : ruta.getDescripcion());
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

    private void handleFullSearch(ISistema sistema, String query, String orden, String tipo,
                                  HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        List<DtRutaVuelo> rutas = new ArrayList<>();
        List<DtPaquete> paquetes = new ArrayList<>();

        // Buscar rutas si corresponde
        if (tipo == null || "todos".equals(tipo) || "rutas".equals(tipo)) {
            rutas = sistema.buscarRutas(query != null ? query : "", orden != null ? orden : "fecha");
        }

        // Buscar paquetes si corresponde
        if (tipo == null || "todos".equals(tipo) || "paquetes".equals(tipo)) {
            paquetes = sistema.buscarPaquetes(query != null ? query : "", orden != null ? orden : "fecha");
        }

        // Pasar resultados al JSP
        request.setAttribute("rutas", rutas);
        request.setAttribute("paquetes", paquetes);
        request.setAttribute("rutasCount", rutas.size());
        request.setAttribute("paquetesCount", paquetes.size());

        // Forward al JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/resultados-busqueda.jsp");
        dispatcher.forward(request, response);
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
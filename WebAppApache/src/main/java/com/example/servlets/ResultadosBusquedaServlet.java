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
public class ResultadosBusquedaServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String query = request.getParameter("q");
        String orden = request.getParameter("orden");

        System.out.println("=== BÚSQUEDA INICIADA ===");
        System.out.println("Action: " + action);
        System.out.println("Query: " + query);
        System.out.println("Orden: " + orden);

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            if ("suggestions".equals(action)) {
                // 🔥 SUGERENCIAS EN TIEMPO REAL - Devuelve JSON
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                System.out.println("Procesando sugerencias...");
                handleSuggestions(sistema, query, out);
            } else {
                // 🔍 BÚSQUEDA COMPLETA - Forward a JSP
                System.out.println("Procesando búsqueda completa...");
                handleFullSearch(request, response, sistema, query, orden);
            }

        } catch (Exception e) {
            System.err.println("[BÚSQUEDA] ❌ Error: " + e.getMessage());
            e.printStackTrace();
            
            // Si es sugerencias, devolver JSON de error
            if ("suggestions".equals(action)) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                PrintWriter out = response.getWriter();
                out.print("{\"error\":\"Error en la búsqueda: " + escapeJson(e.getMessage()) + "\"}");
            } else {
                // Si es búsqueda completa, redirigir a página de error o mostrar mensaje
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                request.setAttribute("error", "Error en la búsqueda: " + e.getMessage());
                RequestDispatcher dispatcher = request.getRequestDispatcher("/resultados-busqueda.jsp");
                dispatcher.forward(request, response);
            }
        }
    }

    private void handleSuggestions(ISistema sistema, String query, PrintWriter out) {
        System.out.println("🔄 [SUGERENCIAS] Iniciando búsqueda para: '" + query + "'");

        if (query == null || query.trim().length() < 2) {
            System.out.println("❌ [SUGERENCIAS] Query demasiado corta o nula");
            out.print("[]");
            return;
        }

        List<Map<String, String>> suggestions = new ArrayList<>();
        String queryLower = query.toLowerCase().trim();

        try {
            // Buscar rutas - Intentar usar buscarRutas, si no existe, filtrar manualmente
            System.out.println("🔍 [SUGERENCIAS] Buscando rutas...");
            List<DtRutaVuelo> rutas = null;
            
            try {
                // Intentar usar el método buscarRutas si existe
                java.lang.reflect.Method buscarRutasMethod = sistema.getClass().getMethod("buscarRutas", String.class, String.class);
                rutas = (List<DtRutaVuelo>) buscarRutasMethod.invoke(sistema, query, "relevancia");
            } catch (NoSuchMethodException e) {
                // Si no existe, obtener todas las rutas y filtrar manualmente
                System.out.println("⚠️ [SUGERENCIAS] Método buscarRutas no existe, filtrando manualmente...");
                List<DtRutaVuelo> todasRutas = sistema.listarRutasConfirmadas(100);
                if (todasRutas != null) {
                    rutas = new ArrayList<>();
                    for (DtRutaVuelo ruta : todasRutas) {
                        String nombre = ruta.getNombre() != null ? ruta.getNombre().toLowerCase() : "";
                        String descripcion = ruta.getDescripcionCorta() != null ? 
                                ruta.getDescripcionCorta().toLowerCase() : 
                                (ruta.getDescripcion() != null ? ruta.getDescripcion().toLowerCase() : "");
                        String origen = ruta.getCiudadOrigen() != null ? ruta.getCiudadOrigen().toLowerCase() : "";
                        String destino = ruta.getCiudadDestino() != null ? ruta.getCiudadDestino().toLowerCase() : "";
                        
                        if (nombre.contains(queryLower) || descripcion.contains(queryLower) || 
                            origen.contains(queryLower) || destino.contains(queryLower)) {
                            rutas.add(ruta);
                        }
                    }
                }
            }
            
            System.out.println("📊 [SUGERENCIAS] Rutas encontradas: " + (rutas != null ? rutas.size() : "null"));

            if (rutas != null) {
                for (DtRutaVuelo ruta : rutas) {
                    if (suggestions.size() >= 5) break;

                    System.out.println("📍 [SUGERENCIAS] Procesando ruta: " + ruta.getNombre());

                    Map<String, String> suggestion = new HashMap<>();
                    suggestion.put("tipo", "ruta");
                    suggestion.put("nombre", ruta.getNombre());
                    suggestion.put("descripcion", ruta.getDescripcionCorta() != null ?
                            ruta.getDescripcionCorta() : (ruta.getDescripcion() != null ? ruta.getDescripcion() : ""));
                    suggestions.add(suggestion);
                    System.out.println("✅ [SUGERENCIAS] Sugerencia ruta agregada: " + ruta.getNombre());
                }
            }

            // Buscar paquetes - Intentar usar buscarPaquetes, si no existe, filtrar manualmente
            System.out.println("🔍 [SUGERENCIAS] Buscando paquetes...");
            List<DtPaquete> paquetes = null;
            
            try {
                // Intentar usar el método buscarPaquetes si existe
                java.lang.reflect.Method buscarPaquetesMethod = sistema.getClass().getMethod("buscarPaquetes", String.class, String.class);
                paquetes = (List<DtPaquete>) buscarPaquetesMethod.invoke(sistema, query, "relevancia");
            } catch (NoSuchMethodException e) {
                // Si no existe, obtener todos los paquetes y filtrar manualmente
                System.out.println("⚠️ [SUGERENCIAS] Método buscarPaquetes no existe, filtrando manualmente...");
                List<DtPaquete> todosPaquetes = sistema.listarPaquetes();
                if (todosPaquetes != null) {
                    paquetes = new ArrayList<>();
                    for (DtPaquete paquete : todosPaquetes) {
                        String nombre = paquete.getNombre() != null ? paquete.getNombre().toLowerCase() : "";
                        String descripcion = paquete.getDescripcion() != null ? paquete.getDescripcion().toLowerCase() : "";
                        
                        if (nombre.contains(queryLower) || descripcion.contains(queryLower)) {
                            paquetes.add(paquete);
                        }
                    }
                }
            }
            
            System.out.println("📊 [SUGERENCIAS] Paquetes encontrados: " + (paquetes != null ? paquetes.size() : "null"));

            if (paquetes != null) {
                for (DtPaquete paquete : paquetes) {
                    if (suggestions.size() >= 5) break;

                    System.out.println("📦 [SUGERENCIAS] Procesando paquete: " + paquete.getNombre());

                    Map<String, String> suggestion = new HashMap<>();
                    suggestion.put("tipo", "paquete");
                    suggestion.put("nombre", paquete.getNombre());
                    suggestion.put("descripcion", paquete.getDescripcion() != null ? paquete.getDescripcion() : "");
                    suggestions.add(suggestion);
                    System.out.println("✅ [SUGERENCIAS] Sugerencia paquete agregada: " + paquete.getNombre());
                }
            }

        } catch (Exception e) {
            System.err.println("❌ [SUGERENCIAS] Error durante la búsqueda: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("🎯 [SUGERENCIAS] Total sugerencias generadas: " + suggestions.size());

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

        System.out.println("📤 [SUGERENCIAS] Respuesta JSON enviada");
    }

    private void handleFullSearch(HttpServletRequest request, HttpServletResponse response,
                                  ISistema sistema, String query, String orden)
            throws ServletException, IOException {

        System.out.println("Realizando búsqueda completa...");

        List<DtRutaVuelo> rutas = new ArrayList<>();
        List<DtPaquete> paquetes = new ArrayList<>();
        String queryLower = (query != null && !query.trim().isEmpty()) ? query.toLowerCase().trim() : null;

        // Si no hay query, mostrar todos los resultados
        if (queryLower == null || queryLower.isEmpty()) {
            System.out.println("Query vacía, mostrando todos los resultados");
            rutas = sistema.listarRutasConfirmadas(100);
            paquetes = sistema.listarPaquetes();
        } else {
            // Buscar rutas y paquetes con filtro
            try {
                // Intentar usar buscarRutas si existe
                try {
                    java.lang.reflect.Method buscarRutasMethod = sistema.getClass().getMethod("buscarRutas", String.class, String.class);
                    rutas = (List<DtRutaVuelo>) buscarRutasMethod.invoke(sistema, query, orden != null ? orden : "relevancia");
                } catch (NoSuchMethodException e) {
                    // Si no existe, filtrar manualmente
                    System.out.println("⚠️ Método buscarRutas no existe, filtrando manualmente...");
                    List<DtRutaVuelo> todasRutas = sistema.listarRutasConfirmadas(100);
                    if (todasRutas != null) {
                        for (DtRutaVuelo ruta : todasRutas) {
                            String nombre = ruta.getNombre() != null ? ruta.getNombre().toLowerCase() : "";
                            String descripcion = ruta.getDescripcionCorta() != null ? 
                                    ruta.getDescripcionCorta().toLowerCase() : 
                                    (ruta.getDescripcion() != null ? ruta.getDescripcion().toLowerCase() : "");
                            String origen = ruta.getCiudadOrigen() != null ? ruta.getCiudadOrigen().toLowerCase() : "";
                            String destino = ruta.getCiudadDestino() != null ? ruta.getCiudadDestino().toLowerCase() : "";
                            
                            if (nombre.contains(queryLower) || descripcion.contains(queryLower) || 
                                origen.contains(queryLower) || destino.contains(queryLower)) {
                                rutas.add(ruta);
                            }
                        }
                    }
                }

                // Intentar usar buscarPaquetes si existe
                try {
                    java.lang.reflect.Method buscarPaquetesMethod = sistema.getClass().getMethod("buscarPaquetes", String.class, String.class);
                    paquetes = (List<DtPaquete>) buscarPaquetesMethod.invoke(sistema, query, orden != null ? orden : "relevancia");
                } catch (NoSuchMethodException e) {
                    // Si no existe, filtrar manualmente
                    System.out.println("⚠️ Método buscarPaquetes no existe, filtrando manualmente...");
                    List<DtPaquete> todosPaquetes = sistema.listarPaquetes();
                    if (todosPaquetes != null) {
                        for (DtPaquete paquete : todosPaquetes) {
                            String nombre = paquete.getNombre() != null ? paquete.getNombre().toLowerCase() : "";
                            String descripcion = paquete.getDescripcion() != null ? paquete.getDescripcion().toLowerCase() : "";
                            
                            if (nombre.contains(queryLower) || descripcion.contains(queryLower)) {
                                paquetes.add(paquete);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error en búsqueda: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("Búsqueda completa - Rutas: " + (rutas != null ? rutas.size() : 0));
            System.out.println("Búsqueda completa - Paquetes: " + (paquetes != null ? paquetes.size() : 0));
        }

        // Aplicar ordenamiento si es necesario
        if (orden != null && "alfabetico".equals(orden)) {
            if (rutas != null) {
                rutas.sort((r1, r2) -> {
                    String n1 = r1.getNombre() != null ? r1.getNombre() : "";
                    String n2 = r2.getNombre() != null ? r2.getNombre() : "";
                    return n1.compareToIgnoreCase(n2);
                });
            }
            if (paquetes != null) {
                paquetes.sort((p1, p2) -> {
                    String n1 = p1.getNombre() != null ? p1.getNombre() : "";
                    String n2 = p2.getNombre() != null ? p2.getNombre() : "";
                    return n1.compareToIgnoreCase(n2);
                });
            }
        }

        request.setAttribute("rutas", rutas != null ? rutas : new ArrayList<>());
        request.setAttribute("paquetes", paquetes != null ? paquetes : new ArrayList<>());
        request.setAttribute("rutasCount", rutas != null ? rutas.size() : 0);
        request.setAttribute("paquetesCount", paquetes != null ? paquetes.size() : 0);

        // Redirigir a la página de resultados
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
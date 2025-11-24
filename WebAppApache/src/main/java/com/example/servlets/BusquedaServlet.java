package com.example.servlets;

import DataTypes.DtAerolinea;
import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtRutaVuelo;
import DataTypes.DtPaquete;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@WebServlet("/busqueda")
public class BusquedaServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String query = request.getParameter("q");

        System.out.println("🔍 [BUSQUEDA] Action: " + action + ", Query: " + query);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            if ("suggestions".equals(action)) {
                handleSuggestions(sistema, query, out);
            } else {
                // Para búsqueda normal, redirigir a la página de resultados
                String redirectUrl = request.getContextPath() + "/resultados-busqueda.jsp";
                if (query != null && !query.trim().isEmpty()) {
                    redirectUrl += "?q=" + URLEncoder.encode(query.trim(), "UTF-8");
                }
                response.sendRedirect(redirectUrl);
            }

        } catch (Exception e) {
            System.err.println("[BÚSQUEDA] ❌ Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error en la búsqueda\"}");
        }
    }

    private void handleSuggestions(ISistema sistema, String query, PrintWriter out) {
        System.out.println("🎯 [SUGERENCIAS] Buscando: '" + query + "'");

        if (query == null || query.trim().length() < 2) {
            System.out.println("❌ [SUGERENCIAS] Query demasiado corta");
            out.print("[]");
            return;
        }

        List<Map<String, String>> suggestions = new ArrayList<>();

        try {
            // Obtener todas las rutas y paquetes para búsqueda inteligente
            List<DtRutaVuelo> todasLasRutas = obtenerTodasLasRutasConfirmadas(sistema);
            List<DtPaquete> todosLosPaquetes = sistema.listarPaquetes();

            System.out.println("📊 [SUGERENCIAS] Total rutas: " + todasLasRutas.size());
            System.out.println("📊 [SUGERENCIAS] Total paquetes: " + (todosLosPaquetes != null ? todosLosPaquetes.size() : 0));

            // Búsqueda inteligente con algoritmo mejorado
            List<SugerenciaConRelevancia> sugerenciasConRelevancia = new ArrayList<>();

            // Buscar en rutas
            for (DtRutaVuelo ruta : todasLasRutas) {
                int relevancia = calcularRelevanciaRuta(ruta, query);
                if (relevancia > 0) {
                    sugerenciasConRelevancia.add(new SugerenciaConRelevancia(
                            "ruta",
                            ruta.getNombre(),
                            ruta.getDescripcionCorta() != null ? ruta.getDescripcionCorta() : ruta.getDescripcion(),
                            relevancia
                    ));
                }
            }

            // Buscar en paquetes
            if (todosLosPaquetes != null) {
                for (DtPaquete paquete : todosLosPaquetes) {
                    int relevancia = calcularRelevanciaPaquete(paquete, query);
                    if (relevancia > 0) {
                        sugerenciasConRelevancia.add(new SugerenciaConRelevancia(
                                "paquete",
                                paquete.getNombre(),
                                paquete.getDescripcion(),
                                relevancia
                        ));
                    }
                }
            }

            // Ordenar por relevancia (mayor primero)
            sugerenciasConRelevancia.sort((s1, s2) -> Integer.compare(s2.relevancia, s1.relevancia));

            // Tomar las 5 más relevantes
            for (int i = 0; i < Math.min(5, sugerenciasConRelevancia.size()); i++) {
                SugerenciaConRelevancia sugerencia = sugerenciasConRelevancia.get(i);

                Map<String, String> suggestion = new HashMap<>();
                suggestion.put("tipo", sugerencia.tipo);
                suggestion.put("nombre", sugerencia.nombre != null ? sugerencia.nombre : "");
                suggestion.put("descripcion", sugerencia.descripcion != null ? sugerencia.descripcion : "");
                suggestion.put("relevancia", String.valueOf(sugerencia.relevancia));

                suggestions.add(suggestion);

                System.out.println("✅ [SUGERENCIAS] " + sugerencia.tipo + ": " + sugerencia.nombre + " (relevancia: " + sugerencia.relevancia + ")");
            }

        } catch (Exception e) {
            System.err.println("❌ [SUGERENCIAS] Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("🎯 [SUGERENCIAS] Total sugerencias: " + suggestions.size());

        // Convertir a JSON de forma segura
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < suggestions.size(); i++) {
            Map<String, String> item = suggestions.get(i);
            json.append("{");
            json.append("\"tipo\":\"").append(escapeJson(item.get("tipo"))).append("\",");
            json.append("\"nombre\":\"").append(escapeJson(item.get("nombre"))).append("\",");
            json.append("\"descripcion\":\"").append(escapeJson(item.get("descripcion"))).append("\",");
            json.append("\"relevancia\":").append(item.get("relevancia"));
            json.append("}");
            if (i < suggestions.size() - 1) json.append(",");
        }
        json.append("]");

        out.print(json.toString());
        System.out.println("📤 [SUGERENCIAS] JSON enviado");
    }

    // Clase auxiliar para manejar sugerencias con relevancia
    private static class SugerenciaConRelevancia {
        String tipo;
        String nombre;
        String descripcion;
        int relevancia;

        SugerenciaConRelevancia(String tipo, String nombre, String descripcion, int relevancia) {
            this.tipo = tipo;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.relevancia = relevancia;
        }
    }

    // ALGORITMO INTELIGENTE DE BÚSQUEDA - Basado en el repositorio
    private int calcularRelevanciaRuta(DtRutaVuelo ruta, String query) {
        if (query == null || query.trim().isEmpty()) return 0;

        String[] palabras = query.toLowerCase().split("\\s+");
        String textoBusqueda = construirTextoBusquedaRuta(ruta).toLowerCase();

        int relevanciaTotal = 0;

        for (String palabra : palabras) {
            if (palabra.length() < 2) continue; // Ignorar palabras muy cortas

            int relevanciaPalabra = 0;

            // Coincidencia exacta en nombre
            if (ruta.getNombre() != null && ruta.getNombre().toLowerCase().equals(palabra)) {
                relevanciaPalabra += 100;
            }
            // Coincidencia parcial en nombre
            else if (ruta.getNombre() != null && ruta.getNombre().toLowerCase().contains(palabra)) {
                relevanciaPalabra += 50;
                // Bonus si está al inicio
                if (ruta.getNombre().toLowerCase().startsWith(palabra)) {
                    relevanciaPalabra += 20;
                }
            }
            // Coincidencia en descripción corta
            else if (ruta.getDescripcionCorta() != null && ruta.getDescripcionCorta().toLowerCase().contains(palabra)) {
                relevanciaPalabra += 30;
            }
            // Coincidencia en descripción larga
            else if (ruta.getDescripcion() != null && ruta.getDescripcion().toLowerCase().contains(palabra)) {
                relevanciaPalabra += 20;
            }
            // Coincidencia en ciudades
            else if (ruta.getCiudadOrigen() != null && ruta.getCiudadOrigen().toLowerCase().contains(palabra)) {
                relevanciaPalabra += 25;
            }
            else if (ruta.getCiudadDestino() != null && ruta.getCiudadDestino().toLowerCase().contains(palabra)) {
                relevanciaPalabra += 25;
            }
            // Coincidencia en aerolínea
            else if (ruta.getAerolinea() != null && ruta.getAerolinea().toLowerCase().contains(palabra)) {
                relevanciaPalabra += 15;
            }

            relevanciaTotal += relevanciaPalabra;
        }

        return relevanciaTotal;
    }

    private int calcularRelevanciaPaquete(DtPaquete paquete, String query) {
        if (query == null || query.trim().isEmpty()) return 0;

        String[] palabras = query.toLowerCase().split("\\s+");
        String textoBusqueda = construirTextoBusquedaPaquete(paquete).toLowerCase();

        int relevanciaTotal = 0;

        for (String palabra : palabras) {
            if (palabra.length() < 2) continue;

            int relevanciaPalabra = 0;

            // Coincidencia exacta en nombre
            if (paquete.getNombre() != null && paquete.getNombre().toLowerCase().equals(palabra)) {
                relevanciaPalabra += 100;
            }
            // Coincidencia parcial en nombre
            else if (paquete.getNombre() != null && paquete.getNombre().toLowerCase().contains(palabra)) {
                relevanciaPalabra += 60;
                // Bonus si está al inicio
                if (paquete.getNombre().toLowerCase().startsWith(palabra)) {
                    relevanciaPalabra += 25;
                }
            }
            // Coincidencia en descripción
            else if (paquete.getDescripcion() != null && paquete.getDescripcion().toLowerCase().contains(palabra)) {
                relevanciaPalabra += 40;
            }

            relevanciaTotal += relevanciaPalabra;
        }

        return relevanciaTotal;
    }

    private String construirTextoBusquedaRuta(DtRutaVuelo ruta) {
        StringBuilder sb = new StringBuilder();

        if (ruta.getNombre() != null) sb.append(ruta.getNombre()).append(" ");
        if (ruta.getDescripcionCorta() != null) sb.append(ruta.getDescripcionCorta()).append(" ");
        if (ruta.getDescripcion() != null) sb.append(ruta.getDescripcion()).append(" ");
        if (ruta.getCiudadOrigen() != null) sb.append(ruta.getCiudadOrigen()).append(" ");
        if (ruta.getCiudadDestino() != null) sb.append(ruta.getCiudadDestino()).append(" ");
        if (ruta.getAerolinea() != null) sb.append(ruta.getAerolinea()).append(" ");

        // Agregar categorías si existen
        if (ruta.getCategorias() != null) {
            for (String categoria : ruta.getCategorias()) {
                sb.append(categoria).append(" ");
            }
        }

        return sb.toString();
    }

    private String construirTextoBusquedaPaquete(DtPaquete paquete) {
        StringBuilder sb = new StringBuilder();

        if (paquete.getNombre() != null) sb.append(paquete.getNombre()).append(" ");
        if (paquete.getDescripcion() != null) sb.append(paquete.getDescripcion()).append(" ");

        return sb.toString();
    }

    /**
     * Método auxiliar para obtener todas las rutas confirmadas del sistema
     */
    private List<DtRutaVuelo> obtenerTodasLasRutasConfirmadas(ISistema sistema) {
        List<DtRutaVuelo> todasLasRutasConfirmadas = new ArrayList<>();

        try {
            // Obtener todas las aerolíneas
            List<DtAerolinea> aerolineas = sistema.listarAerolineas();
            System.out.println("🏢 [RUTAS] Total aerolíneas: " + (aerolineas != null ? aerolineas.size() : 0));

            if (aerolineas != null) {
                for (DtAerolinea aerolinea : aerolineas) {
                    try {
                        // Obtener rutas de cada aerolínea
                        List<DtRutaVuelo> rutasAerolinea = sistema.listarRutasPorAerolinea(aerolinea.getNickname());

                        if (rutasAerolinea != null) {
                            // Filtrar solo rutas confirmadas
                            for (DtRutaVuelo ruta : rutasAerolinea) {
                                if (ruta.getEstado() != null && "CONFIRMADA".equalsIgnoreCase(ruta.getEstado().toString())) {
                                    todasLasRutasConfirmadas.add(ruta);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("⚠️ [RUTAS] Error obteniendo rutas para aerolínea " + aerolinea.getNickname() + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ [RUTAS] Error obteniendo aerolíneas: " + e.getMessage());
        }

        System.out.println("✅ [RUTAS] Total rutas confirmadas obtenidas: " + todasLasRutasConfirmadas.size());
        return todasLasRutasConfirmadas;
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
package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtRutaVuelo;
import DataTypes.DtAerolinea;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/consultaRuta")
public class ConsultaRutaServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombreRuta = request.getParameter("nombreRuta");
        String forzarRecarga = request.getParameter("recargar"); // ✅ Nuevo parámetro

        if (nombreRuta == null || nombreRuta.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\":\"Nombre de ruta requerido\"}");
            return;
        }

        // ✅ HEADERS ANTI-CACHE
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();

            // ✅ OPCIONAL: Recargar desde BD si se solicita
            if ("true".equals(forzarRecarga)) {
                System.out.println("🔄 Forzando recarga de datos desde BD para ruta: " + nombreRuta);
                sistema.cargarDesdeBd(); // Esto recargará todo desde BD
            } else {
                // Carga normal desde memoria (más rápido)
                sistema.cargarDesdeBd();
            }

            DtRutaVuelo rutaEncontrada = null;
            List<DtAerolinea> aerolineas = sistema.listarAerolineas();

            for (DtAerolinea aerolinea : aerolineas) {
                String nickname = aerolinea.getNickname();
                String nombreAerolinea = aerolinea.getNombre();
                List<DtRutaVuelo> rutasAerolinea = sistema.listarRutasPorAerolinea(nickname);

                for (DtRutaVuelo ruta : rutasAerolinea) {
                    boolean nombresIguales = ruta.getNombre() != null &&
                            nombreRuta != null &&
                            ruta.getNombre().trim().equals(nombreRuta.trim());
                    if (nombresIguales) {
                        rutaEncontrada = ruta;
                        break;
                    }
                }
                if (rutaEncontrada != null) break;
            }

            if (rutaEncontrada == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Ruta no encontrada o no confirmada\"}");
                return;
            }

            // ✅ INCREMENTAR CONTADOR DE VISITAS
            try {
                sistema.incrementarVisitasRuta(nombreRuta);
                System.out.println("✅ Visitas incrementadas para ruta: " + nombreRuta);
            } catch (Exception e) {
                System.err.println("⚠️ Error incrementando visitas: " + e.getMessage());
                // No fallar la consulta por esto
            }

            StringBuilder json = new StringBuilder();
            json.append("{");

            // Campos básicos
            json.append("\"nombre\":\"").append(escapeJson(rutaEncontrada.getNombre())).append("\",");
            json.append("\"descripcion\":\"").append(escapeJson(rutaEncontrada.getDescripcion())).append("\",");
            json.append("\"descripcionCorta\":\"").append(escapeJson(rutaEncontrada.getDescripcionCorta())).append("\",");
            json.append("\"origen\":\"").append(escapeJson(rutaEncontrada.getCiudadOrigen())).append("\",");
            json.append("\"destino\":\"").append(escapeJson(rutaEncontrada.getCiudadDestino())).append("\",");
            json.append("\"aerolinea\":\"").append(escapeJson(rutaEncontrada.getAerolinea())).append("\",");
            json.append("\"hora\":\"").append(escapeJson(rutaEncontrada.getHora())).append("\",");
            json.append("\"estado\":\"").append(escapeJson(String.valueOf(rutaEncontrada.getEstado()))).append("\",");
            json.append("\"costoTurista\":").append(rutaEncontrada.getCostoTurista()).append(",");
            json.append("\"costoEjecutivo\":").append(rutaEncontrada.getCostoEjecutivo()).append(",");
            json.append("\"costoEquipaje\":").append(rutaEncontrada.getCostoEquipajeExtra()).append(",");
            json.append("\"contadorVisitas\":").append(rutaEncontrada.getContadorVisitas()).append(",");

            if (rutaEncontrada.getFechaAlta() != null) {
                String fechaAltaStr = String.valueOf(rutaEncontrada.getFechaAlta());

                // Si ya está en formato yyyy-MM-dd, usar tal cual
                if (fechaAltaStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    json.append("\"fechaAlta\":\"").append(escapeJson(fechaAltaStr)).append("\",");
                } else {
                    // Intentar convertir otros formatos
                    try {
                        SimpleDateFormat sdfInput = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"); // formato común
                        SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date date = sdfInput.parse(fechaAltaStr);
                        String fechaFormateada = sdfOutput.format(date);
                        json.append("\"fechaAlta\":\"").append(escapeJson(fechaFormateada)).append("\",");
                    } catch (Exception e) {
                        // Si falla, usar el valor original
                        json.append("\"fechaAlta\":\"").append(escapeJson(fechaAltaStr)).append("\",");
                    }
                }
            } else {
                json.append("\"fechaAlta\":\"\",");
            }

            // Categorías
            json.append("\"categorias\":[");
            if (rutaEncontrada.getCategorias() != null && !rutaEncontrada.getCategorias().isEmpty()) {
                for (int i = 0; i < rutaEncontrada.getCategorias().size(); i++) {
                    if (i > 0) json.append(",");
                    json.append("\"").append(escapeJson(rutaEncontrada.getCategorias().get(i))).append("\"");
                }
            }
            json.append("]");

            // Agregar imagen si existe
            try {
                String imagenVal = rutaEncontrada.getImagenUrl();
                if (imagenVal != null && !imagenVal.isBlank()) {
                    json.append(",\"imagenUrl\":\"").append(escapeJson(imagenVal)).append("\"");
                }
            } catch (Exception e) {
                // Ignorar si no hay imagen
            }

            // Agregar video si existe
            try {
                String videoVal = rutaEncontrada.getVideoUrl();
                if (videoVal != null && !videoVal.isBlank()) {
                    json.append(",\"videoUrl\":\"").append(escapeJson(videoVal)).append("\"");
                }
            } catch (Exception e) {
                // Ignorar si no hay video
            }

            json.append("}");
            out.print(json.toString());

            System.out.println("✅ Ruta consultada: " + nombreRuta + " (recargar=" + forzarRecarga + ")");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error al consultar ruta: " + escapeJson(e.getMessage()) + "\"}");
        }
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
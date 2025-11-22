// src/main/java/com/example/servlets/ConsultaRutaServlet.java
package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtRutaVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.List;

@WebServlet("/consultaRuta")
public class ConsultaRutaServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombreRuta = request.getParameter("nombreRuta");

        if (nombreRuta == null || nombreRuta.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\":\"Nombre de ruta requerido\"}");
            return;
        }

        ContadorVisitasRutas.incrementarVisitaRuta(nombreRuta);
        System.out.println("[CONTADOR] Visita registrada para ruta: " + nombreRuta);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            // === CONTAR LA VISITA A ESTA RUTA ESPECÍFICA ===
            ContadorVisitasRutas.incrementarVisitaRuta(nombreRuta);

            // Buscar la ruta en todas las aerolíneas
            DtRutaVuelo rutaEncontrada = null;
            List<DtRutaVuelo> todasRutas = sistema.listarRutasConfirmadas(1000); // Número grande para obtener todas

            for (DtRutaVuelo ruta : todasRutas) {
                if (ruta.getNombre().equals(nombreRuta)) {
                    rutaEncontrada = ruta;
                    break;
                }
            }

            if (rutaEncontrada == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Ruta no encontrada\"}");
                return;
            }

            // Generar JSON con la información de la ruta
            out.print("{");
            out.print("\"nombre\":\"" + escapeJson(rutaEncontrada.getNombre()) + "\",");
            out.print("\"descripcion\":\"" + escapeJson(rutaEncontrada.getDescripcion()) + "\",");
            out.print("\"origen\":\"" + escapeJson(rutaEncontrada.getCiudadOrigen()) + "\",");
            out.print("\"destino\":\"" + escapeJson(rutaEncontrada.getCiudadDestino()) + "\",");
            out.print("\"estado\":\"" + escapeJson(rutaEncontrada.getEstado()) + "\",");
            out.print("\"costoTurista\":" + rutaEncontrada.getCostoTurista() + ",");
            out.print("\"costoEjecutivo\":" + rutaEncontrada.getCostoEjecutivo() + ",");
            out.print("\"costoEquipaje\":" + rutaEncontrada.getCostoEquipajeExtra());

            // Agregar imagen si existe
            try {
                String imagenVal = rutaEncontrada.getImagenUrl();
                if (imagenVal != null && !imagenVal.isBlank()) {
                    out.print(",\"imagenUrl\":\"" + escapeJson(imagenVal) + "\"");
                }
            } catch (Exception e) {
                // Ignorar si no hay imagen
            }

            // Agregar video si existe
            try {
                String videoVal = rutaEncontrada.getVideoUrl();
                if (videoVal != null && !videoVal.isBlank()) {
                    out.print(",\"videoUrl\":\"" + escapeJson(videoVal) + "\"");
                }
            } catch (Exception e) {
                // Ignorar si no hay video
            }

            out.print("}");

        } catch (Exception e) {
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
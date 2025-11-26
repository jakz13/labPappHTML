// src/main/java/com/example/servlets/ConsultaRutaServlet.java
package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.List;

import serviciosweb.JuanViajesWS;
import serviciosweb.DtRutaVuelo;
import com.example.util.PortUtils;

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

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            JuanViajesWS port = PortUtils.getPort(request);
            // Inicialización centralizada: PortUtils/SoapStartupListener se encarga de cargar datos en startup.

            // === CONTAR LA VISITA A ESTA RUTA ESPECÍFICA ===
            //port.incrementarVisitasRuta(nombreRuta.trim());
            System.out.println("[CONSULTA RUTA] ✅ Visita contada para: " + nombreRuta);

            // Buscar la ruta en todas las aerolíneas
            DtRutaVuelo rutaEncontrada = null;
            List<DtRutaVuelo> todasRutas = port.listarRutasConfirmadas(1000);

            for (DtRutaVuelo ruta : todasRutas) {
                if (ruta.getNombre().equals(nombreRuta)) {
                    rutaEncontrada = ruta;
                    break;
                }
            }

            if (rutaEncontrada == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Ruta no encontrada o no confirmada\"}");
                return;
            }

            // Generar JSON con la información completa de la ruta
            out.print("{");
            out.print("\"nombre\":\"" + escapeJson(rutaEncontrada.getNombre()) + "\",");
            out.print("\"descripcion\":\"" + escapeJson(rutaEncontrada.getDescripcion()) + "\",");
            out.print("\"descripcionCorta\":\"" + escapeJson(rutaEncontrada.getDescripcionCorta()) + "\",");
            out.print("\"origen\":\"" + escapeJson(rutaEncontrada.getCiudadOrigen()) + "\",");
            out.print("\"destino\":\"" + escapeJson(rutaEncontrada.getCiudadDestino()) + "\",");
            out.print("\"aerolinea\":\"" + escapeJson(rutaEncontrada.getAerolinea()) + "\",");
            out.print("\"hora\":\"" + escapeJson(rutaEncontrada.getHora()) + "\",");
            out.print("\"estado\":\"" + escapeJson(String.valueOf(rutaEncontrada.getEstado())) + "\",");
            out.print("\"costoTurista\":" + rutaEncontrada.getCostoTurista() + ",");
            out.print("\"costoEjecutivo\":" + rutaEncontrada.getCostoEjecutivo() + ",");
            out.print("\"costoEquipaje\":" + rutaEncontrada.getCostoEquipajeExtra() + ",");
            out.print("\"contadorVisitas\":" + rutaEncontrada.getContadorVisitas());

            // Categorías
            out.print(",\"categorias\":[");
            if (rutaEncontrada.getCategorias() != null && !rutaEncontrada.getCategorias().isEmpty()) {
                for (int i = 0; i < rutaEncontrada.getCategorias().size(); i++) {
                    if (i > 0) out.print(",");
                    out.print("\"" + escapeJson(rutaEncontrada.getCategorias().get(i)) + "\"");
                }
            }
            out.print("]");

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
            System.err.println("[CONSULTA RUTA] ❌ Error: " + e.getMessage());
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
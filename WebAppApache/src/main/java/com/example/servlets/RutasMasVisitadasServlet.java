// src/main/java/com/example/servlets/RutasMasVisitadasServlet.java
package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtRutaVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.Map;
import java.util.List;

@WebServlet("/rutasMasVisitadas")
public class RutasMasVisitadasServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            Map<String, Integer> top5Visitas = ContadorVisitasRutas.getTop5RutasMasVisitadas();

            out.print("[");
            int count = 0;

            for (Map.Entry<String, Integer> entry : top5Visitas.entrySet()) {
                String nombreRuta = entry.getKey();
                Integer visitas = entry.getValue();

                // Buscar información completa de la ruta
                DtRutaVuelo rutaInfo = buscarRutaPorNombre(sistema, nombreRuta);

                if (count > 0) out.print(",");

                out.print("{");
                out.print("\"nombreRuta\":\"" + escapeJson(nombreRuta) + "\",");
                out.print("\"visitas\":" + visitas);

                if (rutaInfo != null) {
                    out.print(",\"aerolinea\":\"" + escapeJson(obtenerAerolineaDeRuta(rutaInfo)) + "\"");
                    out.print(",\"ciudadOrigen\":\"" + escapeJson(rutaInfo.getCiudadOrigen()) + "\"");
                    out.print(",\"ciudadDestino\":\"" + escapeJson(rutaInfo.getCiudadDestino()) + "\"");
                } else {
                    out.print(",\"aerolinea\":\"Desconocida\"");
                    out.print(",\"ciudadOrigen\":\"Desconocida\"");
                    out.print(",\"ciudadDestino\":\"Desconocida\"");
                }

                out.print("}");
                count++;
            }
            out.print("]");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error al obtener estadísticas: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private DtRutaVuelo buscarRutaPorNombre(ISistema sistema, String nombreRuta) {
        try {
            // Buscar en todas las aerolíneas
            List<DtRutaVuelo> todasRutas = sistema.listarRutasConfirmadas(100);
            for (DtRutaVuelo ruta : todasRutas) {
                if (ruta.getNombre().equals(nombreRuta)) {
                    return ruta;
                }
            }
        } catch (Exception e) {
            // Si falla, retornar null
        }
        return null;
    }

    private String obtenerAerolineaDeRuta(DtRutaVuelo ruta) {
        try {
            // Usar reflexión para obtener la aerolínea si no está disponible directamente
            java.lang.reflect.Method m = ruta.getClass().getMethod("getAerolinea");
            Object aerolinea = m.invoke(ruta);
            return aerolinea != null ? aerolinea.toString() : "Desconocida";
        } catch (Exception e) {
            return "Desconocida";
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
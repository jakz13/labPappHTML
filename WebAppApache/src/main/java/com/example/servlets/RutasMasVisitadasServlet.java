package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtRutaVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
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

            //Obtener las rutas más visitadas como DTOs
            List<DtRutaVuelo> rutasTop = sistema.obtenerTopRutasMasVisitadas(5);

            // Convertir a JSON manualmente
            out.print("[");
            for (int i = 0; i < rutasTop.size(); i++) {
                DtRutaVuelo ruta = rutasTop.get(i);

                if (i > 0) out.print(",");

                out.print("{");
                out.print("\"nombreRuta\":\"" + escapeJson(ruta.getNombre()) + "\",");
                out.print("\"visitas\":" + ruta.getContadorVisitas() + ",");
                out.print("\"aerolinea\":\"" + escapeJson(ruta.getAerolinea()) + "\",");
                out.print("\"ciudadOrigen\":\"" + escapeJson(ruta.getCiudadOrigen()) + "\",");
                out.print("\"ciudadDestino\":\"" + escapeJson(ruta.getCiudadDestino()) + "\"");
                out.print("}");
            }
            out.print("]");

            System.out.println("[RutasMasVisitadasServlet]  Top " + rutasTop.size() + " rutas enviadas en JSON");

        } catch (Exception e) {
            System.err.println("[RutasMasVisitadasServlet]  Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error al obtener estadísticas: " + escapeJson(e.getMessage()) + "\"}");
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
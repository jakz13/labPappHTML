// src/main/java/com/example/servlets/DetalleVueloCompletoServlet.java
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtVuelo;
import DataTypes.DtReserva;
import Logica.Vuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/detalle-vuelo-completo")
public class DetalleVueloCompletoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreVuelo = request.getParameter("nombreVuelo");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            // Obtener información completa del vuelo
            Vuelo vueloo = sistema.verInfoVuelo(nombreVuelo);
            DtVuelo vuelo = vueloo.getDtVuelo();

            if (vuelo != null) {
                out.print("{");
                out.print("\"success\": true,");
                out.print("\"vuelo\": {");
                out.print("\"nombre\": \"" + escapeJson(vuelo.getNombre()) + "\",");
                out.print("\"fecha\": \"" + escapeJson(vuelo.getFecha() != null ? vuelo.getFecha().toString() : "") + "\",");
                out.print("\"duracion\": \"" + escapeJson(String.valueOf(vuelo.getDuracion())) + "\",");
                out.print("\"asientosTurista\": " + vuelo.getAsientosTurista() + ",");
                out.print("\"asientosEjecutivo\": " + vuelo.getAsientosEjecutivo() + ",");
                out.print("\"rutaNombre\": \"" + escapeJson(vuelo.getRutaVuelo().getNombre()) + "\",");
                out.print("\"rutaDescripcion\": \"" + escapeJson(vuelo.getRutaVuelo().getDescripcion()) + "\",");
                out.print("\"totalReservas\": " + (vuelo.getReservas() != null ? vuelo.getReservas().size() : 0));
                out.print("}");
                out.print("}");
            } else {
                out.print("{\"success\": false, \"error\": \"Vuelo no encontrado\"}");
            }

        } catch (Exception e) {
            out.print("{\"success\": false, \"error\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
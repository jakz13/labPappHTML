// src/main/java/com/example/servlets/ListarVuelosPorRutaServlet.java
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/vuelos")
public class ListarVuelosPorRutaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreRuta = request.getParameter("nombreRuta");
        ISistema sistema = Fabrica.getInstance().getISistema();
        List<DtVuelo> vuelos = sistema.listarVuelosPorRuta(nombreRuta);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("[");
        for (int i = 0; i < vuelos.size(); i++) {
            DtVuelo v = vuelos.get(i);
            out.print("{");
            out.print("\"nombre\":\"" + escapeJson(v.getNombre()) + "\",");
            out.print("\"fecha\":\"" + escapeJson(v.getFecha() != null ? v.getFecha().toString() : "") + "\",");
            out.print("\"duracion\":" + v.getDuracion());
            out.print("}");
            if (i < vuelos.size() - 1) out.print(",");
        }
        out.print("]");
        out.flush();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
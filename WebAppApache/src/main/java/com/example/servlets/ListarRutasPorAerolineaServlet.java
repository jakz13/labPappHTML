// src/main/java/com/example/servlets/ListarRutasPorAerolineaServlet.java
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtRutaVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/rutas")
public class ListarRutasPorAerolineaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreAerolinea = request.getParameter("nombreAerolinea");
        ISistema sistema = Fabrica.getInstance().getISistema();
        List<DtRutaVuelo> rutas = sistema.listarRutasPorAerolinea(nombreAerolinea);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("[");
        for (int i = 0; i < rutas.size(); i++) {
            DtRutaVuelo r = rutas.get(i);
            out.print("{");
            out.print("\"nombre\":\"" + escapeJson(r.getNombre()) + "\",");
            out.print("\"descripcion\":\"" + escapeJson(r.getDescripcion()) + "\"");
            out.print("}");
            if (i < rutas.size() - 1) out.print(",");
        }
        out.print("]");
        out.flush();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
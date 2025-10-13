// src/main/java/com/example/servlets/ListarAerolineasServlet.java
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtAerolinea;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/aerolineas")
public class ListarAerolineasServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ISistema sistema = Fabrica.getInstance().getISistema();
        List<DtAerolinea> aerolineas = sistema.listarAerolineas();

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("[");
        for (int i = 0; i < aerolineas.size(); i++) {
            DtAerolinea a = aerolineas.get(i);
            out.print("{");
            out.print("\"nickname\":\"" + escapeJson(a.getNickname()) + "\",");
            out.print("\"nombre\":\"" + escapeJson(a.getNombre()) + "\"");
            out.print("}");
            if (i < aerolineas.size() - 1) out.print(",");
        }
        out.print("]");
        out.flush();
    }

    // Método para escapar caracteres especiales en JSON
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
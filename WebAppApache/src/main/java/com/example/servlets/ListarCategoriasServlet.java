// src/main/java/com/example/servlets/ListarCategoriasServlet.java
package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.*;

import com.example.usecases.CategoriasUseCase;

@WebServlet("/listarCategorias")
public class ListarCategoriasServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Obtener categorías siempre desde el port SOAP a través del caso de uso centralizado
            List<String> categorias = CategoriasUseCase.getCategorias(request, true);

            // Añadir cabeceras diagnósticas para facilitar depuración desde el cliente
            try {
                response.setHeader("X-Categorias-Count", String.valueOf(categorias.size()));
                String sample = categorias.stream().limit(3).collect(java.util.stream.Collectors.joining(","));
                response.setHeader("X-Categorias-Sample", sample);
            } catch (Exception ignored) {}

            // Construir JSON array simple y enviarlo
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < categorias.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(categorias.get(i).replace("\"","\\\"")).append("\"");
            }
            sb.append("]");
            out.print(sb.toString());

        } catch (Exception e) {
            System.err.println("ListarCategoriasServlet: error al listar categorías: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("[]");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}

// ListarCategoriasServlet.java
package com.example.servlets;

import com.example.util.PortUtils;
import serviciosweb.DtCategoria;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.List;

@WebServlet("/listarCategorias")
public class ListarCategoriasServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Obtener el port usando PortUtils
            serviciosweb.JuanViajesWS port = PortUtils.getPort(request);

            List<DtCategoria> categorias = port.listarCategorias();

            if (categorias == null || categorias.isEmpty()) {
                System.out.println("Cantidad de categorías encontradas: 0 (null o vacía)");
                out.print("[]");
            } else {
                System.out.println("Cantidad de categorías encontradas: " + categorias.size());
                for (DtCategoria c : categorias) {
                    System.out.println("Categoría: " + (c != null ? c.getNombre() : "(null)"));
                }
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                for (int i = 0; i < categorias.size(); i++) {
                    DtCategoria c = categorias.get(i);
                    String nombre = c != null ? c.getNombre() : null;
                    sb.append('{');
                    sb.append("\"nombre\":\"").append(escapeJson(nombre)).append("\"");
                    sb.append('}');
                    if (i < categorias.size() - 1) sb.append(',');
                }
                sb.append(']');
                out.print(sb.toString());
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("[]");
            e.printStackTrace();
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r").replace("\t","\\t");
    }
}
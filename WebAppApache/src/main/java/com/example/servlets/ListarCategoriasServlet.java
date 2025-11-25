// src/main/java/com/example/servlets/ListarCategoriasServlet.java
package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.*;

import serviciosweb.JuanViajesWS;
import com.example.util.PortUtils;

@WebServlet("/listarCategorias")
public class ListarCategoriasServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            JuanViajesWS port = PortUtils.getPort(request);
            // Intentar cargar desde BD para asegurar datos en el servicio
            try { port.cargarDesdeBd(); } catch (Exception ignored) {}

            Object raw = null;
            // Intentar invocar listarCategorias si el servicio lo expone (por reflexión)
            try {
                java.lang.reflect.Method m = port.getClass().getMethod("listarCategorias");
                raw = m.invoke(port);
            } catch (NoSuchMethodException nsme) {
                // Si no existe el método en el servicio SOAP, devolver categorías por defecto
                raw = Arrays.asList("nacionales", "internacionales", "europa", "america", "exclusivos", "temporada", "cortos");
                System.out.println("ListarCategoriasServlet: listarCategorias no disponible en el servicio remoto; devolviendo categorías por defecto.");
            } catch (Exception ex) {
                System.err.println("ListarCategoriasServlet: error al invocar listarCategorias: " + ex.getMessage());
                raw = Collections.emptyList();
            }

            List<String> categorias = new ArrayList<>();
            if (raw instanceof List) {
                for (Object elem : (List<?>) raw) {
                    if (elem == null) continue;
                    if (elem instanceof String) {
                        categorias.add((String) elem);
                    } else {
                        try {
                            java.lang.reflect.Method getter = elem.getClass().getMethod("getNombre");
                            Object val = getter.invoke(elem);
                            categorias.add(val != null ? String.valueOf(val) : "");
                        } catch (Exception e) {
                            categorias.add(elem.toString());
                        }
                    }
                }
            }

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
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("[]");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}

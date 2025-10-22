package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import Logica.Vuelo;
import DataTypes.DtVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/debug-vuelo")
public class DebugVueloServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombre = request.getParameter("nombre");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        if (nombre == null || nombre.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"missing nombre\"}");
            return;
        }

        ISistema sistema = null;
        try {
            sistema = Fabrica.getInstance().getISistema();
            try { sistema.cargarDesdeBd(); } catch (Exception ignore) {}
        } catch (Throwable t) {
            out.print("{\"error\":\"No se pudo obtener ISistema: " + t.getMessage() + "\"}");
            return;
        }

        Vuelo vuelo = null;
        try { vuelo = sistema.verInfoVuelo(nombre); } catch (Throwable t) { /* ignore */ }
        if (vuelo == null) {
            out.print("{\"error\":\"Vuelo no encontrado\"}");
            return;
        }

        DtVuelo dt = vuelo.getDtVuelo();
        Map<String, Object> map = new HashMap<>();
        map.put("_class", dt.getClass().getName());
        try {
            Method[] methods = dt.getClass().getMethods();
            for (Method m : methods) {
                String name = m.getName();
                if (m.getParameterCount() == 0 && name.startsWith("get")) {
                    try {
                        Object val = m.invoke(dt);
                        String key = name.substring(3);
                        if (key.length() > 0) key = Character.toLowerCase(key.charAt(0)) + key.substring(1);
                        map.put(key, val == null ? null : String.valueOf(val));
                    } catch (Throwable t) {
                        map.put(name, "<error>" + t.getMessage());
                    }
                }
            }
        } catch (Throwable e) {
            out.print("{\"error\":\"Error reflectando DtVuelo: " + e.getMessage() + "\"}");
            return;
        }

        // Construir JSON simple
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('"').append(e.getKey().replace("\"","\\\"")).append('"').append(':');
            Object v = e.getValue();
            if (v == null) sb.append("null");
            else sb.append('"').append(String.valueOf(v).replace("\\","\\\\").replace("\"","\\\"")).append('"');
        }
        sb.append('}');
        out.print(sb.toString());
    }
}


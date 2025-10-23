package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtRutaVuelo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.temporal.Temporal;
import java.util.*;

@WebServlet("/api/ruta")
public class GetRutaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String nombre = request.getParameter("nombre");

        ISistema sistema = Fabrica.getInstance().getISistema();
        sistema.cargarDesdeBd();
        List<DtRutaVuelo> rutas = sistema.listarRutasPorAerolinea(null);

        Object found = null;
        if (rutas != null) {
            for (DtRutaVuelo r : rutas) {
                try {
                    String rId = invokeGetterSafe(r, new String[]{"getId", "getCodigo", "getNombre", "id"});
                    String rName = invokeGetterSafe(r, new String[]{"getNombre", "getNombreRuta", "nombre"});
                    if (id != null && !id.isEmpty()) {
                        if (rId != null && rId.equalsIgnoreCase(id)) { found = r; break; }
                        if (rName != null && rName.equalsIgnoreCase(id)) { found = r; break; }
                    } else if (nombre != null && !nombre.isEmpty()) {
                        if (rName != null && rName.equalsIgnoreCase(nombre)) { found = r; break; }
                    }
                } catch (Exception ignored) {}
            }
        }

        if (found == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\":\"Ruta no encontrada\"}");
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        appendObjectAsJson(found, sb, 0, new HashSet<>());
        out.print(sb.toString());
    }

    private static String invokeGetterSafe(Object obj, String[] candidates) {
        try {
            for (String name : candidates) {
                try {
                    Method m = obj.getClass().getMethod(name);
                    Object val = m.invoke(obj);
                    if (val != null) return String.valueOf(val);
                } catch (NoSuchMethodException nsme) {
                    try {
                        Field f = obj.getClass().getDeclaredField(name);
                        f.setAccessible(true);
                        Object v = f.get(obj);
                        if (v != null) return String.valueOf(v);
                    } catch (Exception ignore) {}
                }
            }
        } catch (Exception ignore) {}
        return null;
    }

    private static void appendObjectAsJson(Object obj, StringBuilder sb, int depth, Set<Object> seen) {
        if (obj == null) { sb.append("null"); return; }
        if (seen.contains(obj)) { sb.append("null"); return; }
        if (depth > 3) { sb.append('"').append(escapeJson(String.valueOf(obj))).append('"'); return; }

        if (obj instanceof String) {
            sb.append('"').append(escapeJson((String) obj)).append('"');
            return;
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            sb.append(String.valueOf(obj));
            return;
        }
        if (obj instanceof Temporal) {
            sb.append('"').append(escapeJson(obj.toString())).append('"');
            return;
        }
        Class<?> cls = obj.getClass();
        if (cls.isArray()) {
            sb.append('[');
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                appendObjectAsJson(Array.get(obj, i), sb, depth + 1, seen);
                if (i < len - 1) sb.append(',');
            }
            sb.append(']');
            return;
        }
        if (obj instanceof Collection) {
            sb.append('[');
            Iterator<?> it = ((Collection<?>) obj).iterator();
            int i = 0;
            while (it.hasNext()) {
                if (i++ > 0) sb.append(',');
                appendObjectAsJson(it.next(), sb, depth + 1, seen);
            }
            sb.append(']');
            return;
        }

        sb.append('{');
        Method[] methods = cls.getMethods();
        boolean first = true;
        seen.add(obj);
        for (Method m : methods) {
            String name = m.getName();
            if (m.getParameterCount() != 0) continue;
            if (name.equals("getClass")) continue;
            if ((name.startsWith("get") || name.startsWith("is")) && name.length() > 2) {
                String key = name.startsWith("get") ? name.substring(3) : name.substring(2);
                key = Character.toLowerCase(key.charAt(0)) + key.substring(1);
                try {
                    Object val = m.invoke(obj);
                    if (!first) sb.append(',');
                    sb.append('"').append(escapeJson(key)).append('"').append(':');
                    appendObjectAsJson(val, sb, depth + 1, seen);
                    first = false;
                } catch (Exception ignore) {}
            }
        }
        seen.remove(obj);
        sb.append('}');
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r").replace("\t","\\t");
    }
}


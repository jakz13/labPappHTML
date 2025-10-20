package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtRutaVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.time.temporal.Temporal;
import java.util.*;

@WebServlet("/api/rutas")
public class ListarRutasPorAerolineaServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String aerolinea = request.getParameter("aerolinea");
        String categoria = request.getParameter("categoria");
        String estado = request.getParameter("estado");

        ISistema sistema = Fabrica.getInstance().getISistema();
        sistema.cargarDesdeBd();
        List<DtRutaVuelo> rutas = sistema.listarRutasPorAerolinea((aerolinea != null && !aerolinea.trim().isEmpty()) ? aerolinea : null);

        // aplicar filtros adicionales en servidor (categoria y estado)
        List<Object> rutasFiltradas = new ArrayList<>();
        for (DtRutaVuelo r : rutas) {
            try {
                boolean pasaEstado = true;
                if (estado != null && !estado.isEmpty() && !"todas".equalsIgnoreCase(estado)) {
                    // intentar obtener estado mediante getter
                    String estadoVal = invokeGetterSafe(r, new String[]{"getEstado", "getEstadoRuta", "estado"});
                    if (estadoVal == null) estadoVal = "";
                    pasaEstado = "confirmada".equalsIgnoreCase(estado) ? estadoVal.equalsIgnoreCase("Confirmada") || estadoVal.equalsIgnoreCase("confirmada") : true;
                }

                boolean pasaCategoria = true;
                if (categoria != null && !categoria.isEmpty()) {
                    // intentar obtener categorias como arreglo o lista vía reflexión
                    Object catsObj = invokeGetterObjectSafe(r, new String[]{"getCategorias", "categorias", "getCategoria"});
                    if (catsObj != null) {
                        String catsStr = String.valueOf(catsObj).toLowerCase();
                        pasaCategoria = catsStr.contains(categoria.toLowerCase());
                    } else {
                        pasaCategoria = true; // no hay info -> dejar pasar
                    }
                }

                if (pasaEstado && pasaCategoria) {
                    rutasFiltradas.add(r);
                }
            } catch (Exception ex) {
                // si hay error reflexivo, incluir la ruta por defecto
                rutasFiltradas.add(r);
            }
        }

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < rutasFiltradas.size(); i++) {
            Object r = rutasFiltradas.get(i);
            try {
                appendObjectAsJson(r, sb, 0, new HashSet<>());
            } catch (Exception e) {
                sb.append("{}");
            }
            if (i < rutasFiltradas.size() - 1) sb.append(",");
        }
        sb.append("]");
        out.print(sb.toString());
    }

    // intenta invocar getters comunes devolviendo String
    private static String invokeGetterSafe(Object obj, String[] candidates) {
        try {
            for (String name : candidates) {
                try {
                    Method m = obj.getClass().getMethod(name);
                    Object val = m.invoke(obj);
                    if (val != null) return String.valueOf(val);
                } catch (NoSuchMethodException nsme) {
                    // intentar con nombre sin get
                    try {
                        Field f = obj.getClass().getField(name);
                        Object v = f.get(obj);
                        if (v != null) return String.valueOf(v);
                    } catch (Exception ignore) {}
                }
            }
        } catch (Exception ignore) {}
        return null;
    }

    private static Object invokeGetterObjectSafe(Object obj, String[] candidates) {
        try {
            for (String name : candidates) {
                try {
                    Method m = obj.getClass().getMethod(name);
                    return m.invoke(obj);
                } catch (NoSuchMethodException nsme) {
                    try {
                        Field f = obj.getClass().getField(name);
                        return f.get(obj);
                    } catch (Exception ignore) {}
                }
            }
        } catch (Exception ignore) {}
        return null;
    }

    // convertir objeto a JSON via reflexión, profundidad limitada
    private static void appendObjectAsJson(Object obj, StringBuilder sb, int depth, Set<Object> seen) {
        if (obj == null) { sb.append("null"); return; }
        if (seen.contains(obj)) { sb.append("null"); return; }
        if (depth > 3) { sb.append('"').append(escapeJson(String.valueOf(obj))).append('"'); return; }

        Class<?> cls = obj.getClass();
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
        if (obj.getClass().isArray()) {
            sb.append('[');
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                Object el = Array.get(obj, i);
                appendObjectAsJson(el, sb, depth + 1, seen);
                if (i < len - 1) sb.append(',');
            }
            sb.append(']');
            return;
        }
        if (obj instanceof Collection) {
            sb.append('[');
            Iterator<?> it = ((Collection<?>) obj).iterator();
            int idx = 0;
            while (it.hasNext()) {
                if (idx++ > 0) sb.append(',');
                appendObjectAsJson(it.next(), sb, depth + 1, seen);
            }
            sb.append(']');
            return;
        }
        // objetos simples/DTOs: recorrer getters
        sb.append('{');
        Method[] methods = cls.getMethods();
        boolean first = true;
        seen.add(obj);
        for (Method m : methods) {
            String name = m.getName();
            if (m.getParameterCount() != 0) continue;
            if (name.equals("getClass")) continue;
            if ((name.startsWith("get") || name.startsWith("is")) && name.length() > 2) {
                String key = null;
                if (name.startsWith("get")) key = name.substring(3);
                else key = name.substring(2);
                if (key.length() == 0) continue;
                // lower first char
                key = Character.toLowerCase(key.charAt(0)) + key.substring(1);
                try {
                    Object val = m.invoke(obj);
                    if (!first) sb.append(',');
                    sb.append('"').append(escapeJson(key)).append('"').append(':');
                    appendObjectAsJson(val, sb, depth + 1, seen);
                    first = false;
                } catch (Exception ignore) {
                    // skip
                }
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

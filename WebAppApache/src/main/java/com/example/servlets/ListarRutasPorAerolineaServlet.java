package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
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

        // Aplicar filtros
        List<DtRutaVuelo> rutasFiltradas = new ArrayList<>();
        for (DtRutaVuelo r : rutas) {
            boolean pasaFiltros = true;

            // Filtro por categoría
            if (categoria != null && !categoria.isEmpty() && !"todas".equalsIgnoreCase(categoria)) {
                if (r.getCategorias() != null) {
                    boolean tieneCategoria = false;
                    for (String cat : r.getCategorias()) {
                        if (cat.equalsIgnoreCase(categoria)) {
                            tieneCategoria = true;
                            break;
                        }
                    }
                    pasaFiltros = pasaFiltros && tieneCategoria;
                } else {
                    pasaFiltros = false; // Si no tiene categorías definidas, no pasa el filtro
                }
            }

            // Filtro por estado
            if (estado != null && !estado.isEmpty() && !"todas".equalsIgnoreCase(estado)) {
                String estadoRuta = r.getEstado() != null ? r.getEstado() : "";
                pasaFiltros = pasaFiltros && estadoRuta.equalsIgnoreCase(estado);
            }

            if (pasaFiltros) {
                rutasFiltradas.add(r);
            }
        }

        // Convertir a JSON
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < rutasFiltradas.size(); i++) {
            DtRutaVuelo r = rutasFiltradas.get(i);
            sb.append("{");
            sb.append("\"nombre\":\"").append(escapeJson(r.getNombre())).append("\",");
            sb.append("\"descripcion\":\"").append(escapeJson(r.getDescripcion())).append("\",");
            sb.append("\"origen\":\"").append(escapeJson(r.getCiudadOrigen())).append("\",");
            sb.append("\"destino\":\"").append(escapeJson(r.getCiudadDestino())).append("\",");
            sb.append("\"estado\":\"").append(escapeJson(r.getEstado())).append("\",");

            // Categorías como array
            sb.append("\"categorias\":[");
            if (r.getCategorias() != null) {
                for (int j = 0; j < r.getCategorias().size(); j++) {
                    sb.append("\"").append(escapeJson(r.getCategorias().get(j))).append("\"");
                    if (j < r.getCategorias().size() - 1) sb.append(",");
                }
            }
            sb.append("],");

            sb.append("\"costoTurista\":").append(r.getCostoTurista()).append(",");
            sb.append("\"costoEjecutivo\":").append(r.getCostoEjecutivo()).append(",");
            sb.append("\"costoEquipaje\":").append(r.getCostoEquipajeExtra());

            // intentar obtener una imagen (varios getters posibles) y normalizar a URL absoluta
            try {
                String imagenVal = invokeGetterSafe(r, new String[]{"getImagenUrl", "getImagen", "imagenUrl", "imagen", "getImagenPath", "imagenPath", "url"});

                // DEBUG: imprimir en consola el valor bruto recibido desde el DTO/BD
                try {
                    String nombreRuta = r.getNombre();
                    System.out.println("[DEBUG ListarRutas] ruta='" + nombreRuta + "' - valor imagen bruto: '" + (imagenVal == null ? "<null>" : imagenVal) + "'");
                } catch (Exception e) {
                    System.out.println("[DEBUG ListarRutas] error accediendo nombre de ruta: " + e.getMessage());
                }

                if (imagenVal != null && !imagenVal.isBlank()) {
                    String tmp = imagenVal.trim();
                    try {
                        if (!tmp.matches("(?i)^(https?:)?//.*") && !tmp.startsWith("/") && !tmp.startsWith("data:") && !tmp.startsWith("Images/")) {
                            String ctx = request.getContextPath();
                            if (ctx == null) ctx = "";
                            if (!ctx.endsWith("/")) tmp = ctx + "/Images/" + tmp; else tmp = ctx + "Images/" + tmp;
                        }
                    } catch (Exception ignore) {}

                    try {
                        if (!tmp.matches("(?i)^(https?:)?//.*")) {
                            String scheme = request.getScheme();
                            String serverName = request.getServerName();
                            int serverPort = request.getServerPort();
                            String portPart = "";
                            if (!("http".equalsIgnoreCase(scheme) && serverPort == 80) && !("https".equalsIgnoreCase(scheme) && serverPort == 443)) {
                                portPart = ":" + serverPort;
                            }
                            if (!tmp.startsWith("/")) tmp = "/" + tmp;
                            String absolute = scheme + "://" + serverName + portPart + tmp;
                            imagenVal = absolute;
                        } else {
                            imagenVal = tmp;
                        }
                    } catch (Exception ignore) {
                        imagenVal = tmp;
                    }

                    if (imagenVal != null && !imagenVal.isBlank()) {
                        sb.append(",\"imagenUrl\":\"").append(escapeJson(imagenVal)).append("\"");
                    }
                }
            } catch (Exception ignore) {}

            sb.append("}");

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
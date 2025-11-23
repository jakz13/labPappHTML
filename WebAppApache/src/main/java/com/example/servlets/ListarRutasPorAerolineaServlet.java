package com.example.servlets;

import logica.EstadoRuta;
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

        // DEBUG: log del parámetro recibido para facilitar diagnóstico (por qué no lista rutas)
        try { System.out.println("[DEBUG api/rutas] param aerolinea='" + aerolinea + "', categoria='" + categoria + "', estado='" + estado + "'"); } catch (Throwable ignore) {}

        ISistema sistema = Fabrica.getInstance().getISistema();
        sistema.cargarDesdeBd();

        sistema.obtenerAerolinea(aerolinea);
        List<DtRutaVuelo> rutasTotales = sistema.listarRutasPorAerolinea((aerolinea != null && !aerolinea.trim().isEmpty()) ? aerolinea : null);

        // Determinar si el usuario en sesión es la aerolínea propietaria solicitada
        HttpSession session = request.getSession(false);
        boolean ownerIsRequesting = false;
        if (session != null) {
            Object tipoUsuario = session.getAttribute("tipoUsuario");
            Object usuarioSession = session.getAttribute("usuario");
            if (tipoUsuario != null && "aerolinea".equalsIgnoreCase(String.valueOf(tipoUsuario))
                    && usuarioSession != null && aerolinea != null
                    && String.valueOf(usuarioSession).equalsIgnoreCase(aerolinea)) {
                ownerIsRequesting = true;
            }
        }

        // Si el que pide es la aerolínea dueña, le mostramos todas sus rutas (incluidas no confirmadas).
        // En caso contrario, exponemos solo las rutas confirmadas.
        List<DtRutaVuelo> rutas;
        if (rutasTotales == null) {
            rutas = new ArrayList<>();
        } else if (ownerIsRequesting) {
            rutas = rutasTotales; // dueño: ver todas sus rutas
            System.out.println("[DEBUG ListarRutas] usuario dueño detectado, mostrando todas las rutas para: " + aerolinea);
        } else {
            // filtrar sólo CONFIRMADA
            List<DtRutaVuelo> rutasConfirmadas = new ArrayList<>();
            for (DtRutaVuelo ruta : rutasTotales) {
                EstadoRuta est = ruta.getEstado() != null ? ruta.getEstado() : EstadoRuta.INGRESADA;
                if ("CONFIRMADA".equalsIgnoreCase(String.valueOf(est))) {
                    rutasConfirmadas.add(ruta);
                    System.out.println("[DEBUG ListarRutas] ruta confirmada extraida: " + ruta.getNombre());
                }
            }
            rutas = rutasConfirmadas;
        }

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

            // Filtro por estado (si se solicita un estado distinto de "todas")
            if (estado != null && !estado.isEmpty() && !"todas".equalsIgnoreCase(estado)) {
                EstadoRuta estadoRuta = r.getEstado() != null ? r.getEstado() : EstadoRuta.INGRESADA;
                pasaFiltros = pasaFiltros && estadoRuta.equals(estado);
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
            sb.append("\"estado\":\"").append(escapeJson(String.valueOf(r.getEstado()))).append("\",");

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

            try {
                String imagenVal = invokeGetterSafe(r, new String[]{"getImagenUrl", "getImagen", "imagenUrl", "imagen", "getImagenPath", "imagenPath", "url"});


                if (imagenVal != null && !imagenVal.isBlank()) {
                    String tmp = imagenVal.trim();
                    try {
                        if (!tmp.matches("(?i)^(https?:)?//.*")) {
                            // construir URL absoluta igual que en la JVM de consulta de vuelo
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

            // AÑADIR: Procesar videoUrl (NUEVO CÓDIGO)
            try {
                String videoVal = r.getVideoUrl();

                if (videoVal != null && !videoVal.isBlank()) {
                    String tmp = videoVal.trim();
                    try {
                        if (!tmp.matches("(?i)^(https?:)?//.*")) {
                            // construir URL absoluta igual que para imágenes
                            String scheme = request.getScheme();
                            String serverName = request.getServerName();
                            int serverPort = request.getServerPort();
                            String portPart = "";
                            if (!("http".equalsIgnoreCase(scheme) && serverPort == 80) && !("https".equalsIgnoreCase(scheme) && serverPort == 443)) {
                                portPart = ":" + serverPort;
                            }
                            if (!tmp.startsWith("/")) tmp = "/" + tmp;
                            String absolute = scheme + "://" + serverName + portPart + tmp;
                            videoVal = absolute;
                        } else {
                            videoVal = tmp;
                        }
                    } catch (Exception ignore) {
                        videoVal = tmp;
                    }

                    if (videoVal != null && !videoVal.isBlank()) {
                        sb.append(",\"videoUrl\":\"").append(escapeJson(videoVal)).append("\"");
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

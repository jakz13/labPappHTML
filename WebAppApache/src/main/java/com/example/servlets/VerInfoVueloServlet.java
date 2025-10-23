// java
package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;

@WebServlet("/api/vuelo")
public class VerInfoVueloServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombre = request.getParameter("nombre");
        ISistema sistema = Fabrica.getInstance().getISistema();
        sistema.cargarDesdeBd();
        DtVuelo vuelo = null;
        try { vuelo = sistema.verInfoVueloDt(nombre); } catch (Throwable t) { /* ignore */ }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (vuelo != null) {
            DtVuelo dt = vuelo;
            out.print("{");
            out.print("\"nombre\":\"" + dt.getNombre() + "\",");
            out.print("\"fecha\":\"" + dt.getFecha() + "\",");
            out.print("\"duracion\":\"" + dt.getDuracion() + "\",");
            out.print("\"asientosTurista\":" + dt.getAsientosTurista() + ",");
            out.print("\"asientosEjecutivo\":" + dt.getAsientosEjecutivo());

            String imagenVal = null;
            try {
                java.lang.reflect.Method m = dt.getClass().getMethod("getImagenUrl");
                Object img = m.invoke(dt);
                if (img != null) {
                    imagenVal = img.toString();
                }
            } catch (NoSuchMethodException ns) {
                // no disponible, ignorar
            } catch (Exception e) {
                // no bloquear la respuesta por un error reflectivo
            }

            if (imagenVal == null || imagenVal.isBlank()) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, String> map = (Map<String, String>) request.getServletContext().getAttribute("FLIGHT_IMAGE_MAP");
                    if (map == null) {
                        map = loadFlightImageMap(request.getServletContext());
                        if (map != null) {
                            request.getServletContext().setAttribute("FLIGHT_IMAGE_MAP", map);
                        }
                    }
                    if (map != null) {
                        String fromMap = map.get(nombre);
                        if (fromMap != null && !fromMap.isBlank()) {
                            imagenVal = fromMap;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error leyendo FLIGHT_IMAGE_MAP: " + e.getMessage());
                }
            }

            if (imagenVal == null || imagenVal.isBlank()) {
                try {
                    Map<String, Map<String, Object>> flights = loadFlightsMap(request.getServletContext());
                    if (flights != null && flights.containsKey(nombre)) {
                        Map<String, Object> rec = flights.get(nombre);
                        Object iu = rec.get("imagenUrl");
                        if (iu != null) imagenVal = String.valueOf(iu);
                    }
                } catch (Exception e) {
                    System.err.println("VerInfoVueloServlet: error leyendo flights.json -> " + e.getMessage());
                }
            }

            if (imagenVal != null && !imagenVal.isBlank()) {
                // Normalizar/convertir a URL pública si necesario
                String resolvedImagenUrl = imagenVal;
                try {
                    if (resolvedImagenUrl != null) {
                        String tmp = resolvedImagenUrl.trim();
                        // Si no es una URL absoluta, ni una ruta absoluta ni data:, asumir nombre de archivo y prefixar con contextPath/Images/
                        if (!tmp.matches("(?i)^(https?:)?//.*") && !tmp.startsWith("/") && !tmp.startsWith("data:") && !tmp.startsWith("Images/")) {
                            String ctx = request.getContextPath();
                            if (ctx == null) ctx = "";
                            if (!ctx.endsWith("/")) {
                                resolvedImagenUrl = ctx + "/Images/" + tmp;
                              } else {
                                resolvedImagenUrl = ctx + "Images/" + tmp;
                              }
                        } else {
                            resolvedImagenUrl = tmp;
                        }
                    }
                } catch (Exception ex) {
                    // si algo falla, usar el valor original
                    resolvedImagenUrl = imagenVal;
                }
                // escapar caracteres que rompan el JSON
                String esc = resolvedImagenUrl.replace("\\", "\\\\").replace("\"", "\\\"");
                out.print(",\"imagenUrl\":\"" + esc + "\"");
             }

            out.print("}");
        } else {
            try {
                Map<String, Map<String, Object>> flights = loadFlightsMap(request.getServletContext());
                if (flights != null && flights.containsKey(nombre)) {
                    Map<String, Object> rec = flights.get(nombre);
                    StringBuilder sb = new StringBuilder();
                    sb.append('{');
                    boolean first = true;
                    for (Map.Entry<String, Object> e : rec.entrySet()) {
                        if (!first) sb.append(',');
                        sb.append('"').append(e.getKey()).append('"').append(':');
                        Object v = e.getValue();
                        if (v == null) sb.append("null");
                        else sb.append('"').append(String.valueOf(v)).append('"');
                        first = false;
                    }
                    sb.append('}');
                    out.print(sb.toString());
                    return;
                }
            } catch (Exception ex) {
                System.err.println("VerInfoVueloServlet: fallback flights.json failed: " + ex.getMessage());
            }

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Vuelo no encontrado\"}");
        }

    }

    private Map<String, String> loadFlightImageMap(ServletContext ctx) {
        Map<String, String> result = new HashMap<>();
        String path = null;
        try {
            path = ctx.getRealPath("/WEB-INF/flight-images.json");
            if (path == null) {
                path = System.getProperty("java.io.tmpdir") + File.separator + "flight-images.json";
            }
            File f = new File(path);
            if (!f.exists() || !f.isFile()) return result;

            String content = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8).trim();
            if (content.startsWith("{")) content = content.substring(1);
            if (content.endsWith("}")) content = content.substring(0, content.length()-1);
            String[] entries = content.split(",\\n");
            for (String e : entries) {
                e = e.trim();
                if (e.isBlank()) continue;
                int colon = e.indexOf(':');
                if (colon <= 0) continue;
                String k = e.substring(0, colon).trim();
                String v = e.substring(colon + 1).trim();
                if (k.startsWith("\"") && k.endsWith("\"")) k = k.substring(1, k.length()-1);
                if (v.startsWith("\"") && v.endsWith("\"")) v = v.substring(1, v.length()-1);
                k = k.replace("\\\"", "\"").replace("\\\\", "\\");
                v = v.replace("\\\"", "\"").replace("\\\\", "\\");
                result.put(k, v);
            }
        } catch (Exception ex) {
            System.err.println("No se pudo leer flight-images.json en " + path + ": " + ex.getMessage());
        }
        return result;
    }

    private Map<String, Map<String, Object>> loadFlightsMap(ServletContext ctx) {
        Map<String, Map<String, Object>> result = new HashMap<>();
        try {
            String path = ctx.getRealPath("/WEB-INF/flights.json");
            if (path == null) path = System.getProperty("java.io.tmpdir") + File.separator + "flights.json";
            File f = new File(path);
            if (!f.exists()) return result;
            String content = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8).trim();
            if (content.length() == 0) return result;
            if (content.startsWith("{")) content = content.substring(1);
            if (content.endsWith("}")) content = content.substring(0, content.length()-1);
            String[] entries = content.split("},\\n");
            for (String e : entries) {
                e = e.trim();
                if (e.endsWith("}")) e = e.substring(0, e.length()-1);
                int colon = e.indexOf(':');
                if (colon <= 0) continue;
                String key = e.substring(0, colon).trim();
                if (key.startsWith("\"") && key.endsWith("\"")) key = key.substring(1, key.length()-1);
                String obj = e.substring(colon+1).trim();
                if (obj.startsWith("{")) obj = obj.substring(1);
                if (obj.endsWith("}")) obj = obj.substring(0, obj.length()-1);
                Map<String, Object> map = new HashMap<>();
                String[] fields = obj.split(",\\s*");
                for (String fentry : fields) {
                    int c2 = fentry.indexOf(':');
                    if (c2 <= 0) continue;
                    String k2 = fentry.substring(0, c2).trim();
                    String v2 = fentry.substring(c2+1).trim();
                    if (k2.startsWith("\"") && k2.endsWith("\"")) k2 = k2.substring(1, k2.length()-1);
                    if (v2.startsWith("\"") && v2.endsWith("\"")) v2 = v2.substring(1, v2.length()-1);
                    map.put(k2, v2);
                }
                result.put(key, map);
            }
        } catch (Exception ex) {
            System.err.println("VerInfoVueloServlet: Error leyendo flights.json -> " + ex.getMessage());
        }
        return result;
    }

}
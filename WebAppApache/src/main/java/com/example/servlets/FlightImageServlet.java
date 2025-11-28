package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.HashMap;

@WebServlet("/api/flight-image")
public class FlightImageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombre = request.getParameter("nombre");
        if (nombre == null || nombre.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\":\"missing nombre\"}");
            return;
        }

        ISistema sistema = Fabrica.getInstance().getISistema();
        try { sistema.cargarDesdeBd(); } catch (Exception ignore) {}
        DtVuelo vuelo = null;
        try { vuelo = sistema.verInfoVueloDt(nombre); } catch (Throwable t) { /* ignore */ }

        String imagenVal = null;
        // intentar leer desde DtVuelo si existe
        if (vuelo != null) {
            try {
                DtVuelo dt = vuelo;

                // Nuevo: intentar servir imagen binaria si el DTO la expone (getImagenBlob/getImagenBytes)
                try {
                    java.lang.reflect.Method mBlob = null;
                    try { mBlob = dt.getClass().getMethod("getImagenBlob"); } catch (NoSuchMethodException ns1) { try { mBlob = dt.getClass().getMethod("getImagenBytes"); } catch (NoSuchMethodException ns2) { mBlob = null; } }
                    if (mBlob != null) {
                        Object blobObj = mBlob.invoke(dt);
                        if (blobObj != null) {
                            byte[] bytes = null;
                            if (blobObj instanceof byte[]) bytes = (byte[]) blobObj;
                            else if (blobObj instanceof java.sql.Blob) {
                                java.sql.Blob b = (java.sql.Blob) blobObj;
                                try (InputStream is = b.getBinaryStream()) { bytes = is.readAllBytes(); }
                            }
                            if (bytes != null && bytes.length > 0) {
                                // intentar obtener MIME
                                String mime = null;
                                try {
                                    java.lang.reflect.Method mMime = null;
                                    try { mMime = dt.getClass().getMethod("getImagenMime"); } catch (NoSuchMethodException nm1) { try { mMime = dt.getClass().getMethod("getImagenContentType"); } catch (NoSuchMethodException nm2) { mMime = null; } }
                                    if (mMime != null) {
                                        Object mm = mMime.invoke(dt);
                                        if (mm != null) mime = mm.toString();
                                    }
                                } catch (Throwable ignore) {}

                                if (mime == null || mime.isBlank()) mime = "application/octet-stream";
                                response.setContentType(mime);
                                response.setHeader("Cache-Control", "public, max-age=86400");
                                response.setContentLengthLong(bytes.length);
                                try (OutputStream os = response.getOutputStream()) { os.write(bytes); }
                                return;
                            }
                        }
                    }
                } catch (Throwable serveBlobErr) {
                    // si falla la lectura del blob, continuar y tratar otros fallback
                    System.err.println("FlightImageServlet: error leyendo blob de DtVuelo: " + serveBlobErr.getMessage());
                }

                try {
                    java.lang.reflect.Method m = dt.getClass().getMethod("getImagenUrl");
                    Object img = m.invoke(dt);
                    if (img != null) imagenVal = img.toString();
                } catch (NoSuchMethodException ns) {
                    // ignore
                }
            } catch (Throwable t) {
                // ignore
            }
        }

        // fallback: consultar map en contexto (flight-images.json)
        if (imagenVal == null || imagenVal.isBlank()) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) request.getServletContext().getAttribute("FLIGHT_IMAGE_MAP");
                if (map == null) {
                    map = loadFlightImageMap(request.getServletContext());
                    if (map != null) request.getServletContext().setAttribute("FLIGHT_IMAGE_MAP", map);
                }
                if (map != null) {
                    String fromMap = map.get(nombre);
                    if (fromMap != null && !fromMap.isBlank()) imagenVal = fromMap;
                }
            } catch (Exception e) {
                // ignore
            }
        }

        // fallback: flights.json
        if (imagenVal == null || imagenVal.isBlank()) {
            try {
                Map<String, Map<String, Object>> flights = loadFlightsMap(request.getServletContext());
                if (flights != null && flights.containsKey(nombre)) {
                    Map<String, Object> rec = flights.get(nombre);
                    Object iu = rec.get("imagenUrl");
                    if (iu != null) imagenVal = String.valueOf(iu);
                }
            } catch (Exception e) {}
        }

        if (imagenVal == null || imagenVal.isBlank()) {
            // En modo debug, devolver información diagnóstica para ayudar a localizar el problema
            if ("1".equals(request.getParameter("debug"))) {
                response.setContentType("application/json;charset=UTF-8");
                try {
                    java.util.Map<String,Object> diag = new java.util.HashMap<>();
                    diag.put("vueloEncontrado", vuelo != null);
                    // intentar obtener info del DtVuelo si existe
                    if (vuelo != null) {
                        try {
                            Object dt = vuelo;
                            diag.put("dtClass", dt == null ? null : dt.getClass().getName());
                            // intentar extraer imagenUrl reflexivamente
                            try {
                                java.lang.reflect.Method m = dt.getClass().getMethod("getImagenUrl");
                                Object img = m.invoke(dt);
                                diag.put("dt.imagenUrl", img == null ? null : img.toString());
                            } catch (Throwable ignore) { diag.put("dt.imagenUrl", null); }
                            // intentar getters binarios
                            try {
                                java.lang.reflect.Method mb = null;
                                try { mb = dt.getClass().getMethod("getImagenBlob"); } catch (NoSuchMethodException ns1) { try { mb = dt.getClass().getMethod("getImagenBytes"); } catch (NoSuchMethodException ns2) { mb = null; } }
                                diag.put("dt.hasImagenBlobGetter", mb != null);
                            } catch (Throwable ignore) { diag.put("dt.hasImagenBlobGetter", false); }
                        } catch (Throwable t) {
                            diag.put("dtError", t.getMessage());
                        }
                    }
                    // comprobar FLIGHT_IMAGE_MAP
                    try {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String,String> fmap = (java.util.Map<String,String>) request.getServletContext().getAttribute("FLIGHT_IMAGE_MAP");
                        if (fmap == null) fmap = loadFlightImageMap(request.getServletContext());
                        diag.put("flightImageMapContains", fmap != null && fmap.containsKey(nombre));
                        diag.put("flightImageMapValue", fmap == null ? null : fmap.get(nombre));
                    } catch (Throwable t) { diag.put("flightImageMapError", t.getMessage()); }
                    // comprobar flights.json
                    try {
                        java.util.Map<String, java.util.Map<String,Object>> flights = loadFlightsMap(request.getServletContext());
                        diag.put("flightsJsonContains", flights != null && flights.containsKey(nombre));
                        diag.put("flightsJsonValue", flights == null ? null : flights.get(nombre));
                    } catch (Throwable t) { diag.put("flightsJsonError", t.getMessage()); }

                    // incluir IMAGES_DIR si está
                    try { diag.put("imagesDirAttr", (String) request.getServletContext().getAttribute("IMAGES_DIR")); } catch (Throwable t) { diag.put("imagesDirAttrError", t.getMessage()); }

                    // escribir JSON simple
                    StringBuilder sb = new StringBuilder(); sb.append('{');
                    boolean first = true;
                    for (java.util.Map.Entry<String,Object> e : diag.entrySet()) {
                        if (!first) sb.append(','); first = false;
                        sb.append('"').append(e.getKey().replace("\\","\\\\").replace("\"","\\\"")).append('"').append(':');
                        Object v = e.getValue();
                        if (v == null) sb.append("null");
                        else if (v instanceof String) sb.append('"').append(String.valueOf(v).replace("\\","\\\\").replace("\"","\\\"")).append('"');
                        else sb.append(org.json.JSONObject.valueToString(v));
                    }
                    sb.append('}');
                    response.getWriter().print(sb.toString());
                    return;
                } catch (Throwable t) {
                    System.err.println("FlightImageServlet: Error: " + t.getMessage());
                }
            }

            // no image found
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().print("{\"error\":\"Imagen no encontrada\"}");
            return;
        }

        // imagenVal puede ser data:, URL absoluta o nombre de archivo
        try {
            String val = imagenVal.trim();

            // Si el valor incluye el contextPath (por ejemplo '/WebAppApache/Images/xxx'),
            // normalizarlo removiendo el prefijo para que getRealPath funcione correctamente.
            String ctxPath = request.getContextPath();
            if (ctxPath != null && !ctxPath.isEmpty() && val.startsWith(ctxPath)) {
                // quitar sólo la primera aparición
                val = val.substring(ctxPath.length());
                if (val.isEmpty()) val = "/"; // defensa
            }

            // Si se solicita modo debug, devolver info diagnóstica y no intentar servir la imagen.
            if ("1".equals(request.getParameter("debug"))) {
                String candidate = val;
                if (!val.startsWith("Images/") && !val.startsWith("/")) candidate = "/Images/" + val;
                String realPath = request.getServletContext().getRealPath(candidate);
                String rootTry = request.getServletContext().getRealPath("/" + val.replaceFirst("^/+", ""));
                String imagesDirAttr = null;
                try { imagesDirAttr = (String) request.getServletContext().getAttribute("IMAGES_DIR"); } catch (Exception ignore) {}
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print("{" +
                    "\"imagenVal\":\"" + imagenVal.replace("\\","\\\\").replace("\"","\\\"") + "\"," +
                    "\"val\":\"" + val.replace("\\","\\\\").replace("\"","\\\"") + "\"," +
                    "\"candidate\":\"" + candidate.replace("\\","\\\\").replace("\"","\\\"") + "\"," +
                    "\"realPath\":\"" + (realPath==null?"":realPath).replace("\\","\\\\").replace("\"","\\\"") + "\"," +
                    "\"rootTry\":\"" + (rootTry==null?"":rootTry).replace("\\","\\\\").replace("\"","\\\"") + "\"," +
                    "\"imagesDirAttr\":\"" + (imagesDirAttr==null?"":imagesDirAttr).replace("\\","\\\\").replace("\"","\\\"") + "\"}" );
                return;
            }

            if (val.startsWith("data:")) {
                // data URL: data:[<mediatype>][;base64],<data>
                int comma = val.indexOf(',');
                if (comma > 0) {
                    String meta = val.substring(5, comma); // after data:
                    String base64data = val.substring(comma + 1);
                    String contentType = "image/png";
                    if (meta.contains(";")) contentType = meta.substring(0, meta.indexOf(';'));
                    else if (!meta.isBlank()) contentType = meta;
                    byte[] bytes = Base64.getDecoder().decode(base64data);
                    response.setContentType(contentType);
                    response.setContentLength(bytes.length);
                    response.setHeader("Cache-Control", "public, max-age=86400");
                    try (OutputStream os = response.getOutputStream()) { os.write(bytes); }
                    return;
                }
            }

            // Si es URL absoluta -> redirigir
            if (val.matches("(?i)^(https?:)?//.*") ) {
                response.sendRedirect(val);
                return;
            }

            // Si es ruta absoluta en el servidor, intentar servirla
            if (val.startsWith("/")) {
                String real = request.getServletContext().getRealPath(val);
                if (real != null) {
                    File f = new File(real);
                    if (f.exists() && f.isFile()) { streamFile(f, response); return; }
                }
            }

            // Si contiene Images/ o es nombre de archivo, buscar en /Images/
            String candidate = val;
            if (!val.startsWith("Images/") && !val.startsWith("/")) {
                candidate = "/Images/" + val;
            }
            String realPath = request.getServletContext().getRealPath(candidate);
            if (realPath != null) {
                File f = new File(realPath);
                if (f.exists() && f.isFile()) { streamFile(f, response); return; }
            }

            // Fallback adicional: si la aplicación configuró un directorio físico para imágenes (IMAGES_DIR),
            // intentar servir desde allí (ej. cuando AltaVuelo guarda en temp y usa imagenUrl con /Images/filename)
            try {
                String imagesDirAttr = null;
                try { imagesDirAttr = (String) request.getServletContext().getAttribute("IMAGES_DIR"); } catch (Exception ignore) { imagesDirAttr = null; }
                if (imagesDirAttr != null && !imagesDirAttr.isBlank()) {
                    String candidateName = new File(candidate).getName();
                    File f3 = new File(imagesDirAttr, candidateName);
                    if (f3.exists() && f3.isFile()) { streamFile(f3, response); return; }
                }
            } catch (Throwable t) {
                // no bloquear por errores de fallback
            }

            // última opción: si val parece relativo, intentar dentro de webapp root
            String rootTry = request.getServletContext().getRealPath("/" + val.replaceFirst("^/+", ""));
            if (rootTry != null) {
                File f2 = new File(rootTry);
                if (f2.exists() && f2.isFile()) { streamFile(f2, response); return; }
            }

            // Development fallback: buscar en posibles carpetas target/**/Images ascendiendo desde el webapp o user.dir
            try {
                String fileName = new File(candidate).getName();
                File start = null;
                try { start = new File(request.getServletContext().getRealPath("/")); } catch (Exception ignore) { start = null; }
                if (start == null || !start.exists()) start = new File(System.getProperty("user.dir"));
                File search = start;
                for (int depth = 0; depth < 8 && search != null; depth++) {
                    File targetDir = new File(search, "target");
                    if (targetDir.exists() && targetDir.isDirectory()) {
                        // check known folder name
                        File specific = new File(targetDir, "WebAppApache-1.0-SNAPSHOT/Images");
                        File cand = new File(specific, fileName);
                        if (cand.exists() && cand.isFile()) { streamFile(cand, response); return; }

                        // check any subdirectory under target that has Images
                        File[] subs = targetDir.listFiles(File::isDirectory);
                        if (subs != null) {
                            for (File sd : subs) {
                                File imgdir = new File(sd, "Images");
                                File c = new File(imgdir, fileName);
                                if (c.exists() && c.isFile()) { streamFile(c, response); return; }
                            }
                        }

                        // check target/Images
                        File direct = new File(targetDir, "Images");
                        File dfile = new File(direct, fileName);
                        if (dfile.exists() && dfile.isFile()) { streamFile(dfile, response); return; }
                    }
                    search = search.getParentFile();
                }
            } catch (Throwable t) {
                // no bloquear por errores de fallback
            }

            // no encontrado -> log detallado para ayudar a debugging
            try {
                System.err.println("FlightImageServlet: No se encontró imagen para vuelo='" + nombre + "'. imagenVal='" + imagenVal + "'. intentadas: candidate='" + candidate + "', realPath='" + realPath + "', rootTry='" + rootTry + "', contextPath='" + request.getContextPath() + "'");
            } catch (Throwable t) { /* ignore logging errors */ }

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().print("{\"error\":\"Imagen no encontrada\"}");
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"Error sirviendo imagen\"}");
        }
    }

    private void streamFile(File f, HttpServletResponse response) throws IOException {
        String mime = Files.probeContentType(f.toPath());
        if (mime == null) mime = "application/octet-stream";
        response.setContentType(mime);
        response.setHeader("Cache-Control", "public, max-age=86400");
        response.setContentLengthLong(f.length());
        try (InputStream is = new FileInputStream(f); OutputStream os = response.getOutputStream()) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = is.read(buf)) != -1) os.write(buf, 0, r);
        }
    }

    private Map<String, String> loadFlightImageMap(ServletContext ctx) {
        Map<String, String> result = new HashMap<>();
        String path = null;
        try {
            path = ctx.getRealPath("/WEB-INF/flight-images.json");
            if (path == null) path = System.getProperty("java.io.tmpdir") + File.separator + "flight-images.json";
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
            System.err.println("FlightImageServlet: Error leyendo flights.json -> " + ex.getMessage());
        }
        return result;
    }
}

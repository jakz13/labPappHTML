package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtAerolinea;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Base64;

@WebServlet("/altaVuelo")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 5 * 1024 * 1024,           // 5MB
        maxRequestSize = 10 * 1024 * 1024)       // 10MB
public class AltaVueloServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Usar la misma inicialización que AltaRutaServlet
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            // Obtener aerolínea desde la sesión
            HttpSession session = request.getSession(false);
            String nombreAerolinea = null;
            if (session != null) {
                Object tipo = session.getAttribute("tipoUsuario");
                Object usuario = session.getAttribute("usuario");
                if (usuario != null && "aerolinea".equalsIgnoreCase(String.valueOf(tipo))) {
                    nombreAerolinea = String.valueOf(usuario);
                }
            }
            if (nombreAerolinea == null || nombreAerolinea.isEmpty()) {
                out.print("{\"success\": false, \"error\": \"Usuario no autorizado: debe iniciar sesión como aerolínea\"}");
                return;
            }

            // Leer parámetros (compatible con multipart/form-data)
            String nombreVuelo = getParam(request, "nombreVuelo");
            String nombreRuta = getParam(request, "nombreRuta");
            String fechaStr = getParam(request, "fecha");
            LocalDate fecha = null;
            if (fechaStr != null && !fechaStr.trim().isEmpty()) fecha = LocalDate.parse(fechaStr);
            int duracion = parseIntSafe(getParam(request, "duracion"));
            int asientosTurista = parseIntSafe(getParam(request, "asientosTurista"));
            int asientosEjecutivo = parseIntSafe(getParam(request, "asientosEjecutivo"));
            LocalDate fechaAlta = LocalDate.now();

            // Validaciones básicas
            if (nombreVuelo == null || nombreVuelo.trim().isEmpty()) {
                out.print("{\"success\": false, \"error\": \"El nombre del vuelo es obligatorio\"}");
                return;
            }
            if (nombreRuta == null || nombreRuta.trim().isEmpty()) {
                out.print("{\"success\": false, \"error\": \"La ruta es obligatoria\"}");
                return;
            }

            // Comprobar duplicado por nombre en esta aerolínea (si existe API)
            try {
                java.lang.reflect.Method m = sistema.getClass().getMethod("listarVuelosPorAerolinea", String.class);
                Object listado = m.invoke(sistema, nombreAerolinea);
                if (listado instanceof java.util.List) {
                    for (Object v : (java.util.List) listado) {
                        try {
                            java.lang.reflect.Method gname = v.getClass().getMethod("getNombre");
                            Object val = gname.invoke(v);
                            if (val != null && String.valueOf(val).equalsIgnoreCase(nombreVuelo)) {
                                out.print("{\"success\": false, \"error\": \"Ya existe un vuelo con ese nombre para la aerolínea\"}");
                                return;
                            }
                        } catch (NoSuchMethodException ignore) {}
                    }
                }
            } catch (NoSuchMethodException ignore) {
                // no hay método, continuar
            }

            // Procesar imagen (parte 'imagenVuelo' o dataURL) si viene
            String imagenUrl = "";
            Part imagePart = null;
            try { imagePart = request.getPart("imagenVuelo"); } catch (Exception ignored) { imagePart = null; }
            if (imagePart == null) {
                try { imagePart = request.getPart("imagen"); } catch (Exception ignored) { imagePart = null; }
            }

            if (imagePart != null && imagePart.getSize() > 0) {
                String submitted = "";
                try { submitted = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString(); } catch (Exception ignored) {}
                String ext = "";
                int idx = submitted.lastIndexOf('.');
                if (idx > 0) ext = submitted.substring(idx + 1).toLowerCase();
                if (ext.equals("jpeg")) ext = "jpg";
                if (ext.contains("+xml")) ext = "svg";
                if (ext.isBlank()) ext = "jpg";

                byte[] bytes;
                try (InputStream is = imagePart.getInputStream()) { bytes = is.readAllBytes(); }
                try {
                    // usar el nombre del vuelo como prefijo seguro (omitimos caracteres no válidos)
                    String saved = saveBytesAsImage(bytes, ext, request, nombreVuelo);
                    imagenUrl = saved != null ? saved : ""; // guardamos solo el filename
                    System.out.println("AltaVueloServlet: Imagen guardada (multipart) -> " + imagenUrl);
                } catch (Exception e) {
                    System.err.println("AltaVueloServlet: fallo guardando multipart image: " + e.getMessage());
                    imagenUrl = "";
                }
            } else {
                String dataUrl = request.getParameter("imagenVuelo");
                if (dataUrl == null || dataUrl.trim().isEmpty()) dataUrl = request.getParameter("imagen");
                if (dataUrl == null || dataUrl.trim().isEmpty()) dataUrl = request.getParameter("imagenUrl");

                if (dataUrl != null && dataUrl.startsWith("data:")) {
                    try {
                        String saved = saveImageFromDataUrl(dataUrl, request, nombreVuelo);
                        imagenUrl = saved != null ? saved : "";
                        System.out.println("AltaVueloServlet: Imagen guardada (dataURL) -> " + imagenUrl);
                    } catch (Exception ex) {
                        System.err.println("AltaVueloServlet: fallo guardando dataURL: " + ex.getMessage());
                        imagenUrl = "";
                    }
                } else {
                    imagenUrl = "";
                }
            }

            // Obtener aerolínea (DTO) desde la lógica
            DtAerolinea aerolinea = sistema.obtenerAerolinea(nombreAerolinea);
            if (aerolinea == null) {
                out.print("{\"success\": false, \"error\": \"Aerolínea no encontrada\"}");
                return;
            }

            // Llamada a la lógica de negocio
            boolean altaVueloOk = false;
            try {
                java.lang.reflect.Method[] methods = sistema.getClass().getMethods();
                boolean invoked = false;
                for (java.lang.reflect.Method m : methods) {
                    if (!m.getName().equalsIgnoreCase("altaVuelo")) continue;
                    Class<?>[] pts = m.getParameterTypes();
                    try {
                        if (pts.length == 9) {
                            Object[] args = new Object[9];
                            args[0] = nombreVuelo;
                            if (pts[1] == String.class) args[1] = nombreAerolinea; else args[1] = aerolinea;
                            args[2] = nombreRuta;
                            if (pts[3] == LocalDate.class) args[3] = fecha;
                            if (pts[4] == int.class || pts[4] == Integer.class) args[4] = duracion;
                            if (pts[5] == int.class || pts[5] == Integer.class) args[5] = asientosTurista;
                            if (pts[6] == int.class || pts[6] == Integer.class) args[6] = asientosEjecutivo;
                            if (pts[7] == LocalDate.class) args[7] = fechaAlta;
                            if (pts[8] == String.class) args[8] = imagenUrl; else args[8] = imagenUrl;
                            m.invoke(sistema, args);
                            invoked = true;
                            altaVueloOk = true;
                            break;
                        }
                        if (pts.length == 8) {
                            Object[] args = new Object[]{nombreVuelo, nombreAerolinea, nombreRuta, fecha, duracion, asientosTurista, asientosEjecutivo, fechaAlta};
                            m.invoke(sistema, args);
                            invoked = true;
                            altaVueloOk = true;
                            break;
                        }
                    } catch (Exception invokeEx) {
                        // si es un InvocationTargetException, extraer la causa real
                        Throwable cause = invokeEx;
                        if (invokeEx instanceof java.lang.reflect.InvocationTargetException && invokeEx.getCause() != null) {
                            cause = invokeEx.getCause();
                        }
                        System.out.println("AltaVueloServlet: fallo al invocar altaVuelo -> " + cause);

                        String msg = (cause.getMessage() != null) ? cause.getMessage() : "Error al crear el vuelo";
                        String msgLower = msg.toLowerCase();

                        // Si detectamos que es un error de negocio (ruta finalizada o vuelo duplicado)
                        if (msgLower.contains("finalizada") || msgLower.contains("finalizado") ||
                                msgLower.contains("ya existe") || msgLower.contains("existe un vuelo")) {
                            // Error de negocio controlado -> mantener HTTP 200 para que el JS pueda leer el JSON
                            out.print("{\"success\": false, \"error\": \"" + msg.replace("\"", "\\\"") + "\"}");
                            return; // no continuar ni devolver éxito
                        } else {
                            // Otro error de negocio o técnico -> 400 con mensaje devuelto en JSON
                            out.print("{\"success\": false, \"error\": \"" + msg.replace("\"", "\\\"") + "\"}");
                            return;
                        }
                    }
                }
                if (!invoked) {
                    out.print("{\"success\": false, \"error\": \"No se pudo invocar altaVuelo en la lógica (firma no encontrada)\"}");
                    return;
                }
            } catch (Throwable e) {
                // Cualquier otro error inesperado al preparar o buscar el método
                Throwable cause = e instanceof java.lang.reflect.InvocationTargetException && e.getCause() != null
                        ? e.getCause() : e;
                System.out.println("AltaVueloServlet: error inesperado invocando altaVuelo -> " + cause);
                String msg = (cause.getMessage() != null) ? cause.getMessage() : "Error interno al crear el vuelo";

                out.print("{\"success\": false, \"error\": \"" + msg.replace("\"", "\\\"") + "\"}");
                return;
            }

            // Si por alguna razón no se marcó como OK, no seguir y devolver error genérico
            if (!altaVueloOk) {
                out.print("{\"success\": false, \"error\": \"No se pudo completar el alta del vuelo\"}");
                return;
            }

            // Intentar persistir la URL de la imagen en la lógica de negocio si existe
            boolean imagenPersistida = false;
            String persistenceDebug = "";
            if (imagenUrl != null && !imagenUrl.isEmpty()) {
                try {
                    java.lang.reflect.Method[] methods = sistema.getClass().getMethods();
                    for (java.lang.reflect.Method m : methods) {
                        String mname = m.getName().toLowerCase();
                        if (mname.contains("imagen") || mname.contains("vuelo") || mname.contains("setimagen") || mname.contains("setimagenurl") || mname.contains("agregarimagen")) {
                            Class<?>[] pts = m.getParameterTypes();
                            try {
                                if (pts.length == 2 && pts[0] == String.class && pts[1] == String.class) {
                                    m.invoke(sistema, nombreVuelo, imagenUrl);
                                    imagenPersistida = true; persistenceDebug = "invoked " + m.getName(); break;
                                }
                                if (pts.length == 2 && !pts[0].isPrimitive() && pts[1] == String.class) {
                                    try {
                                        java.lang.reflect.Method getter = null;
                                        for (java.lang.reflect.Method gm : methods) {
                                            String gmn = gm.getName().toLowerCase();
                                            if (gmn.contains("ver") && gmn.contains("vuelo")) { getter = gm; break; }
                                            if (gmn.contains("get") && gmn.contains("vuelo")) { getter = gm; break; }
                                        }
                                        if (getter != null) {
                                            Object vueloDto = null;
                                            try { vueloDto = getter.invoke(sistema, nombreVuelo); } catch (IllegalArgumentException ia) {
                                                try { vueloDto = getter.invoke(sistema); } catch (Exception ex) { vueloDto = null; }
                                            }
                                            if (vueloDto != null) {
                                                m.invoke(sistema, vueloDto, imagenUrl);
                                                imagenPersistida = true; persistenceDebug = "invoked " + m.getName() + " via " + getter.getName(); break;
                                            }
                                        }
                                    } catch (Exception ex2) {}
                                }
                            } catch (Exception invokeEx) {
                                System.out.println("AltaVueloServlet: fallo al invocar " + m.getName() + " -> " + invokeEx.getMessage());
                            }
                        }
                    }
                    if (!imagenPersistida) persistenceDebug = "no suitable method found or all invocations failed";
                } catch (Throwable e) {
                    System.out.println("AltaVueloServlet: error buscando métodos para persistir imagen -> " + e.getMessage());
                    persistenceDebug = "error: " + e.getMessage();
                }
            } else {
                persistenceDebug = "no imagenUrl to persist";
            }

            String jsonResp = "{\"success\": true, \"imagenUrl\":\"" + imagenUrl.replace("\"","\\\"") + "\", \"imagenPersistida\": " + imagenPersistida + ", \"persistenceDebug\": \"" + persistenceDebug.replace("\"","\\\"") + "\"}";
            out.print(jsonResp);
        } catch (Exception e) {
            out.print("{\"success\": false, \"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    private static String getParam(HttpServletRequest req, String name) throws IOException {
        String v = req.getParameter(name);
        if (v != null) return v;
        Part p = null;
        try { p = req.getPart(name); } catch (Exception ignored) {}
        if (p != null && p.getSize() > 0) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                return sb.toString();
            }
        }
        return "";
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    // Helpers similares a los de AltaRutaServlet/AltaUsuarioServlet
    private String saveImageFromDataUrl(String dataUrl, HttpServletRequest request, String prefix) throws IOException {
        if (dataUrl == null || !dataUrl.startsWith("data:")) return null;
        int comma = dataUrl.indexOf(',');
        if (comma < 0) return null;
        String meta = dataUrl.substring(5, comma);
        String base64 = dataUrl.substring(comma + 1);
        if (!meta.contains("base64")) return null;
        String mime = meta.split(";")[0];
        String ext = "bin";
        if (mime != null && mime.startsWith("image/")) {
            ext = mime.substring("image/".length());
            if (ext.equalsIgnoreCase("jpeg")) ext = "jpg";
            if (ext.contains("+xml")) ext = "svg";
        }
        byte[] bytes;
        try { bytes = Base64.getDecoder().decode(base64); } catch (IllegalArgumentException iae) { throw new IOException("Base64 inválido", iae); }
        return saveBytesAsImage(bytes, ext, request, prefix);
    }

    private String saveBytesAsImage(byte[] bytes, String ext, HttpServletRequest request, String prefix) throws IOException {
        if (ext == null || ext.isBlank()) ext = "bin";
        String imagesDirPath = null;
        try { imagesDirPath = (String) request.getServletContext().getAttribute("IMAGES_DIR"); } catch (Exception ignore) {}
        if (imagesDirPath == null) imagesDirPath = request.getServletContext().getRealPath("/Images");
        if (imagesDirPath == null) imagesDirPath = System.getProperty("java.io.tmpdir") + File.separator + "WebAppApache_Images";
        File imagesDir = new File(imagesDirPath);
        if (!imagesDir.exists()) imagesDir.mkdirs();
        try { request.getServletContext().setAttribute("IMAGES_DIR", imagesDir.getAbsolutePath()); } catch (Exception ignore) {}

        String safePrefix = (prefix == null || prefix.isBlank()) ? "vuelo" : prefix.replaceAll("[^a-zA-Z0-9_-]", "_");
        String filename = safePrefix + "_" + System.currentTimeMillis() + "." + ext;
        File outFile = new File(imagesDir, filename);
        try (FileOutputStream fos = new FileOutputStream(outFile)) { fos.write(bytes); }
        System.out.println("AltaVueloServlet: Imagen guardada: " + outFile.getAbsolutePath() + " -> stored filename: " + filename + " (para servirla: <contextPath>/Images/" + filename + ")");
        return filename;
    }
}

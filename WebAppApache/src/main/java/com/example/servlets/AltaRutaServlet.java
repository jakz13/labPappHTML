// java
package com.example.servlets;

import DataTypes.DtAerolinea;
import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtRutaVuelo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@WebServlet("/altaRuta")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 5 * 1024 * 1024,           // 5MB
        maxRequestSize = 10 * 1024 * 1024)       // 10MB
public class AltaRutaServlet extends HttpServlet {
    private static final String IMAGES_DIR = "/Images";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
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
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\": false, \"error\": \"Usuario no autorizado: debe iniciar sesión como aerolínea\"}");
                return;
            }

            // Leer params (funciona con multipart/FormData)
            String nombre = getParam(request, "nombre");
            if (nombre.trim().isEmpty()) {
                // aceptar también nombreRuta para compatibilidad con el form
                nombre = getParam(request, "nombreRuta");
            }
            String descripcionCorta = getParam(request, "descripcion");
            String descripcion = getParam(request, "descripcionDetallada");
            String ciudadOrigen = getParam(request, "origen");
            String ciudadDestino = getParam(request, "destino");
            String hora = getParam(request, "hora");
            double costoTurista = parseDoubleSafe(getParam(request, "costoTurista"));
            double costoEjecutivo = parseDoubleSafe(getParam(request, "costoEjecutivo"));
            double costoEquipajeExtra = parseDoubleSafe(getParam(request, "costoEquipaje"));
            String[] categorias = request.getParameterValues("categorias");
            LocalDate fechaAlta = LocalDate.now();

            // Debug: imprimir content-type y partes del request para verificar multipart
            try {
                System.out.println("AltaRutaServlet: Content-Type=" + request.getContentType());
                try {
                    for (Part p : request.getParts()) {
                        System.out.println("AltaRutaServlet: part -> name='" + p.getName() + "' size=" + p.getSize() + " submittedFileName='" + p.getSubmittedFileName() + "'");
                    }
                } catch (Exception e) {
                    System.out.println("AltaRutaServlet: no se pudieron listar las partes: " + e.getMessage());
                }
            } catch (Exception ignore) {}

            // Validaciones básicas
            if (nombre.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"El nombre de la ruta es obligatorio\"}");
                return;
            }
            if (ciudadOrigen.trim().isEmpty() || ciudadDestino.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"Origen y destino son obligatorios\"}");
                return;
            }
            if (ciudadOrigen.trim().equalsIgnoreCase(ciudadDestino.trim())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"La ciudad de origen y destino no pueden ser la misma\"}");
                return;
            }
            if (costoTurista < 0 || costoEjecutivo < 0 || costoEquipajeExtra < 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"Los costos no pueden ser negativos\"}");
                return;
            }
            if (categorias == null || categorias.length == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"Debe seleccionar al menos una categoría\"}");
                return;
            }

            // Comprobar duplicado por nombre en esta aerolínea
            List<DtRutaVuelo> rutasExistentes = sistema.listarRutasPorAerolinea(nombreAerolinea);
            if (rutasExistentes != null) {
                for (DtRutaVuelo r : rutasExistentes) {
                    if (r.getNombre() != null && r.getNombre().equalsIgnoreCase(nombre)) {
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        out.print("{\"success\": false, \"error\": \"Ya existe una ruta con ese nombre para la aerolínea\"}");
                        return;
                    }
                }
            }

            // Procesar imagen (parte 'imagenRuta' o dataURL) si viene
            String imagenUrl = "";
            // Preferir multipart parts 'imagenRuta' o 'imagen' (si hay) y delegar en helpers para guardar con la misma convención que AltaUsuarioServlet
            Part imagePart = null;
            try { imagePart = request.getPart("imagenRuta"); } catch (Exception ignored) { imagePart = null; }
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

                try {
                    byte[] bytes = null;
                    try (InputStream is = imagePart.getInputStream()) { bytes = is.readAllBytes(); }
                    // usar el nombre de la ruta como prefijo seguro
                    String saved = saveBytesAsImage(bytes, ext, request, nombre);
                    imagenUrl = saved; // guardamos solo el filename, igual que AltaUsuarioServlet
                    System.out.println("AltaRutaServlet: Imagen guardada (multipart) -> " + imagenUrl);
                } catch (Exception e) {
                    System.err.println("AltaRutaServlet: fallo guardando multipart image: " + e.getMessage());
                    imagenUrl = "";
                }
            } else {
                // fallback: parámetros que puedan contener dataURL
                String dataUrl = request.getParameter("imagenRuta");
                if (dataUrl == null || dataUrl.trim().isEmpty()) dataUrl = request.getParameter("imagen");
                if (dataUrl == null || dataUrl.trim().isEmpty()) dataUrl = request.getParameter("imagenUrl");

                if (dataUrl != null && dataUrl.startsWith("data:")) {
                    try {
                        String saved = saveImageFromDataUrl(dataUrl, request, nombre);
                        imagenUrl = saved;
                        System.out.println("AltaRutaServlet: Imagen guardada (dataURL) -> " + imagenUrl);
                    } catch (Exception ex) {
                        System.err.println("AltaRutaServlet: fallo guardando dataURL: " + ex.getMessage());
                        imagenUrl = "";
                    }
                } else {
                    imagenUrl = "";
                }
            }

            String videoUrl = request.getParameter("videoUrl");
            if (videoUrl != null) videoUrl = videoUrl.trim();
            if (videoUrl != null && videoUrl.isEmpty()) videoUrl = null;

            // Obtener aerolínea (DTO) desde la lógica
            DtAerolinea aerolinea = sistema.obtenerAerolinea(nombreAerolinea);
            if (aerolinea == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"Aerolínea no encontrada\"}");
                return;
            }
            System.out.println("AltaRutaServlet: Datos a persistir -");
            System.out.println("  Nombre: " + nombre);
            System.out.println("  Video URL: " + videoUrl);
            System.out.println("  Imagen URL: " + imagenUrl);
            System.out.println("  Aerolínea: " + aerolinea.getNombre());

            // Normalizar imagen: si vino como URL absoluta o ruta, extraer sólo el filename
            if (imagenUrl != null && !imagenUrl.isBlank()) {
                try {
                    String onlyFile = extractFilenameFromAnyPath(imagenUrl);
                    System.out.println("AltaRutaServlet: Normalizando imagenUrl '" + imagenUrl + "' -> '" + onlyFile + "'");

                    // Asegurar que el filename siga la convención: <ruta_saneada>_<timestamp>.<ext>
                    String safePrefix = (nombre == null || nombre.isBlank()) ? "ruta" : nombre.replaceAll("[^a-zA-Z0-9_-]", "_");
                    boolean matchesPrefix = onlyFile != null && onlyFile.startsWith(safePrefix + "_");

                    if (!matchesPrefix) {
                        // Intentar renombrar/mover el fichero existente dentro de Images al nuevo nombre
                        String imagesDirPath = null;
                        try { imagesDirPath = (String) request.getServletContext().getAttribute("IMAGES_DIR"); } catch (Exception ignore) {}
                        if (imagesDirPath == null) imagesDirPath = request.getServletContext().getRealPath(IMAGES_DIR);
                        if (imagesDirPath == null) imagesDirPath = System.getProperty("java.io.tmpdir") + File.separator + "WebAppApache_Images";

                        File imagesDir = new File(imagesDirPath);
                        if (!imagesDir.exists()) imagesDir.mkdirs();

                        File oldFile = new File(imagesDir, onlyFile);
                        String newName;
                        int dot = onlyFile.lastIndexOf('.');
                        String ext = (dot > 0) ? onlyFile.substring(dot + 1) : "jpg";
                        newName = safePrefix + "_" + System.currentTimeMillis() + "." + ext;

                        if (oldFile.exists()) {
                          File newFile = new File(imagesDir, newName);
                          try {
                            boolean moved = oldFile.renameTo(newFile);
                            if (moved) {
                              System.out.println("AltaRutaServlet: Renombrado '" + onlyFile + "' -> '" + newName + "' para ajustar convención");
                              onlyFile = newName;
                            } else {
                              System.out.println("AltaRutaServlet: No se pudo renombrar '" + onlyFile + "' -> se generará nombre nuevo: '" + newName + "'");
                              onlyFile = newName;
                            }
                          } catch (SecurityException se) {
                            System.out.println("AltaRutaServlet: permiso denegado renombrando imagen: " + se.getMessage());
                            onlyFile = newName;
                          }
                        } else {
                          // Si no existe el fichero físico, simplemente construir un nombre con la convención
                          System.out.println("AltaRutaServlet: fichero de imagen no encontrado en Images, se generará nombre con convención: '" + newName + "'");
                          onlyFile = newName;
                        }
                    }

                    imagenUrl = onlyFile;
                } catch (Throwable ignore) { /* no bloquear la operación por esto */ }
            }

            // Llamada a la lógica de negocio
            sistema.altaRutaVuelo(
                    nombre, descripcion, descripcionCorta, aerolinea, ciudadOrigen, ciudadDestino, hora,
                    fechaAlta, costoTurista, costoEjecutivo, costoEquipajeExtra, categorias, imagenUrl, videoUrl
            );

            System.out.println("AltaRutaServlet: Video URL guardada: " + videoUrl);

            // Intentar persistir la URL de la imagen en la lógica de negocio (si existe alguna API)
            boolean imagenPersistida = false;
            String persistenceDebug = "";
            if (imagenUrl != null && !imagenUrl.isEmpty()) {
                try {
                    Class<?> sysClass = sistema.getClass();
                    java.lang.reflect.Method[] methods = sysClass.getMethods();
                    for (java.lang.reflect.Method m : methods) {
                        String mname = m.getName().toLowerCase();
                        // heurística: métodos que manejen imagenes o rutas
                        if (mname.contains("imagen") || mname.contains("ruta") || mname.contains("setimagen") || mname.contains("setimagenurl") || mname.contains("agregarimagen")) {
                            Class<?>[] pts = m.getParameterTypes();
                            try {
                                // intentar (String nombre, String imagenUrl)
                                if (pts.length == 2 && pts[0] == String.class && pts[1] == String.class) {
                                    m.invoke(sistema, nombre, imagenUrl);
                                    imagenPersistida = true;
                                    persistenceDebug = "invoked " + m.getName() + "(nombre, imagenUrl)";
                                    break;
                                }
                                // intentar (String imagenUrl, String nombre)
                                if (pts.length == 2 && pts[0] == String.class && pts[1] == String.class) {
                                    // already covered
                                }
                                // intentar (Object rutaDTO, String url) usando verInfoRuta
                                if (pts.length == 2 && !pts[0].isPrimitive() && pts[1] == String.class) {
                                    // intentar obtener DTO de ruta
                                    try {
                                        java.lang.reflect.Method getter = null;
                                        for (java.lang.reflect.Method gm : methods) {
                                            String gmn = gm.getName().toLowerCase();
                                            if (gmn.contains("ver") && gmn.contains("ruta")) { getter = gm; break; }
                                            if (gmn.contains("get") && gmn.contains("ruta")) { getter = gm; break; }
                                        }
                                        if (getter != null) {
                                            Object rutaDto = null;
                                            try { rutaDto = getter.invoke(sistema, nombre); } catch (IllegalArgumentException ia) {
                                                // quizás getter solo toma otro tipo; intentar sin args
                                                try { rutaDto = getter.invoke(sistema); } catch (Exception ex) { rutaDto = null; }
                                            }
                                            if (rutaDto != null) {
                                                m.invoke(sistema, rutaDto, imagenUrl);
                                                imagenPersistida = true;
                                                persistenceDebug = "invoked " + m.getName() + "(rutaDto, imagenUrl) via " + getter.getName();
                                                break;
                                            }
                                        }
                                    } catch (Exception ex2) {
                                        // ignorar
                                    }
                                }
                            } catch (Exception invokeEx) {
                                // no detenerse; registrar debug
                                System.out.println("AltaRutaServlet: fallo al invocar " + m.getName() + " -> " + invokeEx.getMessage());
                            }
                        }
                    }
                    if (!imagenPersistida) persistenceDebug = "no suitable method found or all invocations failed";
                } catch (Throwable e) {
                    System.out.println("AltaRutaServlet: error buscando métodos para persistir imagen -> " + e.getMessage());
                    persistenceDebug = "error: " + e.getMessage();
                }
            } else {
                persistenceDebug = "no imagenUrl to persist";
            }

            // Devolver éxito e imagenUrl (si se guardó)
            // incluir debug opcional sobre persistencia de imagen
            // Asegurar que se devuelva SOLO el filename
            try {
                imagenUrl = extractFilenameFromAnyPath(imagenUrl);
            } catch (Throwable ignore) {}
            System.out.println("AltaRutaServlet: Respondiendo imagenUrl(final)='" + imagenUrl + "'");
            String jsonResp = "{\"success\": true, \"imagenUrl\":\"" + (imagenUrl != null ? imagenUrl.replace("\"","\\\"") : "") + "\", \"imagenPersistida\": " + imagenPersistida + ", \"persistenceDebug\": \"" + persistenceDebug.replace("\"","\\\"") + "\"}";
            out.print(jsonResp);
        } catch (Exception e) {
            System.err.println("AltaRutaServlet error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"error\": \"" + e.getMessage().replace("\"","\\\"") + "\"}");
        }
    }

    private static String getParam(HttpServletRequest req, String name) throws IOException {
        String v = req.getParameter(name);
        if (v != null) return v;
        Part p = null;
        try {
            p = req.getPart(name);
        } catch (Exception ignored) {}
        if (p != null && p.getSize() > 0) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                return sb.toString();
            }
        }
        return "";
    }

    private static double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; }
    }

    private static String getSubmittedFileNameSafe(Part part) {
        try {
            for (String cd : part.getHeader("content-disposition").split(";")) {
                if (cd.trim().startsWith("filename")) {
                    String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                    return Paths.get(filename).getFileName().toString();
                }
            }
        } catch (Exception ignored) {}
        return "";
    }

    private static String extensionFromFilenameOrContentType(String filename, String contentType) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf('.')).toLowerCase();
        }
        if (contentType != null) {
            int slash = contentType.indexOf('/');
            if (slash >= 0) {
                String sub = contentType.substring(slash + 1).toLowerCase();
                if (sub.equals("jpeg")) sub = "jpg";
                return "." + sub;
            }
        }
        return ".jpg";
    }

    // Helpers clonados/adaptados desde AltaUsuarioServlet para mantener la misma convención de guardado
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

        String safePrefix = (prefix == null || prefix.isBlank()) ? "ruta" : prefix.replaceAll("[^a-zA-Z0-9_-]", "_");
        String filename = safePrefix + "_" + System.currentTimeMillis() + "." + ext;
        File outFile = new File(imagesDir, filename);
        try (FileOutputStream fos = new FileOutputStream(outFile)) { fos.write(bytes); }
        System.out.println("AltaRutaServlet: Imagen guardada: " + outFile.getAbsolutePath() + " -> stored filename: " + filename + " (para servirla: <contextPath>/Images/" + filename + ")");
        return filename;
    }

    // Helper: extraer sólo el nombre de fichero de una URL o ruta (maneja http://, /Images/..., rutas físicas, y filenames directos)
    private String extractFilenameFromAnyPath(String stored) {
        if (stored == null) return "";
        stored = stored.trim();
        if (stored.isEmpty()) return "";
        // quitar parámetros de query si existen
        int q = stored.indexOf('?');
        if (q >= 0) stored = stored.substring(0, q);
        // buscar el último separador '/'
        int idx = stored.lastIndexOf('/');
        if (idx >= 0 && idx < stored.length() - 1) return stored.substring(idx + 1);
        // si no hay '/', devolver tal cual
        return stored;
    }
}

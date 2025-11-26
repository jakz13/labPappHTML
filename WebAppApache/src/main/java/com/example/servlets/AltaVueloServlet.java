package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Base64;

import serviciosweb.JuanViajesWS;
import serviciosweb.DtAerolinea;
import serviciosweb.DtVuelo;
import com.example.util.PortUtils;

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
            JuanViajesWS port = PortUtils.getPort(request);


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
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"El nombre del vuelo es obligatorio\"}");
                return;
            }
            if (nombreRuta == null || nombreRuta.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"La ruta es obligatoria\"}");
                return;
            }

            // Comprobar duplicado por nombre en esta aerolínea usando el port
            try {
                java.util.List<serviciosweb.DtVuelo> listado = port.listarVuelosPorRuta(nombreRuta);
                if (listado != null) {
                    for (serviciosweb.DtVuelo v : listado) {
                        try {
                            if (v != null && v.getNombre() != null && v.getNombre().equalsIgnoreCase(nombreVuelo)
                                    && (v.getNombreAerolinea() == null ? nombreAerolinea == null : v.getNombreAerolinea().equalsIgnoreCase(nombreAerolinea))) {
                                response.setStatus(HttpServletResponse.SC_CONFLICT);
                                out.print("{\"success\": false, \"error\": \"Ya existe un vuelo con ese nombre para la aerolínea\"}");
                                return;
                            }
                        } catch (Exception ignore) {}
                    }
                }
            } catch (Exception ignore) {
                // si falla la comprobación, continuar y dejar que el alta falle si es duplicado
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

            // Obtener aerolínea (DTO) desde el port
            DtAerolinea aerolinea = port.obtenerAerolinea(nombreAerolinea);
            if (aerolinea == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"Aerolínea no encontrada\"}");
                return;
            }

            // Llamada al web service altaVuelo
            try {
                port.altaVuelo(nombreVuelo, nombreAerolinea, nombreRuta, (fechaStr == null ? "" : fechaStr), duracion, asientosTurista, asientosEjecutivo, (fechaAlta == null ? "" : fechaAlta.toString()), imagenUrl);
            } catch (Exception svcEx) {
                System.out.println("AltaVueloServlet: error invocando port.altaVuelo -> " + svcEx.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"error\": \"No se pudo crear el vuelo\"}");
                return;
            }

            // Intentar persistir la URL de la imagen en el servicio si existe
            boolean imagenPersistida = false;
            String persistenceDebug = "";
            if (imagenUrl != null && !imagenUrl.isEmpty()) {
                try {
                    // intentar métodos del port para setear imagen (si existen) - muchos servicios no tienen esto, así que se ignora si falla
                    try {
                        // si existe un método para modificar vuelo completo
                        port.modificarDatosAerolineaCompleto(nombreAerolinea, aerolinea.getNombre(), aerolinea.getDescripcion(), aerolinea.getSitioWeb(), "", aerolinea.getImagenUrl());
                        // no realmente relacionado pero intento por heurística; ignorar errores
                    } catch (Exception ignore) {}
                } catch (Throwable e) {
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

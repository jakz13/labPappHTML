package com.example.servlets;

import logica.Sistema;
import logica.TipoDoc;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import DataTypes.DtCliente;
import DataTypes.DtAerolinea;

@MultipartConfig
@WebServlet("/altaUsuario")
public class AltaUsuarioServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String nickname = request.getParameter("nickname");
            String nombre = request.getParameter("nombre");
            String email = request.getParameter("email");
            String password = request.getParameter("password"); // puede venir o no
            String tipoUsuario = request.getParameter("tipoUsuario");

            // Manejo de imagen: primero multipart (nombres 'imagen' o 'imagenPerfil'), si no existe intentar parámetro base64 (data URL)
            String imagenPerfilUrl = "";
            Part imagePart = null;
            try {
                imagePart = request.getPart("imagen");
            } catch (IllegalStateException | ServletException ignored) {
                imagePart = null;
            }
            if (imagePart == null) {
                try { imagePart = request.getPart("imagenPerfil"); } catch (Exception ignored) { imagePart = null; }
            }

            if (imagePart != null && imagePart.getSize() > 0) {
                // Extraer extensión del nombre original y normalizar
                String submitted = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
                String ext = "";
                int idx = submitted.lastIndexOf('.');
                if (idx > 0) ext = submitted.substring(idx + 1).toLowerCase(); // sin punto
                if (ext.equals("jpeg")) ext = "jpg";
                if (ext.contains("+xml")) ext = "svg";
                if (ext.isBlank()) ext = "jpg";

                // Leer bytes del Part y delegar en helper que usa la misma convención que ActualizarUsuarioServlet
                byte[] bytes;
                try (InputStream is = imagePart.getInputStream()) {
                    bytes = is.readAllBytes();
                }

                try {
                    String saved = saveBytesAsImage(bytes, ext, request, nickname);
                    imagenPerfilUrl = saved != null ? saved : "";
                    System.out.println("AltaUsuarioServlet: Imagen guardada (multipart) -> " + imagenPerfilUrl);
                } catch (Exception e) {
                    System.err.println("AltaUsuarioServlet: fallo guardando multipart image: " + e.getMessage());
                    imagenPerfilUrl = "";
                }

            } else {
                // Fallback: verificar parámetro con data URL (imagen en base64) — útil si el frontend envía dataURL
                String dataUrl = request.getParameter("imagen");
                if (dataUrl == null || dataUrl.trim().isEmpty()) {
                    dataUrl = request.getParameter("imagenPerfil");
                }
                if (dataUrl == null || dataUrl.trim().isEmpty()) {
                    dataUrl = request.getParameter("imagenUrl"); // otro nombre posible
                }
                if (dataUrl != null && dataUrl.startsWith("data:")) {
                    try {
                        // Usar la misma implementación que ActualizarUsuarioServlet para guardar data URLs
                        // (nombra con nickname_timestamp.ext y guarda en Images/)
                        String saved = saveImageFromDataUrl(dataUrl, request, nickname);
                        System.out.println("AltaUsuarioServlet: Imagen guardada (dataURL): " + saved);
                        imagenPerfilUrl = saved != null ? saved : "";
                    } catch (IllegalArgumentException | IOException iae) {
                        imagenPerfilUrl = "";
                    }
                } else {
                    imagenPerfilUrl = "";
                }
            }

            Sistema sistema = new Sistema();

            if ("cliente".equals(tipoUsuario)) {
                String apellido = request.getParameter("apellido");
                String fechaNacimiento = request.getParameter("fechaNacimiento");
                String nacionalidad = request.getParameter("nacionalidad");
                String tipoDocumento = request.getParameter("tipoDocumento");
                String numeroDocumento = request.getParameter("numeroDocumento");
                String Password = request.getParameter("password");

                // Validar campos obligatorios
                if (nickname == null || nombre == null || email == null || apellido == null ||
                        fechaNacimiento == null || nacionalidad == null ||
                        tipoDocumento == null || numeroDocumento == null) {
                    out.print("{\"success\": false, \"error\": \"Faltan datos obligatorios\"}");
                    return;
                }

                LocalDate fechaNac;
                try {
                    fechaNac = LocalDate.parse(fechaNacimiento);
                } catch (DateTimeParseException e) {
                    out.print("{\"success\": false, \"error\": \"Fecha de nacimiento inválida\"}");
                    return;
                }

                TipoDoc tipoDoc;
                try {
                    tipoDoc = TipoDoc.valueOf(tipoDocumento.toUpperCase());
                } catch (Exception e) {
                    out.print("{\"success\": false, \"error\": \"Tipo de documento inválido\"}");
                    return;
                }

                // Alta cliente: pasar imagenPerfilUrl (puede ser "")
                System.out.println("AltaUsuarioServlet: antes de altaCliente -> imagenPerfilUrl='" + imagenPerfilUrl + "'");
                sistema.altaCliente(nickname, nombre, apellido, email, fechaNac, nacionalidad, tipoDoc, numeroDocumento, Password, imagenPerfilUrl);
                // Verificar inmediatamente en la lógica si el valor quedó guardado
                try {
                    DtCliente dtc = sistema.obtenerCliente(nickname);
                    String stored = "<null>";
                    try { stored = dtc.getImagenUrl() != null ? dtc.getImagenUrl() : "<null>"; } catch (Throwable ignore) {}
                    System.out.println("AltaUsuarioServlet: post-altaCliente -> sistema.obtenerCliente('" + nickname + "').imagenUrl='" + stored + "'");
                } catch (Throwable t) {
                    System.err.println("AltaUsuarioServlet: no se pudo verificar post-altaCliente: " + t.getMessage());
                }

                // Crear sesión automática tras registro
                try {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("usuario", nickname);
                    session.setAttribute("tipoUsuario", "cliente");
                } catch (Exception sessEx) {
                    System.err.println("Aviso: no se pudo crear sesión automática tras alta: " + sessEx.getMessage());
                }

                // Responder con detalles para el frontend
                out.print(new org.json.JSONObject().put("success", true).put("tipo", "cliente").put("nickname", nickname).put("imagenUrl", imagenPerfilUrl).toString());
                return;

            } else if ("aerolinea".equals(tipoUsuario)) {
                String descripcion = request.getParameter("descripcionAerolinea");
                String sitioWeb = request.getParameter("sitioWeb");
                String Password = request.getParameter("password");
                if (nickname == null || nombre == null || email == null || descripcion == null) {
                    out.print("{\"success\": false, \"error\": \"Faltan datos obligatorios\"}");
                    return;
                }

                // Alta aerolinea: pasar imagenPerfilUrl (puede ser "")
                System.out.println("AltaUsuarioServlet: antes de altaAerolinea -> imagenPerfilUrl='" + imagenPerfilUrl + "'");
                sistema.altaAerolinea(nickname, nombre, descripcion, email, sitioWeb, Password, imagenPerfilUrl);
                // Verificar inmediatamente en la lógica si el valor quedó guardado
                try {
                    DtAerolinea dta = sistema.obtenerAerolinea(nickname);
                    String storedA = "<null>";
                    try { storedA = dta.getImagenUrl() != null ? dta.getImagenUrl() : "<null>"; } catch (Throwable ignore) {}
                    System.out.println("AltaUsuarioServlet: post-altaAerolinea -> sistema.obtenerAerolinea('" + nickname + "').imagenUrl='" + storedA + "'");
                } catch (Throwable t) {
                    System.err.println("AltaUsuarioServlet: no se pudo verificar post-altaAerolinea: " + t.getMessage());
                }

                // Crear sesión automática tras registro
                try {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("usuario", nickname);
                    session.setAttribute("tipoUsuario", "aerolinea");
                } catch (Exception sessEx) {
                    System.err.println("Aviso: no se pudo crear sesión automática tras alta: " + sessEx.getMessage());
                }

                out.print(new org.json.JSONObject().put("success", true).put("tipo", "aerolinea").put("nickname", nickname).put("imagenUrl", imagenPerfilUrl).toString());
                return;

            } else {
                out.print("{\"success\": false, \"error\": \"Tipo de usuario inválido\"}");
                return;
            }

        } catch (IllegalArgumentException e) {
            out.print("{\"success\": false, \"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"error\": \"Error interno del servidor\"}");
        }
    }

    // Helpers adaptados desde ActualizarUsuarioServlet para mantener la misma convención de guardado
    private String saveImageFromDataUrl(String dataUrl, HttpServletRequest request, String nickname) throws IOException {
        int comma = dataUrl.indexOf(',');
        if (comma < 0) return null;
        String meta = dataUrl.substring(5, comma); // e.g. image/png;base64
        String base64 = dataUrl.substring(comma + 1);
        if (!meta.contains("base64")) return null;
        String mime = meta.split(";")[0]; // image/png
        String ext = "bin";
        if (mime != null && mime.startsWith("image/")) {
            ext = mime.substring("image/".length());
            if (ext.equalsIgnoreCase("jpeg")) ext = "jpg";
            if (ext.contains("+xml")) ext = "svg";
        }

        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException iae) {
            throw new IOException("Base64 inválido", iae);
        }

        return saveBytesAsImage(bytes, ext, request, nickname);
    }

    private String saveBytesAsImage(byte[] bytes, String ext, HttpServletRequest request, String nickname) throws IOException {
        if (ext == null || ext.isBlank()) ext = "bin";
        // Ubicación para guardar: carpeta Images dentro del contexto web
        String imagesDirPath = null;
        try { imagesDirPath = (String) request.getServletContext().getAttribute("IMAGES_DIR"); } catch (Exception ignore) {}
        if (imagesDirPath == null) imagesDirPath = request.getServletContext().getRealPath("/Images");
        if (imagesDirPath == null) imagesDirPath = System.getProperty("java.io.tmpdir") + File.separator + "WebAppApache_Images";
        File imagesDir = new File(imagesDirPath);
        if (!imagesDir.exists()) imagesDir.mkdirs();
        try { request.getServletContext().setAttribute("IMAGES_DIR", imagesDir.getAbsolutePath()); } catch (Exception ignore) {}

        String safeNick = (nickname == null || nickname.isBlank()) ? "user" : nickname.replaceAll("[^a-zA-Z0-9_-]", "_");
        String filename = safeNick + "_" + System.currentTimeMillis() + "." + ext;
        File outFile = new File(imagesDir, filename);
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            fos.write(bytes);
        }

        String relative = filename;
        System.out.println("Imagen guardada: " + outFile.getAbsolutePath() + " -> stored filename: " + relative + " (para servirla: <contextPath>/Images/" + filename + ")");
        return relative;
    }
}

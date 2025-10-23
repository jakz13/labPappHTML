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
import java.util.UUID;

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
                String submitted = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
                String ext = "";
                int idx = submitted.lastIndexOf('.');
                if (idx > 0) ext = submitted.substring(idx).toLowerCase();

                // validar extensión básica
                if (!ext.matches("\\.(jpg|jpeg|png|gif|webp|svg)")) {
                    // ignorar extensión no permitida, pero no abortar todo el registro
                    ext = ".jpg";
                }

                String filename = UUID.randomUUID().toString() + ext;

                // Intentar guardar en IMAGES_DIR (inicializado por CargarDatosBD) para coherencia con modificar
                String imagesDirPath = null;
                try { imagesDirPath = (String) request.getServletContext().getAttribute("IMAGES_DIR"); } catch (Exception ignored) {}
                if (imagesDirPath == null) {
                    imagesDirPath = request.getServletContext().getRealPath("/Images");
                }
                if (imagesDirPath == null) {
                    imagesDirPath = System.getProperty("java.io.tmpdir") + File.separator + "WebAppApache_Images";
                }
                File imagesDir = new File(imagesDirPath);
                if (!imagesDir.exists()) imagesDir.mkdirs();

                Path target = Paths.get(imagesDir.getAbsolutePath(), filename);
                try (InputStream is = imagePart.getInputStream()) {
                    Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
                }

                System.out.println("AltaUsuarioServlet: Imagen guardada (multipart): " + target.toAbsolutePath() + " -> URL: " + request.getContextPath() + "/Images/" + filename);
                imagenPerfilUrl = request.getContextPath() + "/Images/" + filename;
            } else {
                // Fallback: verificar parámetro con data URL (imagen en base64) — útil si el frontend envía dataURL
                String dataUrl = request.getParameter("imagen");
                if (dataUrl == null || dataUrl.trim().isEmpty()) {
                    dataUrl = request.getParameter("imagenPerfil");
                }
                if (dataUrl == null || dataUrl.trim().isEmpty()) {
                    dataUrl = request.getParameter("imagenUrl"); // otro nombre posible
                }
                if (dataUrl != null && dataUrl.startsWith("data:") && dataUrl.contains(";base64,")) {
                    try {
                        String meta = dataUrl.substring(5, dataUrl.indexOf(";base64,"));
                        String base64 = dataUrl.substring(dataUrl.indexOf(";base64,") + 8);
                        String mime = meta; // e.g. image/png
                        String ext = "";
                        int slash = mime.indexOf('/');
                        if (slash >= 0) {
                            ext = "." + mime.substring(slash + 1).toLowerCase();
                            if (ext.equals(".jpeg")) ext = ".jpg";
                            if (ext.contains("+xml")) ext = ".svg";
                        }
                        if (!ext.matches("\\.(jpg|jpeg|png|gif|webp|svg)")) {
                            ext = ".jpg";
                        }

                        byte[] imageBytes = Base64.getDecoder().decode(base64);
                        String filename = UUID.randomUUID().toString() + ext;

                        String imagesDirPath = null;
                        try { imagesDirPath = (String) request.getServletContext().getAttribute("IMAGES_DIR"); } catch (Exception ignored) {}
                        if (imagesDirPath == null) {
                            imagesDirPath = request.getServletContext().getRealPath("/Images");
                        }
                        if (imagesDirPath == null) {
                            imagesDirPath = System.getProperty("java.io.tmpdir") + File.separator + "WebAppApache_Images";
                        }
                        File imagesDir = new File(imagesDirPath);
                        if (!imagesDir.exists()) imagesDir.mkdirs();

                        Path target = Paths.get(imagesDir.getAbsolutePath(), filename);
                        Files.write(target, imageBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                        System.out.println("AltaUsuarioServlet: Imagen guardada (dataURL): " + target.toAbsolutePath() + " -> URL: " + request.getContextPath() + "/Images/" + filename);
                        imagenPerfilUrl = request.getContextPath() + "/Images/" + filename;
                    } catch (IllegalArgumentException iae) {
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
                sistema.altaCliente(nickname, nombre, apellido, email, fechaNac, nacionalidad, tipoDoc, numeroDocumento, Password, imagenPerfilUrl);

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
                sistema.altaAerolinea(nickname, nombre, descripcion, email, sitioWeb, Password, imagenPerfilUrl);

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
}

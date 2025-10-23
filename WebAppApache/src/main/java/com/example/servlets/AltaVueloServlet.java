package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtAerolinea;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.UUID;

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

            // Comprobar duplicado por nombre en esta aerolínea (si existe API)
            try {
                // si existe un método listarVuelosPorAerolinea similar a rutas, intentar usarlo
                java.lang.reflect.Method m = sistema.getClass().getMethod("listarVuelosPorAerolinea", String.class);
                Object listado = m.invoke(sistema, nombreAerolinea);
                if (listado instanceof java.util.List) {
                    for (Object v : (java.util.List) listado) {
                        try {
                            java.lang.reflect.Method gname = v.getClass().getMethod("getNombre");
                            Object val = gname.invoke(v);
                            if (val != null && String.valueOf(val).equalsIgnoreCase(nombreVuelo)) {
                                response.setStatus(HttpServletResponse.SC_CONFLICT);
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
                if (idx > 0) ext = submitted.substring(idx).toLowerCase();
                if (!ext.matches("\\.(jpg|jpeg|png|gif|webp|svg)")) {
                    ext = ".jpg";
                }

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
                try { request.getServletContext().setAttribute("IMAGES_DIR", imagesDir.getAbsolutePath()); } catch (Exception ignore) {}

                Path target = Paths.get(imagesDir.getAbsolutePath(), filename);
                try (InputStream is = imagePart.getInputStream()) {
                    Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
                }

                // Guardar la URL relativa basada en el contextPath para que quede en el formato /<context>/Images/<filename>
                String ctx = request.getContextPath();
                if (ctx == null) ctx = "";
                imagenUrl = ctx + "/Images/" + filename;
            } else {
                String dataUrl = request.getParameter("imagenVuelo");
                if (dataUrl == null || dataUrl.trim().isEmpty()) dataUrl = request.getParameter("imagen");
                if (dataUrl == null || dataUrl.trim().isEmpty()) dataUrl = request.getParameter("imagenUrl");

                if (dataUrl != null && dataUrl.startsWith("data:") && dataUrl.contains(";base64,")) {
                    try {
                        String meta = dataUrl.substring(5, dataUrl.indexOf(";base64,"));
                        String base64 = dataUrl.substring(dataUrl.indexOf(";base64,") + 8);
                        String mime = meta;
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

                        byte[] imageBytes = java.util.Base64.getDecoder().decode(base64);
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
                        try { request.getServletContext().setAttribute("IMAGES_DIR", imagesDir.getAbsolutePath()); } catch (Exception ignore) {}

                        Path target = Paths.get(imagesDir.getAbsolutePath(), filename);
                        Files.write(target, imageBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                        // Guardar la URL relativa basada en el contextPath
                        String ctx2 = request.getContextPath();
                        if (ctx2 == null) ctx2 = "";
                        imagenUrl = ctx2 + "/Images/" + filename;
                     } catch (IllegalArgumentException iae) {
                        imagenUrl = "";
                     }
                 } else {
                     imagenUrl = "";
                 }
             }

            // Obtener aerolínea (DTO) desde la lógica
            DtAerolinea aerolinea = sistema.obtenerAerolinea(nombreAerolinea);
            if (aerolinea == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"error\": \"Aerolínea no encontrada\"}");
                return;
            }

            // Llamada a la lógica de negocio
            // Intentar usar la instancia de ISistema si tiene altaVuelo con la firma esperada
            try {
                // buscar método altaVuelo que coincida con muchos posibles tipos
                java.lang.reflect.Method[] methods = sistema.getClass().getMethods();
                boolean invoked = false;
                for (java.lang.reflect.Method m : methods) {
                    if (!m.getName().equalsIgnoreCase("altaVuelo")) continue;
                    Class<?>[] pts = m.getParameterTypes();
                    try {
                        // intentar firma común con imagenUrl al final (9 parámetros)
                        if (pts.length == 9) {
                            Object[] args = new Object[9];
                            // asignar parámetros comunes donde coincida el tipo
                            args[0] = nombreVuelo;
                            // pts[1] puede ser String o DtAerolinea
                            if (pts[1] == String.class) args[1] = nombreAerolinea; else args[1] = aerolinea;
                            args[2] = nombreRuta;
                            if (pts[3] == LocalDate.class) args[3] = fecha;
                            if (pts[4] == int.class || pts[4] == Integer.class) args[4] = duracion;
                            if (pts[5] == int.class || pts[5] == Integer.class) args[5] = asientosTurista;
                            if (pts[6] == int.class || pts[6] == Integer.class) args[6] = asientosEjecutivo;
                            if (pts[7] == LocalDate.class) args[7] = fechaAlta;
                            // último parámetro comúnmente es String para imagen
                            if (pts[8] == String.class) args[8] = imagenUrl; else args[8] = imagenUrl;
                            try { m.invoke(sistema, args); invoked = true; break; } catch (Exception ex) { /* continuar intentando */ }
                        }
                        // intentar variantes antiguas sin imagen (compatibilidad)
                        if (pts.length == 8) {
                            Object[] args = new Object[]{nombreVuelo, nombreAerolinea, nombreRuta, fecha, duracion, asientosTurista, asientosEjecutivo, fechaAlta};
                            try { m.invoke(sistema, args); invoked = true; break; } catch (Exception ex) { /* continuar */ }
                        }
                    } catch (Exception invokeEx) {
                        System.out.println("AltaVueloServlet: fallo al invocar altaVuelo -> " + invokeEx.getMessage());
                    }
                }
                if (!invoked) {
                    // No se pudo encontrar una firma conocida para altaVuelo; devolver error con debug
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"success\": false, \"error\": \"No se pudo invocar altaVuelo en la lógica (firma no encontrada)\"}");
                    return;
                }
             } catch (Throwable e) {
                 System.out.println("AltaVueloServlet: error invocando altaVuelo -> " + e.getMessage());
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
                                    // intentar obtener DTO de vuelo
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
}

// java
package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import logica.TipoDoc;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.time.LocalDate;
import java.util.Base64;
import org.json.*;

@WebServlet("/api/usuario/actualizar")
public class ActualizarUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String body = readRequestBody(request);
        System.out.println("ActualizarUsuarioServlet - payload length: " + (body == null ? 0 : body.length()));

        HttpSession session = request.getSession(false);
        String nickname = null;
        String tipoUsuario = null;
        if (session != null) {
            nickname = (String) session.getAttribute("usuario");
            if (nickname == null) nickname = (String) session.getAttribute("nickname");
            tipoUsuario = (String) session.getAttribute("tipoUsuario");
            if (tipoUsuario == null) tipoUsuario = (String) session.getAttribute("tipo");
        }

        if (nickname == null || tipoUsuario == null) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, new JSONObject().put("success", false).put("error", "Usuario no autenticado"));
            return;
        }

        JSONObject obj;
        try {
            obj = new JSONObject(body == null ? "{}" : body);
        } catch (JSONException je) {
            je.printStackTrace();
            writeJson(response, HttpServletResponse.SC_BAD_REQUEST, new JSONObject().put("success", false).put("error", "JSON inválido"));
            return;
        }

        // Si frontend envía una data URL grande, guardarla en disco y reemplazar por la URL relativa
        try {
            String imagenUrl = obj.optString("imagenUrl", null);
            if (imagenUrl != null && imagenUrl.startsWith("data:")) {
                // rechazar payloads gigantes antes de decodificar
                if (imagenUrl.length() > 1_500_000) {
                    writeJson(response, HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, new JSONObject()
                            .put("success", false)
                            .put("error", "Imagen demasiado grande para procesar (usa una versión más pequeña)"));
                    return;
                }
                try {
                    String savedUrl = saveImageFromDataUrl(imagenUrl, request, nickname);
                    if (savedUrl != null) {
                        obj.put("imagenUrl", savedUrl);
                    } else {
                        // si no se pudo guardar, eliminar campo para evitar error DB
                        obj.remove("imagenUrl");
                    }
                } catch (Exception e) {
                    System.err.println("Error guardando imagen: " + e.getMessage());
                    e.printStackTrace();
                    // no fallamos completamente, simplemente eliminamos la imagen del payload
                    obj.remove("imagenUrl");
                }
            }
        } catch (Throwable tx) {
            // no queremos abortar por errores menores aquí
            System.err.println("Advertencia: no se pudo procesar imagen entrante: " + tx.getMessage());
        }

        ISistema sistema = Fabrica.getInstance().getISistema();
        // opcional: intentar cargar si tu implementación lo requiere
        try { sistema.cargarDesdeBd(); } catch (Exception e) { System.err.println("Warning: cargarDesdeBd() falló: " + e.getMessage()); }

        boolean actualizado = false;
        try {
            if ("cliente".equalsIgnoreCase(tipoUsuario)) {
                actualizado = actualizarCliente(sistema, nickname, obj);
            } else if ("aerolinea".equalsIgnoreCase(tipoUsuario) || "aerolínea".equalsIgnoreCase(tipoUsuario)) {
                actualizado = actualizarAerolinea(sistema, nickname, obj);
            } else {
                writeJson(response, HttpServletResponse.SC_BAD_REQUEST, new JSONObject().put("success", false).put("error", "Tipo de usuario desconocido"));
                return;
            }
        } catch (Throwable t) {
            // capturar excepción detallada para depuración
            t.printStackTrace();
            String trace = getStackTrace(t);
            JSONObject err = new JSONObject().put("success", false).put("error", "Excepción en servidor").put("detail", trace);
            writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, err);
            return;
        }

        if (actualizado) {
            // incluir imagenUrl (si el frontend envió una dataURL, ya fue convertida a una URL relativa y almacenada en `obj`)
            String finalImagen = obj.optString("imagenUrl", null);
            JSONObject resp = new JSONObject().put("success", true).put("message", "Datos actualizados correctamente");
            if (finalImagen != null && !finalImagen.isBlank()) resp.put("imagenUrl", finalImagen);
            writeJson(response, HttpServletResponse.SC_OK, resp);
        } else {
            writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new JSONObject().put("success", false).put("error", "No se pudieron actualizar los datos"));
        }
    }

    private String saveImageFromDataUrl(String dataUrl, HttpServletRequest request, String nickname) throws IOException {
        // data:image/png;base64,AAA...
        int comma = dataUrl.indexOf(',');
        if (comma < 0) return null;
        String meta = dataUrl.substring(5, comma); // e.g. image/png;base64
        String base64 = dataUrl.substring(comma + 1);
        if (!meta.contains("base64")) return null;
        String mime = meta.split(";")[0]; // image/png
        String ext = "bin";
        if (mime != null && mime.startsWith("image/")) {
            ext = mime.substring("image/".length());
            // normalizar extensiones comunes
            if (ext.equalsIgnoreCase("jpeg")) ext = "jpg";
            if (ext.contains("+xml")) ext = "svg"; // svg+xml
        }

        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException iae) {
            throw new IOException("Base64 inválido", iae);
        }

        // Ubicación para guardar: carpeta Images dentro del contexto web
        String imagesDirPath = request.getServletContext().getRealPath("/Images");
        if (imagesDirPath == null) {
            // fallback: usar temp dir
            imagesDirPath = System.getProperty("java.io.tmpdir") + File.separator + "WebAppApache_Images";
        }
        File imagesDir = new File(imagesDirPath);
        if (!imagesDir.exists()) imagesDir.mkdirs();
        // Guardar la ruta en el contexto para que el servlet /Images/* pueda servir desde aquí
        try {
            request.getServletContext().setAttribute("IMAGES_DIR", imagesDir.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("No se pudo guardar IMAGES_DIR en el ServletContext: " + e.getMessage());
        }

        String safeNick = (nickname == null || nickname.isBlank()) ? "user" : nickname.replaceAll("[^a-zA-Z0-9_-]", "_");
        String filename = safeNick + "_" + System.currentTimeMillis() + "." + ext;
        File outFile = new File(imagesDir, filename);
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            fos.write(bytes);
        }

        // Devolver URL relativo que se pueda guardar en la BD y servir desde la app
        String context = request.getContextPath();
        if (context == null) context = "";
        String relative = context + "/Images/" + filename;
        System.out.println("Imagen guardada: " + outFile.getAbsolutePath() + " -> URL: " + relative);
        return relative;
    }

    private String readRequestBody(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void writeJson(HttpServletResponse resp, int status, JSONObject obj) throws IOException {
        resp.setStatus(status);
        try (PrintWriter out = resp.getWriter()) {
            out.print(obj.toString());
        }
    }

    private String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        String s = sw.toString();
        return s.length() > 8000 ? s.substring(0, 8000) + "...[truncated]" : s;
    }

    // java
    private boolean actualizarCliente(ISistema sistema, String nickname, JSONObject datos) throws Exception {
        var cliente = sistema.obtenerCliente(nickname);
        if (cliente == null) {
            throw new IllegalStateException("Cliente no encontrado: " + nickname);
        }

        String nombre = datos.optString("nombre", null);
        String apellido = datos.optString("apellido", null);
        String nacionalidad = datos.optString("nacionalidad", null);
        String tipoDocumentoStr = datos.optString("tipoDocumento", "");
        String numeroDocumento = datos.optString("numeroDocumento", null);
        String imagenUrl = datos.optString("imagenUrl", null);
        String nuevaPassword = datos.optString("password", null);

        TipoDoc tipoDoc = convertirTipoDocumento(tipoDocumentoStr);
        if (tipoDocumentoStr != null && !tipoDocumentoStr.isBlank() && tipoDoc == null) {
            throw new IllegalArgumentException("Tipo de documento no válido: " + tipoDocumentoStr);
        }

        LocalDate fechaNacimiento = null;
        String fstr = datos.optString("fechaNacimiento", null);
        if (fstr != null && !fstr.isBlank()) {
            try {
                fechaNacimiento = LocalDate.parse(fstr);
            } catch (Exception e) {
                throw new IllegalArgumentException("Fecha inválida: " + fstr, e);
            }
        }

        System.out.println("Llamando a modificarDatosClienteCompleto con: nickname=" + nickname +
                ", nombre=" + nombre + ", apellido=" + apellido + ", nacionalidad=" + nacionalidad +
                ", fechaNacimiento=" + fechaNacimiento + ", tipoDoc=" + tipoDoc + ", numeroDocumento=" + numeroDocumento +
                ", nuevaPassword=" + (nuevaPassword != null ? "[PROVIDED]" : "null") +
                ", imagenUrl=" + (imagenUrl != null ? "[data-or-url]" : "null"));

        // Llamada a la lógica; si lanza excepción, se propagará y el doPost la devolverá con stacktrace
        sistema.modificarDatosClienteCompleto(nickname, nombre, apellido, nacionalidad,
                fechaNacimiento, tipoDoc, numeroDocumento, nuevaPassword, imagenUrl);

        return true;
    }

    private boolean actualizarAerolinea(ISistema sistema, String nickname, JSONObject datos) throws Exception {
        var aerolinea = sistema.obtenerAerolinea(nickname);
        if (aerolinea == null) {
            throw new IllegalStateException("Aerolínea no encontrada: " + nickname);
        }

        String nombre = datos.optString("nombre", null);
        String descripcion = datos.optString("descripcion", null);
        String sitioWeb = datos.optString("sitioWeb", null);
        String nuevaPassword = datos.optString("password", null);
        String imagenUrl = datos.optString("imagenUrl", null);

        System.out.println("Llamando a modificarDatosAerolineaCompleto con: nickname=" + nickname +
                ", nombre=" + nombre + ", descripcion=" + descripcion + ", sitioWeb=" + sitioWeb +
                ", nuevaPassword=" + (nuevaPassword != null ? "[PROVIDED]" : "null") +
                ", imagenUrl=" + (imagenUrl != null ? "[data-or-url]" : "null"));

        // Llamada a la lógica; propaga excepción si ocurre
        sistema.modificarDatosAerolineaCompleto(nickname, nombre, descripcion, sitioWeb, nuevaPassword, imagenUrl);

        return true;
    }


    private TipoDoc convertirTipoDocumento(String tipoDocStr) {
        if (tipoDocStr == null) return null;

        // Normalizar: quitar espacios, guiones, guiones bajos y pasar a minúsculas
        String norm = tipoDocStr.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        // Match robusto por contenido
        if (norm.contains("cedula") || norm.contains("cedulaidentidad") || norm.equals("ci") ) {
            return TipoDoc.CEDULAIDENTIDAD;
        }
        if (norm.contains("pasaporte") || norm.equals("passport")) {
            return TipoDoc.PASAPORTE;
        }

        // No reconocido
        return null;
    }
    // Mantén tus métodos actualizarCliente / actualizarAerolinea (idénticos a los actuales),
    // asegurándote de que las llamadas a sistema.* estén dentro de try/catch (ya lo hacen).
    // ...
}

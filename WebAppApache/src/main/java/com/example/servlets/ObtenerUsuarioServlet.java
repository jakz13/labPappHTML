package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtCliente;
import DataTypes.DtAerolinea;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.nio.file.Paths;

@WebServlet("/api/usuario/actual")
public class ObtenerUsuarioServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession(false);
            String nickname = (session != null) ? (String) session.getAttribute("usuario") : null;
            String tipoUsuario = (session != null) ? (String) session.getAttribute("tipoUsuario") : null;

            if (nickname == null || tipoUsuario == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false, \"error\":\"Usuario no autenticado\"}");
                return;
            }

            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            if ("cliente".equals(tipoUsuario)) {
                DtCliente dtCliente = sistema.obtenerCliente(nickname);
                if (dtCliente != null) {
                    String imagen = "";
                    try { imagen = dtCliente.getImagenUrl() != null ? dtCliente.getImagenUrl() : ""; } catch (Throwable t) { imagen = ""; }

                    String publicImg = buildImageUrl(request, imagen);

                    String json = String.format(
                            "{\"success\":true, \"tipo\":\"cliente\", " +
                                    "\"nickname\":\"%s\", \"email\":\"%s\", \"nombre\":\"%s\", " +
                                    "\"apellido\":\"%s\", \"fechaNacimiento\":\"%s\", " +
                                    "\"nacionalidad\":\"%s\", \"tipoDocumento\":\"%s\", " +
                                    "\"numeroDocumento\":\"%s\", \"fechaRegistro\":\"%s\", \"imagenUrl\":\"%s\"}",
                            escapeJson(dtCliente.getNickname()),
                            escapeJson(dtCliente.getEmail()),
                            escapeJson(dtCliente.getNombre()),
                            escapeJson(dtCliente.getApellido()),
                            dtCliente.getFechaNacimiento() != null ? dtCliente.getFechaNacimiento().toString() : "",
                            escapeJson(dtCliente.getNacionalidad()),
                            escapeJson(dtCliente.getTipoDocumento() != null ? dtCliente.getTipoDocumento().toString() : ""),
                            escapeJson(dtCliente.getNumeroDocumento()),
                            dtCliente.getFechaAlta() != null ? dtCliente.getFechaAlta().toString() : "",
                            escapeJson(publicImg)
                    );
                    out.print(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"success\":false, \"error\":\"Cliente no encontrado\"}");
                }
            } else if ("aerolinea".equals(tipoUsuario)) {
                DtAerolinea dtAerolinea = sistema.obtenerAerolinea(nickname);
                if (dtAerolinea != null) {

                    String imagen = "";
                    try { imagen = dtAerolinea.getImagenUrl() != null ? dtAerolinea.getImagenUrl() : ""; } catch (Throwable t) { imagen = ""; }

                    String publicImg = buildImageUrl(request, imagen);

                    String json = String.format(
                            "{\"success\":true, \"tipo\":\"aerolinea\", " +
                                    "\"nickname\":\"%s\", \"email\":\"%s\", \"nombre\":\"%s\", " +
                                    "\"descripcion\":\"%s\", \"sitioWeb\":\"%s\", \"imagenUrl\":\"%s\"}",
                            escapeJson(dtAerolinea.getNickname()),
                            escapeJson(dtAerolinea.getEmail()),
                            escapeJson(dtAerolinea.getNombre()),
                            escapeJson(dtAerolinea.getDescripcion() != null ? dtAerolinea.getDescripcion() : ""),
                            escapeJson(dtAerolinea.getSitioWeb() != null ? dtAerolinea.getSitioWeb() : ""),
                            escapeJson(publicImg)
                    );
                    out.print(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"success\":false, \"error\":\"Aerolínea no encontrada\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"Tipo de usuario no válido\"}");
            }

        } catch (Exception e) {
            System.err.println("Error obteniendo usuario: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false, \"error\":\"Error interno del servidor\"}");
        }
    }

    private String buildImageUrl(HttpServletRequest request, String stored) {
        if (stored == null) return "";
        stored = stored.trim();
        if (stored.isEmpty()) return "";
        if (stored.startsWith("http://") || stored.startsWith("https://")) return stored;
        if (stored.startsWith("/")) return stored;
        try {
            String filename = Paths.get(stored).getFileName().toString();
            if (filename == null || filename.isEmpty()) filename = stored;
            return request.getContextPath() + "/Images/" + filename;
        } catch (Exception e) {
            return request.getContextPath() + "/Images/" + stored;
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

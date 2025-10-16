package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import Logica.Cliente;
import Logica.Aerolinea;
import DataTypes.DtCliente;
import DataTypes.DtAerolinea;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

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
                    String json = String.format(
                            "{\"success\":true, \"tipo\":\"cliente\", " +
                                    "\"nickname\":\"%s\", \"email\":\"%s\", \"nombre\":\"%s\", " +
                                    "\"apellido\":\"%s\", \"fechaNacimiento\":\"%s\", " +
                                    "\"nacionalidad\":\"%s\", \"tipoDocumento\":\"%s\", " +
                                    "\"numeroDocumento\":\"%s\", \"fechaRegistro\":\"%s\"}",
                            dtCliente.getNickname(),
                            dtCliente.getEmail(),
                            dtCliente.getNombre(),
                            dtCliente.getApellido(),
                            dtCliente.getFechaNacimiento() != null ? dtCliente.getFechaNacimiento().toString() : "",
                            dtCliente.getNacionalidad(),
                            dtCliente.getTipoDocumento(),
                            dtCliente.getNumeroDocumento(),
                            dtCliente.getFechaAlta() != null ? dtCliente.getFechaAlta().toString() : ""
                    );
                    out.print(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"success\":false, \"error\":\"Cliente no encontrado\"}");
                }
            } else if ("aerolinea".equals(tipoUsuario)) {
                DtAerolinea dtAerolinea = sistema.obtenerAerolinea(nickname);
                if (dtAerolinea != null) {

                    String json = String.format(
                            "{\"success\":true, \"tipo\":\"aerolinea\", " +
                                    "\"nickname\":\"%s\", \"email\":\"%s\", \"nombre\":\"%s\", " +
                                    "\"descripcion\":\"%s\", \"sitioWeb\":\"%s\"}",
                            dtAerolinea.getNickname(),
                            dtAerolinea.getEmail(),
                            dtAerolinea.getNombre(),
                            dtAerolinea.getDescripcion() != null ? dtAerolinea.getDescripcion() : "",
                            dtAerolinea.getSitioWeb() != null ? dtAerolinea.getSitioWeb() : ""
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
}
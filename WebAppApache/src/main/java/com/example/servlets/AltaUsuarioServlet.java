package com.example.servlets;

import Logica.Sistema;
import Logica.TipoDoc;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
            String password = request.getParameter("password"); // No se usa en lógica actual
            String tipoUsuario = request.getParameter("tipoUsuario");

            Sistema sistema = new Sistema();

            if ("cliente".equals(tipoUsuario)) {
                String apellido = request.getParameter("apellido");
                String fechaNacimiento = request.getParameter("fechaNacimiento");
                String nacionalidad = request.getParameter("nacionalidad");
                String tipoDocumento = request.getParameter("tipoDocumento");
                String numeroDocumento = request.getParameter("numeroDocumento");

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

                // Alta cliente
                sistema.altaCliente(nickname, nombre, apellido, email, fechaNac, nacionalidad, tipoDoc, numeroDocumento);

            } else if ("aerolinea".equals(tipoUsuario)) {
                String descripcion = request.getParameter("descripcionAerolinea");
                String sitioWeb = request.getParameter("sitioWeb");

                if (nickname == null || nombre == null || email == null || descripcion == null) {
                    out.print("{\"success\": false, \"error\": \"Faltan datos obligatorios\"}");
                    return;
                }

                sistema.altaAerolinea(nickname, nombre, descripcion, email, sitioWeb);

            } else {
                out.print("{\"success\": false, \"error\": \"Tipo de usuario inválido\"}");
                return;
            }


            out.print("{\"success\": true}");

        } catch (IllegalArgumentException e) {
            out.print("{\"success\": false, \"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"error\": \"Error interno: " + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }
}

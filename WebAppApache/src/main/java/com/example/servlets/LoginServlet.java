// src/main/java/com/example/servlets/LoginServlet.java
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtUsuario;
import DataTypes.DtCliente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            String emailOrNickname = request.getParameter("email");
            String password = request.getParameter("password");

            // Verificar credenciales usando tu lógica existente

            List<DtCliente> usuarios = sistema.listarClientes();
            DtUsuario usuario = usuarios.get(0);

            if (usuario != null) {
                // Crear sesión HTTP
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuario);
                session.setMaxInactiveInterval(30 * 60); // 30 minutos

                // Preparar datos del usuario para el frontend
                String userType = (usuario instanceof DtCliente) ? "cliente" : "aerolinea";

                out.print("{");
                out.print("\"success\": true,");
                out.print("\"user\": {");
                out.print("\"email\": \"" + escapeJson(usuario.getEmail()) + "\",");
                out.print("\"nickname\": \"" + escapeJson(usuario.getNickname()) + "\",");
                out.print("\"nombre\": \"" + escapeJson(usuario.getNombre()) + "\",");
                out.print("\"tipo\": \"" + userType + "\"");
                out.print("}");
                out.print("}");
            } else {
                out.print("{\"success\": false, \"error\": \"Credenciales inválidas\"}");
            }

        } catch (Exception e) {
            out.print("{\"success\": false, \"error\": \"Error en el servidor: " + e.getMessage() + "\"}");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
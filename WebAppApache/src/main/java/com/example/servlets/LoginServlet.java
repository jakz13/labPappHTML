// src/main/java/com/example/servlets/LoginServlet.java
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            // Aquí debes validar el usuario y contraseña contra tu sistema
            boolean usuarioValido = true; // Debes implementar este método

            if (usuarioValido) {
                // Guardar usuario en sesión
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", nickname);

                out.print("{\"success\":true, \"nickname\":\"" + nickname + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false, \"error\":\"Credenciales incorrectas\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false, \"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
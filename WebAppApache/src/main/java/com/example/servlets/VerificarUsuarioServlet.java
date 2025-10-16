// src/main/java/com/example/servlets/VerificarUsuarioServlet.java
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/verificar-usuario")
public class VerificarUsuarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);

        if (session != null) {
            String nickname = (String) session.getAttribute("usuario");
            String tipo = (String) session.getAttribute("tipoUsuario");

            if (nickname != null && tipo != null) {
                out.print("{");
                out.print("\"autenticado\": true,");
                out.print("\"nickname\": \"" + escapeJson(nickname) + "\",");
                out.print("\"tipo\": \"" + escapeJson(tipo) + "\"");
                out.print("}");
            } else {
                out.print("{\"autenticado\": false}");
            }
        } else {
            out.print("{\"autenticado\": false}");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
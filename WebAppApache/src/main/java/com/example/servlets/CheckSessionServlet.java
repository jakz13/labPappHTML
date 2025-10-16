package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/api/check-session")
public class CheckSessionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("usuario") != null) {
            String nickname = (String) session.getAttribute("usuario");
            String tipo = (String) session.getAttribute("tipoUsuario");
            out.print("{\"authenticated\":true,\"nickname\":\"" + nickname + "\",\"tipo\":\"" + tipo + "\"}");
        } else {
            out.print("{\"authenticated\":false}");
        }
    }
}
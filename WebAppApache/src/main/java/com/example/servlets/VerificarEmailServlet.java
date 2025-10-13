package com.example.servlets;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;


public class VerificarEmailServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("✅ VerificarEmailServlet EJECUTADO");

        String email = request.getParameter("email");
        System.out.println("📧 Email recibido: " + email);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // SIMULACIÓN - siempre disponible excepto admin@admin.com
        boolean disponible = !"admin@admin.com".equalsIgnoreCase(email);

        String json = "{\"disponible\": " + disponible + "}";
        System.out.println("📤 Enviando: " + json);

        PrintWriter out = response.getWriter();
        out.print(json);
    }
}
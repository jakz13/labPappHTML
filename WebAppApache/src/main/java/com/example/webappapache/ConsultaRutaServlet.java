package com.example.webappapache;

// CAMBIA ESTOS IMPORTS:
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/consultarRutas")
public class ConsultaRutaServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Datos de ejemplo temporal
        response.getWriter().print("[{\"id\":\"TEST\",\"nombre\":\"Ruta de prueba\"}]");
    }
}
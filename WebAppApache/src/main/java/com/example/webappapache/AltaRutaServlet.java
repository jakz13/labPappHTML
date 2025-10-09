package com.example.webappapache;

// CAMBIA ESTOS IMPORTS:
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/altaRuta")
@MultipartConfig
public class AltaRutaServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print("{\"success\": true, \"message\": \"Ruta creada exitosamente\"}");
    }
}
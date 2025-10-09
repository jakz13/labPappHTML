package com.example.webappapache;

// CAMBIA ESTOS IMPORTS:
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/verificarEmail")
public class VerificarEmailServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print("{\"disponible\": true}");
    }
}
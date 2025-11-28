package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

import com.example.util.PortUtils;
import serviciosweb.JuanViajesWS;

@WebServlet("/incrementarVisitasRuta")
public class IncrementarVisitasRutaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = request.getParameter("nombreRuta");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (nombre == null || nombre.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false, \"error\":\"nombreRuta requerido\"}");
            return;
        }

        try {
            JuanViajesWS port = PortUtils.getPort(request);
            try {
                port.incrementarVisitasRuta(nombre.trim());
                out.print("{\"success\":true}");
            } catch (Exception e) {
                System.err.println("[IncrementarVisitasRuta] Error al llamar SOAP: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\":false, \"error\":\"Error incrementando visitas\"}");
            }
        } catch (Exception e) {
            System.err.println("[IncrementarVisitasRuta] Error general: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false, \"error\":\"Error interno\"}");
        }
    }
}


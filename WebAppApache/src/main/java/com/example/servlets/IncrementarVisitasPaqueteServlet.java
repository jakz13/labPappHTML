package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.lang.reflect.Method;

import com.example.util.PortUtils;
import serviciosweb.JuanViajesWS;

@WebServlet("/incrementarVisitasPaquete")
public class IncrementarVisitasPaqueteServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String nombre = request.getParameter("nombre");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if ((id == null || id.trim().isEmpty()) && (nombre == null || nombre.trim().isEmpty())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false, \"error\":\"id o nombre requerido\"}");
            return;
        }

        try {
            JuanViajesWS port = PortUtils.getPort(request);
            // Intentar usar método incrementarVisitasPaquete si existe
            try {
                Method m = port.getClass().getMethod("incrementarVisitasPaquete", String.class);
                if (m != null) {
                    if (id != null && !id.trim().isEmpty()) m.invoke(port, id.trim());
                    else m.invoke(port, nombre.trim());
                    out.print("{\"success\":true}");
                    return;
                }
            } catch (NoSuchMethodException nsme) {
                // no existe el método en el port
            }

            // Si no existe el método, devolver success=true (no crítico) pero avisar
            System.out.println("[IncrementarVisitasPaquete] Método no disponible en el servicio SOAP; no se incrementaron visitas");
            out.print("{\"success\":false, \"notice\":\"Método incrementarVisitasPaquete no implementado\"}");

        } catch (Exception e) {
            System.err.println("[IncrementarVisitasPaquete] Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false, \"error\":\"Error interno\"}");
        }
    }
}


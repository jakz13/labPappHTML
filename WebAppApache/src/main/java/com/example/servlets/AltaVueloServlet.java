// Archivo: src/main/java/com/example/servlets/AltaVueloServlet.java
package com.example.servlets;

import Logica.Sistema;
import Logica.TipoAsiento;
import DataTypes.DtAerolinea;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.time.LocalDate;

@WebServlet("/altaVuelo")
public class AltaVueloServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String nombreVuelo = request.getParameter("nombreVuelo");
            String nombreAerolinea ="coso";
            String nombreRuta = request.getParameter("nombreRuta");
            LocalDate fecha = LocalDate.parse(request.getParameter("fecha"));
            int duracion = Integer.parseInt(request.getParameter("duracion"));
            int asientosTurista = Integer.parseInt(request.getParameter("asientosTurista"));
            int asientosEjecutivo = Integer.parseInt(request.getParameter("asientosEjecutivo"));
            LocalDate fechaAlta = LocalDate.now();

            Sistema sistema = new Sistema();
            sistema.altaVuelo(nombreVuelo, nombreAerolinea, nombreRuta, fecha, duracion, asientosTurista, asientosEjecutivo, fechaAlta);

            out.print("{\"success\": true}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }
}

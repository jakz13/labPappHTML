// src/main/java/com/example/servlets/AltaRutaServlet.java
package com.example.servlets;

import DataTypes.DtAerolinea;
import Logica.Fabrica;
import Logica.ISistema;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.time.LocalDate;

@WebServlet("/altaRuta")
public class AltaRutaServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            String nombre = request.getParameter("nombre");
            String descripcionCorta = request.getParameter("descripcionCorta");
            String descripcion = request.getParameter("descripcion");
            String nombreAerolinea = "coso"; // Puedes ajustar si es fijo o por sesión
            String ciudadOrigen = request.getParameter("origen");
            String ciudadDestino = request.getParameter("destino");
            String hora = request.getParameter("hora");
            double costoTurista = Double.parseDouble(request.getParameter("costoTurista"));
            double costoEjecutivo = Double.parseDouble(request.getParameter("costoEjecutivo"));
            double costoEquipajeExtra = Double.parseDouble(request.getParameter("costoEquipaje"));
            String[] categorias = request.getParameterValues("categorias");
            LocalDate fechaAlta = LocalDate.now();

            // Obtener la aerolínea (ajusta si es por sesión)
            DtAerolinea aerolinea = sistema.obtenerAerolinea(nombreAerolinea);

            // Ajuste: la firma de ISistema espera (nombre, descripcionCorta, aerolinea, origen, destino, hora, fechaAlta, costoTurista, costoEjecutivo, costoEquipajeExtra, categorias)
            sistema.altaRutaVuelo(
                    nombre, descripcion, descripcionCorta, aerolinea, ciudadOrigen, ciudadDestino, hora,
                    fechaAlta, costoTurista, costoEjecutivo, costoEquipajeExtra, categorias
            );

            out.print("{\"success\": true}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }
}

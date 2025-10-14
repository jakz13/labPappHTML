// src/main/java/com/example/servlets/ReservaVueloServlet.java
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import Logica.Pasajero;
import Logica.TipoAsiento;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/reservaVuelo")
public class ReservaVueloServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();

            String nombreVuelo = request.getParameter("vuelo");
            String tipoAsientoStr = request.getParameter("tipoAsiento");
            int cantidadPasajes = Integer.parseInt(request.getParameter("cantidadPasajes"));
            int equipajeExtra = Integer.parseInt(request.getParameter("equipajeExtra"));
            String nicknameCliente = request.getParameter("nicknameCliente"); // Debes obtenerlo de la sesión o el form

            // Pasajeros
            String[] nombres = request.getParameterValues("nombrePasajero[]");
            String[] apellidos = request.getParameterValues("apellidoPasajero[]");
            List<Pasajero> pasajeros = new ArrayList<>();
            if (nombres != null && apellidos != null) {
                for (int i = 0; i < nombres.length; i++) {
                    pasajeros.add(sistema.crearPasajero(nombres[i], apellidos[i]));
                }
            }

            TipoAsiento tipoAsiento = tipoAsientoStr.equalsIgnoreCase("ejecutivo") ? TipoAsiento.EJECUTIVO : TipoAsiento.TURISTA;
            LocalDate fechaReserva = LocalDate.now();
            double costo = sistema.calcularCostoReserva(nombreVuelo, tipoAsiento, cantidadPasajes, equipajeExtra);

            sistema.crearYRegistrarReserva(
                    nicknameCliente, nombreVuelo, fechaReserva, costo, tipoAsiento, cantidadPasajes, equipajeExtra, pasajeros
            );

            out.print("{\"success\":true}");
        } catch (Exception e) {
            out.print("{\"success\":false, \"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}

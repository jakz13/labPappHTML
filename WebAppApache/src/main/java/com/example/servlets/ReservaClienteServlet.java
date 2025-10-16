// src/main/java/com/example/servlets/ReservasVueloServlet.java
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtReserva;
import DataTypes.DtVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/api/reserva-cliente")
public class ReservaClienteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreVuelo = request.getParameter("vuelo");
        // En realidad el nickname del cliente vendría de la sesión
        String clienteNickname = "maria123"; // Ejemplo hardcodeado

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            // Obtener todas las reservas del cliente
            List<DtReserva> reservasCliente = sistema.getReservasCliente(clienteNickname);

            // Buscar la reserva para este vuelo específico
            DtReserva reservaEnVuelo = null;
            for (DtReserva reserva : reservasCliente) {
                if (reserva.getVuelo().equals(nombreVuelo)) {
                    reservaEnVuelo = reserva;
                    break;
                }
            }

            if (reservaEnVuelo != null) {
                out.print("{");
                out.print("\"id\":\"" + reservaEnVuelo.getId() + "\",");
                out.print("\"tipoAsiento\":\"" + reservaEnVuelo.getTipoAsiento() + "\",");
                out.print("\"cantidadPasajes\":" + reservaEnVuelo.getCantidadPasajes() + ",");
                out.print("\"equipajeExtra\":" + reservaEnVuelo.getUnidadesEquipajeExtra() + ",");
                out.print("\"costoTotal\":" + reservaEnVuelo.getCosto() + ",");
                out.print("\"fechaReserva\":\"" + reservaEnVuelo.getFecha() + "\"");
                out.print("}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Reserva no encontrada\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error al obtener reserva: " + e.getMessage() + "\"}");
        }
    }
}
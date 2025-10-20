// src/main/java/com/example/servlets/ReservaClienteServlet.java
package com.example.servlets;

import DataTypes.DtAerolinea;
import DataTypes.DtRutaVuelo;
import DataTypes.DtVuelo;
import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtReserva;
import Logica.Reserva;
import Logica.Vuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/api/reserva-cliente")
public class ReservaClienteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreVuelo = request.getParameter("vuelo");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            // Obtener el usuario de la sesión
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuario") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\":\"Usuario no autenticado\"}");
                return;
            }

            String clienteNickname = (String) session.getAttribute("usuario");

            // Obtener todas las reservas del cliente
            List<DtReserva> reservasCliente = sistema.getReservasCliente(clienteNickname);

            // Buscar si alguna reserva del cliente está en el vuelo solicitado
            DtReserva reservaEnVuelo = buscarReservaEnVuelo(reservasCliente, nombreVuelo, sistema);

            if (reservaEnVuelo != null) {
                out.print("{");
                out.print("\"id\":\"" + escapeJson(reservaEnVuelo.getId()) + "\",");
                out.print("\"tipoAsiento\":\"" + escapeJson(String.valueOf(reservaEnVuelo.getTipoAsiento())) + "\",");
                out.print("\"cantidadPasajes\":" + reservaEnVuelo.getCantidadPasajes() + ",");
                out.print("\"equipajeExtra\":" + reservaEnVuelo.getUnidadesEquipajeExtra() + ",");
                out.print("\"costoTotal\":" + reservaEnVuelo.getCosto() + ",");
                out.print("\"fechaReserva\":\"" + escapeJson(String.valueOf(reservaEnVuelo.getFecha())) + "\"");
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

    private DtReserva buscarReservaEnVuelo(List<DtReserva> reservasCliente, String nombreVuelo, ISistema sistema) {
        try {
            // Obtener el vuelo específico
            Vuelo vuelo = sistema.obtenerVuelo(nombreVuelo);
            if (vuelo != null) {
                // Obtener las reservas del vuelo como Map
                Map<String, Reserva> reservasVuelo = vuelo.getReservas();

                // Buscar si alguna reserva del cliente está en el vuelo
                for (DtReserva reservaCliente : reservasCliente) {
                    for (Reserva reservaVuelo : reservasVuelo.values()) {
                        if (reservaVuelo.getId().equals(reservaCliente.getId())) {
                            return reservaCliente;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
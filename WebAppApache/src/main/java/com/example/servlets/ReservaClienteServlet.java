// src/main/java/com/example/servlets/ReservaClienteServlet.java
package com.example.servlets;

import DataTypes.DtVuelo;
import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtReserva;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
                out.print("\"id\":\"" + escapeJson(String.valueOf(reservaEnVuelo.getId())) + "\",");
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
            log("Error en ReservaClienteServlet", e);
        }
    }

    private DtReserva buscarReservaEnVuelo(List<DtReserva> reservasCliente, String nombreVuelo, ISistema sistema) {
        try {
            // Obtener el vuelo específico
            DtVuelo vuelo = sistema.verInfoVueloDt(nombreVuelo);
            if (vuelo != null) {
                // Obtener las reservas del vuelo como lista de DtReserva
                List<DtReserva> reservasVuelo = vuelo.getReservas();
                if (reservasVuelo == null) reservasVuelo = java.util.Collections.emptyList();

                // Buscar si alguna reserva del cliente está en el vuelo
                for (DtReserva reservaCliente : reservasCliente) {
                    for (DtReserva reservaVuelo : reservasVuelo) {
                        // Comparar IDs convirtiéndolos a String para asegurarnos de compatibilidad entre tipos
                        if (String.valueOf(reservaVuelo.getId()).equals(String.valueOf(reservaCliente.getId()))) {
                            return reservaCliente;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log("Error en buscarReservaEnVuelo", e);
        }
        return null;
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
// src/main/java/com/example/servlets/ReservasVueloServlet.java
package com.example.servlets;

import DataTypes.DtCliente;
import DataTypes.DtReserva;
import Logica.Fabrica;
import Logica.ISistema;
import Logica.Reserva;
import Logica.Vuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/api/reservas-vuelo")
public class ReservasVueloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreVuelo = request.getParameter("nombreVuelo");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            // Enfoque simple: buscar en todos los clientes quiénes tienen reservas en este vuelo
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            boolean primeraReserva = true;

            for (DtCliente cliente : sistema.listarClientes()) {
                for (DtReserva reserva : cliente.getReservas()) {
                    // Verificar si esta reserva pertenece al vuelo solicitado
                    if (reservaPerteneceAVuelo(reserva, nombreVuelo, sistema)) {
                        if (!primeraReserva) {
                            jsonBuilder.append(",");
                        }

                        jsonBuilder.append("{");
                        jsonBuilder.append("\"id\":\"").append(escapeJson(String.valueOf(reserva.getId()))).append("\",");
                        jsonBuilder.append("\"clienteNombre\":\"").append(escapeJson(cliente.getNombre() + " " + cliente.getApellido())).append("\",");
                        jsonBuilder.append("\"tipoAsiento\":\"").append(escapeJson(String.valueOf(reserva.getTipoAsiento()))).append("\",");
                        jsonBuilder.append("\"cantidadPasajes\":").append(reserva.getCantidadPasajes()).append(",");
                        jsonBuilder.append("\"equipajeExtra\":").append(reserva.getUnidadesEquipajeExtra()).append(",");
                        jsonBuilder.append("\"costoTotal\":").append(reserva.getCosto()).append(",");
                        jsonBuilder.append("\"fechaReserva\":\"").append(escapeJson(String.valueOf(reserva.getFecha()))).append("\"");
                        jsonBuilder.append("}");

                        primeraReserva = false;
                    }
                }
            }

            jsonBuilder.append("]");
            out.print(jsonBuilder.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error al obtener reservas: " + e.getMessage() + "\"}");
        }
    }

    private boolean reservaPerteneceAVuelo(DtReserva reserva, String nombreVuelo, ISistema sistema) {
        try {
            // Obtener el vuelo específico
            Vuelo vuelo = sistema.obtenerVuelo(nombreVuelo);
            if (vuelo != null) {
                // Verificar si el vuelo tiene esta reserva (Map con clave Long)
                Map<Long, Reserva> reservasVuelo = vuelo.getReservas();
                for (Reserva reservaVuelo : reservasVuelo.values()) {
                    if (String.valueOf(reservaVuelo.getId()).equals(String.valueOf(reserva.getId()))) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
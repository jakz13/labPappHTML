// src/main/java/com/example/servlets/ReservasVueloServlet.java
package com.example.servlets;

import DataTypes.DtCliente;
import DataTypes.DtPasajero;
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


@WebServlet("/api/reserva-detalle")
public class ReservaDetalleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idReserva = request.getParameter("id");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            // Buscar la reserva en todos los clientes
            DtReserva reserva = null;
            String clienteNickname = null;

            for (DtCliente cliente : sistema.listarClientes()) {
                try {
                    reserva = sistema.obtenerReserva(idReserva, cliente.getNickname());
                    if (reserva != null) {
                        clienteNickname = cliente.getNickname();
                        break;
                    }
                } catch (Exception e) {
                    // Continuar buscando
                }
            }

            if (reserva != null && clienteNickname != null) {
                DtCliente cliente = sistema.obtenerCliente(clienteNickname);

                out.print("{");
                out.print("\"id\":\"" + reserva.getId() + "\",");
                out.print("\"clienteNombre\":\"" + escapeJson(cliente.getNombre() + " " + cliente.getApellido()) + "\",");
                out.print("\"tipoAsiento\":\"" + reserva.getTipoAsiento() + "\",");
                out.print("\"cantidadPasajes\":" + reserva.getCantidadPasajes() + ",");
                out.print("\"equipajeExtra\":" + reserva.getUnidadesEquipajeExtra() + ",");
                out.print("\"costoTotal\":" + reserva.getCosto() + ",");
                out.print("\"fechaReserva\":\"" + reserva.getFecha() + "\",");

                // Lista de pasajeros
                out.print("\"pasajeros\":[");
                List<DtPasajero> pasajeros = reserva.getPasajeros();
                for (int i = 0; i < pasajeros.size(); i++) {
                    DtPasajero p = pasajeros.get(i);
                    out.print("{\"nombre\":\"" + p.getNombre() + "\",\"apellido\":\"" + p.getApellido() + "\"}");
                    if (i < pasajeros.size() - 1) out.print(",");
                }
                out.print("]");
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

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

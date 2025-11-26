// src/main/java/com/example/servlets/ReservasVueloServlet.java
package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import serviciosweb.JuanViajesWS;
import serviciosweb.DtCliente;
import serviciosweb.DtReserva;
import com.example.util.PortUtils;

@WebServlet("/api/reservas-vuelo")
public class ReservasVueloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreVuelo = request.getParameter("nombreVuelo");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (nombreVuelo == null || nombreVuelo.trim().isEmpty()) {
            out.print("[]");
            return;
        }

        try {
            JuanViajesWS port = PortUtils.getPort(request);

            List<DtCliente> clientes = null;
            try { clientes = port.listarClientes(); } catch (Exception e) { clientes = null; }

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            boolean primera = true;

            if (clientes != null) {
                for (DtCliente cliente : clientes) {
                    if (cliente == null) continue;
                    List<DtReserva> reservas = cliente.getReservas();
                    if (reservas == null) continue;
                    for (DtReserva reserva : reservas) {
                        if (reserva == null) continue;
                        String vuelo = reserva.getVuelo();
                        if (vuelo != null && vuelo.equalsIgnoreCase(nombreVuelo)) {
                            if (!primera) jsonBuilder.append(",");
                            jsonBuilder.append("{");
                            jsonBuilder.append("\"id\":\"").append(reserva.getId()!=null?reserva.getId().toString():"").append("\",");
                            String clienteNombre = (cliente.getNombre()!=null?cliente.getNombre():"") + " " + (cliente.getApellido()!=null?cliente.getApellido():"");
                            jsonBuilder.append("\"clienteNombre\":\"").append(escapeJson(clienteNombre.trim())).append("\",");
                            jsonBuilder.append("\"tipoAsiento\":\"").append(reserva.getTipoAsiento()!=null?reserva.getTipoAsiento().toString():"").append("\",");
                            jsonBuilder.append("\"cantidadPasajes\":").append(reserva.getCantidadPasajes()).append(",");
                            jsonBuilder.append("\"equipajeExtra\":").append(reserva.getUnidadesEquipajeExtra()).append(",");
                            jsonBuilder.append("\"costoTotal\":").append(reserva.getCosto()).append(",");
                            jsonBuilder.append("\"fechaReserva\":\"").append(reserva.getFecha()!=null?reserva.getFecha().toString():"").append("\"");
                            jsonBuilder.append("}");
                            primera = false;
                        }
                    }
                }
            }

            jsonBuilder.append("]");
            out.print(jsonBuilder.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("[]");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
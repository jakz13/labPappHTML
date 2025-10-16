// src/main/java/com/example/servlets/ReservasVueloServlet.java
package com.example.servlets;

import DataTypes.DtCliente;
import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtReserva;
import DataTypes.DtVuelo;
import Logica.Vuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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

            // Obtener el vuelo
            Vuelo vuelo = sistema.obtenerVuelo(nombreVuelo);
            if (vuelo == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Vuelo no encontrado\"}");
                return;
            }
//
//            // Obtener todas las reservas del vuelo
//            List<DtReserva> reservas = vuelo.getReservas();
//
//            StringBuilder jsonBuilder = new StringBuilder();
//            jsonBuilder.append("[");
//
//            for (int i = 0; i < reservas.size(); i++) {
//                DtReserva r = reservas.get(i);
//                DtCliente cliente = sistema.obtenerCliente(r.getCliente().getNickname());
//
//                jsonBuilder.append("{");
//                jsonBuilder.append("\"id\":\"").append(escapeJson(r.getId())).append("\",");
//                jsonBuilder.append("\"clienteNombre\":\"").append(escapeJson(cliente.getNombre() + " " + cliente.getApellido())).append("\",");
//                jsonBuilder.append("\"tipoAsiento\":\"").append(escapeJson(r.getTipoAsiento())).append("\",");
//                jsonBuilder.append("\"cantidadPasajes\":").append(r.getCantidadPasajes()).append(",");
//                jsonBuilder.append("\"costoTotal\":").append(r.getCosto());
//                jsonBuilder.append("}");
//
//                if (i < reservas.size() - 1) {
//                    jsonBuilder.append(",");
//                }
//            }

//            jsonBuilder.append("]");
//            out.print(jsonBuilder.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error al obtener reservas: " + e.getMessage() + "\"}");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
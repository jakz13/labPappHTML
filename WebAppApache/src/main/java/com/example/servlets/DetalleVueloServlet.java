// src/main/java/com/example/servlets/DetalleVueloServlet.java
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtVuelo;
import DataTypes.DtReserva;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/detalle-vuelo")
public class DetalleVueloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreRuta = request.getParameter("nombreRuta");
        String nombreVuelo = request.getParameter("nombreVuelo");
        ISistema sistema = Fabrica.getInstance().getISistema();
        List<DtVuelo> vuelos = sistema.listarVuelosPorRuta(nombreRuta);

        DtVuelo vuelo = null;
        for (DtVuelo v : vuelos) {
            if (v.getNombre().equals(nombreVuelo)) {
                vuelo = v;
                break;
            }
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (vuelo == null) {
            out.print("{}");
            out.flush();
            return;
        }
        out.print("{");
        out.print("\"nombre\":\"" + escapeJson(vuelo.getNombre()) + "\",");
        out.print("\"rutaVuelo\":\"" + escapeJson(vuelo.getRutaVuelo().getNombre()) + "\",");
        out.print("\"fecha\":\"" + escapeJson(vuelo.getFecha() != null ? vuelo.getFecha().toString() : "") + "\",");
        out.print("\"duracion\":" + vuelo.getDuracion() + ",");
        out.print("\"asientosTurista\":" + vuelo.getAsientosTurista() + ",");
        out.print("\"asientosEjecutivo\":" + vuelo.getAsientosEjecutivo() + ",");
        out.print("\"fechaAlta\":\"" + escapeJson(vuelo.getFechaAlta() != null ? vuelo.getFechaAlta().toString() : "") + "\",");
        out.print("\"reservas\":[");
        List<DtReserva> reservas = vuelo.getReservas();
        for (int i = 0; i < reservas.size(); i++) {
            DtReserva r = reservas.get(i);
            out.print("{");
            out.print("\"id\":\"" + escapeJson(r.getId()) + "\",");
            out.print("\"costo\":" + r.getCosto() + ",");
            out.print("\"tipoAsiento\":\"" + escapeJson(r.getTipoAsiento().toString()) + "\",");
            out.print("\"cantidadPasajes\":" + r.getCantidadPasajes());
            out.print("}");
            if (i < reservas.size() - 1) out.print(",");
        }
        out.print("]");
        out.print("}");
        out.flush();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import Logica.Vuelo;
import DataTypes.DtVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/vuelo")
public class VerInfoVueloServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombre = request.getParameter("nombre");
        ISistema sistema = Fabrica.getInstance().getISistema();
        sistema.cargarDesdeBd();
        Vuelo vuelo = sistema.verInfoVuelo(nombre);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // src/main/java/com/example/servlets/VerInfoVueloServlet.java
        if (vuelo != null) {
            DtVuelo dt = vuelo.getDtVuelo();
            out.print("{");
            out.print("\"nombre\":\"" + dt.getNombre() + "\",");
            out.print("\"fecha\":\"" + dt.getFecha() + "\",");
            out.print("\"duracion\":\"" + dt.getDuracion() + "\",");
            out.print("\"asientosTurista\":" + dt.getAsientosTurista() + ",");
            out.print("\"asientosEjecutivo\":" + dt.getAsientosEjecutivo());
            out.print("}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Vuelo no encontrado\"}");
        }

    }
}
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/vuelos")
public class ListarVuelosPorRutaServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ruta = request.getParameter("ruta");
        ISistema sistema = Fabrica.getInstance().getISistema();
        sistema.cargarDesdeBd();
        List<DtVuelo> vuelos = sistema.listarVuelosPorRuta(ruta);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("[");
        for (int i = 0; i < vuelos.size(); i++) {
            DtVuelo v = vuelos.get(i);
            out.print("{\"nombre\":\"" + v.getNombre() + "\"}");
            if (i < vuelos.size() - 1) out.print(",");
        }
        out.print("]");
    }
}

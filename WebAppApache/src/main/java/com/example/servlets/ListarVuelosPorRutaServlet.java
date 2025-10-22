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
        // Evitar llamar a cargarDesdeBd() aquí (puede provocar problemas de ciclo de
        // vida de recursos).
        // Confiamos en que el sistema ya cargó los datos en inicialización o en otros
        // flujos.
        List<DtVuelo> vuelos = null;
        try {
            vuelos = sistema.listarVuelosPorRuta(ruta);
            if (vuelos == null)
                vuelos = java.util.Collections.emptyList();
        } catch (Exception e) {
            // Log y devolver JSON de error sin exponer stacktrace completo
            e.printStackTrace(System.err);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Error listando vuelos\"}");
            return;
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("[");
        for (int i = 0; i < vuelos.size(); i++) {
            DtVuelo v = vuelos.get(i);
            out.print("{\"nombre\":\"" + v.getNombre() + "\"}");
            if (i < vuelos.size() - 1)
                out.print(",");
        }
        out.print("]");
    }
}

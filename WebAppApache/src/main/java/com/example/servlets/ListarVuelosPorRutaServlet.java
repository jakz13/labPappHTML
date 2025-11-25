package com.example.servlets;


import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;
import serviciosweb.DtVuelo;
import com.example.util.PortUtils;

@WebServlet("/api/vuelos")
public class ListarVuelosPorRutaServlet extends HttpServlet {
    // ...existing code...

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ruta = request.getParameter("ruta");

        List<DtVuelo> vuelos = null;
        // usar PortUtils para obtener el puerto
        JuanViajesWS port = PortUtils.getPort(request);
        try {
            try { port.cargarDesdeBd(); } catch (Exception ignored) {}
            vuelos = port.listarVuelosPorRuta(ruta);
        } catch (Exception e) {
            // si falla el WS, devolver lista vacía en lugar de usar la lógica local
            vuelos = java.util.Collections.emptyList();
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (vuelos == null) {
            out.print("[]");
            return;
        }
        out.print("[");
        for (int i = 0; i < vuelos.size(); i++) {
            DtVuelo v = vuelos.get(i);
            out.print("{\"nombre\":\"" + (v.getNombre() != null ? v.getNombre() : "") + "\"}");
            if (i < vuelos.size() - 1) out.print(",");
        }
        out.print("]");
    }
}

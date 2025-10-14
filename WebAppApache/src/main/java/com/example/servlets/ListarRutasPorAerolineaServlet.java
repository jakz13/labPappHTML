package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtRutaVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/rutas")
public class ListarRutasPorAerolineaServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String aerolinea = request.getParameter("aerolinea");
        ISistema sistema = Fabrica.getInstance().getISistema();
        sistema.cargarDesdeBd();
        List<DtRutaVuelo> rutas = sistema.listarRutasPorAerolinea(aerolinea);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("[");
        for (int i = 0; i < rutas.size(); i++) {
            DtRutaVuelo r = rutas.get(i);
            out.print("{\"nombre\":\"" + r.getNombre() + "\"}");
            if (i < rutas.size() - 1) out.print(",");
        }
        out.print("]");
    }
}

package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtRutaVuelo;
import Logica.RutaVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.List;
// src/main/java/com/example/servlets/ListarRutasServlet.java
@WebServlet("/listarRutas")
public class ListarRutasServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            // Cambia aquí el nickname
            sistema.cargarDesdeBd();
            List<DtRutaVuelo> rutas = sistema.listarRutasPorAerolinea("latam001");
            out.print("[");
            for (int i = 0; i < rutas.size(); i++) {
                DtRutaVuelo r = rutas.get(i);
                out.print("{\"nombre\":\"" + r.getNombre() + "\",\"descripcion\":\"" + r.getDescripcion() + "\"}");
                if (i < rutas.size() - 1) out.print(",");
            }
            out.print("]");
        } catch (Exception e) {
            out.print("[]");
        }
    }
}

package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtAerolinea;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/aerolineas")
public class ListarAerolineasServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ISistema sistema = Fabrica.getInstance().getISistema();
        sistema.cargarDesdeBd();
        List<DtAerolinea> aerolineas = sistema.listarAerolineas();

        System.out.println("Aerolineas extraÃdas de la BD:");
        for (DtAerolinea a : aerolineas) {
            System.out.println("Nickname: " + a.getNickname() + ", Nombre: " + a.getNombre());
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("[");
        for (int i = 0; i < aerolineas.size(); i++) {
            DtAerolinea a = aerolineas.get(i);
            out.print("{\"nickname\":\"" + a.getNickname() + "\",\"nombre\":\"" + a.getNombre() + "\"}");
            if (i < aerolineas.size() - 1) out.print(",");
        }
        out.print("]");
    }
}
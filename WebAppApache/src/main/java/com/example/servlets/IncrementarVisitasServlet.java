package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/IncrementarVisitas")
public class IncrementarVisitasServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombreRuta = request.getParameter("nombreRuta");

        System.out.println("👀 [INCREMENTAR_VISITAS] Solicitado para: " + nombreRuta);

        if (nombreRuta != null && !nombreRuta.trim().isEmpty()) {
            try {
                ISistema sistema = Fabrica.getInstance().getISistema();
                sistema.cargarDesdeBd();

                sistema.incrementarVisitasRuta(nombreRuta);
                System.out.println("✅ [INCREMENTAR_VISITAS] Visitas incrementadas para: " + nombreRuta);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print("OK");

            } catch (Exception e) {
                System.err.println("❌ [INCREMENTAR_VISITAS] Error: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print("ERROR");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("BAD_REQUEST");
        }
    }
}
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtRutaVuelo;
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

            // Priorizar aerolinea en sesión si existe
            HttpSession session = request.getSession(false);
            String nombreAerolinea = null;
            if (session != null) {
                Object tipo = session.getAttribute("tipoUsuario");
                Object usuario = session.getAttribute("usuario");
                if (usuario != null && "aerolinea".equalsIgnoreCase(String.valueOf(tipo))) {
                    nombreAerolinea = String.valueOf(usuario);
                }
            }

            // Fallback: permitir pasar ?aerolinea=XXX (por ejemplo tras registro antes de que la cookie esté activa)
            if (nombreAerolinea == null || nombreAerolinea.trim().isEmpty()) {
                String param = request.getParameter("aerolinea");
                if (param != null && !param.trim().isEmpty()) {
                    nombreAerolinea = param.trim();
                }
            }

            // Si no hay aerolínea disponible, devolver lista vacía
            if (nombreAerolinea == null || nombreAerolinea.trim().isEmpty()) {
                out.print("[]");
                return;
            }

            sistema.cargarDesdeBd();
            List<DtRutaVuelo> rutas = sistema.listarRutasPorAerolinea(nombreAerolinea);
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

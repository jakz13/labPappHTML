// src/main/java/com/example/servlets/VerificarPermisosVueloServlet.java
package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtReserva;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/verificar-permisos-vuelo")
public class VerificarPermisosVueloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreVuelo = request.getParameter("nombreVuelo");
        String aerolineaSeleccionada = request.getParameter("aerolinea");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);

        try {
            if (session != null) {
                String usuarioNickname = (String) session.getAttribute("usuario");
                String tipoUsuario = (String) session.getAttribute("tipoUsuario");

                if (usuarioNickname != null && tipoUsuario != null) {
                    ISistema sistema = Fabrica.getInstance().getISistema();
                    sistema.cargarDesdeBd();

                    // Verificar si es aerolínea dueña del vuelo
                    boolean esAerolineaDueña = false;
                    boolean tieneReservaCliente = false;
                    String idReservaCliente = null;

                    if ("aerolinea".equals(tipoUsuario) && aerolineaSeleccionada != null) {
                        esAerolineaDueña = usuarioNickname.equals(aerolineaSeleccionada);
                    } else if ("cliente".equals(tipoUsuario) && nombreVuelo != null) {
                        // Verificar si el cliente tiene reserva en este vuelo usando getReservasCliente
                        List<DtReserva> reservasCliente = sistema.getReservasCliente(usuarioNickname);
                        for (DtReserva reserva : reservasCliente) {
                            if (String.valueOf(reserva.getVuelo()).equals(String.valueOf(nombreVuelo))) {
                                tieneReservaCliente = true;
                                idReservaCliente = String.valueOf(reserva.getId());
                                break;
                            }
                        }
                    }

                    out.print("{");
                    out.print("\"autenticado\": true,");
                    out.print("\"tipoUsuario\": \"" + escapeJson(tipoUsuario) + "\",");
                    out.print("\"esAerolineaDueña\": " + esAerolineaDueña + ",");
                    out.print("\"tieneReservaCliente\": " + tieneReservaCliente + ",");
                    out.print("\"idReservaCliente\": \"" + (idReservaCliente != null ? escapeJson(String.valueOf(idReservaCliente)) : "") + "\"");
                    out.print("}");
                    return;
                }
            }

            // Usuario no autenticado
            out.print("{\"autenticado\": false}");

        } catch (Exception e) {
            out.print("{\"autenticado\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
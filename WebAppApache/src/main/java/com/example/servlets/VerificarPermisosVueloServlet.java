// src/main/java/com/example/servlets/VerificarPermisosVueloServlet.java
package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import serviciosweb.JuanViajesWS;
import serviciosweb.DtReserva;
import com.example.util.PortUtils;

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
                    JuanViajesWS port = PortUtils.getPort(request);


                    boolean esAerolineaDueña = false;
                    boolean tieneReservaCliente = false;
                    String idReservaCliente = null;

                    if ("aerolinea".equalsIgnoreCase(tipoUsuario) && aerolineaSeleccionada != null) {
                        esAerolineaDueña = usuarioNickname.equalsIgnoreCase(aerolineaSeleccionada);
                    } else if ("cliente".equalsIgnoreCase(tipoUsuario) && nombreVuelo != null) {
                        List<DtReserva> reservasCliente = null;
                        try { reservasCliente = port.getReservasCliente(usuarioNickname); } catch (Exception ex) { reservasCliente = null; }
                        if (reservasCliente != null) {
                            for (DtReserva reserva : reservasCliente) {
                                if (reserva != null && reserva.getVuelo() != null && reserva.getVuelo().equalsIgnoreCase(nombreVuelo)) {
                                    tieneReservaCliente = true;
                                    idReservaCliente = reserva.getId() != null ? String.valueOf(reserva.getId()) : null;
                                    break;
                                }
                            }
                        }
                    }

                    out.print("{");
                    out.print("\"autenticado\": true,");
                    out.print("\"tipoUsuario\": \"" + escapeJson(tipoUsuario) + "\",");
                    out.print("\"esAerolineaDue\": " + esAerolineaDueña + ",");
                    out.print("\"tieneReservaCliente\": " + tieneReservaCliente + ",");
                    out.print("\"idReservaCliente\": \"" + (idReservaCliente != null ? escapeJson(idReservaCliente) : "") + "\"");
                    out.print("}");
                    return;
                }
            }

            out.print("{\"autenticado\": false}");

        } catch (Exception e) {
            out.print("{\"autenticado\": false, \"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
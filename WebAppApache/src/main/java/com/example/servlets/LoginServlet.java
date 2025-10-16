package com.example.servlets;

import DataTypes.DtAerolinea;
import DataTypes.DtCliente;
import Logica.Fabrica;
import Logica.ISistema;
import Logica.Cliente;
import Logica.Aerolinea;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.List;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String user = request.getParameter("nickname");
        String password = request.getParameter("password"); // No se usa por ahora

        System.out.println("=== INICIO PROCESO LOGIN ===");
        System.out.println("Usuario recibido: " + user);

        ISistema sistema = Fabrica.getInstance().getISistema();

        try {
            // Cargar datos desde BD
            sistema.cargarDesdeBd();

            Cliente cliente = null;
            Aerolinea aerolinea = null;

            // Buscar cliente SOLO por nickname
            List<DtCliente> clientes = sistema.listarClientes();
            System.out.println("Clientes encontrados: " + clientes.size());

            for (DtCliente c : clientes) {
                System.out.println("Cliente disponible: " + c.getNickname());
                if (c.getNickname().equalsIgnoreCase(user)) {
                    try {
                        cliente = sistema.verInfoCliente(c.getNickname());
                        System.out.println("Cliente encontrado: " + c.getNickname());
                        break;
                    } catch (Exception e) {
                        System.err.println("Error obteniendo info cliente: " + e.getMessage());
                    }
                }
            }

            // Buscar aerolínea SOLO por nickname
            if (cliente == null) {
                List<DtAerolinea> aerolineas = sistema.listarAerolineas();
                System.out.println("Aerolíneas encontradas: " + aerolineas.size());

                for (DtAerolinea a : aerolineas) {
                    System.out.println("Aerolínea disponible: " + a.getNickname());
                    if (a.getNickname().equalsIgnoreCase(user)) {
                        try {
                            aerolinea = sistema.verInfoAerolinea(a.getNickname());
                            System.out.println("Aerolínea encontrada: " + a.getNickname());
                            break;
                        } catch (Exception e) {
                            System.err.println("Error obteniendo info aerolínea: " + e.getMessage());
                        }
                    }
                }
            }

            // Crear respuesta
            if (cliente != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", cliente.getNickname());
                session.setAttribute("tipoUsuario", "cliente");

                System.out.println("LOGIN EXITOSO - Cliente: " + cliente.getNickname());

                String jsonResponse = "{\"success\":true,\"nickname\":\"" +
                        escapeJson(cliente.getNickname()) + "\",\"tipo\":\"cliente\"}";
                out.print(jsonResponse);

            } else if (aerolinea != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", aerolinea.getNickname());
                session.setAttribute("tipoUsuario", "aerolinea");

                System.out.println("LOGIN EXITOSO - Aerolinea: " + aerolinea.getNickname());

                String jsonResponse = "{\"success\":true,\"nickname\":\"" +
                        escapeJson(aerolinea.getNickname()) + "\",\"tipo\":\"aerolinea\"}";
                out.print(jsonResponse);

            } else {
                System.out.println("LOGIN FALLIDO - Usuario no encontrado: " + user);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false,\"error\":\"Usuario no encontrado\"}");
            }

        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO en login: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"error\":\"Error interno del servidor - " +
                    e.getMessage().replace("\"", "'") + "\"}");
        }

        System.out.println("=== FIN PROCESO LOGIN ===");
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
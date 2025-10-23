package com.example.servlets;

import DataTypes.DtAerolinea;
import DataTypes.DtCliente;
import logica.Fabrica;
import logica.ISistema;
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

        // El parámetro 'nickname' puede contener el nickname o el email (gmail)
        String user = request.getParameter("nickname");
        String password = request.getParameter("password"); // No se usa por ahora

        System.out.println("=== INICIO PROCESO LOGIN ===");
        System.out.println("Usuario recibido: " + user);

        ISistema sistema = Fabrica.getInstance().getISistema();

        try {
            // Cargar datos desde BD
            sistema.cargarDesdeBd();

            DtCliente cliente = null;
            DtAerolinea aerolinea = null;

            // Buscar cliente por nickname o por email y verificar contraseña
            List<DtCliente> clientes = sistema.listarClientes();
            System.out.println("Clientes encontrados: " + clientes.size());
            for (DtCliente c : clientes) {
                try {
                    String nick = c.getNickname();
                    String mail = c.getEmail();
                    boolean match = false;
                    if (nick != null && nick.equalsIgnoreCase(user)) match = true;
                    if (mail != null && mail.equalsIgnoreCase(user)) match = true;
                    if (!match) continue;

                    // verificar contraseña usando el email (clave en el sistema)
                    boolean ok = false;
                    try { ok = sistema.verificarLogin(mail, password); } catch (Exception ex) { ok = false; }
                    if (!ok) {
                        System.out.println("Contraseña incorrecta para cliente: " + nick + " (identificador: " + user + ")");
                        // no autenticado, continuar buscando (por seguridad no revelamos si nickname/email existe)
                        continue;
                    }

                    cliente = sistema.obtenerCliente(nick);
                    System.out.println("Cliente autenticado: " + nick);
                    break;
                } catch (Exception e) {
                    System.err.println("Error obteniendo info cliente: " + e.getMessage());
                }
            }

            // Buscar aerolínea por nickname o email y verificar contraseña
            if (cliente == null) {
                List<DtAerolinea> aerolineas = sistema.listarAerolineas();
                System.out.println("Aerolíneas encontradas: " + aerolineas.size());
                for (DtAerolinea a : aerolineas) {
                    try {
                        String nick = a.getNickname();
                        String mail = a.getEmail();
                        boolean match = false;
                        if (nick != null && nick.equalsIgnoreCase(user)) match = true;
                        if (mail != null && mail.equalsIgnoreCase(user)) match = true;
                        if (!match) continue;

                        boolean ok = false;
                        try { ok = sistema.verificarLogin(mail, password); } catch (Exception ex) { ok = false; }
                        if (!ok) {
                            System.out.println("Contraseña incorrecta para aerolínea: " + nick + " (identificador: " + user + ")");
                            continue;
                        }

                        aerolinea = sistema.obtenerAerolinea(nick);
                        System.out.println("Aerolínea autenticada: " + nick);
                        break;
                    } catch (Exception e) {
                        System.err.println("Error obteniendo info aerolínea: " + e.getMessage());
                    }
                }
            }

            // Crear respuesta
            if (cliente != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", cliente.getNickname());
                session.setAttribute("tipoUsuario", "cliente");
                // Mantener compatibilidad con JSPs que usan 'tipo'
                session.setAttribute("tipo", "cliente");

                System.out.println("LOGIN EXITOSO - Cliente: " + cliente.getNickname());

                String jsonResponse = "{\"success\":true,\"nickname\":\"" +
                        escapeJson(cliente.getNickname()) + "\",\"tipo\":\"cliente\"}";
                out.print(jsonResponse);

            } else if (aerolinea != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", aerolinea.getNickname());
                session.setAttribute("tipoUsuario", "aerolinea");
                // Mantener compatibilidad con JSPs que usan 'tipo'
                session.setAttribute("tipo", "aerolinea");

                System.out.println("LOGIN EXITOSO - Aerolinea: " + aerolinea.getNickname());

                String jsonResponse = "{\"success\":true,\"nickname\":\"" +
                        escapeJson(aerolinea.getNickname()) + "\",\"tipo\":\"aerolinea\"}";
                out.print(jsonResponse);

            } else {
                System.out.println("LOGIN FALLIDO - Usuario no encontrado o contraseña inválida: " + user);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false,\"error\":\"Usuario no encontrado o credenciales inválidas\"}");
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
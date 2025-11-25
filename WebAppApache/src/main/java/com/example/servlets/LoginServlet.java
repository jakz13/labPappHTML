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
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String user = request.getParameter("nickname");
        String password = request.getParameter("password");

        System.out.println("=== INICIO PROCESO LOGIN ===");
        System.out.println("Usuario: " + user);

        ISistema sistema = Fabrica.getInstance().getISistema();

        try {
            // INTENTAR cargar desde BD, pero si falla continuar sin datos frescos
            try {
                sistema.cargarDesdeBd();
                System.out.println("✓ Datos cargados desde BD exitosamente");
            } catch (Exception dbError) {
                System.err.println("⚠️ Error cargando desde BD: " + dbError.getMessage());
                dbError.printStackTrace();
                // Continuar sin datos frescos - usar datos en memoria
            }

            DtCliente cliente = null;
            DtAerolinea aerolinea = null;

            // Buscar cliente - con manejo robusto de errores
            try {
                List<DtCliente> clientes = sistema.listarClientes();
                System.out.println("Clientes encontrados: " + clientes.size());

                for (DtCliente c : clientes) {
                    try {
                        String nick = c.getNickname();
                        String mail = c.getEmail();

                        boolean match = (nick != null && nick.equalsIgnoreCase(user)) ||
                                (mail != null && mail.equalsIgnoreCase(user));

                        if (!match) continue;

                        // Verificar contraseña
                        String identificadorLogin = mail != null ? mail : user;
                        boolean ok = false;
                        try {
                            ok = sistema.verificarLogin(identificadorLogin, password);
                        } catch (Exception ex) {
                            System.out.println("Error en verificarLogin: " + ex.getMessage());
                            continue;
                        }

                        if (!ok) continue;

                        cliente = c; // Usar el objeto que ya tenemos
                        break;
                    } catch (Exception e) {
                        System.err.println("Error procesando cliente individual: " + e.getMessage());
                        continue;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error listando clientes: " + e.getMessage());
            }

            // Buscar aerolínea
            if (cliente == null) {
                try {
                    List<DtAerolinea> aerolineas = sistema.listarAerolineas();
                    System.out.println("Aerolíneas encontradas: " + aerolineas.size());

                    for (DtAerolinea a : aerolineas) {
                        try {
                            String nick = a.getNickname();
                            String mail = a.getEmail();

                            boolean match = (nick != null && nick.equalsIgnoreCase(user)) ||
                                    (mail != null && mail.equalsIgnoreCase(user));

                            if (!match) continue;

                            String identificadorLogin = mail != null ? mail : user;
                            boolean ok = false;
                            try {
                                ok = sistema.verificarLogin(identificadorLogin, password);
                            } catch (Exception ex) {
                                System.out.println("Error en verificarLogin: " + ex.getMessage());
                                continue;
                            }

                            if (!ok) continue;

                            aerolinea = a;
                            break;
                        } catch (Exception e) {
                            System.err.println("Error procesando aerolínea individual: " + e.getMessage());
                            continue;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error listando aerolíneas: " + e.getMessage());
                }
            }

            // Crear respuesta
            if (cliente != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", cliente.getNickname());
                session.setAttribute("tipoUsuario", "cliente");
                session.setAttribute("tipo", "cliente");

                System.out.println("✓ LOGIN EXITOSO - Cliente: " + cliente.getNickname());

                out.print("{\"success\":true,\"nickname\":\"" +
                        escapeJson(cliente.getNickname()) + "\",\"tipo\":\"cliente\"}");

            } else if (aerolinea != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", aerolinea.getNickname());
                session.setAttribute("tipoUsuario", "aerolinea");
                session.setAttribute("tipo", "aerolinea");

                System.out.println("✓ LOGIN EXITOSO - Aerolínea: " + aerolinea.getNickname());

                out.print("{\"success\":true,\"nickname\":\"" +
                        escapeJson(aerolinea.getNickname()) + "\",\"tipo\":\"aerolinea\"}");

            } else {
                System.out.println("✗ LOGIN FALLIDO - Usuario no encontrado");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false,\"error\":\"Usuario no encontrado o credenciales inválidas\"}");
            }

        } catch (Exception e) {
            System.err.println("💥 ERROR CRÍTICO en login: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"error\":\"Error de conexión con la base de datos\"}");
        }

        System.out.println("=== FIN PROCESO LOGIN ===\n");
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
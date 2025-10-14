package com.example.servlets;

import DataTypes.DtAerolinea;
import DataTypes.DtCliente;
import Logica.Fabrica;
import Logica.ISistema;
import Logica.Cliente;
import Logica.Aerolinea;
import Logica.Usuario;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String user = request.getParameter("user");
        String password = request.getParameter("password");

        ISistema sistema = Fabrica.getInstance().getISistema();

        try {
            // Aquí deberías validar el usuario y la contraseña con tu lógica real
            // Por ejemplo, buscar por nickname o email y comparar la contraseña
            // Este ejemplo asume que tienes métodos para validar clientes y aerolíneas

            Cliente cliente = null;
            Aerolinea aerolinea = null;
// Buscar cliente y mostrar todos en consola
            for (DtCliente c : sistema.listarClientes()) {
                System.out.println("Cliente: nickname=" + c.getNickname() + ", email=" + c.getEmail());
                if ((c.getNickname().equalsIgnoreCase(user) || c.getEmail().equalsIgnoreCase(user))) {
                    cliente = sistema.verInfoCliente(c.getNickname());
                    break;
                }
            }

            // Buscar cliente
            for (DtCliente c : sistema.listarClientes()) {
                if ((c.getNickname().equalsIgnoreCase(user) || c.getEmail().equalsIgnoreCase(user))) {
                    cliente = sistema.verInfoCliente(c.getNickname());
                    break;
                }
            }
            // Buscar aerolínea si no es cliente


            if (cliente != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", cliente.getNickname());
                session.setAttribute("tipoUsuario", "cliente");
                out.print("{\"success\":true,\"nickname\":\"" + cliente.getNickname() + "\",\"tipo\":\"cliente\"}");
            } else if (aerolinea != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", aerolinea.getNickname());
                session.setAttribute("tipoUsuario", "aerolinea");
                out.print("{\"success\":true,\"nickname\":\"" + aerolinea.getNickname() + "\",\"tipo\":\"aerolinea\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false,\"error\":\"Usuario o contraseña incorrectos\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}

package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import Logica.ManejadorCliente; // Asegúrate de que el paquete sea correcto

public class UsuarioServlet extends HttpServlet {
    private ManejadorCliente controlador;

    @Override
    public void init() throws ServletException {
        controlador = ManejadorCliente.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Ejemplo: obtener lista de usuarios
        // List<Usuario> usuarios = controlador.listarUsuarios();
        // req.setAttribute("usuarios", usuarios);
        // req.getRequestDispatcher("/consulta-usuario.jsp").forward(req, resp);
        resp.getWriter().write("Servlet funcionando");
    }
}

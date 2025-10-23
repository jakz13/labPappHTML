// src/main/java/com/example/servlets/ListarCategoriasServlet.java
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtCategoria;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.List;

@WebServlet("/listarCategorias")
public class ListarCategoriasServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            List<DtCategoria> categorias = sistema.listarCategorias();

            System.out.println("Cantidad de categorías encontradas: " + categorias.size());
            for (DtCategoria c : categorias) {
                System.out.println("Categoría: " + c.getNombre());
            }

            out.print("[");
            for (int i = 0; i < categorias.size(); i++) {
                DtCategoria c = categorias.get(i);
                out.print("{\"nombre\":\"" + c.getNombre() + "\"}");
                if (i < categorias.size() - 1) out.print(",");
            }
            out.print("]");

        } catch (Exception e) {
            out.print("[]");
        }
    }
}

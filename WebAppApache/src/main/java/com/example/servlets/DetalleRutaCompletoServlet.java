// src/main/java/com/example/servlets/DetalleRutaCompletoServlet.java
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtRutaVuelo;
import Logica.RutaVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/detalle-ruta")
public class DetalleRutaCompletoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreRuta = request.getParameter("nombreRuta");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            // Obtener información completa de la ruta
            RutaVuelo rutaa = sistema.obtenerRuta(nombreRuta);
            DtRutaVuelo ruta = rutaa.getDtRutaVuelo();

            if (ruta != null) {
                out.print("{");
                out.print("\"success\": true,");
                out.print("\"ruta\": {");
                out.print("\"nombre\": \"" + escapeJson(ruta.getNombre()) + "\",");
                out.print("\"descripcion\": \"" + escapeJson(ruta.getDescripcion()) + "\",");
                out.print("\"ciudadOrigen\": \"" + escapeJson(ruta.getCiudadOrigen()) + "\",");
                out.print("\"ciudadDestino\": \"" + escapeJson(ruta.getCiudadDestino()) + "\",");
                out.print("\"hora\": \"" + escapeJson(ruta.getHora()) + "\",");
                out.print("\"costoTurista\": " + ruta.getCostoTurista() + ",");
                out.print("\"costoEjecutivo\": " + ruta.getCostoEjecutivo() + ",");
                out.print("\"costoEquipaje\": " + ruta.getCostoEquipajeExtra() + ",");
                out.print("\"fechaAlta\": \"" + escapeJson(ruta.getFechaAlta() != null ? ruta.getFechaAlta().toString() : "") + "\",");
                out.print("\"aerolineaNombre\": \"" + escapeJson(ruta.getAerolinea()) + "\",");
                out.print("\"categorias\": \"" + escapeJson(String.join(", ", ruta.getCategorias())) + "\"");
                out.print("}");
                out.print("}");
            } else {
                out.print("{\"success\": false, \"error\": \"Ruta no encontrada\"}");
            }

        } catch (Exception e) {
            out.print("{\"success\": false, \"error\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
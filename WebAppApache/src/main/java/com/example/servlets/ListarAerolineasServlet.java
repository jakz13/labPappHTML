// ListarAerolineasServlet.java
package com.example.servlets;

import com.example.util.PortUtils;
import serviciosweb.DtAerolinea;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/aerolineas")
public class ListarAerolineasServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        System.out.println("=== ListarAerolineasServlet INICIADO ===");

        try {
            // Obtener el port usando PortUtils
            System.out.println("Obteniendo port...");
            serviciosweb.JuanViajesWS port = PortUtils.getPort(request);
            System.out.println("Port obtenido correctamente");

            // Cargar datos desde BD
            System.out.println("Llamando a port.listarAerolineas()...");
            List<DtAerolinea> aerolineas = port.listarAerolineas();
            System.out.println("Aerolineas obtenidas. Cantidad: " + (aerolineas != null ? aerolineas.size() : "null"));

            if (aerolineas == null) {
                System.out.println("Aerolineas es null, retornando array vacío");
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("[]");
                return;
            }

            // DEBUG: Imprimir cada aerolínea
            System.out.println("=== LISTA DE AEROLÍNEAS ===");
            for (DtAerolinea a : aerolineas) {
                System.out.println("Nickname: " + a.getNickname() + ", Nombre: " + a.getNombre());
            }
            System.out.println("============================");

            // Construir JSON manualmente
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < aerolineas.size(); i++) {
                DtAerolinea a = aerolineas.get(i);
                json.append("{")
                        .append("\"nickname\":\"").append(escapeJson(a.getNickname())).append("\",")
                        .append("\"nombre\":\"").append(escapeJson(a.getNombre())).append("\"")
                        .append("}");
                if (i < aerolineas.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");

            out.print(json.toString());
            System.out.println("=== ListarAerolineasServlet COMPLETADO EXITOSAMENTE ===");

        } catch (Exception e) {
            System.err.println("=== ERROR en ListarAerolineasServlet ===");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "null"));
            e.printStackTrace();
            System.err.println("=========================================");

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno del servidor: " + escapeJson(e.getMessage()) + "\"}");
        }
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
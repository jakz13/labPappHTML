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
        try {
            // Obtener el port usando PortUtils
            serviciosweb.JuanViajesWS port = PortUtils.getPort(request);

            // Cargar datos desde BD
            List<DtAerolinea> aerolineas = port.listarAerolineas();

            // DEBUG: Imprimir en consola del servidor
            System.out.println("Aerolineas extraídas del WS:");
            for (DtAerolinea a : aerolineas) {
                System.out.println("Nickname: " + a.getNickname() + ", Nombre: " + a.getNombre());
            }

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("[");
            for (int i = 0; i < aerolineas.size(); i++) {
                DtAerolinea a = aerolineas.get(i);
                out.print("{\"nickname\":\"" + a.getNickname() + "\",\"nombre\":\"" + a.getNombre() + "\"}");
                if (i < aerolineas.size() - 1) out.print(",");
            }
            out.print("]");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"Error al obtener aerolíneas: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}
package com.example.servlets;

import com.example.util.PortUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import serviciosweb.DtCiudad;
import serviciosweb.JuanViajesWS;

@WebServlet("/listarCiudades")
public class ListarCiudadesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Obtener el port usando PortUtils
            JuanViajesWS port = PortUtils.getPort(request);

            // Llamar al método listarCiudades del servicio web
            List<DtCiudad> ciudades = port.listarCiudades();
            List<String> ciudadesNombres = new ArrayList<>();

            System.out.println("ListarCiudadesServlet: ciudades leídas desde servicio web. count=" + ciudades.size());

            for (DtCiudad ciudad : ciudades) {
                String nombre = "";
                if (ciudad != null) {
                    nombre = ciudad.getNombre() != null ? ciudad.getNombre() : "";
                    System.out.println(" - " + nombre);
                }
                ciudadesNombres.add(nombre);
            }

            // Construir JSON array simple y enviarlo
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < ciudadesNombres.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(ciudadesNombres.get(i).replace("\"","\\\"")).append("\"");
            }
            sb.append("]");
            out.print(sb.toString());

        } catch (Exception e) {
            System.err.println("ListarCiudadesServlet: error al listar ciudades: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("[]");
        }
    }
}
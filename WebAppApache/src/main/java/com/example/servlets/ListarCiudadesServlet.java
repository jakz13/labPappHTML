// java
package com.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;
import com.example.util.PortUtils;

@WebServlet("/listarCiudades")
public class ListarCiudadesServlet extends HttpServlet {
    // Usar PortUtils para obtener el puerto del servicio web

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Usar el port del servicio web en lugar de la lógica local
            JuanViajesWS port = PortUtils.getPort(request);

            // Intentar cargar desde BD para tener datos en el servicio
            try { port.cargarDesdeBd(); } catch (Exception ignored) {}

            List<String> ciudadesNombres = new ArrayList<>();
            Object raw = null;
            try {
                raw = port.listarCiudades();
            } catch (Exception ex) {
                // Si falla el WS, devolver lista vacía en lugar de depender de la lógica local
                raw = java.util.Collections.emptyList();
            }

            if (raw instanceof List) {
                List<?> rawList = (List<?>) raw;
                for (Object elem : rawList) {
                    String nombre = null;
                    if (elem == null) {
                        nombre = "";
                    } else if (elem instanceof String) {
                        nombre = (String) elem;
                    } else {
                        // intentar obtener un campo nombre vía método getNombre()
                        try {
                            java.lang.reflect.Method getter = elem.getClass().getMethod("getNombre");
                            Object val = getter.invoke(elem);
                            nombre = val != null ? String.valueOf(val) : "";
                        } catch (Exception ex) {
                            // fallback a toString()
                            nombre = elem.toString();
                        }
                    }
                    ciudadesNombres.add(nombre);
                }
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
        } catch (java.util.ConcurrentModificationException cme) {
            System.err.println("ListarCiudadesServlet: ConcurrentModificationException al listar ciudades: " + cme.getMessage());
            cme.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("[]");
        } catch (Exception e) {
            System.err.println("ListarCiudadesServlet: error al listar ciudades: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("[]");
        }
    }
}

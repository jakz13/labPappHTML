// java
package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/listarCiudades")
public class ListarCiudadesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();

            // No llamar a sistema.cargarDesdeBd() aquí (se ejecuta en contextInitialized).
            // Intentar invocar listarCiudades() o, si no existe, intentar getCiudades()
            List<String> ciudadesNombres = new ArrayList<>();
            Object raw = null;
            try {
                Method m = sistema.getClass().getMethod("listarCiudades");
                raw = m.invoke(sistema);
            } catch (NoSuchMethodException nsme) {
                try {
                    Method m2 = sistema.getClass().getMethod("getCiudades");
                    raw = m2.invoke(sistema);
                } catch (NoSuchMethodException ignored) {
                    System.out.println("ListarCiudadesServlet: no existe listarCiudades() ni getCiudades() en la implementación de ISistema.");
                }
            }

            if (raw instanceof List) {
                List<?> rawList = (List<?>) raw;
                System.out.println("ListarCiudadesServlet: ciudades leídas desde la lógica. count=" + rawList.size());
                for (Object elem : rawList) {
                    String nombre = null;
                    if (elem == null) {
                        nombre = "";
                    } else if (elem instanceof String) {
                        nombre = (String) elem;
                    } else {
                        // intentar obtener un campo nombre vía método getNombre()
                        try {
                            Method getter = elem.getClass().getMethod("getNombre");
                            Object val = getter.invoke(elem);
                            nombre = val != null ? String.valueOf(val) : "";
                        } catch (Exception ex) {
                            // fallback a toString()
                            nombre = elem.toString();
                        }
                    }
                    System.out.println(" - " + nombre);
                    ciudadesNombres.add(nombre);
                }
            } else {
                System.out.println("ListarCiudadesServlet: resultado de listar/getCiudades es nulo o no es lista -> enviando []");
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

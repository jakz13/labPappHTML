// src/main/java/com/example/servlets/ContadorVisitasRutas.java
package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet("/contadorVisitas/*")
public class ContadorVisitasRutas extends HttpServlet {

    private static final Map<String, AtomicInteger> contadorVisitas = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        System.out.println("[CONTADOR] Path info recibido: " + pathInfo);

        // ✅ NUEVO: Endpoint para incrementar desde frontend
        if (pathInfo != null && pathInfo.equals("/incrementar")) {
            String nombreRuta = request.getParameter("ruta");
            System.out.println("[CONTADOR] Incrementar solicitud para ruta: " + nombreRuta);

            if (nombreRuta != null && !nombreRuta.trim().isEmpty()) {
                int nuevoTotal = incrementarVisitaRuta(nombreRuta.trim());

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print("{\"ruta\":\"" + nombreRuta + "\", \"total\":" + nuevoTotal + "}");
                return;
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("{\"error\":\"Parámetro 'ruta' requerido\"}");
                return;
            }
        }

        // ✅ Endpoint original para obtener estadísticas (compatibilidad)
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Integer> top5 = getTop5RutasMasVisitadas();
        java.io.PrintWriter out = response.getWriter();
        out.print("{");

        int count = 0;
        for (Map.Entry<String, Integer> entry : top5.entrySet()) {
            if (count > 0) out.print(",");
            out.print("\"" + entry.getKey() + "\":" + entry.getValue());
            count++;
        }
        out.print("}");
    }

    // ✅ Método sincronizado para incrementar (modificado)
    public static synchronized int incrementarVisitaRuta(String nombreRuta) {
        if (nombreRuta != null && !nombreRuta.trim().isEmpty()) {
            String rutaKey = nombreRuta.trim();
            AtomicInteger contador = contadorVisitas.computeIfAbsent(rutaKey, k -> new AtomicInteger(0));
            int nuevoValor = contador.incrementAndGet();
            System.out.println("[CONTADOR] ✅ Visita contada para ruta: " + rutaKey + " - Total: " + nuevoValor);
            return nuevoValor;
        }
        return 0;
    }

    // Método para obtener las rutas más visitadas (sin cambios)
    public static Map<String, Integer> getTop5RutasMasVisitadas() {
        Map<String, Integer> result = new java.util.LinkedHashMap<>();

        contadorVisitas.entrySet().stream()
                .sorted(Map.Entry.<String, AtomicInteger>comparingByValue(
                        (a, b) -> Integer.compare(b.get(), a.get())
                ))
                .limit(5)
                .forEachOrdered(entry ->
                        result.put(entry.getKey(), entry.getValue().get())
                );

        System.out.println("[CONTADOR] Top 5 rutas: " + result);
        return result;
    }
}
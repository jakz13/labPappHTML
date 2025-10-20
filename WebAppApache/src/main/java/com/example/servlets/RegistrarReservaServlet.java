package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import Logica.Pasajero;
import Logica.TipoAsiento;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

@WebServlet("/api/reservas")
public class RegistrarReservaServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Leer JSON del body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            String body = sb.toString();
            System.out.println("=== comienza reserva ===");
            JSONObject obj = new JSONObject(body);

            String vuelo = obj.getString("vuelo");
            String tipoAsientoStr = obj.getString("tipoAsiento");
            int cantidadPasajes = obj.getInt("cantidadPasajes");
            int equipajeExtra = obj.getInt("equipajeExtra");

            // Obtener usuario de la sesión
            HttpSession session = request.getSession(false);
            String nicknameCliente = (session != null) ? (String) session.getAttribute("usuario") : null;
            if (nicknameCliente == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false, \"error\":\"Usuario no autenticado\"}");
                return;
            }
            System.out.println("encontrando en la sesion");

            System.out.println(nicknameCliente+"encontrado en la sesion");
            // Pasajeros (opcional)
            List<Pasajero> pasajeros = new ArrayList<>();
            if (obj.has("pasajeros")) {
                JSONArray pasajerosArr = obj.getJSONArray("pasajeros");
                for (int i = 0; i < pasajerosArr.length(); i++) {
                    JSONObject p = pasajerosArr.getJSONObject(i);
                    pasajeros.add(Fabrica.getInstance().getISistema().crearPasajero(
                            p.getString("nombre"), p.getString("apellido")
                    ));
                }
            }
            System.out.println("Pasajeros procesados: " + pasajeros.size());

            TipoAsiento tipoAsiento = tipoAsientoStr.equalsIgnoreCase("ejecutivo") ? TipoAsiento.EJECUTIVO : TipoAsiento.TURISTA;
            LocalDate fechaReserva = LocalDate.now();

            ISistema sistema = Fabrica.getInstance().getISistema();
            double costo = sistema.calcularCostoReserva(vuelo, tipoAsiento, cantidadPasajes, equipajeExtra);

            sistema.crearYRegistrarReserva(
                    nicknameCliente, vuelo, fechaReserva, costo, tipoAsiento, cantidadPasajes, equipajeExtra, pasajeros
            );
            System.out.println("=== FIN PROCESO DE COMPRA ===");
            out.print("{\"success\":true, \"codigoReserva\":\"RES-" + System.currentTimeMillis() + "\"}");
        } catch (Exception e) {
            // Log completo para debugging
            System.err.println("Error en RegistrarReservaServlet: " + e.getMessage());
            e.printStackTrace(System.err);

            // Obtener causa raíz
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();
            String causa = root.getClass().getSimpleName() + ": " + (root.getMessage() != null ? root.getMessage() : "(sin mensaje)");

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // Devolver mensaje útil para debugging sin exponer stacktrace completo al cliente
            out.print("{\"success\":false, \"error\":\"Error al agregar la reserva: " + escapeForJson(causa) + "\"}");
        }
    }

    private static String escapeForJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = request.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
}
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

            TipoAsiento tipoAsiento = tipoAsientoStr.equalsIgnoreCase("ejecutivo") ? TipoAsiento.EJECUTIVO : TipoAsiento.TURISTA;
            LocalDate fechaReserva = LocalDate.now();

            ISistema sistema = Fabrica.getInstance().getISistema();
            double costo = sistema.calcularCostoReserva(vuelo, tipoAsiento, cantidadPasajes, equipajeExtra);

            sistema.crearYRegistrarReserva(
                    nicknameCliente, vuelo, fechaReserva, costo, tipoAsiento, cantidadPasajes, equipajeExtra, pasajeros
            );

            out.print("{\"success\":true, \"codigoReserva\":\"RES-" + System.currentTimeMillis() + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false, \"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
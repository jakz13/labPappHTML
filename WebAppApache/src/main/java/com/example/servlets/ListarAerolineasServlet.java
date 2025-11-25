package com.example.servlets;


import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import serviciosweb.JuanViajesWS;
import com.example.util.PortUtils;
import serviciosweb.DtAerolinea;
@WebServlet("/api/aerolineas")
public class ListarAerolineasServlet extends HttpServlet {
    // Usar PortUtils para obtener el puerto del servicio web

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<DtAerolinea> aerolineas = null;
        JuanViajesWS port = PortUtils.getPort(request);
        try {
            // Asegurar que el servicio tenga datos
            try { port.cargarDesdeBd(); } catch (Exception ignored) {}

            aerolineas = port.listarAerolineas();
        } catch (Exception e) {
            // Si falla el WS, registrar traza completa y devolver lista vacía
            System.err.println("ListarAerolineasServlet: error llamando al WS listarAerolineas: " + (e == null ? "(null)" : e.getClass().getName() + ": " + e.getMessage()));
            if (e != null) e.printStackTrace();
            // Enviar un encabezado breve para facilitar diagnóstico desde el cliente (no exponer detalles sensibles)
            try {
                String hdr = e == null ? "unknown" : (e.getClass().getSimpleName() + ": " + (e.getMessage() != null ? e.getMessage().replaceAll("[\n\r]"," ") : "(no message)"));
                if (hdr.length() > 200) hdr = hdr.substring(0,200);
                response.setHeader("X-Backend-Error", hdr);
            } catch (Exception ignore) {}
            aerolineas = java.util.Collections.emptyList();
        }

        if (aerolineas == null) aerolineas = java.util.Collections.emptyList();

        // DEBUG: Imprimir en consola del servidor
        System.out.println("Aerolineas extraídas:");
        for (DtAerolinea a : aerolineas) {
            try { System.out.println("Nickname: " + a.getNickname() + ", Nombre: " + a.getNombre()); } catch (Throwable ignore) {}
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("[");
        for (int i = 0; i < aerolineas.size(); i++) {
            DtAerolinea a = aerolineas.get(i);
            String nick = a.getNickname() != null ? a.getNickname() : "";
            String nombre = a.getNombre() != null ? a.getNombre() : "";
            out.print("{\"nickname\":\"" + nick + "\",\"nombre\":\"" + nombre + "\"}");
            if (i < aerolineas.size() - 1) out.print(",");
        }
        out.print("]");
    }
}
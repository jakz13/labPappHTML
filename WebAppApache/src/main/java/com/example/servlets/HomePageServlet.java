// src/main/java/com/example/servlets/HomePageServlet.java
package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import serviciosweb.JuanViajesWS;
import serviciosweb.DtRutaVuelo;
import serviciosweb.DtPaquete;
import serviciosweb.DtAerolinea;
import com.example.util.PortUtils;
import serviciosweb.EstadoRuta;

@WebServlet("/homeData")
public class HomePageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            JuanViajesWS port = PortUtils.getPort(request);
            try { port.cargarDesdeBd(); } catch (Exception ignored) {}

            List<DtRutaVuelo> rutasDestacadas = new ArrayList<>();

            List<DtAerolinea> aerolineas = null;
            try { aerolineas = port.listarAerolineas(); } catch (Exception e) { aerolineas = new ArrayList<>(); }
            if (aerolineas == null) aerolineas = new ArrayList<>();

            for (DtAerolinea aerolinea : aerolineas) {
                try {
                    List<serviciosweb.DtRutaVuelo> rutasAerolinea = port.listarRutasPorAerolinea(aerolinea.getNickname());
                    if (rutasAerolinea == null) continue;
                    for (DtRutaVuelo ruta : rutasAerolinea) {
                        if (ruta.getEstado() != null && "CONFIRMADA".equalsIgnoreCase(String.valueOf(ruta.getEstado()))) {
                            rutasDestacadas.add(ruta);
                            if (rutasDestacadas.size() >= 3) break;
                        }
                    }
                    if (rutasDestacadas.size() >= 3) break;
                } catch (Exception e) {
                    continue;
                }
            }

            List<DtPaquete> paquetesDestacados = null;
            try { paquetesDestacados = port.listarPaquetes(); } catch (Exception e) { paquetesDestacados = new ArrayList<>(); }
            if (paquetesDestacados == null) paquetesDestacados = new ArrayList<>();
            paquetesDestacados = paquetesDestacados.stream().filter(paquete -> paquete.getCosto() > 0).collect(Collectors.toList());
            if (paquetesDestacados.size() > 2) paquetesDestacados = paquetesDestacados.subList(0, 2);

            List<DtAerolinea> aerolineasRecomendadas = aerolineas;
            if (aerolineasRecomendadas.size() > 4) aerolineasRecomendadas = aerolineasRecomendadas.subList(0, 4);

            StringBuilder json = new StringBuilder();
            json.append("{");

            json.append("\"rutasDestacadas\":[");
            for (int i = 0; i < rutasDestacadas.size(); i++) {
                DtRutaVuelo ruta = rutasDestacadas.get(i);
                json.append("{");
                json.append("\"nombre\":\"").append(escapeJson(ruta.getNombre())).append("\",");
                json.append("\"descripcion\":\"").append(escapeJson(ruta.getDescripcion())).append("\",");
                json.append("\"descripcionCorta\":\"").append(escapeJson(ruta.getDescripcionCorta())).append("\",");
                json.append("\"aerolinea\":\"").append(escapeJson(ruta.getAerolinea())).append("\",");
                json.append("\"ciudadOrigen\":\"").append(escapeJson(ruta.getCiudadOrigen())).append("\",");
                json.append("\"ciudadDestino\":\"").append(escapeJson(ruta.getCiudadDestino())).append("\",");
                json.append("\"hora\":\"").append(escapeJson(ruta.getHora())).append("\",");
                json.append("\"costoTurista\":").append(ruta.getCostoTurista()).append(",");
                json.append("\"costoEjecutivo\":").append(ruta.getCostoEjecutivo()).append(",");
                json.append("\"estado\":\"").append(escapeJson(String.valueOf(ruta.getEstado()))).append("\",");
                json.append("\"tieneCategorias\":").append(ruta.getCategorias() != null && !ruta.getCategorias().isEmpty());
                json.append("}");
                if (i < rutasDestacadas.size() - 1) json.append(",");
            }
            json.append("],");

            json.append("\"paquetesDestacados\":[");
            for (int i = 0; i < paquetesDestacados.size(); i++) {
                DtPaquete paquete = paquetesDestacados.get(i);
                json.append("{");
                json.append("\"nombre\":\"").append(escapeJson(paquete.getNombre())).append("\",");
                json.append("\"descripcion\":\"").append(escapeJson(paquete.getDescripcion())).append("\",");
                json.append("\"costo\":").append(paquete.getCosto()).append(",");
                json.append("\"descuentoPorc\":").append(paquete.getDescuentoPorc()).append(",");
                json.append("\"periodoValidezDias\":").append(paquete.getPeriodoValidezDias()).append(",");
                json.append("\"cantidadItems\":").append(paquete.getItems() != null ? paquete.getItems().size() : 0);
                json.append("}");
                if (i < paquetesDestacados.size() - 1) json.append(",");
            }
            json.append("],");

            // Aerolíneas recomendadas
            json.append("\"aerolineasRecomendadas\":[");
            for (int i = 0; i < aerolineasRecomendadas.size(); i++) {
                DtAerolinea aerolinea = aerolineasRecomendadas.get(i);
                json.append("{");
                json.append("\"nickname\":\"").append(escapeJson(aerolinea.getNickname())).append("\",");
                json.append("\"nombre\":\"").append(escapeJson(aerolinea.getNombre())).append("\",");
                json.append("\"descripcion\":\"").append(escapeJson(aerolinea.getDescripcion())).append("\",");
                json.append("\"imagenUrl\":\"").append(escapeJson(aerolinea.getImagenUrl())).append("\",");
                json.append("\"email\":\"").append(escapeJson(aerolinea.getEmail())).append("\",");
                json.append("\"sitioWeb\":\"").append(escapeJson(aerolinea.getSitioWeb())).append("\"");
                json.append("}");
                if (i < aerolineasRecomendadas.size() - 1) json.append(",");
            }
            json.append("]");

            json.append("}");

            out.print(json.toString());

        } catch (Exception e) {
            // En caso de error, devolver datos vacíos
            out.print("{\"rutasDestacadas\":[],\"paquetesDestacados\":[],\"aerolineasRecomendadas\":[]}");
            e.printStackTrace();
        }
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
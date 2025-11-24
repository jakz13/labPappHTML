//LISTARRUTASFINALIZABLES

package com.example.servlets;

import DataTypes.DtRutaVuelo;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logica.Fabrica;
import logica.ISistema;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/rutas-finalizables")
public class ListarRutasFinalizablesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("=== ListarRutasFinalizablesServlet INICIADO ===");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Query String: " + request.getQueryString());

        String aerolinea = request.getParameter("aerolinea");
        System.out.println("Aerolínea recibida: " + aerolinea);

        // Validar parámetro requerido
        if (aerolinea == null || aerolinea.trim().isEmpty()) {
            System.out.println("ERROR: Parámetro aerolinea vacío");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\": \"Parámetro 'aerolinea' requerido\"}");
            return;
        }

        ISistema sistema = Fabrica.getInstance().getISistema();
        sistema.cargarDesdeBd();

        try {
            // Obtener rutas finalizables usando el método específico
            List<DtRutaVuelo> rutasFinalizables = sistema.listarRutasFinalizables(aerolinea);

            System.out.println("[DEBUG] Rutas finalizables encontradas: " +
                    (rutasFinalizables != null ? rutasFinalizables.size() : "null"));

            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();

            if (rutasFinalizables == null || rutasFinalizables.isEmpty()) {
                System.out.println("[DEBUG] No hay rutas finalizables, enviando array vacío");
                out.print("[]");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < rutasFinalizables.size(); i++) {
                DtRutaVuelo r = rutasFinalizables.get(i);
                sb.append("{");
                sb.append("\"nombre\":\"").append(escapeJson(r.getNombre())).append("\",");
                sb.append("\"descripcion\":\"").append(escapeJson(r.getDescripcion())).append("\",");
                sb.append("\"origen\":\"").append(escapeJson(r.getCiudadOrigen())).append("\",");
                sb.append("\"destino\":\"").append(escapeJson(r.getCiudadDestino())).append("\",");
                sb.append("\"estado\":\"").append(escapeJson(String.valueOf(r.getEstado()))).append("\",");

                // Categorías como array
                sb.append("\"categorias\":[");
                if (r.getCategorias() != null) {
                    for (int j = 0; j < r.getCategorias().size(); j++) {
                        sb.append("\"").append(escapeJson(r.getCategorias().get(j))).append("\"");
                        if (j < r.getCategorias().size() - 1) sb.append(",");
                    }
                }
                sb.append("],");

                sb.append("\"costoTurista\":").append(r.getCostoTurista()).append(",");
                sb.append("\"costoEjecutivo\":").append(r.getCostoEjecutivo()).append(",");
                sb.append("\"costoEquipaje\":").append(r.getCostoEquipajeExtra()).append(",");

                // Información de vuelos
                sb.append("\"vuelos\":[");
                try {
                    // Intentar obtener información de vuelos
                    List<Object> vuelosInfo = getVuelosInfo(r, request);
                    for (int j = 0; j < vuelosInfo.size(); j++) {
                        sb.append(vuelosInfo.get(j));
                        if (j < vuelosInfo.size() - 1) sb.append(",");
                    }
                } catch (Exception e) {
                    System.out.println("[DEBUG] Error obteniendo vuelos: " + e.getMessage());
                }
                sb.append("]");

                // Procesar imagen
                try {
                    String imagenVal = invokeGetterSafe(r, new String[]{"getImagenUrl", "getImagen", "imagenUrl", "imagen", "getImagenPath", "imagenPath", "url"});
                    if (imagenVal != null && !imagenVal.isBlank()) {
                        String tmp = imagenVal.trim();
                        try {
                            if (!tmp.matches("(?i)^(https?:)?//.*")) {
                                String scheme = request.getScheme();
                                String serverName = request.getServerName();
                                int serverPort = request.getServerPort();
                                String portPart = "";
                                if (!("http".equalsIgnoreCase(scheme) && serverPort == 80) && !("https".equalsIgnoreCase(scheme) && serverPort == 443)) {
                                    portPart = ":" + serverPort;
                                }
                                if (!tmp.startsWith("/")) tmp = "/" + tmp;
                                String absolute = scheme + "://" + serverName + portPart + tmp;
                                imagenVal = absolute;
                            } else {
                                imagenVal = tmp;
                            }
                        } catch (Exception ignore) {
                            imagenVal = tmp;
                        }

                        if (imagenVal != null && !imagenVal.isBlank()) {
                            sb.append(",\"imagenUrl\":\"").append(escapeJson(imagenVal)).append("\"");
                        }
                    }
                } catch (Exception ignore) {}

                // Procesar video
                try {
                    String videoVal = r.getVideoUrl();
                    if (videoVal != null && !videoVal.isBlank()) {
                        String tmp = videoVal.trim();
                        try {
                            if (!tmp.matches("(?i)^(https?:)?//.*")) {
                                String scheme = request.getScheme();
                                String serverName = request.getServerName();
                                int serverPort = request.getServerPort();
                                String portPart = "";
                                if (!("http".equalsIgnoreCase(scheme) && serverPort == 80) && !("https".equalsIgnoreCase(scheme) && serverPort == 443)) {
                                    portPart = ":" + serverPort;
                                }
                                if (!tmp.startsWith("/")) tmp = "/" + tmp;
                                String absolute = scheme + "://" + serverName + portPart + tmp;
                                videoVal = absolute;
                            } else {
                                videoVal = tmp;
                            }
                        } catch (Exception ignore) {
                            videoVal = tmp;
                        }

                        if (videoVal != null && !videoVal.isBlank()) {
                            sb.append(",\"videoUrl\":\"").append(escapeJson(videoVal)).append("\"");
                        }
                    }
                } catch (Exception ignore) {}

                sb.append("}");
                if (i < rutasFinalizables.size() - 1) sb.append(",");
            }
            sb.append("]");
            out.print(sb.toString());

        } catch (Exception e) {
            System.err.println("[ERROR ListarRutasFinalizables] " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, "Error interno del servidor: " + e.getMessage());
        }
    }

    // Método auxiliar para obtener información de vuelos
    private List<Object> getVuelosInfo(DtRutaVuelo ruta, HttpServletRequest request) {
        List<Object> vuelosInfo = new ArrayList<>();

        try {
            // Aquí deberías implementar la lógica para obtener los vuelos de la ruta
            // Por ahora, devolvemos un array vacío o información básica
            // Puedes adaptar esto según tu implementación específica

            // Ejemplo básico:
            Map<String, Object> vueloEjemplo = new HashMap<>();
            vueloEjemplo.put("nombre", "Vuelo de " + ruta.getCiudadOrigen() + " a " + ruta.getCiudadDestino());
            vueloEjemplo.put("fecha", "2024-01-01"); // Esto debería venir de tu lógica real
            vuelosInfo.add(convertToJsonString(vueloEjemplo));

        } catch (Exception e) {
            System.out.println("[DEBUG] Error en getVuelosInfo: " + e.getMessage());
        }

        return vuelosInfo;
    }

    private String convertToJsonString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escapeJson(entry.getKey())).append("\":");
            if (entry.getValue() instanceof String) {
                sb.append("\"").append(escapeJson((String) entry.getValue())).append("\"");
            } else {
                sb.append(entry.getValue());
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        PrintWriter out = response.getWriter();
        out.print("{\"error\":\"" + escapeJson(message) + "\"}");
    }

    // Métodos auxiliares (copiados de tu servlet original)
    private static String invokeGetterSafe(Object obj, String[] candidates) {
        try {
            for (String name : candidates) {
                try {
                    Method m = obj.getClass().getMethod(name);
                    Object val = m.invoke(obj);
                    if (val != null) return String.valueOf(val);
                } catch (NoSuchMethodException nsme) {
                    try {
                        Field f = obj.getClass().getField(name);
                        Object v = f.get(obj);
                        if (v != null) return String.valueOf(v);
                    } catch (Exception ignore) {}
                }
            }
        } catch (Exception ignore) {}
        return null;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r").replace("\t","\\t");
    }
}
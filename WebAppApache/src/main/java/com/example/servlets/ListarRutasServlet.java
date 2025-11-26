package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.List;

import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;
import serviciosweb.DtRutaVuelo;

// src/main/java/com/example/servlets/ListarRutasServlet.java
@WebServlet("/listarRutas")
public class ListarRutasServlet extends HttpServlet {

    private JuanViajesWS getPort(HttpServletRequest request) {
        try {
            Object o = request.getServletContext().getAttribute("port");
            if (o instanceof JuanViajesWS) return (JuanViajesWS) o;
        } catch (Exception ignored) {}
        WebServicesService svc = new WebServicesService();
        return svc.getJuanViajesWSPort();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Priorizar aerolinea en sesión si existe
            HttpSession session = request.getSession(false);
            String nombreAerolinea = null;
            if (session != null) {
                Object tipo = session.getAttribute("tipoUsuario");
                Object usuario = session.getAttribute("usuario");
                if (usuario != null && "aerolinea".equalsIgnoreCase(String.valueOf(tipo))) {
                    nombreAerolinea = String.valueOf(usuario);
                }
            }

            // Fallback: permitir pasar ?aerolinea=XXX
            if (nombreAerolinea == null || nombreAerolinea.trim().isEmpty()) {
                String param = request.getParameter("aerolinea");
                if (param != null && !param.trim().isEmpty()) {
                    nombreAerolinea = param.trim();
                }
            }

            if (nombreAerolinea == null || nombreAerolinea.trim().isEmpty()) {
                out.print("[]");
                return;
            }

            JuanViajesWS port = getPort(request);


            List<DtRutaVuelo> rutas = null;
            try {
                rutas = port.listarRutasPorAerolinea(nombreAerolinea);
            } catch (Exception ex) {
                // si falla la llamada remota, devolvemos array vacío
                rutas = java.util.Collections.emptyList();
            }

            out.print("[");
            for (int i = 0; i < rutas.size(); i++) {
                DtRutaVuelo r = rutas.get(i);

                String imagenVal = null;
                try {
                    java.lang.reflect.Method m = r.getClass().getMethod("getImagenUrl");
                    Object img = m.invoke(r);
                    if (img != null) imagenVal = img.toString();
                } catch (NoSuchMethodException ns) {
                    // no disponible
                } catch (Exception e) {
                    // ignorar errores reflectivos
                }

                if (imagenVal != null && !imagenVal.isBlank()) {
                    String tmp = imagenVal.trim();
                    try {
                        if (!tmp.matches("(?i)^(https?:)?//.*") && !tmp.startsWith("/") && !tmp.startsWith("data:") && !tmp.startsWith("Images/")) {
                            String ctx = request.getContextPath();
                            if (ctx == null) ctx = "";
                            if (!ctx.endsWith("/")) tmp = ctx + "/Images/" + tmp; else tmp = ctx + "Images/" + tmp;
                        }
                    } catch (Exception ex) {
                        // si algo falla, usar el valor original
                    }

                    try {
                        if (!tmp.matches("(?i)^(https?:)?//.*")) {
                            String scheme = request.getScheme();
                            String serverName = request.getServerName();
                            int serverPort = request.getServerPort();
                            String portPart = "";
                            if (!("http".equalsIgnoreCase(scheme) && serverPort == 80) && !("https".equalsIgnoreCase(scheme) && serverPort == 443)) {
                                portPart = ":" + serverPort;
                            }
                            if (!tmp.startsWith("/")) tmp = tmp.startsWith("/") ? tmp : "/" + tmp;
                            String absolute = scheme + "://" + serverName + portPart + tmp;
                            imagenVal = absolute;
                        } else {
                            imagenVal = tmp;
                        }
                    } catch (Exception ex) {
                        imagenVal = tmp;
                    }
                }

                String nombreEsc = r.getNombre() != null ? r.getNombre().replace("\\", "\\\\").replace("\"", "\\\"") : "";
                String descEsc = r.getDescripcion() != null ? r.getDescripcion().replace("\\", "\\\\").replace("\"", "\\\"") : "";
                out.print("{\"nombre\":\"" + nombreEsc + "\",\"descripcion\":\"" + descEsc + "\"");
                if (imagenVal != null && !imagenVal.isBlank()) {
                    String imgEsc = imagenVal.replace("\\", "\\\\").replace("\"", "\\\"");
                    out.print(",\"imagenUrl\":\"" + imgEsc + "\"");
                }
                out.print("}");
                if (i < rutas.size() - 1) out.print(",");
            }
            out.print("]");
        } catch (Exception e) {
            out.print("[]");
        }
    }
}

package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.Map;

import serviciosweb.JuanViajesWS;
import serviciosweb.DtVuelo;
import com.example.util.PortUtils;

@WebServlet("/api/flight-image")
public class FlightImageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombre = request.getParameter("nombre");
        if (nombre == null || nombre.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\":\"missing nombre\"}");
            return;
        }

        JuanViajesWS port = PortUtils.getPort(request);
        try { port.cargarDesdeBd(); } catch (Exception ignore) {}
        DtVuelo vuelo = null;
        try { vuelo = port.verInfoVueloDt(nombre); } catch (Throwable t) { /* ignore */ }

        String imagenVal = null;
        if (vuelo != null) {
            try {
                imagenVal = vuelo.getImagenUrl();
            } catch (Throwable ignore) {}
        }

        // fallback: check FLIGHT_IMAGE_MAP in context
        if (imagenVal == null || imagenVal.isBlank()) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) request.getServletContext().getAttribute("FLIGHT_IMAGE_MAP");
                if (map != null) imagenVal = map.get(nombre);
            } catch (Throwable ignore) {}
        }

        // if still null, try flights.json via servlet context attribute 'FLIGHTS_JSON' if present
        if (imagenVal == null || imagenVal.isBlank()) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Map<String, Object>> flights = (Map<String, Map<String, Object>>) request.getServletContext().getAttribute("FLIGHTS_JSON");
                if (flights != null && flights.containsKey(nombre)) {
                    Object v = flights.get(nombre).get("imagenUrl");
                    if (v != null) imagenVal = String.valueOf(v);
                }
            } catch (Throwable ignore) {}
        }

        if (imagenVal == null || imagenVal.isBlank()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().print("{\"error\":\"Imagen no encontrada\"}");
            return;
        }

        imagenVal = imagenVal.trim();
        // data URL
        if (imagenVal.startsWith("data:")) {
            // format: data:[mime];base64,xxxx
            int comma = imagenVal.indexOf(',');
            String meta = imagenVal.substring(5, comma);
            String base64 = imagenVal.substring(comma + 1);
            String mime = meta.split(";")[0];
            byte[] bytes = java.util.Base64.getDecoder().decode(base64);
            response.setContentType(mime != null && !mime.isBlank() ? mime : "application/octet-stream");
            response.setContentLength(bytes.length);
            try (OutputStream os = response.getOutputStream()) { os.write(bytes); }
            return;
        }

        // Remote http(s) -> redirect
        if (imagenVal.startsWith("http://") || imagenVal.startsWith("https://")) {
            response.sendRedirect(imagenVal);
            return;
        }

        // Local path -> serve from webapp Images folder
        String candidate = imagenVal;
        if (!candidate.startsWith("/")) {
            if (!candidate.startsWith("Images/")) candidate = "Images/" + candidate;
            candidate = "/" + candidate;
        }
        String realPath = request.getServletContext().getRealPath(candidate);
        if (realPath != null) {
            File f = new File(realPath);
            if (f.exists() && f.isFile()) {
                String mime = request.getServletContext().getMimeType(f.getName());
                if (mime == null) mime = "application/octet-stream";
                response.setContentType(mime);
                response.setContentLengthLong(f.length());
                try (OutputStream os = response.getOutputStream(); FileInputStream fis = new FileInputStream(f)) {
                    byte[] buf = new byte[8192]; int r; while ((r = fis.read(buf)) != -1) os.write(buf, 0, r);
                }
                return;
            }
        }

        // If none matched, return 404
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().print("{\"error\":\"Imagen no encontrada\"}");
    }
}

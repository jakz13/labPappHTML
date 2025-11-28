package com.example.servlets;

import serviciosweb.JuanViajesWS;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import java.net.URL;

@WebServlet("/api/finalizar-ruta")
public class FinalizarRutaServlet extends HttpServlet {

    private static volatile boolean sistemaCargado = false;
    private static final Object lock = new Object();
    private JuanViajesWS servicioWeb;

    @Override
    public void init() throws ServletException {
        try {
            // Configurar la conexión al servicio web usando la URL del WSDL
            URL wsdlURL = new URL("http://localhost:8081/JuanViajes?wsdl");
            QName serviceName = new QName("http://ServiciosWeb/", "WebServicesService");
            Service service = Service.create(wsdlURL, serviceName);
            servicioWeb = service.getPort(JuanViajesWS.class);

            System.out.println("FinalizarRutaServlet - Cliente del servicio web inicializado correctamente");

        } catch (Exception e) {
            System.err.println("ERROR FinalizarRutaServlet - Error inicializando el cliente del servicio web: " + e.getMessage());
            throw new ServletException("Error inicializando el cliente del servicio web", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreRuta = request.getParameter("nombreRuta");

        if (nombreRuta == null || nombreRuta.trim().isEmpty()) {
            sendErrorResponse(response, "Parámetro 'nombreRuta' requerido", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Cargar sistema solo una vez de forma thread-safe
            cargarSistemaUnaVez();

            // Primero verificar si puede finalizar usando el servicio web
            int resultadoVerificacion = servicioWeb.puedeFinalizarRuta(nombreRuta);

            if (resultadoVerificacion != 4) {
                String mensajeError = "";
                switch (resultadoVerificacion) {
                    case 0:
                        mensajeError = "Ruta no encontrada";
                        break;
                    case 1:
                        mensajeError = "La ruta no puede ser finalizada porque aún no está confirmada";
                        break;
                    case 2:
                        mensajeError = "La ruta no puede ser finalizada porque tiene vuelos pendientes";
                        break;
                    case 3:
                        mensajeError = "La ruta no puede ser finalizada porque está asociada a un paquete";
                        break;
                    default:
                        mensajeError = "La ruta no puede ser finalizada en este momento";
                }
                sendErrorResponse(response, mensajeError, HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Finalizar la ruta usando el servicio web
            servicioWeb.finalizarRutaVuelo(nombreRuta);
            out.print("{\"success\":true,\"message\":\"Ruta finalizada exitosamente\"}");
            System.out.println("FinalizarRutaServlet - Ruta '" + nombreRuta + "' finalizada exitosamente");

        } catch (IllegalArgumentException e) {
            System.err.println("[ERROR FinalizarRutaServlet] Error de validación: " + e.getMessage());
            sendErrorResponse(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("[ERROR FinalizarRutaServlet] Error interno: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response,
                    "Error interno del servidor: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void cargarSistemaUnaVez() {
        if (!sistemaCargado) {
            synchronized (lock) {
                if (!sistemaCargado) {
                    try {
                        servicioWeb.cargarDesdeBd();
                        sistemaCargado = true;
                        System.out.println("FinalizarRutaServlet - Sistema cargado desde BD");
                    } catch (Exception e) {
                        System.err.println("ERROR FinalizarRutaServlet - Error cargando sistema desde BD: " + e.getMessage());
                    }
                }
            }
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print("{\"success\":false,\"error\":\"" + escapeJson(message) + "\"}");
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
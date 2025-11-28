package com.example.servlets;

import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jakarta.xml.ws.BindingProvider;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet(
        urlPatterns = "/api/verificar-finalizacion",
        initParams = {
                @WebInitParam(name = "wsEndpoint", value = "http://localhost:8081/JuanViajes")
        }
)
public class VerificarFinalizacionServlet extends HttpServlet {

    private volatile WebServicesService service;
    private String endpointUrl;

    @Override
    public void init() {
        endpointUrl = getInitParameter("wsEndpoint");
        if (endpointUrl == null || endpointUrl.isBlank()) {
            endpointUrl = "http://localhost:8081/JuanViajes";
        }
        System.out.println("[VerificarFinalizacionServlet] Inicializado con endpoint: " + endpointUrl);
    }

    private WebServicesService getService() {
        if (service == null) {
            synchronized (this) {
                if (service == null) {
                    service = new WebServicesService();
                }
            }
        }
        return service;
    }

    private JuanViajesWS createPort() throws Exception {
        try {
            JuanViajesWS port = getService().getJuanViajesWSPort();
            BindingProvider bp = (BindingProvider) port;
            Map<String, Object> ctx = bp.getRequestContext();
            ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

            // Configurar timeouts
            ctx.put("javax.xml.ws.client.connectionTimeout", 2000);
            ctx.put("javax.xml.ws.client.receiveTimeout", 5000);
            ctx.put("org.apache.cxf.transport.http.client.connection.timeout", 2000);
            ctx.put("org.apache.cxf.transport.http.client.receive.timeout", 5000);
            ctx.put("com.sun.xml.ws.connect.timeout", 2000);
            ctx.put("com.sun.xml.ws.request.timeout", 5000);

            return port;
        } catch (Exception e) {
            throw new Exception("No se pudo inicializar el cliente del WebService", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreRuta = request.getParameter("nombreRuta");

        if (nombreRuta == null || nombreRuta.trim().isEmpty()) {
            sendErrorResponse(response, "Parámetro 'nombreRuta' requerido", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            System.out.println("[VerificarFinalizacionServlet] Verificando finalización para ruta: " + nombreRuta);

            // Crear el port con la configuración correcta
            JuanViajesWS port = createPort();

            // Llamar al servicio web para verificar si puede finalizar
            int codigo = port.puedeFinalizarRuta(nombreRuta);

            System.out.println("[VerificarFinalizacionServlet] Código de verificación: " + codigo);

            // Construir respuesta JSON según el código
            boolean puedeFinalizar = (codigo == 0);
            String motivo = "";

            switch (codigo) {
                case 0:
                    motivo = "La ruta puede ser finalizada";
                    break;
                case 1:
                    motivo = "La ruta no existe";
                    break;
                case 2:
                    motivo = "La ruta ya está finalizada";
                    break;
                case 3:
                    motivo = "La ruta no tiene vuelos asociados";
                    break;
                case 4:
                    motivo = "Existen vuelos sin fecha de alta";
                    break;
                default:
                    motivo = "Error desconocido al verificar la ruta";
                    break;
            }

            String json = "{" +
                    "\"puedeFinalizar\":" + puedeFinalizar + "," +
                    "\"codigo\":" + codigo + "," +
                    "\"motivo\":\"" + escapeJson(motivo) + "\"" +
                    "}";

            out.print(json);

        } catch (Exception e) {
            System.err.println("[ERROR VerificarFinalizacionServlet] Error al verificar finalización para ruta '" + nombreRuta + "': " + e.getMessage());
            e.printStackTrace();

            // Enviar respuesta de error
            String errorJson = "{" +
                    "\"puedeFinalizar\":false," +
                    "\"codigo\":-1," +
                    "\"motivo\":\"" + escapeJson("Error al verificar finalización: " + e.getMessage()) + "\"" +
                    "}";
            out.print(errorJson);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
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

package com.example.servlets;

import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;
import serviciosweb.DtVuelo;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jakarta.xml.ws.BindingProvider;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet(
        urlPatterns = "/api/vuelos-por-ruta",
        initParams = {
                @WebInitParam(name = "wsEndpoint", value = "http://localhost:8081/JuanViajes")
        }
)
public class VuelosPorRutaServlet extends HttpServlet {

    private volatile WebServicesService service;
    private String endpointUrl;

    @Override
    public void init() {
        endpointUrl = getInitParameter("wsEndpoint");
        if (endpointUrl == null || endpointUrl.isBlank()) {
            endpointUrl = "http://localhost:8081/JuanViajes";
        }
        System.out.println("[VuelosPorRutaServlet] Inicializado con endpoint: " + endpointUrl);
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

            // Configurar timeouts más largos para evitar errores de concurrencia
            ctx.put("javax.xml.ws.client.connectionTimeout", 5000);
            ctx.put("javax.xml.ws.client.receiveTimeout", 10000);
            ctx.put("org.apache.cxf.transport.http.client.connection.timeout", 5000);
            ctx.put("org.apache.cxf.transport.http.client.receive.timeout", 10000);
            ctx.put("com.sun.xml.ws.connect.timeout", 5000);
            ctx.put("com.sun.xml.ws.request.timeout", 10000);

            return port;
        } catch (Exception e) {
            throw new Exception("No se pudo inicializar el cliente del WebService", e);
        }
    }

    /**
     * Intenta listar los vuelos de una ruta con reintentos en caso de error de concurrencia
     */
    private List<DtVuelo> listarVuelosConReintentos(String nombreRuta) throws Exception {
        int maxReintentos = 3;
        int intentoActual = 0;
        Exception ultimoError = null;

        while (intentoActual < maxReintentos) {
            try {
                // Esperar un poco antes de cada reintento (excepto el primero)
                if (intentoActual > 0) {
                    int espera = intentoActual * 200; // 200ms, 400ms, 600ms
                    System.out.println("[VuelosPorRutaServlet] Reintento " + intentoActual + " para ruta: " + nombreRuta);
                    Thread.sleep(espera);
                }

                // Crear port y hacer la llamada
                JuanViajesWS port = createPort();
                List<DtVuelo> vuelos = port.listarVuelosPorRuta(nombreRuta);

                // Si llegamos aquí sin excepción, retornar el resultado
                return vuelos;

            } catch (Exception e) {
                ultimoError = e;
                intentoActual++;

                // Verificar si es un error que vale la pena reintentar
                String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                boolean esErrorDeReintento = errorMsg.contains("statement has been closed")
                        || errorMsg.contains("session")
                        || errorMsg.contains("jdbc")
                        || errorMsg.contains("connection")
                        || errorMsg.contains("null");

                if (!esErrorDeReintento || intentoActual >= maxReintentos) {
                    // No vale la pena reintentar o ya agotamos los intentos
                    break;
                }
            }
        }

        // Si llegamos aquí, todos los reintentos fallaron
        System.err.println("[VuelosPorRutaServlet] Error tras " + maxReintentos + " intentos para ruta '" + nombreRuta + "': " +
                (ultimoError != null ? ultimoError.getMessage() : "Error desconocido"));

        // Devolver lista vacía en lugar de lanzar excepción
        return java.util.Collections.emptyList();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreRuta = request.getParameter("nombreRuta");

        if (nombreRuta == null || nombreRuta.trim().isEmpty()) {
            sendErrorResponse(response, "Parámetro 'nombreRuta' requerido", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        PrintWriter out = response.getWriter();

        try {
            System.out.println("[VuelosPorRutaServlet] Buscando vuelos para ruta: " + nombreRuta);

            // Llamar al servicio web con reintentos
            List<DtVuelo> vuelos = listarVuelosConReintentos(nombreRuta);

            System.out.println("[VuelosPorRutaServlet] Número de vuelos encontrados: " + (vuelos != null ? vuelos.size() : "null"));

            if (vuelos != null && !vuelos.isEmpty()) {
                for (DtVuelo v : vuelos) {
                    System.out.println("[VuelosPorRutaServlet]   - Vuelo: " + v.getNombre() + ", Fecha: " + v.getFecha());
                }
            }

            // Construir JSON de respuesta
            StringBuilder json = new StringBuilder();
            json.append("[");
            if (vuelos != null && !vuelos.isEmpty()) {
                for (int i = 0; i < vuelos.size(); i++) {
                    DtVuelo vuelo = vuelos.get(i);
                    if (i > 0) json.append(",");

                    String nombre = vuelo.getNombre() != null ? vuelo.getNombre() : "Sin nombre";

                    // Extraer la fecha del objeto LocalDate del servicio web
                    String fechaStr = extraerFechaFormateada(vuelo.getFecha());

                    String nombreAerolinea = vuelo.getNombreAerolinea() != null ? vuelo.getNombreAerolinea() : "N/A";
                    int asientosTurista = vuelo.getAsientosTurista();
                    int asientosEjecutivo = vuelo.getAsientosEjecutivo();
                    int duracion = vuelo.getDuracion();
                    String imagenUrl = vuelo.getImagenUrl() != null ? vuelo.getImagenUrl() : "";

                    json.append("{")
                            .append("\"nombre\":\"").append(escapeJson(nombre)).append("\",")
                            .append("\"fecha\":\"").append(escapeJson(fechaStr)).append("\",")
                            .append("\"nombreAerolinea\":\"").append(escapeJson(nombreAerolinea)).append("\",")
                            .append("\"asientosTurista\":").append(asientosTurista).append(",")
                            .append("\"asientosEjecutivo\":").append(asientosEjecutivo).append(",")
                            .append("\"duracion\":").append(duracion).append(",")
                            .append("\"imagenUrl\":\"").append(escapeJson(imagenUrl)).append("\"")
                            .append("}");
                }
            }
            json.append("]");

            String jsonFinal = json.toString();
            out.print(jsonFinal);
            System.out.println("[VuelosPorRutaServlet] Respuesta enviada con " + (vuelos != null ? vuelos.size() : 0) + " vuelos");

        } catch (Exception e) {
            System.err.println("[ERROR VuelosPorRutaServlet] Error interno para ruta '" + nombreRuta + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{\"success\":false,\"error\":\"" + escapeJson(message) + "\"}");
    }

    /**
     * Extrae y formatea la fecha desde el objeto serviciosweb.LocalDate
     */
    /**
     * Extrae y formatea la fecha desde el objeto serviciosweb.LocalDate - VERSIÓN SIMPLE
     */
    private String extraerFechaFormateada(Object localDate) {
        if (localDate == null) {
            return "N/A";
        }

        try {
            String fechaStr = localDate.toString();
            System.out.println("[DEBUG] Fecha cruda: " + fechaStr);

            // Si viene en formato serviciosweb.LocalDate@hash
            if (fechaStr.contains("LocalDate")) {
                // El formato típico es: serviciosweb.LocalDate@3f566696
                // O si tiene más información: serviciosweb.LocalDate@hash{year=2024, month=11, day=27}

                // Intentar extraer información del toString() si contiene datos
                if (fechaStr.contains("year=") && fechaStr.contains("month=") && fechaStr.contains("day=")) {
                    try {
                        // Formato: serviciosweb.LocalDate@hash{year=2024, month=11, day=27}
                        int year = Integer.parseInt(fechaStr.split("year=")[1].split(",")[0].trim());
                        int month = Integer.parseInt(fechaStr.split("month=")[1].split(",")[0].trim());
                        int day = Integer.parseInt(fechaStr.split("day=")[1].split("}")[0].trim());

                        return String.format("%02d/%02d/%04d", day, month, year);
                    } catch (Exception e) {
                        System.err.println("[DEBUG] Error parseando fecha estructurada: " + e.getMessage());
                    }
                }

                // Si no tiene datos estructurados, devolver fecha genérica
                return "Fecha no disponible";
            }

            // Si ya viene en formato legible, devolverlo tal cual
            return fechaStr;

        } catch (Exception e) {
            System.err.println("[VuelosPorRutaServlet] Error extrayendo fecha: " + e.getMessage());
            return "N/A";
        }
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

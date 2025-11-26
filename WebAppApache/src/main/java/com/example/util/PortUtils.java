package com.example.util;

import jakarta.servlet.http.HttpServletRequest;
import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;

import jakarta.xml.ws.BindingProvider;
import java.util.Map;

// Importar la feature de logging de CXF
import org.apache.cxf.feature.LoggingFeature;
import jakarta.xml.ws.WebServiceFeature;

public class PortUtils {
    public static JuanViajesWS getPort(HttpServletRequest request) {
        try {
            Object o = request.getServletContext().getAttribute("port");
            if (o instanceof JuanViajesWS) return (JuanViajesWS) o;
        } catch (Exception ignored) {}
        WebServicesService svc = new WebServicesService();
        // Crear port con LoggingFeature para que CXF imprima request/response SOAP en logs
        JuanViajesWS port;
        try {
            WebServiceFeature lf = new LoggingFeature();
            port = svc.getJuanViajesWSPort(lf);
        } catch (Throwable t) {
            // fallback si no existe la sobrecarga con features
            port = svc.getJuanViajesWSPort();
        }

        // Permitir sobrescribir la URL del endpoint SOAP mediante context-param o atributo de ServletContext
        try {
            String override = null;
            // Primero revisar atributo en contexto (programático)
            Object attr = request.getServletContext().getAttribute("soap.endpoint");
            if (attr instanceof String) override = (String) attr;
            // Luego revisar init-param (web.xml) si no hay atributo
            if (override == null) {
                String param = request.getServletContext().getInitParameter("soap.endpoint");
                if (param != null && param.trim().length() > 0) override = param.trim();
            }
            if (override != null && override.length() > 0) {
                try {
                    BindingProvider bp = (BindingProvider) port;
                    Map<String, Object> rc = bp.getRequestContext();
                    rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, override);
                    System.out.println("PortUtils: endpoint SOAP sobrescrito a: " + override);
                } catch (Throwable t) {
                    System.err.println("PortUtils: no fue posible sobrescribir endpoint SOAP: " + t.getMessage());
                }
            }
        } catch (Exception ignored) {}

        // Inicializar datos en el servicio remoto sólo una vez por contexto para evitar condiciones de carrera
        try {
            Object inited = request.getServletContext().getAttribute("soap.initialized");
            if (!(inited instanceof Boolean && ((Boolean) inited))) {
                synchronized (request.getServletContext()) {
                    Object again = request.getServletContext().getAttribute("soap.initialized");
                    if (!(again instanceof Boolean && ((Boolean) again))) {
                        try {
                            System.out.println("PortUtils: inicializando datos del servicio SOAP (cargarDesdeBd)...");
                            port.cargarDesdeBd();
                            request.getServletContext().setAttribute("soap.initialized", Boolean.TRUE);
                            System.out.println("PortUtils: inicialización del servicio SOAP completada.");
                        } catch (Throwable t) {
                            System.err.println("PortUtils: fallo al inicializar datos SOAP: " + t.getMessage());
                            // No marcar como inicializado para permitir reintentos posteriores
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        return port;
    }
}

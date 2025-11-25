package com.example.util;

import jakarta.servlet.http.HttpServletRequest;
import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;

import jakarta.xml.ws.BindingProvider;
import java.util.Map;

public class PortUtils {
    public static JuanViajesWS getPort(HttpServletRequest request) {
        try {
            Object o = request.getServletContext().getAttribute("port");
            if (o instanceof JuanViajesWS) return (JuanViajesWS) o;
        } catch (Exception ignored) {}
        WebServicesService svc = new WebServicesService();
        JuanViajesWS port = svc.getJuanViajesWSPort();

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

        return port;
    }
}

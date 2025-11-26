package com.example.listeners;

import com.example.util.PortUtils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;

import jakarta.xml.ws.BindingProvider;
import java.util.Map;

/**
 * Inicializa el cliente SOAP y llama a cargarDesdeBd() una vez al inicio del contexto
 * para evitar inicializaciones concurrentes durante peticiones HTTP.
 */
@WebListener
public class SoapStartupListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        try {
            System.out.println("SoapStartupListener: inicializando cliente SOAP en startup...");
            WebServicesService svc = new WebServicesService();
            JuanViajesWS port = svc.getJuanViajesWSPort();

            // Aplicar override de endpoint si está configurado en web.xml (context-param)
            try {
                String override = ctx.getInitParameter("soap.endpoint");
                if (override != null && !override.trim().isEmpty()) {
                    BindingProvider bp = (BindingProvider) port;
                    Map<String, Object> rc = bp.getRequestContext();
                    rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, override.trim());
                    System.out.println("SoapStartupListener: endpoint SOAP sobrescrito a: " + override);
                }
            } catch (Throwable t) {
                System.err.println("SoapStartupListener: fallo aplicando override endpoint: " + t.getMessage());
            }

            try {
                port.cargarDesdeBd();
                ctx.setAttribute("soap.initialized", Boolean.TRUE);
                ctx.setAttribute("port", port);
                System.out.println("SoapStartupListener: cargarDesdeBd() ejecutado correctamente; port guardado en contexto.");
            } catch (Throwable t) {
                System.err.println("SoapStartupListener: error ejecutando cargarDesdeBd() en startup: " + t.getMessage());
                t.printStackTrace();
                // no marcar inicializado para permitir reintentos desde PortUtils
            }
        } catch (Throwable t) {
            System.err.println("SoapStartupListener: error inicializando cliente SOAP: " + t.getMessage());
            t.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // nada que limpiar por ahora
    }
}


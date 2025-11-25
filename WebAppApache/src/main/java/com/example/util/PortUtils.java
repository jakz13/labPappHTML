package com.example.util;

import jakarta.servlet.http.HttpServletRequest;
import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;

public class PortUtils {
    public static JuanViajesWS getPort(HttpServletRequest request) {
        try {
            Object o = request.getServletContext().getAttribute("port");
            if (o instanceof JuanViajesWS) return (JuanViajesWS) o;
        } catch (Exception ignored) {}
        WebServicesService svc = new WebServicesService();
        return svc.getJuanViajesWSPort();
    }
}


package com.example.listeners;

import ServiciosWeb.IWebServices;
import jakarta.xml.ws.Service;

import javax.xml.namespace.QName;
import java.net.URI;
import java.net.URL;

public class ClienteSOAP {
    public static void main(String[] args) throws Exception {
        String wsdl = "http://localhost:8081/JuanViajes?wsdl";
        if (args != null && args.length > 0 && args[0] != null && !args[0].isEmpty()) {
            wsdl = args[0];
        }
        URL url = URI.create(wsdl).toURL();
        QName serviceName = new QName("http://ServiciosWeb/", "WebServicesService");
        Service service = Service.create(url, serviceName);
        // Obtener el port usando la interfaz SEI
        IWebServices port = service.getPort(IWebServices.class);

        String nombre = "Mundo";
        if (args != null && args.length > 1 && args[1] != null && !args[1].isEmpty()) {
            nombre = args[1];
        }

        String resp = port.ping(nombre);
        System.out.println("Respuesta del servicio: " + resp);
    }
}

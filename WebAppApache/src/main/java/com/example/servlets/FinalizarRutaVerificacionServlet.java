package com.example.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.soap.*;
import logica.Fabrica;
import logica.ISistema;

import java.io.IOException;
import java.io.PrintWriter;
import javax.xml.namespace.QName;
import java.util.Iterator;

@WebServlet("/JuanViajes")
public class FinalizarRutaVerificacionServlet extends HttpServlet {

    private static volatile boolean sistemaCargado = false;
    private static final Object lock = new Object();

    private void cargarSistemaUnaVez(ISistema sistema) {
        if (!sistemaCargado) {
            synchronized (lock) {
                if (!sistemaCargado) {
                    sistema.cargarDesdeBd();
                    sistemaCargado = true;
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/xml;charset=UTF-8");

        try {
            // Crear mensaje SOAP desde la petición
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapRequest = messageFactory.createMessage(null, request.getInputStream());

            // Procesar la petición SOAP
            SOAPMessage soapResponse = processSOAPRequest(soapRequest);

            // Enviar respuesta SOAP
            soapResponse.writeTo(response.getOutputStream());

        } catch (SOAPException e) {
            System.err.println("[ERROR SOAPServlet] Error procesando petición SOAP: " + e.getMessage());
            e.printStackTrace();
            sendSOAPFault(response, "Error procesando petición SOAP", e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR SOAPServlet] Error interno: " + e.getMessage());
            e.printStackTrace();
            sendSOAPFault(response, "Error interno del servidor", e.getMessage());
        }
    }

    private SOAPMessage processSOAPRequest(SOAPMessage request) throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage response = messageFactory.createMessage();

        SOAPPart requestPart = request.getSOAPPart();
        SOAPEnvelope requestEnvelope = requestPart.getEnvelope();

        SOAPPart responsePart = response.getSOAPPart();
        SOAPEnvelope responseEnvelope = responsePart.getEnvelope();

        // Configurar namespaces
        responseEnvelope.addNamespaceDeclaration("tns", "http://ServiciosWeb/");

        // Obtener el cuerpo del mensaje de solicitud
        SOAPBody requestBody = requestEnvelope.getBody();
        SOAPBody responseBody = responseEnvelope.getBody();

        // Verificar qué operación se está llamando
        Iterator<?> children = requestBody.getChildElements();
        while (children.hasNext()) {
            Object child = children.next();
            if (child instanceof SOAPElement) {
                SOAPElement element = (SOAPElement) child;
                String elementName = element.getElementName().getLocalName();

                if ("puedeFinalizarRuta".equals(elementName)) {
                    return processPuedeFinalizarRuta(element, response);
                }
            }
        }

        // Si no se reconoce la operación, devolver fault
        return createSOAPFault(response, "Client", "Operación no soportada");
    }

    private SOAPMessage processPuedeFinalizarRuta(SOAPElement requestElement, SOAPMessage response) throws SOAPException {
        SOAPEnvelope envelope = response.getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();

        try {
            // Extraer parámetro nombreRuta del mensaje SOAP
            String nombreRuta = null;
            Iterator<?> params = requestElement.getChildElements();
            while (params.hasNext()) {
                Object param = params.next();
                if (param instanceof SOAPElement) {
                    SOAPElement paramElement = (SOAPElement) param;
                    if ("nombreRuta".equals(paramElement.getElementName().getLocalName())) {
                        nombreRuta = paramElement.getValue();
                        break;
                    }
                }
            }

            if (nombreRuta == null || nombreRuta.trim().isEmpty()) {
                return createSOAPFault(response, "Client", "Parámetro 'nombreRuta' requerido");
            }

            // Llamar a la lógica del sistema
            ISistema sistema = Fabrica.getInstance().getISistema();
            cargarSistemaUnaVez(sistema);

            int resultado = sistema.puedeFinalizarRuta(nombreRuta);

            // Crear respuesta SOAP
            SOAPElement responseElement = body.addChildElement(
                    "puedeFinalizarRutaResponse", "tns");

            SOAPElement codigoElement = responseElement.addChildElement("codigo");
            codigoElement.addTextNode(String.valueOf(resultado));

            return response;

        } catch (Exception e) {
            System.err.println("[ERROR SOAPServlet] Error procesando puedeFinalizarRuta: " + e.getMessage());
            e.printStackTrace();
            return createSOAPFault(response, "Server", "Error al verificar finalización: " + e.getMessage());
        }
    }

    private SOAPMessage createSOAPFault(SOAPMessage message, String faultCode, String faultString) throws SOAPException {
        SOAPBody body = message.getSOAPPart().getEnvelope().getBody();

        SOAPFault fault = body.addFault();
        fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", faultCode));
        fault.setFaultString(faultString);

        return message;
    }

    private void sendSOAPFault(HttpServletResponse httpResponse, String faultCode, String faultString) throws IOException {
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage faultMessage = messageFactory.createMessage();

            SOAPEnvelope envelope = faultMessage.getSOAPPart().getEnvelope();
            SOAPBody body = envelope.getBody();

            SOAPFault fault = body.addFault();
            fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", faultCode));
            fault.setFaultString(faultString);

            faultMessage.writeTo(httpResponse.getOutputStream());

        } catch (SOAPException e) {
            // Fallback: enviar error HTTP simple si no se puede crear el fault SOAP
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = httpResponse.getWriter();
            out.print("Error: " + faultString);
        }
    }

    // Método auxiliar para escapar XML (similar al que tenías para JSON)
    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}

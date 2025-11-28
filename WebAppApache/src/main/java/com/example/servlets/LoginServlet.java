package com.example.servlets;

import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jakarta.xml.ws.BindingProvider;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;
import serviciosweb.DtCliente;
import serviciosweb.DtAerolinea;

@WebServlet(
        urlPatterns = "/api/login",
        initParams = {
                @WebInitParam(name = "wsEndpoint", value = "http://localhost:8081/JuanViajes")
        }
)
public class LoginServlet extends HttpServlet {

    private WebServicesService service;
    private String endpointUrl;

    @Override
    public void init() {
        // Inicializa la Service una vez
        endpointUrl = getInitParameter("wsEndpoint");
        if (endpointUrl == null || endpointUrl.isBlank()) {
            endpointUrl = "http://localhost:8081/JuanViajes";
        }

        // Servicio generado por CXF/wsdl2java. Ajusta el constructor si el generado es distinto.
        service = new WebServicesService();
    }

    // Crea un port por cada petición y fuerza la endpoint URL (y timeouts)
    private JuanViajesWS createPort() {
        JuanViajesWS port = service.getJuanViajesWSPort(); // método generado; revisa el nombre exacto
        BindingProvider bp = (BindingProvider) port;
        Map<String, Object> ctx = bp.getRequestContext();
        ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

        // Timeouts: ponemos varias claves para cubrir diferentes implementaciones (Metro/CXF)
        // Valores en ms
        ctx.put("javax.xml.ws.client.connectionTimeout", 1000);
        ctx.put("javax.xml.ws.client.receiveTimeout", 2000);
        // CXF-specific
        ctx.put("org.apache.cxf.transport.http.client.connection.timeout", 1000);
        ctx.put("org.apache.cxf.transport.http.client.receive.timeout", 2000);

        // Metro / RI properties (fallback)
        ctx.put("com.sun.xml.ws.connect.timeout", 1000);
        ctx.put("com.sun.xml.ws.request.timeout", 2000);

        return port;
    }

    // Obtener port preferentemente desde ServletContext, si existe; si no, crear uno nuevo.
    private JuanViajesWS getPort(HttpServletRequest request) {
        try {
            Object obj = request.getServletContext().getAttribute("port");
            if (obj instanceof JuanViajesWS) {
                JuanViajesWS port = (JuanViajesWS) obj;
                // Asegurar que tenga el endpoint y timeouts configurados
                try {
                    BindingProvider bp = (BindingProvider) port;
                    Map<String, Object> ctx = bp.getRequestContext();
                    ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
                    ctx.put("javax.xml.ws.client.connectionTimeout", 1000);
                    ctx.put("javax.xml.ws.client.receiveTimeout", 2000);
                    ctx.put("org.apache.cxf.transport.http.client.connection.timeout", 1000);
                    ctx.put("org.apache.cxf.transport.http.client.receive.timeout", 2000);
                    ctx.put("com.sun.xml.ws.connect.timeout", 1000);
                    ctx.put("com.sun.xml.ws.request.timeout", 2000);
                } catch (Exception e) {
                    // si no es BindingProvider o falla, ignorar y fallback a createPort
                }
                return port;
            }
        } catch (Exception e) {
            // Ignorar y crear un port nuevo
        }
        return createPort();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String user = request.getParameter("nickname");
        String password = request.getParameter("password");

        if (user == null || user.isBlank() || password == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"error\":\"Parámetros faltantes\"}");
            return;
        }

        try {
            JuanViajesWS port = getPort(request);

            DtCliente cliente = null;
            DtAerolinea aerolinea = null;

            // Buscar cliente - con manejo robusto de errores
            try {
                List<DtCliente> clientes = sistema.listarClientes();
                System.out.println("Clientes encontrados: " + clientes.size());

                for (DtCliente c : clientes) {
                    try {
                        String nick = c.getNickname();
                        String mail = c.getEmail();

                        boolean match = (nick != null && nick.equalsIgnoreCase(user)) ||
                                (mail != null && mail.equalsIgnoreCase(user));

                        if (!match) continue;

                    // verificarLogin espera email y password según tu SEI; si usas nickname, ajusta
                    boolean ok;
                    try { ok = port.verificarLogin(mail, password); } catch (Exception ex) { ok = false; }
                    if (!ok) continue;

                    // Obtener datos completos del cliente
                    clienteEncontrado = port.obtenerCliente(nick);
                    break;
                }
            } catch (Exception e) {
                System.err.println("Error listando clientes: " + e.getMessage());
            }

            if (clienteEncontrado != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", clienteEncontrado.getNickname());
                session.setAttribute("tipoUsuario", "cliente");
                session.setAttribute("tipo", "cliente");

                String jsonResponse = "{\"success\":true,\"nickname\":\"" + escapeJson(clienteEncontrado.getNickname()) + "\",\"tipo\":\"cliente\"}";
                out.print(jsonResponse);
                return;
            }

            // Si no es cliente, intentamos aerolinea
            List<DtAerolinea> aerolineas = null;
            DtAerolinea aeroEncontrada = null;
            try {
                aerolineas = port.listarAerolineas();
            } catch (Exception ex) {
                throw ex;
            }

            if (aerolineas != null) {
                for (DtAerolinea a : aerolineas) {
                    if (a == null) continue;
                    String nick = a.getNickname();
                    String mail = a.getEmail();
                    boolean match = (nick != null && nick.equalsIgnoreCase(user))
                            || (mail != null && mail.equalsIgnoreCase(user));
                    if (!match) continue;

                    boolean ok = false;
                    try { ok = port.verificarLogin(mail, password); } catch (Exception ex) { ok = false; }
                    if (!ok) continue;

                    aeroEncontrada = port.obtenerAerolinea(nick);
                    break;
                }
            }

            if (aeroEncontrada != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", aeroEncontrada.getNickname());
                session.setAttribute("tipoUsuario", "aerolinea");
                session.setAttribute("tipo", "aerolinea");

                String jsonResponse = "{\"success\":true,\"nickname\":\"" + escapeJson(aeroEncontrada.getNickname()) + "\",\"tipo\":\"aerolinea\"}";
                out.print(jsonResponse);
                return;
            }


            // Si llegamos aquí: fallo de autenticación
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\":false,\"error\":\"Usuario no encontrado o credenciales inválidas\"}");

        } catch (Exception e) {
            // Error remoto / red / marshalling
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n","\\n").replace("\r","\\r");
    }
}

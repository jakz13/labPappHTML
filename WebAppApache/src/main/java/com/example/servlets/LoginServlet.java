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

    // Cliente SOAP creado de forma lazy
    private volatile WebServicesService service;
    private String endpointUrl;

    @Override
    public void init() {
        endpointUrl = getInitParameter("wsEndpoint");
        if (endpointUrl == null || endpointUrl.isBlank()) {
            endpointUrl = "http://localhost:8081/JuanViajes";
        }
    }

    private WebServicesService getService() {
        if (service == null) {
            synchronized (this) {
                if (service == null) {
                    // Primera creación del stub: puede tardar, por eso ajustamos bien los timeouts
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

            // Timeouts más bajos (2s conexión, 5s respuesta)
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            JuanViajesWS port = createPort();

            // --- Intento como cliente ---
            DtCliente clienteEncontrado = null;
            try {
                List<DtCliente> clientes = port.listarClientes();
                if (clientes != null) {
                    for (DtCliente c : clientes) {
                        if (c == null) continue;
                        String nick = c.getNickname();
                        String mail = c.getEmail();
                        boolean match = (nick != null && nick.equalsIgnoreCase(user))
                                || (mail != null && mail.equalsIgnoreCase(user));
                        if (!match) continue;

                        // Solo si matchea, llamamos al WS para verificar login
                        boolean ok = port.verificarLogin(mail, password);
                        if (!ok) break;

                        clienteEncontrado = port.obtenerCliente(nick);
                        break;
                    }
                }
            } catch (Exception ex) {
                // Error de conexión o tiempo de espera: devolvemos 500 rápido
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\":false,\"error\":\"Error de conexión con el servidor de autenticación\"}");
                return;
            }

            if (clienteEncontrado != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", clienteEncontrado.getNickname());
                session.setAttribute("tipoUsuario", "cliente");
                session.setAttribute("tipo", "cliente");

                String jsonResponse = "{\"success\":true,\"nickname\":\""
                        + escapeJson(clienteEncontrado.getNickname())
                        + "\",\"tipo\":\"cliente\"}";
                out.print(jsonResponse);
                return;
            }

            // --- Intento como aerolínea ---
            DtAerolinea aeroEncontrada = null;
            try {
                List<DtAerolinea> aerolineas = port.listarAerolineas();
                if (aerolineas != null) {
                    for (DtAerolinea a : aerolineas) {
                        if (a == null) continue;
                        String nick = a.getNickname();
                        String mail = a.getEmail();
                        boolean match = (nick != null && nick.equalsIgnoreCase(user))
                                || (mail != null && mail.equalsIgnoreCase(user));
                        if (!match) continue;

                        boolean ok = port.verificarLogin(mail, password);
                        if (!ok) break;

                        aeroEncontrada = port.obtenerAerolinea(nick);
                        break;
                    }
                }
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\":false,\"error\":\"Error de conexión con el servidor de autenticación\"}");
                return;
            }

            if (aeroEncontrada != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", aeroEncontrada.getNickname());
                session.setAttribute("tipoUsuario", "aerolinea");
                session.setAttribute("tipo", "aerolinea");

                String jsonResponse = "{\"success\":true,\"nickname\":\""
                        + escapeJson(aeroEncontrada.getNickname())
                        + "\",\"tipo\":\"aerolinea\"}";
                out.print(jsonResponse);
                return;
            }

            // Fallo de autenticación
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\":false,\"error\":\"Usuario no encontrado o credenciales inválidas\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String msg = (e.getMessage() != null) ? e.getMessage() : "Error inesperado";
            out.print("{\"success\":false,\"error\":\"Error interno: " + escapeJson(msg) + "\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}

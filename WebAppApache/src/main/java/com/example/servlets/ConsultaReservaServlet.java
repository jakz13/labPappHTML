package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.*;
import Logica.Vuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.List;

@WebServlet("/api/consulta-reserva")
public class ConsultaReservaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String usuario = request.getParameter("usuario");
        String vuelo = request.getParameter("vuelo");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            if ("reservas-cliente".equals(action) && usuario != null) {
                // Obtener reservas de un cliente específico
                obtenerReservasCliente(sistema, usuario, out);
            } else if ("reservas-vuelo".equals(action) && vuelo != null) {
                // Obtener reservas de un vuelo específico
                obtenerReservasVuelo(sistema, vuelo, out);
            } else if ("reserva-cliente-vuelo".equals(action) && usuario != null && vuelo != null) {
                // Obtener reserva específica de un cliente en un vuelo
                obtenerReservaClienteEnVuelo(sistema, usuario, vuelo, out);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Parámetros inválidos\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno: " + e.getMessage() + "\"}");
        }
    }

    private void obtenerReservasCliente(ISistema sistema, String usuario, PrintWriter out) {
        try {
            DtCliente cliente = sistema.obtenerCliente(usuario);
            if (cliente != null) {
                List<DtReserva> reservas = cliente.getReservas();
                escribirReservasJSON(reservas, out);
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            out.print("[]");
        }
    }

    private void obtenerReservasVuelo(ISistema sistema, String nombreVuelo, PrintWriter out) {
        try {
            Vuelo vuelo = sistema.obtenerVuelo(nombreVuelo);
            if (vuelo != null) {
                List<DtReserva> reservas = (List<DtReserva>) vuelo.getReservas();
                escribirReservasVueloJSON(reservas, sistema, out);
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            out.print("[]");
        }
    }

    private void obtenerReservaClienteEnVuelo(ISistema sistema, String usuario, String nombreVuelo, PrintWriter out) {
        try {
            DtCliente cliente = sistema.obtenerCliente(usuario);
            if (cliente != null) {
                List<DtReserva> reservasCliente = cliente.getReservas();

                // Obtener todas las reservas del vuelo
                Vuelo vuelo = sistema.obtenerVuelo(nombreVuelo);
                if (vuelo != null) {
                    List<DtReserva> reservasVuelo = (List<DtReserva>) vuelo.getReservas();

                    // Buscar si alguna reserva del cliente está en el vuelo
                    for (DtReserva reservaCliente : reservasCliente) {
                        for (DtReserva reservaVuelo : reservasVuelo) {
                            if (reservasSonIguales(reservaCliente, reservaVuelo)) {
                                escribirReservaDetalleJSON(reservaCliente, out);
                                return;
                            }
                        }
                    }
                }
            }
            out.print("{\"error\":\"Reserva no encontrada\"}");
        } catch (Exception e) {
            out.print("{\"error\":\"Error buscando reserva\"}");
        }
    }

    // Comparar reservas por sus atributos principales
    private boolean reservasSonIguales(DtReserva r1, DtReserva r2) {
        return r1.getId().equals(r2.getId()) &&
                r1.getTipoAsiento().equals(r2.getTipoAsiento()) &&
                r1.getCantidadPasajes() == r2.getCantidadPasajes() &&
                r1.getCosto() == r2.getCosto();
    }

    private void escribirReservasJSON(List<DtReserva> reservas, PrintWriter out) {
        out.print("[");
        for (int i = 0; i < reservas.size(); i++) {
            DtReserva r = reservas.get(i);
            out.print("{");
            out.print("\"id\":\"" + escapeJson(r.getId()) + "\",");
            out.print("\"tipoAsiento\":\"" + escapeJson(String.valueOf(r.getTipoAsiento())) + "\",");
            out.print("\"cantidadPasajes\":" + r.getCantidadPasajes() + ",");
            out.print("\"equipajeExtra\":" + r.getUnidadesEquipajeExtra() + ",");
            out.print("\"costoTotal\":" + r.getCosto() + ",");
            out.print("\"fechaReserva\":\"" + r.getFecha() + "\"");
            out.print("}");

            if (i < reservas.size() - 1) out.print(",");
        }
        out.print("]");
    }

    private void escribirReservasVueloJSON(List<DtReserva> reservas, ISistema sistema, PrintWriter out) {
        out.print("[");
        for (int i = 0; i < reservas.size(); i++) {
            DtReserva r = reservas.get(i);

            // Buscar el cliente dueño de esta reserva
            String clienteNombre = buscarClienteDeReserva(r, sistema);

            out.print("{");
            out.print("\"id\":\"" + escapeJson(r.getId()) + "\",");
            out.print("\"clienteNombre\":\"" + escapeJson(clienteNombre) + "\",");
            out.print("\"tipoAsiento\":\"" + escapeJson(String.valueOf(r.getTipoAsiento())) + "\",");
            out.print("\"cantidadPasajes\":" + r.getCantidadPasajes() + ",");
            out.print("\"equipajeExtra\":" + r.getUnidadesEquipajeExtra() + ",");
            out.print("\"costoTotal\":" + r.getCosto() + ",");
            out.print("\"fechaReserva\":\"" + r.getFecha() + "\"");
            out.print("}");

            if (i < reservas.size() - 1) out.print(",");
        }
        out.print("]");
    }

    private String buscarClienteDeReserva(DtReserva reserva, ISistema sistema) {
        // Buscar en todos los clientes quién tiene esta reserva
        try {
            for (DtCliente cliente : sistema.listarClientes()) {
                for (DtReserva reservaCliente : cliente.getReservas()) {
                    if (reservasSonIguales(reserva, reservaCliente)) {
                        return cliente.getNombre() + " " + cliente.getApellido();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Cliente no encontrado";
    }

    private void escribirReservaDetalleJSON(DtReserva reserva, PrintWriter out) {
        out.print("{");
        out.print("\"id\":\"" + escapeJson(reserva.getId()) + "\",");
        out.print("\"tipoAsiento\":\"" + escapeJson(String.valueOf(reserva.getTipoAsiento())) + "\",");
        out.print("\"cantidadPasajes\":" + reserva.getCantidadPasajes() + ",");
        out.print("\"equipajeExtra\":" + reserva.getUnidadesEquipajeExtra() + ",");
        out.print("\"costoTotal\":" + reserva.getCosto() + ",");
        out.print("\"fechaReserva\":\"" + reserva.getFecha() + "\"");
        out.print("}");
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.*;
import Logica.Vuelo;
import Logica.Reserva;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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
                System.out.println("Buscando reservas para usuario: " + usuario);
                obtenerReservasCliente(sistema, usuario, out);
            } else if ("reservas-vuelo".equals(action) && vuelo != null) {
                System.out.println("Buscando reservas para vuelo: " + vuelo);
                // Obtener reservas de un vuelo específico
                obtenerReservasVuelo(sistema, vuelo, out);
            } else if ("reserva-cliente-vuelo".equals(action) && usuario != null && vuelo != null) {
                // Obtener reserva específica de un cliente en un vuelo
                System.out.println("Buscando reserva para usuario: " + usuario + " en vuelo: " + vuelo);
                obtenerReservaClienteEnVuelo(sistema, usuario, vuelo, out);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Parámetros inválidos\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void obtenerReservasCliente(ISistema sistema, String usuario, PrintWriter out) {
        try {
            DtCliente cliente = sistema.obtenerCliente(usuario);
            if (cliente != null) {
                List<DtReserva> reservas = cliente.getReservas();
                for (DtReserva r : reservas) {
                    System.out.println("Reserva encontrada: " + r.getId());
                }
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
                // Convertir el Map<String, Reserva> a List<DtReserva> directamente aquí
                Map<Long, Reserva> reservasMap = vuelo.getReservas();
                List<DtReserva> reservas = new ArrayList<>();

                for (Reserva reserva : reservasMap.values()) {
                    // Buscar el DtReserva correspondiente en todos los clientes
                    DtReserva dtReserva = buscarDtReservaPorId(reserva.getId(), sistema);
                    if (dtReserva != null) {
                        reservas.add(dtReserva);
                    }
                }

                escribirReservasVueloJSON(reservas, sistema, out);
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            out.print("[]");
        }
    }

    private DtReserva buscarDtReservaPorId(Long idReserva, ISistema sistema) {
        try {
            // Buscar en todos los clientes la reserva con este ID
            for (DtCliente cliente : sistema.listarClientes()) {
                for (DtReserva reserva : cliente.getReservas()) {
                    if (reserva.getId().equals(idReserva)) {
                        return reserva;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void obtenerReservaClienteEnVuelo(ISistema sistema, String usuario, String nombreVuelo, PrintWriter out) {
        try {
            DtCliente cliente = sistema.obtenerCliente(usuario);
            if (cliente != null) {
                List<DtReserva> reservasCliente = cliente.getReservas();

                // Buscar si alguna reserva del cliente está en el vuelo
                for (DtReserva reservaCliente : reservasCliente) {
                    if (nombreVuelo.equals(reservaCliente.getVuelo())) {
                        escribirReservaDetalleJSON(reservaCliente, out);
                        return;
                    }
                }
            }
            out.print("{\"error\":\"Reserva no encontrada\"}");
        } catch (Exception e) {
            out.print("{\"error\":\"Error buscando reserva: " + escapeJson(e.getMessage()) + "\"}");
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
            out.print("\"id\":" + r.getId() + ","); // Cambiado a número (sin comillas)
            out.print("\"tipoAsiento\":\"" + escapeJson(String.valueOf(r.getTipoAsiento())) + "\",");
            out.print("\"cantidadPasajes\":" + r.getCantidadPasajes() + ",");
            out.print("\"equipajeExtra\":" + r.getUnidadesEquipajeExtra() + ",");
            out.print("\"costoTotal\":" + r.getCosto() + ",");
            out.print("\"fechaReserva\":\"" + escapeJson(String.valueOf(r.getFecha())) + "\",");
            out.print("\"vuelo\":\"" + escapeJson(r.getVuelo()) + "\""); // Agregado nombre del vuelo
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
            out.print("\"id\":" + r.getId() + ","); // Cambiado a número (sin comillas)
            out.print("\"clienteNombre\":\"" + escapeJson(clienteNombre) + "\",");
            out.print("\"tipoAsiento\":\"" + escapeJson(String.valueOf(r.getTipoAsiento())) + "\",");
            out.print("\"cantidadPasajes\":" + r.getCantidadPasajes() + ",");
            out.print("\"equipajeExtra\":" + r.getUnidadesEquipajeExtra() + ",");
            out.print("\"costoTotal\":" + r.getCosto() + ",");
            out.print("\"fechaReserva\":\"" + escapeJson(String.valueOf(r.getFecha())) + "\"");
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
        out.print("\"id\":" + reserva.getId() + ","); // Cambiado a número (sin comillas)
        out.print("\"tipoAsiento\":\"" + escapeJson(String.valueOf(reserva.getTipoAsiento())) + "\",");
        out.print("\"cantidadPasajes\":" + reserva.getCantidadPasajes() + ",");
        out.print("\"equipajeExtra\":" + reserva.getUnidadesEquipajeExtra() + ",");
        out.print("\"costoTotal\":" + reserva.getCosto() + ",");
        out.print("\"fechaReserva\":\"" + escapeJson(String.valueOf(reserva.getFecha())) + "\",");
        out.print("\"vuelo\":\"" + escapeJson(reserva.getVuelo()) + "\""); // Agregado nombre del vuelo
        out.print("}");
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.List;

@WebServlet("/consulta-usuario")
public class ConsultaUsuarioServerlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String usuarioId = request.getParameter("usuario");
        String tipoUsuario = request.getParameter("tipo");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            if ("listar-usuarios".equals(action)) {
                // Listar todos los usuarios (clientes y aerolíneas)
                System.out.println("Listando todos los usuarios");
                listarUsuarios(sistema, out);
            } else if ("obtener-usuario".equals(action) && usuarioId != null && tipoUsuario != null) {
                // Obtener información específica de un usuario
                System.out.println("Obteniendo información del usuario: " + usuarioId + " tipo: " + tipoUsuario);
                obtenerUsuarioDetalle(sistema, usuarioId, tipoUsuario, out, response);
            } else if ("obtener-rutas-aerolinea".equals(action) && usuarioId != null) {
                // Obtener rutas de una aerolínea
                System.out.println("Obteniendo rutas de la aerolínea: " + usuarioId);
                obtenerRutasAerolinea(sistema, usuarioId, out, response);
            } else if ("obtener-reservas-cliente".equals(action) && usuarioId != null) {
                // Obtener reservas de un cliente
                System.out.println("Obteniendo reservas del cliente: " + usuarioId);
                obtenerReservasCliente(sistema, usuarioId, out, response);
            } else if ("obtener-paquetes-cliente".equals(action) && usuarioId != null) {
                // Obtener paquetes de un cliente
                System.out.println("Obteniendo paquetes del cliente: " + usuarioId);
                obtenerPaquetesCliente(sistema, usuarioId, out, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Parámetros inválidos. Acciones válidas: listar-usuarios, obtener-usuario, obtener-rutas-aerolinea, obtener-reservas-cliente, obtener-paquetes-cliente\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
            e.printStackTrace();
        }
    }

    private void listarUsuarios(ISistema sistema, PrintWriter out) {
        try {
            out.print("{");

            // Clientes
            out.print("\"clientes\":[");
            List<DtCliente> clientes = sistema.listarClientes();
            for (int i = 0; i < clientes.size(); i++) {
                DtCliente c = clientes.get(i);
                out.print("{");
                out.print("\"id\":\"" + escapeJson(c.getNickname()) + "\",");
                out.print("\"nombre\":\"" + escapeJson(c.getNombre() + " " + c.getApellido()) + "\",");
                out.print("\"tipo\":\"Cliente\",");
                out.print("\"correo\":\"" + escapeJson(c.getEmail()) + "\",");
                out.print("\"fechaRegistro\":\"" + escapeJson(c.getFechaAlta() != null ? c.getFechaAlta().toString() : "") + "\",");
                out.print("\"imagen\":\"" + escapeJson(c.getImagenUrl()) + "\"");
                out.print("}");
                if (i < clientes.size() - 1) out.print(",");
            }
            out.print("],");

            // Aerolíneas
            out.print("\"aerolineas\":[");
            List<DtAerolinea> aerolineas = sistema.listarAerolineas();
            for (int i = 0; i < aerolineas.size(); i++) {
                DtAerolinea a = aerolineas.get(i);
                out.print("{");
                out.print("\"id\":\"" + escapeJson(a.getNickname()) + "\",");
                out.print("\"nombre\":\"" + escapeJson(a.getNombre()) + "\",");
                out.print("\"tipo\":\"Aerolinea\",");
                out.print("\"correo\":\"" + escapeJson(a.getEmail()) + "\",");
                out.print("\"fechaRegistro\":\"\",");
                out.print("\"imagen\":\"" + escapeJson(a.getImagenUrl()) + "\"");
                out.print("}");
                if (i < aerolineas.size() - 1) out.print(",");
            }
            out.print("]");

            out.print("}");

        } catch (Exception e) {
            System.err.println("Error listando usuarios: " + e.getMessage());
            out.print("{\"clientes\":[],\"aerolineas\":[]}");
        }
    }

    private void obtenerUsuarioDetalle(ISistema sistema, String usuarioId, String tipoUsuario, PrintWriter out, HttpServletResponse response) {
        try {
            if ("cliente".equals(tipoUsuario)) {
                DtCliente cliente = sistema.obtenerCliente(usuarioId);
                if (cliente != null) {
                    escribirClienteDetalleJSON(cliente, out); // Cambiado a escribirInfoClienteJSON
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Cliente no encontrado\"}");
                }
            } else if ("aerolinea".equals(tipoUsuario)) {
                DtAerolinea aerolinea = sistema.obtenerAerolinea(usuarioId);
                if (aerolinea != null) {
                    escribirInfoAerolineaJSON(aerolinea, out); // Cambiado a escribirInfoAerolineaJSON
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Aerolínea no encontrada\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Tipo de usuario no válido\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Usuario no encontrado: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void obtenerRutasAerolinea(ISistema sistema, String aerolineaId, PrintWriter out, HttpServletResponse response) {
        try {
            List<DtRutaVuelo> rutas = sistema.listarRutasPorAerolinea(aerolineaId);
            escribirRutasAerolineaJSON(rutas, out);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Error obteniendo rutas: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void obtenerReservasCliente(ISistema sistema, String clienteId, PrintWriter out, HttpServletResponse response) {
        try {
            List<DtReserva> reservas = sistema.getReservasCliente(clienteId);
            escribirReservasClienteJSON(reservas, out);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Error obteniendo reservas: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void obtenerPaquetesCliente(ISistema sistema, String clienteId, PrintWriter out, HttpServletResponse response) {
        try {
            DtCliente cliente = sistema.obtenerCliente(clienteId);
            if (cliente != null) {
                List<DtPaquete> paquetes = cliente.getPaquetesComprados();
                escribirPaquetesClienteJSON(paquetes, out);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Cliente no encontrado\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Error obteniendo paquetes: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void escribirUsuariosJSON(List<DtCliente> clientes, List<DtAerolinea> aerolineas, PrintWriter out) {
        out.print("{");

        // Clientes
        out.print("\"clientes\":[");
        for (int i = 0; i < clientes.size(); i++) {
            DtCliente c = clientes.get(i);
            out.print("{");
            out.print("\"id\":\"" + escapeJson(c.getNickname()) + "\",");
            out.print("\"nombre\":\"" + escapeJson(c.getNombre() + " " + c.getApellido()) + "\",");
            out.print("\"tipo\":\"Cliente\",");
            out.print("\"correo\":\"" + escapeJson(c.getEmail()) + "\",");
            out.print("\"fechaRegistro\":\"" + escapeJson(c.getFechaAlta() != null ? c.getFechaAlta().toString() : "") + "\",");
            out.print("\"imagen\":\"" + escapeJson(c.getImagenUrl()) + "\"");
            out.print("}");
            if (i < clientes.size() - 1) out.print(",");
        }
        out.print("],");

        // Aerolíneas
        out.print("\"aerolineas\":[");
        for (int i = 0; i < aerolineas.size(); i++) {
            DtAerolinea a = aerolineas.get(i);
            out.print("{");
            out.print("\"id\":\"" + escapeJson(a.getNickname()) + "\",");
            out.print("\"nombre\":\"" + escapeJson(a.getNombre()) + "\",");
            out.print("\"tipo\":\"Aerolinea\",");
            out.print("\"correo\":\"" + escapeJson(a.getEmail()) + "\",");
            out.print("\"fechaRegistro\":\"\","); // Vacío para aerolíneas
            out.print("\"imagen\":\"" + escapeJson(a.getImagenUrl()) + "\"");
            out.print("}");
            if (i < aerolineas.size() - 1) out.print(",");
        }
        out.print("]");

        out.print("}");
    }

    private void escribirClienteDetalleJSON(DtCliente cliente, PrintWriter out) {
        out.print("{");
        out.print("\"tipo\":\"cliente\",");
        out.print("\"id\":\"" + escapeJson(cliente.getNickname()) + "\",");
        out.print("\"nickname\":\"" + escapeJson(cliente.getNickname()) + "\",");
        out.print("\"nombre\":\"" + escapeJson(cliente.getNombre()) + "\",");
        out.print("\"apellido\":\"" + escapeJson(cliente.getApellido()) + "\",");
        out.print("\"email\":\"" + escapeJson(cliente.getEmail()) + "\",");
        out.print("\"fechaNacimiento\":\"" + (cliente.getFechaNacimiento() != null ? cliente.getFechaNacimiento().toString() : "") + "\",");
        out.print("\"nacionalidad\":\"" + escapeJson(cliente.getNacionalidad()) + "\",");
        out.print("\"tipoDocumento\":\"" + escapeJson(cliente.getTipoDocumento() != null ? cliente.getTipoDocumento().toString() : "") + "\",");
        out.print("\"numeroDocumento\":\"" + escapeJson(cliente.getNumeroDocumento()) + "\",");
        out.print("\"fechaRegistro\":\"" + (cliente.getFechaAlta() != null ? cliente.getFechaAlta().toString() : "") + "\",");
        out.print("\"cantidadReservas\":" + (cliente.getReservas() != null ? cliente.getReservas().size() : 0) + ",");
        out.print("\"cantidadPaquetes\":" + (cliente.getPaquetesComprados() != null ? cliente.getPaquetesComprados().size() : 0));
        out.print("}");
    }

    private void escribirInfoAerolineaJSON(DtAerolinea aerolinea, PrintWriter out) {
        out.print("{");
        out.print("\"tipo\":\"Aerolinea\",");
        out.print("\"id\":\"" + escapeJson(aerolinea.getNickname()) + "\",");
        out.print("\"nombre\":\"" + escapeJson(aerolinea.getNombre()) + "\",");
        out.print("\"nombreCompleto\":\"" + escapeJson(aerolinea.getNombre()) + "\",");
        out.print("\"correo\":\"" + escapeJson(aerolinea.getEmail()) + "\",");
        out.print("\"fechaRegistro\":\"\",");
        out.print("\"imagen\":\"" + escapeJson(aerolinea.getImagenUrl()) + "\",");
        out.print("\"descripcion\":\"" + escapeJson(aerolinea.getDescripcion()) + "\",");
        out.print("\"sitioWeb\":\"" + escapeJson(aerolinea.getSitioWeb()) + "\",");
        out.print("\"cantidadRutas\":" + (aerolinea.getRutas() != null ? aerolinea.getRutas().size() : 0));
        out.print("}");
    }

    private void escribirRutasAerolineaJSON(List<DtRutaVuelo> rutas, PrintWriter out) {
        out.print("[");
        for (int i = 0; i < rutas.size(); i++) {
            DtRutaVuelo r = rutas.get(i);
            out.print("{");
            out.print("\"id\":\"" + escapeJson(r.getNombre()) + "\",");
            out.print("\"nombre\":\"" + escapeJson(r.getNombre()) + "\",");
            out.print("\"descripcion\":\"" + escapeJson(r.getDescripcion()) + "\",");
            out.print("\"descripcionCorta\":\"" + escapeJson(r.getDescripcionCorta()) + "\",");
            out.print("\"ciudadOrigen\":\"" + escapeJson(r.getCiudadOrigen()) + "\",");
            out.print("\"ciudadDestino\":\"" + escapeJson(r.getCiudadDestino()) + "\",");
            out.print("\"duracion\":\"" + escapeJson(r.getHora()) + "\",");
            out.print("\"costoTurista\":" + r.getCostoTurista() + ",");
            out.print("\"costoEjecutivo\":" + r.getCostoEjecutivo() + ",");
            out.print("\"costoEquipajeExtra\":" + r.getCostoEquipajeExtra() + ",");
            out.print("\"estado\":\"" + escapeJson(r.getEstado()) + "\",");
            out.print("\"fechaAlta\":\"" + (r.getFechaAlta() != null ? r.getFechaAlta().toString() : "") + "\"");
            out.print("}");

            if (i < rutas.size() - 1) out.print(",");
        }
        out.print("]");
    }

    private void escribirReservasClienteJSON(List<DtReserva> reservas, PrintWriter out) {
        out.print("[");
        for (int i = 0; i < reservas.size(); i++) {
            DtReserva reserva = reservas.get(i);
            out.print("{");
            out.print("\"id\":\"" + escapeJson(reserva.getId().toString()) + "\",");
            out.print("\"vuelo\":\"" + escapeJson(reserva.getVuelo()) + "\",");
            out.print("\"fecha\":\"" + escapeJson(reserva.getFecha() != null ? reserva.getFecha().toString() : "") + "\","); // Cambiado a getFecha()
            out.print("\"estado\":\"Confirmada\",");
            out.print("\"costo\":" + reserva.getCosto() + ",");
            out.print("\"tipoAsiento\":\"" + escapeJson(reserva.getTipoAsiento().toString()) + "\",");
            out.print("\"cantidadPasajes\":" + reserva.getCantidadPasajes() + ",");
            out.print("\"equipajeExtra\":" + reserva.getUnidadesEquipajeExtra());
            out.print("}");
            if (i < reservas.size() - 1) out.print(",");
        }
        out.print("]");
    }

    private void escribirPaquetesClienteJSON(List<DtPaquete> paquetes, PrintWriter out) {
        out.print("[");
        for (int i = 0; i < paquetes.size(); i++) {
            DtPaquete paquete = paquetes.get(i);
            out.print("{");
            out.print("\"id\":\"" + escapeJson(paquete.getNombre()) + "\",");
            out.print("\"nombre\":\"" + escapeJson(paquete.getNombre()) + "\",");
            out.print("\"compra\":\"" + escapeJson(paquete.getFechaAlta() != null ? paquete.getFechaAlta().toString() : "") + "\",");
            out.print("\"vencimiento\":\"" + escapeJson(calcularVencimiento(paquete)) + "\",");
            out.print("\"estado\":\"" + escapeJson(calcularEstadoPaquete(paquete)) + "\",");
            out.print("\"descripcion\":\"" + escapeJson(paquete.getDescripcion()) + "\",");
            out.print("\"costo\":" + paquete.getCosto() + ",");
            out.print("\"descuento\":" + paquete.getDescuentoPorc() + ",");
            out.print("\"vigenciaDias\":" + paquete.getPeriodoValidezDias() + ",");
            out.print("\"cantidadRutas\":" + (paquete.getItems() != null ? paquete.getItems().size() : 0));
            out.print("}");
            if (i < paquetes.size() - 1) out.print(",");
        }
        out.print("]");
    }

    private String calcularVencimiento(DtPaquete paquete) {
        if (paquete.getFechaAlta() == null || paquete.getPeriodoValidezDias() == 0) {
            return "";
        }
        return paquete.getFechaAlta().plusDays(paquete.getPeriodoValidezDias()).toString();
    }

    private String calcularEstadoPaquete(DtPaquete paquete) {
        if (paquete.getFechaAlta() == null || paquete.getPeriodoValidezDias() == 0) {
            return "Vigente";
        }

        java.time.LocalDate vencimiento = paquete.getFechaAlta().plusDays(paquete.getPeriodoValidezDias());
        java.time.LocalDate hoy = java.time.LocalDate.now();

        return vencimiento.isAfter(hoy) ? "Vigente" : "Vencido";
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
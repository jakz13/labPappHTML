package com.example.servlets;

import serviciosweb.*;
import com.example.util.PortUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/checkin-reservas")
public class CheckinReservasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clienteNickname = request.getParameter("cliente");
        String reservaIdParam = request.getParameter("reservaId");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Obtener el port del web service
            JuanViajesWS port = PortUtils.getPort(request);

            if (reservaIdParam != null && !reservaIdParam.isEmpty()) {
                // Consulta específica de check-in detallado
                Long reservaId = Long.parseLong(reservaIdParam);
                DtReserva reserva = port.consultarCheckinReserva(reservaId, clienteNickname);

                if (reserva == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Reserva no encontrada\"}");
                    return;
                }

                // Obtener hora de la ruta usando el web service
                String horaRuta = port.obtenerHoraRutaPorReserva(reservaId);

                out.print(construirJsonCheckinDetallado(reserva, horaRuta));

            } else {
                // Listar reservas con check-in realizados
                List<DtReserva> reservasConCheckin = port.obtenerReservasConCheckin(clienteNickname);

                out.print(construirJsonListaCheckin(reservasConCheckin));
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"ID de reserva inválido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error al consultar check-in: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Construye JSON con lista de reservas que tienen check-in realizado
     */
    private String construirJsonListaCheckin(List<DtReserva> reservas) {
        if (reservas == null || reservas.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < reservas.size(); i++) {
            DtReserva r = reservas.get(i);
            if (i > 0) json.append(",");

            String fechaCheckin = formatearFechaJAXB(r.getFechaCheckin());
            String horaEmbarque = formatearHoraJAXB(r.getHoraInicioEmbarque());
            String fechaReserva = formatearFechaJAXB(r.getFecha());

            json.append("{")
                    .append("\"id\":").append(r.getId()).append(",")
                    .append("\"vuelo\":\"").append(escapeJson(r.getVuelo())).append("\",")
                    .append("\"fechaReserva\":\"").append(fechaReserva).append("\",")
                    .append("\"fechaCheckin\":\"").append(fechaCheckin).append("\",")
                    .append("\"horaEmbarque\":\"").append(horaEmbarque).append("\",")
                    .append("\"tipoAsiento\":\"").append(escapeJson(r.getTipoAsiento() != null ? r.getTipoAsiento().toString() : "N/A")).append("\",")
                    .append("\"cantidadPasajeros\":").append(r.getCantidadPasajes()).append(",")
                    .append("\"costoTotal\":").append(r.getCosto())
                    .append("}");
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Construye JSON con información detallada del check-in
     */
    private String construirJsonCheckinDetallado(DtReserva reserva, String horaRuta) {
        StringBuilder json = new StringBuilder("{");

        String fechaCheckin = formatearFechaJAXB(reserva.getFechaCheckin());
        String horaEmbarque = formatearHoraJAXB(reserva.getHoraInicioEmbarque());
        String fechaReserva = formatearFechaJAXB(reserva.getFecha());

        json.append("\"id\":").append(reserva.getId()).append(",")
                .append("\"vuelo\":\"").append(escapeJson(reserva.getVuelo())).append("\",")
                .append("\"fechaReserva\":\"").append(fechaReserva).append("\",")
                .append("\"fechaCheckin\":\"").append(fechaCheckin).append("\",")
                .append("\"horaEmbarque\":\"").append(horaEmbarque).append("\",")
                .append("\"tipoAsiento\":\"").append(escapeJson(reserva.getTipoAsiento() != null ? reserva.getTipoAsiento().toString() : "N/A")).append("\",")
                .append("\"cantidadPasajes\":").append(reserva.getCantidadPasajes()).append(",")
                .append("\"equipajeExtra\":").append(reserva.getUnidadesEquipajeExtra()).append(",")
                .append("\"costoTotal\":").append(reserva.getCosto()).append(",")
                .append("\"asientosAsignados\":").append(construirJsonAsientos(reserva.getAsientosAsignados())).append(",")
                .append("\"pasajeros\":").append(construirJsonPasajeros(reserva.getPasajeros(), reserva.getAsientosAsignados()))
                .append("}");

        return json.toString();
    }

    /**
     * Construye array JSON de asientos asignados
     */
    private String construirJsonAsientos(List<String> asientos) {
        if (asientos == null || asientos.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < asientos.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(escapeJson(asientos.get(i))).append("\"");
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Construye array JSON de pasajeros con sus asientos asignados
     */
    private String construirJsonPasajeros(List<DtPasajero> pasajeros, List<String> asientos) {
        if (pasajeros == null || pasajeros.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < pasajeros.size(); i++) {
            DtPasajero p = pasajeros.get(i);
            String asiento = (asientos != null && i < asientos.size()) ? asientos.get(i) : "No asignado";

            if (i > 0) json.append(",");
            json.append("{")
                    .append("\"nombre\":\"").append(escapeJson(p.getNombre())).append("\",")
                    .append("\"apellido\":\"").append(escapeJson(p.getApellido())).append("\",")
                    .append("\"asiento\":\"").append(escapeJson(asiento)).append("\"")
                    .append("}");
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Formatea serviciosweb.LocalDate directamente sin convertir a java.time.LocalDate
     */
    private String formatearFechaJAXB(serviciosweb.LocalDate fecha) {
        if (fecha == null) return "N/A";
        try {
            String fechaString = fecha.toString();
            return fechaString;
        } catch (Exception e) {
            System.err.println("Error formateando fecha JAXB: " + e.getMessage());
            return "N/A";
        }
    }

    /**
     * Formatea serviciosweb.LocalTime directamente sin convertir a java.time.LocalTime
     */
    private String formatearHoraJAXB(serviciosweb.LocalTime hora) {
        if (hora == null) return "N/A";
        try {
            String horaString = hora.toString();
            return horaString;
        } catch (Exception e) {
            System.err.println("Error formateando hora JAXB: " + e.getMessage());
            return "N/A";
        }
    }

    /**
     * Escapa caracteres especiales para JSON
     */
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Método POST para realizar check-in
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String clienteNickname = request.getParameter("cliente");
            String reservaIdParam = request.getParameter("reservaId");
            String asientosParam = request.getParameter("asientos");
            String horaEmbarqueParam = request.getParameter("horaEmbarque");

            if (clienteNickname == null || reservaIdParam == null ||
                    asientosParam == null || horaEmbarqueParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Parámetros incompletos\"}");
                return;
            }

            Long reservaId = Long.parseLong(reservaIdParam);
            List<String> asientos = List.of(asientosParam.split(","));

            // Obtener el port del web service
            JuanViajesWS port = PortUtils.getPort(request);

            // Realizar check-in usando el web service
            port.realizarCheckinReserva(reservaId, asientos, horaEmbarqueParam);

            out.print("{\"success\":true,\"message\":\"Check-in realizado correctamente\",\"reservaId\":" + reservaId + "}");

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"ID de reserva inválido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error al realizar check-in: " + escapeJson(e.getMessage()) + "\"}");
        }
    }
}
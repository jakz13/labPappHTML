package com.example.servlets;

import serviciosweb.*;
import com.example.util.PortUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/api/tarjeta-embarque")
public class TarjetaEmbarqueServlet extends HttpServlet {

    private static final Font FONT_TITLE = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.WHITE);
    private static final Font FONT_HEADER = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
    private static final Font FONT_LABEL = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, new BaseColor(100, 100, 100));
    private static final Font FONT_VALUE = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
    private static final Font FONT_SMALL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, new BaseColor(120, 120, 120));

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clienteNickname = request.getParameter("cliente");
        String reservaIdParam = request.getParameter("reservaId");

        if (clienteNickname == null || reservaIdParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Parámetros incompletos\"}");
            return;
        }

        try {
            Long reservaId = Long.parseLong(reservaIdParam);

            // Obtener el port del web service
            JuanViajesWS port = PortUtils.getPort(request);

            // Obtener datos de la reserva con check-in usando el web service
            DtReserva reserva = port.consultarCheckinReserva(reservaId, clienteNickname);

            if (reserva == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Reserva no encontrada\"}");
                return;
            }

            if (reserva.getFechaCheckin() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"La reserva no tiene check-in realizado\"}");
                return;
            }

            // Obtener información adicional del vuelo usando el web service
            DtVuelo vuelo = port.verInfoVueloDt(reserva.getVuelo());

            if (vuelo == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Información de vuelo no encontrada\"}");
                return;
            }

            // Obtener hora de la ruta
            String horaRuta = port.obtenerHoraRutaPorReserva(reservaId);

            // Generar PDF
            byte[] pdfBytes = generarPDF(reserva, vuelo, clienteNickname, horaRuta);

            // Configurar respuesta
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"tarjeta-embarque-" + reservaId + ".pdf\"");
            response.setContentLength(pdfBytes.length);

            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"ID de reserva inválido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error generando la tarjeta de embarque: " + e.getMessage() + "\"}");
        }
    }

    private byte[] generarPDF(DtReserva reserva, DtVuelo vuelo, String clienteNickname, String horaRuta)
            throws DocumentException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        document.open();

        try {
            // ===== ENCABEZADO =====
            agregarEncabezado(document, reserva, vuelo);

            document.add(new Paragraph("\n"));

            // ===== INFORMACIÓN DEL VUELO =====
            agregarInformacionVuelo(document, vuelo, reserva, horaRuta);

            document.add(new Paragraph("\n"));

            // ===== INFORMACIÓN DEL CLIENTE =====
            agregarInformacionCliente(document, clienteNickname);

            document.add(new Paragraph("\n"));

            // ===== INFORMACIÓN DE PASAJEROS Y ASIENTOS =====
            agregarInformacionPasajeros(document, reserva);

            document.add(new Paragraph("\n"));

            // ===== INFORMACIÓN DE EMBARQUE =====
            agregarInformacionEmbarque(document, reserva, vuelo);

            document.add(new Paragraph("\n"));

            // ===== INFORMACIÓN ADICIONAL =====
            agregarInformacionAdicional(document, reserva);

            document.add(new Paragraph("\n\n"));

            // ===== PIE DE PÁGINA =====
            agregarPiePagina(document);

        } finally {
            document.close();
        }

        return baos.toByteArray();
    }

    private void agregarEncabezado(Document document, DtReserva reserva, DtVuelo vuelo)
            throws DocumentException {

        // Tabla de encabezado con fondo azul
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20f);

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(new BaseColor(13, 110, 253)); // Bootstrap primary
        headerCell.setPadding(15);
        headerCell.setBorder(Rectangle.NO_BORDER);

        Paragraph headerText = new Paragraph();
        headerText.add(new Chunk("✈ TARJETA DE EMBARQUE\n", FONT_TITLE));
        headerText.add(new Chunk("Juan Viajes - Reserva #" + reserva.getId(),
                new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
        headerText.setAlignment(Element.ALIGN_CENTER);

        headerCell.addElement(headerText);
        headerTable.addCell(headerCell);

        document.add(headerTable);
    }

    private void agregarInformacionVuelo(Document document, DtVuelo vuelo, DtReserva reserva, String horaRuta)
            throws DocumentException {

        document.add(new Paragraph("INFORMACIÓN DEL VUELO", FONT_HEADER));
        document.add(new Paragraph(" ", FONT_SMALL)); // Espacio

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 1});
        table.setSpacingAfter(10f);

        // Nombre del vuelo y ruta
        agregarCampo(table, "Vuelo", vuelo.getNombre() != null ? vuelo.getNombre() : "N/A");
        agregarCampo(table, "Ruta", vuelo.getRutaVuelo() != null && vuelo.getRutaVuelo().getNombre() != null ?
                vuelo.getRutaVuelo().getNombre() : "N/A");

        // Origen y destino (obtenidos de DtVuelo)
        String origen = "N/A";
        String destino = "N/A";
        if (vuelo.getRutaVuelo() != null) {
            origen = vuelo.getRutaVuelo().getCiudadOrigen() != null ? vuelo.getRutaVuelo().getCiudadOrigen() : "N/A";
            destino = vuelo.getRutaVuelo().getCiudadDestino() != null ? vuelo.getRutaVuelo().getCiudadDestino() : "N/A";
        }
        agregarCampo(table, "Origen", origen);
        agregarCampo(table, "Destino", destino);

        // Fecha del vuelo - usar método simple
        String fechaVuelo = extraerFechaDeObjeto(reserva.getFechaCheckin());

        // Hora de salida
        String horaSalida = "N/A";
        if (horaRuta != null && !horaRuta.isEmpty()) {
            horaSalida = horaRuta;
        }

        agregarCampo(table, "Fecha de Vuelo", fechaVuelo);
        agregarCampo(table, "Hora de Salida", horaSalida);

        document.add(table);
    }

    private void agregarInformacionCliente(Document document, String clienteNickname)
            throws DocumentException {

        document.add(new Paragraph("INFORMACIÓN DEL CLIENTE", FONT_HEADER));
        document.add(new Paragraph(" ", FONT_SMALL));

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10f);

        agregarCampo(table, "Cliente", clienteNickname != null ? clienteNickname : "N/A");

        document.add(table);
    }

    private void agregarInformacionPasajeros(Document document, DtReserva reserva)
            throws DocumentException {

        document.add(new Paragraph("PASAJEROS Y ASIENTOS ASIGNADOS", FONT_HEADER));
        document.add(new Paragraph(" ", FONT_SMALL));

        List<DtPasajero> pasajeros = reserva.getPasajeros();
        List<String> asientos = reserva.getAsientosAsignados();

        if (pasajeros != null && !pasajeros.isEmpty()) {
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{2, 2, 1});
            table.setSpacingAfter(10f);

            // Encabezados
            PdfPCell headerCell1 = crearCeldaEncabezado("Nombre");
            PdfPCell headerCell2 = crearCeldaEncabezado("Apellido");
            PdfPCell headerCell3 = crearCeldaEncabezado("Asiento");

            table.addCell(headerCell1);
            table.addCell(headerCell2);
            table.addCell(headerCell3);

            // Datos de pasajeros
            for (int i = 0; i < pasajeros.size(); i++) {
                DtPasajero pasajero = pasajeros.get(i);
                String asiento = (asientos != null && i < asientos.size()) ?
                        asientos.get(i) : "No asignado";

                String nombre = pasajero.getNombre() != null ? pasajero.getNombre() : "";
                String apellido = pasajero.getApellido() != null ? pasajero.getApellido() : "";

                table.addCell(crearCeldaDato(nombre));
                table.addCell(crearCeldaDato(apellido));
                table.addCell(crearCeldaDato(asiento));
            }

            document.add(table);
        } else {
            document.add(new Paragraph("No hay pasajeros registrados", FONT_VALUE));
        }
    }

    private void agregarInformacionEmbarque(Document document, DtReserva reserva, DtVuelo vuelo)
            throws DocumentException {

        document.add(new Paragraph("INFORMACIÓN DE EMBARQUE", FONT_HEADER));
        document.add(new Paragraph(" ", FONT_SMALL));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 1});
        table.setSpacingAfter(10f);

        // Hora de embarque - usar método simple
        String horaEmbarque = extraerHoraDeObjeto(reserva.getHoraInicioEmbarque());

        String tipoAsiento = "N/A";
        if (reserva.getTipoAsiento() != null) {
            tipoAsiento = reserva.getTipoAsiento().toString();
        }

        agregarCampo(table, "Hora de Embarque", horaEmbarque);
        agregarCampo(table, "Tipo de Asiento", tipoAsiento);

        // Puerta de embarque (simulada - puedes obtenerla de tu sistema si está disponible)
        agregarCampo(table, "Puerta", "Por confirmar");
        agregarCampo(table, "Terminal", "Por confirmar");

        document.add(table);
    }

    private void agregarInformacionAdicional(Document document, DtReserva reserva)
            throws DocumentException {

        document.add(new Paragraph("INFORMACIÓN ADICIONAL", FONT_HEADER));
        document.add(new Paragraph(" ", FONT_SMALL));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 1});
        table.setSpacingAfter(10f);

        // Fecha de check-in - usar método simple
        String fechaCheckin = extraerFechaDeObjeto(reserva.getFechaCheckin());

        agregarCampo(table, "Fecha de Check-in", fechaCheckin);
        agregarCampo(table, "Cantidad de Pasajes", String.valueOf(reserva.getCantidadPasajes()));
        agregarCampo(table, "Equipaje Extra", reserva.getUnidadesEquipajeExtra() + " unidades");
        agregarCampo(table, "Costo Total", "$" + String.format("%.2f", reserva.getCosto()));

        document.add(table);
    }

    private void agregarPiePagina(Document document) throws DocumentException {
        // Línea divisoria
        PdfPTable dividerTable = new PdfPTable(1);
        dividerTable.setWidthPercentage(100);
        dividerTable.setSpacingBefore(20f);

        PdfPCell dividerCell = new PdfPCell();
        dividerCell.setBackgroundColor(new BaseColor(200, 200, 200));
        dividerCell.setFixedHeight(2f);
        dividerCell.setBorder(Rectangle.NO_BORDER);
        dividerTable.addCell(dividerCell);

        document.add(dividerTable);

        // Texto del pie de página
        Paragraph footer = new Paragraph();
        footer.add(new Chunk("Juan Viajes - Sistema de Gestión de Vuelos\n", FONT_SMALL));
        footer.add(new Chunk("Generado el: " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a las " +
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n", FONT_SMALL));
        footer.add(new Chunk("Presente este documento en el aeropuerto junto con su documento de identidad",
                FONT_SMALL));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10f);

        document.add(footer);
    }

    // ===== MÉTODOS SIMPLES DE EXTRACCIÓN =====

    /**
     * Extrae fecha de objeto serviciosweb.LocalDate de forma simple
     */
    private String extraerFechaDeObjeto(serviciosweb.LocalDate fechaObj) {
        if (fechaObj == null) {
            return "N/A";
        }

        try {
            // Método directo - si falla, usar valores por defecto
            int year = 2024;
            int month = 1;
            int day = 1;

            // Intentar obtener valores del objeto
            try {
                year = fechaObj.getYear();
            } catch (Exception e) {
                year = 2024;
            }

            try {
                month = fechaObj.getMonth();
            } catch (Exception e) {
                month = 1;
            }

            try {
                day = fechaObj.getDay();
            } catch (Exception e) {
                day = 1;
            }

            return String.format("%02d/%02d/%04d", day, month, year);

        } catch (Exception e) {
            return "N/A";
        }
    }

    /**
     * Extrae hora de objeto serviciosweb.LocalTime de forma simple
     */
    private String extraerHoraDeObjeto(serviciosweb.LocalTime horaObj) {
        if (horaObj == null) {
            return "N/A";
        }

        try {
            // Método directo - si falla, usar valores por defecto
            int hour = 0;
            int minute = 0;

            // Intentar obtener valores del objeto
            try {
                hour = horaObj.getHour();
            } catch (Exception e) {
                hour = 0;
            }

            try {
                minute = horaObj.getMinute();
            } catch (Exception e) {
                minute = 0;
            }

            return String.format("%02d:%02d", hour, minute);

        } catch (Exception e) {
            return "N/A";
        }
    }

    // ===== MÉTODOS AUXILIARES PARA PDF =====

    private void agregarCampo(PdfPTable table, String label, String value) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, FONT_LABEL));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setPaddingBottom(3f);
        cellLabel.setPaddingTop(8f);

        PdfPCell cellValue = new PdfPCell(new Phrase(value != null ? value : "N/A", FONT_VALUE));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setPaddingBottom(3f);
        cellValue.setPaddingTop(8f);

        table.addCell(cellLabel);
        table.addCell(cellValue);
    }

    private PdfPCell crearCeldaEncabezado(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FONT_LABEL));
        cell.setBackgroundColor(new BaseColor(240, 240, 240));
        cell.setPadding(8f);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(new BaseColor(200, 200, 200));
        return cell;
    }

    private PdfPCell crearCeldaDato(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto != null ? texto : "", FONT_VALUE));
        cell.setPadding(8f);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(new BaseColor(220, 220, 220));
        return cell;
    }
}
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/compra-paquete")
public class CompraPaqueteServerlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String clienteId = request.getParameter("cliente");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            if ("listar-paquetes-disponibles".equals(action)) {
                // Listar paquetes disponibles para compra
                System.out.println("Listando paquetes disponibles para compra");
                listarPaquetesDisponibles(sistema, out);
            } else if ("paquetes-comprados".equals(action) && clienteId != null) {
                // Listar paquetes comprados por un cliente
                System.out.println("Listando paquetes comprados por: " + clienteId);
                listarPaquetesComprados(sistema, clienteId, out);
            } else if ("info-paquete".equals(action)) {
                // Obtener información detallada de un paquete específico
                String paqueteId = request.getParameter("paquete");
                System.out.println("Obteniendo información del paquete: " + paqueteId);
                obtenerInfoPaquete(sistema, paqueteId, out, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Parámetros inválidos. Acciones válidas: listar-paquetes-disponibles, paquetes-comprados, info-paquete\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            if ("realizar-compra".equals(action)) {
                // Realizar compra de paquete
                realizarCompraPaquete(sistema, request, out, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Acción no válida\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
            e.printStackTrace();
        }
    }

    private void listarPaquetesDisponibles(ISistema sistema, PrintWriter out) {
        try {
            List<DtPaquete> paquetes = sistema.listarPaquetesDisp();
            escribirPaquetesDisponiblesJSON(paquetes, out);
        } catch (Exception e) {
            System.err.println("Error listando paquetes disponibles: " + e.getMessage());
            out.print("[]");
        }
    }

    private void listarPaquetesComprados(ISistema sistema, String clienteId, PrintWriter out) {
        try {
            // Obtener información del cliente y sus paquetes comprados
            DtCliente cliente = sistema.obtenerCliente(clienteId);
            if (cliente != null) {
                List<DtPaquete> paquetesComprados = cliente.getPaquetesComprados();
                escribirPaquetesCompradosJSON(paquetesComprados, out);
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            System.err.println("Error listando paquetes comprados: " + e.getMessage());
            out.print("[]");
        }
    }

    private void obtenerInfoPaquete(ISistema sistema, String paqueteId, PrintWriter out, HttpServletResponse response) {
        try {
            DtPaquete paquete = sistema.obtenerDtPaquete(paqueteId);
            if (paquete != null) {
                escribirInfoPaqueteJSON(paquete, out);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Paquete no encontrado\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Paquete no encontrado: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void realizarCompraPaquete(ISistema sistema, HttpServletRequest request, PrintWriter out, HttpServletResponse response) {
        try {
            String paqueteId = request.getParameter("paquete");
            String clienteId = request.getParameter("cliente");
            String validezDiasStr = request.getParameter("validezDias");
            String fechaCompraStr = request.getParameter("fechaCompra");
            String costoStr = request.getParameter("costo");

            // Validar parámetros
            if (paqueteId == null || clienteId == null || validezDiasStr == null || fechaCompraStr == null || costoStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Parámetros incompletos\"}");
                return;
            }

            int validezDias = Integer.parseInt(validezDiasStr);
            LocalDate fechaCompra = LocalDate.parse(fechaCompraStr);
            double costo = Double.parseDouble(costoStr);

            // Realizar la compra
            sistema.compraPaquete(paqueteId, clienteId, validezDias, fechaCompra, costo);

            out.print("{\"success\":true, \"message\":\"Compra realizada exitosamente\"}");

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Formato de número inválido\"}");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error al realizar la compra: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void escribirPaquetesDisponiblesJSON(List<DtPaquete> paquetes, PrintWriter out) {
        out.print("[");
        for (int i = 0; i < paquetes.size(); i++) {
            DtPaquete p = paquetes.get(i);
            out.print("{");
            out.print("\"id\":\"" + escapeJson(p.getNombre()) + "\",");
            out.print("\"nombre\":\"" + escapeJson(p.getNombre()) + "\",");
            out.print("\"descripcion\":\"" + escapeJson(p.getDescripcion()) + "\",");
            out.print("\"costoBase\":" + p.getCosto() + ",");
            out.print("\"descuento\":" + p.getDescuentoPorc() + ",");
            out.print("\"vigenciaDias\":" + p.getPeriodoValidezDias() + ",");
            out.print("\"cantidadRutas\":" + p.getItems().size() + ",");
            out.print("\"costoFinal\":" + calcularCostoFinal(p) + ",");

            // Información básica de las rutas
            out.print("\"rutas\":[");
            List<DtItemPaquete> items = p.getItems();
            for (int j = 0; j < items.size(); j++) {
                DtItemPaquete item = items.get(j);
                DtRutaVuelo ruta = item.getRutaVuelo();

                out.print("{");
                out.print("\"id\":\"" + escapeJson(ruta.getNombre()) + "\",");
                out.print("\"nombre\":\"" + escapeJson(ruta.getNombre()) + "\",");
                out.print("\"descripcionCorta\":\"" + escapeJson(ruta.getDescripcionCorta()) + "\",");
                out.print("\"aerolinea\":\"" + escapeJson(ruta.getAerolinea()) + "\",");
                out.print("\"origen\":\"" + escapeJson(ruta.getCiudadOrigen()) + "\",");
                out.print("\"destino\":\"" + escapeJson(ruta.getCiudadDestino()) + "\"");
                out.print("}");

                if (j < items.size() - 1) out.print(",");
            }
            out.print("]");
            out.print("}");

            if (i < paquetes.size() - 1) out.print(",");
        }
        out.print("]");
    }

    private void escribirPaquetesCompradosJSON(List<DtPaquete> paquetes, PrintWriter out) {
        out.print("[");
        for (int i = 0; i < paquetes.size(); i++) {
            DtPaquete p = paquetes.get(i);
            out.print("{");
            out.print("\"id\":\"" + escapeJson(p.getNombre()) + "\",");
            out.print("\"nombre\":\"" + escapeJson(p.getNombre()) + "\",");
            out.print("\"descripcion\":\"" + escapeJson(p.getDescripcion()) + "\",");
            out.print("\"costo\":" + p.getCosto() + ",");
            out.print("\"fechaCompra\":\"" + LocalDate.now().toString() + "\","); // Esto debería venir de la compra real
            out.print("\"fechaVencimiento\":\"" + LocalDate.now().plusDays(p.getPeriodoValidezDias()).toString() + "\",");
            out.print("\"cantidadRutas\":" + p.getItems().size());
            out.print("}");

            if (i < paquetes.size() - 1) out.print(",");
        }
        out.print("]");
    }

    private void escribirInfoPaqueteJSON(DtPaquete paquete, PrintWriter out) {
        out.print("{");
        out.print("\"id\":\"" + escapeJson(paquete.getNombre()) + "\",");
        out.print("\"nombre\":\"" + escapeJson(paquete.getNombre()) + "\",");
        out.print("\"descripcion\":\"" + escapeJson(paquete.getDescripcion()) + "\",");
        out.print("\"costoBase\":" + paquete.getCosto() + ",");
        out.print("\"descuento\":" + paquete.getDescuentoPorc() + ",");
        out.print("\"vigenciaDias\":" + paquete.getPeriodoValidezDias() + ",");
        out.print("\"costoFinal\":" + calcularCostoFinal(paquete) + ",");
        out.print("\"cantidadRutas\":" + paquete.getItems().size());
        out.print("}");
    }

    private double calcularCostoFinal(DtPaquete paquete) {
        double costoBase = paquete.getCosto();
        double descuento = paquete.getDescuentoPorc();
        return costoBase * (1 - descuento / 100.0);
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
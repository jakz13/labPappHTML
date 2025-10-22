package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.List;

@WebServlet("/consulta-paquete")
public class ConsultaDePaqueteServerlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String paqueteId = request.getParameter("paquete");
        String rutaId = request.getParameter("ruta");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            if ("listar-paquetes".equals(action)) {
                // Listar todos los paquetes disponibles CON información básica de rutas
                System.out.println("Listando todos los paquetes con información básica de rutas");
                listarPaquetesConRutas(sistema, out);
            } else if ("obtener-paquete".equals(action) && paqueteId != null) {
                // Obtener información específica de un paquete (detalle completo)
                System.out.println("Obteniendo información detallada del paquete: " + paqueteId);
                obtenerPaqueteDetalle(sistema, paqueteId, out, response);
            } else if ("obtener-ruta".equals(action) && paqueteId != null && rutaId != null) {
                // Obtener información detallada de una ruta específica dentro de un paquete
                System.out.println("Obteniendo ruta " + rutaId + " del paquete " + paqueteId);
                obtenerRutaDetalle(sistema, paqueteId, rutaId, out, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Parámetros inválidos. Acciones válidas: listar-paquetes, obtener-paquete, obtener-ruta\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
            e.printStackTrace();
        }
    }

    private void listarPaquetesConRutas(ISistema sistema, PrintWriter out) {
        try {
            List<DtPaquete> paquetes = sistema.listarPaquetes();
            escribirPaquetesConRutasBasicasJSON(paquetes, out);
        } catch (Exception e) {
            System.err.println("Error listando paquetes: " + e.getMessage());
            out.print("[]");
        }
    }

    private void obtenerPaqueteDetalle(ISistema sistema, String paqueteId, PrintWriter out, HttpServletResponse response) {
        try {
            DtPaquete paquete = sistema.obtenerDtPaquete(paqueteId);
            if (paquete != null) {
                // Obtener los items (rutas) del paquete
                List<DtItemPaquete> items = sistema.getDtItemRutasPaquete(paqueteId);
                escribirPaqueteDetalleJSON(paquete, items, out);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Paquete no encontrado\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Paquete no encontrado: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void obtenerRutaDetalle(ISistema sistema, String paqueteId, String rutaId, PrintWriter out, HttpServletResponse response) {
        try {
            // Primero obtenemos el paquete para validar que existe
            DtPaquete paquete = sistema.obtenerDtPaquete(paqueteId);
            if (paquete == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Paquete no encontrado\"}");
                return;
            }

            // Buscar la ruta en los items del paquete
            List<DtItemPaquete> items = sistema.getDtItemRutasPaquete(paqueteId);
            DtItemPaquete itemEncontrado = null;

            for (DtItemPaquete item : items) {
                if (item.getRutaVuelo().getNombre().equals(rutaId)) {
                    itemEncontrado = item;
                    break;
                }
            }

            if (itemEncontrado != null) {
                // Obtener información completa de la ruta
                DtRutaVuelo ruta = itemEncontrado.getRutaVuelo();
                escribirRutaDetalleJSON(ruta, itemEncontrado, out);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Ruta no encontrada en el paquete\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Error obteniendo ruta: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void escribirPaquetesConRutasBasicasJSON(List<DtPaquete> paquetes, PrintWriter out) {
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
                out.print("\"destino\":\"" + escapeJson(ruta.getCiudadDestino()) + "\",");
                out.print("\"duracion\":\"" + escapeJson(ruta.getHora()) + "\",");
                out.print("\"cantidadAsientos\":" + item.getCantAsientos() + ",");
                out.print("\"tipoAsiento\":\"" + escapeJson(item.getTipoAsiento()) + "\",");
                out.print("\"costoTurista\":" + ruta.getCostoTurista() + ",");
                out.print("\"costoEjecutivo\":" + ruta.getCostoEjecutivo() + ",");
                out.print("\"costoEquipaje\":" + ruta.getCostoEquipajeExtra() + ",");
                out.print("\"estado\":\"" + escapeJson(ruta.getEstado()) + "\",");
                out.print("\"imagen\":\"" + escapeJson(ruta.getImagenUrl()) + "\"");
                out.print("}");

                if (j < items.size() - 1) out.print(",");
            }
            out.print("]");
            out.print("}");

            if (i < paquetes.size() - 1) out.print(",");
        }
        out.print("]");
    }

    private void escribirPaqueteDetalleJSON(DtPaquete paquete, List<DtItemPaquete> items, PrintWriter out) {
        out.print("{");
        out.print("\"id\":\"" + escapeJson(paquete.getNombre()) + "\",");
        out.print("\"nombre\":\"" + escapeJson(paquete.getNombre()) + "\",");
        out.print("\"descripcion\":\"" + escapeJson(paquete.getDescripcion()) + "\",");
        out.print("\"costoBase\":" + paquete.getCosto() + ",");
        out.print("\"descuento\":" + paquete.getDescuentoPorc() + ",");
        out.print("\"vigenciaDias\":" + paquete.getPeriodoValidezDias() + ",");
        out.print("\"costoFinal\":" + calcularCostoFinal(paquete) + ",");

        // Beneficios del paquete
        out.print("\"beneficios\":[");
        out.print("\"" + escapeJson(paquete.getDescuentoPorc() + "% de descuento") + "\",");
        out.print("\"" + escapeJson("Vigencia de " + paquete.getPeriodoValidezDias() + " días") + "\"");
        out.print("],");

        // Rutas del paquete (detalle completo)
        out.print("\"rutas\":[");
        for (int i = 0; i < items.size(); i++) {
            DtItemPaquete item = items.get(i);
            DtRutaVuelo ruta = item.getRutaVuelo();

            out.print("{");
            out.print("\"id\":\"" + escapeJson(ruta.getNombre()) + "\",");
            out.print("\"nombre\":\"" + escapeJson(ruta.getNombre()) + "\",");
            out.print("\"descripcionCorta\":\"" + escapeJson(ruta.getDescripcionCorta()) + "\",");
            out.print("\"descripcionCompleta\":\"" + escapeJson(ruta.getDescripcion()) + "\",");
            out.print("\"aerolinea\":\"" + escapeJson(ruta.getAerolinea()) + "\",");
            out.print("\"origen\":\"" + escapeJson(ruta.getCiudadOrigen()) + "\",");
            out.print("\"destino\":\"" + escapeJson(ruta.getCiudadDestino()) + "\",");
            out.print("\"duracion\":\"" + escapeJson(ruta.getHora()) + "\",");
            out.print("\"cantidadAsientos\":" + item.getCantAsientos() + ",");
            out.print("\"tipoAsiento\":\"" + escapeJson(item.getTipoAsiento()) + "\",");
            out.print("\"costoTurista\":" + ruta.getCostoTurista() + ",");
            out.print("\"costoEjecutivo\":" + ruta.getCostoEjecutivo() + ",");
            out.print("\"costoEquipaje\":" + ruta.getCostoEquipajeExtra() + ",");
            out.print("\"estado\":\"" + escapeJson(ruta.getEstado()) + "\",");
            out.print("\"categorias\":\"" + escapeJson(String.join(", ", ruta.getCategorias())) + "\",");
            out.print("\"imagen\":\"" + escapeJson(ruta.getImagenUrl()) + "\"");
            out.print("}");

            if (i < items.size() - 1) out.print(",");
        }
        out.print("]");
        out.print("}");
    }

    private void escribirRutaDetalleJSON(DtRutaVuelo ruta, DtItemPaquete item, PrintWriter out) {
        out.print("{");
        out.print("\"id\":\"" + escapeJson(ruta.getNombre()) + "\",");
        out.print("\"nombre\":\"" + escapeJson(ruta.getNombre()) + "\",");
        out.print("\"descripcionCorta\":\"" + escapeJson(ruta.getDescripcionCorta()) + "\",");
        out.print("\"descripcionCompleta\":\"" + escapeJson(ruta.getDescripcion()) + "\",");
        out.print("\"aerolinea\":\"" + escapeJson(ruta.getAerolinea()) + "\",");
        out.print("\"origen\":\"" + escapeJson(ruta.getCiudadOrigen()) + "\",");
        out.print("\"destino\":\"" + escapeJson(ruta.getCiudadDestino()) + "\",");
        out.print("\"duracion\":\"" + escapeJson(ruta.getHora()) + "\",");
        out.print("\"horaSalida\":\"" + escapeJson(ruta.getHora()) + "\",");
        out.print("\"costoTurista\":" + ruta.getCostoTurista() + ",");
        out.print("\"costoEjecutivo\":" + ruta.getCostoEjecutivo() + ",");
        out.print("\"costoEquipaje\":" + ruta.getCostoEquipajeExtra() + ",");
        out.print("\"cantidadAsientos\":" + item.getCantAsientos() + ",");
        out.print("\"tipoAsiento\":\"" + escapeJson(item.getTipoAsiento()) + "\",");
        out.print("\"categorias\":\"" + escapeJson(String.join(", ", ruta.getCategorias())) + "\",");
        out.print("\"estado\":\"" + escapeJson(ruta.getEstado()) + "\",");
        out.print("\"imagen\":\"" + escapeJson(ruta.getImagenUrl()) + "\"");
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
package com.example.servlets;

import com.example.util.PortUtils;
import serviciosweb.JuanViajesWS;
import serviciosweb.DtPaquete;
import serviciosweb.DtItemPaquete;
import serviciosweb.DtRutaVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/consulta-paquete")
public class ConsultaDePaqueteServlet extends HttpServlet {

    private JuanViajesWS servicioWeb;

    private void enviarError(HttpServletResponse response, String mensaje, int statusCode) throws IOException {
        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"error\":\"" + escapeJson(mensaje) + "\"}");
        }
    }

    @Override
    public void init() throws ServletException {
        try {
            // No inicializamos el servicio aquí, lo haremos en cada request usando PortUtils
            System.out.println("✅ ConsultaDePaqueteServerlet inicializado - Usará PortUtils para obtener servicio web");
        } catch (Exception e) {
            System.err.println("❌ Error inicializando servlet: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("No se pudo inicializar el servlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Obtener el servicio web usando PortUtils
        servicioWeb = PortUtils.getPort(request);

        String action = request.getParameter("action");
        String paqueteId = request.getParameter("paquete");
        String rutaId = request.getParameter("ruta");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Verificar que el servicio esté disponible
        if (servicioWeb == null) {
            enviarError(response, "Servicio web no disponible", HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            System.out.println("=== CONSULTA PAQUETE ===");
            System.out.println("Action: " + action);
            System.out.println("Paquete: " + paqueteId);
            System.out.println("Ruta: " + rutaId);

            if ("listar-paquetes".equals(action)) {
                System.out.println("Listando todos los paquetes con información básica de rutas");
                listarPaquetesConRutas(out);
            } else if ("obtener-paquete".equals(action) && paqueteId != null) {
                System.out.println("Obteniendo información detallada del paquete: " + paqueteId);
                obtenerPaqueteDetalle(paqueteId, out, response);
            } else if ("obtener-ruta".equals(action) && paqueteId != null && rutaId != null) {
                System.out.println("Obteniendo ruta " + rutaId + " del paquete " + paqueteId);
                obtenerRutaDetalle(paqueteId, rutaId, out, response);
            } else {
                enviarError(response, "Parámetros inválidos. Acciones válidas: listar-paquetes, obtener-paquete, obtener-ruta",
                        HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            System.err.println("💥 ERROR EN SERVLET CONSULTA PAQUETE: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, "Error interno: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void listarPaquetesConRutas(PrintWriter out) {
        try {
            List<DtPaquete> paquetes = servicioWeb.listarPaquetes();
            if (paquetes == null) {
                paquetes = new ArrayList<>();
            }
            System.out.println("📦 Número de paquetes encontrados: " + paquetes.size());
            escribirPaquetesConRutasBasicasJSON(paquetes, out);
        } catch (Exception e) {
            System.err.println("💥 ERROR listando paquetes: " + e.getMessage());
            e.printStackTrace();
            out.print("[]");
        }
    }

    private void obtenerPaqueteDetalle(String paqueteId, PrintWriter out, HttpServletResponse response) throws IOException {
        try {
            System.out.println("🔍 Buscando paquete: " + paqueteId);
            DtPaquete paquete = servicioWeb.obtenerDtPaquete(paqueteId);

            if (paquete != null) {
                System.out.println("📋 Paquete obtenido: " + paquete.getNombre());
                List<DtItemPaquete> items = servicioWeb.getDtItemRutasPaquete(paqueteId);
                if (items == null) {
                    items = new ArrayList<>();
                }
                System.out.println("Número de rutas en paquete: " + items.size());
                escribirPaqueteDetalleJSON(paquete, items, out);
            } else {
                System.out.println("❌ Paquete no encontrado: " + paqueteId);
                enviarError(response, "Paquete no encontrado", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("💥 ERROR obteniendo paquete: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, "Error obteniendo paquete: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void obtenerRutaDetalle(String paqueteId, String rutaId, PrintWriter out, HttpServletResponse response) throws IOException {
        try {
            System.out.println("🔍 Buscando ruta " + rutaId + " en paquete " + paqueteId);

            DtPaquete paquete = servicioWeb.obtenerDtPaquete(paqueteId);
            if (paquete == null) {
                System.out.println("❌ Paquete no encontrado: " + paqueteId);
                enviarError(response, "Paquete no encontrado", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            List<DtItemPaquete> items = servicioWeb.getDtItemRutasPaquete(paqueteId);
            DtItemPaquete itemEncontrado = null;

            if (items != null) {
                for (DtItemPaquete item : items) {
                    if (item.getRutaVuelo() != null && rutaId.equals(item.getRutaVuelo().getNombre())) {
                        itemEncontrado = item;
                        break;
                    }
                }
            }

            if (itemEncontrado != null) {
                DtRutaVuelo ruta = itemEncontrado.getRutaVuelo();
                System.out.println("📋 Ruta encontrada: " + ruta.getNombre());
                escribirRutaDetalleJSON(ruta, itemEncontrado, out);
            } else {
                System.out.println("❌ Ruta no encontrada en el paquete: " + rutaId);
                enviarError(response, "Ruta no encontrada en el paquete", HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (Exception e) {
            System.err.println("💥 ERROR obteniendo ruta: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, "Error obteniendo ruta: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
                out.print("\"estado\":\"" + escapeJson(String.valueOf(ruta.getEstado())) + "\",");
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
            out.print("\"estado\":\"" + escapeJson(String.valueOf(ruta.getEstado())) + "\",");
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
        out.print("\"estado\":\"" + escapeJson(String.valueOf(ruta.getEstado())) + "\",");
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
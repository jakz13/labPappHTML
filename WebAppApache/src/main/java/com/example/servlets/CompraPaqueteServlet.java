package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.time.LocalDate;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;

@WebServlet("/compra-paquete")
public class CompraPaqueteServlet extends HttpServlet {

    private ISistema sistema;

    @Override
    public void init() throws ServletException {
        // Cargar el sistema UNA SOLA VEZ al iniciar el servlet
        sistema = Fabrica.getInstance().getISistema();
        try {
            sistema.cargarDesdeBd();
            System.out.println("✅ Sistema cargado correctamente en init() - Compra Paquete");
        } catch (Exception e) {
            System.err.println("❌ Error cargando sistema en init(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String clienteId = request.getParameter("cliente");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            System.out.println("=== COMPRA PAQUETE GET ===");
            System.out.println("Action: " + action);
            System.out.println("Cliente: " + clienteId);

            // NO llamar cargarDesdeBd() aquí - ya se cargó en init()

            if ("listar-paquetes-disponibles".equals(action)) {
                // Listar TODOS los paquetes VÁLIDOS (filtrados)
                System.out.println("Listando paquetes VÁLIDOS para compra");
                listarPaquetesValidos(sistema, out);
            } else if ("paquetes-comprados".equals(action) && clienteId != null) {
                // Listar paquetes comprados por un cliente específico
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
            System.err.println("💥 ERROR EN SERVLET COMPRA PAQUETE: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            System.out.println("Parámetro: " + paramName + " = " + paramValue);
        }
        System.out.println("============================");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // NO llamar cargarDesdeBd() aquí - ya se cargó en init()

            if ("realizar-compra".equals(action)) {
                // Realizar compra de paquete
                realizarCompraPaquete(sistema, request, out, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Acción no válida. Action recibido: " + (action != null ? action : "null") + "\"}");
            }

        } catch (Exception e) {
            System.err.println("💥 ERROR EN SERVLET COMPRA PAQUETE POST: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    // NUEVO MÉTODO: Filtrar paquetes válidos (con costo > 0 y con rutas)
    private void listarPaquetesValidos(ISistema sistema, PrintWriter out) {
        try {
            // Obtener TODOS los paquetes
            List<DtPaquete> todosLosPaquetes = sistema.listarPaquetes();
            System.out.println("📦 Número total de paquetes encontrados: " + (todosLosPaquetes != null ? todosLosPaquetes.size() : "NULL"));

            // Filtrar paquetes válidos
            List<DtPaquete> paquetesValidos = new ArrayList<>();
            if (todosLosPaquetes != null) {
                for (DtPaquete paquete : todosLosPaquetes) {
                    if (esPaqueteValido(paquete)) {
                        paquetesValidos.add(paquete);
                        System.out.println("✅ Paquete VÁLIDO: " + paquete.getNombre() +
                                " - Costo: $" + paquete.getCosto() +
                                " - Rutas: " + (paquete.getItems() != null ? paquete.getItems().size() : 0));
                    } else {
                        System.out.println("❌ Paquete INVÁLIDO (filtrado): " + paquete.getNombre() +
                                " - Costo: $" + paquete.getCosto() +
                                " - Rutas: " + (paquete.getItems() != null ? paquete.getItems().size() : 0));
                    }
                }
            }

            System.out.println("📊 Paquetes válidos después del filtro: " + paquetesValidos.size());
            escribirPaquetesDisponiblesJSON(paquetesValidos, out);

        } catch (Exception e) {
            System.err.println("💥 ERROR listando paquetes válidos: " + e.getMessage());
            e.printStackTrace();
            out.print("[]");
        }
    }

    // MÉTODO PARA VALIDAR PAQUETES
    private boolean esPaqueteValido(DtPaquete paquete) {
        if (paquete == null) {
            return false;
        }

        // 1. Validar que tenga costo mayor a 0
        if (paquete.getCosto() <= 0) {
            System.out.println("   ❌ Filtrado por costo: $" + paquete.getCosto());
            return false;
        }

        // 2. Validar que tenga rutas
        if (paquete.getItems() == null || paquete.getItems().isEmpty()) {
            System.out.println("   ❌ Filtrado por falta de rutas");
            return false;
        }

        // 3. Validar que tenga al menos una ruta válida
        boolean tieneRutasValidas = false;
        for (DtItemPaquete item : paquete.getItems()) {
            if (item != null && item.getRutaVuelo() != null) {
                tieneRutasValidas = true;
                break;
            }
        }

        if (!tieneRutasValidas) {
            System.out.println("   ❌ Filtrado por rutas inválidas");
            return false;
        }

        // 4. Validar que tenga nombre
        if (paquete.getNombre() == null || paquete.getNombre().trim().isEmpty()) {
            System.out.println("   ❌ Filtrado por nombre vacío");
            return false;
        }

        System.out.println("   ✅ Paquete cumple todos los criterios de validación");
        return true;
    }

    private void listarPaquetesComprados(ISistema sistema, String clienteId, PrintWriter out) {
        try {
            System.out.println("🔍 Buscando cliente: " + clienteId);
            // Obtener información del cliente y sus paquetes comprados
            DtCliente cliente = sistema.obtenerCliente(clienteId);
            System.out.println("📋 Cliente obtenido: " + (cliente != null ? cliente.getNickname() : "NULL"));

            if (cliente != null) {
                List<DtPaquete> paquetesComprados = cliente.getPaquetesComprados();
                System.out.println("📦 Número de paquetes comprados: " + (paquetesComprados != null ? paquetesComprados.size() : "NULL"));

                // Filtrar sólo paquetes que parecen realmente comprados (tienen fechaAlta no nula)
                java.util.List<DtPaquete> filtrados = new java.util.ArrayList<>();
                if (paquetesComprados != null) {
                    for (DtPaquete p : paquetesComprados) {
                        if (p != null && p.getFechaAlta() != null) {
                            filtrados.add(p);
                        }
                    }
                }
                escribirPaquetesCompradosJSON(filtrados, out);
            } else {
                System.out.println("❌ Cliente no encontrado: " + clienteId);
                out.print("[]");
            }
        } catch (Exception e) {
            System.err.println("💥 ERROR listando paquetes comprados: " + e.getMessage());
            e.printStackTrace();
            out.print("[]");
        }
    }

    private void obtenerInfoPaquete(ISistema sistema, String paqueteId, PrintWriter out, HttpServletResponse response) {
        try {
            System.out.println("🔍 Buscando paquete: " + paqueteId);
            DtPaquete paquete = sistema.obtenerDtPaquete(paqueteId);
            System.out.println("📋 Paquete obtenido: " + (paquete != null ? paquete.getNombre() : "NULL"));

            if (paquete != null) {
                escribirInfoPaqueteJSON(paquete, out);
            } else {
                System.out.println("❌ Paquete no encontrado: " + paqueteId);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Paquete no encontrado\"}");
            }
        } catch (Exception e) {
            System.err.println("💥 ERROR obteniendo paquete: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error obteniendo paquete: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void realizarCompraPaquete(ISistema sistema, HttpServletRequest request, PrintWriter out, HttpServletResponse response) {
        try {
            String paqueteId = request.getParameter("paquete");
            String clienteId = request.getParameter("cliente");
            String validezDiasStr = request.getParameter("validezDias");
            String fechaCompraStr = request.getParameter("fechaCompra");
            String costoStr = request.getParameter("costo");

            System.out.println("💰 Procesando compra:");
            System.out.println("  - Paquete: " + paqueteId);
            System.out.println("  - Cliente: " + clienteId);
            System.out.println("  - Validez días: " + validezDiasStr);
            System.out.println("  - Fecha compra: " + fechaCompraStr);
            System.out.println("  - Costo: " + costoStr);

            // Validar parámetros
            if (paqueteId == null || clienteId == null || validezDiasStr == null || fechaCompraStr == null || costoStr == null) {
                System.out.println("❌ Parámetros incompletos");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Parámetros incompletos\"}");
                return;
            }

            int validezDias = Integer.parseInt(validezDiasStr);
            LocalDate fechaCompra = LocalDate.parse(fechaCompraStr);
            double costo = Double.parseDouble(costoStr);

            // Verificar que el paquete existe
            DtPaquete paquete = sistema.obtenerDtPaquete(paqueteId);
            if (paquete == null) {
                System.out.println("❌ Paquete no existe: " + paqueteId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"El paquete no existe\"}");
                return;
            }

            // Verificar que el cliente existe
            DtCliente cliente = sistema.obtenerCliente(clienteId);
            if (cliente == null) {
                System.out.println("❌ Cliente no existe: " + clienteId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"El cliente no existe\"}");
                return;
            }

            // Realizar la compra
            System.out.println("🛒 Ejecutando compraPaquete...");
            sistema.compraPaquete(paqueteId, clienteId, validezDias, fechaCompra, costo);

            System.out.println("✅ Compra realizada exitosamente");
            out.print("{\"success\":true, \"message\":\"Compra realizada exitosamente\"}");

        } catch (NumberFormatException e) {
            System.err.println("❌ Formato de número inválido: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Formato de número inválido\"}");
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error de validación: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        } catch (Exception e) {
            System.err.println("💥 Error al realizar la compra: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error al realizar la compra: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void escribirPaquetesDisponiblesJSON(List<DtPaquete> paquetes, PrintWriter out) {
        out.print("[");
        if (paquetes != null) {
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
                if (items != null) {
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
                }
                out.print("]");
                out.print("}");

                if (i < paquetes.size() - 1) out.print(",");
            }
        }
        out.print("]");
    }

    private void escribirPaquetesCompradosJSON(List<DtPaquete> paquetes, PrintWriter out) {
        out.print("[");
        if (paquetes != null) {
            for (int i = 0; i < paquetes.size(); i++) {
                DtPaquete p = paquetes.get(i);
                out.print("{");
                out.print("\"id\":\"" + escapeJson(p.getNombre()) + "\",");
                out.print("\"nombre\":\"" + escapeJson(p.getNombre()) + "\",");
                out.print("\"descripcion\":\"" + escapeJson(p.getDescripcion()) + "\",");
                out.print("\"costo\":" + p.getCosto() + ","); // ← ESTA LÍNEA
                out.print("\"costoFinal\":" + calcularCostoFinal(p) + ",");
                String fechaCompraStr = p.getFechaAlta() != null ? p.getFechaAlta().toString() : "";
                out.print("\"fechaCompra\":\"" + fechaCompraStr + "\",");
                String fechaVencimientoStr = "";
                if (p.getFechaAlta() != null && p.getPeriodoValidezDias() > 0) {
                    fechaVencimientoStr = p.getFechaAlta().plusDays(p.getPeriodoValidezDias()).toString();
                }
                out.print("\"fechaVencimiento\":\"" + fechaVencimientoStr + "\",");
                out.print("\"cantidadRutas\":" + (p.getItems() != null ? p.getItems().size() : 0));
                out.print("}");

                if (i < paquetes.size() - 1) out.print(",");
            }
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
package com.example.servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Paths;

import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;
import serviciosweb.DtCliente;
import serviciosweb.DtAerolinea;
import serviciosweb.DtRutaVuelo;
import serviciosweb.DtVuelo;
import serviciosweb.DtReserva;
import serviciosweb.DtPaquete;
import serviciosweb.DtItemPaquete;

@WebServlet("/consulta-usuario")
public class ConsultaUsuarioServlet extends HttpServlet {

    // Ya no se usa ISistema/local logic; todo se obtiene vía WS

    private JuanViajesWS getPort(HttpServletRequest request) {
        try {
            Object o = request.getServletContext().getAttribute("port");
            if (o instanceof JuanViajesWS) return (JuanViajesWS) o;
        } catch (Exception ignored) {}
        WebServicesService svc = new WebServicesService();
        return svc.getJuanViajesWSPort();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String usuarioId = request.getParameter("usuario");
        String tipoUsuario = request.getParameter("tipo");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            System.out.println("=== CONSULTA USUARIO ===");
            System.out.println("Action: " + action);
            System.out.println("Usuario: " + usuarioId);
            System.out.println("Tipo: " + tipoUsuario);

            if ("listar-usuarios".equals(action)) {
                System.out.println("Listando todos los usuarios");
                listarUsuarios(out, request);
            } else if ("obtener-usuario".equals(action) && usuarioId != null && tipoUsuario != null) {
                System.out.println("Obteniendo información del usuario: " + usuarioId + " tipo: " + tipoUsuario);
                obtenerUsuarioDetalle(usuarioId, tipoUsuario, out, response, request);
            } else if ("obtener-rutas-aerolinea".equals(action) && usuarioId != null) {
                System.out.println("Obteniendo rutas de la aerolínea: " + usuarioId);
                obtenerRutasAerolinea(usuarioId, out, request);
            } else if ("obtener-reservas-cliente".equals(action) && usuarioId != null) {
                System.out.println("Obteniendo reservas del cliente: " + usuarioId);
                obtenerReservasCliente(usuarioId, out, request);
            } else if ("obtener-paquetes-cliente".equals(action) && usuarioId != null) {
                System.out.println("Obteniendo paquetes del cliente: " + usuarioId);
                obtenerPaquetesCliente(usuarioId, out, request);
            } else if ("obtener-vuelos-aerolinea".equals(action) && usuarioId != null) {
                System.out.println("Obteniendo vuelos de la aerolínea: " + usuarioId);
                obtenerVuelosAerolinea(usuarioId, out, request);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Parámetros inválidos. Acciones válidas: listar-usuarios, obtener-usuario, obtener-rutas-aerolinea, obtener-reservas-cliente, obtener-paquetes-cliente, obtener-vuelos-aerolinea\"}");
            }

        } catch (Exception e) {
            System.err.println("💥 ERROR EN SERVLET: " + e.getMessage());
            logError("Detalle del error en doGet", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    // Helper para obtener el valor de imagen desde un DTO probando varios getters por reflection
    private String extractImageValue(Object dto) {
        if (dto == null) return "";
        String[] candidates = new String[]{"getImagenUrl", "getImagen", "getImage", "getFoto", "getFotoUrl", "getUrl", "imagenUrl", "imagen", "image", "foto", "url"};
        for (String name : candidates) {
            try {
                java.lang.reflect.Method m = dto.getClass().getMethod(name);
                Object v = m.invoke(dto);
                if (v != null) {
                    String sval = v.toString();
                    return sval;
                }
            } catch (NoSuchMethodException ns) {
                // seguir probando
            } catch (Throwable t) {
                // ignora y continúa con el siguiente
            }
        }
        // Si no hay getter, intentar acceder a campos públicos por reflexión
        try {
            java.lang.reflect.Field[] fields = dto.getClass().getDeclaredFields();
            for (java.lang.reflect.Field f : fields) {
                String fname = f.getName().toLowerCase();
                if (fname.contains("imagen") || fname.contains("foto") || fname.contains("image") || fname.contains("url")) {
                    try {
                        f.setAccessible(true);
                        Object val = f.get(dto);
                        if (val != null) {
                            return val.toString();
                        }
                    } catch (Throwable tt) {}
                }
            }
        } catch (Throwable t) {}
        return "";
    }

    // Helper para construir la URL pública de la imagen usando el contexto
    private String buildImageUrl(HttpServletRequest request, String stored) {
        if (stored == null) return "";
        stored = stored.trim();
        if (stored.isEmpty()) return "";
        if (stored.startsWith("data:")) return stored;
        if (stored.startsWith("http://") || stored.startsWith("https://")) return stored;
        if (stored.startsWith("/")) return stored;
        try {
            String filename = Paths.get(stored).getFileName().toString();
            if (filename.isEmpty()) filename = stored;
            return request.getContextPath() + "/Images/" + filename;
        } catch (Exception e) {
            return request.getContextPath() + "/Images/" + stored;
        }
    }

    private void obtenerVuelosAerolinea(String aerolineaId, PrintWriter out, HttpServletRequest request) {
        try {
            JuanViajesWS port = getPort(request);
            List<DtRutaVuelo> rutas = null;
            try { rutas = port.listarRutasPorAerolinea(aerolineaId); } catch (Exception ex) { rutas = null; }

            List<DtVuelo> todosLosVuelos = new ArrayList<>();
            if (rutas != null) {
                for (DtRutaVuelo ruta : rutas) {
                    try {
                        List<DtVuelo> vuelosRuta = port.listarVuelosPorRuta(ruta.getNombre());
                        if (vuelosRuta != null) todosLosVuelos.addAll(vuelosRuta);
                    } catch (Exception e) {}
                }
            }
            escribirVuelosAerolineaJSONConvert(todosLosVuelos, out, request);
        } catch (Exception e) {
            out.print("[]");
        }
    }

    private void escribirVuelosAerolineaJSONConvert(List<DtVuelo> vuelos, PrintWriter out, HttpServletRequest request) {
        out.print("[");
        for (int i = 0; i < vuelos.size(); i++) {
            DtVuelo vuelo = vuelos.get(i);
            out.print("{");
            out.print("\"id\":\"" + escapeJson(vuelo.getNombre()) + "\",");
            out.print("\"nombre\":\"" + escapeJson(vuelo.getNombre()) + "\",");
            out.print("\"aerolinea\":\"" + escapeJson(vuelo.getNombreAerolinea()) + "\",");
            out.print("\"ruta\":\"" + (vuelo.getRutaVuelo() != null ? escapeJson(vuelo.getRutaVuelo().getNombre()) : "") + "\",");
            out.print("\"fecha\":\"" + (vuelo.getFecha() != null ? vuelo.getFecha().toString() : "") + "\",");
            out.print("\"duracion\":" + vuelo.getDuracion() + ",");
            out.print("\"origen\":\"" + (vuelo.getRutaVuelo() != null ? escapeJson(vuelo.getRutaVuelo().getCiudadOrigen()) : "") + "\",");
            out.print("\"destino\":\"" + (vuelo.getRutaVuelo() != null ? escapeJson(vuelo.getRutaVuelo().getCiudadDestino()) : "") + "\"");
            out.print("}");
            if (i < vuelos.size() - 1) out.print(",");
        }
        out.print("]");
    }

    private void listarUsuarios(PrintWriter out, HttpServletRequest request) {
        try {
            out.print("{");
            JuanViajesWS port = getPort(request);
            List<DtCliente> clientes = null;
            List<DtAerolinea> aerolineas = null;
            try {
                clientes = port.listarClientes();
                aerolineas = port.listarAerolineas();
            } catch (Exception e) {
                clientes = new ArrayList<>();
                aerolineas = new ArrayList<>();
            }

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
                String rawImgC = extractImageValue(c);
                String imgC = buildImageUrl(request, rawImgC);
                out.print("\"imagen\":\"" + escapeJson(imgC) + "\"");
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
                String rawImgA = "";
                try { rawImgA = a.getImagenUrl() != null ? a.getImagenUrl() : ""; } catch (Throwable ignore) { rawImgA = ""; }
                if (rawImgA == null || rawImgA.trim().isEmpty()) {
                    try { rawImgA = extractImageValue(a); } catch (Throwable ignore) {}
                }
                String imgA = buildImageUrl(request, rawImgA);
                out.print("\"imagen\":\"" + escapeJson(imgA) + "\"");
                out.print("}");
                if (i < aerolineas.size() - 1) out.print(",");
            }
            out.print("]");

            out.print("}");

        } catch (Exception e) {
            out.print("{\"clientes\":[],\"aerolineas\":[]}");
        }
    }

    private void obtenerUsuarioDetalle(String usuarioId, String tipoUsuario, PrintWriter out, HttpServletResponse response, HttpServletRequest request) {
        try {
            JuanViajesWS port = getPort(request);
            if ("cliente".equals(tipoUsuario)) {
                DtCliente clienteWs = null;
                try { clienteWs = port.obtenerCliente(usuarioId); } catch (Exception ex) { clienteWs = null; }
                if (clienteWs != null) {
                    escribirClienteDetalleJSON(clienteWs, out, request);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Cliente no encontrado\"}");
                }
            } else if ("aerolinea".equals(tipoUsuario)) {
                DtAerolinea aerolineaWs = null;
                try { aerolineaWs = port.obtenerAerolinea(usuarioId); } catch (Exception ex) { aerolineaWs = null; }
                if (aerolineaWs != null) {
                    escribirInfoAerolineaJSON(aerolineaWs, out, request);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Aerolínea no encontrada\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Tipo de usuario no válido\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error obteniendo usuario: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void obtenerRutasAerolinea(String aerolineaId, PrintWriter out, HttpServletRequest request) {
        try {
            JuanViajesWS port = getPort(request);
            List<DtRutaVuelo> rutas = null;
            try { rutas = port.listarRutasPorAerolinea(aerolineaId); } catch (Exception ex) { rutas = null; }
            if (rutas != null) {
                escribirRutasAerolineaJSON(rutas, out);
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            out.print("[]");
        }
    }

    private void obtenerReservasCliente(String clienteId, PrintWriter out, HttpServletRequest request) {
        try {
            JuanViajesWS port = getPort(request);
            List<DtReserva> reservas = null;
            try { reservas = port.getReservasCliente(clienteId); } catch (Exception ex) { reservas = new ArrayList<>(); }
            if (reservas != null) escribirReservasClienteJSON(reservas, out); else out.print("[]");
        } catch (Exception e) {
            out.print("[]");
        }
    }

    private void obtenerPaquetesCliente(String clienteId, PrintWriter out, HttpServletRequest request) {
        try {
            JuanViajesWS port = getPort(request);
            DtCliente cliente = null;
            try { cliente = port.obtenerCliente(clienteId); } catch (Exception ex) { cliente = null; }
            if (cliente != null) {
                List<DtPaquete> paquetes = cliente.getPaquetesComprados();
                if (paquetes != null) escribirPaquetesClienteJSON(paquetes, out); else out.print("[]");
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            out.print("[]");
        }
    }

    private void escribirClienteDetalleJSON(DtCliente cliente, PrintWriter out, HttpServletRequest request) {
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
        String img = buildImageUrl(request, cliente.getImagenUrl());
        out.print(",\"imagenUrl\":\"" + escapeJson(img) + "\"");
        out.print("}");
    }

    private void escribirInfoAerolineaJSON(DtAerolinea aerolinea, PrintWriter out, HttpServletRequest request) {
        out.print("{");
        out.print("\"tipo\":\"Aerolinea\",");
        out.print("\"id\":\"" + escapeJson(aerolinea.getNickname()) + "\",");
        out.print("\"nombre\":\"" + escapeJson(aerolinea.getNombre()) + "\",");
        out.print("\"nombreCompleto\":\"" + escapeJson(aerolinea.getNombre()) + "\",");
        out.print("\"correo\":\"" + escapeJson(aerolinea.getEmail()) + "\",");
        out.print("\"fechaRegistro\":\"\",");
        String img = buildImageUrl(request, aerolinea.getImagenUrl());
        out.print("\"imagen\":\"" + escapeJson(img) + "\",");
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
            out.print("\"estado\":\"" + escapeJson(String.valueOf(r.getEstado())) + "\",");
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
            out.print("\"fecha\":\"" + escapeJson(reserva.getFecha() != null ? reserva.getFecha().toString() : "") + "\",");
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
        try {
            java.time.LocalDate ld = toLocalDate(paquete.getFechaAlta());
            if (ld == null) return "";
            return ld.plusDays(paquete.getPeriodoValidezDias()).toString();
        } catch (Exception e) {
            // Si no podemos parsear, devolver cadena vacía en lugar de lanzar
            return "";
        }
    }

    private String calcularEstadoPaquete(DtPaquete paquete) {
        if (paquete.getFechaAlta() == null || paquete.getPeriodoValidezDias() == 0) {
            return "Vigente";
        }

        try {
            java.time.LocalDate ld = toLocalDate(paquete.getFechaAlta());
            if (ld == null) return "Vigente";
            java.time.LocalDate vencimiento = ld.plusDays(paquete.getPeriodoValidezDias());
            java.time.LocalDate hoy = java.time.LocalDate.now();
            return vencimiento.isAfter(hoy) ? "Vigente" : "Vencido";
        } catch (Exception e) {
            // Si no podemos calcular, asumir vigente para no bloquear funcionalidades
            return "Vigente";
        }
    }

    // convert DTO LocalDate to java.time.LocalDate
    private java.time.LocalDate toLocalDate(serviciosweb.LocalDate ld) {
        if (ld == null) return null;
        try {
            String s = ld.toString();
            if (s == null || s.isBlank()) return null;
            return java.time.LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Utility para registrar excepciones with stacktrace en un solo lugar
    private void logError(String message, Throwable t) {
        if (message != null) System.err.println(message + (t != null ? ": " + t.getMessage() : ""));
        if (t != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.flush();
            System.err.println(sw.toString());
        }
    }
}


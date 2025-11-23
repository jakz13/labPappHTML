package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import DataTypes.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Paths;

@WebServlet("/consulta-usuario")
public class ConsultaUsuarioServerlet extends HttpServlet {

    private ISistema sistema;

    @Override
    public void init() {
        sistema = Fabrica.getInstance().getISistema();
        try {
            sistema.cargarDesdeBd();
            System.out.println("✅ Sistema cargado correctamente en init()");
        } catch (Exception e) {
            logError("❌ Error cargando sistema en init()", e);
        }
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

            // NO llamar cargarDesdeBd() aquí - ya se cargó en init()

            if ("listar-usuarios".equals(action)) {
                System.out.println("Listando todos los usuarios");
                listarUsuarios(sistema, out, request);
            } else if ("obtener-usuario".equals(action) && usuarioId != null && tipoUsuario != null) {
                System.out.println("Obteniendo información del usuario: " + usuarioId + " tipo: " + tipoUsuario);
                obtenerUsuarioDetalle(sistema, usuarioId, tipoUsuario, out, response, request);
            } else if ("obtener-rutas-aerolinea".equals(action) && usuarioId != null) {
                System.out.println("Obteniendo rutas de la aerolínea: " + usuarioId);
                obtenerRutasAerolinea(sistema, usuarioId, out);
            } else if ("obtener-reservas-cliente".equals(action) && usuarioId != null) {
                System.out.println("Obteniendo reservas del cliente: " + usuarioId);
                obtenerReservasCliente(sistema, usuarioId, out);
            } else if ("obtener-paquetes-cliente".equals(action) && usuarioId != null) {
                System.out.println("Obteniendo paquetes del cliente: " + usuarioId);
                obtenerPaquetesCliente(sistema, usuarioId, out);
            } else if ("obtener-vuelos-aerolinea".equals(action) && usuarioId != null) {
                System.out.println("Obteniendo vuelos de la aerolínea: " + usuarioId);
                obtenerVuelosAerolinea(sistema, usuarioId, out);
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
                    System.out.println("extractImageValue: DTO=" + dto.getClass().getSimpleName() + " -> getter='" + name + "' returned (len=" + sval.length() + "): '" + truncate(sval,200) + "'");
                    return sval;
                }
            } catch (NoSuchMethodException ns) {
                // seguir probando
            } catch (Throwable t) {
                // ignora y continúa con el siguiente
                System.err.println("extractImageValue: error al invocar " + name + " en " + dto.getClass().getName() + ": " + t.getMessage());
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
                            String sval = val.toString();
                            System.out.println("extractImageValue: DTO=" + dto.getClass().getSimpleName() + " -> field='" + f.getName() + "' value (len=" + sval.length() + "): '" + truncate(sval,200) + "'");
                            return sval;
                        }
                    } catch (Throwable tt) {
                        System.err.println("extractImageValue: error leyendo campo " + f.getName() + ": " + tt.getMessage());
                    }
                }
            }
            // Si llegamos aquí, no se encontró valor relevante; listar campos para debug
            StringBuilder sb = new StringBuilder();
            for (java.lang.reflect.Field f : fields) {
                try {
                    f.setAccessible(true);
                    Object val = f.get(dto);
                    sb.append(f.getName()).append("=").append(val != null ? truncate(val.toString(),80) : "<null>").append("; ");
                } catch (Throwable ignore) {}
            }
            System.out.println("extractImageValue: DTO=" + dto.getClass().getSimpleName() + " -> no image getter/field found. Fields: " + sb.toString());
        } catch (Throwable t) {
            System.err.println("extractImageValue: error inspeccionando campos: " + t.getMessage());
        }
        return "";
    }

    // Helper para truncar logs largos
    private String truncate(String s, int max) {
        if (s == null) return null;
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...(" + s.length() + " chars)";
    }

    // Helper para construir la URL pública de la imagen usando el contexto
    private String buildImageUrl(HttpServletRequest request, String stored) {
        if (stored == null) return "";
        stored = stored.trim();
        if (stored.isEmpty()) return "";
        // Si ya es data URI, devolver tal cual
        if (stored.startsWith("data:")) return stored;
        // Si ya es URL absoluta
        if (stored.startsWith("http://") || stored.startsWith("https://")) return stored;
        // Si ya es ruta absoluta en el servidor (empieza con '/'), devolver tal cual
        if (stored.startsWith("/")) return stored;
        // Si contiene separadores de archivos (ruta física), extraer filename
        try {
            String filename = Paths.get(stored).getFileName().toString();
            if (filename.isEmpty()) filename = stored;
            return request.getContextPath() + "/Images/" + filename;
        } catch (Exception e) {
            // fallback: tratar como filename
            System.err.println("buildImageUrl: error procesando ruta de imagen '" + stored + "': " + e.getMessage());
            return request.getContextPath() + "/Images/" + stored;
        }
    }

    private void obtenerVuelosAerolinea(ISistema sistema, String aerolineaId, PrintWriter out) {
        try {
            List<DtRutaVuelo> rutas = sistema.listarRutasPorAerolinea(aerolineaId);
            List<DtVuelo> todosLosVuelos = new ArrayList<>();

            for (DtRutaVuelo ruta : rutas) {
                try {
                    List<DtVuelo> vuelosRuta = sistema.listarVuelosPorRuta(ruta.getNombre());
                    if (vuelosRuta != null) {
                        todosLosVuelos.addAll(vuelosRuta);
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Error obteniendo vuelos para ruta " + ruta.getNombre() + ": " + e.getMessage());
                    // Continuar con la siguiente ruta
                }
            }

            escribirVuelosAerolineaJSON(todosLosVuelos, out);
        } catch (Exception e) {
            System.err.println("💥 ERROR obteniendo vuelos: " + e.getMessage());
            logError("Detalle del error obteniendo vuelos", e);
            // En lugar de error, devolver array vacío
            out.print("[]");
        }
    }

    private void escribirVuelosAerolineaJSON(List<DtVuelo> vuelos, PrintWriter out) {
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

    private void listarUsuarios(ISistema sistema, PrintWriter out, HttpServletRequest request) {
        try {
            out.print("{");

            // Clientes
            out.print("\"clientes\":[");
            List<DtCliente> clientes = sistema.listarClientes();
            if (clientes != null) {
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
                    // debug log cliente
                    try { System.out.println("ConsultaUsuarioServerlet: cliente='" + c.getNickname() + "' rawImg='" + rawImgC + "' -> public='" + imgC + "'"); } catch (Throwable t) { System.err.println("Error log cliente: " + t.getMessage()); }
                    out.print("\"imagen\":\"" + escapeJson(imgC) + "\"");
                    out.print("}");
                    if (i < clientes.size() - 1) out.print(",");
                }
            }
            out.print("],");

            // Aerolíneas
            out.print("\"aerolineas\":[");
            List<DtAerolinea> aerolineas = sistema.listarAerolineas();
            if (aerolineas != null) {
                for (int i = 0; i < aerolineas.size(); i++) {
                    DtAerolinea a = aerolineas.get(i);
                    out.print("{");
                    out.print("\"id\":\"" + escapeJson(a.getNickname()) + "\",");
                    out.print("\"nombre\":\"" + escapeJson(a.getNombre()) + "\",");
                    out.print("\"tipo\":\"Aerolinea\",");
                    out.print("\"correo\":\"" + escapeJson(a.getEmail()) + "\",");
                    // usar fechaAlta si existe
                    String fechaAltaA = "";

                    out.print("\"fechaRegistro\":\"" + escapeJson(fechaAltaA) + "\",");
                    // Obtener la imagen directamente desde el DTO (misma convención que en otros servlets)
                    String rawImgA = "";
                    try { rawImgA = a.getImagenUrl() != null ? a.getImagenUrl() : ""; } catch (Throwable ignore) { rawImgA = ""; }
                    // Fallback: si está vacío, intentar obtener por reflexión (por compatibilidad)
                    if (rawImgA == null || rawImgA.trim().isEmpty()) {
                        try {
                            rawImgA = extractImageValue(a);
                        } catch (Throwable ignore) { /* ignore */ }
                    }
                    String imgA = buildImageUrl(request, rawImgA);
                    try { System.out.println("ConsultaUsuarioServerlet: aerolinea='" + a.getNickname() + "' rawImg='" + rawImgA + "' -> public='" + imgA + "'"); } catch (Throwable t) { System.err.println("Error log aerolinea: " + t.getMessage()); }
                    out.print("\"imagen\":\"" + escapeJson(imgA) + "\"");
                    out.print("}");
                    if (i < aerolineas.size() - 1) out.print(",");
                }
            }
            out.print("]");

            out.print("}");

        } catch (Exception e) {
            System.err.println("💥 ERROR listando usuarios: " + e.getMessage());
            logError("Detalle del error listando usuarios", e);
            out.print("{\"clientes\":[],\"aerolineas\":[]}");
        }
    }

    private void obtenerUsuarioDetalle(ISistema sistema, String usuarioId, String tipoUsuario, PrintWriter out, HttpServletResponse response, HttpServletRequest request) {
        try {
            if ("cliente".equals(tipoUsuario)) {
                DtCliente cliente = sistema.obtenerCliente(usuarioId);
                if (cliente != null) {
                    escribirClienteDetalleJSON(cliente, out, request);
                } else {
                    System.out.println("❌ Cliente no encontrado: " + usuarioId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Cliente no encontrado\"}");
                }
            } else if ("aerolinea".equals(tipoUsuario)) {
                DtAerolinea aerolinea = sistema.obtenerAerolinea(usuarioId);
                if (aerolinea != null) {
                    escribirInfoAerolineaJSON(aerolinea, out, request);
                } else {
                    System.out.println("❌ Aerolínea no encontrada: " + usuarioId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Aerolínea no encontrada\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Tipo de usuario no válido\"}");
            }
        } catch (Exception e) {
            System.err.println("💥 ERROR obteniendo usuario: " + e.getMessage());
            logError("Detalle del error obteniendo usuario", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error obteniendo usuario: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void obtenerRutasAerolinea(ISistema sistema, String aerolineaId, PrintWriter out) {
        try {
            List<DtRutaVuelo> rutas = sistema.listarRutasPorAerolinea(aerolineaId);
            if (rutas != null) {
                escribirRutasAerolineaJSON(rutas, out);
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            System.err.println("💥 ERROR obteniendo rutas: " + e.getMessage());
            logError("Detalle del error obteniendo rutas", e);
            out.print("[]");
        }
    }

    private void obtenerReservasCliente(ISistema sistema, String clienteId, PrintWriter out) {
        try {
            List<DtReserva> reservas = sistema.getReservasCliente(clienteId);
            if (reservas != null) {
                escribirReservasClienteJSON(reservas, out);
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            System.err.println("💥 ERROR obteniendo reservas: " + e.getMessage());
            logError("Detalle del error obteniendo reservas", e);
            out.print("[]");
        }
    }

    private void obtenerPaquetesCliente(ISistema sistema, String clienteId, PrintWriter out) {
        try {
            DtCliente cliente = sistema.obtenerCliente(clienteId);
            if (cliente != null) {
                List<DtPaquete> paquetes = cliente.getPaquetesComprados();
                if (paquetes != null) {
                    escribirPaquetesClienteJSON(paquetes, out);
                } else {
                    out.print("[]");
                }
            } else {
                System.out.println("❌ Cliente no encontrado: " + clienteId);
                out.print("[]");
            }
        } catch (Exception e) {
            System.err.println("💥 ERROR obteniendo paquetes: " + e.getMessage());
            logError("Detalle del error obteniendo paquetes", e);
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
        // añadir imagen si existe
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

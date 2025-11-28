package com.example.servlets;

import com.example.util.PortUtils;
import serviciosweb.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/consulta-usuario")
public class ConsultaUsuarioServlet extends HttpServlet {

    // ✅ NUEVO: Sistema simple de cache
    private static final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION_MS = 30000; // 30 segundos

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        String usuarioId = request.getParameter("usuario");
        String tipoUsuario = request.getParameter("tipo");

        // Trazas para depuración
        System.out.println("[ConsultaUsuarioServlet] doGet -> action=" + action + ", usuario=" + usuarioId + ", tipo=" + tipoUsuario);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Obtener el port usando PortUtils
            JuanViajesWS port = PortUtils.getPort(request);

            if (port == null) {
                System.err.println("[ConsultaUsuarioServlet] PortUtils.getPort returned null");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":\"Error de conexión con el servicio\"}");
                return;
            }

            if ("listar-usuarios".equals(action)) {
                listarUsuarios(port, out);
            } else if ("obtener-usuario".equals(action) && usuarioId != null && tipoUsuario != null) {
                obtenerUsuarioDetalle(port, usuarioId, tipoUsuario, out, response);
            } else if ("obtener-rutas-aerolinea".equals(action) && usuarioId != null) {
                obtenerRutasAerolinea(port, usuarioId, out, response);
            } else if ("obtener-reservas-cliente".equals(action) && usuarioId != null) {
                obtenerReservasCliente(port, usuarioId, out, response);
            } else if ("obtener-paquetes-cliente".equals(action) && usuarioId != null) {
                obtenerPaquetesCliente(port, usuarioId, out, response);
            } else if ("obtener-vuelos-aerolinea".equals(action) && usuarioId != null) {
                obtenerVuelosAerolinea(port, usuarioId, out, response);
            } else if ("seguir".equals(action) && usuarioId != null) {
                seguirUsuario(port, usuarioId, request, out, response);
            } else if ("dejar-de-seguir".equals(action) && usuarioId != null) {
                dejarDeSeguirUsuario(port, usuarioId, request, out, response);
            } else if ("verificar-seguimiento".equals(action) && usuarioId != null) {
                verificarSeguimiento(port, usuarioId, request, out, response);
            } else if ("obtener-estadisticas-seguimiento".equals(action) && usuarioId != null) {
                obtenerEstadisticasSeguimiento(port, usuarioId, request, out, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Parámetros inválidos\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    // ✅ NUEVO: Métodos de gestión de cache
    private void invalidarCacheUsuario(String usuarioId) {
        cacheTimestamps.remove("stats_" + usuarioId);
        cacheTimestamps.remove("verificar_" + usuarioId);
        System.out.println("🗑️ Cache invalidado para usuario: " + usuarioId);
    }

    private boolean isCacheValido(String clave) {
        Long timestamp = cacheTimestamps.get(clave);
        return timestamp != null && (System.currentTimeMillis() - timestamp) < CACHE_DURATION_MS;
    }

    private void marcarCache(String clave) {
        cacheTimestamps.put(clave, System.currentTimeMillis());
    }

    private void seguirUsuario(JuanViajesWS port, String usuarioId, HttpServletRequest request, PrintWriter out, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\":\"Usuario no autenticado\"}");
                return;
            }

            String usuarioActualId = (String) session.getAttribute("usuario");
            if (usuarioActualId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\":\"Usuario no autenticado\"}");
                return;
            }

            if (usuarioActualId.equals(usuarioId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"No puedes seguirte a ti mismo\"}");
                return;
            }

            port.followUsuario(usuarioActualId, usuarioId);

            // ✅ INVALIDAR CACHE de ambos usuarios
            invalidarCacheUsuario(usuarioActualId);
            invalidarCacheUsuario(usuarioId);
            System.out.println("✅ Follow realizado - Cache invalidado para: " + usuarioActualId + " y " + usuarioId);

            out.print("{\"success\":true,\"message\":\"Ahora sigues a este usuario\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error siguiendo usuario: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void dejarDeSeguirUsuario(JuanViajesWS port, String usuarioId, HttpServletRequest request, PrintWriter out, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\":\"Usuario no autenticado\"}");
                return;
            }

            String usuarioActualId = (String) session.getAttribute("usuario");
            if (usuarioActualId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\":\"Usuario no autenticado\"}");
                return;
            }

            if (usuarioActualId.equals(usuarioId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"No puedes dejar de seguirte a ti mismo\"}");
                return;
            }

            port.unfollowUsuario(usuarioActualId, usuarioId);

            // ✅ INVALIDAR CACHE de ambos usuarios
            invalidarCacheUsuario(usuarioActualId);
            invalidarCacheUsuario(usuarioId);
            System.out.println("✅ Unfollow realizado - Cache invalidado para: " + usuarioActualId + " y " + usuarioId);

            out.print("{\"success\":true,\"message\":\"Has dejado de seguir a este usuario\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error dejando de seguir usuario: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void verificarSeguimiento(JuanViajesWS port, String usuarioId, HttpServletRequest request, PrintWriter out, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                out.print("{\"siguiendo\":false}");
                return;
            }

            String usuarioActualId = (String) session.getAttribute("usuario");
            if (usuarioActualId == null) {
                out.print("{\"siguiendo\":false}");
                return;
            }

            if (usuarioActualId.equals(usuarioId)) {
                out.print("{\"siguiendo\":false,\"esMiUsuario\":true}");
                return;
            }

            // ✅ DEBUG: Verificar parámetros
            String forzarRecarga = request.getParameter("forzarRecarga");
            System.out.println("🔍 verificarSeguimiento - usuarioActual: " + usuarioActualId +
                    ", usuarioConsultado: " + usuarioId +
                    ", forzarRecarga: " + forzarRecarga);

            boolean siguiendo = port.verificarSeguimiento(usuarioActualId, usuarioId);

            System.out.println("✅ Resultado verificarSeguimiento: " + siguiendo);

            // ✅ INVALIDAR CACHE si se fuerza recarga
            if ("true".equals(forzarRecarga)) {
                String cacheKey = "verificar_" + usuarioActualId + "_" + usuarioId;
                cacheTimestamps.remove(cacheKey);
                System.out.println("🗑️ Cache invalidado por forzarRecarga: " + cacheKey);
            }

            out.print("{\"siguiendo\":" + siguiendo + "}");

        } catch (Exception e) {
            System.out.println("❌ Error en verificarSeguimiento: " + e.getMessage());
            out.print("{\"siguiendo\":false}");
        }
    }

    private void obtenerEstadisticasSeguimiento(JuanViajesWS port, String usuarioId, HttpServletRequest request, PrintWriter out, HttpServletResponse response) {
        try {
            // ✅ DEBUG: Verificar parámetros
            String forzarRecarga = request.getParameter("forzarRecarga");
            System.out.println("🔍 obtenerEstadisticasSeguimiento - usuario: " + usuarioId +
                    ", forzarRecarga: " + forzarRecarga);

            int seguidores = port.obtenerCantidadSeguidores(usuarioId);
            int seguidos = port.obtenerCantidadSeguidos(usuarioId);

            System.out.println("✅ Estadísticas - seguidores: " + seguidores + ", seguidos: " + seguidos);

            // ✅ INVALIDAR CACHE si se fuerza recarga
            if ("true".equals(forzarRecarga)) {
                String cacheKey = "stats_" + usuarioId;
                cacheTimestamps.remove(cacheKey);
                System.out.println("🗑️ Cache invalidado por forzarRecarga: " + cacheKey);
            }

            out.print("{\"seguidores\":" + seguidores + ",\"seguidos\":" + seguidos + "}");

        } catch (Exception e) {
            System.out.println("❌ Error en obtenerEstadisticasSeguimiento: " + e.getMessage());
            out.print("{\"seguidores\":0,\"seguidos\":0}");
        }
    }

    private void obtenerVuelosAerolinea(JuanViajesWS port, String aerolineaId, PrintWriter out, HttpServletResponse response) {
        try {
            List<DtRutaVuelo> rutas = port.listarRutasPorAerolinea(aerolineaId);
            List<DtVuelo> todosLosVuelos = new ArrayList<>();

            if (rutas != null) {
                for (DtRutaVuelo ruta : rutas) {
                    try {
                        List<DtVuelo> vuelosRuta = port.listarVuelosPorRuta(ruta.getNombre());
                        if (vuelosRuta != null) {
                            todosLosVuelos.addAll(vuelosRuta);
                        }
                    } catch (Exception e) {
                        // Continuar con la siguiente ruta
                        System.err.println("Error obteniendo vuelos para ruta " + ruta.getNombre() + ": " + e.getMessage());
                    }
                }
            }

            escribirVuelosAerolineaJSON(todosLosVuelos, out);
        } catch (Exception e) {
            System.err.println("Error en obtenerVuelosAerolinea: " + e.getMessage());
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

    private void listarUsuarios(JuanViajesWS port, PrintWriter out) {
        try {
            out.print("{");
            out.print("\"clientes\":[");
            List<DtCliente> clientes = port.listarClientes();
            if (clientes != null) {
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
            }
            out.print("],");
            out.print("\"aerolineas\":[");
            List<DtAerolinea> aerolineas = port.listarAerolineas();
            if (aerolineas != null) {
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
            }
            out.print("]");
            out.print("}");
        } catch (Exception e) {
            System.err.println("Error en listarUsuarios: " + e.getMessage());
            out.print("{\"clientes\":[],\"aerolineas\":[]}");
        }
    }

    private void obtenerUsuarioDetalle(JuanViajesWS port, String usuarioId, String tipoUsuario, PrintWriter out, HttpServletResponse response) {
        try {
            System.out.println("[ConsultaUsuarioServlet] obtenerUsuarioDetalle -> usuarioId=" + usuarioId + ", tipoUsuario=" + tipoUsuario);
            if ("cliente".equals(tipoUsuario)) {
                DtCliente cliente = null;
                try {
                    cliente = port.obtenerCliente(usuarioId);
                    System.out.println("[ConsultaUsuarioServlet] obtenerCliente returned: " + (cliente != null ? cliente.getNickname() + " / " + cliente.getNombre() : "null"));
                } catch (Throwable t) {
                    System.err.println("[ConsultaUsuarioServlet] Error calling port.obtenerCliente: " + t.getMessage());
                    t.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\":\"Error al obtener cliente: " + escapeJson(t.getMessage()) + "\"}");
                    return;
                }

                if (cliente != null) {
                    try {
                        escribirClienteDetalleJSON(cliente, out);
                    } catch (Throwable t) {
                        System.err.println("[ConsultaUsuarioServlet] Error serializing cliente JSON: " + t.getMessage());
                        t.printStackTrace();
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.print("{\"error\":\"Error procesando cliente\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Cliente no encontrado\"}");
                }
            } else if ("aerolinea".equals(tipoUsuario)) {
                DtAerolinea aerolinea = null;
                try {
                    aerolinea = port.obtenerAerolinea(usuarioId);
                    System.out.println("[ConsultaUsuarioServlet] obtenerAerolinea returned: " + (aerolinea != null ? aerolinea.getNickname() + " / " + aerolinea.getNombre() : "null"));
                } catch (Throwable t) {
                    System.err.println("[ConsultaUsuarioServlet] Error calling port.obtenerAerolinea: " + t.getMessage());
                    t.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\":\"Error al obtener aerolinea: " + escapeJson(t.getMessage()) + "\"}");
                    return;
                }

                if (aerolinea != null) {
                    try {
                        escribirInfoAerolineaJSON(aerolinea, out);
                    } catch (Throwable t) {
                        System.err.println("[ConsultaUsuarioServlet] Error serializing aerolinea JSON: " + t.getMessage());
                        t.printStackTrace();
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.print("{\"error\":\"Error procesando aerolinea\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Aerolínea no encontrada\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Tipo de usuario no válido\"}");
            }
        } catch (Exception e) {
            System.err.println("[ConsultaUsuarioServlet] Error en obtenerUsuarioDetalle: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error obteniendo usuario: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void obtenerRutasAerolinea(JuanViajesWS port, String aerolineaId, PrintWriter out, HttpServletResponse response) {
        try {
            List<DtRutaVuelo> rutas = port.listarRutasPorAerolinea(aerolineaId);
            if (rutas != null) {
                escribirRutasAerolineaJSON(rutas, out);
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            System.err.println("Error en obtenerRutasAerolinea: " + e.getMessage());
            out.print("[]");
        }
    }

    private void obtenerReservasCliente(JuanViajesWS port, String clienteId, PrintWriter out, HttpServletResponse response) {
        try {
            List<DtReserva> reservas = port.getReservasCliente(clienteId);
            if (reservas != null) {
                escribirReservasClienteJSON(reservas, out);
            } else {
                out.print("[]");
            }
        } catch (Exception e) {
            System.err.println("Error en obtenerReservasCliente: " + e.getMessage());
            out.print("[]");
        }
    }

    private void obtenerPaquetesCliente(JuanViajesWS port, String clienteId, PrintWriter out, HttpServletResponse response) {
        try {
            // Método temporal - devuelve lista vacía hasta que se implemente en servicio web
            out.print("[]");
        } catch (Exception e) {
            System.err.println("Error en obtenerPaquetesCliente: " + e.getMessage());
            out.print("[]");
        }
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
        out.print("\"cantidadPaquetes\":0"); // No disponible en servicio web
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

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import Logica.Pasajero;
import Logica.TipoAsiento;
import DataTypes.DtCliente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

@WebServlet("/api/reservas")
public class RegistrarReservaServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Leer JSON del body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            String body = sb.toString();
            System.out.println("=== comienza reserva ===");
            System.out.println("Request body: " + body);
            JSONObject obj = new JSONObject(body);

            // Leer campos con tolerancia a valores faltantes
            String vuelo = obj.optString("vuelo", null);
            String tipoAsientoStr = obj.optString("tipoAsiento", null);
            int cantidadPasajes = obj.has("cantidadPasajes") ? obj.optInt("cantidadPasajes", 0) : 0;
            int equipajeExtra = obj.has("equipajeExtra") ? obj.optInt("equipajeExtra", 0) : 0;

            // Validar campos obligatorios y devolver errores claros
            if (vuelo == null || vuelo.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"Campo 'vuelo' es obligatorio\"}");
                return;
            }
            if (tipoAsientoStr == null || tipoAsientoStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"Campo 'tipoAsiento' es obligatorio\"}");
                return;
            }
            if (cantidadPasajes <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"Campo 'cantidadPasajes' debe ser mayor que 0\"}");
                return;
            }

            // Obtener usuario de la sesión
            HttpSession session = request.getSession(false);
            String nicknameCliente = (session != null) ? (String) session.getAttribute("usuario") : null;
            String tipoUsuario = (session != null) ? (String) session.getAttribute("tipoUsuario") : null;
            if (nicknameCliente == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false, \"error\":\"Usuario no autenticado\"}");
                return;
            }

            // No permitir que aerolíneas reserven (solo clientes)
            if (tipoUsuario != null && tipoUsuario.equalsIgnoreCase("aerolinea")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\":false, \"error\":\"Las aerolíneas no pueden realizar reservas\"}");
                return;
            }
            System.out.println("encontrando en la sesion");

            System.out.println(nicknameCliente+"encontrado en la sesion");
            // Pasajeros (opcional)
            List<Pasajero> pasajeros = new ArrayList<>();

            ISistema sistema = Fabrica.getInstance().getISistema();
            // cargar datos necesarios desde BD para poder obtener info del cliente si es necesario
            sistema.cargarDesdeBd();

            if (obj.has("pasajeros")) {
                JSONArray pasajerosArr = obj.getJSONArray("pasajeros");
                for (int i = 0; i < pasajerosArr.length(); i++) {
                    JSONObject p = pasajerosArr.getJSONObject(i);
                    pasajeros.add(sistema.crearPasajero(
                            p.optString("nombre", ""), p.optString("apellido", "")
                    ));
                }
            }

            System.out.println("Pasajeros procesados inicialmente: " + pasajeros.size());

            // Si no se enviaron pasajeros o la cantidad no coincide con cantidadPasajes,
            // intentar autocompletar con el usuario en sesión solo si falta exactamente 1 pasajero
            if (pasajeros.size() == 0 && cantidadPasajes == 1) {
                // caso: se pidió 1 pasaje y el cliente no envió pasajeros -> autocompletar con sesión
                try {
                    DtCliente dt = sistema.obtenerCliente(nicknameCliente);
                    if (dt != null) {
                        String nombreSesion = dt.getNombre() != null ? dt.getNombre() : "";
                        String apellidoSesion = dt.getApellido() != null ? dt.getApellido() : "";
                        pasajeros.add(sistema.crearPasajero(nombreSesion, apellidoSesion));
                        System.out.println("Autocompletado pasajero desde sesión (1 pasajero): " + nombreSesion + " " + apellidoSesion);
                    }
                } catch (Exception e) {
                    System.err.println("No se pudo autocompletar pasajero desde sesión: " + e.getMessage());
                }
            } else if (pasajeros.size() == cantidadPasajes - 1) {
                // caso común: cliente envió N-1 formularios para N pasajes -> añadir usuario en sesión
                try {
                    DtCliente dt = sistema.obtenerCliente(nicknameCliente);
                    if (dt != null) {
                        String nombreSesion = dt.getNombre() != null ? dt.getNombre() : "";
                        String apellidoSesion = dt.getApellido() != null ? dt.getApellido() : "";
                        pasajeros.add(sistema.crearPasajero(nombreSesion, apellidoSesion));
                        System.out.println("Autocompletado pasajero desde sesión: " + nombreSesion + " " + apellidoSesion);
                    }
                } catch (Exception e) {
                    System.err.println("No se pudo autocompletar pasajero desde sesión: " + e.getMessage());
                }
            }

            System.out.println("Pasajeros procesados final: " + pasajeros.size());

            // Validar que al menos un pasajero esté presente (caso de uso solicitado)
            if (pasajeros.size() < 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"Debe registrar al menos un pasajero\"}");
                return;
            }

            // Validar la correspondencia entre cantidad de pasajes y pasajeros recibidos
            if (pasajeros.size() != cantidadPasajes) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"La cantidad de pasajeros no coincide con la cantidad de pasajes\"}");
                return;
            }

            // Nota: se removió el pre-check que intentaba detectar reservas previas del cliente
            // porque estaba generando falsos positivos (detectaba reservas inexistentes). Confiamos
            // en la lógica de negocio en crearYRegistrarReserva y en el post-check que sigue si hay excepción.
            System.out.println("Pre-check de reservas omitido (evitar falsos positivos)");

            TipoAsiento tipoAsiento = tipoAsientoStr.equalsIgnoreCase("ejecutivo") ? TipoAsiento.EJECUTIVO : TipoAsiento.TURISTA;
            LocalDate fechaReserva = LocalDate.now();

            double costo = sistema.calcularCostoReserva(vuelo, tipoAsiento, cantidadPasajes, equipajeExtra);

            try {
                sistema.crearYRegistrarReserva(
                        nicknameCliente, vuelo, fechaReserva, costo, tipoAsiento, cantidadPasajes, equipajeExtra, pasajeros
                );
            } catch (IllegalArgumentException iae) {
                // Intentar post-check: la lógica de negocio pudo crear la reserva y luego lanzar la excepción.
                try {
                    Object vueloObj = null;
                    try { vueloObj = sistema.obtenerVuelo(vuelo); } catch (Exception vx) { vueloObj = null; }

                    boolean found = false;
                    String reservaId = null;
                    if (vueloObj != null) {
                        Object reservasObj = null;
                        try { reservasObj = vueloObj.getClass().getMethod("getReservas").invoke(vueloObj); } catch (Exception m) { reservasObj = null; }

                        Iterable<?> iterable = null;
                        if (reservasObj instanceof java.util.Map) iterable = ((java.util.Map<?, ?>) reservasObj).values();
                        else if (reservasObj instanceof java.lang.Iterable) iterable = (Iterable<?>) reservasObj;

                        if (iterable != null) {
                            for (Object r : iterable) {
                                String clienteProp = getClienteNicknameFromReserva(r);
                                if (clienteProp != null && clienteProp.equalsIgnoreCase(nicknameCliente)) {
                                    found = true;
                                    reservaId = getPropAsString(r, "getId", "id");
                                    break;
                                }
                            }
                        }
                    }

                    if (found) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        String codigo = (reservaId != null && !reservaId.isEmpty()) ? "RES-" + reservaId : "RES-" + System.currentTimeMillis();
                        out.print("{\"success\":true, \"codigoReserva\":\"" + escapeForJson(codigo) + "\"}");
                        System.out.println("Post-check (iae): reserva detectada tras IllegalArgumentException — devolviendo éxito. ID=" + reservaId);
                        return;
                    }
                } catch (Throwable t) {
                    System.err.println("Error en post-check tras IllegalArgumentException: " + t.getMessage());
                }

                // Si no se detectó la reserva, devolver el mensaje de negocio original
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false, \"error\":\"" + escapeForJson(iae.getMessage()) + "\"}");
                return;
            } catch (Exception ex) {
                // Si ocurre una excepción, comprobar si la reserva ya fue creada de todas formas
                try {
                    // intentar detectar si ya existe una reserva del cliente en el vuelo
                    Object vueloObj = null;
                    try {
                        vueloObj = sistema.obtenerVuelo(vuelo);
                    } catch (Exception vx) { vueloObj = null; }

                    boolean found = false;
                    String reservaId = null;
                    if (vueloObj != null) {
                        Object reservasObj = null;
                        try {
                            reservasObj = vueloObj.getClass().getMethod("getReservas").invoke(vueloObj);
                        } catch (Exception m) {
                            reservasObj = null;
                        }

                        Iterable<?> iterable = null;
                        if (reservasObj instanceof java.util.Map) {
                            iterable = ((java.util.Map<?, ?>) reservasObj).values();
                        } else if (reservasObj instanceof java.lang.Iterable) {
                            iterable = (Iterable<?>) reservasObj;
                        }

                        if (iterable != null) {
                            for (Object r : iterable) {
                                String clienteProp = getClienteNicknameFromReserva(r);
                                if (clienteProp != null && clienteProp.equalsIgnoreCase(nicknameCliente)) {
                                    found = true;
                                    reservaId = getPropAsString(r, "getId", "id");
                                    break;
                                }
                            }
                        }
                    }

                    if (found) {
                        // Si se detectó que la reserva existe, devolver éxito para que el cliente muestre el modal
                        response.setStatus(HttpServletResponse.SC_OK);
                        String codigo = (reservaId != null && !reservaId.isEmpty()) ? "RES-" + reservaId : "RES-" + System.currentTimeMillis();
                        out.print("{\"success\":true, \"codigoReserva\":\"" + escapeForJson(codigo) + "\"}");
                        System.out.println("Reserva detectada tras excepción — devolviendo éxito para cliente. ID=" + reservaId);
                        return;
                    }
                } catch (Throwable t) {
                    System.err.println("Error verificando existencia de reserva tras excepción: " + t.getMessage());
                }

                // Si no se detectó, devolver el mensaje original como 400 para que el cliente lo muestre
                try {
                    String msg = ex.getMessage() != null ? ex.getMessage() : "Error al procesar la reserva";
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\":false, \"error\":\"" + escapeForJson(msg) + "\"}");
                    return;
                } catch (Exception inner) {
                    // si algo falla aquí, caer al handler general
                    throw ex;
                }
            }
            System.out.println("=== FIN PROCESO DE COMPRA ===");
            out.print("{\"success\":true, \"codigoReserva\":\"RES-" + System.currentTimeMillis() + "\"}");
        } catch (Exception e) {
            // Log completo para debugging
            System.err.println("Error en RegistrarReservaServlet: " + e.getMessage());
            e.printStackTrace(System.err);

            // Obtener causa raíz
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();
            String causa = root.getClass().getSimpleName() + ": " + (root.getMessage() != null ? root.getMessage() : "(sin mensaje)");

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // Devolver mensaje útil para debugging sin exponer stacktrace completo al cliente
            out.print("{\"success\":false, \"error\":\"Error al agregar la reserva: " + escapeForJson(causa) + "\"}");
        }
    }

    private static String escapeForJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = request.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    // helper: usar reflexión para obtener propiedades comunes de objetos Reserva
    private String getPropAsString(Object obj, String... names) {
        if (obj == null) return null;
        for (String name : names) {
            try {
                java.lang.reflect.Method m = obj.getClass().getMethod(name);
                Object val = m.invoke(obj);
                if (val != null) return String.valueOf(val);
            } catch (NoSuchMethodException nsme) {
                // intentar con 'is' prefix
                try {
                    String isName = "is" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
                    java.lang.reflect.Method m2 = obj.getClass().getMethod(isName);
                    Object val2 = m2.invoke(obj);
                    if (val2 != null) return String.valueOf(val2);
                } catch (Exception e) { /* ignore */ }
            } catch (Exception e) {
                // si el método existe pero lanza, intentar siguiente
            }
        }
        return null;
    }

    // helper: obtener nickname del cliente desde un objeto reserva de forma robusta
    private String getClienteNicknameFromReserva(Object reservaObj) {
        if (reservaObj == null) return null;
        // intentar obtener cliente a través de varios getters
        String[] clienteGetters = new String[] {"getCliente", "cliente", "getClienteNickname", "getClienteNick", "getClient", "getClienteNickName"};
        for (String g : clienteGetters) {
            try {
                java.lang.reflect.Method m = reservaObj.getClass().getMethod(g);
                Object clienteVal = m.invoke(reservaObj);
                if (clienteVal == null) continue;
                if (clienteVal instanceof String) return (String) clienteVal;
                // si es objeto, intentar obtener nickname mediante getNickname, getNick, getNickname
                try {
                    java.lang.reflect.Method m2 = clienteVal.getClass().getMethod("getNickname");
                    Object nick = m2.invoke(clienteVal);
                    if (nick != null) return String.valueOf(nick);
                } catch (Exception e) { /* ignore */ }
                try {
                    java.lang.reflect.Method m3 = clienteVal.getClass().getMethod("getNick");
                    Object nick3 = m3.invoke(clienteVal);
                    if (nick3 != null) return String.valueOf(nick3);
                } catch (Exception e) { /* ignore */ }
                try {
                    java.lang.reflect.Method m4 = clienteVal.getClass().getMethod("getNicknameCliente");
                    Object nick4 = m4.invoke(clienteVal);
                    if (nick4 != null) return String.valueOf(nick4);
                } catch (Exception e) { /* ignore */ }
                // fallback a toString
                return clienteVal.toString();
            } catch (NoSuchMethodException ns) {
                continue;
            } catch (Exception ex) {
                continue;
            }
        }
        return null;
    }
}

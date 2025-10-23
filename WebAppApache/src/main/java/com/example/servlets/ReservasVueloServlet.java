// src/main/java/com/example/servlets/ReservasVueloServlet.java
package com.example.servlets;

import DataTypes.DtCliente;
import DataTypes.DtReserva;
import Logica.Fabrica;
import Logica.ISistema;
import DataTypes.DtVuelo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@WebServlet("/api/reservas-vuelo")
public class ReservasVueloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombreVuelo = request.getParameter("nombreVuelo");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            // Enfoque simple: buscar en todos los clientes quiénes tienen reservas en este vuelo
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            boolean primeraReserva = true;

            for (DtCliente cliente : sistema.listarClientes()) {
                List<DtReserva> reservasCliente = cliente.getReservas();
                if (reservasCliente == null) continue;
                for (DtReserva reserva : reservasCliente) {
                    // Verificar si esta reserva pertenece al vuelo solicitado
                    if (reservaPerteneceAVuelo(reserva, nombreVuelo, sistema)) {
                        if (!primeraReserva) {
                            jsonBuilder.append(",");
                        }

                        jsonBuilder.append("{");
                        jsonBuilder.append("\"id\":\"").append(escapeJson(String.valueOf(reserva.getId()))).append("\",");
                        jsonBuilder.append("\"clienteNombre\":\"").append(escapeJson(cliente.getNombre() + " " + cliente.getApellido())).append("\",");
                        jsonBuilder.append("\"tipoAsiento\":\"").append(escapeJson(String.valueOf(reserva.getTipoAsiento()))).append("\",");
                        jsonBuilder.append("\"cantidadPasajes\":").append(reserva.getCantidadPasajes()).append(",");
                        jsonBuilder.append("\"equipajeExtra\":").append(reserva.getUnidadesEquipajeExtra()).append(",");
                        jsonBuilder.append("\"costoTotal\":").append(reserva.getCosto()).append(",");
                        jsonBuilder.append("\"fechaReserva\":\"").append(escapeJson(String.valueOf(reserva.getFecha()))).append("\"");

                        jsonBuilder.append("}");

                        primeraReserva = false;
                    }
                }
            }

            jsonBuilder.append("]");
            out.print(jsonBuilder.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Error al obtener reservas: " + e.getMessage() + "\"}");
            log("Error en ReservasVueloServlet", e);
        }
    }

    private boolean reservaPerteneceAVuelo(DtReserva reserva, String nombreVuelo, ISistema sistema) {
        try {
            DtVuelo vuelo = sistema.verInfoVueloDt(nombreVuelo);
            if (vuelo != null) {
                Object reservasObj = vuelo.getReservas();
                if (reservasObj == null) return false;

                Collection<?> reservasCollection = null;
                if (reservasObj instanceof Map) {
                    reservasCollection = ((Map<?, ?>) reservasObj).values();
                } else if (reservasObj instanceof Collection) {
                    reservasCollection = (Collection<?>) reservasObj;
                }

                if (reservasCollection != null) {
                    for (Object rObj : reservasCollection) {
                        String idR = getPropAsString(rObj, "getId", "id");
                        if (idR != null && idR.equals(String.valueOf(reserva.getId()))) return true;
                    }
                } else {
                    // Si no es colección ni map, intentar comparar directamente si es un DtReserva u objeto con id
                    String idR = getPropAsString(reservasObj, "getId", "id");
                    if (idR != null && idR.equals(String.valueOf(reserva.getId()))) return true;
                }
            }
        } catch (Exception e) {
            log("Error verificando si reserva pertenece a vuelo", e);
        }
        return false;
    }

    // helper reflexivo para obtener propiedades o métodos comunes
    private String getPropAsString(Object obj, String... candidateMethods) {
        if (obj == null) return null;
        for (String mName : candidateMethods) {
            try {
                Method m = obj.getClass().getMethod(mName);
                Object val = m.invoke(obj);
                if (val != null) return String.valueOf(val);
            } catch (NoSuchMethodException nsme) {
                // ignore
            } catch (Exception ignore) {}
        }
        // try fields
        for (String fName : candidateMethods) {
            try {
                java.lang.reflect.Field f = obj.getClass().getDeclaredField(fName);
                f.setAccessible(true);
                Object val = f.get(obj);
                if (val != null) return String.valueOf(val);
            } catch (Exception ignore) {}
        }
        return null;
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
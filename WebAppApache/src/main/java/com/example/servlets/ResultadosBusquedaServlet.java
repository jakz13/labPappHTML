package com.example.servlets;

import DataTypes.DtAerolinea;
import logica.Fabrica;
import logica.ISistema;
import DataTypes.DtRutaVuelo;
import DataTypes.DtPaquete;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.*;

@WebServlet("/ResultadosBusqueda")
public class ResultadosBusquedaServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar encoding
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String query = request.getParameter("q");
        String orden = request.getParameter("orden");

        System.out.println("=== BÚSQUEDA INICIADA ===");
        System.out.println("Query recibido: [" + query + "]");
        System.out.println("Orden: " + orden);

        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            // Normalizar query
            String queryBusqueda = "";
            boolean hayBusqueda = false;

            if (query != null && !query.trim().isEmpty()) {
                queryBusqueda = query.trim().toLowerCase();
                hayBusqueda = true;
                System.out.println("✅ Búsqueda activa con: '" + queryBusqueda + "'");
            } else {
                System.out.println("📋 Mostrando todos los resultados");
            }

            // Obtener TODAS las rutas y paquetes
            List<DtRutaVuelo> todasRutas = obtenerTodasLasRutasConfirmadas(sistema);
            List<DtPaquete> todosPaquetes = sistema.listarPaquetes();
            if (todosPaquetes == null) todosPaquetes = new ArrayList<>();

            System.out.println("📊 Total rutas: " + todasRutas.size());
            System.out.println("📦 Total paquetes: " + todosPaquetes.size());

            // Separar en coincidentes y no coincidentes
            List<DtRutaVuelo> rutasCoincidentes = new ArrayList<>();
            List<DtRutaVuelo> rutasNoCoincidentes = new ArrayList<>();
            List<DtPaquete> paquetesCoincidentes = new ArrayList<>();
            List<DtPaquete> paquetesNoCoincidentes = new ArrayList<>();

            if (!hayBusqueda) {
                // Sin búsqueda: todo va a "no coincidentes"
                rutasNoCoincidentes.addAll(todasRutas);
                paquetesNoCoincidentes.addAll(todosPaquetes);
            } else {
                // CON búsqueda: filtrar
                for (DtRutaVuelo ruta : todasRutas) {
                    if (coincideRuta(ruta, queryBusqueda)) {
                        rutasCoincidentes.add(ruta);
                        System.out.println("✅ Ruta coincide: " + ruta.getNombre());
                    } else {
                        rutasNoCoincidentes.add(ruta);
                    }
                }

                for (DtPaquete paquete : todosPaquetes) {
                    if (coincidePaquete(paquete, queryBusqueda)) {
                        paquetesCoincidentes.add(paquete);
                        System.out.println("✅ Paquete coincide: " + paquete.getNombre());
                    } else {
                        paquetesNoCoincidentes.add(paquete);
                    }
                }
            }

            // Ordenar según parámetro
            if ("alfabetico".equals(orden)) {
                ordenarAlfabeticamente(rutasCoincidentes, rutasNoCoincidentes,
                        paquetesCoincidentes, paquetesNoCoincidentes);
            } else {
                // Por defecto: fecha descendente
                ordenarPorFecha(rutasCoincidentes, rutasNoCoincidentes);
            }

            // Estadísticas
            System.out.println("📈 Resultados:");
            System.out.println("   Rutas coincidentes: " + rutasCoincidentes.size());
            System.out.println("   Rutas no coincidentes: " + rutasNoCoincidentes.size());
            System.out.println("   Paquetes coincidentes: " + paquetesCoincidentes.size());
            System.out.println("   Paquetes no coincidentes: " + paquetesNoCoincidentes.size());

            // Pasar al JSP
            request.setAttribute("rutasCoincidentes", rutasCoincidentes);
            request.setAttribute("rutasNoCoincidentes", rutasNoCoincidentes);
            request.setAttribute("paquetesCoincidentes", paquetesCoincidentes);
            request.setAttribute("paquetesNoCoincidentes", paquetesNoCoincidentes);
            request.setAttribute("queryOriginal", query);
            request.setAttribute("hayBusqueda", hayBusqueda);
            request.setAttribute("totalCount",
                    todasRutas.size() + todosPaquetes.size());

            RequestDispatcher dispatcher = request.getRequestDispatcher("/resultados-busqueda.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();

            request.setAttribute("error", "Error: " + e.getMessage());
            request.setAttribute("rutasCoincidentes", new ArrayList<>());
            request.setAttribute("rutasNoCoincidentes", new ArrayList<>());
            request.setAttribute("paquetesCoincidentes", new ArrayList<>());
            request.setAttribute("paquetesNoCoincidentes", new ArrayList<>());
            request.setAttribute("totalCount", 0);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/resultados-busqueda.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Verifica si una ruta coincide con la búsqueda
     */
    private boolean coincideRuta(DtRutaVuelo ruta, String query) {
        if (ruta.getNombre() != null &&
                ruta.getNombre().toLowerCase().contains(query)) {
            return true;
        }
        if (ruta.getDescripcion() != null &&
                ruta.getDescripcion().toLowerCase().contains(query)) {
            return true;
        }
        if (ruta.getDescripcionCorta() != null &&
                ruta.getDescripcionCorta().toLowerCase().contains(query)) {
            return true;
        }
        if (ruta.getCiudadOrigen() != null &&
                ruta.getCiudadOrigen().toLowerCase().contains(query)) {
            return true;
        }
        if (ruta.getCiudadDestino() != null &&
                ruta.getCiudadDestino().toLowerCase().contains(query)) {
            return true;
        }
        return false;
    }

    /**
     * Verifica si un paquete coincide con la búsqueda
     */
    private boolean coincidePaquete(DtPaquete paquete, String query) {
        if (paquete.getNombre() != null &&
                paquete.getNombre().toLowerCase().contains(query)) {
            return true;
        }
        if (paquete.getDescripcion() != null &&
                paquete.getDescripcion().toLowerCase().contains(query)) {
            return true;
        }
        return false;
    }

    /**
     * Obtiene todas las rutas confirmadas del sistema
     */
    private List<DtRutaVuelo> obtenerTodasLasRutasConfirmadas(ISistema sistema) {
        List<DtRutaVuelo> rutasConfirmadas = new ArrayList<>();

        try {
            List<DtAerolinea> aerolineas = sistema.listarAerolineas();

            for (DtAerolinea aerolinea : aerolineas) {
                try {
                    List<DtRutaVuelo> rutas = sistema.listarRutasPorAerolinea(
                            aerolinea.getNickname());

                    for (DtRutaVuelo ruta : rutas) {
                        if (ruta.getEstado() != null &&
                                "CONFIRMADA".equalsIgnoreCase(ruta.getEstado().toString())) {
                            rutasConfirmadas.add(ruta);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Error con aerolínea " +
                            aerolinea.getNickname());
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo rutas: " + e.getMessage());
        }

        return rutasConfirmadas;
    }

    /**
     * Ordena alfabéticamente
     */
    private void ordenarAlfabeticamente(List<DtRutaVuelo> rutas1,
                                        List<DtRutaVuelo> rutas2,
                                        List<DtPaquete> paq1,
                                        List<DtPaquete> paq2) {
        Comparator<DtRutaVuelo> rutaComp = (r1, r2) -> {
            String n1 = r1.getNombre() != null ? r1.getNombre() : "";
            String n2 = r2.getNombre() != null ? r2.getNombre() : "";
            return n1.compareToIgnoreCase(n2);
        };

        Comparator<DtPaquete> paqComp = (p1, p2) -> {
            String n1 = p1.getNombre() != null ? p1.getNombre() : "";
            String n2 = p2.getNombre() != null ? p2.getNombre() : "";
            return n1.compareToIgnoreCase(n2);
        };

        rutas1.sort(rutaComp);
        rutas2.sort(rutaComp);
        paq1.sort(paqComp);
        paq2.sort(paqComp);

        System.out.println("🔤 Ordenado alfabéticamente");
    }

    /**
     * Ordena por fecha descendente
     */
    private void ordenarPorFecha(List<DtRutaVuelo> rutas1,
                                 List<DtRutaVuelo> rutas2) {
        Comparator<DtRutaVuelo> fechaComp = (r1, r2) -> {
            if (r1.getFechaAlta() != null && r2.getFechaAlta() != null) {
                return r2.getFechaAlta().compareTo(r1.getFechaAlta());
            }
            return 0;
        };

        rutas1.sort(fechaComp);
        rutas2.sort(fechaComp);

        System.out.println("📅 Ordenado por fecha descendente");
    }
}
@WebServlet("/consultarRutas")
public class ConsultaRutaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Obtener parámetros de filtro
            String aerolinea = request.getParameter("aerolinea");
            String categoria = request.getParameter("categoria");

            // Obtener instancia de tu fachada/lógica
            IFachada fachada = Fachada.getInstancia();

            List<DtRutaVuelo> rutas;

            if (aerolinea != null && !aerolinea.trim().isEmpty()) {
                // Filtrar por aerolínea específica
                rutas = fachada.listarRutasPorAerolinea(aerolinea);
            } else {
                // Obtener todas las rutas (necesitarías implementar este método)
                rutas = fachada.listarRutasConfirmadas();
            }

            // Filtrar por categoría si se especifica
            if (categoria != null && !categoria.trim().isEmpty()) {
                rutas = rutas.stream()
                        .filter(ruta -> tieneCategoria(ruta, categoria))
                        .collect(Collectors.toList());
            }

            // Convertir a JSON
            String jsonResponse = convertirRutasAJSON(rutas);
            out.print(jsonResponse);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error al obtener las rutas: " + e.getMessage() + "\"}");
        }
    }

    private boolean tieneCategoria(DtRutaVuelo ruta, String categoria) {
        // Asumiendo que DtRutaVuelo tiene un método getCategorias()
        return ruta.getCategorias().stream()
                .anyMatch(cat -> cat.equalsIgnoreCase(categoria));
    }

    private String convertirRutasAJSON(List<DtRutaVuelo> rutas) {
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < rutas.size(); i++) {
            DtRutaVuelo ruta = rutas.get(i);
            json.append("{")
                    .append("\"id\": \"").append(ruta.getId()).append("\",")
                    .append("\"nombre\": \"").append(escapeJSON(ruta.getNombre())).append("\",")
                    .append("\"descripcionCorta\": \"").append(escapeJSON(ruta.getDescripcionCorta())).append("\",")
                    .append("\"descripcionCompleta\": \"").append(escapeJSON(ruta.getDescripcion())).append("\",")
                    .append("\"aerolinea\": \"").append(escapeJSON(ruta.getAerolinea())).append("\",")
                    .append("\"origen\": \"").append(escapeJSON(ruta.getOrigen())).append("\",")
                    .append("\"destino\": \"").append(escapeJSON(ruta.getDestino())).append("\",")
                    .append("\"hora\": \"").append(ruta.getHora()).append("\",")
                    .append("\"costoTurista\": ").append(ruta.getCostoTurista()).append(",")
                    .append("\"costoEjecutivo\": ").append(ruta.getCostoEjecutivo()).append(",")
                    .append("\"costoEquipaje\": ").append(ruta.getCostoEquipajeExtra()).append(",")
                    .append("\"categorias\": ").append(convertirCategoriasAJSON(ruta.getCategorias())).append(",")
                    .append("\"estado\": \"").append(ruta.getEstado()).append("\",")
                    .append("\"fechaAlta\": \"").append(ruta.getFechaAlta()).append("\",")
                    .append("\"imagen\": \"").append(ruta.getImagen() != null ? ruta.getImagen() : "").append("\",")
                    .append("\"vuelos\": ").append(convertirVuelosAJSON(ruta.getVuelos()))
                    .append("}");

            if (i < rutas.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");
        return json.toString();
    }

    private String convertirCategoriasAJSON(List<String> categorias) {
        if (categorias == null || categorias.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < categorias.size(); i++) {
            json.append("\"").append(escapeJSON(categorias.get(i))).append("\"");
            if (i < categorias.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    private String convertirVuelosAJSON(List<DtVuelo> vuelos) {
        if (vuelos == null || vuelos.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < vuelos.size(); i++) {
            DtVuelo vuelo = vuelos.get(i);
            json.append("{")
                    .append("\"id\": \"").append(vuelo.getId()).append("\",")
                    .append("\"fecha\": \"").append(vuelo.getFecha()).append("\",")
                    .append("\"disponible\": ").append(vuelo.isDisponible())
                    .append("}");

            if (i < vuelos.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    private String escapeJSON(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
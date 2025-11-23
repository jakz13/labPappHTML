<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="DataTypes.DtRutaVuelo" %>
<%@ page import="DataTypes.DtPaquete" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Resultados de Búsqueda - Juan Viajes</title>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css">
    <!-- Estilos personalizados -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CssLogica/estilo-css.css">
</head>
<body>
<!-- Incluir navbar -->
<jsp:include page="navbar.jsp" />

<!-- Encabezado de búsqueda -->
<div class="search-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h1 class="display-5 fw-bold mb-3">
                    <i class="bi bi-search me-3"></i>Resultados de Búsqueda
                </h1>
                <%
                    String query = request.getParameter("q");
                    if (query != null && !query.trim().isEmpty()) {
                %>
                <p class="lead mb-0">Buscando: "<strong><%= query %></strong>"</p>
                <%
                } else {
                %>
                <p class="lead mb-0">Mostrando todos los resultados</p>
                <%
                    }
                %>
            </div>
            <div class="col-md-4 text-md-end">
                <div class="result-count">
                    <%
                        Integer rutasCount = (Integer) request.getAttribute("rutasCount");
                        Integer paquetesCount = (Integer) request.getAttribute("paquetesCount");
                        int total = (rutasCount != null ? rutasCount : 0) + (paquetesCount != null ? paquetesCount : 0);
                    %>
                    <span class="badge bg-primary fs-6">
                            <%= total %> resultado(s) encontrado(s)
                        </span>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="container">
    <!-- Filtros y ordenamiento -->
    <div class="filter-section">
        <div class="row align-items-center">
            <div class="col-md-6">
                <form id="filterForm" action="ResultadosBusqueda" method="get" class="row g-2">
                    <input type="hidden" name="q" value="<%= query != null ? query : "" %>">

                    <div class="col-auto">
                        <label for="orden" class="col-form-label"><strong>Ordenar por:</strong></label>
                    </div>
                    <div class="col-auto">
                        <select name="orden" id="orden" class="form-select" onchange="document.getElementById('filterForm').submit()">
                            <option value="fecha" ${param.orden == 'fecha' ? 'selected' : ''}>Fecha (más recientes)</option>
                            <option value="alfabetico" ${param.orden == 'alfabetico' ? 'selected' : ''}>Orden alfabético</option>
                        </select>
                    </div>

                    <div class="col-auto">
                        <label for="tipo" class="col-form-label"><strong>Filtrar por:</strong></label>
                    </div>
                    <div class="col-auto">
                        <select name="tipo" id="tipo" class="form-select" onchange="document.getElementById('filterForm').submit()">
                            <option value="todos" ${param.tipo == 'todos' ? 'selected' : ''}>Todos</option>
                            <option value="rutas" ${param.tipo == 'rutas' ? 'selected' : ''}>Solo Rutas</option>
                            <option value="paquetes" ${param.tipo == 'paquetes' ? 'selected' : ''}>Solo Paquetes</option>
                        </select>
                    </div>
                </form>
            </div>

            <div class="col-md-6 text-md-end">
                <a href="PaginaPrincipal.jsp" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left me-2"></i>Volver al inicio
                </a>
            </div>
        </div>
    </div>

    <!-- Resultados -->
    <div class="row">
        <!-- Rutas de Vuelo -->
        <%
            List<DtRutaVuelo> rutas = (List<DtRutaVuelo>) request.getAttribute("rutas");
            String tipoFiltro = request.getParameter("tipo");
            boolean mostrarRutas = tipoFiltro == null || "todos".equals(tipoFiltro) || "rutas".equals(tipoFiltro);

            if (mostrarRutas && rutas != null && !rutas.isEmpty()) {
        %>
        <div class="col-12 mb-4">
            <div class="d-flex align-items-center mb-3">
                <h3 class="mb-0">
                    <i class="bi bi-geo-alt text-primary me-2"></i>
                    Rutas de Vuelo
                </h3>
                <span class="badge bg-primary ms-2 fs-6"><%= rutas.size() %></span>
            </div>

            <div class="row">
                <%
                    for (DtRutaVuelo ruta : rutas) {
                        String imagenUrl = ruta.getImagenUrl() != null ? ruta.getImagenUrl() : "https://via.placeholder.com/300x120/3498db/ffffff?text=Ruta+" + java.net.URLEncoder.encode(ruta.getNombre(), "UTF-8");
                %>
                <div class="col-lg-6 mb-3">
                    <div class="card result-card ruta-card h-100">
                        <div class="row g-0 h-100">
                            <div class="col-md-4">
                                <img src="<%= imagenUrl %>" class="ruta-image h-100" alt="<%= ruta.getNombre() %>">
                            </div>
                            <div class="col-md-8">
                                <div class="card-body d-flex flex-column h-100">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <h5 class="card-title">
                                            <a href="DetalleRuta?nombre=<%= java.net.URLEncoder.encode(ruta.getNombre(), "UTF-8") %>"
                                               class="text-decoration-none text-light">
                                                <%= highlightText(ruta.getNombre(), query) %>
                                            </a>
                                        </h5>
                                        <span class="badge badge-ruta">Ruta</span>
                                    </div>

                                    <p class="card-text flex-grow-1 text-light">
                                        <%= highlightText(ruta.getDescripcionCorta() != null ? ruta.getDescripcionCorta() : ruta.getDescripcion(), query) %>
                                    </p>

                                    <div class="mt-auto">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <small class="text-muted">
                                                    <i class="bi bi-airplane me-1"></i>
                                                    <%= ruta.getCiudadOrigen() %> → <%= ruta.getCiudadDestino() %>
                                                </small>
                                                <br>
                                                <small class="text-muted">
                                                    <i class="bi bi-building me-1"></i>
                                                    <%= ruta.getAerolinea() %>
                                                </small>
                                            </div>
                                            <div class="text-end">
                                                <div class="costo-destacado">
                                                    $<%= String.format("%.0f", ruta.getCostoTurista()) %>
                                                </div>
                                                <small class="text-muted">turista</small>
                                            </div>
                                        </div>

                                        <div class="d-flex justify-content-between align-items-center mt-2">
                                            <div>
                                                <% if (ruta.getCategorias() != null && !ruta.getCategorias().isEmpty()) {
                                                    for (int i = 0; i < Math.min(2, ruta.getCategorias().size()); i++) { %>
                                                <span class="badge category-badge">
                                                            <%= ruta.getCategorias().get(i) %>
                                                        </span>
                                                <% } } %>

                                                <span class="badge stats-badge">
                                                        <i class="bi bi-eye me-1"></i><%= ruta.getContadorVisitas() %> vistas
                                                    </span>
                                            </div>
                                            <a href="DetalleRuta?nombre=<%= java.net.URLEncoder.encode(ruta.getNombre(), "UTF-8") %>"
                                               class="btn btn-primary btn-sm action-btn">
                                                Ver Detalles
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                    }
                %>
            </div>
        </div>
        <%
        } else if (mostrarRutas) {
        %>
        <div class="col-12">
            <div class="empty-state">
                <i class="bi bi-geo-alt"></i>
                <h4>No se encontraron rutas</h4>
                <p class="text-muted">No hay rutas que coincidan con tu búsqueda.</p>
            </div>
        </div>
        <%
            }
        %>

        <!-- Paquetes -->
        <%
            List<DtPaquete> paquetes = (List<DtPaquete>) request.getAttribute("paquetes");
            boolean mostrarPaquetes = tipoFiltro == null || "todos".equals(tipoFiltro) || "paquetes".equals(tipoFiltro);

            if (mostrarPaquetes && paquetes != null && !paquetes.isEmpty()) {
        %>
        <div class="col-12 mb-4">
            <div class="d-flex align-items-center mb-3">
                <h3 class="mb-0">
                    <i class="bi bi-box text-success me-2"></i>
                    Paquetes
                </h3>
                <span class="badge bg-success ms-2 fs-6"><%= paquetes.size() %></span>
            </div>

            <div class="row">
                <%
                    for (DtPaquete paquete : paquetes) {
                        String imagenUrl = "https://via.placeholder.com/300x120/22c55e/ffffff?text=Paquete+" + java.net.URLEncoder.encode(paquete.getNombre(), "UTF-8");
                %>
                <div class="col-lg-6 mb-3">
                    <div class="card result-card paquete-card h-100">
                        <div class="row g-0 h-100">
                            <div class="col-md-4">
                                <img src="<%= imagenUrl %>" class="ruta-image h-100" alt="<%= paquete.getNombre() %>">
                            </div>
                            <div class="col-md-8">
                                <div class="card-body d-flex flex-column h-100">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <h5 class="card-title">
                                            <a href="DetallePaquete?nombre=<%= java.net.URLEncoder.encode(paquete.getNombre(), "UTF-8") %>"
                                               class="text-decoration-none text-light">
                                                <%= highlightText(paquete.getNombre(), query) %>
                                            </a>
                                        </h5>
                                        <span class="badge badge-paquete">Paquete</span>
                                    </div>

                                    <p class="card-text flex-grow-1 text-light">
                                        <%= highlightText(paquete.getDescripcion(), query) %>
                                    </p>

                                    <div class="mt-auto">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <small class="text-muted">
                                                    <i class="bi bi-clock me-1"></i>
                                                    Válido por <%= paquete.getPeriodoValidezDias() %> días
                                                </small>
                                                <br>
                                                <small class="text-muted">
                                                    <i class="bi bi-percent me-1"></i>
                                                    <%= paquete.getDescuentoPorc() %>% de descuento
                                                </small>
                                            </div>
                                            <div class="text-end">
                                                <div class="costo-destacado text-success">
                                                    $<%= String.format("%.0f", paquete.getCosto() * (1 - paquete.getDescuentoPorc() / 100.0)) %>
                                                </div>
                                                <small class="text-muted text-decoration-line-through">
                                                    $<%= String.format("%.0f", paquete.getCosto()) %>
                                                </small>
                                            </div>
                                        </div>

                                        <div class="d-flex justify-content-between align-items-center mt-2">
                                            <div>
                                                    <span class="badge bg-warning text-dark">
                                                        <i class="bi bi-collection me-1"></i>
                                                        <%= paquete.getItems() != null ? paquete.getItems().size() : 0 %> rutas incluidas
                                                    </span>
                                            </div>
                                            <a href="DetallePaquete?nombre=<%= java.net.URLEncoder.encode(paquete.getNombre(), "UTF-8") %>"
                                               class="btn btn-success btn-sm action-btn">
                                                Ver Paquete
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                    }
                %>
            </div>
        </div>
        <%
        } else if (mostrarPaquetes) {
        %>
        <div class="col-12">
            <div class="empty-state">
                <i class="bi bi-box"></i>
                <h4>No se encontraron paquetes</h4>
                <p class="text-muted">No hay paquetes que coincidan con tu búsqueda.</p>
            </div>
        </div>
        <%
            }
        %>
    </div>

    <!-- Estado vacío cuando no hay resultados -->
    <%
        if ((rutas == null || rutas.isEmpty()) && (paquetes == null || paquetes.isEmpty())) {
    %>
    <div class="row">
        <div class="col-12">
            <div class="empty-state">
                <i class="bi bi-search"></i>
                <h4>No se encontraron resultados</h4>
                <p class="text-muted">
                    <% if (query != null && !query.trim().isEmpty()) { %>
                    No hay rutas ni paquetes que coincidan con "<strong><%= query %></strong>".
                    <% } else { %>
                    No hay rutas ni paquetes disponibles en este momento.
                    <% } %>
                </p>
                <a href="PaginaPrincipal.jsp" class="btn btn-primary mt-3">
                    <i class="bi bi-house me-2"></i>Volver al inicio
                </a>
            </div>
        </div>
    </div>
    <%
        }
    %>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- Script para resaltar texto -->
<script>
    function highlightText(text, query) {
        if (!query || !text) return text;
        const regex = new RegExp(`(${query})`, 'gi');
        return text.replace(regex, '<mark class="highlight">$1</mark>');
    }

    // Aplicar highlight a todos los textos relevantes
    document.addEventListener('DOMContentLoaded', function() {
        const query = '<%= query != null ? query : "" %>';
        if (query.trim()) {
            // Esta función se ejecuta del lado del servidor, pero por si acaso...
        }
    });
</script>
</body>
</html>

<%!
    // Método helper para resaltar texto en el servidor
    private String highlightText(String text, String query) {
        if (text == null || query == null || query.trim().isEmpty()) {
            return text != null ? text : "";
        }

        String lowerText = text.toLowerCase();
        String lowerQuery = query.toLowerCase();

        if (!lowerText.contains(lowerQuery)) {
            return text;
        }

        // Resaltar todas las ocurrencias
        StringBuilder result = new StringBuilder();
        int lastIndex = 0;
        int index = lowerText.indexOf(lowerQuery);

        while (index >= 0) {
            result.append(text.substring(lastIndex, index));
            result.append("<mark class=\"highlight\">")
                    .append(text.substring(index, index + query.length()))
                    .append("</mark>");

            lastIndex = index + query.length();
            index = lowerText.indexOf(lowerQuery, lastIndex);
        }

        result.append(text.substring(lastIndex));
        return result.toString();
    }
%>
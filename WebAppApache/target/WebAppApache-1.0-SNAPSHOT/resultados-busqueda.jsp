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
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CssLogica/estilo-css.css">

    <style>
        .result-card {
            transition: all 0.3s ease;
            margin-bottom: 20px;
        }
        .result-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }
        .badge-coincidente {
            background: #28a745;
            color: white;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 0.9em;
        }
        .section-header {
            border-bottom: 3px solid #007bff;
            padding-bottom: 10px;
            margin-bottom: 20px;
        }
        mark {
            background-color: #ffeb3b;
            padding: 0.1em 0.2em;
            border-radius: 0.25em;
            font-weight: bold;
        }
    </style>
</head>
<body>
<jsp:include page="navbar.jsp" />

<div class="container mt-4">
    <!-- Encabezado -->
    <div class="row mb-4">
        <div class="col-md-8">
            <h1><i class="bi bi-search me-2"></i>Resultados de Búsqueda</h1>
            <%
                String query = (String) request.getAttribute("queryOriginal");
                Boolean hayBusqueda = (Boolean) request.getAttribute("hayBusqueda");

                if (hayBusqueda != null && hayBusqueda && query != null) {
            %>
            <p class="lead">Buscando: "<strong><%= query %></strong>"</p>
            <%
            } else {
            %>
            <p class="lead">Mostrando todos los resultados</p>
            <%
                }
            %>
        </div>
        <div class="col-md-4 text-end">
            <span class="badge bg-primary fs-6">
                <%= request.getAttribute("totalCount") %> resultados
            </span>
        </div>
    </div>

    <!-- Filtros -->
    <div class="row mb-4">
        <div class="col-md-12">
            <form method="get" action="ResultadosBusqueda" class="row g-2">
                <input type="hidden" name="q" value="<%= query != null ? query : "" %>">

                <div class="col-auto">
                    <label class="form-label"><strong>Ordenar por:</strong></label>
                </div>
                <div class="col-auto">
                    <select name="orden" class="form-select" onchange="this.form.submit()">
                        <option value="fecha" <%= "fecha".equals(request.getParameter("orden")) ? "selected" : "" %>>
                            Fecha (descendente)
                        </option>
                        <option value="alfabetico" <%= "alfabetico".equals(request.getParameter("orden")) ? "selected" : "" %>>
                            Alfabético
                        </option>
                    </select>
                </div>

                <div class="col-auto">
                    <a href="PaginaPrincipal.jsp" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left me-1"></i>Volver
                    </a>
                </div>
            </form>
        </div>
    </div>

    <%
        List<DtRutaVuelo> rutasCoincidentes = (List<DtRutaVuelo>) request.getAttribute("rutasCoincidentes");
        List<DtRutaVuelo> rutasNoCoincidentes = (List<DtRutaVuelo>) request.getAttribute("rutasNoCoincidentes");
        List<DtPaquete> paquetesCoincidentes = (List<DtPaquete>) request.getAttribute("paquetesCoincidentes");
        List<DtPaquete> paquetesNoCoincidentes = (List<DtPaquete>) request.getAttribute("paquetesNoCoincidentes");

        if (rutasCoincidentes == null) rutasCoincidentes = new java.util.ArrayList<>();
        if (rutasNoCoincidentes == null) rutasNoCoincidentes = new java.util.ArrayList<>();
        if (paquetesCoincidentes == null) paquetesCoincidentes = new java.util.ArrayList<>();
        if (paquetesNoCoincidentes == null) paquetesNoCoincidentes = new java.util.ArrayList<>();

        boolean tieneCoincidencias = !rutasCoincidentes.isEmpty() || !paquetesCoincidentes.isEmpty();
    %>

    <!-- RUTAS COINCIDENTES -->
    <% if (!rutasCoincidentes.isEmpty()) { %>
    <div class="section-header">
        <h3>
            <i class="bi bi-geo-alt text-primary me-2"></i>
            Rutas Coincidentes
            <span class="badge-coincidente ms-2">
                <%= rutasCoincidentes.size() %> coincidencia(s)
            </span>
        </h3>
    </div>
    <div class="row">
        <% for (DtRutaVuelo ruta : rutasCoincidentes) { %>
        <div class="col-md-6">
            <div class="card result-card">
                <div class="card-body">
                    <h5 class="card-title">
                        <a href="DetalleRuta?nombre=<%= java.net.URLEncoder.encode(ruta.getNombre(), "UTF-8") %>">
                            <%= ruta.getNombre() %>
                        </a>
                        <span class="badge bg-primary ms-2">Ruta</span>
                    </h5>
                    <p class="card-text">
                        <%= ruta.getDescripcionCorta() != null ? ruta.getDescripcionCorta() : ruta.getDescripcion() %>
                    </p>
                    <p class="text-muted">
                        <i class="bi bi-airplane me-1"></i>
                        <%= ruta.getCiudadOrigen() %> → <%= ruta.getCiudadDestino() %>
                    </p>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="h5 mb-0 text-primary">
                            $<%= String.format("%.0f", ruta.getCostoTurista()) %>
                        </span>
                        <a href="DetalleRuta?nombre=<%= java.net.URLEncoder.encode(ruta.getNombre(), "UTF-8") %>"
                           class="btn btn-primary btn-sm">
                            Ver Detalles
                        </a>
                    </div>
                </div>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>

    <!-- PAQUETES COINCIDENTES -->
    <% if (!paquetesCoincidentes.isEmpty()) { %>
    <div class="section-header mt-4">
        <h3>
            <i class="bi bi-box text-success me-2"></i>
            Paquetes Coincidentes
            <span class="badge-coincidente ms-2">
                <%= paquetesCoincidentes.size() %> coincidencia(s)
            </span>
        </h3>
    </div>
    <div class="row">
        <% for (DtPaquete paquete : paquetesCoincidentes) { %>
        <div class="col-md-6">
            <div class="card result-card">
                <div class="card-body">
                    <h5 class="card-title">
                        <a href="DetallePaquete?nombre=<%= java.net.URLEncoder.encode(paquete.getNombre(), "UTF-8") %>">
                            <%= paquete.getNombre() %>
                        </a>
                        <span class="badge bg-success ms-2">Paquete</span>
                    </h5>
                    <p class="card-text"><%= paquete.getDescripcion() %></p>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="badge bg-warning text-dark">
                            <%= paquete.getDescuentoPorc() %>% descuento
                        </span>
                        <a href="DetallePaquete?nombre=<%= java.net.URLEncoder.encode(paquete.getNombre(), "UTF-8") %>"
                           class="btn btn-success btn-sm">
                            Ver Paquete
                        </a>
                    </div>
                </div>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>

    <!-- Separador -->
    <% if (tieneCoincidencias && (!rutasNoCoincidentes.isEmpty() || !paquetesNoCoincidentes.isEmpty())) { %>
    <hr class="my-4">
    <h4 class="text-muted">Otros Resultados</h4>
    <% } %>

    <!-- RUTAS NO COINCIDENTES -->
    <% if (!rutasNoCoincidentes.isEmpty()) { %>
    <div class="section-header mt-4">
        <h3>
            <i class="bi bi-geo-alt text-primary me-2"></i>
            <%= tieneCoincidencias ? "Más Rutas" : "Rutas de Vuelo" %>
        </h3>
    </div>
    <div class="row">
        <% for (DtRutaVuelo ruta : rutasNoCoincidentes) { %>
        <div class="col-md-6">
            <div class="card result-card">
                <div class="card-body">
                    <h5 class="card-title">
                        <a href="DetalleRuta?nombre=<%= java.net.URLEncoder.encode(ruta.getNombre(), "UTF-8") %>">
                            <%= ruta.getNombre() %>
                        </a>
                    </h5>
                    <p class="card-text text-muted">
                        <%= ruta.getDescripcionCorta() != null ? ruta.getDescripcionCorta() : ruta.getDescripcion() %>
                    </p>
                    <p class="text-muted small">
                        <%= ruta.getCiudadOrigen() %> → <%= ruta.getCiudadDestino() %>
                    </p>
                    <div class="d-flex justify-content-between">
                        <span class="text-primary">$<%= String.format("%.0f", ruta.getCostoTurista()) %></span>
                        <a href="DetalleRuta?nombre=<%= java.net.URLEncoder.encode(ruta.getNombre(), "UTF-8") %>"
                           class="btn btn-outline-primary btn-sm">
                            Ver Detalles
                        </a>
                    </div>
                </div>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>

    <!-- PAQUETES NO COINCIDENTES -->
    <% if (!paquetesNoCoincidentes.isEmpty()) { %>
    <div class="section-header mt-4">
        <h3>
            <i class="bi bi-box text-success me-2"></i>
            <%= tieneCoincidencias ? "Más Paquetes" : "Paquetes" %>
        </h3>
    </div>
    <div class="row">
        <% for (DtPaquete paquete : paquetesNoCoincidentes) { %>
        <div class="col-md-6">
            <div class="card result-card">
                <div class="card-body">
                    <h5 class="card-title">
                        <a href="DetallePaquete?nombre=<%= java.net.URLEncoder.encode(paquete.getNombre(), "UTF-8") %>">
                            <%= paquete.getNombre() %>
                        </a>
                    </h5>
                    <p class="card-text text-muted"><%= paquete.getDescripcion() %></p>
                    <div class="d-flex justify-content-between">
                        <span class="badge bg-warning text-dark">
                            <%= paquete.getDescuentoPorc() %>% OFF
                        </span>
                        <a href="DetallePaquete?nombre=<%= java.net.URLEncoder.encode(paquete.getNombre(), "UTF-8") %>"
                           class="btn btn-outline-success btn-sm">
                            Ver Paquete
                        </a>
                    </div>
                </div>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>

    <!-- Sin resultados -->
    <% if (rutasCoincidentes.isEmpty() && rutasNoCoincidentes.isEmpty() &&
            paquetesCoincidentes.isEmpty() && paquetesNoCoincidentes.isEmpty()) { %>
    <div class="alert alert-info text-center">
        <h4><i class="bi bi-info-circle me-2"></i>No se encontraron resultados</h4>
        <p>No hay rutas ni paquetes que coincidan con tu búsqueda.</p>
        <a href="PaginaPrincipal.jsp" class="btn btn-primary mt-3">
            Volver al inicio
        </a>
    </div>
    <% } %>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
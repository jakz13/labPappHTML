<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="Logica.Fabrica" %>
<%@ page import="Logica.ISistema" %>
<%@ page import="DataTypes.DtRutaVuelo" %>
<%@ page import="DataTypes.DtPaquete" %>
<%@ page import="DataTypes.DtAerolinea" %>
<%
    // Cargar datos desde la base de datos
    ISistema sistema = Fabrica.getInstance().getISistema();
    sistema.cargarDesdeBd();

    // Obtener rutas de forma segura
    List<DtRutaVuelo> rutasDestacadas = new java.util.ArrayList<>();
    List<DtAerolinea> aerolineas = sistema.listarAerolineas();

    for (DtAerolinea aerolinea : aerolineas) {
        try {
            List<DtRutaVuelo> rutasAerolinea = sistema.listarRutasPorAerolinea(aerolinea.getNickname());
            for (DtRutaVuelo ruta : rutasAerolinea) {
                if ("CONFIRMADA".equals(ruta.getEstado())) {
                    rutasDestacadas.add(ruta);
                    if (rutasDestacadas.size() >= 3) break;
                }
            }
            if (rutasDestacadas.size() >= 3) break;
        } catch (Exception e) {
            // Continuar con la siguiente aerolínea si hay error
            continue;
        }
    }

    // Obtener paquetes
    List<DtPaquete> paquetesDestacados = sistema.listarPaquetes();
    if (paquetesDestacados.size() > 2) {
        paquetesDestacados = paquetesDestacados.subList(0, 2);
    }
%>

<html lang="es">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Juan Viajes - Tu plataforma de viajes</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="CssLogica/estilo-css.css">
</head>
<body>
<header>
    <%@ include file="navbar.jsp"%>
</header>

<main class="container mt-4">
    <!-- Hero Section -->
    <section class="hero-section fade-in">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-6">
                    <h1 class="hero-title">Descubre el mundo con Juan Viajes</h1>
                    <p class="hero-subtitle">Tu plataforma premium para reservar vuelos y paquetes turísticos exclusivos. Explora destinos únicos con las mejores ofertas.</p>
                    <div class="hero-buttons">
                        <a class="btn btn-primary btn-lg" href="consulta-vuelo.jsp" role="button">
                            <i class="bi bi-airplane me-2"></i>Explorar Vuelos
                        </a>
                        <a class="btn btn-light btn-lg" href="consulta-paquete.jsp" role="button">
                            <i class="bi bi-gift me-2"></i>Ver Paquetes
                        </a>
                    </div>
                </div>
                <div class="col-lg-6 text-center">
                    <i class="bi bi-airplane-fill" style="font-size: 10rem; opacity: 0.1;"></i>
                </div>
            </div>
        </div>
    </section>

    <div class="row mt-5">
        <!-- Sidebar -->
        <div class="col-lg-3">
            <div class="sidebar-modern fade-in">
                <h5 class="sidebar-title">Categorías</h5>
                <ul class="sidebar-list">
                    <li><a href="consulta-vuelo.jsp?categoria=nacionales"><i class="bi bi-geo-alt"></i> Nacionales</a></li>
                    <li><a href="consulta-vuelo.jsp?categoria=internacionales"><i class="bi bi-globe-americas"></i> Internacionales</a></li>
                    <li><a href="consulta-vuelo.jsp?categoria=europa"><i class="bi bi-building"></i> Europa</a></li>
                    <li><a href="consulta-vuelo.jsp?categoria=america"><i class="bi bi-tree"></i> América</a></li>
                    <li><a href="consulta-vuelo.jsp?categoria=exclusivos"><i class="bi bi-star"></i> Exclusivos</a></li>
                    <li><a href="consulta-vuelo.jsp?categoria=temporada"><i class="bi bi-sun"></i> Temporada</a></li>
                    <li><a href="consulta-vuelo.jsp?categoria=cortos"><i class="bi bi-clock"></i> Cortos</a></li>
                </ul>
            </div>

            <div class="sidebar-modern fade-in">
                <h5 class="sidebar-title">Destinos Populares</h5>
                <div class="card-modern mb-3">
                    <img src="https://images.unsplash.com/photo-1502602898536-47ad22581b52?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80" class="card-img-top" alt="París" style="height: 120px; object-fit: cover;">
                    <div class="card-body-modern">
                        <h6 class="card-title-modern">París, Francia</h6>
                        <p class="card-text-modern small">La ciudad del amor y la luz.</p>
                        <a href="consulta-vuelo.jsp?destino=paris" class="read-more">Ver vuelos</a>
                    </div>
                </div>
                <div class="card-modern mb-3">
                    <img src="https://images.unsplash.com/photo-1483729558449-99ef09a8c325?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80" class="card-img-top" alt="Río de Janeiro" style="height: 120px; object-fit: cover;">
                    <div class="card-body-modern">
                        <h6 class="card-title-modern">Río de Janeiro, Brasil</h6>
                        <p class="card-text-modern small">La ciudad maravillosa.</p>
                        <a href="consulta-vuelo.jsp?destino=rio" class="read-more">Ver vuelos</a>
                    </div>
                </div>
                <div class="card-modern">
                    <img src="https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80" class="card-img-top" alt="Nueva York" style="height: 120px; object-fit: cover;">
                    <div class="card-body-modern">
                        <h6 class="card-title-modern">Nueva York, USA</h6>
                        <p class="card-text-modern small">La ciudad que nunca duerme.</p>
                        <a href="consulta-vuelo.jsp?destino=ny" class="read-more">Ver vuelos</a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Contenido principal -->
        <div class="col-lg-9">
            <h2 class="section-title fade-in">Rutas de Vuelo Disponibles</h2>

            <!-- Tarjetas de vuelo dinámicas -->
            <% for (DtRutaVuelo ruta : rutasDestacadas) { %>
            <div class="flight-card fade-in">
                <div class="flight-header">
                    <div>
                        <span class="flight-route"><%= ruta.getCiudadOrigen() %> - <%= ruta.getCiudadDestino() %></span>
                        <div><%= ruta.getAerolinea() %> (<%= ruta.getNombre() %>)</div>
                    </div>
                    <span class="badge bg-success">Confirmada</span>
                    <% if (ruta.getCategorias() != null && !ruta.getCategorias().isEmpty()) { %>
                    <span class="badge bg-primary">Categorías</span>
                    <% } %>
                </div>
                <div class="flight-description">
                    <%= ruta.getDescripcion() != null ? ruta.getDescripcion() : "Descripción no disponible" %>
                </div>
                <div class="flight-details">
                    <small>
                        <strong>Hora:</strong> <%= ruta.getHora() != null ? ruta.getHora() : "No disponible" %> |
                        <strong>Precio Turista:</strong> $<%= ruta.getCostoTurista() %> |
                        <strong>Precio Ejecutivo:</strong> $<%= ruta.getCostoEjecutivo() %>
                    </small>
                </div>
                <a href="consulta-vuelo.jsp?nombre=<%= ruta.getNombre() %>" class="read-more">
                    Ver detalles <i class="bi bi-arrow-right"></i>
                </a>
            </div>
            <% } %>

            <!-- Mensaje si no hay rutas -->
            <% if (rutasDestacadas.isEmpty()) { %>
            <div class="flight-card fade-in">
                <div class="text-center py-4">
                    <i class="bi bi-airplane" style="font-size: 3rem; opacity: 0.3;"></i>
                    <p class="text-muted mt-2">No hay rutas de vuelo disponibles en este momento.</p>
                    <a href="consulta-vuelo.jsp" class="btn btn-primary btn-sm">Explorar todas las rutas</a>
                </div>
            </div>
            <% } %>

            <!-- Sección de paquetes -->
            <h3 class="section-title mt-5 fade-in">Paquetes Destacados</h3>

            <% for (DtPaquete paquete : paquetesDestacados) { %>
            <div class="flight-card fade-in">
                <div class="flight-header">
                    <div>
                        <span class="flight-route"><%= paquete.getNombre() %></span>
                    </div>
                    <% if (paquete.getDescuentoPorc() > 0) { %>
                    <span class="badge bg-warning text-dark"><%= paquete.getDescuentoPorc() %>% OFF</span>
                    <% } %>
                    <span class="badge bg-info"><%= paquete.getItems() != null ? paquete.getItems().size() : 0 %> rutas</span>
                </div>
                <div class="flight-description">
                    <%= paquete.getDescripcion() != null ? paquete.getDescripcion() : "Paquete de viaje exclusivo" %>
                    <% if (paquete.getDescuentoPorc() > 0) { %>
                    <br><small class="text-success">¡Ahorra <%= paquete.getDescuentoPorc() %>% con este paquete!</small>
                    <% } %>
                </div>
                <div class="flight-details">
                    <small>
                        <strong>Precio:</strong> $<%= paquete.getCosto() %> |
                        <strong>Vigencia:</strong> <%= paquete.getPeriodoValidezDias() %> días |
                        <strong>Incluye:</strong> <%= paquete.getItems() != null ? paquete.getItems().size() : 0 %> rutas
                    </small>
                </div>
                <a href="consulta-paquete.jsp?nombre=<%= paquete.getNombre() %>" class="read-more">
                    Ver detalles del paquete <i class="bi bi-arrow-right"></i>
                </a>
            </div>
            <% } %>

            <!-- Mensaje si no hay paquetes -->
            <% if (paquetesDestacados.isEmpty()) { %>
            <div class="flight-card fade-in">
                <div class="text-center py-4">
                    <i class="bi bi-gift" style="font-size: 3rem; opacity: 0.3;"></i>
                    <p class="text-muted mt-2">No hay paquetes disponibles en este momento.</p>
                    <a href="consulta-paquete.jsp" class="btn btn-primary btn-sm">Explorar todos los paquetes</a>
                </div>
            </div>
            <% } %>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
</body>
</html>
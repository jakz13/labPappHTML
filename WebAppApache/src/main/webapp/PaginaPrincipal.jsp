<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
            <!-- Sección de Aerolíneas Recomendadas (se carga dinámicamente) -->
            <div class="sidebar-modern fade-in">
                <h5 class="sidebar-title">Aerolíneas Recomendadas</h5>
                <div id="aerolineasRecomendadas" class="aerolineas-list">
                    <div class="text-center py-3">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Cargando aerolíneas...</span>
                        </div>
                        <p class="text-muted mt-2">Cargando aerolíneas...</p>
                    </div>
                </div>

                <!-- Ver todas las aerolíneas -->
                <div class="text-center mt-3">
                    <a href="consulta-usuario.jsp" class="btn btn-outline-primary btn-sm">
                        Ver todas las aerolíneas
                    </a>
                </div>
            </div>

            <!-- Destinos Populares (se mantiene estático) -->
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
            <div id="rutasDestacadas">
                <div class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Cargando rutas...</span>
                    </div>
                    <p class="text-muted mt-2">Cargando rutas de vuelo...</p>
                </div>
            </div>

            <!-- Sección de paquetes -->
            <h3 class="section-title mt-5 fade-in">Paquetes Destacados</h3>

            <div id="paquetesDestacados">
                <div class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Cargando paquetes...</span>
                    </div>
                    <p class="text-muted mt-2">Cargando paquetes...</p>
                </div>
            </div>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script>
    // Cargar datos de la página principal
    document.addEventListener('DOMContentLoaded', function() {
        cargarDatosHomePage();
    });

    function cargarDatosHomePage() {
        fetch('homeData')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error en la respuesta del servidor: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('Datos recibidos:', data); // Para debug
                mostrarAerolineasRecomendadas(data.aerolineasRecomendadas);
                mostrarRutasDestacadas(data.rutasDestacadas);
                mostrarPaquetesDestacados(data.paquetesDestacados);
            })
            .catch(error => {
                console.error('Error cargando datos:', error);
                mostrarDatosPorDefecto();
            });
    }

    function mostrarAerolineasRecomendadas(aerolineas) {
        const container = document.getElementById('aerolineasRecomendadas');

        if (!aerolineas || aerolineas.length === 0) {
            container.innerHTML = '<div class="text-center py-3">' +
                '<i class="bi bi-airplane" style="font-size: 2rem; opacity: 0.3;"></i>' +
                '<p class="text-muted mt-2">No hay aerolíneas disponibles</p>' +
                '</div>';
            return;
        }

        let aerolineasHTML = '';

        aerolineas.forEach(aerolinea => {
            const tieneImagen = aerolinea.imagenUrl && aerolinea.imagenUrl.trim() !== '';
            const descCorta = aerolinea.descripcion && aerolinea.descripcion.length > 30 ?
                aerolinea.descripcion.substring(0, 30) + '...' :
                (aerolinea.descripcion || 'Aerolínea de confianza');

            let imagenHTML = '';
            if (tieneImagen) {
                imagenHTML = '<div class="aerolinea-logo me-3">' +
                    '<img src="' + aerolinea.imagenUrl + '" ' +
                    'alt="' + aerolinea.nombre + '" ' +
                    'class="aerolinea-img" ' +
                    'onerror="this.style.display=\'none\'; this.nextElementSibling.style.display=\'flex\';">' +
                    '<div class="aerolinea-fallback" style="display: none;">' +
                    '<i class="bi bi-airplane fs-4"></i>' +
                    '</div>' +
                    '</div>';
            } else {
                imagenHTML = '<div class="aerolinea-logo me-3">' +
                    '<div class="aerolinea-fallback">' +
                    '<i class="bi bi-airplane fs-4"></i>' +
                    '</div>' +
                    '</div>';
            }

            aerolineasHTML += '<div class="aerolinea-card mb-3">' +
                '<div class="d-flex align-items-center">' +
                imagenHTML +
                '<div class="aerolinea-info flex-grow-1">' +
                '<h6 class="mb-1">' + aerolinea.nombre + '</h6>' +
                '<p class="small text-muted mb-0">' + descCorta + '</p>' +
                '</div>' +
                '</div>' +
                '<a href="consulta-vuelo.jsp?aerolinea=' + aerolinea.nickname + '" class="stretched-link"></a>' +
                '</div>';
        });

        container.innerHTML = aerolineasHTML;
    }

    function mostrarRutasDestacadas(rutas) {
        const container = document.getElementById('rutasDestacadas');

        if (!rutas || rutas.length === 0) {
            container.innerHTML = '<div class="flight-card fade-in">' +
                '<div class="text-center py-4">' +
                '<i class="bi bi-airplane" style="font-size: 3rem; opacity: 0.3;"></i>' +
                '<p class="text-muted mt-2">No hay rutas de vuelo disponibles en este momento.</p>' +
                '<a href="consulta-vuelo.jsp" class="btn btn-primary btn-sm">Explorar todas las rutas</a>' +
                '</div>' +
                '</div>';
            return;
        }

        let rutasHTML = '';

        rutas.forEach(ruta => {
            const categoriasBadge = ruta.tieneCategorias ? '<span class="badge bg-primary"><i class="bi bi-tags me-1"></i>Categorías</span>' : '';

            rutasHTML += '<div class="flight-card fade-in">' +
                '<div class="flight-header">' +
                '<div>' +
                '<span class="flight-route">' + ruta.ciudadOrigen + ' - ' + ruta.ciudadDestino + '</span>' +
                '<div class="text-muted small">' +
                '<i class="bi bi-airplane me-1"></i>' + ruta.aerolinea + ' • ' + ruta.nombre +
                '</div>' +
                '</div>' +
                categoriasBadge +
                '</div>' +
                '<div class="flight-description">' +
                (ruta.descripcion || 'Vuelo directo con todas las comodidades.') +
                '</div>' +
                '<div class="flight-details">' +
                '<div class="row">' +
                '<div class="col-md-4">' +
                '<small><strong><i class="bi bi-clock me-1"></i>Hora:</strong> ' + (ruta.hora || 'Por confirmar') + '</small>' +
                '</div>' +
                '<div class="col-md-4">' +
                '<small><strong><i class="bi bi-currency-dollar me-1"></i>Turista:</strong> $' + ruta.costoTurista + '</small>' +
                '</div>' +
                '<div class="col-md-4">' +
                '<small><strong><i class="bi bi-star me-1"></i>Ejecutivo:</strong> $' + ruta.costoEjecutivo + '</small>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '<a href="consulta-vuelo.jsp?nombre=' + ruta.nombre + '" class="read-more">' +
                'Ver detalles y reservar <i class="bi bi-arrow-right"></i>' +
                '</a>' +
                '</div>';
        });

        container.innerHTML = rutasHTML;
    }

    function mostrarPaquetesDestacados(paquetes) {
        const container = document.getElementById('paquetesDestacados');

        if (!paquetes || paquetes.length === 0) {
            container.innerHTML = '<div class="flight-card fade-in">' +
                '<div class="text-center py-4">' +
                '<i class="bi bi-gift" style="font-size: 3rem; opacity: 0.3;"></i>' +
                '<p class="text-muted mt-2">No hay paquetes disponibles en este momento.</p>' +
                '<a href="consulta-paquete.jsp" class="btn btn-primary btn-sm">Explorar todos los paquetes</a>' +
                '</div>' +
                '</div>';
            return;
        }

        let paquetesHTML = '';

        paquetes.forEach(paquete => {
            const ahorro = paquete.descuentoPorc > 0 ?
                Math.round(paquete.costo * paquete.descuentoPorc / 100) : 0;

            let descuentoBadge = '';
            if (paquete.descuentoPorc > 0) {
                descuentoBadge = '<span class="badge bg-warning text-dark me-1">' +
                    '<i class="bi bi-percent me-1"></i>' + paquete.descuentoPorc + '% OFF' +
                    '</span>';
            }

            let ahorroHTML = '';
            if (paquete.descuentoPorc > 0) {
                ahorroHTML = '<div class="mt-2">' +
                    '<span class="badge bg-success">' +
                    '<i class="bi bi-piggy-bank me-1"></i>¡Ahorra $' + ahorro + '!' +
                    '</span>' +
                    '</div>';
            }

            paquetesHTML += '<div class="flight-card fade-in">' +
                '<div class="flight-header">' +
                '<div>' +
                '<span class="flight-route">' + paquete.nombre + '</span>' +
                '<div class="text-muted small">' +
                '<i class="bi bi-gift me-1"></i>Paquete exclusivo' +
                '</div>' +
                '</div>' +
                '<div class="badge-group">' +
                descuentoBadge +
                '<span class="badge bg-info">' +
                '<i class="bi bi-map me-1"></i>' + paquete.cantidadItems + ' rutas' +
                '</span>' +
                '</div>' +
                '</div>' +
                '<div class="flight-description">' +
                (paquete.descripcion || 'Paquete de viaje exclusivo con múltiples destinos.') +
                ahorroHTML +
                '</div>' +
                '<div class="flight-details">' +
                '<div class="row">' +
                '<div class="col-md-4">' +
                '<small><strong><i class="bi bi-currency-dollar me-1"></i>Precio:</strong> $' + paquete.costo + '</small>' +
                '</div>' +
                '<div class="col-md-4">' +
                '<small><strong><i class="bi bi-calendar me-1"></i>Vigencia:</strong> ' + paquete.periodoValidezDias + ' días</small>' +
                '</div>' +
                '<div class="col-md-4">' +
                '<small><strong><i class="bi bi-geo-alt me-1"></i>Rutas:</strong> ' + paquete.cantidadItems + '</small>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '<a href="consulta-paquete.jsp?nombre=' + paquete.nombre + '" class="read-more">' +
                'Ver detalles del paquete <i class="bi bi-arrow-right"></i>' +
                '</a>' +
                '</div>';
        });

        container.innerHTML = paquetesHTML;
    }

    function mostrarDatosPorDefecto() {
        // Mostrar mensajes de error
        document.getElementById('aerolineasRecomendadas').innerHTML =
            '<div class="text-center py-3">' +
            '<i class="bi bi-exclamation-triangle text-warning" style="font-size: 2rem;"></i>' +
            '<p class="text-muted mt-2">Error al cargar aerolíneas</p>' +
            '</div>';

        document.getElementById('rutasDestacadas').innerHTML =
            '<div class="flight-card fade-in">' +
            '<div class="text-center py-4">' +
            '<i class="bi bi-exclamation-triangle text-warning" style="font-size: 3rem;"></i>' +
            '<p class="text-muted mt-2">Error al cargar rutas</p>' +
            '<a href="consulta-vuelo.jsp" class="btn btn-primary btn-sm">Explorar todas las rutas</a>' +
            '</div>' +
            '</div>';

        document.getElementById('paquetesDestacados').innerHTML =
            '<div class="flight-card fade-in">' +
            '<div class="text-center py-4">' +
            '<i class="bi bi-exclamation-triangle text-warning" style="font-size: 3rem;"></i>' +
            '<p class="text-muted mt-2">Error al cargar paquetes</p>' +
            '<a href="consulta-paquete.jsp" class="btn btn-primary btn-sm">Explorar todos los paquetes</a>' +
            '</div>' +
            '</div>';
    }
</script>
</body>
</html>
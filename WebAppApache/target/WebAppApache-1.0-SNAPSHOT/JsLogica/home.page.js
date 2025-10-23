// JsLogica/home-page.js
// Lógica para la página principal - Carga y muestra datos dinámicos

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
            console.log('Datos recibidos:', data);
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

    // Filtrar paquetes que tienen costo mayor a 0
    const paquetesFiltrados = paquetes ? paquetes.filter(paquete => paquete.costo > 0) : [];

    if (!paquetesFiltrados || paquetesFiltrados.length === 0) {
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

    paquetesFiltrados.forEach(paquete => {
        const precioOriginal = paquete.costo;
        const precioConDescuento = paquete.descuentoPorc > 0 ?
            Math.round(precioOriginal * (1 - paquete.descuentoPorc / 100)) : precioOriginal;

        let descuentoBadge = '';
        if (paquete.descuentoPorc > 0) {
            descuentoBadge = '<span class="badge bg-warning text-dark me-1">' +
                '<i class="bi bi-percent me-1"></i>' + paquete.descuentoPorc + '% OFF' +
                '</span>';
        }

        let precioHTML = '';
        if (paquete.descuentoPorc > 0) {
            precioHTML = '<div class="mt-2">' +
                '<div class="d-flex align-items-center gap-2">' +
                '<span class="text-muted text-decoration-line-through small">$' + precioOriginal + '</span>' +
                '<span class="h6 mb-0 text-success">$' + precioConDescuento + '</span>' +
                '</div>' +
                '</div>';
        } else {
            precioHTML = '<div class="mt-2">' +
                '<span class="h6 mb-0">$' + precioOriginal + '</span>' +
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
            precioHTML +
            '</div>' +
            '<div class="flight-details">' +
            '<div class="row">' +
            '<div class="col-md-4">' +
            '<small><strong><i class="bi bi-currency-dollar me-1"></i>Precio final:</strong> $' + precioConDescuento + '</small>' +
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
// Reemplazar tu array estático por una función que obtenga datos del servidor
let rutasDisponibles = [];

// Cargar rutas al iniciar la página
document.addEventListener('DOMContentLoaded', function() {
    cargarRutasDelServidor();
});

function cargarRutasDelServidor(filtros = {}) {
    const container = document.getElementById('listaRutas');
    container.innerHTML = '<div class="col-12 text-center py-4"><div class="spinner-border text-primary" role="status"></div><p class="mt-2">Cargando rutas...</p></div>';

    // Construir URL con parámetros de filtro
    let url = '/tu-app/consultarRutas';
    const params = new URLSearchParams();

    if (filtros.aerolinea) params.append('aerolinea', filtros.aerolinea);
    if (filtros.categoria) params.append('categoria', filtros.categoria);

    if (params.toString()) {
        url += '?' + params.toString();
    }

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor');
            }
            return response.json();
        })
        .then(rutas => {
            rutasDisponibles = rutas;
            mostrarRutas(rutas);
        })
        .catch(error => {
            console.error('Error:', error);
            container.innerHTML = `
                <div class="col-12 text-center py-4">
                    <p class="text-danger">Error al cargar las rutas: ${error.message}</p>
                    <button class="btn btn-outline-primary" onclick="cargarRutasDelServidor()">Reintentar</button>
                </div>
            `;
        });
}

function mostrarRutas(rutas) {
    const container = document.getElementById('listaRutas');
    container.innerHTML = '';

    if (rutas.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center py-4">
                <p class="text-muted">No se encontraron rutas con los filtros seleccionados.</p>
                <button class="btn btn-outline-primary" onclick="limpiarFiltros()">Mostrar todas las rutas</button>
            </div>
        `;
        return;
    }

    rutas.forEach(ruta => {
        const rutaHTML = `
            <div class="col-md-6">
                <div class="card ruta-card h-100" onclick="mostrarDetallesRuta('${ruta.id}')">
                    <img src="${ruta.imagen || 'https://via.placeholder.com/600x400/6c757d/ffffff?text=Sin+Imagen'}" 
                         class="ruta-image" alt="${ruta.nombre}">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <h6 class="card-title">${ruta.nombre}</h6>
                            <span class="info-badge">${ruta.id}</span>
                        </div>
                        <p class="card-text small text-muted">${ruta.descripcionCorta}</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="text-primary fw-bold">${ruta.aerolinea}</span>
                            <span class="costo-destacado">$${ruta.costoTurista}</span>
                        </div>
                        <div class="mt-2">
                            ${ruta.categorias.map(cat => `<span class="badge bg-secondary me-1">${cat}</span>`).join('')}
                        </div>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += rutaHTML;
    });
}

function mostrarDetallesRuta(rutaId) {
    const ruta = rutasDisponibles.find(r => r.id === rutaId);

    if (!ruta) return;

    // Actualizar información de la ruta (igual que antes)
    document.getElementById('rutaNombre').textContent = ruta.nombre;
    document.getElementById('rutaDescripcionCorta').textContent = ruta.descripcionCorta;
    document.getElementById('rutaDescripcion').textContent = ruta.descripcionCompleta;
    document.getElementById('rutaAerolinea').textContent = ruta.aerolinea;
    document.getElementById('rutaOrigen').textContent = ruta.origen;
    document.getElementById('rutaDestino').textContent = ruta.destino;
    document.getElementById('rutaHora').textContent = ruta.hora;
    document.getElementById('rutaEstado').textContent = ruta.estado;
    document.getElementById('rutaFechaAlta').textContent = ruta.fechaAlta;
    document.getElementById('rutaCategorias').textContent = ruta.categorias.join(', ');
    document.getElementById('costoTurista').textContent = ruta.costoTurista;
    document.getElementById('costoEjecutivo').textContent = ruta.costoEjecutivo;
    document.getElementById('costoEquipaje').textContent = ruta.costoEquipaje;
    document.getElementById('imagenRuta').src = ruta.imagen || 'https://via.placeholder.com/600x400/6c757d/ffffff?text=Sin+Imagen';

    // Cargar vuelos asociados
    const vuelosContainer = document.getElementById('vuelosAsociados');
    vuelosContainer.innerHTML = '';

    if (ruta.vuelos && ruta.vuelos.length > 0) {
        ruta.vuelos.forEach(vuelo => {
            const vueloHTML = `
                <div class="col-md-4">
                    <div class="card ${vuelo.disponible ? 'border-success' : 'border-secondary'}">
                        <div class="card-body text-center">
                            <h6 class="card-title">${vuelo.id}</h6>
                            <p class="mb-1">${vuelo.fecha}</p>
                            <span class="badge ${vuelo.disponible ? 'bg-success' : 'bg-secondary'}">
                                ${vuelo.disponible ? 'Disponible' : 'Completo'}
                            </span>
                        </div>
                    </div>
                </div>
            `;
            vuelosContainer.innerHTML += vueloHTML;
        });
    } else {
        vuelosContainer.innerHTML = '<div class="col-12 text-center"><p class="text-muted">No hay vuelos disponibles para esta ruta</p></div>';
    }

    // Mostrar sección de información
    document.getElementById('infoRuta').style.display = 'block';

    // Remover selección anterior y marcar actual
    document.querySelectorAll('.ruta-card').forEach(card => {
        card.classList.remove('selected');
    });
    event.currentTarget.classList.add('selected');

    // Scroll a la información de la ruta
    document.getElementById('infoRuta').scrollIntoView({ behavior: 'smooth' });
}

function filtrarRutas() {
    const aerolineaFiltro = document.getElementById('aerolinea').value;
    const categoriaFiltro = document.getElementById('categoria').value;

    const filtros = {};
    if (aerolineaFiltro) filtros.aerolinea = aerolineaFiltro;
    if (categoriaFiltro) filtros.categoria = categoriaFiltro;

    cargarRutasDelServidor(filtros);
    document.getElementById('infoRuta').style.display = 'none';
}

function limpiarFiltros() {
    document.getElementById('aerolinea').value = '';
    document.getElementById('categoria').value = '';
    cargarRutasDelServidor();
    document.getElementById('infoRuta').style.display = 'none';
}
// Datos de ejemplo de rutas
const rutasDisponibles = [
    {
        id: "ZL1502",
        nombre: "Montevideo - Rio de Janeiro",
        descripcionCorta: "Vuelo directo a las playas de Brasil",
        descripcionCompleta: "Disfrute de un vuelo directo desde Montevideo hasta la maravillosa ciudad de Rio de Janeiro. Con una duración de apenas 2 horas y 30 minutos, llegará listo para explorar las famosas playas de Copacabana e Ipanema, el Cristo Redentor y el Pan de Azúcar. Incluye servicio de comidas y bebidas a bordo.",
        aerolinea: "ZulyFly",
        origen: "Montevideo, Uruguay (MVD)",
        destino: "Rio de Janeiro, Brasil (GIG)",
        hora: "07:15",
        costoTurista: 320,
        costoEjecutivo: 550,
        costoEquipaje: 25,
        categorias: ["Internacionales", "América", "Cortos"],
        estado: "Confirmada",
        fechaAlta: "15/01/2024",
        imagen: "https://via.placeholder.com/600x400/3498db/ffffff?text=Montevideo-Rio+de+Janeiro",
        vuelos: [
            { id: "ZL1502001", fecha: "25/10/2024", disponible: true },
            { id: "ZL1502002", fecha: "26/10/2024", disponible: true },
            { id: "ZL1502003", fecha: "27/10/2024", disponible: false }
        ]
    },
    {
        id: "IB6012",
        nombre: "Montevideo - Madrid",
        descripcionCorta: "Conexión directa con Europa",
        descripcionCompleta: "Vuele directamente desde Montevideo hasta Madrid en nuestro servicio premium. Disfrute de entretenimiento a bordo en todos los asientos, comidas gourmet y servicio de primera clase. Perfecto para viajes de negocios o placer.",
        aerolinea: "Iberia",
        origen: "Montevideo, Uruguay (MVD)",
        destino: "Madrid, España (MAD)",
        hora: "22:00",
        costoTurista: 750,
        costoEjecutivo: 1200,
        costoEquipaje: 30,
        categorias: ["Internacionales", "Europa"],
        estado: "Confirmada",
        fechaAlta: "10/02/2024",
        imagen: "https://via.placeholder.com/600x400/8e44ad/ffffff?text=Montevideo-Madrid",
        vuelos: [
            { id: "IB6012201", fecha: "28/10/2024", disponible: true },
            { id: "IB6012202", fecha: "29/10/2024", disponible: true }
        ]
    },
    {
        id: "CN804",
        nombre: "Ciudad de Panamá - Nueva York",
        descripcionCorta: "De Centroamérica a la gran manzana",
        descripcionCompleta: "Conecte desde Ciudad de Panamá hasta Nueva York en este vuelo internacional. Copa Airlines ofrece un servicio excepcional con comidas, bebidas y entretenimiento a bordo para hacer su viaje más placentero.",
        aerolinea: "Copa Airlines",
        origen: "Ciudad de Panamá, Panamá (PTY)",
        destino: "Nueva York, USA (JFK)",
        hora: "08:30",
        costoTurista: 520,
        costoEjecutivo: 850,
        costoEquipaje: 35,
        categorias: ["Internacionales", "América"],
        estado: "Confirmada",
        fechaAlta: "05/03/2024",
        imagen: "https://via.placeholder.com/600x400/e74c3c/ffffff?text=Panamá-Nueva+York",
        vuelos: [
            { id: "CN804101", fecha: "30/10/2024", disponible: true }
        ]
    },
    {
        id: "AR2050",
        nombre: "Buenos Aires - Santiago",
        descripcionCorta: "Cruzando la cordillera de los Andes",
        descripcionCompleta: "Experimente la majestuosidad de los Andes en este vuelo entre Buenos Aires y Santiago. Disfrute de vistas panorámicas de la cordillera mientras cruza hacia Chile.",
        aerolinea: "Aerolíneas Argentinas",
        origen: "Buenos Aires, Argentina (EZE)",
        destino: "Santiago, Chile (SCL)",
        hora: "14:30",
        costoTurista: 280,
        costoEjecutivo: 480,
        costoEquipaje: 30,
        categorias: ["Internacionales", "América", "Cortos"],
        estado: "Confirmada",
        fechaAlta: "20/01/2024",
        imagen: "https://via.placeholder.com/600x400/2ecc71/ffffff?text=Bs+As-Santiago",
        vuelos: [
            { id: "AR205001", fecha: "01/11/2024", disponible: true },
            { id: "AR205002", fecha: "02/11/2024", disponible: true }
        ]
    }
];

// Cargar rutas al iniciar la página
document.addEventListener('DOMContentLoaded', function() {
    cargarRutas(rutasDisponibles);
});

function cargarRutas(rutas) {
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
                    <img src="${ruta.imagen}" class="ruta-image" alt="${ruta.nombre}">
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

    // Actualizar información de la ruta
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
    document.getElementById('imagenRuta').src = ruta.imagen;

    // Cargar vuelos asociados
    const vuelosContainer = document.getElementById('vuelosAsociados');
    vuelosContainer.innerHTML = '';

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
    const estadoFiltro = document.getElementById('estado').value;

    let rutasFiltradas = rutasDisponibles.filter(ruta => {
        const coincideAerolinea = !aerolineaFiltro ||
            ruta.aerolinea.toLowerCase().includes(aerolineaFiltro.toLowerCase());
        const coincideCategoria = !categoriaFiltro ||
            ruta.categorias.some(cat => cat.toLowerCase().includes(categoriaFiltro.toLowerCase()));
        const coincideEstado = estadoFiltro === 'todas' || ruta.estado === 'Confirmada';

        return coincideAerolinea && coincideCategoria && coincideEstado;
    });

    cargarRutas(rutasFiltradas);
    document.getElementById('infoRuta').style.display = 'none';
}

function limpiarFiltros() {
    document.getElementById('aerolinea').value = '';
    document.getElementById('categoria').value = '';
    document.getElementById('estado').value = 'confirmada';
    cargarRutas(rutasDisponibles);
    document.getElementById('infoRuta').style.display = 'none';
}

// Mejorar la experiencia de usuario
document.addEventListener('DOMContentLoaded', function() {
    // Agregar tooltips si es necesario
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});
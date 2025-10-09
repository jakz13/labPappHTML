// Datos de ejemplo - Paquetes disponibles
const paquetesDisponibles = [
    {
        id: 1,
        nombre: "Sudamérica Básico",
        costo: 500,
        vigencia: 180, // días
        rutas: [
            { id: "R001", nombre: "Montevideo - Buenos Aires", aerolinea: "ZulyFly" },
            { id: "R002", nombre: "Buenos Aires - Santiago", aerolinea: "Aerolíneas Argentinas" }
        ],
        descripcion: "Ideal para viajes regionales dentro de Sudamérica"
    },
    {
        id: 2,
        nombre: "Europa Explorer",
        costo: 1200,
        vigencia: 365,
        rutas: [
            { id: "R101", nombre: "Madrid - París", aerolinea: "Iberia" },
            { id: "R102", nombre: "París - Roma", aerolinea: "Air France" },
            { id: "R103", nombre: "Roma - Barcelona", aerolinea: "Alitalia" }
        ],
        descripcion: "Descubre las principales capitales europeas"
    },
    {
        id: 3,
        nombre: "Caribe Paradise",
        costo: 800,
        vigencia: 90,
        rutas: [
            { id: "R201", nombre: "Miami - Cancún", aerolinea: "American Airlines" },
            { id: "R202", nombre: "Cancún - Punta Cana", aerolinea: "Copa Airlines" }
        ],
        descripcion: "Escape tropical a los mejores destinos del Caribe"
    },
    {
        id: 4,
        nombre: "Norteamérica Premium",
        costo: 1500,
        vigencia: 240,
        rutas: [
            { id: "R301", nombre: "Nueva York - Los Ángeles", aerolinea: "United Airlines" },
            { id: "R302", nombre: "Los Ángeles - Vancouver", aerolinea: "American Airlines" },
            { id: "R303", nombre: "Vancouver - Toronto", aerolinea: "Air Canada" }
        ],
        descripcion: "Recorre las principales ciudades de Norteamérica"
    }
];

// Paquetes ya comprados por el cliente (inicialmente vacío)
let paquetesComprados = [];

// Elementos del DOM
let listaPaquetes, infoPaquete, mensajeCompra, listaPaquetesComprados, contadorPaquetes, saldoCliente;

// Variables globales
let paqueteSeleccionado = null;

// Inicializar la página
function inicializarPagina() {
    // Obtener elementos del DOM
    listaPaquetes = document.getElementById('listaPaquetes');
    infoPaquete = document.getElementById('infoPaquete');
    mensajeCompra = document.getElementById('mensajeCompra');
    listaPaquetesComprados = document.getElementById('listaPaquetesComprados');
    contadorPaquetes = document.getElementById('contadorPaquetes');
    saldoCliente = document.getElementById('saldoCliente');

    // Configurar event listeners
    document.getElementById('btnConfirmarCompra').addEventListener('click', confirmarCompra);
    document.getElementById('btnCancelarSeleccion').addEventListener('click', cancelarSeleccion);
    document.getElementById('btnModalConfirmar').addEventListener('click', realizarCompraDesdeModal);

    // Cargar datos iniciales
    cargarPaquetesDisponibles();
    actualizarPaquetesComprados();
    actualizarContadores();
}

// Cargar paquetes disponibles en la lista
function cargarPaquetesDisponibles() {
    listaPaquetes.innerHTML = '';

    paquetesDisponibles.forEach(paquete => {
        const yaComprado = paquetesComprados.some(p => p.id === paquete.id && estaVigente(p));

        const paqueteHTML = `
            <div class="col-md-6">
                <div class="card paquete-card ${yaComprado ? 'opacity-50' : ''}"
                     onclick="${yaComprado ? '' : `seleccionarPaquete(${paquete.id})`}">
                    <div class="card-body">
                        <h6 class="card-title">${paquete.nombre}</h6>
                        <p class="card-text small text-muted">${paquete.descripcion}</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="h5 text-primary">$${paquete.costo}</span>
                            <span class="badge bg-info">Vigencia: ${paquete.vigencia} días</span>
                        </div>
                        <div class="mt-2">
                            <small class="text-muted">Incluye ${paquete.rutas.length} rutas</small>
                        </div>
                        ${yaComprado ? '<div class="text-center mt-2"><span class="badge bg-warning">Ya comprado</span></div>' : ''}
                    </div>
                </div>
            </div>
        `;

        listaPaquetes.innerHTML += paqueteHTML;
    });
}

// Seleccionar un paquete
function seleccionarPaquete(id) {
    paqueteSeleccionado = paquetesDisponibles.find(p => p.id === id);

    // Actualizar información del paquete seleccionado
    document.getElementById('nombrePaqueteSeleccionado').textContent = paqueteSeleccionado.nombre;
    document.getElementById('costoPaqueteSeleccionado').textContent = paqueteSeleccionado.costo;
    document.getElementById('vigenciaPaquete').textContent = paqueteSeleccionado.vigencia;

    const fechaCompra = new Date();
    const fechaVencimiento = new Date();
    fechaVencimiento.setDate(fechaVencimiento.getDate() + paqueteSeleccionado.vigencia);

    document.getElementById('fechaCompra').textContent = fechaCompra.toLocaleDateString();
    document.getElementById('fechaVencimiento').textContent = fechaVencimiento.toLocaleDateString();

    // Mostrar rutas incluidas
    const rutasContainer = document.getElementById('rutasPaqueteSeleccionado');
    rutasContainer.innerHTML = '';
    paqueteSeleccionado.rutas.forEach(ruta => {
        const rutaHTML = `
            <div class="ruta-item">
                <strong>${ruta.nombre}</strong><br>
                <small class="text-muted">${ruta.aerolinea}</small>
            </div>
        `;
        rutasContainer.innerHTML += rutaHTML;
    });

    // Mostrar sección de información
    infoPaquete.classList.remove('d-none');
    mensajeCompra.innerHTML = '';

    // Remover selección anterior y marcar actual
    document.querySelectorAll('.paquete-card').forEach(card => {
        card.classList.remove('selected');
    });
    event.currentTarget.classList.add('selected');
}

// Confirmar compra
function confirmarCompra() {
    if (!paqueteSeleccionado) {
        mostrarMensaje('Por favor seleccione un paquete primero.', 'warning');
        return;
    }

    // Verificar saldo suficiente
    const saldo = parseInt(saldoCliente.textContent.replace(',', ''));
    if (saldo < paqueteSeleccionado.costo) {
        mostrarMensaje('Saldo insuficiente para realizar esta compra.', 'danger');
        return;
    }

    // Verificar si ya está comprado
    const yaComprado = paquetesComprados.some(p => p.id === paqueteSeleccionado.id && estaVigente(p));
    if (yaComprado) {
        mostrarMensaje('Ya tienes este paquete vigente en tu cuenta.', 'warning');
        return;
    }

    // Mostrar modal de confirmación
    document.getElementById('modalNombrePaquete').textContent = paqueteSeleccionado.nombre;
    document.getElementById('modalCostoPaquete').textContent = paqueteSeleccionado.costo;
    document.getElementById('modalVigencia').textContent = paqueteSeleccionado.vigencia;

    const confirmacionModal = new bootstrap.Modal(document.getElementById('confirmacionModal'));
    confirmacionModal.show();
}

// Realizar compra desde el modal
function realizarCompraDesdeModal() {
    const fechaCompra = new Date();
    const fechaVencimiento = new Date();
    fechaVencimiento.setDate(fechaVencimiento.getDate() + paqueteSeleccionado.vigencia);

    // Agregar a paquetes comprados
    const compra = {
        ...paqueteSeleccionado,
        fechaCompra: fechaCompra.toISOString(),
        fechaVencimiento: fechaVencimiento.toISOString()
    };

    paquetesComprados.push(compra);

    // Actualizar saldo
    const nuevoSaldo = parseInt(saldoCliente.textContent.replace(',', '')) - paqueteSeleccionado.costo;
    saldoCliente.textContent = nuevoSaldo.toLocaleString();

    // Mostrar mensaje de éxito
    mostrarMensaje(`¡Compra realizada con éxito! El paquete "${paqueteSeleccionado.nombre}" ha sido agregado a tu cuenta. Vence el ${fechaVencimiento.toLocaleDateString()}.`, 'success');

    // Actualizar interfaz
    actualizarPaquetesComprados();
    actualizarContadores();
    cargarPaquetesDisponibles();

    // Limpiar selección
    cancelarSeleccion();

    // Cerrar modal
    const modal = bootstrap.Modal.getInstance(document.getElementById('confirmacionModal'));
    modal.hide();
}

// Cancelar selección
function cancelarSeleccion() {
    paqueteSeleccionado = null;
    infoPaquete.classList.add('d-none');
    document.querySelectorAll('.paquete-card').forEach(card => {
        card.classList.remove('selected');
    });
    mensajeCompra.innerHTML = '';
}

// Actualizar lista de paquetes comprados
function actualizarPaquetesComprados() {
    listaPaquetesComprados.innerHTML = '';

    if (paquetesComprados.length === 0) {
        listaPaquetesComprados.innerHTML = '<div class="col-12"><p class="text-muted text-center">No has comprado ningún paquete aún.</p></div>';
        return;
    }

    paquetesComprados.forEach(paquete => {
        const vigente = estaVigente(paquete);
        const paqueteHTML = `
            <div class="col-md-6">
                <div class="card ${vigente ? 'border-success' : 'border-secondary'}">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start">
                            <h6 class="card-title">${paquete.nombre}</h6>
                            <span class="badge ${vigente ? 'badge-vigente' : 'badge-vencido'}">${vigente ? 'Vigente' : 'Vencido'}</span>
                        </div>
                        <p class="small text-muted mb-2">Comprado: ${new Date(paquete.fechaCompra).toLocaleDateString()}</p>
                        <p class="small text-muted mb-2">Vence: ${new Date(paquete.fechaVencimiento).toLocaleDateString()}</p>
                        <div class="mt-2">
                            <small><strong>Rutas:</strong> ${paquete.rutas.length} incluidas</small>
                        </div>
                        ${vigente ? '<div class="mt-2"><button class="btn btn-outline-primary btn-sm" onclick="utilizarPaquete(' + paquete.id + ')">Utilizar Paquete</button></div>' : ''}
                    </div>
                </div>
            </div>
        `;
        listaPaquetesComprados.innerHTML += paqueteHTML;
    });
}

// Verificar si un paquete está vigente
function estaVigente(paquete) {
    return new Date(paquete.fechaVencimiento) > new Date();
}

// Actualizar contadores
function actualizarContadores() {
    const paquetesVigentes = paquetesComprados.filter(estaVigente).length;
    contadorPaquetes.textContent = `${paquetesVigentes}/${paquetesComprados.length}`;
}

// Mostrar mensajes
function mostrarMensaje(mensaje, tipo) {
    mensajeCompra.innerHTML = `<div class="alert alert-${tipo}">${mensaje}</div>`;
}

// Utilizar paquete (función de ejemplo)
function utilizarPaquete(id) {
    const paquete = paquetesComprados.find(p => p.id === id);
    if (paquete && estaVigente(paquete)) {
        mostrarMensaje(`Redirigiendo a selección de vuelos para el paquete "${paquete.nombre}". Podrás utilizar las ${paquete.rutas.length} rutas incluidas.`, 'info');
        // En una implementación real, aquí se redirigiría a la página de reservas
        // window.location.href = `reserva-vuelo.html?paquete=${id}`;
    } else {
        mostrarMensaje('Este paquete no está disponible para usar.', 'warning');
    }
}

// Inicializar la página cuando se carga
document.addEventListener('DOMContentLoaded', inicializarPagina);
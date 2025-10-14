// Validación del formulario y funcionalidad
document.addEventListener('DOMContentLoaded', function() {
    // Inicializar funcionalidad
    inicializarConsultaReserva();
});

// Datos de ejemplo
const datos = {
    aerolineas: {
        'zulyfly': {
            nombre: 'ZulyFly Airlines',
            rutas: {
                'ZL1502': {
                    nombre: 'Montevideo - Rio de Janeiro',
                    vuelos: {
                        'ZL1502001': {
                            fecha: '25/10/2024',
                            reservas: [
                                {
                                    id: 'RES-2024-ZL001',
                                    cliente: 'María González',
                                    pasajeros: ['María González', 'Carlos Rodríguez'],
                                    tipoAsiento: 'Ejecutivo',
                                    cantidadPasajes: 2,
                                    equipajeExtra: 1,
                                    costoTotal: 1100,
                                    fechaReserva: '15/10/2024',
                                    estado: 'Confirmada'
                                }
                            ]
                        }
                    }
                }
            }
        },
        'iberia': {
            nombre: 'Iberia',
            rutas: {
                'IB6012': {
                    nombre: 'Montevideo - Madrid',
                    vuelos: {
                        'IB6012201': {
                            fecha: '28/10/2024',
                            reservas: [
                                {
                                    id: 'RES-2024-IB001',
                                    cliente: 'María González',
                                    pasajeros: ['María González'],
                                    tipoAsiento: 'Turista',
                                    cantidadPasajes: 1,
                                    equipajeExtra: 0,
                                    costoTotal: 750,
                                    fechaReserva: '10/10/2024',
                                    estado: 'Confirmada'
                                }
                            ]
                        }
                    }
                }
            }
        },
        'copa': {
            nombre: 'Copa Airlines',
            rutas: {
                'CN804': {
                    nombre: 'Ciudad de Panamá - Nueva York',
                    vuelos: {
                        'CN804101': {
                            fecha: '30/10/2024',
                            reservas: [
                                {
                                    id: 'RES-2024-CN001',
                                    cliente: 'María González',
                                    pasajeros: ['María González', 'Ana López', 'Pedro Martínez'],
                                    tipoAsiento: 'Turista',
                                    cantidadPasajes: 3,
                                    equipajeExtra: 2,
                                    costoTotal: 1200,
                                    fechaReserva: '12/10/2024',
                                    estado: 'Confirmada'
                                }
                            ]
                        }
                    }
                }
            }
        },
        'american': {
            nombre: 'American Airlines',
            rutas: {
                'AA904': {
                    nombre: 'Miami - Cancún',
                    vuelos: {
                        'AA904301': {
                            fecha: '01/11/2024',
                            reservas: [
                                {
                                    id: 'RES-2024-AA001',
                                    cliente: 'María González',
                                    pasajeros: ['María González'],
                                    tipoAsiento: 'Ejecutivo',
                                    cantidadPasajes: 1,
                                    equipajeExtra: 1,
                                    costoTotal: 650,
                                    fechaReserva: '18/10/2024',
                                    estado: 'Pendiente'
                                }
                            ]
                        }
                    }
                }
            }
        }
    },
    aerolineaActual: 'zulyfly' // Para cuando el usuario es aerolínea
};

// Variables globales
let tipoUsuario = 'cliente'; // 'cliente' o 'aerolinea'
let reservaSeleccionada = null;

function inicializarConsultaReserva() {
    cargarDatosIniciales();
    configurarEventListeners();
}

function cargarDatosIniciales() {
    // Cargar rutas para aerolínea
    cargarRutasAerolinea();
}

function configurarEventListeners() {
    // Cliente
    document.getElementById('aerolineaCliente').addEventListener('change', function() {
        const aerolinea = this.value;
        cargarRutasCliente(aerolinea);
        document.getElementById('btnCliente1').disabled = !aerolinea;
    });

    document.getElementById('rutaCliente').addEventListener('change', function() {
        const aerolinea = document.getElementById('aerolineaCliente').value;
        const ruta = this.value;
        cargarVuelosCliente(aerolinea, ruta);
        document.getElementById('btnCliente2').disabled = !ruta;
    });

    document.getElementById('vueloCliente').addEventListener('change', function() {
        document.getElementById('btnCliente3').disabled = !this.value;
    });

    // Aerolínea
    document.getElementById('rutaAerolinea').addEventListener('change', function() {
        const ruta = this.value;
        cargarVuelosAerolinea(ruta);
        document.getElementById('btnAerolinea1').disabled = !ruta;
    });

    document.getElementById('vueloAerolinea').addEventListener('change', function() {
        const ruta = document.getElementById('rutaAerolinea').value;
        const vuelo = this.value;
        cargarReservasAerolinea(ruta, vuelo);
        document.getElementById('btnAerolinea2').disabled = !vuelo;
    });
}

// Funciones para Cliente
function siguientePasoCliente(paso) {
    if (paso === 4) {
        const aerolinea = document.getElementById('aerolineaCliente').value;
        const ruta = document.getElementById('rutaCliente').value;
        const vuelo = document.getElementById('vueloCliente').value;
        mostrarReservaCliente(aerolinea, ruta, vuelo);
    }

    actualizarPasos('cliente', paso);
}

function cargarRutasCliente(aerolinea) {
    const select = document.getElementById('rutaCliente');
    select.innerHTML = '<option value="">Seleccione una ruta...</option>';

    if (aerolinea && datos.aerolineas[aerolinea]) {
        Object.keys(datos.aerolineas[aerolinea].rutas).forEach(rutaId => {
            const ruta = datos.aerolineas[aerolinea].rutas[rutaId];
            const option = document.createElement('option');
            option.value = rutaId;
            option.textContent = `${rutaId} - ${ruta.nombre}`;
            select.appendChild(option);
        });
    }
}

function cargarVuelosCliente(aerolinea, ruta) {
    const select = document.getElementById('vueloCliente');
    select.innerHTML = '<option value="">Seleccione un vuelo...</option>';

    if (aerolinea && ruta && datos.aerolineas[aerolinea].rutas[ruta]) {
        Object.keys(datos.aerolineas[aerolinea].rutas[ruta].vuelos).forEach(vueloId => {
            const vuelo = datos.aerolineas[aerolinea].rutas[ruta].vuelos[vueloId];
            const option = document.createElement('option');
            option.value = vueloId;
            option.textContent = `${vueloId} - ${vuelo.fecha}`;
            select.appendChild(option);
        });
    }
}

function mostrarReservaCliente(aerolinea, ruta, vuelo) {
    const container = document.getElementById('reservaClienteDetalle');

    if (aerolinea && ruta && vuelo && datos.aerolineas[aerolinea].rutas[ruta].vuelos[vuelo]) {
        const vueloData = datos.aerolineas[aerolinea].rutas[ruta].vuelos[vuelo];
        const reserva = vueloData.reservas.find(r => r.cliente === 'María González');

        if (reserva) {
            const estadoBadge = reserva.estado === 'Confirmada' ? 'bg-success' : 'bg-warning';

            container.innerHTML = `
                <div class="card border-success">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h5 class="text-success">Reserva ${reserva.estado}</h5>
                                <p class="mb-1 text-light"><strong>Código:</strong> ${reserva.id}</p>
                                <p class="mb-1 text-light"><strong>Vuelo:</strong> ${vuelo} - ${datos.aerolineas[aerolinea].rutas[ruta].nombre}</p>
                                <p class="mb-1 text-light"><strong>Fecha:</strong> ${vueloData.fecha}</p>
                                <p class="mb-1 text-light"><strong>Aerolínea:</strong> ${datos.aerolineas[aerolinea].nombre}</p>
                            </div>
                            <div class="col-md-6">
                                <p class="mb-1 text-light"><strong>Estado:</strong> <span class="badge ${estadoBadge}">${reserva.estado}</span></p>
                                <p class="mb-1 text-light"><strong>Fecha Reserva:</strong> ${reserva.fechaReserva}</p>
                                <p class="mb-1 text-light"><strong>Tipo Asiento:</strong> ${reserva.tipoAsiento}</p>
                                <p class="mb-1 text-light"><strong>Costo Total:</strong> $${reserva.costoTotal}</p>
                            </div>
                        </div>

                        <div class="mt-4">
                            <h6 class="text-light">Pasajeros</h6>
                            ${reserva.pasajeros.map(pasajero => `
                                <div class="pasajero-item">
                                    <i class="bi bi-person"></i> ${pasajero}
                                </div>
                            `).join('')}
                        </div>

                        <div class="mt-4">
                            <h6 class="text-light">Detalles Adicionales</h6>
                            <p class="mb-1 text-light"><strong>Cantidad de Pasajes:</strong> ${reserva.cantidadPasajes}</p>
                            <p class="mb-1 text-light"><strong>Equipaje Extra:</strong> ${reserva.equipajeExtra}</p>
                        </div>
                    </div>
                </div>
            `;
        } else {
            container.innerHTML = `
                <div class="alert alert-warning text-center">
                    <h5 class="text-warning">No tiene reserva en este vuelo</h5>
                    <p class="text-light">No se encontró una reserva a su nombre para el vuelo seleccionado.</p>
                    <a href="reserva-vuelo.html" class="btn btn-primary mt-2">Realizar Reserva</a>
                </div>
            `;
        }
    }
}

// Funciones para Aerolínea
function siguientePasoAerolinea(paso) {
    if (paso === 4 && reservaSeleccionada) {
        mostrarReservaAerolinea();
    }

    actualizarPasos('aerolinea', paso);
}

function cargarRutasAerolinea() {
    const select = document.getElementById('rutaAerolinea');
    select.innerHTML = '<option value="">Seleccione una ruta...</option>';

    const aerolinea = datos.aerolineaActual;
    if (datos.aerolineas[aerolinea]) {
        Object.keys(datos.aerolineas[aerolinea].rutas).forEach(rutaId => {
            const ruta = datos.aerolineas[aerolinea].rutas[rutaId];
            const option = document.createElement('option');
            option.value = rutaId;
            option.textContent = `${rutaId} - ${ruta.nombre}`;
            select.appendChild(option);
        });
    }
}

function cargarVuelosAerolinea(ruta) {
    const select = document.getElementById('vueloAerolinea');
    select.innerHTML = '<option value="">Seleccione un vuelo...</option>';

    const aerolinea = datos.aerolineaActual;
    if (ruta && datos.aerolineas[aerolinea].rutas[ruta]) {
        Object.keys(datos.aerolineas[aerolinea].rutas[ruta].vuelos).forEach(vueloId => {
            const vuelo = datos.aerolineas[aerolinea].rutas[ruta].vuelos[vueloId];
            const option = document.createElement('option');
            option.value = vueloId;
            option.textContent = `${vueloId} - ${vuelo.fecha} (${vuelo.reservas.length} reservas)`;
            select.appendChild(option);
        });
    }
}

function cargarReservasAerolinea(ruta, vuelo) {
    const container = document.getElementById('listaReservasAerolinea');
    container.innerHTML = '';

    const aerolinea = datos.aerolineaActual;
    if (ruta && vuelo && datos.aerolineas[aerolinea].rutas[ruta].vuelos[vuelo]) {
        const reservas = datos.aerolineas[aerolinea].rutas[ruta].vuelos[vuelo].reservas;

        if (reservas.length === 0) {
            container.innerHTML = '<div class="col-12"><p class="text-muted text-center">No hay reservas para este vuelo.</p></div>';
            return;
        }

        reservas.forEach((reserva, index) => {
            const estadoBadge = reserva.estado === 'Confirmada' ? 'bg-success' : 'bg-warning';

            const reservaHTML = `
                <div class="col-md-6">
                    <div class="reserva-card" onclick="seleccionarReservaAerolinea(${index})">
                        <div class="card-body">
                            <h6 class="card-title text-light">${reserva.id}</h6>
                            <p class="card-text mb-1 text-light">Cliente: ${reserva.cliente}</p>
                            <p class="card-text mb-1 text-light">Pasajeros: ${reserva.pasajeros.length}</p>
                            <p class="card-text mb-1 text-light">Asiento: ${reserva.tipoAsiento}</p>
                            <span class="badge ${estadoBadge}">${reserva.estado}</span>
                        </div>
                    </div>
                </div>
            `;
            container.innerHTML += reservaHTML;
        });

        document.getElementById('btnAerolinea3').disabled = false;
    }
}

function seleccionarReservaAerolinea(index) {
    const ruta = document.getElementById('rutaAerolinea').value;
    const vuelo = document.getElementById('vueloAerolinea').value;
    const aerolinea = datos.aerolineaActual;

    if (ruta && vuelo && datos.aerolineas[aerolinea].rutas[ruta].vuelos[vuelo]) {
        reservaSeleccionada = datos.aerolineas[aerolinea].rutas[ruta].vuelos[vuelo].reservas[index];

        // Remover selección anterior y marcar actual
        document.querySelectorAll('#flujoAerolinea .reserva-card').forEach(card => {
            card.classList.remove('selected');
        });
        event.currentTarget.classList.add('selected');
    }
}

function mostrarReservaAerolinea() {
    const container = document.getElementById('reservaAerolineaDetalle');

    if (reservaSeleccionada) {
        const estadoBadge = reservaSeleccionada.estado === 'Confirmada' ? 'bg-success' : 'bg-warning';

        container.innerHTML = `
            <div class="card border-primary">
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <h5 class="text-primary">Detalles de la Reserva</h5>
                            <p class="mb-1 text-light"><strong>Código:</strong> ${reservaSeleccionada.id}</p>
                            <p class="mb-1 text-light"><strong>Cliente:</strong> ${reservaSeleccionada.cliente}</p>
                            <p class="mb-1 text-light"><strong>Fecha Reserva:</strong> ${reservaSeleccionada.fechaReserva}</p>
                            <p class="mb-1"><strong>Estado:</strong> <span class="badge ${estadoBadge}">${reservaSeleccionada.estado}</span></p>
                        </div>
                        <div class="col-md-6">
                            <p class="mb-1 text-light"><strong>Tipo Asiento:</strong> ${reservaSeleccionada.tipoAsiento}</p>
                            <p class="mb-1 text-light"><strong>Cantidad Pasajes:</strong> ${reservaSeleccionada.cantidadPasajes}</p>
                            <p class="mb-1 text-light"><strong>Equipaje Extra:</strong> ${reservaSeleccionada.equipajeExtra}</p>
                            <p class="mb-1 text-light"><strong>Costo Total:</strong> $${reservaSeleccionada.costoTotal}</p>
                        </div>
                    </div>

                    <div class="mt-4">
                        <h6 class="text-light">Lista de Pasajeros</h6>
                        ${reservaSeleccionada.pasajeros.map(pasajero => `
                            <div class="pasajero-item">
                                <i class="bi bi-person"></i> <strong class="text-light">${pasajero}</strong>
                            </div>
                        `).join('')}
                    </div>
                </div>
            </div>
        `;
    }
}

// Funciones generales
function actualizarPasos(tipo, pasoActivo) {
    const prefix = tipo === 'cliente' ? 'Cliente' : 'Aerolinea';

    // Ocultar todas las secciones
    document.querySelectorAll(`#flujo${prefix} .form-section`).forEach(section => {
        section.classList.remove('active');
    });

    // Mostrar sección activa
    document.getElementById(`section${prefix}${pasoActivo}`).classList.add('active');

    // Actualizar indicadores de paso
    document.querySelectorAll(`#flujo${prefix} .step`).forEach(step => {
        step.classList.remove('active', 'completed');
    });

    for (let i = 1; i <= pasoActivo; i++) {
        const stepEl = document.getElementById(`step${prefix}${i}`);
        if (i === pasoActivo) {
            stepEl.classList.add('active');
        } else {
            stepEl.classList.add('completed');
        }
    }
}

function cambiarTipoUsuario() {
    tipoUsuario = tipoUsuario === 'cliente' ? 'aerolinea' : 'cliente';

    document.getElementById('flujoCliente').style.display = tipoUsuario === 'cliente' ? 'block' : 'none';
    document.getElementById('flujoAerolinea').style.display = tipoUsuario === 'aerolinea' ? 'block' : 'none';

    document.getElementById('tipoUsuarioTexto').textContent = tipoUsuario === 'cliente' ? 'Cliente' : 'Aerolínea';
    document.getElementById('tipoAlternativo').textContent = tipoUsuario === 'cliente' ? 'Aerolínea' : 'Cliente';
    document.getElementById('nombreUsuario').textContent = tipoUsuario === 'cliente' ? 'María González' : 'ZulyFly Airlines';

    // Reiniciar ambos flujos
    siguientePasoCliente(1);
    siguientePasoAerolinea(1);
}

function nuevaConsulta() {
    if (tipoUsuario === 'cliente') {
        siguientePasoCliente(1);
        document.getElementById('formCliente').reset();
    } else {
        siguientePasoAerolinea(1);
        document.getElementById('formAerolinea').reset();
        reservaSeleccionada = null;
    }
}

// Función para mostrar mensaje de error
function mostrarMensajeError(mensaje) {
    const toastHTML = `
        <div class="toast align-items-center text-bg-danger border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>${mensaje}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    const toastContainer = document.getElementById('toastContainer') || (() => {
        const container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '1060';
        document.body.appendChild(container);
        return container;
    })();

    toastContainer.innerHTML = toastHTML;
    const toastElement = toastContainer.querySelector('.toast');
    const toast = new bootstrap.Toast(toastElement);
    toast.show();
}
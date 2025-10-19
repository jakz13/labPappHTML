// Variables globales
let tipoUsuario = null;
let usuarioInfo = null;
let reservaSeleccionada = null;
let datosAerolineas = [];
let datosRutas = [];
let datosVuelos = [];

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    console.log('Inicializando consulta de reserva...');
    inicializarConsultaReserva();
});

async function inicializarConsultaReserva() {
    // Esperar a que session-manager se inicialice
    await new Promise(resolve => setTimeout(resolve, 500));

    const estadoSesion = await verificarSesion();

    if (estadoSesion && estadoSesion.authenticated) {
        usuarioInfo = estadoSesion;
        determinarTipoUsuario(estadoSesion);
        await cargarAerolineas();
        configurarEventListeners();
    } else {
        mostrarFlujoNoAutenticado();
    }
}

// Función para verificar sesión
function verificarSesion() {
    return new Promise((resolve) => {
        fetch('api/check-session', {
            credentials: 'include'
        })
            .then(res => res.json())
            .then(data => {
                console.log('Estado de sesión en consulta-reserva:', data);
                resolve(data);
            })
            .catch(error => {
                console.error('Error verificando sesión:', error);
                resolve({ authenticated: false });
            });
    });
}

// Determinar tipo de usuario basado en la sesión
function determinarTipoUsuario(estadoSesion) {
    // Usar el tipo que viene del servlet check-session
    tipoUsuario = estadoSesion.tipo; // 'cliente' o 'aerolinea'

    console.log('Tipo de usuario determinado:', tipoUsuario);

    // Actualizar UI
    document.getElementById('tipoUsuarioTexto').textContent = tipoUsuario === 'aerolinea' ? 'Aerolínea' : 'Cliente';
    document.getElementById('nombreUsuario').textContent = estadoSesion.nickname || 'Usuario';

    // Mostrar el flujo correspondiente
    if (tipoUsuario === 'cliente') {
        document.getElementById('flujoCliente').style.display = 'block';
        document.getElementById('flujoAerolinea').style.display = 'none';
        document.getElementById('flujoNoAutenticado').style.display = 'none';
    } else if (tipoUsuario === 'aerolinea') {
        document.getElementById('flujoCliente').style.display = 'none';
        document.getElementById('flujoAerolinea').style.display = 'block';
        document.getElementById('flujoNoAutenticado').style.display = 'none';
    } else {
        mostrarFlujoNoAutenticado();
    }
}

function mostrarFlujoNoAutenticado() {
    document.getElementById('flujoCliente').style.display = 'none';
    document.getElementById('flujoAerolinea').style.display = 'none';
    document.getElementById('flujoNoAutenticado').style.display = 'block';
    document.getElementById('tipoUsuarioTexto').textContent = 'No autenticado';
    document.getElementById('nombreUsuario').textContent = 'Invitado';
}

// Cargar aerolíneas desde backend
function cargarAerolineas() {
    return new Promise((resolve, reject) => {
        fetch('api/aerolineas')
            .then(res => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then(data => {
                datosAerolineas = data;
                console.log('Aerolíneas cargadas:', datosAerolineas);

                if (tipoUsuario === 'cliente') {
                    const selectCliente = document.getElementById('aerolineaCliente');
                    selectCliente.innerHTML = '<option value="">Seleccione aerolínea...</option>';
                    data.forEach(a => {
                        selectCliente.innerHTML += `<option value="${a.nickname}">${a.nombre}</option>`;
                    });
                } else {
                    // Para aerolínea, cargar rutas de la aerolínea del usuario
                    const aerolineaUsuario = usuarioInfo.nickname;
                    if (aerolineaUsuario) {
                        cargarRutasAerolinea(aerolineaUsuario);
                    }
                }
                resolve();
            })
            .catch(err => {
                console.error("Error al cargar aerolíneas:", err);
                mostrarMensajeError('Error al cargar las aerolíneas');

                if (tipoUsuario === 'cliente') {
                    document.getElementById('aerolineaCliente').innerHTML = '<option value="">Error al cargar aerolíneas</option>';
                }
                reject(err);
            });
    });
}

// Configuración de event listeners
function configurarEventListeners() {
    if (tipoUsuario === 'cliente') {
        // Event listeners para cliente
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
    } else {
        // Event listeners para aerolínea
        document.getElementById('rutaAerolinea').addEventListener('change', function() {
            const ruta = this.value;
            cargarVuelosAerolinea(ruta);
            document.getElementById('btnAerolinea1').disabled = !ruta;
        });

        document.getElementById('vueloAerolinea').addEventListener('change', function() {
            const vuelo = this.value;
            cargarReservasAerolinea(vuelo);
            document.getElementById('btnAerolinea2').disabled = !vuelo;
        });
    }
}

// ========== FUNCIONES PARA CARGAR DATOS ==========

// Cargar rutas para cliente
async function cargarRutasCliente(aerolineaNickname) {
    try {
        const select = document.getElementById('rutaCliente');
        select.innerHTML = '<option value="">Cargando rutas...</option>';

        const response = await fetch('api/rutas?aerolinea=' + encodeURIComponent(aerolineaNickname));

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        datosRutas = await response.json();

        select.innerHTML = '<option value="">Seleccione ruta...</option>';

        if (datosRutas.length === 0) {
            select.innerHTML = '<option value="">No hay rutas disponibles</option>';
            return;
        }

        datosRutas.forEach(ruta => {
            const option = document.createElement('option');
            option.value = ruta.nombre;
            option.textContent = ruta.nombre + (ruta.descripcion ? ` - ${ruta.descripcion}` : '');
            select.appendChild(option);
        });

    } catch (error) {
        console.error('Error cargando rutas:', error);
        const select = document.getElementById('rutaCliente');
        select.innerHTML = '<option value="">Error al cargar rutas</option>';
        mostrarMensajeError('Error al cargar las rutas');
    }
}

// Cargar rutas para aerolínea
async function cargarRutasAerolinea(aerolineaNickname) {
    try {
        const select = document.getElementById('rutaAerolinea');
        select.innerHTML = '<option value="">Cargando rutas...</option>';

        const response = await fetch('api/rutas?aerolinea=' + encodeURIComponent(aerolineaNickname));

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        datosRutas = await response.json();

        select.innerHTML = '<option value="">Seleccione ruta...</option>';

        if (datosRutas.length === 0) {
            select.innerHTML = '<option value="">No hay rutas disponibles</option>';
            return;
        }

        datosRutas.forEach(ruta => {
            const option = document.createElement('option');
            option.value = ruta.nombre;
            option.textContent = ruta.nombre + (ruta.descripcion ? ` - ${ruta.descripcion}` : '');
            select.appendChild(option);
        });

    } catch (error) {
        console.error('Error cargando rutas para aerolínea:', error);
        const select = document.getElementById('rutaAerolinea');
        select.innerHTML = '<option value="">Error al cargar rutas</option>';
    }
}

// Cargar vuelos para cliente
async function cargarVuelosCliente(aerolinea, ruta) {
    try {
        const select = document.getElementById('vueloCliente');
        select.innerHTML = '<option value="">Cargando vuelos...</option>';

        const response = await fetch('api/vuelos?ruta=' + encodeURIComponent(ruta));

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        datosVuelos = await response.json();

        select.innerHTML = '<option value="">Seleccione vuelo...</option>';

        if (datosVuelos.length === 0) {
            select.innerHTML = '<option value="">No hay vuelos disponibles</option>';
            return;
        }

        datosVuelos.forEach(vuelo => {
            const option = document.createElement('option');
            option.value = vuelo.nombre;
            option.textContent = vuelo.nombre;
            select.appendChild(option);
        });

    } catch (error) {
        console.error('Error cargando vuelos:', error);
        const select = document.getElementById('vueloCliente');
        select.innerHTML = '<option value="">Error al cargar vuelos</option>';
        mostrarMensajeError('Error al cargar los vuelos');
    }
}

// Cargar vuelos para aerolínea
async function cargarVuelosAerolinea(ruta) {
    try {
        const select = document.getElementById('vueloAerolinea');
        select.innerHTML = '<option value="">Cargando vuelos...</option>';

        const response = await fetch('api/vuelos?ruta=' + encodeURIComponent(ruta));

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        datosVuelos = await response.json();

        select.innerHTML = '<option value="">Seleccione vuelo...</option>';

        if (datosVuelos.length === 0) {
            select.innerHTML = '<option value="">No hay vuelos disponibles</option>';
            return;
        }

        datosVuelos.forEach(vuelo => {
            const option = document.createElement('option');
            option.value = vuelo.nombre;
            option.textContent = vuelo.nombre;
            select.appendChild(option);
        });

    } catch (error) {
        console.error('Error cargando vuelos para aerolínea:', error);
        const select = document.getElementById('vueloAerolinea');
        select.innerHTML = '<option value="">Error al cargar vuelos</option>';
    }
}

// Cargar reservas para aerolínea
async function cargarReservasAerolinea(vueloNombre) {
    try {
        const container = document.getElementById('listaReservasAerolinea');
        container.innerHTML = '<div class="col-12 text-center"><div class="spinner-border text-primary" role="status"></div><p class="mt-2">Cargando reservas...</p></div>';

        const response = await fetch(`api/consulta-reserva?action=reservas-vuelo&vuelo=${encodeURIComponent(vueloNombre)}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const reservas = await response.json();
        container.innerHTML = '';

        if (reservas.length === 0) {
            container.innerHTML = '<div class="col-12"><div class="alert alert-info text-center">No hay reservas para este vuelo.</div></div>';
            document.getElementById('btnAerolinea3').disabled = true;
            return;
        }

        reservas.forEach((reserva, index) => {
            const estadoBadge = 'bg-success';

            const reservaHTML = `
                <div class="col-md-6">
                    <div class="card reserva-card mb-3" onclick="seleccionarReservaAerolinea(
                        '${reserva.id}', 
                        '${reserva.clienteNombre}', 
                        '${reserva.tipoAsiento}', 
                        ${reserva.cantidadPasajes}, 
                        ${reserva.equipajeExtra}, 
                        ${reserva.costoTotal},
                        '${reserva.fechaReserva}'
                    )">
                        <div class="card-body">
                            <h6 class="card-title text-dark">${reserva.id}</h6>
                            <p class="card-text mb-1 text-dark">Cliente: ${reserva.clienteNombre}</p>
                            <p class="card-text mb-1 text-dark">Pasajeros: ${reserva.cantidadPasajes}</p>
                            <p class="card-text mb-1 text-dark">Asiento: ${reserva.tipoAsiento}</p>
                            <p class="card-text mb-1 text-dark">Costo: $${reserva.costoTotal}</p>
                            <span class="badge ${estadoBadge}">Confirmada</span>
                        </div>
                    </div>
                </div>
            `;
            container.innerHTML += reservaHTML;
        });

        document.getElementById('btnAerolinea3').disabled = false;

    } catch (error) {
        console.error('Error cargando reservas:', error);
        const container = document.getElementById('listaReservasAerolinea');
        container.innerHTML = '<div class="col-12"><div class="alert alert-danger text-center">Error al cargar las reservas</div></div>';
        mostrarMensajeError('Error al cargar las reservas: ' + error.message);
    }
}

// ========== FUNCIONES DE NAVEGACIÓN ==========

function siguientePasoCliente(paso) {
    if (paso === 4) {
        const vuelo = document.getElementById('vueloCliente').value;
        mostrarReservaCliente(vuelo);
    }
    actualizarPasos('cliente', paso);
}

function siguientePasoAerolinea(paso) {
    if (paso === 4 && reservaSeleccionada) {
        mostrarReservaAerolinea();
    }
    actualizarPasos('aerolinea', paso);
}

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

// Modificar la función de selección para guardar más datos
function seleccionarReservaAerolinea(reservaId, clienteNombre, tipoAsiento, cantidadPasajes, equipajeExtra, costoTotal, fechaReserva) {
    reservaSeleccionada = {
        id: reservaId,
        clienteNombre: clienteNombre,
        tipoAsiento: tipoAsiento,
        cantidadPasajes: cantidadPasajes,
        equipajeExtra: equipajeExtra,
        costoTotal: costoTotal,
        fechaReserva: fechaReserva
    };

    // Remover selección anterior y marcar actual
    document.querySelectorAll('#flujoAerolinea .reserva-card').forEach(card => {
        card.classList.remove('border-primary', 'bg-light');
    });
    event.currentTarget.classList.add('border-primary', 'bg-light');

    document.getElementById('btnAerolinea3').disabled = false;
}

// ========== FUNCIONES DE MOSTRAR RESULTADOS ==========

async function mostrarReservaCliente(vueloNombre) {
    const container = document.getElementById('reservaClienteDetalle');
    container.innerHTML = '<div class="text-center"><div class="spinner-border text-primary" role="status"></div><p class="mt-2">Buscando reserva...</p></div>';

    try {
        // Buscar reserva del cliente en este vuelo usando el nuevo servlet
        const responseReserva = await fetch(`api/consulta-reserva?action=reserva-cliente-vuelo&usuario=${encodeURIComponent(usuarioInfo.nickname)}&vuelo=${encodeURIComponent(vueloNombre)}`);

        if (responseReserva.ok) {
            const reserva = await responseReserva.json();

            if (reserva.error) {
                // No tiene reserva en este vuelo
                container.innerHTML = `
                    <div class="alert alert-warning text-center">
                        <h5 class="text-warning">No tiene reserva en este vuelo</h5>
                        <p class="text-dark">No se encontró una reserva a su nombre para el vuelo seleccionado.</p>
                    </div>
                `;
                return;
            }

            const estadoBadge = 'bg-success';
            const aerolineaSelect = document.getElementById('aerolineaCliente');
            const aerolineaNombre = aerolineaSelect.options[aerolineaSelect.selectedIndex].text;

            container.innerHTML = `
                <div class="card border-success">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h5 class="text-success">Reserva Confirmada</h5>
                                <p class="mb-1 text-dark"><strong>Código:</strong> ${reserva.id}</p>
                                <p class="mb-1 text-dark"><strong>Vuelo:</strong> ${vueloNombre}</p>
                                <p class="mb-1 text-dark"><strong>Aerolínea:</strong> ${aerolineaNombre}</p>
                            </div>
                            <div class="col-md-6">
                                <p class="mb-1 text-dark"><strong>Estado:</strong> <span class="badge ${estadoBadge}">Confirmada</span></p>
                                <p class="mb-1 text-dark"><strong>Fecha Reserva:</strong> ${reserva.fechaReserva}</p>
                                <p class="mb-1 text-dark"><strong>Tipo Asiento:</strong> ${reserva.tipoAsiento}</p>
                                <p class="mb-1 text-dark"><strong>Costo Total:</strong> $${reserva.costoTotal}</p>
                            </div>
                        </div>

                        <div class="mt-4">
                            <h6 class="text-dark">Detalles Adicionales</h6>
                            <p class="mb-1 text-dark"><strong>Cantidad de Pasajes:</strong> ${reserva.cantidadPasajes}</p>
                            <p class="mb-1 text-dark"><strong>Equipaje Extra:</strong> ${reserva.equipajeExtra}</p>
                        </div>
                    </div>
                </div>
            `;
        } else {
            // No tiene reserva en este vuelo
            container.innerHTML = `
                <div class="alert alert-warning text-center">
                    <h5 class="text-warning">No tiene reserva en este vuelo</h5>
                    <p class="text-dark">No se encontró una reserva a su nombre para el vuelo seleccionado.</p>
                </div>
            `;
        }

    } catch (error) {
        console.error('Error:', error);
        container.innerHTML = `
            <div class="alert alert-warning text-center">
                <h5 class="text-warning">No se pudo cargar la reserva</h5>
                <p class="text-dark">Error al obtener la información de la reserva.</p>
            </div>
        `;
    }
}

// Para aerolínea - mostrar detalles básicos de reserva
async function mostrarReservaAerolinea() {
    const container = document.getElementById('reservaAerolineaDetalle');
    container.innerHTML = '<div class="text-center"><div class="spinner-border text-primary" role="status"></div><p class="mt-2">Cargando detalles...</p></div>';

    if (!reservaSeleccionada) {
        container.innerHTML = '<div class="alert alert-warning">Seleccione una reserva primero</div>';
        return;
    }

    try {
        const estadoBadge = 'bg-success';

        container.innerHTML = `
            <div class="card border-primary">
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <h5 class="text-primary">Detalles de la Reserva</h5>
                            <p class="mb-1 text-dark"><strong>Código:</strong> ${reservaSeleccionada.id}</p>
                            <p class="mb-1 text-dark"><strong>Cliente:</strong> ${reservaSeleccionada.clienteNombre}</p>
                            <p class="mb-1 text-dark"><strong>Fecha Reserva:</strong> ${reservaSeleccionada.fechaReserva}</p>
                            <p class="mb-1"><strong>Estado:</strong> <span class="badge ${estadoBadge}">Confirmada</span></p>
                        </div>
                        <div class="col-md-6">
                            <p class="mb-1 text-dark"><strong>Tipo Asiento:</strong> ${reservaSeleccionada.tipoAsiento}</p>
                            <p class="mb-1 text-dark"><strong>Cantidad Pasajes:</strong> ${reservaSeleccionada.cantidadPasajes}</p>
                            <p class="mb-1 text-dark"><strong>Equipaje Extra:</strong> ${reservaSeleccionada.equipajeExtra}</p>
                            <p class="mb-1 text-dark"><strong>Costo Total:</strong> $${reservaSeleccionada.costoTotal}</p>
                        </div>
                    </div>
                </div>
            </div>
        `;

    } catch (error) {
        console.error('Error:', error);
        container.innerHTML = '<div class="alert alert-danger">Error al cargar los detalles de la reserva</div>';
    }
}

function nuevaConsulta() {
    if (tipoUsuario === 'cliente') {
        siguientePasoCliente(1);
        document.getElementById('formCliente').reset();
    } else {
        siguientePasoAerolinea(1);
        document.getElementById('formAerolinea').reset();
        reservaSeleccionada = null;

        // Limpiar selección de reservas
        document.querySelectorAll('#flujoAerolinea .reserva-card').forEach(card => {
            card.classList.remove('border-primary', 'bg-light');
        });
    }
}

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

    const toastContainer = document.getElementById('toastContainer');
    toastContainer.innerHTML = toastHTML;
    const toastElement = toastContainer.querySelector('.toast');
    const toast = new bootstrap.Toast(toastElement);
    toast.show();
}
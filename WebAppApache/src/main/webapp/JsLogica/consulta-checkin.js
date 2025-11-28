// Variables globales
let tipoUsuario = null;
let usuarioInfo = null;
let reservaCheckinSeleccionada = null;

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    console.log('Inicializando consulta de check-in...');
    inicializarConsultaCheckin();
});

async function inicializarConsultaCheckin() {
    // Esperar a que session-manager se inicialice
    await new Promise(resolve => setTimeout(resolve, 500));

    const estadoSesion = await verificarSesion();

    if (estadoSesion && estadoSesion.authenticated) {
        usuarioInfo = estadoSesion;
        determinarTipoUsuario(estadoSesion);
        await cargarReservasConCheckin();
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
                console.log('Estado de sesión en consulta-checkin:', data);
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
    tipoUsuario = estadoSesion.tipo; // 'cliente' o 'aerolinea'

    console.log('Tipo de usuario determinado:', tipoUsuario);

    // Actualizar UI
    document.getElementById('tipoUsuarioTexto').textContent = tipoUsuario === 'aerolinea' ? 'Aerolínea' : 'Cliente';
    document.getElementById('nombreUsuario').textContent = estadoSesion.nickname || 'Usuario';

    // Mostrar el flujo correspondiente (solo clientes pueden hacer check-in)
    if (tipoUsuario === 'cliente') {
        document.getElementById('flujoCliente').style.display = 'block';
        document.getElementById('flujoNoAutenticado').style.display = 'none';
        document.getElementById('flujoNoCliente').style.display = 'none';
    } else {
        document.getElementById('flujoCliente').style.display = 'none';
        document.getElementById('flujoNoAutenticado').style.display = 'none';
        document.getElementById('flujoNoCliente').style.display = 'block';
    }
}

function mostrarFlujoNoAutenticado() {
    document.getElementById('flujoCliente').style.display = 'none';
    document.getElementById('flujoNoCliente').style.display = 'none';
    document.getElementById('flujoNoAutenticado').style.display = 'block';
    document.getElementById('tipoUsuarioTexto').textContent = 'No autenticado';
    document.getElementById('nombreUsuario').textContent = 'Invitado';
}

// Configuración de event listeners
function configurarEventListeners() {
    // Event listener para selección de reserva
    document.addEventListener('click', function(e) {
        if (e.target.closest('.reserva-checkin-card')) {
            const card = e.target.closest('.reserva-checkin-card');
            const reservaId = card.getAttribute('data-reserva-id');
            seleccionarReservaCheckin(reservaId, card);
        }
    });

    // Event listener para botón de ver detalles
    document.getElementById('btnVerDetallesCheckin').addEventListener('click', function() {
        if (reservaCheckinSeleccionada) {
            mostrarDetallesCheckin(reservaCheckinSeleccionada);
        }
    });

    // Event listeners para botones de tarjeta de embarque
    document.getElementById('btnTarjetaEmbarque').addEventListener('click', function() {
        if (reservaCheckinSeleccionada) {
            generarTarjetaEmbarque(reservaCheckinSeleccionada);
        }
    });

    // Segundo botón en la vista de detalles
    const btnTarjetaEmbarque2 = document.getElementById('btnTarjetaEmbarque2');
    if (btnTarjetaEmbarque2) {
        btnTarjetaEmbarque2.addEventListener('click', function() {
            if (reservaCheckinSeleccionada) {
                generarTarjetaEmbarque(reservaCheckinSeleccionada);
            }
        });
    }
}

// Cargar reservas con check-in realizados
async function cargarReservasConCheckin() {
    try {
        const container = document.getElementById('listaReservasCheckin');
        container.innerHTML = '<div class="col-12 text-center"><div class="spinner-border text-primary" role="status"></div><p class="mt-2">Cargando reservas con check-in...</p></div>';

        const response = await fetch(`api/checkin-reservas?cliente=${encodeURIComponent(usuarioInfo.nickname)}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const reservas = await response.json();
        console.log('Reservas con check-in recibidas:', reservas);

        container.innerHTML = '';

        if (reservas.length === 0) {
            container.innerHTML = `
                <div class="col-12">
                    <div class="alert alert-info text-center">
                        <h5 class="text-info">No hay check-ins realizados</h5>
                        <p class="text-dark">No se encontraron reservas con check-in realizado.</p>
                        <a href="realizar-checkin.jsp" class="btn btn-primary mt-2">Realizar Check-in</a>
                    </div>
                </div>
            `;
            document.getElementById('btnVerDetallesCheckin').disabled = true;
            document.getElementById('btnTarjetaEmbarque').disabled = true;
            return;
        }

        reservas.forEach((reserva, index) => {
            const reservaHTML = `
                <div class="col-md-6 col-lg-4">
                    <div class="card reserva-checkin-card mb-3" data-reserva-id="${reserva.id}">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <h6 class="card-title text-dark mb-0">Reserva #${reserva.id}</h6>
                                <span class="badge bg-success">Check-in Realizado</span>
                            </div>
                            <p class="card-text mb-1 text-dark">
                                <i class="bi bi-airplane me-2"></i>
                                <strong>Vuelo:</strong> ${reserva.vuelo}
                            </p>
                            <p class="card-text mb-1 text-dark">
                                <i class="bi bi-calendar-check me-2"></i>
                                <strong>Check-in:</strong> ${reserva.fechaCheckin}
                            </p>
                            <p class="card-text mb-1 text-dark">
                                <i class="bi bi-clock me-2"></i>
                                <strong>Embarque:</strong> ${reserva.horaEmbarque}
                            </p>
                            <p class="card-text mb-1 text-dark">
                                <i class="bi bi-people me-2"></i>
                                <strong>Pasajeros:</strong> ${reserva.cantidadPasajeros}
                            </p>
                            <p class="card-text mb-0 text-dark">
                                <i class="bi bi-cash-coin me-2"></i>
                                <strong>Costo:</strong> $${reserva.costoTotal}
                            </p>
                        </div>
                    </div>
                </div>
            `;
            container.innerHTML += reservaHTML;
        });

        // Habilitar botones
        document.getElementById('btnVerDetallesCheckin').disabled = true;
        document.getElementById('btnTarjetaEmbarque').disabled = true;

    } catch (error) {
        console.error('Error cargando reservas con check-in:', error);
        const container = document.getElementById('listaReservasCheckin');
        container.innerHTML = '<div class="col-12"><div class="alert alert-danger text-center">Error al cargar las reservas con check-in</div></div>';
        mostrarMensajeError('Error al cargar las reservas con check-in: ' + error.message);
    }
}

// Seleccionar reserva para check-in
function seleccionarReservaCheckin(reservaId, cardElement) {
    // Remover selección anterior
    document.querySelectorAll('.reserva-checkin-card').forEach(card => {
        card.classList.remove('border-primary', 'bg-light');
    });

    // Marcar como seleccionada
    cardElement.classList.add('border-primary', 'bg-light');

    // Guardar reserva seleccionada
    reservaCheckinSeleccionada = reservaId;

    // Habilitar botones
    document.getElementById('btnVerDetallesCheckin').disabled = false;
    document.getElementById('btnTarjetaEmbarque').disabled = false;
}

// Mostrar detalles del check-in
async function mostrarDetallesCheckin(reservaId) {
    try {
        const container = document.getElementById('detallesCheckinContainer');
        container.innerHTML = '<div class="text-center"><div class="spinner-border text-primary" role="status"></div><p class="mt-2">Cargando detalles del check-in...</p></div>';

        const response = await fetch(`api/checkin-reservas?cliente=${encodeURIComponent(usuarioInfo.nickname)}&reservaId=${reservaId}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const checkinDetalles = await response.json();
        console.log('Detalles de check-in recibidos:', checkinDetalles);

        // Construir HTML de detalles
        let pasajerosHTML = '';
        if (checkinDetalles.pasajeros && checkinDetalles.pasajeros.length > 0) {
            pasajerosHTML = checkinDetalles.pasajeros.map(pasajero => `
                <div class="row border-bottom pb-2 mb-2">
                    <div class="col-md-6">
                        <strong class="text-dark">Pasajero:</strong>
                        <span class="text-dark">${pasajero.nombre} ${pasajero.apellido}</span>
                    </div>
                    <div class="col-md-6">
                        <strong class="text-dark">Asiento:</strong>
                        <span class="badge bg-primary">${pasajero.asiento}</span>
                    </div>
                </div>
            `).join('');
        }

        const detallesHTML = `
            <div class="card border-success">
                <div class="card-header bg-success text-white">
                    <h5 class="mb-0">
                        <i class="bi bi-check-circle-fill me-2"></i>
                        Detalles del Check-in - Reserva #${checkinDetalles.id}
                    </h5>
                </div>
                <div class="card-body">
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <h6 class="text-success">Información del Vuelo</h6>
                            <p class="mb-1 text-dark"><strong>Vuelo:</strong> ${checkinDetalles.vuelo}</p>
                            <p class="mb-1 text-dark"><strong>Fecha de Reserva:</strong> ${checkinDetalles.fechaReserva}</p>
                            <p class="mb-1 text-dark"><strong>Tipo de Asiento:</strong> ${checkinDetalles.tipoAsiento}</p>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-success">Información del Check-in</h6>
                            <p class="mb-1 text-dark"><strong>Fecha de Check-in:</strong> ${checkinDetalles.fechaCheckin}</p>
                            <p class="mb-1 text-dark"><strong>Hora de Embarque:</strong> ${checkinDetalles.horaEmbarque}</p>
                            <p class="mb-1 text-dark"><strong>Cantidad de Pasajes:</strong> ${checkinDetalles.cantidadPasajes}</p>
                        </div>
                    </div>

                    <div class="row mb-4">
                        <div class="col-12">
                            <h6 class="text-success">Detalles de Costos</h6>
                            <div class="row">
                                <div class="col-md-4">
                                    <p class="mb-1 text-dark"><strong>Costo Total:</strong> $${checkinDetalles.costoTotal}</p>
                                </div>
                                <div class="col-md-4">
                                    <p class="mb-1 text-dark"><strong>Equipaje Extra:</strong> ${checkinDetalles.equipajeExtra} unidades</p>
                                </div>
                                <div class="col-md-4">
                                    <p class="mb-1 text-dark"><strong>Asientos Asignados:</strong></p>
                                    <div class="badge-group">
                                        ${checkinDetalles.asientosAsignados.map(asiento =>
            `<span class="badge bg-primary me-1">${asiento}</span>`
        ).join('')}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-12">
                            <h6 class="text-success">Pasajeros y Asignación de Asientos</h6>
                            ${pasajerosHTML}
                        </div>
                    </div>
                </div>
            </div>
        `;

        container.innerHTML = detallesHTML;

        // Mostrar la sección de detalles
        siguientePasoCheckin(2);

    } catch (error) {
        console.error('Error cargando detalles del check-in:', error);
        const container = document.getElementById('detallesCheckinContainer');
        container.innerHTML = '<div class="alert alert-danger">Error al cargar los detalles del check-in</div>';
        mostrarMensajeError('Error al cargar los detalles del check-in: ' + error.message);
    }
}

// Generar tarjeta de embarque en PDF - VERSIÓN ACTUALIZADA
async function generarTarjetaEmbarque(reservaId) {
    try {
        // Deshabilitar ambos botones durante la generación
        const btn1 = document.getElementById('btnTarjetaEmbarque');
        const btn2 = document.getElementById('btnTarjetaEmbarque2');

        const oldHtml1 = btn1.innerHTML;
        const oldHtml2 = btn2 ? btn2.innerHTML : '';

        btn1.innerHTML = '<span class="spinner-border spinner-border-sm" role="status"></span> Generando PDF...';
        btn1.disabled = true;

        if (btn2) {
            btn2.innerHTML = '<span class="spinner-border spinner-border-sm" role="status"></span> Generando PDF...';
            btn2.disabled = true;
        }

        // Construir URL del servlet
        const url = `api/tarjeta-embarque?cliente=${encodeURIComponent(usuarioInfo.nickname)}&reservaId=${reservaId}`;

        console.log('Descargando tarjeta de embarque desde:', url);

        // Hacer la petición para obtener el PDF
        const response = await fetch(url, {
            method: 'GET',
            credentials: 'include'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error al generar PDF: ${response.status} - ${errorText}`);
        }

        // Obtener el blob del PDF
        const blob = await response.blob();

        // Crear URL del blob y descargar
        const blobUrl = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = blobUrl;
        link.download = `tarjeta-embarque-${reservaId}.pdf`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        // Liberar memoria
        window.URL.revokeObjectURL(blobUrl);

        mostrarMensajeExito('Tarjeta de embarque descargada correctamente');

    } catch (error) {
        console.error('Error generando tarjeta de embarque:', error);
        mostrarMensajeError('Error al generar la tarjeta de embarque: ' + error.message);
    } finally {
        // Restaurar botones
        const btn1 = document.getElementById('btnTarjetaEmbarque');
        const btn2 = document.getElementById('btnTarjetaEmbarque2');

        btn1.innerHTML = '<i class="bi bi-download me-2"></i>Descargar Tarjeta de Embarque';
        btn1.disabled = false;

        if (btn2) {
            btn2.innerHTML = '<i class="bi bi-download me-2"></i>Descargar Tarjeta de Embarque';
            btn2.disabled = false;
        }
    }
}

// ========== FUNCIONES DE NAVEGACIÓN ==========

function siguientePasoCheckin(paso) {
    actualizarPasosCheckin(paso);
}

function anteriorPasoCheckin(paso) {
    actualizarPasosCheckin(paso);
}

function actualizarPasosCheckin(pasoActivo) {
    // Ocultar todas las secciones
    document.querySelectorAll('#flujoCliente .form-section-checkin').forEach(section => {
        section.classList.remove('active');
    });

    // Mostrar sección activa
    document.getElementById(`sectionCheckin${pasoActivo}`).classList.add('active');

    // Actualizar indicadores de paso
    document.querySelectorAll('#flujoCliente .step-checkin').forEach(step => {
        step.classList.remove('active', 'completed');
    });

    for (let i = 1; i <= pasoActivo; i++) {
        const stepEl = document.getElementById(`stepCheckin${i}`);
        if (i === pasoActivo) {
            stepEl.classList.add('active');
        } else {
            stepEl.classList.add('completed');
        }
    }
}

function nuevaConsultaCheckin() {
    // Resetear selección
    reservaCheckinSeleccionada = null;
    document.querySelectorAll('.reserva-checkin-card').forEach(card => {
        card.classList.remove('border-primary', 'bg-light');
    });

    // Deshabilitar botones
    document.getElementById('btnVerDetallesCheckin').disabled = true;
    document.getElementById('btnTarjetaEmbarque').disabled = true;

    // Volver al paso 1
    siguientePasoCheckin(1);

    // Recargar lista
    cargarReservasConCheckin();
}

// ========== FUNCIONES DE MENSAJES ==========

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

function mostrarMensajeExito(mensaje) {
    const toastHTML = `
        <div class="toast align-items-center text-bg-success border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi bi-check-circle-fill me-2"></i>${mensaje}
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
// Variables globales
let vueloSeleccionado = null;
let sessionUser = null; // info de sesión (nombre, apellido, nickname, tipo)

// Inicialización
document.addEventListener('DOMContentLoaded', async function() {
    await cargarSesionUsuario();
    cargarAerolineas();
    configurarEventListeners();
    inicializarValidacion();

    // Renderizar pasajeros inicial según el valor por defecto del input
    const cantidadInicial = parseInt(document.getElementById('cantidadPasajes').value || '1', 10);
    renderPasajeros(cantidadInicial);
});

// Cargar información de sesión del servidor o window.CURRENT_SESSION
async function cargarSesionUsuario() {
    try {
        if (window.CURRENT_SESSION) {
            sessionUser = window.CURRENT_SESSION;
            return;
        }
        const res = await fetch('api/check-session', { credentials: 'include' });
        if (res.ok) {
            const data = await res.json();
            sessionUser = data;
        } else {
            sessionUser = null;
        }
    } catch (e) {
        console.warn('No se pudo obtener sesión:', e);
        sessionUser = null;
    }
}

// Cargar aerolíneas desde backend
function cargarAerolineas() {
    fetch('api/aerolineas')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('aerolinea');
            select.innerHTML = '<option value="">Seleccione aerolínea...</option>';
            data.forEach(a => {
                select.innerHTML += `<option value="${a.nickname}">${a.nombre}</option>`;
            });
            select.disabled = false;
        })
        .catch(err => {
            console.error("Error al cargar aerolíneas:", err);
        });
}

function configurarEventListeners() {
    // Aerolínea -> rutas
    document.getElementById('aerolinea').addEventListener('change', function() {
        const aerolinea = this.value;
        const rutaSelect = document.getElementById('rutaVuelo');
        const vueloSelect = document.getElementById('vuelo');
        rutaSelect.innerHTML = '<option value="">Cargando rutas...</option>';
        rutaSelect.disabled = true;
        vueloSelect.innerHTML = '<option value="">Seleccione una ruta primero</option>';
        vueloSelect.disabled = true;
        document.getElementById('btnSiguiente1').disabled = true;
        document.getElementById('infoVuelo').style.display = 'none';
        vueloSeleccionado = null;

        if (aerolinea) {
            fetch('api/rutas?aerolinea=' + encodeURIComponent(aerolinea))
                .then(res => res.json())
                .then(data => {
                    rutaSelect.innerHTML = '<option value="">Seleccione ruta...</option>';
                    data.forEach(r => {
                        rutaSelect.innerHTML += `<option value="${r.nombre}">${r.nombre}</option>`;
                    });
                    rutaSelect.disabled = false;
                });
        }
    });

    // Ruta -> vuelos
    document.getElementById('rutaVuelo').addEventListener('change', function() {
        const ruta = this.value;
        const vueloSelect = document.getElementById('vuelo');
        vueloSelect.innerHTML = '<option value="">Cargando vuelos...</option>';
        vueloSelect.disabled = true;
        document.getElementById('btnSiguiente1').disabled = true;
        document.getElementById('infoVuelo').style.display = 'none';
        vueloSeleccionado = null;

        if (ruta) {
            fetch('api/vuelos?ruta=' + encodeURIComponent(ruta))
                .then(res => res.json())
                .then(data => {
                    vueloSelect.innerHTML = '<option value="">Seleccione vuelo...</option>';
                    data.forEach(v => {
                        vueloSelect.innerHTML += `<option value="${v.nombre}">${v.nombre}</option>`;
                    });
                    vueloSelect.disabled = false;
                });
        }
    });

    // Vuelo -> mostrar info y habilitar botón
    document.getElementById('vuelo').addEventListener('change', function() {
        const vuelo = this.value;
        const btnSiguiente = document.getElementById('btnSiguiente1');
        const infoVueloDiv = document.getElementById('infoVuelo');
        const infoVueloCard = infoVueloDiv.querySelector('.card-body');

        if (vuelo) {
            fetch('api/vuelo?nombre=' + encodeURIComponent(vuelo))
                .then(res => res.json())
                .then(data => {
                    vueloSeleccionado = data;
                    const nombre = data.nombre || '-';
                    const fecha = data.fecha || '-';
                    const duracion = data.duracion !== undefined ? data.duracion : '-';
                    const asientosTurista = data.asientosTurista !== undefined ? data.asientosTurista : '-';
                    const asientosEjecutivo = data.asientosEjecutivo !== undefined ? data.asientosEjecutivo : '-';

                    infoVueloCard.innerHTML = `
        <div class="row">
            <div class="col-md-12">
                <h5 class="text-primary mb-2">${nombre}</h5>
                <p class="mb-1"><strong>Fecha:</strong> ${fecha}</p>
                <p class="mb-1"><strong>Duración:</strong> ${duracion}</p>
                <p class="mb-1"><strong>Asientos Turista:</strong> ${asientosTurista} &nbsp; <strong>Ejecutivo:</strong> ${asientosEjecutivo}</p>
            </div>
        </div>
    `;
                    infoVueloDiv.style.display = 'block';
                    btnSiguiente.disabled = false;
                })
                .catch(() => {
                    infoVueloDiv.style.display = 'none';
                    btnSiguiente.disabled = true;
                    vueloSeleccionado = null;
                });
        } else {
            infoVueloDiv.style.display = 'none';
            btnSiguiente.disabled = true;
            vueloSeleccionado = null;
        }
    });

    // Gestión de pasajeros (usar la función renderPasajeros)
    document.getElementById('cantidadPasajes').addEventListener('input', function() {
        const cantidad = parseInt(this.value) || 1;
        renderPasajeros(cantidad);
        calcularCostos();
    });

    // Gestión de forma de pago
    document.getElementById('formaPago').addEventListener('change', function() {
        const selectorPaquete = document.getElementById('selectorPaquete');
        selectorPaquete.style.display = this.value === 'paquete' ? 'block' : 'none';
        calcularCostos();
    });

    // Event listeners para cálculos automáticos
    document.getElementById('tipoAsiento').addEventListener('change', calcularCostos);
    document.getElementById('equipajeExtra').addEventListener('input', calcularCostos);
    document.getElementById('paqueteSelect').addEventListener('change', calcularCostos);

    // Envío del formulario
    document.getElementById('formReservaVuelo').addEventListener('submit', function(event) {
        event.preventDefault();
        event.stopPropagation();

        if (this.checkValidity() && validarPaso2() && document.getElementById('confirmarReserva').checked) {
            // recolectar datos de pasajeros
            const pasajeroCards = Array.from(document.querySelectorAll('#pasajerosContainer .pasajero-card'));
            const pasajeros = pasajeroCards.map(card => {
                const nombreInput = card.querySelector('input[name="pasajero-nombre"]');
                const apellidoInput = card.querySelector('input[name="pasajero-apellido"]');
                return {
                    nombre: nombreInput ? nombreInput.value.trim() : '',
                    apellido: apellidoInput ? apellidoInput.value.trim() : ''
                };
            });

            const datosReserva = {
                vuelo: vueloSeleccionado ? vueloSeleccionado.nombre : null,
                tipoAsiento: document.getElementById('tipoAsiento').value,
                cantidadPasajes: parseInt(document.getElementById('cantidadPasajes').value, 10) || 1,
                equipajeExtra: parseInt(document.getElementById('equipajeExtra').value, 10) || 0,
                formaPago: document.getElementById('formaPago').value,
                paquete: document.getElementById('formaPago').value === 'paquete' ? document.getElementById('paqueteSelect').value : null,
                pasajeros: pasajeros
            };

            fetch('api/reservas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosReserva),
                credentials: 'include'
            })
                .then(res => {
                    if (!res.ok) throw new Error('Error al registrar la reserva');
                    return res.json();
                })
                .then(data => {
                    document.getElementById('codigoReserva').textContent = data.codigoReserva;
                    const successModal = new bootstrap.Modal(document.getElementById('successModal'));
                    successModal.show();
                    this.classList.remove('was-validated');
                })
                .catch(() => {
                    mostrarMensajeError('No se pudo registrar la reserva. Intente nuevamente.');
                });
        } else {
            if (!document.getElementById('confirmarReserva').checked) {
                mostrarMensajeError('Debe confirmar que los datos son correctos antes de realizar la reserva.');
            }
            event.stopPropagation();
        }

        this.classList.add('was-validated');
    });
}

// Función para renderizar los formularios de pasajeros
function renderPasajeros(cantidad) {
    const container = document.getElementById('pasajerosContainer');
    const pasajerosDiv = document.getElementById('pasajerosDiv');
    container.innerHTML = '';

    if (!cantidad || cantidad < 1) {
        pasajerosDiv.style.display = 'none';
        return;
    }

    pasajerosDiv.style.display = 'block';

    for (let i = 1; i <= cantidad; i++) {
        // crear card
        const isFirst = i === 1;
        const nombreVal = isFirst ? (sessionUser && (sessionUser.nombre || sessionUser.firstName) ? (sessionUser.nombre || sessionUser.firstName) : (sessionUser && sessionUser.nickname ? sessionUser.nickname.split(' ')[0] : '')) : '';
        const apellidoVal = isFirst ? (sessionUser && (sessionUser.apellido || sessionUser.lastName) ? (sessionUser.apellido || sessionUser.lastName) : (sessionUser && sessionUser.nickname ? (sessionUser.nickname.split(' ').slice(1).join(' ') || '') : '')) : '';

        const readonlyAttr = isFirst ? 'readonly' : '';
        const helperNote = isFirst ? '<small class="text-muted">(Autocompletado desde su cuenta)</small>' : '';

        const pasajeroHTML = document.createElement('div');
        pasajeroHTML.className = 'pasajero-card mb-3 p-3 border rounded bg-dark';
        pasajeroHTML.innerHTML = `
            <h6 class="mb-2 text-light">Pasajero ${i} ${helperNote}</h6>
            <div class="row g-2">
                <div class="col-md-6">
                    <input type="text" name="pasajero-nombre" class="form-control" placeholder="Nombre *" value="${escapeHtml(nombreVal)}" ${readonlyAttr} required>
                </div>
                <div class="col-md-6">
                    <input type="text" name="pasajero-apellido" class="form-control" placeholder="Apellido *" value="${escapeHtml(apellidoVal)}" ${readonlyAttr} required>
                </div>
            </div>
        `;

        container.appendChild(pasajeroHTML);
    }
}

// helper para evitar inyección de HTML al insertar valores
function escapeHtml(str) {
    if (!str) return '';
    return String(str).replace(/[&<>"']/g, function (c) {
        return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];
    });
}

// Navegación entre pasos
function nextStep(step) {
    if (step === 2 && !validarPaso1()) return;
    if (step === 3 && !validarPaso2()) return;

    document.querySelectorAll('.form-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById('section' + step).classList.add('active');

    document.querySelectorAll('.step').forEach(stepEl => {
        stepEl.classList.remove('active', 'completed');
    });

    for (let i = 1; i <= step; i++) {
        const stepEl = document.getElementById('step' + i);
        if (i === step) {
            stepEl.classList.add('active');
        } else {
            stepEl.classList.add('completed');
        }
    }

    if (step === 3) {
        generarResumenReserva();
    }
}

function prevStep(step) {
    nextStep(step);
}

// Validaciones
function validarPaso1() {
    if (vueloSeleccionado === null) {
        mostrarMensajeError('Por favor seleccione un vuelo para continuar.');
        return false;
    }
    return true;
}

// Validar que los datos de pasajeros estén completos
function validarPaso2() {
    const tipoAsiento = document.getElementById('tipoAsiento').value;
    const cantidadPasajes = parseInt(document.getElementById('cantidadPasajes').value, 10) || 1;
    const formaPago = document.getElementById('formaPago').value;

    if (!tipoAsiento || !cantidadPasajes || !formaPago) {
        mostrarMensajeError('Por favor complete todos los campos requeridos del paso 2.');
        return false;
    }

    if (formaPago === 'paquete' && !document.getElementById('paqueteSelect').value) {
        mostrarMensajeError('Por favor seleccione un paquete para el pago.');
        return false;
    }

    // validar pasajeros
    const pasajeroCards = Array.from(document.querySelectorAll('#pasajerosContainer .pasajero-card'));
    if (pasajeroCards.length !== cantidadPasajes) {
        mostrarMensajeError('La cantidad de formularios de pasajeros no coincide con la cantidad de pasajes.');
        return false;
    }

    for (const card of pasajeroCards) {
        const nombre = card.querySelector('input[name="pasajero-nombre"]').value.trim();
        const apellido = card.querySelector('input[name="pasajero-apellido"]').value.trim();
        if (!nombre || !apellido) {
            mostrarMensajeError('Por favor complete el nombre y apellido de todos los pasajeros.');
            return false;
        }
    }

    return true;
}

// Cálculo de costos (simulado, debes adaptar si tienes endpoint real)
function calcularCostos() {
    document.getElementById('resumenCostos').style.display = 'none';
    document.getElementById('btnSiguiente2').disabled = false;
}

// Generar resumen de reserva
function generarResumenReserva() {
    const resumen = document.getElementById('resumenReserva');
    const tipoAsiento = document.getElementById('tipoAsiento').value;
    const cantidadPasajes = document.getElementById('cantidadPasajes').value;
    const equipajeExtra = document.getElementById('equipajeExtra').value;
    const formaPago = document.getElementById('formaPago').value;

    let html = `
        <p class="text-light"><strong>Vuelo:</strong> ${vueloSeleccionado ? vueloSeleccionado.nombre : ''}</p>
        <p class="text-light"><strong>Fecha:</strong> ${vueloSeleccionado ? vueloSeleccionado.fecha : ''}</p>
        <p class="text-light"><strong>Tipo de asiento:</strong> ${tipoAsiento}</p>
        <p class="text-light"><strong>Cantidad de pasajes:</strong> ${cantidadPasajes}</p>
        <p class="text-light"><strong>Equipaje extra:</strong> ${equipajeExtra} unidades</p>
        <p class="text-light"><strong>Forma de pago:</strong> ${formaPago === 'general' ? 'Pago General' : 'Pago con Paquete'}</p>
        <p class="text-light"><strong>Costo total:</strong> ${document.getElementById('costoTotal').textContent}</p>
    `;

    resumen.innerHTML = html;
}

function mostrarModalExito() {
    const codigoReserva = 'RES-' + Math.floor(1000 + Math.random() * 9000) + '-' + new Date().getFullYear();
    document.getElementById('codigoReserva').textContent = codigoReserva;

    const successModal = new bootstrap.Modal(document.getElementById('successModal'));
    successModal.show();
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
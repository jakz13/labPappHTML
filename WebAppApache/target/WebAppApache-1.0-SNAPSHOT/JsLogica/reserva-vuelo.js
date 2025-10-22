// Variables globales
let vueloSeleccionado = null;

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    cargarAerolineas();
    configurarEventListeners();
    inicializarValidacion();
});

// Cargar aerolíneas desde backend
function cargarAerolineas() {
    fetch('aerolineas')
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

    // Gestión de pasajeros
    document.getElementById('cantidadPasajes').addEventListener('input', function() {
        const cantidad = parseInt(this.value);
        const container = document.getElementById('pasajerosContainer');
        const pasajerosDiv = document.getElementById('pasajerosDiv');

        container.innerHTML = '';

        if (cantidad > 1) {
            pasajerosDiv.style.display = 'block';
            for (let i = 1; i <= cantidad; i++) {
                const pasajeroHTML = `
                    <div class="pasajero-card">
                        <h6 class="mb-3 text-light">Pasajero ${i}</h6>
                        <div class="row g-2">
                            <div class="col-md-6">
                                <input type="text" class="form-control" placeholder="Nombre *" required>
                            </div>
                            <div class="col-md-6">
                                <input type="text" class="form-control" placeholder="Apellido *" required>
                            </div>
                        </div>
                    </div>
                `;
                container.innerHTML += pasajeroHTML;
            }
        } else {
            pasajerosDiv.style.display = 'none';
        }

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
            const datosReserva = {
                vuelo: vueloSeleccionado.nombre,
                tipoAsiento: document.getElementById('tipoAsiento').value,
                cantidadPasajes: document.getElementById('cantidadPasajes').value,
                equipajeExtra: document.getElementById('equipajeExtra').value,
                formaPago: document.getElementById('formaPago').value,
                paquete: document.getElementById('formaPago').value === 'paquete' ? document.getElementById('paqueteSelect').value : null
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

function inicializarValidacion() {
    const form = document.getElementById('formReservaVuelo');
    const camposRequeridos = form.querySelectorAll('[required]');
    camposRequeridos.forEach(campo => {
        campo.addEventListener('change', function() {
            if (this.value.trim()) {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
            } else {
                this.classList.remove('is-valid');
                this.classList.add('is-invalid');
            }
        });
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

function validarPaso2() {
    const tipoAsiento = document.getElementById('tipoAsiento').value;
    const cantidadPasajes = document.getElementById('cantidadPasajes').value;
    const formaPago = document.getElementById('formaPago').value;

    if (!tipoAsiento || !cantidadPasajes || !formaPago) {
        mostrarMensajeError('Por favor complete todos los campos requeridos del paso 2.');
        return false;
    }

    if (formaPago === 'paquete' && !document.getElementById('paqueteSelect').value) {
        mostrarMensajeError('Por favor seleccione un paquete para el pago.');
        return false;
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
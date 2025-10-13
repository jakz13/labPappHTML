// Validación del formulario y funcionalidad
document.addEventListener('DOMContentLoaded', function() {
    // Inicializar funcionalidad
    inicializarReservaVuelo();
});

// Datos de ejemplo
const datosVuelos = {
    'iberia': {
        rutas: {
            'IB6012': {
                nombre: 'Montevideo - Madrid',
                vuelos: {
                    'IB6012201': {
                        nombre: 'IB6012 - Vuelo 201',
                        fecha: '25/10/2024',
                        duracion: '12 horas',
                        horaSalida: '22:00',
                        horaLlegada: '14:00 (+1)',
                        asientosTurista: 142,
                        asientosEjecutivo: 24,
                        costoTurista: 750,
                        costoEjecutivo: 1200,
                        costoEquipaje: 30,
                        imagen: 'https://via.placeholder.com/600x300/3498db/ffffff?text=IB6012+Montevideo-Madrid'
                    }
                }
            }
        }
    },
    'zulyfly': {
        rutas: {
            'ZL1502': {
                nombre: 'Montevideo - Rio de Janeiro',
                vuelos: {
                    'ZL1502001': {
                        nombre: 'ZL1502 - Vuelo 001',
                        fecha: '28/10/2024',
                        duracion: '2 horas 30 min',
                        horaSalida: '07:15',
                        horaLlegada: '09:45',
                        asientosTurista: 148,
                        asientosEjecutivo: 28,
                        costoTurista: 320,
                        costoEjecutivo: 550,
                        costoEquipaje: 25,
                        imagen: 'https://via.placeholder.com/600x300/8e44ad/ffffff?text=ZL1502+Montevideo-Rio'
                    }
                }
            }
        }
    },
    'copa': {
        rutas: {
            'CN804': {
                nombre: 'Ciudad de Panamá - Nueva York',
                vuelos: {
                    'CN804101': {
                        nombre: 'CN804 - Vuelo 101',
                        fecha: '30/10/2024',
                        duracion: '5 horas 15 min',
                        horaSalida: '08:30',
                        horaLlegada: '15:45',
                        asientosTurista: 135,
                        asientosEjecutivo: 18,
                        costoTurista: 450,
                        costoEjecutivo: 780,
                        costoEquipaje: 35,
                        imagen: 'https://via.placeholder.com/600x300/e74c3c/ffffff?text=CN804+Panamá-NY'
                    }
                }
            }
        }
    },
    'american': {
        rutas: {
            'AA904': {
                nombre: 'Miami - Cancún',
                vuelos: {
                    'AA904301': {
                        nombre: 'AA904 - Vuelo 301',
                        fecha: '01/11/2024',
                        duracion: '2 horas 30 min',
                        horaSalida: '10:00',
                        horaLlegada: '12:30',
                        asientosTurista: 156,
                        asientosEjecutivo: 32,
                        costoTurista: 380,
                        costoEjecutivo: 650,
                        costoEquipaje: 28,
                        imagen: 'https://via.placeholder.com/600x300/dc2626/ffffff?text=AA904+Miami-Cancún'
                    }
                }
            }
        }
    }
};

// Variables globales
let vueloSeleccionado = null;

function inicializarReservaVuelo() {
    // Configurar event listeners
    configurarEventListeners();

    // Inicializar validación
    inicializarValidacion();
}

function configurarEventListeners() {
    // Navegación de aerolínea -> ruta -> vuelo
    document.getElementById('aerolinea').addEventListener('change', function() {
        const aerolinea = this.value;
        const rutaSelect = document.getElementById('rutaVuelo');
        const vueloSelect = document.getElementById('vuelo');

        rutaSelect.innerHTML = '<option value="">Seleccione ruta...</option>';
        vueloSelect.innerHTML = '<option value="">Primero seleccione ruta</option>';
        vueloSelect.disabled = true;
        document.getElementById('btnSiguiente1').disabled = true;
        document.getElementById('infoVuelo').style.display = 'none';

        if (aerolinea && datosVuelos[aerolinea]) {
            rutaSelect.disabled = false;
            Object.keys(datosVuelos[aerolinea].rutas).forEach(rutaId => {
                const ruta = datosVuelos[aerolinea].rutas[rutaId];
                const option = document.createElement('option');
                option.value = rutaId;
                option.textContent = `${rutaId} - ${ruta.nombre}`;
                rutaSelect.appendChild(option);
            });
        } else {
            rutaSelect.disabled = true;
        }
    });

    document.getElementById('rutaVuelo').addEventListener('change', function() {
        const aerolinea = document.getElementById('aerolinea').value;
        const ruta = this.value;
        const vueloSelect = document.getElementById('vuelo');

        vueloSelect.innerHTML = '<option value="">Seleccione vuelo...</option>';
        document.getElementById('btnSiguiente1').disabled = true;
        document.getElementById('infoVuelo').style.display = 'none';

        if (aerolinea && ruta && datosVuelos[aerolinea].rutas[ruta]) {
            vueloSelect.disabled = false;
            Object.keys(datosVuelos[aerolinea].rutas[ruta].vuelos).forEach(vueloId => {
                const vuelo = datosVuelos[aerolinea].rutas[ruta].vuelos[vueloId];
                const option = document.createElement('option');
                option.value = vueloId;
                option.textContent = `${vueloId} - ${vuelo.nombre}`;
                vueloSelect.appendChild(option);
            });
        } else {
            vueloSelect.disabled = true;
        }
    });

    document.getElementById('vuelo').addEventListener('change', function() {
        const aerolinea = document.getElementById('aerolinea').value;
        const ruta = document.getElementById('rutaVuelo').value;
        const vueloId = this.value;

        if (aerolinea && ruta && vueloId && datosVuelos[aerolinea].rutas[ruta].vuelos[vueloId]) {
            vueloSeleccionado = datosVuelos[aerolinea].rutas[ruta].vuelos[vueloId];

            // Actualizar información del vuelo
            document.getElementById('nombreVueloDetalle').textContent = vueloSeleccionado.nombre;
            document.getElementById('aerolineaDetalle').textContent = document.getElementById('aerolinea').options[document.getElementById('aerolinea').selectedIndex].text;
            document.getElementById('rutaDetalle').textContent = `${ruta} - ${datosVuelos[aerolinea].rutas[ruta].nombre}`;
            document.getElementById('fechaVueloDetalle').textContent = vueloSeleccionado.fecha;
            document.getElementById('duracionVueloDetalle').textContent = vueloSeleccionado.duracion;
            document.getElementById('horaSalidaDetalle').textContent = vueloSeleccionado.horaSalida;
            document.getElementById('horaLlegadaDetalle').textContent = vueloSeleccionado.horaLlegada;
            document.getElementById('asientosTuristaDetalle').textContent = vueloSeleccionado.asientosTurista + ' disponibles';
            document.getElementById('asientosEjecutivoDetalle').textContent = vueloSeleccionado.asientosEjecutivo + ' disponibles';
            document.getElementById('imagenVueloDetalle').src = vueloSeleccionado.imagen;

            document.getElementById('infoVuelo').style.display = 'block';
            document.getElementById('btnSiguiente1').disabled = false;
        } else {
            vueloSeleccionado = null;
            document.getElementById('infoVuelo').style.display = 'none';
            document.getElementById('btnSiguiente1').disabled = true;
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
                vuelo: vueloSeleccionado,
                tipoAsiento: document.getElementById('tipoAsiento').value,
                cantidadPasajes: document.getElementById('cantidadPasajes').value,
                equipajeExtra: document.getElementById('equipajeExtra').value,
                formaPago: document.getElementById('formaPago').value,
                paquete: document.getElementById('formaPago').value === 'paquete' ? document.getElementById('paqueteSelect').value : null,
                costoTotal: document.getElementById('costoTotal').textContent
            };

            console.log('Reserva a realizar:', datosReserva);

            // Simular envío exitoso
            mostrarModalExito();
            this.classList.remove('was-validated');
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

    // Validación en tiempo real para campos requeridos
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

// Cálculo de costos
function calcularCostos() {
    if (!vueloSeleccionado) return;

    const tipoAsiento = document.getElementById('tipoAsiento').value;
    const cantidadPasajes = parseInt(document.getElementById('cantidadPasajes').value) || 1;
    const equipajeExtra = parseInt(document.getElementById('equipajeExtra').value) || 0;
    const formaPago = document.getElementById('formaPago').value;

    let costoPorPasaje = tipoAsiento === 'ejecutivo' ? vueloSeleccionado.costoEjecutivo : vueloSeleccionado.costoTurista;
    let costoPasajes = costoPorPasaje * cantidadPasajes;
    let costoEquipaje = vueloSeleccionado.costoEquipaje * equipajeExtra;
    let descuento = formaPago === 'paquete' ? costoPasajes * 0.2 : 0; // 20% de descuento con paquete
    let total = costoPasajes + costoEquipaje - descuento;

    document.getElementById('costoPasajes').textContent = '$' + costoPasajes;
    document.getElementById('costoEquipaje').textContent = '$' + costoEquipaje;
    document.getElementById('descuentoPaquete').textContent = '-$' + descuento;
    document.getElementById('costoTotal').textContent = '$' + total;

    document.getElementById('resumenCostos').style.display = 'block';
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
        <p class="text-light"><strong>Vuelo:</strong> ${document.getElementById('nombreVueloDetalle').textContent}</p>
        <p class="text-light"><strong>Fecha:</strong> ${vueloSeleccionado.fecha}</p>
        <p class="text-light"><strong>Tipo de asiento:</strong> ${tipoAsiento}</p>
        <p class="text-light"><strong>Cantidad de pasajes:</strong> ${cantidadPasajes}</p>
        <p class="text-light"><strong>Equipaje extra:</strong> ${equipajeExtra} unidades</p>
        <p class="text-light"><strong>Forma de pago:</strong> ${formaPago === 'general' ? 'Pago General' : 'Pago con Paquete'}</p>
        <p class="text-light"><strong>Costo total:</strong> ${document.getElementById('costoTotal').textContent}</p>
    `;

    resumen.innerHTML = html;
}

function mostrarModalExito() {
    // Generar código de reserva aleatorio
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
// Validación del formulario y funcionalidad
document.addEventListener('DOMContentLoaded', function() {
    // Inicializar funcionalidad
    inicializarConsultaVuelo();
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
                        estado: 'Confirmado',
                        imagen: 'https://via.placeholder.com/600x300/3498db/ffffff?text=IB6012+Montevideo-Madrid',
                        tieneReservaCliente: true,
                        totalReservas: 45
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
                        estado: 'Confirmado',
                        imagen: 'https://via.placeholder.com/600x300/8e44ad/ffffff?text=ZL1502+Montevideo-Rio',
                        tieneReservaCliente: false,
                        totalReservas: 32
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
                        estado: 'Confirmado',
                        imagen: 'https://via.placeholder.com/600x300/e74c3c/ffffff?text=CN804+Panamá-NY',
                        tieneReservaCliente: false,
                        totalReservas: 28
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
                        estado: 'Confirmado',
                        imagen: 'https://via.placeholder.com/600x300/dc2626/ffffff?text=AA904+Miami-Cancún',
                        tieneReservaCliente: false,
                        totalReservas: 41
                    }
                }
            }
        }
    },
    'aerolineas': {
        rutas: {
            'AR2050': {
                nombre: 'Buenos Aires - Santiago',
                vuelos: {
                    'AR2050150': {
                        nombre: 'AR2050 - Vuelo 150',
                        fecha: '02/11/2024',
                        duracion: '2 horas 15 min',
                        horaSalida: '14:20',
                        horaLlegada: '16:35',
                        asientosTurista: 138,
                        asientosEjecutivo: 22,
                        estado: 'Confirmado',
                        imagen: 'https://via.placeholder.com/600x300/16a34a/ffffff?text=AR2050+Buenos+Aires-Santiago',
                        tieneReservaCliente: false,
                        totalReservas: 29
                    }
                }
            }
        }
    }
};

function inicializarConsultaVuelo() {
    // Elementos del DOM
    const aerolineaSelect = document.getElementById('aerolinea');
    const rutaSelect = document.getElementById('rutaVuelo');
    const vueloSelect = document.getElementById('vuelo');
    const resultadoConsulta = document.getElementById('resultadoConsulta');
    const formConsulta = document.getElementById('formConsultaVuelo');

    // Cargar rutas cuando se selecciona aerolínea
    aerolineaSelect.addEventListener('change', function() {
        const aerolinea = this.value;
        rutaSelect.innerHTML = '<option value="">Seleccione ruta...</option>';
        vueloSelect.innerHTML = '<option value="">Primero seleccione ruta</option>';
        vueloSelect.disabled = true;

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

        resultadoConsulta.style.display = 'none';
    });

    // Cargar vuelos cuando se selecciona ruta
    rutaSelect.addEventListener('change', function() {
        const aerolinea = aerolineaSelect.value;
        const ruta = this.value;
        vueloSelect.innerHTML = '<option value="">Seleccione vuelo...</option>';

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

        resultadoConsulta.style.display = 'none';
    });

    // Mostrar información del vuelo seleccionado
    formConsulta.addEventListener('submit', function(event) {
        event.preventDefault();
        event.stopPropagation();

        if (this.checkValidity()) {
            const aerolinea = aerolineaSelect.value;
            const ruta = rutaSelect.value;
            const vuelo = vueloSelect.value;

            if (aerolinea && ruta && vuelo && datosVuelos[aerolinea].rutas[ruta].vuelos[vuelo]) {
                const vueloData = datosVuelos[aerolinea].rutas[ruta].vuelos[vuelo];

                // Actualizar información en la interfaz
                document.getElementById('nombreVueloDetalle').textContent = vueloData.nombre;
                document.getElementById('aerolineaDetalle').textContent = aerolineaSelect.options[aerolineaSelect.selectedIndex].text;
                document.getElementById('rutaDetalle').textContent = `${ruta} - ${datosVuelos[aerolinea].rutas[ruta].nombre}`;
                document.getElementById('fechaVueloDetalle').textContent = vueloData.fecha;
                document.getElementById('duracionVueloDetalle').textContent = vueloData.duracion;
                document.getElementById('horaSalidaDetalle').textContent = vueloData.horaSalida;
                document.getElementById('horaLlegadaDetalle').textContent = vueloData.horaLlegada;
                document.getElementById('asientosTuristaDetalle').textContent = vueloData.asientosTurista;
                document.getElementById('asientosEjecutivoDetalle').textContent = vueloData.asientosEjecutivo;
                document.getElementById('estadoVueloDetalle').textContent = vueloData.estado;
                document.getElementById('imagenVueloDetalle').src = vueloData.imagen;
                document.getElementById('totalReservas').textContent = vueloData.totalReservas;

                // Mostrar/ocultar secciones según el tipo de usuario
                document.getElementById('infoAerolinea').style.display = 'none';
                document.getElementById('infoCliente').style.display = vueloData.tieneReservaCliente ? 'block' : 'none';
                document.getElementById('btnReservar').style.display = !vueloData.tieneReservaCliente ? 'block' : 'none';

                // Mostrar resultados con animación
                resultadoConsulta.style.display = 'block';
                resultadoConsulta.classList.add('fade-in');

                // Scroll a los resultados
                resultadoConsulta.scrollIntoView({ behavior: 'smooth' });
            }
        } else {
            event.stopPropagation();
        }

        this.classList.add('was-validated');
    });

    // Limpiar búsqueda
    document.getElementById('btnLimpiar').addEventListener('click', function() {
        resultadoConsulta.style.display = 'none';
        formConsulta.classList.remove('was-validated');

        // Resetear selects
        aerolineaSelect.value = '';
        rutaSelect.innerHTML = '<option value="">Primero seleccione aerolínea</option>';
        rutaSelect.disabled = true;
        vueloSelect.innerHTML = '<option value="">Primero seleccione ruta</option>';
        vueloSelect.disabled = true;
    });

    // Validación en tiempo real
    const camposRequeridos = formConsulta.querySelectorAll('[required]');
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
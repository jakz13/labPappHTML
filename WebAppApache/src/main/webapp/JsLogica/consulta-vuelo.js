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
        document.getElementById('resultadoConsulta').style.display = 'none';
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
        document.getElementById('resultadoConsulta').style.display = 'none';
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

    // Vuelo -> mostrar info detallada
    document.getElementById('vuelo').addEventListener('change', function() {
        const vuelo = this.value;
        const resultadoConsulta = document.getElementById('resultadoConsulta');

        if (vuelo) {
            fetch('api/vuelo?nombre=' + encodeURIComponent(vuelo))
                .then(res => res.json())
                .then(data => {
                    vueloSeleccionado = data;

                    // Actualizar información en la interfaz
                    document.getElementById('nombreVueloDetalle').textContent = data.nombre || '-';
                    document.getElementById('aerolineaDetalle').textContent = document.getElementById('aerolinea').options[document.getElementById('aerolinea').selectedIndex].text;
                    document.getElementById('rutaDetalle').textContent = document.getElementById('rutaVuelo').value;
                    document.getElementById('fechaVueloDetalle').textContent = data.fecha || '-';
                    document.getElementById('duracionVueloDetalle').textContent = data.duracion || '-';
                    document.getElementById('horaSalidaDetalle').textContent = data.horaSalida || '-';
                    document.getElementById('horaLlegadaDetalle').textContent = data.horaLlegada || '-';
                    document.getElementById('asientosTuristaDetalle').textContent = data.asientosTurista !== undefined ? data.asientosTurista : '-';
                    document.getElementById('asientosEjecutivoDetalle').textContent = data.asientosEjecutivo !== undefined ? data.asientosEjecutivo : '-';
                    document.getElementById('estadoVueloDetalle').textContent = data.estado || 'Confirmado';

                    // Mostrar resultados
                    resultadoConsulta.style.display = 'block';
                    resultadoConsulta.classList.add('fade-in');
                })
                .catch(() => {
                    resultadoConsulta.style.display = 'none';
                    vueloSeleccionado = null;
                });
        } else {
            resultadoConsulta.style.display = 'none';
            vueloSeleccionado = null;
        }
    });

    // Envío del formulario
    document.getElementById('formConsultaVuelo').addEventListener('submit', function(event) {
        event.preventDefault();
        event.stopPropagation();

        if (this.checkValidity()) {
            // La consulta ya se realiza automáticamente cuando se selecciona un vuelo
            console.log("Consulta realizada para:", vueloSeleccionado);
        } else {
            event.stopPropagation();
        }

        this.classList.add('was-validated');
    });

    // Botón limpiar
    document.getElementById('btnLimpiar').addEventListener('click', function() {
        limpiarFormulario();
    });
}

function inicializarValidacion() {
    const form = document.getElementById('formConsultaVuelo');
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

function limpiarFormulario() {
    const form = document.getElementById('formConsultaVuelo');
    const aerolineaSelect = document.getElementById('aerolinea');
    const rutaSelect = document.getElementById('rutaVuelo');
    const vueloSelect = document.getElementById('vuelo');
    const resultadoConsulta = document.getElementById('resultadoConsulta');

    form.classList.remove('was-validated');
    aerolineaSelect.value = '';
    rutaSelect.innerHTML = '<option value="">Primero seleccione aerolínea</option>';
    rutaSelect.disabled = true;
    vueloSelect.innerHTML = '<option value="">Primero seleccione ruta</option>';
    vueloSelect.disabled = true;
    resultadoConsulta.style.display = 'none';
    vueloSeleccionado = null;

    // Limpiar validación visual
    const campos = form.querySelectorAll('.is-valid, .is-invalid');
    campos.forEach(campo => {
        campo.classList.remove('is-valid', 'is-invalid');
    });
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
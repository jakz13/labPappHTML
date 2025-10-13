// src/main/webapp/JsLogica/consulta-vuelo.js

document.addEventListener('DOMContentLoaded', function() {
    inicializarConsultaVuelo();
});

function inicializarConsultaVuelo() {
    const aerolineaSelect = document.getElementById('aerolinea');
    const rutaSelect = document.getElementById('rutaVuelo');
    const vueloSelect = document.getElementById('vuelo');
    const resultadoConsulta = document.getElementById('resultadoConsulta');
    const formConsulta = document.getElementById('formConsultaVuelo');

    // Cargar aerolíneas
    fetch('/api/aerolineas')
        .then(res => res.json())
        .then(aerolineas => {
            aerolineaSelect.innerHTML = '<option value="">Seleccione aerolínea...</option>';
            aerolineas.forEach(a => {
                const option = document.createElement('option');
                option.value = a.nickname;
                option.textContent = a.nombre;
                aerolineaSelect.appendChild(option);
            });
        })
        .catch(() => mostrarMensajeError('Error al cargar aerolíneas'));

    aerolineaSelect.addEventListener('change', function() {
        const nickname = this.value;
        rutaSelect.innerHTML = '<option value="">Seleccione ruta...</option>';
        vueloSelect.innerHTML = '<option value="">Primero seleccione ruta</option>';
        rutaSelect.disabled = true;
        vueloSelect.disabled = true;
        resultadoConsulta.style.display = 'none';

        if (nickname) {
            fetch(`/api/rutas?nombreAerolinea=${encodeURIComponent(nickname)}`)
                .then(res => res.json())
                .then(rutas => {
                    rutaSelect.disabled = false;
                    rutas.forEach(r => {
                        const option = document.createElement('option');
                        option.value = r.nombre;
                        option.textContent = `${r.nombre} - ${r.descripcion}`;
                        rutaSelect.appendChild(option);
                    });
                })
                .catch(() => mostrarMensajeError('Error al cargar rutas'));
        }
    });

    rutaSelect.addEventListener('change', function() {
        const nombreRuta = this.value;
        vueloSelect.innerHTML = '<option value="">Seleccione vuelo...</option>';
        vueloSelect.disabled = true;
        resultadoConsulta.style.display = 'none';

        if (nombreRuta) {
            fetch(`/api/vuelos?nombreRuta=${encodeURIComponent(nombreRuta)}`)
                .then(res => res.json())
                .then(vuelos => {
                    vueloSelect.disabled = false;
                    vuelos.forEach(v => {
                        const option = document.createElement('option');
                        option.value = v.nombre;
                        option.textContent = `${v.nombre} - ${v.fecha}`;
                        vueloSelect.appendChild(option);
                    });
                })
                .catch(() => mostrarMensajeError('Error al cargar vuelos'));
        }
    });

    formConsulta.addEventListener('submit', function(event) {
        event.preventDefault();
        event.stopPropagation();

        if (this.checkValidity()) {
            const nombreRuta = rutaSelect.value;
            const nombreVuelo = vueloSelect.value;

            if (nombreRuta && nombreVuelo) {
                fetch(`/api/vuelos?nombreRuta=${encodeURIComponent(nombreRuta)}`)
                    .then(res => res.json())
                    .then(vuelos => {
                        const vueloData = vuelos.find(v => v.nombre === nombreVuelo);
                        if (!vueloData) {
                            mostrarMensajeError('No se encontró el vuelo seleccionado');
                            return;
                        }

                        document.getElementById('nombreVueloDetalle').textContent = vueloData.nombre;
                        document.getElementById('aerolineaDetalle').textContent = aerolineaSelect.options[aerolineaSelect.selectedIndex].text;
                        document.getElementById('rutaDetalle').textContent = nombreRuta;
                        document.getElementById('fechaVueloDetalle').textContent = vueloData.fecha || '';
                        document.getElementById('duracionVueloDetalle').textContent = vueloData.duracion || '';
                        document.getElementById('horaSalidaDetalle').textContent = vueloData.horaSalida || '';
                        document.getElementById('horaLlegadaDetalle').textContent = vueloData.horaLlegada || '';
                        document.getElementById('asientosTuristaDetalle').textContent = vueloData.asientosTurista || '';
                        document.getElementById('asientosEjecutivoDetalle').textContent = vueloData.asientosEjecutivo || '';
                        document.getElementById('estadoVueloDetalle').textContent = vueloData.estado || 'Confirmado';
                        document.getElementById('imagenVueloDetalle').src = vueloData.imagen || '';
                        document.getElementById('totalReservas').textContent = vueloData.reservas ? vueloData.reservas.length : 0;

                        document.getElementById('infoAerolinea').style.display = 'none';
                        document.getElementById('infoCliente').style.display = 'none';
                        document.getElementById('btnReservar').style.display = 'block';

                        resultadoConsulta.style.display = 'block';
                        resultadoConsulta.classList.add('fade-in');
                        resultadoConsulta.scrollIntoView({ behavior: 'smooth' });
                    })
                    .catch(() => mostrarMensajeError('Error al consultar vuelo'));
            }
        }
        this.classList.add('was-validated');
    });

    document.getElementById('btnLimpiar').addEventListener('click', function() {
        resultadoConsulta.style.display = 'none';
        formConsulta.classList.remove('was-validated');
        aerolineaSelect.value = '';
        rutaSelect.innerHTML = '<option value="">Primero seleccione aerolínea</option>';
        rutaSelect.disabled = true;
        vueloSelect.innerHTML = '<option value="">Primero seleccione ruta</option>';
        vueloSelect.disabled = true;
    });

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
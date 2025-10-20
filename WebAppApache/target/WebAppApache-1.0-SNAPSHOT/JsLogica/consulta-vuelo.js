// Variables globales
let vueloSeleccionado = null;
let usuarioInfo = null;

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

                    // Verificar permisos del usuario y mostrar secciones correspondientes
                    verificarPermisosUsuario(vuelo, document.getElementById('aerolinea').value);

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

// Verificar permisos del usuario para el vuelo
function verificarPermisosUsuario(nombreVuelo, aerolineaSeleccionada) {
    fetch(`api/verificar-permisos-vuelo?nombreVuelo=${encodeURIComponent(nombreVuelo)}&aerolinea=${encodeURIComponent(aerolineaSeleccionada)}`)
        .then(res => res.json())
        .then(data => {
            usuarioInfo = data;
            mostrarSeccionesUsuario(data, nombreVuelo);
        })
        .catch(err => {
            console.error("Error verificando permisos:", err);
            // Por defecto, mostrar como usuario no autenticado
            usuarioInfo = { autenticado: false };
            mostrarSeccionesUsuario({ autenticado: false }, nombreVuelo);
        });
}

// Mostrar secciones según el tipo de usuario
function mostrarSeccionesUsuario(usuarioData, nombreVuelo) {
    const infoAerolinea = document.getElementById('infoAerolinea');
    const infoCliente = document.getElementById('infoCliente');
    const btnReservar = document.getElementById('btnReservar');

    // Ocultar todas las secciones primero
    infoAerolinea.style.display = 'none';
    infoCliente.style.display = 'none';
    btnReservar.style.display = 'none';

    if (usuarioData.autenticado) {
        if (usuarioData.esAerolineaDueña) {
            // Es la aerolínea que publicó el vuelo - mostrar gestión de reservas
            infoAerolinea.style.display = 'block';
            cargarReservasVuelo(nombreVuelo);
        } else if (usuarioData.tieneReservaCliente) {
            // Es un cliente con reserva en este vuelo
            infoCliente.style.display = 'block';
        } else {
            // Usuario autenticado pero sin reserva - mostrar opción de reserva
            btnReservar.style.display = 'block';
        }
    } else {
        // Usuario no autenticado - mostrar opción de reserva
        btnReservar.style.display = 'block';
    }
}

// Cargar reservas del vuelo (para aerolíneas)
function cargarReservasVuelo(nombreVuelo) {
    fetch(`api/reservas-vuelo?nombreVuelo=${encodeURIComponent(nombreVuelo)}`)
        .then(res => res.json())
        .then(reservas => {
            const totalReservas = document.getElementById('totalReservas');
            totalReservas.textContent = reservas.length;

            // Actualizar el botón para mostrar detalles
            const btnVerReservas = infoAerolinea.querySelector('button');
            btnVerReservas.onclick = function() {
                mostrarDetallesReservas(reservas);
            };
        })
        .catch(err => {
            console.error("Error cargando reservas:", err);
            document.getElementById('totalReservas').textContent = '0';
        });
}

// Mostrar modal con detalles de reservas
function mostrarDetallesReservas(reservas) {
    // Crear contenido para el modal
    let contenido = `
        <div class="table-responsive">
            <table class="table table-dark table-striped">
                <thead>
                    <tr>
                        <th>ID Reserva</th>
                        <th>Cliente</th>
                        <th>Tipo Asiento</th>
                        <th>Cantidad</th>
                        <th>Costo</th>
                    </tr>
                </thead>
                <tbody>
    `;

    reservas.forEach(reserva => {
        contenido += `
            <tr>
                <td>${reserva.id}</td>
                <td>${reserva.cliente}</td>
                <td>${reserva.tipoAsiento}</td>
                <td>${reserva.cantidadPasajes}</td>
                <td>$${reserva.costo}</td>
            </tr>
        `;
    });

    contenido += `
                </tbody>
            </table>
        </div>
        <div class="mt-3">
            <strong>Total de reservas:</strong> ${reservas.length}
        </div>
    `;

    // Mostrar modal (puedes usar Bootstrap modal o crear uno simple)
    alert(`Detalles de Reservas:\n\n${contenido.replace(/<[^>]*>/g, '')}`);
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
    usuarioInfo = null;

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
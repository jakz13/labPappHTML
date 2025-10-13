// Variable global para guardar las rutas cargadas
let rutasData = {};

document.addEventListener('DOMContentLoaded', function() {
    const selectRuta = document.getElementById('rutaVuelo');
    const formulario = document.getElementById('formAltaVuelo');

    // ...
    fetch('listarRutas')
        .then(res => res.json())
        .then(rutas => {
            console.log("Rutas recibidas del backend:", rutas); // <-- Agrega esta línea
            rutasData = {}; // Limpiar antes de cargar
            selectRuta.innerHTML = '<option value="">Seleccione una ruta...</option>';
            rutas.forEach(ruta => {
                rutasData[ruta.nombre] = ruta; // Guardar cada ruta por nombre
                selectRuta.innerHTML += `<option value="${ruta.nombre}">${ruta.nombre} - ${ruta.descripcion}</option>`;
            });
        })
        .catch(() => {
            selectRuta.innerHTML = '<option value="">Error cargando rutas</option>';
        });
// ...


    // Actualizar información de la ruta seleccionada
    selectRuta.addEventListener('change', function() {
        const rutaSeleccionada = this.value;
        const infoRuta = document.getElementById('infoRuta');
        const detallesRuta = document.getElementById('detallesRuta');

        if (rutaSeleccionada && rutasData[rutaSeleccionada]) {
            const ruta = rutasData[rutaSeleccionada];
            detallesRuta.innerHTML = `
                <p><strong>Ruta:</strong> ${ruta.nombre}</p>
                <p><strong>Descripción:</strong> ${ruta.descripcion}</p>
                <!-- Agrega más campos si tu backend los devuelve -->
            `;
            infoRuta.classList.remove('d-none');
        } else {
            infoRuta.classList.add('d-none');
        }
    });

    // Validación del formulario y envío al backend
    formulario.addEventListener('submit', function(event) {
        event.preventDefault();

        if (this.checkValidity()) {
            // Recolectar datos
            const datosVuelo = {
                nombreVuelo: document.getElementById('nombreVuelo').value,
                nombreAerolinea: "cos", // Ajusta según corresponda
                nombreRuta: document.getElementById('rutaVuelo').value,
                fecha: document.getElementById('fechaVuelo').value,
                duracion: (parseInt(document.getElementById('horas').value) * 60 + parseInt(document.getElementById('minutos').value)).toString(),
                asientosTurista: document.getElementById('asientosTurista').value,
                asientosEjecutivo: document.getElementById('asientosEjecutivo').value
            };

            fetch('altaVuelo', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams(datosVuelo)
            })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        mostrarMensajeExito();
                        setTimeout(() => {
                            formulario.reset();
                            formulario.classList.remove('was-validated');
                            document.getElementById('infoRuta').classList.add('d-none');
                        }, 2000);
                    } else {
                        alert("Error: " + data.error);
                    }
                })
                .catch(err => {
                    alert("Error de red o servidor: " + err);
                });
        } else {
            event.stopPropagation();
        }

        this.classList.add('was-validated');
    });

    // Validar que la fecha sea futura
    document.getElementById('fechaVuelo').min = new Date().toISOString().split('T')[0];

    // Validación en tiempo real para campos requeridos
    const camposRequeridos = formulario.querySelectorAll('[required]');
    camposRequeridos.forEach(campo => {
        campo.addEventListener('input', function() {
            if (this.value.trim()) {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
            } else {
                this.classList.remove('is-valid');
                this.classList.add('is-invalid');
            }
        });
    });

    // Validación específica para duración
    const horasInput = document.getElementById('horas');
    const minutosInput = document.getElementById('minutos');

    function validarDuracion() {
        const horas = parseInt(horasInput.value) || 0;
        const minutos = parseInt(minutosInput.value) || 0;

        if (horas === 0 && minutos === 0) {
            horasInput.classList.add('is-invalid');
            minutosInput.classList.add('is-invalid');
        } else {
            horasInput.classList.remove('is-invalid');
            minutosInput.classList.remove('is-invalid');
            horasInput.classList.add('is-valid');
            minutosInput.classList.add('is-valid');
        }
    }

    horasInput.addEventListener('input', validarDuracion);
    minutosInput.addEventListener('input', validarDuracion);
});

function mostrarMensajeExito() {
    // Crear modal de éxito dinámicamente si no existe
    if (!document.getElementById('successModal')) {
        const modalHTML = `
            <div class="modal fade" id="successModal" tabindex="-1" aria-labelledby="successModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header bg-success text-white">
                            <h5 class="modal-title" id="successModalLabel">Vuelo Creado Exitosamente</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body text-center">
                            <div class="mb-3">
                                <i class="bi bi-check-circle-fill text-success" style="font-size: 3rem;"></i>
                            </div>
                            <h5>¡Vuelo Registrado!</h5>
                            <p>El vuelo ha sido creado exitosamente y está ahora disponible en el sistema.</p>
                            <p><strong>Próximo paso:</strong> Los clientes podrán ver este vuelo y realizar reservas.</p>
                        </div>
                        <div class="modal-footer justify-content-center">
                            <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Continuar</button>
                            <a href="/alta-vuelo.jsp" class="btn btn-outline-primary">Crear Otro Vuelo</a>
                        </div>
                    </div>
                </div>
            </div>
        `;
        document.body.insertAdjacentHTML('beforeend', modalHTML);
    }

    // Mostrar modal
    const successModal = new bootstrap.Modal(document.getElementById('successModal'));
    successModal.show();
}

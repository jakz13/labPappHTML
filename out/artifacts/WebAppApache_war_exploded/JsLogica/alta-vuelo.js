// Datos de ejemplo de rutas
const rutasData = {
    'ZL1502': {
        nombre: 'Montevideo - Rio de Janeiro',
        descripcion: 'Vuelo directo con duración aproximada de 2 horas 30 minutos',
        costo_turista: 75,
        costo_ejecutivo: 190,
        origen: 'Montevideo (MVD)',
        destino: 'Rio de Janeiro (GIG)'
    },
    'ZL2001': {
        nombre: 'Montevideo - São Paulo',
        descripcion: 'Vuelo directo con duración aproximada de 3 horas',
        costo_turista: 80,
        costo_ejecutivo: 200,
        origen: 'Montevideo (MVD)',
        destino: 'São Paulo (GRU)'
    },
    'ZL3005': {
        nombre: 'Buenos Aires - Santiago',
        descripcion: 'Vuelo directo cruzando la cordillera de los Andes',
        costo_turista: 120,
        costo_ejecutivo: 280,
        origen: 'Buenos Aires (EZE)',
        destino: 'Santiago (SCL)'
    },
    'ZL4002': {
        nombre: 'Lima - Bogotá',
        descripcion: 'Vuelo internacional con servicio completo a bordo',
        costo_turista: 150,
        costo_ejecutivo: 320,
        origen: 'Lima (LIM)',
        destino: 'Bogotá (BOG)'
    }
};

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    const formulario = document.getElementById('formAltaVuelo');

    // Actualizar información de la ruta seleccionada
    document.getElementById('rutaVuelo').addEventListener('change', function() {
        const rutaSeleccionada = this.value;
        const infoRuta = document.getElementById('infoRuta');
        const detallesRuta = document.getElementById('detallesRuta');

        if (rutaSeleccionada && rutasData[rutaSeleccionada]) {
            const ruta = rutasData[rutaSeleccionada];
            detallesRuta.innerHTML = `
                <p><strong>Ruta:</strong> ${ruta.nombre}</p>
                <p><strong>Origen:</strong> ${ruta.origen}</p>
                <p><strong>Destino:</strong> ${ruta.destino}</p>
                <p><strong>Descripción:</strong> ${ruta.descripcion}</p>
                <p><strong>Costos base:</strong> Turista: $${ruta.costo_turista} | Ejecutivo: $${ruta.costo_ejecutivo}</p>
            `;
            infoRuta.classList.remove('d-none');
        } else {
            infoRuta.classList.add('d-none');
        }
    });

    // Validación del formulario
    formulario.addEventListener('submit', function(event) {
        event.preventDefault();

        if (this.checkValidity()) {
            // Simular envío exitoso
            mostrarMensajeExito();

            // Aquí iría la lógica real para enviar al servidor
            const datosVuelo = {
                ruta: document.getElementById('rutaVuelo').value,
                nombre: document.getElementById('nombreVuelo').value,
                fecha: document.getElementById('fechaVuelo').value,
                duracion: `${document.getElementById('horas').value}h ${document.getElementById('minutos').value}m`,
                asientos_turista: document.getElementById('asientosTurista').value,
                asientos_ejecutivo: document.getElementById('asientosEjecutivo').value,
                imagen: document.getElementById('imagenVuelo').files[0]?.name || 'No seleccionada'
            };

            console.log('Datos del vuelo a guardar:', datosVuelo);

            // Limpiar formulario después de éxito
            setTimeout(() => {
                this.reset();
                this.classList.remove('was-validated');
                document.getElementById('infoRuta').classList.add('d-none');
            }, 2000);

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
            minutosInput.classList.add('is-valid');
            minutosInput.classList.remove('is-invalid');
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
                            <a href="alta-vuelo.html" class="btn btn-outline-primary">Crear Otro Vuelo</a>
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
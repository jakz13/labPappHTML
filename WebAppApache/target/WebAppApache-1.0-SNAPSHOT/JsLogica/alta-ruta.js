// Validación del formulario y envío al backend
document.addEventListener('DOMContentLoaded', function() {
    const formulario = document.getElementById('formAltaRuta');
    const selectCategorias = document.getElementById('categorias');

    // Cargar categorías desde el backend
    fetch('listarCategorias')
        .then(res => res.json())
        .then(categorias => {
            selectCategorias.innerHTML = '';
            categorias.forEach(cat => {
                const option = document.createElement('option');
                option.value = cat.nombre;
                option.textContent = cat.nombre;
                selectCategorias.appendChild(option);
            });
        });

    formulario.addEventListener('submit', function(event) {
        event.preventDefault();

        if (this.checkValidity()) {
            const datosRuta = {
                nombre: document.getElementById('nombreRuta').value,
                descripcion: document.getElementById('descripcionCorta').value,
                descripcionDetallada: document.getElementById('descripcion').value,
                hora: document.getElementById('hora').value,
                costoTurista: document.getElementById('costoTurista').value,
                costoEjecutivo: document.getElementById('costoEjecutivo').value,
                costoEquipaje: document.getElementById('costoEquipaje').value,
                origen: document.getElementById('origen').value,
                destino: document.getElementById('destino').value,
                categorias: Array.from(selectCategorias.selectedOptions).map(option => option.value)
            };

            // Construir los parámetros para x-www-form-urlencoded
            const params = new URLSearchParams();
            params.append('nombre', datosRuta.nombre);
            params.append('descripcion', datosRuta.descripcion);
            params.append('descripcionDetallada', datosRuta.descripcionDetallada);
            params.append('hora', datosRuta.hora);
            params.append('costoTurista', datosRuta.costoTurista);
            params.append('costoEjecutivo', datosRuta.costoEjecutivo);
            params.append('costoEquipaje', datosRuta.costoEquipaje);
            params.append('origen', datosRuta.origen);
            params.append('destino', datosRuta.destino);
            datosRuta.categorias.forEach(cat => params.append('categorias', cat));
            // Si tienes la aerolínea por sesión, puedes agregarla aquí:
            // params.append('aerolinea', 'latam001');

            fetch('altaRuta', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params
            })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        mostrarMensajeExito();
                        formulario.reset();
                        formulario.classList.remove('was-validated');
                    } else {
                        alert('Error: ' + (data.error || 'No se pudo crear la ruta.'));
                    }
                })
                .catch(() => {
                    alert('Error de conexión con el servidor.');
                });
        } else {
            event.stopPropagation();
        }

        this.classList.add('was-validated');
    });

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

    // Validación para select múltiple
    selectCategorias.addEventListener('change', function() {
        if (this.selectedOptions.length > 0) {
            this.classList.remove('is-invalid');
            this.classList.add('is-valid');
        } else {
            this.classList.remove('is-valid');
            this.classList.add('is-invalid');
        }
    });

    // Agregar texto de ayuda
    const ayudaTexto = document.createElement('div');
    ayudaTexto.className = 'form-text text-light mt-1';
    ayudaTexto.textContent = 'Mantenga Ctrl (Cmd en Mac) para seleccionar múltiples categorías';
    selectCategorias.parentNode.appendChild(ayudaTexto);
});

function mostrarMensajeExito() {
    // Crear modal de éxito dinámicamente
    const modalHTML = `
        <div class="modal fade" id="successModal" tabindex="-1" aria-labelledby="successModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title" id="successModalLabel">¡Ruta Creada Exitosamente!</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body text-center">
                        <div class="mb-3">
                            <i class="bi bi-check-circle-fill text-success" style="font-size: 3rem;"></i>
                        </div>
                        <h5>Ruta de Vuelo Registrada</h5>
                        <p>La ruta ha sido creada exitosamente y será revisada por el administrador.</p>
                    </div>
                    <div class="modal-footer justify-content-center">
                        <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Continuar</button>
                        <a href="/consulta-ruta.jsp" class="btn btn-outline-primary">Ver Rutas</a>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Agregar modal al documento si no existe
    if (!document.getElementById('successModal')) {
        document.body.insertAdjacentHTML('beforeend', modalHTML);
    }

    // Mostrar modal
    const successModal = new bootstrap.Modal(document.getElementById('successModal'));
    successModal.show();
}

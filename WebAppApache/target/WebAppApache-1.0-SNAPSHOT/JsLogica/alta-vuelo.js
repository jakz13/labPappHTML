// Variable global para guardar las rutas cargadas
let rutasData = {};

document.addEventListener('DOMContentLoaded', function() {
    const selectRuta = document.getElementById('rutaVuelo');
    const formulario = document.getElementById('formAltaVuelo');

    // Cargar sesión y rutas inicialmente
    function loadSessionAndRutas() {
        const base = window.SESSION_API_BASE || '';
        // Si session-manager ya resolvió la sesión, usarla y evitar una segunda petición
        if (window.CURRENT_SESSION) {
            const session = window.CURRENT_SESSION;
            console.log('Usando window.CURRENT_SESSION (alta-vuelo):', session);
            procesarSessionYCargarRutas(session, base);
            return;
        }

        // comprobar sesión
        fetch(base + '/api/check-session', { credentials: 'include' })
            .then(res => {
                if (!res.ok) throw new Error('HTTP ' + res.status);
                const ct = res.headers.get('content-type') || '';
                if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
                return res.json();
            })
            .then(session => {
                console.log('Session info (alta-vuelo):', session);
                procesarSessionYCargarRutas(session, base);
            })
            .catch(err => {
                console.error('Error verificando sesión:', err);
                const aerolineaNameEl = document.getElementById('aerolineaName');
                if (aerolineaNameEl) aerolineaNameEl.textContent = '(no autenticado)';
                selectRuta.innerHTML = '<option value="">Error verificando sesión</option>';
            });
    }

    // Extraer la lógica de procesamiento de session y carga de rutas para reutilizarla
    function procesarSessionYCargarRutas(session, base) {
        console.log('Procesando session y cargando rutas (alta-vuelo):', session);
        const aerolineaNameEl = document.getElementById('aerolineaName');
        if (session && session.authenticated && session.tipo === 'aerolinea') {
            if (aerolineaNameEl) aerolineaNameEl.textContent = session.nickname;
            // cargar rutas para la aerolínea logueada
            fetch(base + '/listarRutas', { credentials: 'include' })
                .then(res => {
                    if (!res.ok) throw new Error('HTTP ' + res.status);
                    const ct = res.headers.get('content-type') || '';
                    if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
                    return res.json();
                })
                .then(rutas => {
                    console.log('Rutas recibidas del backend:', rutas);
                    rutasData = {};
                    selectRuta.innerHTML = '<option value="">Seleccione una ruta...</option>';
                    rutas.forEach(ruta => {
                        rutasData[ruta.nombre] = ruta;
                        selectRuta.innerHTML += `<option value="${ruta.nombre}">${ruta.nombre} - ${ruta.descripcion}</option>`;
                    });
                })
                .catch(err => {
                    console.error('Error cargando rutas:', err);
                    selectRuta.innerHTML = '<option value="">Error cargando rutas</option>';
                });
        } else {
            // FALLBACK: intentar usar localStorage (p. ej. después de registro) si existe
            const storedNick = localStorage.getItem('session_nickname');
            const storedTipo = localStorage.getItem('session_tipo');
            if (storedNick && storedTipo === 'aerolinea') {
                console.log('Usando fallback localStorage session_nickname:', storedNick);
                if (aerolineaNameEl) aerolineaNameEl.textContent = storedNick;
                // llamar listarRutas con parámetro aerolinea como fallback
                fetch(base + '/listarRutas?aerolinea=' + encodeURIComponent(storedNick))
                    .then(res => {
                        if (!res.ok) throw new Error('HTTP ' + res.status);
                        const ct = res.headers.get('content-type') || '';
                        if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
                        return res.json();
                    })
                    .then(rutas => {
                        console.log('Rutas recibidas (fallback):', rutas);
                        rutasData = {};
                        selectRuta.innerHTML = '<option value="">Seleccione una ruta...</option>';
                        rutas.forEach(ruta => {
                            rutasData[ruta.nombre] = ruta;
                            selectRuta.innerHTML += `<option value="${ruta.nombre}">${ruta.nombre} - ${ruta.descripcion}</option>`;
                        });
                    })
                    .catch(err => {
                        console.error('Error cargando rutas (fallback):', err);
                        selectRuta.innerHTML = '<option value="">Error cargando rutas</option>';
                    });
            } else {
                if (aerolineaNameEl) aerolineaNameEl.textContent = '(no autenticado)';
                // mostrar mensaje para iniciar sesión
                selectRuta.innerHTML = '<option value="">Inicie sesión como aerolínea</option>';
            }
        }
    }

    // cargar al inicio
    loadSessionAndRutas();

    // refrescar cuando la sesión cambie
    window.addEventListener('sessionUpdated', () => {
        loadSessionAndRutas();
    });

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

    formulario.addEventListener('submit', function(event) {
        event.preventDefault();

        if (this.checkValidity()) {
            // calcular duración en minutos y guardarla en el input hidden
            const horas = parseInt(document.getElementById('horas').value) || 0;
            const minutos = parseInt(document.getElementById('minutos').value) || 0;
            const duracionMin = horas * 60 + minutos;
            document.getElementById('duracion').value = String(duracionMin);

            // Mostrar indicador de carga
            const submitBtn = this.querySelector('button[type="submit"]');
            const originalText = submitBtn.innerHTML;
            submitBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Procesando...';
            submitBtn.disabled = true;

            // Construir FormData (multipart) para enviar imagen y campos
            const formData = new FormData();
            formData.append('nombreVuelo', document.getElementById('nombreVuelo').value);
            formData.append('nombreRuta', document.getElementById('rutaVuelo').value);
            formData.append('fecha', document.getElementById('fechaVuelo').value);
            formData.append('duracion', String(duracionMin));
            formData.append('asientosTurista', document.getElementById('asientosTurista').value);
            formData.append('asientosEjecutivo', document.getElementById('asientosEjecutivo').value);

            const imagenInput = document.getElementById('imagenVuelo');
            if (imagenInput && imagenInput.files && imagenInput.files.length > 0) {
                formData.append('imagenVuelo', imagenInput.files[0]);
            }

            // usar base para asegurar la URL absoluta correcta
            fetch((window.SESSION_API_BASE || '') + '/altaVuelo', {
                method: 'POST',
                body: formData,
                credentials: 'include'
            })
                .then(async res => {
                    const data = await res.json();

                    if (!res.ok) {
                        // Si hay error HTTP pero el servidor devolvió JSON con mensaje
                        if (data && data.error) {
                            throw new Error(data.error);
                        } else {
                            throw new Error(`Error del servidor: ${res.status}`);
                        }
                    }
                    return data;
                })
                .then(data => {
                    if (data.success) {
                        mostrarMensajeExito();
                        setTimeout(() => {
                            formulario.reset();
                            formulario.classList.remove('was-validated');
                            document.getElementById('infoRuta').classList.add('d-none');
                        }, 2000);
                    } else {
                        mostrarError(data.error || "Error desconocido al crear el vuelo");
                    }
                })
                .catch(err => {
                    // Usar nuestra función elegante de mostrar error, no alert() nativo
                    mostrarError(err.message || "Error de red o servidor");
                })
                .finally(() => {
                    // Restaurar botón
                    submitBtn.innerHTML = originalText;
                    submitBtn.disabled = false;
                });
        } else {
            event.stopPropagation();
        }

        this.classList.add('was-validated');
    });


    // Función para mostrar errores en un cartel arriba a la izquierda
    function mostrarError(mensaje) {
        // Remover alertas anteriores si existen
        const alertasAnteriores = document.querySelectorAll('.alert-error-alta-vuelo');
        alertasAnteriores.forEach(alerta => alerta.remove());

        // Crear nueva alerta más destacada
        const alertaHTML = `
        <div class="alert alert-danger alert-error-alta-vuelo alert-dismissible fade show mb-4" role="alert">
            <div class="d-flex align-items-center">
                <i class="bi bi-exclamation-octagon-fill me-3 fs-5"></i>
                <div class="flex-grow-1">
                    <h6 class="alert-heading mb-1">No se pudo crear el vuelo</h6>
                    <p class="mb-0 small">${mensaje}</p>
                </div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </div>
    `;

        // Insertar la alerta al principio del card-body (arriba de todo)
        const cardBody = document.querySelector('.card-body');
        if (cardBody) {
            cardBody.insertAdjacentHTML('afterbegin', alertaHTML);

            // Hacer scroll suave hasta el error para que el usuario lo vea
            alertaHTML.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }

        // Auto-eliminar después de 10 segundos
        setTimeout(() => {
            const alerta = document.querySelector('.alert-error-alta-vuelo');
            if (alerta) {
                const bsAlert = new bootstrap.Alert(alerta);
                bsAlert.close();
            }
        }, 10000);
    }

    // También mejora la función de éxito para que sea consistente:
    function mostrarMensajeExito() {
        // Remover alertas anteriores si existen
        const alertasAnteriores = document.querySelectorAll('.alert-error-alta-vuelo');
        alertasAnteriores.forEach(alerta => alerta.remove());

        // Crear alerta de éxito mejorada
        const alertaHTML = `
        <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
            <div class="d-flex align-items-center">
                <i class="bi bi-check-circle-fill me-3 fs-5"></i>
                <div class="flex-grow-1">
                    <h6 class="alert-heading mb-1">¡Vuelo creado exitosamente!</h6>
                    <p class="mb-0 small">El vuelo ha sido registrado en el sistema y está disponible para reservas.</p>
                </div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </div>
    `;

        const cardBody = document.querySelector('.card-body');
        if (cardBody) {
            cardBody.insertAdjacentHTML('afterbegin', alertaHTML);

            // Hacer scroll suave hasta el éxito
            alertaHTML.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }

        // Auto-eliminar después de 6 segundos
        setTimeout(() => {
            const alerta = document.querySelector('.alert-success');
            if (alerta) {
                const bsAlert = new bootstrap.Alert(alerta);
                bsAlert.close();
            }
        }, 6000);
    }

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
// javascript
// javascript
document.addEventListener('DOMContentLoaded', function() {
    const formulario = document.getElementById('formAltaRuta');
    const selectCategorias = document.getElementById('categorias');
    const selectOrigen = document.getElementById('origen');
    const selectDestino = document.getElementById('destino');

    function setDefaultSelect(select, label) {
        if (!select) return;
        select.innerHTML = `<option value="" selected disabled>${label}</option>`;
    }

    // Cargar categorías (manejando lista de strings o lista de objetos {nombre:...})
    fetch('listarCategorias')
        .then(res => {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            return res.json();
        })
        .then(categorias => {
            setDefaultSelect(selectCategorias, 'Seleccione categorías (Ctrl/Cmd + click para múltiples)');
            if (!Array.isArray(categorias) || categorias.length === 0) {
                console.warn('No se recibieron categorías.');
                return;
            }
            categorias.forEach(cat => {
                const nombre = (typeof cat === 'string') ? cat : (cat && (cat.nombre || cat.name) ? (cat.nombre || cat.name) : null);
                if (!nombre) return;
                const option = document.createElement('option');
                option.value = nombre;
                option.textContent = nombre;
                selectCategorias.appendChild(option);
            });
        })
        .catch(err => {
            console.error('Error cargando categorias:', err);
            setDefaultSelect(selectCategorias, 'No se pudieron cargar categorías');
        });

    // Cargar ciudades desde el backend y poblar selects origen/destino
    fetch('listarCiudades')
        .then(res => {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            return res.json();
        })
        .then(ciudades => {
            console.debug('listarCiudades ->', ciudades);
            setDefaultSelect(selectOrigen, 'Seleccione ciudad de origen');
            setDefaultSelect(selectDestino, 'Seleccione ciudad de destino');
            if (!Array.isArray(ciudades) || ciudades.length === 0) {
                console.warn('No se recibieron ciudades.');
                return;
            }
            ciudades.forEach(ciudad => {
                const nombre = (typeof ciudad === 'string') ? ciudad : (ciudad && (ciudad.nombre || ciudad.name) ? (ciudad.nombre || ciudad.name) : null);
                if (!nombre) return;
                const opt = document.createElement('option');
                opt.value = nombre;
                opt.textContent = nombre;
                // clonar para cada select
                if (selectOrigen) selectOrigen.appendChild(opt.cloneNode(true));
                if (selectDestino) selectDestino.appendChild(opt.cloneNode(true));
            });
        })
        .catch(err => {
            console.error('Error cargando ciudades:', err);
            setDefaultSelect(selectOrigen, 'No se pudieron cargar ciudades');
            setDefaultSelect(selectDestino, 'No se pudieron cargar ciudades');
        });

    formulario.addEventListener('submit', function(event) {
        event.preventDefault();

        if (this.checkValidity()) {
            const formData = new FormData();
            formData.append('nombre', document.getElementById('nombreRuta').value);
            formData.append('descripcion', document.getElementById('descripcionCorta').value);
            formData.append('descripcionDetallada', document.getElementById('descripcion').value);
            // Validar y agregar videoUrl
            const videoUrl = document.getElementById('videoRuta').value.trim();
            if (videoUrl) {
                // Validación básica de URL de video
                if (!esUrlVideoValida(videoUrl)) {
                    alert('Por favor ingrese una URL de video válida (YouTube, Vimeo o enlace directo a archivo de video).');
                    document.getElementById('videoRuta').focus();
                    return;
                }
                formData.append('videoUrl', videoUrl);
            }
            formData.append('hora', document.getElementById('hora').value);
            formData.append('costoTurista', document.getElementById('costoTurista').value);
            formData.append('costoEjecutivo', document.getElementById('costoEjecutivo').value);
            formData.append('costoEquipaje', document.getElementById('costoEquipaje').value);
            formData.append('origen', document.getElementById('origen').value);
            formData.append('destino', document.getElementById('destino').value);

            const categorias = Array.from(selectCategorias.selectedOptions).map(option => option.value);
            categorias.forEach(cat => formData.append('categorias', cat));

            const imagenInput = document.getElementById('imagenRuta');
            if (imagenInput && imagenInput.files && imagenInput.files[0]) {
                const file = imagenInput.files[0];
                formData.append('imagenRuta', file, file.name);
            }

            fetch('altaRuta', {
                method: 'POST',
                credentials: 'include',
                body: formData
            })
                .then(async res => {
                    let data;
                    try { data = await res.json(); } catch (e) { data = { success: false, error: 'Respuesta inválida del servidor' }; }
                    if (res.ok && data && data.success) {
                        console.log('Ruta creada exitosamente. Datos:', data);
                        
                        // Limpiar preview de imagen
                        const imgPrev = document.getElementById('imagenPreviewRuta');
                        if (imgPrev) {
                            imgPrev.classList.add('d-none');
                            imgPrev.src = '';
                        }
                        
                        mostrarMensajeExito();
                        formulario.reset();
                        formulario.classList.remove('was-validated');
                    } else {
                        if (res.status === 409) {
                            const volver = confirm((data && data.error) ? (data.error + '\n\n¿Desea reingresar los datos?') : 'Ya existe una ruta con ese nombre.\n\n¿Desea reingresar los datos?');
                            if (volver) document.getElementById('nombreRuta').focus();
                            else window.location.href = '/PaginaPrincipal.jsp';
                        } else {
                            alert('Error: ' + (data && data.error ? data.error : 'No se pudo crear la ruta.'));
                        }
                    }
                })
                .catch(err => {
                    console.error('Error en fetch altaRuta:', err);
                    alert('Error de conexión con el servidor.');
                });
        } else {
            event.stopPropagation();
        }

        this.classList.add('was-validated');
    });


// Función auxiliar para validar URLs de video
    function esUrlVideoValida(url) {
        // YouTube
        const youtubeRegex = /^(https?:\/\/)?(www\.)?(youtube\.com\/watch\?v=|youtu\.be\/)([a-zA-Z0-9_-]{11})/;
        // Vimeo
        const vimeoRegex = /^(https?:\/\/)?(www\.)?vimeo\.com\/(\d+)/;
        // Enlaces directos a archivos de video
        const videoFileRegex = /\.(mp4|webm|ogg|mov|avi|wmv)(\?.*)?$/i;
        // URLs genéricas (para otros servicios)
        const urlRegex = /^https?:\/\/.+\..+/;

        return youtubeRegex.test(url) || vimeoRegex.test(url) || videoFileRegex.test(url) || urlRegex.test(url);
    }

// Agregar validación en tiempo real para el campo de video
    const videoInput = document.getElementById('videoRuta');
    if (videoInput) {
        videoInput.addEventListener('input', function() {
            const url = this.value.trim();
            if (url === '') {
                this.classList.remove('is-valid', 'is-invalid');
                return;
            }

            if (esUrlVideoValida(url)) {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
            } else {
                this.classList.remove('is-valid');
                this.classList.add('is-invalid');
            }
        });

        // También validar al perder el foco
        videoInput.addEventListener('blur', function() {
            const url = this.value.trim();
            if (url && !esUrlVideoValida(url)) {
                this.classList.add('is-invalid');
            }
        });
    }

    // Validaciones en tiempo real
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

    const selectCats = document.getElementById('categorias');
    if (selectCats) {
        selectCats.addEventListener('change', function() {
            if (this.selectedOptions.length > 0) {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
            } else {
                this.classList.remove('is-valid');
                this.classList.add('is-invalid');
            }
        });
    }

    // Preview local al seleccionar archivo (igual que en modificar-usuario.jsp)
    const imagenInputRuta = document.getElementById('imagenRuta');
    if (imagenInputRuta) {
        imagenInputRuta.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                // Validar tamaño (2MB)
                if (file.size > 2 * 1024 * 1024) {
                    alert('La imagen debe ser menor a 2MB');
                    this.value = '';
                    return;
                }

                // Validar tipo (JPG/PNG)
                if (!file.type.match('image/jpeg') && !file.type.match('image/png')) {
                    alert('Solo se permiten imágenes JPG y PNG');
                    this.value = '';
                    return;
                }

                const reader = new FileReader();
                reader.onload = function(evt) {
                    // Crear preview si no existe
                    let preview = document.getElementById('imagenPreviewRuta');
                    if (!preview) {
                        preview = document.createElement('img');
                        preview.id = 'imagenPreviewRuta';
                        preview.className = 'mt-2 rounded d-none';
                        preview.style.maxWidth = '150px';
                        preview.style.maxHeight = '150px';
                        imagenInputRuta.parentNode.appendChild(preview);
                    }
                    preview.src = evt.target.result;
                    preview.classList.remove('d-none');
                }
                reader.readAsDataURL(file);
            }
        });
    }

});

function mostrarMensajeExito() {
    const base = window.CONTEXT_PATH || '';
    const url = `${base}/consulta-ruta.jsp`;
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
                        <a href="${base}/consulta-ruta.jsp" class="btn btn-outline-primary">
                            <i class="bi bi-search"></i> Ver Rutas
                        </a>
                    </div>
                </div>
            </div>
        </div>
    `;
    if (!document.getElementById('successModal')) {
        document.body.insertAdjacentHTML('beforeend', modalHTML);
    }
    const successModal = new bootstrap.Modal(document.getElementById('successModal'));
    successModal.show();
}



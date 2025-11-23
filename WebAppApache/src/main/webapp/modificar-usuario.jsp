<!-- HTML -->
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Modificar Datos de Usuario - Juan Viajes</title>
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="CssLogica/estilo-css.css">
</head>
<body class="bg-dark modificar-usuario">
<header>
    <%@ include file="navbar.jsp"%>
</header>

<div class="container mt-4 mb-5">
    <div class="row">
        <div class="col-lg-8 mx-auto">
            <div class="card shadow-lg">
                <div class="card-header">
                    <h4 class="mb-0">Modificar Datos de Usuario</h4>
                    <p class="mb-0 mt-2 small">Actualice su información personal en el sistema</p>
                </div>
                <div class="card-body p-4">

                    <!-- Información del usuario actual -->
                    <div class="alert alert-custom mb-4">
                        <div class="d-flex align-items-center">
                            <img src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjYwIiByeD0iMzAiIGZpbGw9IiMzNDk4REIiLz4KPHN2ZyB4PSIxNSIgeT0iMTUiIHdpZHRoPSIzMCIgaGVpZ2h0PSIzMCIgdmlld0JveD0iMCAwIDI0IDI0IiBmaWxsPSJ3aGl0ZSIgeG1zbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTEyIDEyYzIuMjEgMCA0LTEuNzkgNC00cy0xLjc5LTQtNC00LTQgMS43OS00IDQgMS43OSA0IDQgNHptMCAyYy0yLjY3IDAtOCAxLjM0LTggNHYyaDE2di0yYzAtMi42Ni01LjMzLTQtOC00eiIvPgo8L3N2Zz4KPC9zdmc+"
                                 alt="Imagen de perfil" class="rounded-circle me-3" width="60" height="60" id="userProfileImage">
                            <div>
                                <h6 class="mb-1" id="userDisplayName">Cargando...</h6>
                                <p class="mb-1 small" id="userTypeInfo">Cargando tipo de usuario...</p>
                                <p class="mb-0 small text-muted">Solo puedes modificar tus propios datos</p>
                            </div>
                        </div>
                    </div>

                    <form id="formModificarUsuario" novalidate>
                        <!-- Imagen de perfil -->
                        <div class="text-center mb-4">
                            <img id="imagenPreview" src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgdmlld0JveD0iMCAwIDE1MCAxNTAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIxNTAiIGhlaWdodD0iMTUwIiByeD0iNzUiIGZpbGw9IiMzNDk4REIiLz4KPHN2ZyB4PSIzOCIgeT0iMzgiIHdpZHRoPSI3NCIgaGVpZ2h0PSI3NCIgdmlld0JveD0iMCAwIDI0IDI0IiBmaWxsPSJ3aGl0ZSIgeG1zbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTEyIDEyYzIuMjEgMCA0LTEuNzkgNC00cy0xLjc5LTQtNC00LTQgMS43OS00IDQgMS43OSA0IDQgNHptMCAyYy0yLjY3IDAtOCAxLjM0LTggNHYyaDE2di0yYzAtMi42Ni01LjMzLTQtOC00eiIvPgo8L3N2Zz4KPC9zdmc+"
                                 alt="Vista previa" class="imagen-preview mb-2">
                            <div>
                                <label for="imagenPerfil" class="btn btn-outline-primary btn-sm">
                                    <i class="bi bi-camera"></i> Cambiar Imagen
                                </label>
                                <input type="file" class="d-none" id="imagenPerfil" accept="image/*">
                                <button type="button" class="btn btn-outline-danger btn-sm" onclick="eliminarImagen()">
                                    <i class="bi bi-trash"></i> Eliminar
                                </button>
                            </div>
                            <div class="form-text">Formatos: JPG, PNG, GIF. Máx. 2MB</div>
                        </div>

                        <!-- Sección 1: Datos Básicos -->
                        <div class="form-section">
                            <h5 class="text-primary mb-3">Datos Básicos</h5>

                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="nickname" class="form-label">Nickname</label>
                                    <input type="text" class="form-control campo-bloqueado" id="nickname" disabled>
                                    <div class="form-text">El nickname no se puede modificar</div>
                                </div>

                                <div class="col-md-6">
                                    <label for="correo" class="form-label">Correo electrónico</label>
                                    <input type="email" class="form-control campo-bloqueado" id="correo" disabled>
                                    <div class="form-text">El correo no se puede modificar</div>
                                </div>

                                <div class="col-12">
                                    <label for="nombre" class="form-label">Nombre *</label>
                                    <input type="text" class="form-control" id="nombre" required>
                                    <div class="invalid-feedback">Por favor ingrese su nombre.</div>
                                </div>
                            </div>
                        </div>

                        <!-- Sección 2: Seguridad -->
                        <div class="form-section">
                            <h5 class="text-primary mb-3">Seguridad</h5>

                            <div class="alert alert-info">
                                <small><i class="bi bi-info-circle"></i> Complete solo si desea cambiar su contraseña</small>
                            </div>

                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="password" class="form-label">Nueva contraseña</label>
                                    <input type="password" class="form-control" id="password" minlength="6">
                                    <div class="password-strength" id="passwordStrength"></div>
                                    <div class="form-text">Mínimo 6 caracteres</div>
                                </div>

                                <div class="col-md-6">
                                    <label for="confirmar" class="form-label">Confirmar contraseña</label>
                                    <input type="password" class="form-control" id="confirmar">
                                    <div class="invalid-feedback" id="passwordError">Las contraseñas no coinciden</div>
                                </div>
                            </div>
                        </div>

                        <!-- Sección 3: Información Específica -->
                        <div class="form-section">
                            <h5 class="text-primary mb-3">Información Personal</h5>

                            <!-- Campos para Cliente -->
                            <div id="clienteFields">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label for="apellido" class="form-label">Apellido *</label>
                                        <input type="text" class="form-control" id="apellido" required>
                                        <div class="invalid-feedback">Por favor ingrese su apellido.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label for="fechaNacimiento" class="form-label">Fecha de nacimiento *</label>
                                        <input type="date" class="form-control" id="fechaNacimiento" required>
                                        <div class="invalid-feedback">Por favor ingrese su fecha de nacimiento.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label for="nacionalidad" class="form-label">Nacionalidad *</label>
                                        <input type="text" class="form-control" id="nacionalidad" required>
                                        <div class="invalid-feedback">Por favor ingrese su nacionalidad.</div>
                                    </div>

                                    <div class="col-md-3">
                                        <label for="tipoDoc" class="form-label">Tipo documento *</label>
                                        <select class="form-select" id="tipoDoc" required>
                                            <option value="">Seleccionar...</option>
                                        </select>
                                        <div class="invalid-feedback">Por favor seleccione un tipo de documento.</div>
                                    </div>

                                    <div class="col-md-3">
                                        <label for="numDoc" class="form-label">Número *</label>
                                        <input type="text" class="form-control" id="numDoc" required>
                                        <div class="invalid-feedback">Por favor ingrese el número de documento.</div>
                                    </div>
                                </div>
                            </div>

                            <!-- Campos para Aerolínea -->
                            <div id="aerolineaFields" style="display: none;">
                                <div class="mb-3">
                                    <label for="descripcion" class="form-label">Descripción general *</label>
                                    <textarea class="form-control" id="descripcion" rows="3" required></textarea>
                                    <div class="invalid-feedback">Por favor ingrese una descripción.</div>
                                </div>

                                <div class="mb-3">
                                    <label for="sitioWeb" class="form-label">Sitio web</label>
                                    <input type="url" class="form-control" id="sitioWeb" placeholder="https://...">
                                    <div class="form-text">Enlace a su sitio web oficial (opcional)</div>
                                </div>
                            </div>
                        </div>

                        <!-- Botones de acción -->
                        <div class="d-flex gap-2 mt-4">
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-lg"></i> Guardar Cambios
                            </button>
                            <button type="reset" class="btn btn-secondary">
                                <i class="bi bi-arrow-clockwise"></i> Restablecer
                            </button>
                            <a href="consulta-usuario.jsp" class="btn btn-outline-primary">
                                <i class="bi bi-x-lg"></i> Cancelar
                            </a>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal de confirmación -->
<div class="modal fade" id="confirmacionModal" tabindex="-1" aria-labelledby="confirmacionModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title" id="confirmacionModalLabel">Cambios Guardados</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body text-center">
                <div class="mb-3">
                    <i class="bi bi-check-circle-fill text-success" style="font-size: 3rem;"></i>
                </div>
                <h5>¡Datos actualizados correctamente!</h5>
                <p>Su información personal ha sido modificada en el sistema.</p>
            </div>
            <div class="modal-footer justify-content-center">
                <a href="consulta-usuario.jsp" class="btn btn-primary">Ver Mi Perfil</a>
                <button type="button" class="btn btn-outline-primary" data-bs-dismiss="modal">Continuar Editando</button>
            </div>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script>
    // Script para cargar los datos del usuario desde el servidor y enviar actualizaciones
    let currentUserTipo = null; // 'cliente' o 'aerolinea'
    const API_BASE = '<%= request.getContextPath() %>';
    let saveBtn = null;
    let isUserLoaded = false;

    // Helper para construir la URL de la imagen en frontend.
    // Acepta: 1) URL absoluta (http(s)://...) -> se devuelve tal cual
    //         2) ruta absoluta dentro del sitio (/... ) -> se devuelve tal cual
    //         3) sólo nombre de archivo (ej: 'user_1234.jpg' o 'Images/user_1234.jpg') -> se convierte en CONTEXT + '/Images/<filename>'
    function buildImageUrl(value) {
        if (!value) return '';
        value = String(value).trim();
        if (value.length === 0) return '';
        // Si ya es URL absoluta
        if (/^https?:\/\//i.test(value)) return value;
        // Si comienza con slash, usar tal cual (servidor servirá /Images/xxx)
        if (value.startsWith('/')) return value;
        // Si contiene 'Images/' ya, evitar duplicar y añadir contexto
        if (value.startsWith('Images/')) return API_BASE + '/' + value;
        // Si contiene algún path (e.g. folder/name.ext) pero no empieza con '/', dejarlo relativo al contexto
        if (value.indexOf('/') >= 0) return API_BASE + '/' + value;
        // Finalmente: sólo filename -> usar /Images/<filename>
        return API_BASE + '/Images/' + value;
    }

    async function fetchAndFill() {
        console.log('fetchAndFill: pidiendo datos de usuario a', API_BASE + '/api/usuario/actual');
        try {
            const res = await fetch(API_BASE + '/api/usuario/actual', { method: 'GET', credentials: 'include' });
            if (res.status === 200) {
                const data = await res.json();
                console.log('fetchAndFill: datos recibidos del servidor:', data); // <-- nuevo log
                if (data && data.success !== false) {
                    currentUserTipo = data.tipo;
                    cargarDatosUsuario(data);
                    // habilitar botón guardar
                    isUserLoaded = true;
                    if (saveBtn) saveBtn.disabled = false;
                    return;
                }
            } else if (res.status === 401) {
                mostrarMensajeNoAutenticado();
                return;
            }
            console.error('fetchAndFill: respuesta inesperada', res.status);
            mostrarMensajeNoAutenticado();
        } catch (err) {
            console.error('fetchAndFill: fallo', err);
            mostrarMensajeNoAutenticado();
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        // Obtener referencia al botón Guardar y deshabilitar hasta cargar datos
        saveBtn = document.querySelector('#formModificarUsuario button[type="submit"]');
        if (saveBtn) saveBtn.disabled = true;

        fetchAndFill();
    });

    // Reaccionar cuando el modal de login del navbar actualice la sesión
    window.addEventListener('sessionUpdated', function() {
        console.log('sessionUpdated recibido: re-obteniendo datos de usuario');
        // deshabilitar mientras recargamos
        if (saveBtn) saveBtn.disabled = true;
        isUserLoaded = false;
        fetchAndFill();
    });

    function mostrarMensajeNoAutenticado() {
        const cardBody = document.querySelector('.card-body');
        cardBody.innerHTML = `
        <div class="text-center py-5">
            <i class="bi bi-exclamation-triangle text-warning" style="font-size: 3rem;"></i>
            <h4 class="mt-3">Debe iniciar sesión</h4>
            <p class="text-muted">Para modificar sus datos, primero debe iniciar sesión en el sistema.</p>
            <button class="btn btn-primary mt-3" data-bs-toggle="modal" data-bs-target="#loginModal">
                <i class="bi bi-box-arrow-in-right"></i> Iniciar Sesión
            </button>
        </div>
    `;
    }
    // javascript
    // Pegar en `modificar-usuario.jsp` (dentro del <script>, antes de cargarDatosUsuario)

    async function populateTipoDoc(selectedValue = '') {
        const select = document.getElementById('tipoDoc');
        if (!select) return;

        // estado de carga (solo opción temporal)
        select.innerHTML = '';
        const loadingOpt = document.createElement('option');
        loadingOpt.value = '';
        loadingOpt.text = 'Cargando...';
        select.appendChild(loadingOpt);

        let list = [];
        try {
            const res = await fetch(API_BASE + '/api/documento/tipos', { credentials: 'include' });
            if (res.ok) {
                const json = await res.json().catch(() => null);
                if (Array.isArray(json)) list = json;
                else if (json && Array.isArray(json.tipos)) list = json.tipos;
            }
        } catch (e) {
            console.warn('populateTipoDoc: fallo al obtener tipos', e);
        }

        // fallback si la API no responde con lista
        if (!Array.isArray(list) || list.length === 0) {
            list = ['DNI', 'PASAPORTE', 'CEDULA'];
        }

        // rellenar select sin tocar otros elementos de la página
        select.innerHTML = '';
        const defaultOpt = document.createElement('option');
        defaultOpt.value = '';
        defaultOpt.text = 'Seleccionar...';
        select.appendChild(defaultOpt);

        list.forEach(t => {
            const opt = document.createElement('option');
            opt.value = String(t);
            opt.text = String(t);
            select.appendChild(opt);
        });

        // seleccionar valor pasado (crear opción si no existe)
        if (selectedValue && String(selectedValue).trim().length > 0) {
            const value = String(selectedValue).trim();
            let found = Array.from(select.options).find(o => o.value === value || ((o.text||'').trim().toLowerCase() === value.toLowerCase()));
            if (!found) {
                const opt = document.createElement('option');
                opt.value = value;
                opt.text = value;
                select.appendChild(opt);
            }
            select.value = value;
            select.dispatchEvent(new Event('change', { bubbles: true }));
        } else {
            select.value = '';
        }
    }

    // escucha mínima para actualizar datos tras login/registro (no cambia estética)
    window.addEventListener('sessionUpdated', function() {
        console.log('sessionUpdated recibido: re-obteniendo datos de usuario (solo lógica)');
        if (typeof saveBtn !== 'undefined' && saveBtn) saveBtn.disabled = true;
        if (typeof isUserLoaded !== 'undefined') isUserLoaded = false;

        setTimeout(() => {
            if (typeof fetchAndFill === 'function') {
                fetchAndFill().then(() => {
                    // no forzar cambios visuales aquí; si sigue sin cargar, dejar que el flujo existente actúe
                    if (typeof isUserLoaded !== 'undefined' && !isUserLoaded) {
                        console.warn('sessionUpdated: usuario aún no cargado después del reintento');
                    }
                }).catch((e) => {
                    console.error('sessionUpdated: error en fetchAndFill', e);
                });
            }
        }, 200);
    });


    function cargarDatosUsuario(sessionData) {
        // Rellenar campos comunes
        document.getElementById('nickname').value = sessionData.nickname || '';
        document.getElementById('correo').value = sessionData.email || sessionData.correo || '';
        document.getElementById('nombre').value = sessionData.nombre || '';

        // Actualizar información de display
        document.getElementById('userDisplayName').textContent = sessionData.nombre || sessionData.nickname || '';
        document.getElementById('userTypeInfo').textContent =
            (sessionData.tipo === 'cliente' ? 'Cliente' : 'Aerolínea') +
            ' | Registrado: ' + (sessionData.fechaRegistro || sessionData.fechaAlta || 'N/A');

        // Cargar imagen si viene
        if (sessionData.imagenUrl && sessionData.imagenUrl.length > 0) {
            document.getElementById('imagenPreview').src = buildImageUrl(sessionData.imagenUrl);
            document.getElementById('userProfileImage').src = buildImageUrl(sessionData.imagenUrl);
        }

        // Mostrar campos según el tipo de usuario
        if (sessionData.tipo === 'cliente' || currentUserTipo === 'cliente') {
            document.getElementById('clienteFields').style.display = 'block';
            document.getElementById('aerolineaFields').style.display = 'none';

            // Cargar datos específicos de cliente
            document.getElementById('apellido').value = sessionData.apellido || '';
            document.getElementById('fechaNacimiento').value = sessionData.fechaNacimiento || '';
            document.getElementById('nacionalidad').value = sessionData.nacionalidad || '';
            populateTipoDoc(sessionData.tipoDocumento || sessionData.tipoDoc || '');            document.getElementById('numDoc').value = sessionData.numeroDocumento || sessionData.numeroDocumento || '';

            // Ajustar atributos required: cliente los necesita
            document.getElementById('apellido').required = true;
            document.getElementById('fechaNacimiento').required = true;
            document.getElementById('nacionalidad').required = true;
            document.getElementById('tipoDoc').required = true;
            document.getElementById('numDoc').required = true;
            // aerolinea no requeridos
            document.getElementById('descripcion').required = false;
            document.getElementById('sitioWeb').required = false;

        } else if (sessionData.tipo === 'aerolinea' || currentUserTipo === 'aerolinea') {
            document.getElementById('clienteFields').style.display = 'none';
            document.getElementById('aerolineaFields').style.display = 'block';

            // Cargar datos específicos de aerolínea
            document.getElementById('descripcion').value = sessionData.descripcion || '';
            document.getElementById('sitioWeb').value = sessionData.sitioWeb || '';

            // Ajustar atributos required: aerolinea requiere descripcion
            document.getElementById('descripcion').required = true;
            document.getElementById('sitioWeb').required = false;
            // cliente no requeridos
            document.getElementById('apellido').required = false;
            document.getElementById('fechaNacimiento').required = false;
            document.getElementById('nacionalidad').required = false;
            document.getElementById('tipoDoc').required = false;
            document.getElementById('numDoc').required = false;
        }
    }

    // Devuelve lista de campos inválidos con mensajes para debugging
    function listarCamposInvalidos(form) {
        const invalids = [];
        for (let el of form.elements) {
            // ignorar campos deshabilitados y botones
            if (el.disabled) continue;
            if (el.willValidate === false) continue;
            if (!el.checkValidity()) {
                invalids.push({ name: el.name || el.id || el.tagName, id: el.id || null, message: el.validationMessage });
            }
        }
        return invalids;
    }

    // Validación previa específica por tipo de usuario para dar mensajes más claros
    function preValidateForm(form) {
        // Limpia customValidity previas
        const cleanList = ['apellido','fechaNacimiento','nacionalidad','tipoDoc','numDoc','descripcion','sitioWeb','nombre'];
        cleanList.forEach(id => {
            const el = document.getElementById(id);
            if (el) el.setCustomValidity('');
        });

        const invalids = [];
        if (currentUserTipo === 'cliente') {
            const requiredIds = [
                {id:'nombre', msg:'El nombre es obligatorio.'},
                {id:'apellido', msg:'El apellido es obligatorio.'},
                {id:'fechaNacimiento', msg:'La fecha de nacimiento es obligatoria.'},
                {id:'nacionalidad', msg:'La nacionalidad es obligatoria.'},
                {id:'tipoDoc', msg:'El tipo de documento es obligatorio.'},
                {id:'numDoc', msg:'El número de documento es obligatorio.'}
            ];
            for (const r of requiredIds) {
                const el = document.getElementById(r.id);
                if (!el) continue;
                const val = (el.value || '').trim();
                if (val.length === 0) {
                    el.setCustomValidity(r.msg);
                    invalids.push({id: r.id, name: el.name || r.id, message: r.msg});
                } else {
                    el.setCustomValidity('');
                }
            }
            // Validar formato de fecha (opcional): no permitir fechas futuras
            const fechaEl = document.getElementById('fechaNacimiento');
            if (fechaEl && fechaEl.value) {
                const fecha = new Date(fechaEl.value);
                const hoy = new Date();
                if (fecha > hoy) {
                    const msg = 'La fecha de nacimiento no puede ser futura.';
                    fechaEl.setCustomValidity(msg);
                    invalids.push({id:'fechaNacimiento', name:'fechaNacimiento', message: msg});
                }
            }
        } else if (currentUserTipo === 'aerolinea') {
            const desc = document.getElementById('descripcion');
            if (desc && (desc.value || '').trim().length === 0) {
                const msg = 'La descripción es obligatoria para aerolíneas.';
                desc.setCustomValidity(msg);
                invalids.push({id:'descripcion', name:'descripcion', message: msg});
            }
        }

        return invalids;
    }

    // Manejo del formulario
    const passInput = document.getElementById('password');
    const confirmInput = document.getElementById('confirmar');
    const passwordError = document.getElementById('passwordError');

    function validatePasswordMatch() {
        // Asegurarse que los elementos existan
        if (!passInput || !confirmInput || !passwordError) return true;
        // Si ambos vacíos, permitimos no cambiar la contraseña
        if ((passInput.value || '').length === 0 && (confirmInput.value || '').length === 0) {
            confirmInput.setCustomValidity('');
            passwordError.style.display = 'none';
            return true;
        }
        if (passInput.value !== confirmInput.value) {
            confirmInput.setCustomValidity('Las contraseñas no coinciden');
            passwordError.style.display = 'block';
            return false;
        } else {
            confirmInput.setCustomValidity('');
            passwordError.style.display = 'none';
            return true;
        }
    }

    if (passInput && confirmInput) {
        passInput.addEventListener('input', validatePasswordMatch);
        confirmInput.addEventListener('input', validatePasswordMatch);
    }

    document.getElementById('formModificarUsuario').addEventListener('submit', function(e) {
        e.preventDefault();

        console.log('submit: iniciando validación y envío');

        // Evitar submit hasta que los datos del usuario estén cargados
        if (!isUserLoaded) {
            console.warn('submit: los datos del usuario aún no se cargaron. Abortando.');
            alert('Los datos del usuario aún se están cargando. Espere unos instantes e intente nuevamente.');
            return;
        }

        // debug: logear valores de campos críticos
        console.log('DEBUG valores: currentUserTipo=', currentUserTipo,
            'nombre=', document.getElementById('nombre')?.value,
            'apellido=', document.getElementById('apellido')?.value,
            'fechaNacimiento=', document.getElementById('fechaNacimiento')?.value,
            'nacionalidad=', document.getElementById('nacionalidad')?.value,
            'tipoDoc=', document.getElementById('tipoDoc')?.value,
            'numDoc=', document.getElementById('numDoc')?.value,
            'descripcion=', document.getElementById('descripcion')?.value);

        // validación previa específica
        const preInvalids = preValidateForm(this);
        if (preInvalids.length > 0) {
            this.classList.add('was-validated');
            console.warn('submit: validación previa detectó campos inválidos', preInvalids);
            const msgs = preInvalids.map(i => (i.id + ': ' + i.message)).join('\n');
            alert('Corrija los siguientes errores:\n' + msgs);
            this.reportValidity();
            return;
        }

        if (!this.checkValidity()) {
            this.classList.add('was-validated');
            console.warn('submit: formulario inválido según checkValidity');
            // listar campos inválidos y mostrarlos al usuario
            const invalids = listarCamposInvalidos(this);
            console.log('Campos inválidos:', invalids);
            if (invalids.length > 0) {
                // Marcar visualmente y enfocar el primer inválido
                let firstEl = null;
                invalids.forEach(inv => {
                    if (inv.id) {
                        const el = document.getElementById(inv.id);
                        if (el) {
                            el.classList.add('is-invalid');
                            // attach listener to remove class on input/change
                            const clearInvalid = () => {
                                el.classList.remove('is-invalid');
                                el.removeEventListener('input', clearInvalid);
                                el.removeEventListener('change', clearInvalid);
                            };
                            el.addEventListener('input', clearInvalid);
                            el.addEventListener('change', clearInvalid);
                            if (!firstEl) firstEl = el;
                        }
                    }
                });

                const msgs = invalids.map(i => (i.id ? i.id + ' (' + i.name + '): ' + i.message : i.name + ': ' + i.message)).join('\n');
                alert('Faltan o son inválidos los siguientes campos:\n' + msgs);
                if (firstEl) {
                    try { firstEl.focus(); firstEl.scrollIntoView({behavior:'smooth', block:'center'}); } catch(e){}
                }
            } else {
                alert('El formulario es inválido. Revise los campos requeridos.');
            }
            // Mostrar feedback nativo
            this.reportValidity();
            return;
        }

        // validar confirmación de contraseña
        if (!validatePasswordMatch()) {
            this.classList.add('was-validated');
            console.warn('submit: contraseñas no coinciden');
            return;
        }

        // Construir payload según tipo
        const payload = {};
        payload.nombre = document.getElementById('nombre').value || '';
        // incluir password solo si se completó
        const pass = (passInput && passInput.value) ? passInput.value : '';
        if (pass.length >= 6) payload.password = pass;

        // imagen como dataURL
        const imagenUrl = document.getElementById('imagenPreview').src || '';
        payload.imagenUrl = imagenUrl;

        if (currentUserTipo === 'cliente') {
            payload.apellido = document.getElementById('apellido').value || '';
            payload.fechaNacimiento = document.getElementById('fechaNacimiento').value || '';
            payload.nacionalidad = document.getElementById('nacionalidad').value || '';

            // Obtener tipo de documento de forma robusta: preferir value, si no usar texto de la opción
            const tipoSelectEl = document.getElementById('tipoDoc');
            if (tipoSelectEl) {
                const selectedOption = tipoSelectEl.options[tipoSelectEl.selectedIndex];
                if (selectedOption) {
                    const val = (selectedOption.value || '').trim();
                    payload.tipoDocumento = val.length > 0 ? val : (selectedOption.text || '').trim();
                } else {
                    payload.tipoDocumento = '';
                }
            } else {
                payload.tipoDocumento = '';
            }

            payload.numeroDocumento = document.getElementById('numDoc').value || '';
        } else if (currentUserTipo === 'aerolinea') {
            payload.descripcion = document.getElementById('descripcion').value || '';
            payload.sitioWeb = document.getElementById('sitioWeb').value || '';
        } else {
            console.error('submit: currentUserTipo no definido. Abortando.');
            alert('No autenticado o tipo de usuario desconocido. Inicie sesión desde el modal.');
            return;
        }

        console.log('submit: payload preparado, enviando a', API_BASE + '/api/usuario/actualizar', payload);

        // evitar doble submit
        if (saveBtn) saveBtn.disabled = true;

        // Enviar al servlet de actualización
        fetch(API_BASE + '/api/usuario/actualizar', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        }).then(async (res) => {
            const json = await res.json().catch(() => null);
            if (res.ok && json && json.success) {
                console.log('submit: actualización exitosa', json);
                // Si el servidor devolvió una imagen guardada, actualizar la preview y la imagen de perfil
                if (json.imagenUrl) {
                    try {
                        document.getElementById('imagenPreview').src = json.imagenUrl;
                        document.getElementById('userProfileImage').src = json.imagenUrl;
                    } catch (e) { console.warn('No se pudo actualizar imagen en UI:', e); }
                }
                const modal = new bootstrap.Modal(document.getElementById('confirmacionModal'));
                modal.show();
                if (saveBtn) saveBtn.disabled = false;
            } else {
                const err = json && json.error ? json.error : ('Error ' + res.status);
                console.error('submit: error al actualizar', err, json);
                alert('No se pudieron guardar los cambios: ' + err);
                if (saveBtn) saveBtn.disabled = false;
            }
        }).catch((err) => {
            console.error('Error enviando actualización:', err);
            alert('Error de red al intentar actualizar los datos. Intente nuevamente.');
            if (saveBtn) saveBtn.disabled = false;
        });
    });

    // Función para eliminar imagen
    function eliminarImagen() {
        if (confirm('¿Está seguro de que desea eliminar su imagen de perfil?')) {
            document.getElementById('imagenPreview').src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgdmlld0JveD0iMCAwIDE1MCAxNTAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIxNTAiIGhlaWdodD0iMTUwIiByeD0iNzUiIGZpbGw9IiMzNDk4REIiLz4KPHN2ZyB4PSIzOCIgeT0iMzgiIHdpZHRoPSI3NCIgaGVpZ2h0PSI3NCIgdmlld0JveD0iMCAwIDI0IDI0IiBmaWxsPSJ3aGl0ZSIgeG1zbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTEyIDEyYzIuMjEgMCA0LTEuNzkgNC00cy0xLjc5LTQtNC00LTQgMS43OS00IDQgMS43OSA0IDQgNHptMCAyYy0yLjY3IDAtOCAxLjM0LTggNHYyaDE2di0yYzAtMi42Ni01LjMzLTQtOC00eiIvPgo8L3N2Zz4KPC9zdmc+';
            document.getElementById('userProfileImage').src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgZmlsbD0ibm9uZSIgeG1zbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjYwIiByeD0iMzAiIGZpbGw9IiMzNDk4REIiLz4KPHN2ZyB4PSIxNSIgeT0iMTUiIHdpZHRoPSIzMCIgaGVpZ2h0PSIzMCIgdmlld0JveD0iMCAwIDI0IDI0IiBmaWxsPSJ3aGl0ZSIgeG1zbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTEyIDEyYzIuMjEgMCA0LTEuNzkgNC00cy0xLjc5LTQtNC00LTQgMS43OS00IDQgMS43OSA0IDQgNHptMCAyYy0yLjY3IDAtOCAxLjM0LTggNHYyaDE2di0yYzAtMi42Ni01LjMzLTQtOC00eiIvPgo8L3N2Zz4KPC9zdmc+';
        }
    }

    // Preview de imagen
    document.getElementById('imagenPerfil').addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                document.getElementById('imagenPreview').src = e.target.result;
                document.getElementById('userProfileImage').src = e.target.result;
            }
            reader.readAsDataURL(file);
        }
    });
</script>
</body>
</html>
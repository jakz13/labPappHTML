// Variables globales
let vueloSeleccionado = null;
let usuarioInfo = null;

// helper para construir URLs a la API que funciona con o sin SESSION_API_BASE inyectado
function api(path) {
    // Si ya fue inyectado por la JSP o calculado por session-manager, úsalo
    if (window.SESSION_API_BASE && window.SESSION_API_BASE.length) return window.SESSION_API_BASE + path;

    // Fallback: construir base desde origin + contextPath calculado a partir de pathname
    try {
        const origin = window.location.origin || (window.location.protocol + '//' + window.location.host);
        const contextPath = window.location.pathname.replace(/\/[^/]*$/, '');
        return origin + contextPath + path;
    } catch (e) {
        // Último recurso: devolver path sin cambios (relativo) para compatibilidad
        return path.startsWith('/') ? path.slice(1) : path;
    }
}

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    const base = window.SESSION_API_BASE || '';
    cargarAerolineas(base);
    configurarEventListeners(base);
    inicializarValidacion();
    verificarSesionUsuario(base);
});

// Verificar sesión del usuario
async function verificarSesionUsuario(base) {
    try {
        const response = await fetch(api('/api/check-session'), {
            credentials: 'include'
        });
        if (response.ok) {
            const ct = response.headers.get('content-type') || '';
            if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
            usuarioInfo = await response.json();
            console.log('Usuario autenticado:', usuarioInfo);
        } else {
            // Intentar fallback desde localStorage (ej: después de registro)
            const storedNick = localStorage.getItem('session_nickname');
            const storedTipo = localStorage.getItem('session_tipo');
            if (storedNick && storedTipo) {
                usuarioInfo = { authenticated: true, nickname: storedNick, tipo: storedTipo };
                console.log('Usuario autenticado por fallback localStorage:', usuarioInfo);
            } else {
                usuarioInfo = { authenticated: false };
            }
        }
    } catch (error) {
        console.error('Error verificando sesión:', error);
        // intentar fallback localStorage
        const storedNick = localStorage.getItem('session_nickname');
        const storedTipo = localStorage.getItem('session_tipo');
        if (storedNick && storedTipo) {
            usuarioInfo = { authenticated: true, nickname: storedNick, tipo: storedTipo };
            console.log('Usuario autenticado por fallback localStorage (error path):', usuarioInfo);
        } else {
            usuarioInfo = { authenticated: false };
        }
    }
}

// Cargar aerolíneas desde backend
function cargarAerolineas(base) {
    const url = api('/api/aerolineas');
    console.log('cargarAerolineas -> fetch URL:', url);
    fetch(url)
        .then(res => {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            const ct = res.headers.get('content-type') || '';
            if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
            return res.json();
        })
        .then(data => {
            console.log('cargarAerolineas -> datos recibidos:', data);
            const select = document.getElementById('aerolinea');
            if (!select) {
                console.warn("Elemento select #aerolinea no encontrado en la página");
                return;
            }
            select.innerHTML = '<option value="">Seleccione aerolínea...</option>';
            if (!Array.isArray(data) || data.length === 0) {
                select.innerHTML = '<option value="">No hay aerolíneas disponibles</option>';
                select.disabled = true;
                return;
            }
            data.forEach(a => {
                select.innerHTML += `<option value="${a.nickname}">${a.nombre}</option>`;
            });
            select.disabled = false;
        })
        .catch(err => {
            console.error("Error al cargar aerolíneas:", err);
            const select = document.getElementById('aerolinea');
            if (select) {
                select.innerHTML = '<option value="">Error al cargar aerolíneas</option>';
                select.disabled = true;
            }
        });
}

function configurarEventListeners(base) {
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
            fetch(api('/api/rutas?aerolinea=' + encodeURIComponent(aerolinea)))
                .then(res => {
                    if (!res.ok) throw new Error('HTTP ' + res.status);
                    const ct = res.headers.get('content-type') || '';
                    if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
                    return res.json();
                })
                .then(data => {
                    rutaSelect.innerHTML = '<option value="">Seleccione ruta...</option>';
                    data.forEach(r => {
                        rutaSelect.innerHTML += `<option value="${r.nombre}">${r.nombre}</option>`;
                    });
                    rutaSelect.disabled = false;
                })
                .catch(err => {
                    console.error('Error cargando rutas:', err);
                    rutaSelect.innerHTML = '<option value="">Error cargando rutas</option>';
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
            fetch(api('/api/vuelos?ruta=' + encodeURIComponent(ruta)))
                .then(res => {
                    if (!res.ok) throw new Error('HTTP ' + res.status);
                    const ct = res.headers.get('content-type') || '';
                    if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
                    return res.json();
                })
                .then(data => {
                    vueloSelect.innerHTML = '<option value="">Seleccione vuelo...</option>';
                    data.forEach(v => {
                        vueloSelect.innerHTML += `<option value="${v.nombre}">${v.nombre}</option>`;
                    });
                    vueloSelect.disabled = false;
                })
                .catch(err => {
                    console.error('Error cargando vuelos:', err);
                    vueloSelect.innerHTML = '<option value="">Error cargando vuelos</option>';
                });
        }
    });

    // Vuelo -> mostrar info detallada
    document.getElementById('vuelo').addEventListener('change', function() {
        const vuelo = this.value;
        const resultadoConsulta = document.getElementById('resultadoConsulta');

        if (vuelo) {
            cargarDetallesVuelo(vuelo);
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
            const vuelo = document.getElementById('vuelo').value;
            if (vuelo) {
                cargarDetallesVuelo(vuelo);
            }
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

// Cargar detalles completos del vuelo
// Cargar detalles completos del vuelo
function cargarDetallesVuelo(nombreVuelo) {
    const base = window.SESSION_API_BASE || '';
    const resultadoConsulta = document.getElementById('resultadoConsulta');

    // Mostrar loading
    resultadoConsulta.style.display = 'block';
    resultadoConsulta.innerHTML = `
        <div class="text-center py-4">
            <div class="spinner-border text-primary" role="status"></div>
            <p class="mt-2">Cargando información del vuelo...</p>
        </div>
    `;

    // Cargar información básica del vuelo
    fetch(api('/api/vuelo?nombre=' + encodeURIComponent(nombreVuelo)))
        .then(res => {
            if (!res.ok) {
                throw new Error('Error al cargar el vuelo: ' + res.status);
            }
            const ct = res.headers.get('content-type') || '';
            if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
            return res.json();
        })
        .then(data => {
            vueloSeleccionado = data;
            mostrarDetallesVuelo(data);

            // Verificar permisos del usuario para mostrar sección de reservas
            if (usuarioInfo && usuarioInfo.authenticated) {
                verificarPermisosUsuario(nombreVuelo, document.getElementById('aerolinea').value);
            } else {
                // Usuario no autenticado - mostrar opción para iniciar sesión
                mostrarSeccionNoAutenticada();
            }
        })
        .catch(err => {
            console.error("Error cargando detalles del vuelo:", err);
            resultadoConsulta.innerHTML = `
                <div class="alert alert-danger text-center">
                    <h5>Error al cargar información del vuelo</h5>
                    <p>${err.message}</p>
                </div>
            `;
            vueloSeleccionado = null;
        });
}

function mostrarDetallesVuelo(vueloData) {
    const resultadoConsulta = document.getElementById('resultadoConsulta');

    // DEBUG: mostrar el objeto vueloData completo para facilitar diagnóstico de la URL de la imagen
    try { console.log('mostrarDetallesVuelo - vueloData:', vueloData); } catch (e) { /* ignore */ }

    // Primero, restaurar el contenido original del resultadoConsulta
    resultadoConsulta.innerHTML = `
        <hr>
        <h5 class="text-primary mb-4">Información del Vuelo</h5>

        <div class="row">
            <!-- Imagen del vuelo -->
            <div class="col-md-6 mb-4">
                <img id="imagenVueloDetalle" src="" alt="Imagen del vuelo" class="img-fluid flight-image w-100 rounded">
            </div>

            <!-- Información principal -->
            <div class="col-md-6">
                <div class="info-card p-3 mb-3 bg-dark border rounded">
                    <h4 id="nombreVueloDetalle" class="text-primary mb-3"></h4>
                    <div class="row">
                        <div class="col-6">
                            <strong class="text-light">Aerolínea:</strong>
                            <span id="aerolineaDetalle" class="text-light"></span>
                        </div>
                        <div class="col-6">
                            <strong class="text-light">Ruta:</strong>
                            <span id="rutaDetalle" class="text-light"></span>
                        </div>
                    </div>
                </div>

                <!-- Detalles del vuelo -->
                <div class="row g-3">
                    <div class="col-md-6">
                        <div class="card h-100 bg-secondary border-light">
                            <div class="card-body">
                                <h6 class="card-title text-light">Información del Vuelo</h6>
                                <p class="mb-1 text-light"><strong>Fecha:</strong> <span id="fechaVueloDetalle"></span></p>
                                <p class="mb-1 text-light"><strong>Duración:</strong> <span id="duracionVueloDetalle"></span></p>
                                <p class="mb-1 text-light"><strong>Hora salida:</strong> <span id="horaSalidaDetalle"></span></p>
                                <p class="mb-1 text-light"><strong>Hora llegada:</strong> <span id="horaLlegadaDetalle"></span></p>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <div class="card h-100 bg-secondary border-light">
                            <div class="card-body">
                                <h6 class="card-title text-light">Disponibilidad</h6>
                                <p class="mb-1 text-light"><strong>Turista:</strong> <span id="asientosTuristaDetalle"></span> asientos</p>
                                <p class="mb-1 text-light"><strong>Ejecutivo:</strong> <span id="asientosEjecutivoDetalle"></span> asientos</p>
                                <p class="mb-1 text-light"><strong>Estado:</strong> <span id="estadoVueloDetalle" class="badge bg-success"></span></p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Sección de gestión de reservas (solo para usuarios logueados) -->
                <div id="seccionReservas" class="mt-4" style="display: none;">
                    <hr>
                    <h5 class="text-warning mb-4">Gestión de Reservas</h5>
                    
                    <!-- Información para aerolíneas (reservas) -->
                    <div id="infoAerolinea" class="reserva-section p-3 mt-3 bg-dark border rounded" style="display: none;">
                        <h6 class="text-warning mb-3">
                            <i class="bi bi-building"></i> Gestión de Reservas - Panel Aerolínea
                        </h6>
                        <p class="mb-2 text-light">Este vuelo tiene <strong id="totalReservas" class="text-warning">0</strong> reservas confirmadas.</p>
                        <button class="btn btn-sm btn-outline-warning">
                            <i class="bi bi-list-ul"></i> Ver Detalles de Reservas
                        </button>
                    </div>

                    <!-- Información para clientes (mi reserva) -->
                    <div id="infoCliente" class="reserva-section p-3 mt-3 bg-dark border rounded" style="display: none;">
                        <h6 class="text-success mb-3">
                            <i class="bi bi-person-check"></i> Tu Reserva Confirmada
                        </h6>
                        <p class="mb-2 text-light">Ya tienes una reserva confirmada para este vuelo.</p>
                        <button class="btn btn-sm btn-outline-success">
                            <i class="bi bi-ticket-perforated"></i> Ver Mi Reserva
                        </button>
                    </div>

                    <!-- Opción de reserva para usuarios autenticados sin reserva -->
                    <div id="infoReservar" class="reserva-section p-3 mt-3 bg-dark border rounded" style="display: none;">
                        <h6 class="text-info mb-3">
                            <i class="bi bi-calendar-plus"></i> Realizar Reserva
                        </h6>
                        <p class="mb-2 text-light">Puedes realizar una reserva para este vuelo.</p>
                        <a href="reserva-vuelo.jsp" class="btn btn-sm btn-outline-info">
                            <i class="bi bi-calendar-check"></i> Reservar este Vuelo
                        </a>
                    </div>

                    <!-- Opción de reserva para usuarios no autenticados -->
                    <div id="infoNoAutenticado" class="reserva-section p-3 mt-3 bg-dark border rounded" style="display: none;">
                        <h6 class="text-warning mb-3">
                            <i class="bi bi-person"></i> Iniciar Sesión para Reservar
                        </h6>
                        <p class="mb-2 text-light">Inicia sesión para realizar una reserva en este vuelo.</p>
                        <button type="button" class="btn btn-sm btn-outline-warning" data-bs-toggle="modal" data-bs-target="#loginModal">
                            <i class="bi bi-box-arrow-in-right"></i> Iniciar Sesión
                        </button>
                    </div>
                </div>

                <!-- Acciones generales -->
                <div class="d-flex gap-2 mt-4">
                    <a href="reserva-vuelo.jsp" class="btn btn-success">
                        <i class="bi bi-calendar-check"></i> Reservar este Vuelo
                    </a>
                    <button class="btn btn-outline-primary" onclick="window.print()">
                        <i class="bi bi-printer"></i> Imprimir Información
                    </button>
                </div>
            </div>
        </div>
    `;

    // Ahora actualizar los datos del vuelo
    actualizarElementoSiExiste('nombreVueloDetalle', vueloData.nombre || '-');
    actualizarElementoSiExiste('aerolineaDetalle', document.getElementById('aerolinea').options[document.getElementById('aerolinea').selectedIndex].text);
    actualizarElementoSiExiste('rutaDetalle', document.getElementById('rutaVuelo').value);
    actualizarElementoSiExiste('fechaVueloDetalle', vueloData.fecha || '-');
    actualizarElementoSiExiste('duracionVueloDetalle', vueloData.duracion || '-');
    actualizarElementoSiExiste('horaSalidaDetalle', vueloData.horaSalida || '-');
    actualizarElementoSiExiste('horaLlegadaDetalle', vueloData.horaLlegada || '-');
    actualizarElementoSiExiste('asientosTuristaDetalle', vueloData.asientosTurista !== undefined ? vueloData.asientosTurista : '-');
    actualizarElementoSiExiste('asientosEjecutivoDetalle', vueloData.asientosEjecutivo !== undefined ? vueloData.asientosEjecutivo : '-');
    actualizarElementoSiExiste('estadoVueloDetalle', vueloData.estado || 'Confirmado');

    // Mostrar imagen del vuelo (si está disponible)
    const imagenVuelo = document.getElementById('imagenVueloDetalle');
    // admitir varios nombres que el backend podría devolver: 'imagen', 'imagenUrl', 'image', 'foto', etc.
    const imageValue = vueloData.imagen || vueloData.imagenUrl || vueloData.imagenURL || vueloData.image || vueloData.foto || vueloData.url || '';
    // Si viene una URL absoluta, asignarla directamente (es el caso común en tu backend)
    const absRe = /^(https?:)?\/\//i;
    if (imagenVuelo && imageValue && absRe.test(String(imageValue).trim())) {
        const url = String(imageValue).trim();
        imagenVuelo.src = url;
        imagenVuelo.style.display = 'block';
        // placeholder para fallback
        const originP = window.location.origin || (window.location.protocol + '//' + window.location.host);
        const contextPathP = window.location.pathname.replace(/\/[^/]*$/, '');
        const baseP = (window.SESSION_API_BASE && window.SESSION_API_BASE.length) ? window.SESSION_API_BASE : (originP + contextPathP);
        const baseWithSlashP = baseP.endsWith('/') ? baseP : baseP + '/';
        const placeholderUrlP = new URL('Images/placeholder.svg', baseWithSlashP).href;
        imagenVuelo.onerror = function() {
            console.warn('La imagen absoluta falló, usando placeholder:', url);
            imagenVuelo.src = placeholderUrlP;
            imagenVuelo.style.objectFit = 'contain';
        };
    } else if (imagenVuelo && imageValue) {
        // Construir una lista de candidatos y probarlos secuencialmente
        function buildImageCandidates(img) {
            const candidates = [];
            const trimmed = String(img).trim();
            const origin = window.location.origin || (window.location.protocol + '//' + window.location.host);
            const contextPath = window.location.pathname.replace(/\/[^/]*$/, '');
            const base = (window.SESSION_API_BASE && window.SESSION_API_BASE.length) ? window.SESSION_API_BASE : (origin + contextPath);
            const baseWithSlash = base.endsWith('/') ? base : base + '/';

            // Si es absoluto, sólo él
            if (/^(https?:)?\/\//i.test(trimmed) || trimmed.startsWith('data:')) {
                candidates.push(trimmed);
                return candidates;
            }

            // Posibles formas que el backend puede devolver
            try {
                candidates.push(resolveImageUrl(trimmed)); // la resolución preferida
            } catch (e) { /* ignore */ }

            // Sin 'Images/' directo relativo al origen
            candidates.push(new URL('Images/' + trimmed, origin + '/').href);
            // Relativo al contexto de la app
            candidates.push(new URL('Images/' + trimmed, baseWithSlash).href);
            // Si el backend ya incluye subcarpeta (ej: 'flights/x.jpg'), probar relativo al base
            candidates.push(new URL(trimmed, baseWithSlash).href);
            // Por último, probar el trimmed tal cual (relativo a la página actual)
            try { candidates.push(new URL(trimmed, window.location.href).href); } catch (e) { /* ignore */ }

            // Filtrar duplicados y nulos
            return Array.from(new Set(candidates.filter(Boolean)));
        }

        // Función que prueba cada candidato (asíncrono con Image) y asigna la primera que carga
        function tryLoadCandidates(candidates, imgEl, placeholderUrl) {
            let idx = 0;
            let finished = false;

            function next() {
                if (finished) return;
                if (idx >= candidates.length) {
                    finished = true;
                    imgEl.src = placeholderUrl;
                    imgEl.style.objectFit = 'contain';
                    imgEl.style.display = 'block';
                    return;
                }

                const url = candidates[idx++];
                const tester = new Image();
                tester.onload = function() {
                    if (finished) return;
                    finished = true;
                    imgEl.src = url;
                    imgEl.style.display = 'block';
                    imgEl.style.objectFit = '';
                };
                tester.onerror = function() {
                    // intentar siguiente candidato
                    next();
                };
                // iniciar la carga
                tester.src = url;
            }

            next();
        }

        const candidates = buildImageCandidates(imageValue);
        console.log('Imagen del vuelo - candidatos (usando field):', imageValue, candidates);

        // construir placeholder final
        const origin2 = window.location.origin || (window.location.protocol + '//' + window.location.host);
        const contextPath2 = window.location.pathname.replace(/\/[^/]*$/, '');
        const base2 = (window.SESSION_API_BASE && window.SESSION_API_BASE.length) ? window.SESSION_API_BASE : (origin2 + contextPath2);
        const baseWithSlash2 = base2.endsWith('/') ? base2 : base2 + '/';
        const placeholderUrl = new URL('Images/placeholder.svg', baseWithSlash2).href;

        tryLoadCandidates(candidates, imagenVuelo, placeholderUrl);

        // si ninguna carga, el tryLoadCandidates ya pone placeholder; también dejamos un onerror final por si acaso
        imagenVuelo.onerror = function() {
            console.warn('Imagen final falló, usando placeholder');
            imagenVuelo.src = placeholderUrl;
            imagenVuelo.style.objectFit = 'contain';
        };
    } else if (imagenVuelo) {
        imagenVuelo.style.display = 'none';
    }

    // Mostrar resultados con animación
    resultadoConsulta.style.display = 'block';
    resultadoConsulta.classList.add('fade-in');
}

// Función auxiliar para actualizar elementos solo si existen
function actualizarElementoSiExiste(id, valor) {
    const elemento = document.getElementById(id);
    if (elemento) {
        elemento.textContent = valor;
    } else {
        console.warn(`Elemento con id '${id}' no encontrado`);
    }
}
// Verificar permisos del usuario para el vuelo usando tu servlet existente
function verificarPermisosUsuario(nombreVuelo, aerolineaSeleccionada) {
    fetch(api(`/api/verificar-permisos-vuelo?nombreVuelo=${encodeURIComponent(nombreVuelo)}&aerolinea=${encodeURIComponent(aerolineaSeleccionada)}`))
        .then(res => {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            const ct = res.headers.get('content-type') || '';
            if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
            return res.json();
        })
        .then(data => {
            // Combinar la información de sesión con los permisos específicos del vuelo
            usuarioInfo = {
                ...usuarioInfo,
                ...data
            };
            mostrarSeccionesUsuario(data, nombreVuelo);
        })
        .catch(err => {
            console.error("Error verificando permisos:", err);
            // Por defecto, mostrar como usuario sin permisos especiales
            mostrarSeccionReservaNormal();
        });
}

// Mostrar secciones según el tipo de usuario - adaptado para tu servlet
function mostrarSeccionesUsuario(usuarioData, nombreVuelo) {
    const seccionReservas = document.getElementById('seccionReservas');
    const infoAerolinea = document.getElementById('infoAerolinea');
    const infoCliente = document.getElementById('infoCliente');
    const infoReservar = document.getElementById('infoReservar');
    const infoNoAutenticado = document.getElementById('infoNoAutenticado');

    // Mostrar sección de reservas
    seccionReservas.style.display = 'block';

    // Ocultar todas las subsecciones primero
    infoAerolinea.style.display = 'none';
    infoCliente.style.display = 'none';
    infoReservar.style.display = 'none';
    infoNoAutenticado.style.display = 'none';

    if (usuarioData.autenticado) {
        if (usuarioData.esAerolineaDueña) {
            // Es la aerolínea que publicó el vuelo - mostrar gestión de reservas
            infoAerolinea.style.display = 'block';
            cargarReservasVuelo(nombreVuelo);

            // Configurar el botón para ver detalles de reservas
            const btnVerReservas = infoAerolinea.querySelector('button');
            if (btnVerReservas) {
                btnVerReservas.onclick = function() {
                    mostrarDetallesReservasModal();
                };
            }
        } else if (usuarioData.tieneReservaCliente) {
            // Es un cliente con reserva en este vuelo
            infoCliente.style.display = 'block';

            // Guardar el ID de la reserva para usarlo después
            if (usuarioData.idReservaCliente) {
                infoCliente.setAttribute('data-id-reserva', usuarioData.idReservaCliente);
            }

            // Configurar el botón para ver la reserva
            const btnVerReserva = infoCliente.querySelector('button');
            if (btnVerReserva) {
                btnVerReserva.onclick = function() {
                    verDetalleReservaCliente(usuarioData.idReservaCliente);
                };
            }
        } else {
            // Usuario autenticado pero sin reserva - mostrar opción de reserva
            infoReservar.style.display = 'block';
        }
    } else {
        // Usuario no autenticado - mostrar opción para iniciar sesión
        infoNoAutenticado.style.display = 'block';
    }
}

// Función para ver el detalle de la reserva del cliente
function verDetalleReservaCliente(idReserva) {
    if (!idReserva) {
        mostrarMensajeError('No se pudo identificar la reserva');
        return;
    }

    // Usar el endpoint de consulta-reserva que ya tienes
    fetch(api(`/api/consulta-reserva?action=reserva-cliente-vuelo&usuario=${encodeURIComponent(usuarioInfo.nickname)}&vuelo=${encodeURIComponent(vueloSeleccionado.nombre)}`))
        .then(res => {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            const ct = res.headers.get('content-type') || '';
            if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
            return res.json();
        })
        .then(data => {
            if (data.error) {
                // No se encontró la reserva
                mostrarMensajeError('No se pudo encontrar la reserva: ' + data.error);
            } else {
                mostrarModalDetalleReserva(data);
            }
        })
        .catch(err => {
            console.error("Error cargando detalle de reserva:", err);
            mostrarMensajeError('Error al cargar los detalles de la reserva');
        });
}

// Mostrar modal con detalles de la reserva del cliente
function mostrarModalDetalleReserva(reserva) {
    const contenido = `
        <div class="detalle-reserva">
            <h5 class="text-primary mb-3">Detalles de tu Reserva</h5>
            <div class="row">
                <div class="col-6">
                    <p><strong>ID Reserva:</strong> ${reserva.id}</p>
                    <p><strong>Vuelo:</strong> ${vueloSeleccionado.nombre}</p>
                    <p><strong>Tipo de Asiento:</strong> ${reserva.tipoAsiento}</p>
                </div>
                <div class="col-6">
                    <p><strong>Cantidad de Pasajes:</strong> ${reserva.cantidadPasajes}</p>
                    <p><strong>Equipaje Extra:</strong> ${reserva.equipajeExtra}</p>
                    <p><strong>Costo Total:</strong> $${reserva.costoTotal}</p>
                    <p><strong>Estado:</strong> <span class="badge bg-success">Confirmada</span></p>
                </div>
            </div>
            ${reserva.fechaReserva ? `<p><strong>Fecha de Reserva:</strong> ${reserva.fechaReserva}</p>` : ''}
        </div>
    `;

    // Mostrar modal usando Bootstrap
    const modalHTML = `
        <div class="modal fade" id="modalDetalleReserva" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Detalles de Reserva</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        ${contenido}
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Remover modal existente
    const modalExistente = document.getElementById('modalDetalleReserva');
    if (modalExistente) {
        modalExistente.remove();
    }

    // Agregar nuevo modal al body
    document.body.insertAdjacentHTML('beforeend', modalHTML);

    // Mostrar modal
    const modal = new bootstrap.Modal(document.getElementById('modalDetalleReserva'));
    modal.show();
}

function mostrarSeccionNoAutenticada() {
    const seccionReservas = document.getElementById('seccionReservas');
    const infoNoAutenticado = document.getElementById('infoNoAutenticado');

    seccionReservas.style.display = 'block';
    infoNoAutenticado.style.display = 'block';

    // Ocultar otras secciones
    document.getElementById('infoAerolinea').style.display = 'none';
    document.getElementById('infoCliente').style.display = 'none';
    document.getElementById('infoReservar').style.display = 'none';
}

function mostrarSeccionReservaNormal() {
    const seccionReservas = document.getElementById('seccionReservas');
    const infoReservar = document.getElementById('infoReservar');

    seccionReservas.style.display = 'block';
    infoReservar.style.display = 'block';

    // Ocultar otras secciones
    document.getElementById('infoAerolinea').style.display = 'none';
    document.getElementById('infoCliente').style.display = 'none';
    document.getElementById('infoNoAutenticado').style.display = 'none';
}

function cargarReservasVuelo(nombreVuelo) {
    fetch(api(`/api/reservas-vuelo?nombreVuelo=${encodeURIComponent(nombreVuelo)}`))
        .then(res => {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            const ct = res.headers.get('content-type') || '';
            if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
            return res.json();
        })
        .then(reservas => {
            console.log('Reservas recibidas para total:', reservas); // DEBUG
            const totalReservas = document.getElementById('totalReservas');
            if (totalReservas) {
                totalReservas.textContent = reservas.length;
            }

            // Actualizar el botón para mostrar detalles
            const btnVerReservas = document.querySelector('#infoAerolinea button');
            if (btnVerReservas) {
                btnVerReservas.onclick = function() {
                    mostrarDetallesReservasModal(reservas);
                };
            }
        })
        .catch(err => {
            console.error("Error cargando reservas:", err);
            const totalReservas = document.getElementById('totalReservas');
            if (totalReservas) {
                totalReservas.textContent = '0';
            }
        });
}

// Mostrar modal con detalles de reservas (para aerolíneas)
function mostrarDetallesReservasModal(reservas) {
    if (!reservas || reservas.length === 0) {
        mostrarMensajeError('No hay reservas para mostrar');
        return;
    }

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
                        <th>Fecha Reserva</th>
                    </tr>
                </thead>
                <tbody>
    `;

    reservas.forEach(reserva => {
        // Usar los nombres de campos correctos que vienen del servlet
        const id = reserva.id || 'N/A';
        const cliente = reserva.clienteNombre || 'Cliente no disponible';
        const tipoAsiento = reserva.tipoAsiento || 'N/A';
        const cantidadPasajes = reserva.cantidadPasajes || '0';
        const costo = reserva.costoTotal !== undefined ? `$${reserva.costoTotal}` : '$0';
        const fechaReserva = reserva.fechaReserva || 'N/A';

        contenido += `
            <tr>
                <td>${id}</td>
                <td>${cliente}</td>
                <td>${tipoAsiento}</td>
                <td>${cantidadPasajes}</td>
                <td>${costo}</td>
                <td>${fechaReserva}</td>
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

    // Mostrar modal usando Bootstrap
    const modalHTML = `
        <div class="modal fade" id="modalReservasAerolinea" tabindex="-1">
            <div class="modal-dialog modal-xl">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Detalles de Reservas - ${vueloSeleccionado.nombre}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        ${contenido}
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Remover modal existente
    const modalExistente = document.getElementById('modalReservasAerolinea');
    if (modalExistente) {
        modalExistente.remove();
    }

    // Agregar nuevo modal al body
    document.body.insertAdjacentHTML('beforeend', modalHTML);

    // Mostrar modal
    const modal = new bootstrap.Modal(document.getElementById('modalReservasAerolinea'));
    modal.show();
}
// Ver mi reserva (para clientes) - redirige a consulta-reserva.jsp
function verMiReserva() {
    if (!vueloSeleccionado || !usuarioInfo) return;

    // Redirigir a consulta-reserva.jsp con parámetros pre-cargados
    const aerolinea = document.getElementById('aerolinea').value;
    const ruta = document.getElementById('rutaVuelo').value;
    const vuelo = vueloSeleccionado.nombre;

    window.location.href = `consulta-reserva.jsp?aerolinea=${encodeURIComponent(aerolinea)}&ruta=${encodeURIComponent(ruta)}&vuelo=${encodeURIComponent(vuelo)}`;
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

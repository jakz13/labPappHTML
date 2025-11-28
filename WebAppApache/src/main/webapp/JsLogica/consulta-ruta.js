// Variables globales
let rutaSeleccionada = null;
let vueloSeleccionado = null;
let usuarioInfo = null;
let rutasCargadas = [];

// Función para construir URLs a la API
function api(path) {
    if (window.SESSION_API_BASE && window.SESSION_API_BASE.length) return window.SESSION_API_BASE + path;
    try {
        const origin = window.location.origin || (window.location.protocol + '//' + window.location.host);
        const contextPath = window.location.pathname.replace(/\/[^/]*$/, '');
        return origin + contextPath + path;
    } catch (e) {
        return path.startsWith('/') ? path : ('/' + path);
    }
}

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Inicializando consulta-ruta.js');
    cargarAerolineas();
    cargarCategorias();
    configurarEventListeners();
});

// Cargar aerolíneas desde backend
function cargarAerolineas() {
    const timestamp = new Date().getTime();
    const url = api('/api/aerolineas?_=' + timestamp);
    console.log('cargarAerolineas -> fetch URL (sin cache):', url);

    fetch(url, {
        credentials: 'include',
        headers: {
            'Cache-Control': 'no-cache',
            'Pragma': 'no-cache'
        }
    })
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

            // RESTAURACIÓN
            const saved = sessionStorage.getItem('consultaRuta_aerolinea');
            if (saved) {
                const opt = Array.from(select.options).find(o => o.value === saved);
                if (opt) {
                    select.value = saved;
                    console.log('cargarAerolineas: restaurada aerolínea desde sessionStorage ->', saved);
                    setTimeout(() => {
                        select.dispatchEvent(new Event('change'));
                    }, 100);
                } else {
                    console.warn('cargarAerolineas: valor guardado en sessionStorage no encontrado:', saved);
                    sessionStorage.removeItem('consultaRuta_aerolinea');
                }
            }
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

// Cargar categorías
function cargarCategorias() {
    const timestamp = new Date().getTime();
    const url = api('/listarCategorias?_=' + timestamp);

    fetch(url, { credentials: 'include' })
        .then(res => {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            const ct = res.headers.get('content-type') || '';
            if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
            return res.json();
        })
        .then(data => {
            const select = document.getElementById('categoria');
            if (!select) return;
            select.innerHTML = '<option value="">Todas las categorías</option>';
            if (!Array.isArray(data) || data.length === 0) return;
            data.forEach(c => {
                select.innerHTML += `<option value="${c.nombre}">${c.nombre}</option>`;
            });

            // Restaurar categoría si existe
            const categoriaGuardada = sessionStorage.getItem('consultaRuta_categoria');
            if (categoriaGuardada) {
                select.value = categoriaGuardada;
            }
        })
        .catch(err => {
            console.error('Error al cargar categorías:', err);
        });
}

function configurarEventListeners() {
    const aerolineaSelect = document.getElementById('aerolinea');
    const categoriaSelect = document.getElementById('categoria');

    // Guardar en sessionStorage
    aerolineaSelect.addEventListener('change', function() {
        sessionStorage.setItem('consultaRuta_aerolinea', this.value);
        sessionStorage.removeItem('consultaRuta_rutaSeleccionada');
    });

    categoriaSelect.addEventListener('change', function() {
        sessionStorage.setItem('consultaRuta_categoria', this.value);
    });

    // Aerolínea -> rutas
    aerolineaSelect.addEventListener('change', function() {
        const aerolinea = this.value;
        cargarRutasPorAerolinea(aerolinea);
    });

    // Botón aplicar filtros
    const btnAplicar = document.getElementById('btnAplicarFiltros');
    if (btnAplicar) {
        btnAplicar.addEventListener('click', function() {
            aplicarFiltros();
        });
    }

    // Botón limpiar
    const btnLimpiar = document.getElementById('btnLimpiar');
    if (btnLimpiar) {
        btnLimpiar.addEventListener('click', function() {
            limpiarFiltros();
        });
    }

    // Botón actualizar datos
    const btnActualizar = document.getElementById('btnActualizar');
    if (btnActualizar) {
        btnActualizar.addEventListener('click', function() {
            console.log('🔄 Forzando actualización de datos...');
            const aerolineaActual = document.getElementById('aerolinea').value;
            if (aerolineaActual) {
                // Limpiar cache y recargar
                rutasCargadas = [];
                cargarRutasPorAerolinea(aerolineaActual);
                mostrarMensajeExito('Datos actualizados correctamente');
            } else {
                mostrarMensajeError('Seleccione una aerolínea primero');
            }
        });
    }

    // Aplicar filtro cuando se cambia la categoría
    if (categoriaSelect) {
        categoriaSelect.addEventListener('change', function() {
            aplicarFiltros();
        });
    }
}

// Función específica para cargar rutas por aerolínea
function cargarRutasPorAerolinea(aerolinea) {
    const rutasList = document.getElementById('listaRutas');
    const vuelosList = document.getElementById('vuelosAsociados');

    rutasList.innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">Cargando rutas...</p></div>';
    vuelosList.innerHTML = '';
    document.getElementById('infoRuta').style.display = 'none';
    document.getElementById('infoVuelo').style.display = 'none';
    rutaSeleccionada = null;
    vueloSeleccionado = null;

    if (aerolinea) {
        // AGREGAR TIMESTAMP PARA EVITAR CACHE
        const timestamp = new Date().getTime();
        const url = api('/api/rutas?aerolinea=' + encodeURIComponent(aerolinea) + '&_=' + timestamp);

        console.log('🔄 Cargando rutas (sin cache):', url);

        fetch(url, {
            credentials: 'include',
            headers: {
                'Cache-Control': 'no-cache',
                'Pragma': 'no-cache'
            }
        })
            .then(res => {
                if (!res.ok) throw new Error('HTTP ' + res.status);
                return res.json();
            })
            .then(data => {
                rutasCargadas = data;
                console.log('✅ Rutas cargadas (actualizadas):', data);
                aplicarFiltros();

                // Restaurar ruta seleccionada si existe
                const rutaGuardada = sessionStorage.getItem('consultaRuta_rutaSeleccionada');
                if (rutaGuardada && data.some(r => r.nombre === rutaGuardada)) {
                    setTimeout(() => {
                        seleccionarRuta(rutaGuardada);
                    }, 200);
                }
            })
            .catch(err => {
                console.error('Error al cargar rutas:', err);
                rutasList.innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">Error al cargar rutas</p></div>';
            });
    } else {
        rutasList.innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">Seleccione una aerolínea para ver las rutas</p></div>';
        rutasCargadas = [];
    }
}

function cargarRutas(rutas) {
    const container = document.getElementById('listaRutas');
    container.innerHTML = '';

    if (!rutas || rutas.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center py-4">
                <p class="text-muted">No se encontraron rutas para esta aerolínea.</p>
            </div>
        `;
        return;
    }

    rutas.forEach(ruta => {
        const rutaHTML = `
            <div class="col-md-6 mb-3">
                <div class="card ruta-card h-100" onclick="seleccionarRuta('${ruta.nombre}')" style="cursor: pointer;">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <h6 class="card-title text-primary">${ruta.nombre}</h6>
                            <span class="badge bg-success">${ruta.estado || 'Confirmada'}</span>
                        </div>
                        <small class="text-muted">${ruta.origen} → ${ruta.destino}</small>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += rutaHTML;
    });
}

function seleccionarRuta(nombreRuta) {
    console.log('🔍 Seleccionando ruta:', nombreRuta);
    sessionStorage.setItem('consultaRuta_rutaSeleccionada', nombreRuta);

    // AGREGAR TIMESTAMP PARA EVITAR CACHE
    const timestamp = new Date().getTime();
    const url = api('/consultaRuta?nombreRuta=' + encodeURIComponent(nombreRuta) + '&_=' + timestamp);

    console.log('🔄 Cargando detalles de ruta (sin cache):', url);

    fetch(url, {
        credentials: 'include',
        headers: {
            'Cache-Control': 'no-cache',
            'Pragma': 'no-cache'
        }
    })
        .then(response => {
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            return response.json();
        })
        .then(rutaDetallada => {
            if (rutaDetallada.error) {
                throw new Error(rutaDetallada.error);
            }

            console.log('✅ Detalles de ruta obtenidos (actualizados):', rutaDetallada);
            rutaSeleccionada = rutaDetallada;
            mostrarDetallesRuta(rutaDetallada);
            cargarVuelosRuta(nombreRuta);

            // Remover selección anterior y marcar actual
            document.querySelectorAll('.ruta-card').forEach(card => {
                card.classList.remove('border-primary', 'bg-light');
            });

            // Encontrar y marcar la tarjeta seleccionada
            const todasLasTarjetas = document.querySelectorAll('.ruta-card');
            for (let card of todasLasTarjetas) {
                const titulo = card.querySelector('.card-title');
                if (titulo && titulo.textContent === nombreRuta) {
                    card.classList.add('border-primary', 'bg-light');
                    break;
                }
            }
        })
        .catch(error => {
            console.error('❌ Error consultando ruta:', error);
            const rutaLocal = rutasCargadas.find(r => r.nombre === nombreRuta);
            if (rutaLocal) {
                rutaSeleccionada = rutaLocal;
                mostrarDetallesRuta(rutaLocal);
                cargarVuelosRuta(nombreRuta);
            } else {
                mostrarMensajeError('No se pudieron cargar los detalles de la ruta');
            }
        });
}

function mostrarDetallesRuta(ruta) {
    document.getElementById('rutaNombre').textContent = ruta.nombre || '-';
    document.getElementById('rutaDescripcion').textContent = ruta.descripcion || 'Sin descripción';

    const aerSelect = document.getElementById('aerolinea');
    const aerText = (aerSelect && aerSelect.options[aerSelect.selectedIndex]) ? aerSelect.options[aerSelect.selectedIndex].text : '-';
    document.getElementById('rutaAerolinea').textContent = aerText;

    document.getElementById('rutaOrigen').textContent = ruta.origen || '-';
    document.getElementById('rutaDestino').textContent = ruta.destino || '-';
    document.getElementById('rutaEstado').textContent = ruta.estado || 'Confirmada';
    document.getElementById('rutaCategorias').textContent = ruta.categorias && ruta.categorias.length > 0 ? ruta.categorias.join(', ') : 'No especificadas';
    document.getElementById('costoTurista').textContent = ruta.costoTurista !== undefined ? `$${ruta.costoTurista}` : 'N/A';
    document.getElementById('costoEjecutivo').textContent = ruta.costoEjecutivo !== undefined ? `$${ruta.costoEjecutivo}` : 'N/A';
    document.getElementById('costoEquipaje').textContent = ruta.costoEquipaje !== undefined ? `$${ruta.costoEquipaje}` : 'N/A';

    // Manejo de imagen
    const infoRutaEl = document.getElementById('infoRuta');
    if (infoRutaEl) {
        let imgContainer = document.getElementById('rutaImagenContainer');
        if (!imgContainer) {
            imgContainer = document.createElement('div');
            imgContainer.id = 'rutaImagenContainer';
            imgContainer.className = 'mb-3';
            infoRutaEl.insertAdjacentElement('afterbegin', imgContainer);
        }

        let imgEl = document.getElementById('imagenRutaDetalle');
        if (!imgEl) {
            imgEl = document.createElement('img');
            imgEl.id = 'imagenRutaDetalle';
            imgEl.alt = 'Imagen de la ruta';
            imgEl.className = 'img-fluid route-image w-100 rounded';
            imgContainer.innerHTML = '';
            imgContainer.appendChild(imgEl);
        }

        const imageValue = ruta.imagen || ruta.imagenUrl || ruta.imagenURL || ruta.image || ruta.foto || ruta.url || '';
        if (imageValue) {
            const raw = String(imageValue).trim();
            const resolved = toPublicImageUrl(raw);
            console.log('Imagen de ruta:', resolved);
            imgEl.src = resolved;
            imgEl.style.display = 'block';
            imgEl.style.objectFit = 'cover';
            imgEl.onerror = function() {
                console.warn('La imagen de ruta falló al cargar:', resolved);
                imgEl.style.display = 'none';
            };
        } else {
            imgEl.style.display = 'none';
        }
    }

    // Manejo de video
    const videoUrl = ruta.videoUrl || ruta.video || '';
    const videoUrlContainer = document.getElementById('videoUrlContainer');
    const videoContainer = document.getElementById('videoContainer');

    if (videoUrl) {
        if (videoUrlContainer) {
            videoUrlContainer.style.display = 'block';
            const linkEl = document.getElementById('videoUrlLink');
            if (linkEl) {
                linkEl.href = videoUrl;
                linkEl.textContent = videoUrl;
            }
        }

        if (videoContainer) {
            videoContainer.innerHTML = '';
            const ytId = getYouTubeEmbed(videoUrl);
            const vimeoId = getVimeoEmbed(videoUrl);
            if (ytId) {
                const iframe = document.createElement('iframe');
                iframe.width = '100%';
                iframe.height = '360';
                iframe.src = 'https://www.youtube.com/embed/' + ytId;
                iframe.allow = 'accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture';
                iframe.allowFullscreen = true;
                iframe.className = 'rounded';
                videoContainer.appendChild(iframe);
            } else if (vimeoId) {
                const iframe = document.createElement('iframe');
                iframe.width = '100%';
                iframe.height = '360';
                iframe.src = 'https://player.vimeo.com/video/' + vimeoId;
                iframe.allowFullscreen = true;
                iframe.className = 'rounded';
                videoContainer.appendChild(iframe);
            } else if (/\.mp4($|\?)/i.test(videoUrl)) {
                const videoEl = document.createElement('video');
                videoEl.controls = true;
                videoEl.className = 'w-100 rounded';
                const src = document.createElement('source');
                src.src = videoUrl;
                src.type = 'video/mp4';
                videoEl.appendChild(src);
                videoContainer.appendChild(videoEl);
            }
        }
    } else {
        if (videoUrlContainer) videoUrlContainer.style.display = 'none';
        if (videoContainer) videoContainer.innerHTML = '';
    }

    try {
        document.getElementById('infoRuta').style.display = 'block';
    } catch (e) {}
}

// Filtrar rutas por categoría
function aplicarFiltros() {
    const categoriaSeleccionada = document.getElementById('categoria').value;

    if (!rutasCargadas || rutasCargadas.length === 0) return;

    if (!categoriaSeleccionada || categoriaSeleccionada.trim() === '') {
        cargarRutas(rutasCargadas);
        return;
    }

    const rutasFiltradas = rutasCargadas.filter(ruta => {
        if (!ruta.categorias || ruta.categorias.length === 0) return false;
        return ruta.categorias.some(cat => cat.toString().toLowerCase() === categoriaSeleccionada.toString().toLowerCase());
    });

    cargarRutas(rutasFiltradas);
}

function limpiarFiltros() {
    sessionStorage.removeItem('consultaRuta_aerolinea');
    sessionStorage.removeItem('consultaRuta_categoria');
    sessionStorage.removeItem('consultaRuta_rutaSeleccionada');

    document.getElementById('aerolinea').value = '';
    document.getElementById('categoria').value = '';
    rutasCargadas = [];
    document.getElementById('listaRutas').innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">Seleccione una aerolínea para ver las rutas</p></div>';
    document.getElementById('vuelosAsociados').innerHTML = '';
    document.getElementById('infoRuta').style.display = 'none';
    document.getElementById('infoVuelo').style.display = 'none';
    rutaSeleccionada = null;
    vueloSeleccionado = null;
}

function cargarVuelosRuta(nombreRuta) {
    const vuelosList = document.getElementById('vuelosAsociados');
    vuelosList.innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">Cargando vuelos...</p></div>';

    const timestamp = new Date().getTime();
    const url = api('/api/vuelos?ruta=' + encodeURIComponent(nombreRuta) + '&_=' + timestamp);

    fetch(url, {
        credentials: 'include',
        headers: {
            'Cache-Control': 'no-cache',
            'Pragma': 'no-cache'
        }
    })
        .then(res => res.json())
        .then(data => {
            if (!data || data.length === 0) {
                vuelosList.innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">No hay vuelos asociados a esta ruta.</p></div>';
                return;
            }

            vuelosList.innerHTML = '';
            data.forEach(vuelo => {
                const vueloHTML = `
                    <div class="col-md-6 mb-3">
                        <div class="card border-success h-100" onclick="seleccionarVuelo('${vuelo.nombre || vuelo.id || ''}')" style="cursor: pointer;">
                            <div class="card-body text-center">
                                <h6 class="card-title">${vuelo.codigoVuelo || vuelo.nombre || 'Vuelo'}</h6>
                                <p class="mb-1 small text-muted">${vuelo.fechaHoraSalida || ''} - ${vuelo.origen || ''} → ${vuelo.destino || ''}</p>
                                <span class="badge bg-success">${vuelo.estado || 'Disponible'}</span>
                            </div>
                        </div>
                    </div>`;
                vuelosList.innerHTML += vueloHTML;
            });
        })
        .catch(err => {
            console.error("Error cargando vuelos:", err);
            vuelosList.innerHTML = '<div class="col-12 text-center py-2"><p class="text-muted">Error al cargar vuelos</p></div>';
        });
}

// Helper functions
function toPublicImageUrl(value) {
    if (!value) return '';
    const v = String(value).trim();
    if (v.length === 0) return '';
    if (/^(data:|https?:)?\/\//i.test(v)) return v;
    if (v.startsWith('/')) return v;
    if (v.startsWith('Images/')) {
        const ctx = window.CONTEXT_PATH || window.location.pathname.replace(/\/[^/]*$/, '');
        return (ctx.endsWith('/') ? ctx.slice(0, -1) : ctx) + '/' + v;
    }
    if (v.indexOf('/') < 0) {
        const ctx = window.CONTEXT_PATH || window.location.pathname.replace(/\/[^/]*$/, '');
        const prefix = (ctx.endsWith('/')) ? ctx.slice(0, -1) : ctx;
        return prefix + '/Images/' + v;
    }
    try {
        const origin = window.location.origin || (window.location.protocol + '//' + window.location.host);
        const contextPath = window.CONTEXT_PATH || window.location.pathname.replace(/\/[^/]*$/, '');
        return new URL(v, origin + contextPath + '/').href;
    } catch (e) {
        return v;
    }
}

function getYouTubeEmbed(url) {
    try {
        const u = new URL(url);
        if (u.hostname.indexOf('youtube.com') >= 0) {
            return u.searchParams.get('v');
        }
        if (u.hostname.indexOf('youtu.be') >= 0) {
            return u.pathname.split('/').filter(Boolean)[0];
        }
    } catch (e) { }
    return null;
}

function getVimeoEmbed(url) {
    try {
        const u = new URL(url);
        if (u.hostname.indexOf('vimeo.com') >= 0) {
            return u.pathname.split('/').filter(Boolean)[0];
        }
    } catch (e) { }
    return null;
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

function mostrarMensajeExito(mensaje) {
    const toastHTML = `
        <div class="toast align-items-center text-bg-success border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi bi-check-circle-fill me-2"></i>${mensaje}
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

// Función placeholder para seleccionarVuelo
function seleccionarVuelo(nombreVuelo) {
    console.log('Vuelo seleccionado:', nombreVuelo);
    mostrarMensajeError('Función de selección de vuelo no implementada');
}
// Variables globales
let rutaSeleccionada = null;
let vueloSeleccionado = null;
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
                <div class="card ruta-card h-100" onclick="incAndSelectRuta('${ruta.nombre}')" style="cursor: pointer;">
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

// Nueva función: incrementa visitas (best-effort) y selecciona la ruta
function incAndSelectRuta(nombreRuta) {
    try {
        const contextPath = window.CONTEXT_PATH || '';
        const incUrl = `${contextPath}/incrementarVisitasRuta?nombreRuta=${encodeURIComponent(nombreRuta)}`;
        const controller = new AbortController();
        const timeout = setTimeout(() => controller.abort(), 800);
        // Fire-and-forget: no await para no bloquear la UI
        fetch(incUrl, { method: 'POST', credentials: 'include', signal: controller.signal })
            .catch(e => console.warn('⚠️ No se pudo incrementar visitas (onclick):', e))
            .finally(() => clearTimeout(timeout));
    } catch (e) {
        console.warn('⚠️ Error iniciando incremento de visitas (onclick):', e);
    }

    // Llamar a la función existente que carga el detalle
    try {
        seleccionarRuta(nombreRuta);
    } catch (e) {
        console.error('❌ Error al seleccionar ruta después de incrementar visitas:', e);
    }
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
    // Función mejorada para formatear fechas
    function formatearFecha(valor) {
        if (!valor) return '-';
        try {
            // Si ya es una fecha formateada (dd/MM/yyyy), devolver tal cual
            if (typeof valor === 'string' && /^\d{1,2}\/\d{1,2}\/\d{4}$/.test(valor)) {
                return valor;
            }

            // Si es formato ISO (yyyy-MM-dd) o similar, convertir
            const d = new Date(valor);
            if (isNaN(d.getTime())) {
                // Intentar parsear formato yyyy-MM-dd
                const parts = String(valor).split('-');
                if (parts.length === 3) {
                    const [year, month, day] = parts;
                    d.setFullYear(parseInt(year), parseInt(month) - 1, parseInt(day));
                }
            }

            if (!isNaN(d.getTime())) {
                const dd = String(d.getDate()).padStart(2, '0');
                const mm = String(d.getMonth() + 1).padStart(2, '0');
                const yyyy = d.getFullYear();
                return `${dd}/${mm}/${yyyy}`;
            }

            return String(valor); // Devolver el valor original si no se puede parsear
        } catch (e) {
            console.warn('Error formateando fecha:', valor, e);
            return String(valor);
        }
    }

    // Actualizar los campos en la UI
    document.getElementById('rutaNombre').textContent = ruta.nombre || '-';
    document.getElementById('rutaDescripcion').textContent = ruta.descripcion || 'Sin descripción';

    const aerSelect = document.getElementById('aerolinea');
    const aerText = (aerSelect && aerSelect.options[aerSelect.selectedIndex]) ?
        aerSelect.options[aerSelect.selectedIndex].text : '-';
    document.getElementById('rutaAerolinea').textContent = aerText;

    document.getElementById('rutaOrigen').textContent = ruta.origen || '-';
    document.getElementById('rutaDestino').textContent = ruta.destino || '-';

    // === Estado con colores según valor ===
    const estadoSpan = document.getElementById('rutaEstado');
    const estadoValor = (ruta.estado || 'CONFIRMADA').toString().toUpperCase();
    estadoSpan.textContent = ruta.estado || 'Confirmada';
    // limpiar clases previas
    estadoSpan.className = 'badge';
    if (estadoValor === 'CONFIRMADA') {
        estadoSpan.classList.add('badge-estado-confirmada');
    } else if (estadoValor === 'INGRESADA') {
        estadoSpan.classList.add('badge-estado-ingresada');
    } else if (estadoValor === 'FINALIZADA' || estadoValor === 'RECHAZADA') {
        estadoSpan.classList.add('badge-estado-finalizada');
    } else {
        // por si aparece algún otro estado, usamos estilo neutro
        estadoSpan.classList.add('bg-secondary');
    }

    const fechaMostrada = formatearFecha(ruta.fechaAlta);
    const fechaEl = document.getElementById('rutaFechaAlta');
    if (fechaEl) fechaEl.textContent = fechaMostrada;

    document.getElementById('rutaCategorias').textContent = ruta.categorias && ruta.categorias.length > 0 ? ruta.categorias.join(', ') : 'No especificadas';
    document.getElementById('costoTurista').textContent = ruta.costoTurista !== undefined ? `$${ruta.costoTurista}` : 'N/A';
    document.getElementById('costoEjecutivo').textContent = ruta.costoEjecutivo !== undefined ? `$${ruta.costoEjecutivo}` : 'N/A';
    document.getElementById('costoEquipaje').textContent = ruta.costoEquipaje !== undefined ? `$${ruta.costoEquipaje}` : 'N/A';

    // === MANEJO DE IMAGEN - CORREGIDO ===
    const imgElement = document.getElementById('imagenRutaDetalle');
    if (imgElement) {
        const possibleImage = ruta.imagenUrl || ruta.imagen || ruta.image || ruta.foto || ruta.url || '';
        if (possibleImage && String(possibleImage).trim() !== '') {
            let imgPath = String(possibleImage).trim();
            const contextPath = window.CONTEXT_PATH || '';

            console.log('🖼️ Imagen original:', imgPath);
            console.log('🖼️ Context path:', contextPath);

            // CASO 1: Solo nombre de archivo (pruebaDeRuta9876_1764308939475.jpg)
            if (!imgPath.includes('/') && !/^https?:\/\//i.test(imgPath)) {
                imgPath = contextPath + '/Images/' + imgPath;
            }
            // CASO 2: Ya tiene "Images/" pero sin contextPath
            else if (imgPath.startsWith('Images/') && !/^https?:\/\//i.test(imgPath)) {
                imgPath = contextPath + '/' + imgPath;
            }
            // CASO 3: Si empieza con / pero no tiene el contexto completo
            else if (imgPath.startsWith('/') && !imgPath.includes(contextPath) && !/^https?:\/\//i.test(imgPath)) {
                // Si es una ruta absoluta pero le falta el contextPath, agregarlo
                if (!imgPath.startsWith(contextPath)) {
                    imgPath = contextPath + imgPath;
                }
            }
            // CASO 4: Si ya es una URL completa, dejarla como está
            else if (/^https?:\/\//i.test(imgPath)) {
                // No hacer nada, ya es una URL completa
            }
            // CASO 5: Cualquier otro caso, construir la ruta completa
            else {
                imgPath = contextPath + '/Images/' + imgPath.split('/').pop();
            }

            console.log('🖼️ Imagen final construida:', imgPath);

            imgElement.src = imgPath;
            imgElement.style.display = 'block';
            imgElement.style.objectFit = 'cover';

            // Manejo de errores mejorado
            imgElement.onerror = function() {
                console.warn('❌ La imagen de ruta falló al cargar:', imgPath);

                // Intentar alternativas si falla la primera
                const fallbackPaths = [
                    contextPath + '/Images/' + (ruta.imagenUrl || ruta.imagen || ruta.image || ruta.foto || ruta.url || '').split('/').pop(),
                    contextPath + '/images/' + (ruta.imagenUrl || ruta.imagen || ruta.image || ruta.foto || ruta.url || '').split('/').pop(),
                    '/Images/' + (ruta.imagenUrl || ruta.imagen || ruta.image || ruta.foto || ruta.url || '').split('/').pop()
                ];

                let currentFallbackIndex = 0;
                const tryFallback = () => {
                    if (currentFallbackIndex < fallbackPaths.length) {
                        const fallbackPath = fallbackPaths[currentFallbackIndex++];
                        console.log('🔄 Intentando fallback:', fallbackPath);
                        imgElement.src = fallbackPath;
                    } else {
                        console.log('❌ Todas las alternativas fallaron, ocultando imagen');
                        imgElement.style.display = 'none';
                    }
                };

                // Reasignar el onerror para que siga intentando
                imgElement.onerror = tryFallback;
                tryFallback();
            };

            imgElement.onload = function() {
                console.log('✅ Imagen cargada correctamente:', imgPath);
            };
        } else {
            imgElement.style.display = 'none';
            console.log('🖼️ No hay imagen para esta ruta');
        }
    }

    // Manejar video
    const videoContainer = document.getElementById('videoContainer');
    const videoUrlContainer = document.getElementById('videoUrlContainer');
    const videoUrlLink = document.getElementById('videoUrlLink');

    if (ruta.videoUrl && String(ruta.videoUrl).trim() !== '') {
        const videoUrl = String(ruta.videoUrl).trim();

        // Verificar si es YouTube o Vimeo para embeber
        if (videoUrl.includes('youtube.com') || videoUrl.includes('youtu.be')) {
            let videoId = '';
            if (videoUrl.includes('youtu.be/')) {
                videoId = videoUrl.split('youtu.be/')[1].split('?')[0];
            } else if (videoUrl.includes('watch?v=')) {
                videoId = videoUrl.split('watch?v=')[1].split('&')[0];
            }

            if (videoId && videoContainer) {
                videoContainer.innerHTML = `
                    <div class="ratio ratio-16x9">
                        <iframe src="https://www.youtube.com/embed/${videoId}" allowfullscreen></iframe>
                    </div>`;
                videoContainer.style.display = 'block';
            }
        } else if (videoUrl.includes('vimeo.com')) {
            const vimeoId = videoUrl.split('vimeo.com/')[1];
            if (vimeoId && videoContainer) {
                videoContainer.innerHTML = `
                    <div class="ratio ratio-16x9">
                        <iframe src="https://player.vimeo.com/video/${vimeoId}" allowfullscreen></iframe>
                    </div>`;
                videoContainer.style.display = 'block';
            }
        } else {
            // Para otros videos, mostrar solo el enlace y no embeber
            if (videoContainer) videoContainer.style.display = 'none';
        }

        // Siempre mostrar el enlace
        if (videoUrlLink && videoUrlContainer) {
            videoUrlLink.href = videoUrl;
            videoUrlLink.textContent = videoUrl;
            videoUrlContainer.style.display = 'block';
        }
    } else {
        // No hay video
        if (videoContainer) videoContainer.style.display = 'none';
        if (videoUrlContainer) videoUrlContainer.style.display = 'none';
    }

    // ✅ MOSTRAR la sección de información
    const infoRutaSection = document.getElementById('infoRuta');
    if (infoRutaSection) {
        infoRutaSection.style.display = 'block';
    }
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
// Variables globales
let rutaSeleccionada = null;
let vueloSeleccionado = null;
let usuarioInfo = null;
let rutasCargadas = []; // <--- guarda las rutas reales de la aerolínea

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    cargarAerolineas();
    cargarCategorias(); // <-- agregado: cargar las categorías al iniciar
    configurarEventListeners();
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
        })
        .catch(err => {
            console.error("Error al cargar aerolíneas:", err);
        });
}

// Nueva función: cargar categorías desde el servlet ListarCategoriasServlet (ruta: /listarCategorias)
function cargarCategorias() {
    fetch('listarCategorias')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('categoria');
            // Si no existe el select, evitamos errores
            if (!select) return;
            select.innerHTML = '<option value="">Todas las categorías</option>';
            data.forEach(c => {
                // c.nombre según lo que devuelve tu servlet
                select.innerHTML += `<option value="${c.nombre}">${c.nombre}</option>`;
            });
        })
        .catch(err => {
            console.error("Error al cargar categorías:", err);
        });
}

function configurarEventListeners() {
    // Aerolínea -> rutas
    document.getElementById('aerolinea').addEventListener('change', function() {
        const aerolinea = this.value;
        const rutasList = document.getElementById('listaRutas');
        const vuelosList = document.getElementById('vuelosAsociados');

        rutasList.innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">Cargando rutas...</p></div>';
        vuelosList.innerHTML = '';
        document.getElementById('infoRuta').style.display = 'none';
        document.getElementById('infoVuelo').style.display = 'none';
        rutaSeleccionada = null;
        vueloSeleccionado = null;

        if (aerolinea) {
            fetch('api/rutas?aerolinea=' + encodeURIComponent(aerolinea))
                .then(res => res.json())
                .then(data => {
                    rutasCargadas = data; // <--- guardar todas las rutas cargadas
                    aplicarFiltros(); // aplicar filtro de categoría localmente (si hay)
                })
                .catch(err => {
                    console.error("Error al cargar rutas:", err);
                    rutasList.innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">Error al cargar rutas</p></div>';
                });
        } else {
            rutasList.innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">Seleccione una aerolínea para ver las rutas</p></div>';
            rutasCargadas = [];
        }
    });

    // Botón aplicar filtros
    const btnAplicar = document.getElementById('btnAplicarFiltros');
    if (btnAplicar) btnAplicar.addEventListener('click', function() {
        aplicarFiltros();
    });

    // Botón limpiar
    const btnLimpiar = document.getElementById('btnLimpiar');
    if (btnLimpiar) btnLimpiar.addEventListener('click', function() {
        limpiarFiltros();
    });

    // Aplicar filtro cuando se cambia la categoría (UX: filtrado instantáneo)
    const selectCategoria = document.getElementById('categoria');
    if (selectCategoria) {
        selectCategoria.addEventListener('change', function() {
            aplicarFiltros();
        });
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
    // Buscar la ruta seleccionada en el arreglo de rutas cargadas
    const ruta = rutasCargadas.find(r => r.nombre === nombreRuta);

    if (!ruta) {
        console.error("Ruta no encontrada:", nombreRuta);
        return;
    }

    rutaSeleccionada = ruta;
    mostrarDetallesRuta(ruta);
    cargarVuelosRuta(nombreRuta);

    // Remover selección anterior y marcar actual
    document.querySelectorAll('.ruta-card').forEach(card => {
        card.classList.remove('border-primary', 'bg-light');
    });
    // event puede no estar definido en algunos contextos; si falla, no romper
    try {
        event.currentTarget.classList.add('border-primary', 'bg-light');
    } catch (e) {
        // no hacemos nada si no existe event
    }
}

function mostrarDetallesRuta(ruta) {
    // Actualizar información de la ruta con datos reales
    console.log("Datos de la ruta recibidos:", ruta);
    console.log("Video URL:", ruta.videoUrl);
    console.log("Imagen URL:", ruta.imagenUrl);
    // PRUEBA: Mostrar TODOS los campos de la ruta en la consola
    console.log("=== TODOS LOS CAMPOS DE LA RUTA ===");
    for (let key in ruta) {
        console.log(`📌 ${key}:`, ruta[key]);
    }
    console.log("===================================");
    document.getElementById('rutaNombre').textContent = ruta.nombre || '-';
    document.getElementById('rutaDescripcion').textContent = ruta.descripcion || 'Sin descripción';
    // si el select de aerolinea no tiene texto (por ejemplo value ''), poner '-'
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

    // ---------- Imagen de la ruta (nuevo) ----------
    // Crear o reutilizar un contenedor para la imagen dentro de #infoRuta
    const infoRutaEl = document.getElementById('infoRuta');
    if (infoRutaEl) {
        let imgContainer = document.getElementById('rutaImagenContainer');
        if (!imgContainer) {
            imgContainer = document.createElement('div');
            imgContainer.id = 'rutaImagenContainer';
            imgContainer.className = 'mb-3';
            // Insertarlo al principio de infoRuta
            infoRutaEl.insertAdjacentElement('afterbegin', imgContainer);
        }

        // Crear o reutilizar la etiqueta img
        let imgEl = document.getElementById('imagenRutaDetalle');
        if (!imgEl) {
            imgEl = document.createElement('img');
            imgEl.id = 'imagenRutaDetalle';
            imgEl.alt = 'Imagen de la ruta';
            imgEl.className = 'img-fluid route-image w-100 rounded';
            imgContainer.innerHTML = '';
            imgContainer.appendChild(imgEl);
        }

        // Obtener posible valor de imagen desde el objeto ruta (varias claves posibles)
        const imageValue = ruta.imagen || ruta.imagenUrl || ruta.imagenURL || ruta.image || ruta.foto || ruta.url || '';

        // Helper: construir candidatos de URL para intentar cargar
        function buildImageCandidates(img) {
            const candidates = [];
            const trimmed = String(img || '').trim();
            if (!trimmed) return candidates;
            const origin = window.location.origin || (window.location.protocol + '//' + window.location.host);
            const contextPath = window.location.pathname.replace(/\/[^/]*$/, '');
            const base = (window.SESSION_API_BASE && window.SESSION_API_BASE.length) ? window.SESSION_API_BASE : (origin + contextPath);
            const baseWithSlash = base.endsWith('/') ? base : base + '/';

            // Si es absoluto o data:, usarlo directo
            if (/^(https?:)?\/\//i.test(trimmed) || trimmed.startsWith('data:')) {
                candidates.push(trimmed);
                return Array.from(new Set(candidates));
            }

            // Preferir resolver relativo al app (si el backend devuelve rutas relativas)
            try {
                // Si la JSP inyectó window.CONTEXT_PATH, usarla también como candidato preferente
                const ctx = (typeof window !== 'undefined' && window.CONTEXT_PATH) ? window.CONTEXT_PATH : contextPath;
                const ctxWithSlash = ctx && ctx.endsWith('/') ? ctx : (ctx ? ctx + '/' : '/');
                // candidate: contextPath/Images/trimmed
                try { candidates.push(origin + (ctxWithSlash.startsWith('/') ? '' : '') + ctxWithSlash + 'Images/' + trimmed); } catch (e) { }

                candidates.push(new URL(trimmed, baseWithSlash).href);
            } catch (e) { /* ignore */ }

            // Prueba con /Images/<file> relativo al origen y al contexto base
            try { candidates.push(new URL('Images/' + trimmed, origin + '/').href); } catch (e) { }
            try { candidates.push(new URL('Images/' + trimmed, baseWithSlash).href); } catch (e) { }

            // Heurística: probar variantes comunes de nombres de contexto que aparecen en despliegues
            try {
                // extraer primer segmento del pathname (ej: /WebAppApache_war_exploded/...)
                const pathParts = window.location.pathname.split('/').filter(Boolean);
                if (pathParts.length > 0) {
                    const first = pathParts[0];
                    candidates.push(origin + '/' + first + '/Images/' + trimmed);
                    // variantes comunes
                    candidates.push(origin + '/' + first + '_war_exploded/Images/' + trimmed);
                    candidates.push(origin + '/' + first + '-1.0-SNAPSHOT/Images/' + trimmed);
                }
            } catch (e) { /* ignore */ }

            // También probar nombres usados en build/target del proyecto
            try {
                candidates.push(origin + '/WebAppApache_war_exploded/Images/' + trimmed);
                candidates.push(origin + '/WebAppApache-1.0-SNAPSHOT/Images/' + trimmed);
            } catch (e) { /* ignore */ }

            // Por último probar trimmed relativo a la página
            try { candidates.push(new URL(trimmed, window.location.href).href); } catch (e) { }

            return Array.from(new Set(candidates.filter(Boolean)));
        }

        // Helper: probar candidatos secuencialmente y asignar la primera que cargue
        function tryLoadCandidates(candidates, imgElement, placeholderUrl) {
            let idx = 0;
            let finished = false;

            function next() {
                if (finished) return;
                if (idx >= candidates.length) {
                    finished = true;
                    imgElement.src = placeholderUrl;
                    imgElement.style.objectFit = 'contain';
                    imgElement.style.display = 'block';
                    return;
                }

                const url = candidates[idx++];
                const tester = new Image();
                tester.onload = function() {
                    if (finished) return;
                    finished = true;
                    imgElement.src = url;
                    imgElement.style.display = 'block';
                    imgElement.style.objectFit = '';
                };
                tester.onerror = function() {
                    next();
                };
                tester.src = url;
            }

            next();
        }

        // Construir placeholder
        const origin2 = window.location.origin || (window.location.protocol + '//' + window.location.host);
        const contextPath2 = window.location.pathname.replace(/\/[^/]*$/, '');
        const base2 = (window.SESSION_API_BASE && window.SESSION_API_BASE.length) ? window.SESSION_API_BASE : (origin2 + contextPath2);
        const baseWithSlash2 = base2.endsWith('/') ? base2 : base2 + '/';
        const placeholderUrl = new URL('Images/placeholder.svg', baseWithSlash2).href;

        // Lógica principal: asignar imagen según valor
        const absRe = /^(https?:)?\/\//i;
        if (imageValue && absRe.test(String(imageValue).trim())) {
            const url = String(imageValue).trim();
            console.log('Imagen de ruta: asignando URL absoluta ->', url);
            imgEl.src = url;
            imgEl.style.display = 'block';
            imgEl.onerror = function() {
                console.warn('La imagen absoluta de ruta falló, usando placeholder:', url);
                imgEl.src = placeholderUrl;
                imgEl.style.objectFit = 'contain';
            };
        } else if (imageValue) {
            const candidates = buildImageCandidates(imageValue);
            console.log('Imagen de ruta - candidatos (usando field):', imageValue, candidates);
            tryLoadCandidates(candidates, imgEl, placeholderUrl);
            imgEl.onerror = function() {
                imgEl.src = placeholderUrl;
                imgEl.style.objectFit = 'contain';
            };
        } else {
            // No hay imagen definida -> usar placeholder
            imgEl.src = placeholderUrl;
            imgEl.style.display = 'block';
            imgEl.style.objectFit = 'contain';
        }
    }

    // ---------- Renderizar video de la ruta ----------
    renderizarVideo(ruta.videoUrl);

    // Mostrar sección de información de la ruta
    document.getElementById('infoRuta').style.display = 'block';
    document.getElementById('infoVuelo').style.display = 'none';

    // Scroll a la información de la ruta
    document.getElementById('infoRuta').scrollIntoView({ behavior: 'smooth' });
}

function renderizarVideo(videoUrl) {
    let cont = document.getElementById('videoContainer');
    let urlCont = document.getElementById('videoUrlContainer'); // ✅ Nuevo contenedor

    // Crear contenedores si no existen
    if (!cont) {
        cont = document.createElement('div');
        cont.id = 'videoContainer';
        cont.className = 'mt-3';
        const infoRutaCard = document.querySelector('#infoRuta .card-body');
        infoRutaCard.appendChild(cont);
    }

    if (!urlCont) {
        urlCont = document.createElement('div');
        urlCont.id = 'videoUrlContainer';
        urlCont.className = 'mb-3';
        const infoRutaCard = document.querySelector('#infoRuta .card-body');
        // Insertar después de la imagen y antes del video embebido
        const imagenContainer = document.getElementById('imagenRutaDetalle');
        if (imagenContainer) {
            imagenContainer.parentNode.insertBefore(urlCont, imagenContainer.nextSibling);
        } else {
            infoRutaCard.insertBefore(urlCont, cont);
        }
    }

    cont.innerHTML = ""; // limpiar contenedor de video embebido
    urlCont.style.display = 'none'; // ocultar contenedor de URL por defecto

    if (!videoUrl || videoUrl.trim() === "") {
        // Ocultar ambos contenedores si no hay video
        cont.style.display = 'none';
        urlCont.style.display = 'none';
        return;
    }

    videoUrl = videoUrl.trim();
    console.log("🎥 Procesando video URL:", videoUrl);

    // SIEMPRE mostrar el enlace URL (incluso si es YouTube/Vimeo)
    const videoUrlLink = document.getElementById('videoUrlLink') || (() => {
        const link = document.createElement('a');
        link.id = 'videoUrlLink';
        link.target = '_blank';
        link.className = 'text-break';
        return link;
    })();

    videoUrlLink.href = videoUrl;
    videoUrlLink.textContent = videoUrl;

    // Configurar el contenedor de URL
    urlCont.innerHTML = `
        <div class="card bg-light">
            <div class="card-body">
                <h6 class="card-title">
                    <i class="bi bi-camera-video text-primary"></i> 
                    Enlace de Video de la Ruta
                </h6>
                <p class="mb-1"><strong>URL del video:</strong></p>
                <a href="${videoUrl}" target="_blank" class="text-break">
                    ${videoUrl}
                </a>
                <div class="mt-2">
                    <small class="text-muted">
                        <i class="bi bi-info-circle"></i> 
                        Haz clic para ver el video en una nueva pestaña
                    </small>
                </div>
            </div>
        </div>
    `;
    urlCont.style.display = 'block';

    // YouTube
    if (videoUrl.includes("youtube.com") || videoUrl.includes("youtu.be")) {
        // función robusta para extraer ID de YouTube desde varias formas de URL
        function getYouTubeId(url) {
            if (!url) return null;
            try {
                const u = new URL(url);
                const host = u.hostname.toLowerCase();
                // youtu.be/ID
                if (host === 'youtu.be') {
                    return u.pathname.replace(/^\//, '');
                }
                // youtube.com - buscar parámetro v
                if (host.includes('youtube.com')) {
                    const v = u.searchParams.get('v');
                    if (v) return v;
                    // /embed/ID
                    const parts = u.pathname.split('/').filter(Boolean);
                    const embIdx = parts.indexOf('embed');
                    if (embIdx !== -1 && parts.length > embIdx + 1) return parts[embIdx + 1];
                }
            } catch (e) {
                // no es una URL válida: intentar fallback por regex
                const m = url.match(/(?:v=|\/embed\/|youtu\.be\/)([A-Za-z0-9_-]{11})/);
                if (m) return m[1];
            }
            return null;
        }

        const videoId = getYouTubeId(videoUrl);
        console.log('🎯 YouTube ID extraído (consulta-ruta):', videoId);

        if (videoId) {
            cont.innerHTML = `
                <div class="mb-2">
                    <small class="text-muted"><i class="bi bi-youtube text-danger"></i> Video de YouTube (reproductor embebido)</small>
                </div>
                <div class="ratio ratio-16x9">
                    <iframe 
                        src="https://www.youtube.com/embed/${videoId}" 
                        title="Video de la ruta ${rutaSeleccionada ? rutaSeleccionada.nombre : ''}"
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                        allowfullscreen>
                    </iframe>
                </div>`;
            cont.style.display = 'block';
            return;
        }
    }

    // Vimeo
    if (videoUrl.includes("vimeo.com")) {
        let videoId = videoUrl.split("/").pop();
        // ... (código existente para Vimeo) ...

        if (videoId) {
            cont.innerHTML = `
                <div class="mb-2">
                    <small class="text-muted"><i class="bi bi-camera-video text-primary"></i> Video de Vimeo (reproductor embebido)</small>
                </div>
                <div class="ratio ratio-16x9">
                    <iframe 
                        src="https://player.vimeo.com/video/${videoId}" 
                        title="Video de la ruta ${rutaSeleccionada ? rutaSeleccionada.nombre : ''}"
                        allow="autoplay; fullscreen; picture-in-picture" 
                        allowfullscreen>
                    </iframe>
                </div>`;
            cont.style.display = 'block';
            return;
        }
    }

    // Archivo MP4 u otro video directo
    if (videoUrl.match(/\.(mp4|webm|ogg|mov|avi|wmv)(\?.*)?$/i)) {
        cont.innerHTML = `
            <div class="mb-2">
                <small class="text-muted"><i class="bi bi-film text-info"></i> Video de la ruta (reproductor nativo)</small>
            </div>
            <video controls class="w-100 rounded" style="max-height:400px; background:#000;">
                <source src="${videoUrl}" type="video/mp4">
                <source src="${videoUrl}" type="video/webm">
                <source src="${videoUrl}" type="video/ogg">
                Tu navegador no soporta la reproducción de video.
            </video>`;
        cont.style.display = 'block';
        return;
    }

    // Si no coincide con ningún formato conocido, solo mostrar el enlace (ya está visible)
    cont.style.display = 'none'; // Ocultar contenedor de video embebido
    console.log("ℹ️ Video URL no reconocido para embed, mostrando solo enlace:", videoUrl);
}


function cargarVuelosRuta(nombreRuta) {
    const vuelosContainer = document.getElementById('vuelosAsociados');
    vuelosContainer.innerHTML = '<div class="col-12 text-center py-2"><p class="text-muted">Cargando vuelos...</p></div>';

    fetch(`api/vuelos?ruta=${encodeURIComponent(nombreRuta)}`)
        .then(res => res.json())
        .then(vuelos => {
            vuelosContainer.innerHTML = '';

            if (!vuelos || vuelos.length === 0) {
                vuelosContainer.innerHTML = '<div class="col-12 text-center py-2"><p class="text-muted">No hay vuelos disponibles para esta ruta</p></div>';
                return;
            }

            vuelos.forEach(vuelo => {
                const vueloHTML = `
                    <div class="col-md-4 mb-3">
                        <div class="card border-success h-100" onclick="seleccionarVuelo('${vuelo.nombre}')" style="cursor: pointer;">
                            <div class="card-body text-center">
                                <h6 class="card-title">${vuelo.nombre}</h6>
                                <p class="mb-1 small text-muted">Vuelo disponible</p>
                                <span class="badge bg-success">Disponible</span>
                            </div>
                        </div>
                    </div>
                `;
                vuelosContainer.innerHTML += vueloHTML;
            });
        })
        .catch(err => {
            console.error("Error cargando vuelos:", err);
            vuelosContainer.innerHTML = '<div class="col-12 text-center py-2"><p class="text-muted">Error al cargar vuelos</p></div>';
        });
}

function seleccionarVuelo(nombreVuelo) {
    // Cargar información completa del vuelo
    fetch(`api/vuelo?nombre=${encodeURIComponent(nombreVuelo)}`)
        .then(res => res.json())
        .then(data => {
            vueloSeleccionado = data;
            mostrarDetallesVuelo(data);
            verificarPermisosUsuario(nombreVuelo, document.getElementById('aerolinea').value);
        })
        .catch(err => {
            console.error("Error cargando detalles del vuelo:", err);
            mostrarMensajeError('Error al cargar detalles del vuelo');
        });

    // Remover selección anterior de vuelos y marcar actual
    document.querySelectorAll('#vuelosAsociados .card').forEach(card => {
        card.classList.remove('border-warning', 'bg-light');
    });
    try {
        event.currentTarget.classList.add('border-warning', 'bg-light');
    } catch (e) {
        // ignore
    }
}

function mostrarDetallesVuelo(vueloData) {
    // Actualizar información del vuelo
    document.getElementById('nombreVueloDetalle').textContent = vueloData.nombre || '-';
    const aerSelect = document.getElementById('aerolinea');
    const aerText = (aerSelect && aerSelect.options[aerSelect.selectedIndex]) ? aerSelect.options[aerSelect.selectedIndex].text : '-';
    document.getElementById('aerolineaVueloDetalle').textContent = aerText;
    document.getElementById('rutaVueloDetalle').textContent = rutaSeleccionada ? rutaSeleccionada.nombre : '-';
    document.getElementById('fechaVueloDetalle').textContent = vueloData.fecha || '-';
    document.getElementById('duracionVueloDetalle').textContent = vueloData.duracion || '-';
    document.getElementById('asientosTuristaDetalle').textContent = vueloData.asientosTurista !== undefined ? vueloData.asientosTurista : '-';
    document.getElementById('asientosEjecutivoDetalle').textContent = vueloData.asientosEjecutivo !== undefined ? vueloData.asientosEjecutivo : '-';
    document.getElementById('estadoVueloDetalle').textContent = vueloData.estado || 'Confirmado';

    // Mostrar sección de información del vuelo
    document.getElementById('infoVuelo').style.display = 'block';

    // Scroll a la información del vuelo
    document.getElementById('infoVuelo').scrollIntoView({ behavior: 'smooth' });
}

// Verificar permisos del usuario para el vuelo
function verificarPermisosUsuario(nombreVuelo, aerolineaSeleccionada) {
    // Primero verificar sesión actual
    fetch('api/check-session', { credentials: 'include' })
        .then(res => res.json())
        .then(sessionData => {
            if (sessionData.authenticated) {
                // Si está autenticado, verificar permisos específicos del vuelo
                return fetch(`api/verificar-permisos-vuelo?nombreVuelo=${encodeURIComponent(nombreVuelo)}&aerolinea=${encodeURIComponent(aerolineaSeleccionada)}`)
                    .then(res => res.json());
            } else {
                return { autenticado: false };
            }
        })
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
    if (infoAerolinea) infoAerolinea.style.display = 'none';
    if (infoCliente) infoCliente.style.display = 'none';
    if (btnReservar) btnReservar.style.display = 'none';

    if (usuarioData.autenticado) {
        if (usuarioData.esAerolineaDueña) {
            // Es la aerolínea que publicó el vuelo - mostrar gestión de reservas
            if (infoAerolinea) infoAerolinea.style.display = 'block';
            cargarReservasVuelo(nombreVuelo);
        } else if (usuarioData.tieneReservaCliente) {
            // Es un cliente con reserva en este vuelo
            if (infoCliente) infoCliente.style.display = 'block';

            // Configurar el botón para ver los detalles de la reserva
            const btnVerReserva = infoCliente ? infoCliente.querySelector('button') : null;
            if (btnVerReserva) {
                btnVerReserva.onclick = function() {
                    verDetalleReservaCliente(usuarioData.idReservaCliente);
                };
            }
        } else {
            // Usuario autenticado pero sin reserva - mostrar opción de reserva
            if (btnReservar) btnReservar.style.display = 'block';
        }
    } else {
        // Usuario no autenticado - mostrar opción de reserva
        if (btnReservar) btnReservar.style.display = 'block';
    }
}

// Cargar reservas del vuelo (para aerolíneas)
function cargarReservasVuelo(nombreVuelo) {
    fetch(`api/reservas-vuelo?nombreVuelo=${encodeURIComponent(nombreVuelo)}`)
        .then(res => res.json())
        .then(reservas => {
            const totalReservas = document.getElementById('totalReservas');
            if (totalReservas) totalReservas.textContent = reservas.length;

            // Actualizar el botón para mostrar detalles
            const btnVerReservas = document.querySelector('#infoAerolinea button');
            if (btnVerReservas) {
                btnVerReservas.onclick = function() {
                    mostrarDetallesReservas(reservas);
                };
            }
        })
        .catch(err => {
            console.error("Error cargando reservas:", err);
            const totalReservas = document.getElementById('totalReservas');
            if (totalReservas) totalReservas.textContent = '0';
        });
}

// Función para ver el detalle de la reserva del cliente
function verDetalleReservaCliente(idReserva) {
    if (!idReserva) {
        mostrarMensajeError('No se pudo identificar la reserva');
        return;
    }

    // Usar el mismo endpoint que funciona en consulta-reserva
    fetch(`api/consulta-reserva?action=reserva-cliente-vuelo&usuario=${encodeURIComponent(usuarioInfo.nickname)}&vuelo=${encodeURIComponent(vueloSeleccionado.nombre)}`)
        .then(res => res.json())
        .then(data => {
            if (data.error) {
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

// Mostrar modal con detalles de reservas
function mostrarDetallesReservas(reservas) {
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
        // Usar los nombres de campos correctos como en consulta-vuelo
        const clienteDisplay = reserva.clienteNombre || reserva.cliente || 'Cliente no disponible';
        const costoDisplay = reserva.costoTotal !== undefined ? reserva.costoTotal : (reserva.costo || 0);

        contenido += `
            <tr>
                <td>${reserva.id}</td>
                <td>${clienteDisplay}</td>
                <td>${reserva.tipoAsiento}</td>
                <td>${reserva.cantidadPasajes}</td>
                <td>$${costoDisplay}</td>
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
                        <h5 class="modal-title">Detalles de Reservas - ${vueloSeleccionado ? vueloSeleccionado.nombre : ''}</h5>
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

// Mostrar modal con detalles de la reserva del cliente
function mostrarModalDetalleReserva(reserva) {
    const contenido = `
        <div class="detalle-reserva">
            <h5 class="text-primary mb-3">Detalles de tu Reserva</h5>
            <div class="row">
                <div class="col-6">
                    <p><strong>ID Reserva:</strong> ${reserva.id}</p>
                    <p><strong>Vuelo:</strong> ${vueloSeleccionado ? vueloSeleccionado.nombre : ''}</p>
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

// --------------------------------------------------
// A PARTIR DE AQUÍ: filtrado local por categoría (sin tocar servlets)
// --------------------------------------------------

// Aplicar filtros: ahora filtra localmente usando rutasCargadas
function aplicarFiltros() {
    const aerolinea = document.getElementById('aerolinea') ? document.getElementById('aerolinea').value : '';
    const categoria = document.getElementById('categoria') ? document.getElementById('categoria').value : '';

    if (!aerolinea) {
        mostrarMensajeError('Por favor seleccione una aerolínea primero');
        return;
    }

    if (!rutasCargadas || rutasCargadas.length === 0) {
        // Si por alguna razón no hay rutas cargadas, intentamos cargar una vez más
        fetch('/api/rutas?aerolinea=' + encodeURIComponent(aerolinea))
            .then(res => res.json())
            .then(data => {
                rutasCargadas = data;
                // aplicar filtro local ahora
                let rutasFiltradas = rutasCargadas;
                if (categoria && categoria !== '') {
                    rutasFiltradas = rutasCargadas.filter(r => r.categorias && r.categorias.includes(categoria));
                }
                cargarRutas(rutasFiltradas);
                document.getElementById('infoRuta').style.display = 'none';
                document.getElementById('infoVuelo').style.display = 'none';
                rutaSeleccionada = null;
                vueloSeleccionado = null;
            })
            .catch(err => {
                console.error("Error aplicando filtros (fetch fallback):", err);
                mostrarMensajeError('Error al aplicar filtros');
            });
        return;
    }

    // Filtrado local
    let rutasFiltradas = rutasCargadas;
    if (categoria && categoria !== '') {
        rutasFiltradas = rutasCargadas.filter(r => r.categorias && r.categorias.includes(categoria));
    }

    cargarRutas(rutasFiltradas);
    document.getElementById('infoRuta').style.display = 'none';
    document.getElementById('infoVuelo').style.display = 'none';
    rutaSeleccionada = null;
    vueloSeleccionado = null;
}

// Limpiar filtros: restablece la vista localmente sin llamar al backend
function limpiarFiltros() {
    document.getElementById('aerolinea').value = '';
    document.getElementById('categoria').value = '';
    rutasCargadas = []; // limpiar cache local porque quitamos aerolínea
    document.getElementById('listaRutas').innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">Seleccione una aerolínea para ver las rutas</p></div>';
    document.getElementById('vuelosAsociados').innerHTML = '';
    document.getElementById('infoRuta').style.display = 'none';
    document.getElementById('infoVuelo').style.display = 'none';
    rutaSeleccionada = null;
    vueloSeleccionado = null;
}

// Mostrar toast de error (igual que antes)
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
// Variables globales
let rutaSeleccionada = null;
let vueloSeleccionado = null;
let usuarioInfo = null;
let rutasCargadas = []; // <--- guarda las rutas reales de la aerolínea

// Función para restaurar estado al recargar
function restaurarEstado() {
    const aerolineaGuardada = sessionStorage.getItem('consultaRuta_aerolinea');
    const categoriaGuardada = sessionStorage.getItem('consultaRuta_categoria');
    const rutaSeleccionadaGuardada = sessionStorage.getItem('consultaRuta_rutaSeleccionada');

    if (aerolineaGuardada) {
        // Establecer el valor primero
        document.getElementById('aerolinea').value = aerolineaGuardada;

        // Esperar a que el DOM se actualice y luego cargar rutas
        setTimeout(() => {
            // Disparar el cambio para cargar rutas
            const event = new Event('change');
            document.getElementById('aerolinea').dispatchEvent(event);

            // Esperar a que las rutas se carguen antes de restaurar selecciones
            const checkRutasCargadas = setInterval(() => {
                if (rutasCargadas.length > 0) {
                    clearInterval(checkRutasCargadas);

                    // Restaurar categoría
                    if (categoriaGuardada) {
                        document.getElementById('categoria').value = categoriaGuardada;
                        // Aplicar filtros si hay categoría
                        setTimeout(() => aplicarFiltros(), 100);
                    }

                    // Restaurar ruta seleccionada
                    if (rutaSeleccionadaGuardada) {
                        const ruta = rutasCargadas.find(r => r.nombre === rutaSeleccionadaGuardada);
                        if (ruta) {
                            // Esperar un poco más para asegurar que la UI esté lista
                            setTimeout(() => {
                                seleccionarRuta(rutaSeleccionadaGuardada);
                            }, 300);
                        }
                    }
                }
            }, 100);

            // Timeout de seguridad
            setTimeout(() => clearInterval(checkRutasCargadas), 3000);
        }, 200);
    }
}

// Inicialización CORREGIDA
document.addEventListener('DOMContentLoaded', function() {
    cargarAerolineas();
    cargarCategorias();
    configurarEventListeners();

    // Restaurar estado después de que todo esté configurado
    setTimeout(restaurarEstado, 500);
});

// Cargar aerolíneas desde backend
function cargarAerolineas() {
    fetch((window.CONTEXT_PATH || '') + '/api/aerolineas')
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
    // ========== GUARDAR EN SESSIONSTORAGE ==========
    document.getElementById('aerolinea').addEventListener('change', function() {
        sessionStorage.setItem('consultaRuta_aerolinea', this.value);
        sessionStorage.removeItem('consultaRuta_rutaSeleccionada'); // Limpiar ruta al cambiar aerolínea
    });

    document.getElementById('categoria').addEventListener('change', function() {
        sessionStorage.setItem('consultaRuta_categoria', this.value);
    });
    // ========== FIN GUARDAR EN SESSIONSTORAGE ==========

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
            fetch((window.CONTEXT_PATH || '') + '/api/rutas?aerolinea=' + encodeURIComponent(aerolinea))
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

    // Botón limpiar - CORREGIDO
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

// Función limpiarFiltros COMPLETA - AÑADIR ESTA FUNCIÓN
function limpiarFiltros() {
    // Limpiar sessionStorage
    sessionStorage.removeItem('consultaRuta_aerolinea');
    sessionStorage.removeItem('consultaRuta_categoria');
    sessionStorage.removeItem('consultaRuta_rutaSeleccionada');

    // Limpiar UI
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

// [El resto de tus funciones permanecen igual: cargarRutas, seleccionarRuta, mostrarDetallesRuta, etc.]

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

    // ✅ UNA SOLA LLAMADA: Obtener detalles Y contar visita
    fetch(`${CONTEXT_PATH}/consultaRuta?nombreRuta=${encodeURIComponent(nombreRuta)}`)
        .then(response => {
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            return response.json();
        })
        .then(rutaDetallada => {
            if (rutaDetallada.error) {
                throw new Error(rutaDetallada.error);
            }

            console.log('✅ Detalles de ruta obtenidos (visita contada):', rutaDetallada);

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
            // Fallback a datos locales
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

// [Mantén el resto de tus funciones como están...]

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

        // Helper: convertir un valor devuelto por el backend en una URL pública de imagen
        function toPublicImageUrl(value) {
            if (!value) return '';
            const v = String(value).trim();
            if (v.length === 0) return '';
            try {
                // Si es data URL, devolver tal cual
                if (/^data:/i.test(v)) return v;
                // Si es URL absoluta, analizarla para decidir si normalizar
                if (/^https?:\/\//i.test(v)) {
                    try {
                        const parsed = new URL(v);
                        const origin = window.location.origin || (window.location.protocol + '//' + window.location.host);
                        // Si la URL absoluta apunta al mismo origen del navegador, extraer filename y construir con contextPath
                        if (parsed.origin === origin || parsed.pathname.indexOf('/Images/') >= 0 || parsed.pathname.split('/').length <= 2) {
                            // extraer filename
                            const path = parsed.pathname || '';
                            const parts = path.split('/').filter(Boolean);
                            const filename = parts.length > 0 ? parts[parts.length - 1] : '';
                            if (filename) {
                                const ctx = (typeof window !== 'undefined' && window.CONTEXT_PATH) ? window.CONTEXT_PATH : window.location.pathname.replace(/\/[^/]*$/, '');
                                const prefix = (ctx.endsWith('/')) ? ctx.slice(0, -1) : ctx;
                                return prefix + '/Images/' + filename;
                            }
                        }
                        // si no coincide con el origen o no contiene un filename, devolver la URL tal cual
                        return v;
                    } catch (e) {
                        return v;
                    }
                }
                // si ya tiene un slash al inicio (ruta absoluta dentro del host), intentar extraer filename si contiene Images/
                if (v.startsWith('/')) {
                    if (v.indexOf('/Images/') >= 0) {
                        const parts = v.split('/').filter(Boolean);
                        const filename = parts.length > 0 ? parts[parts.length - 1] : '';
                        if (filename) {
                            const ctx = (typeof window !== 'undefined' && window.CONTEXT_PATH) ? window.CONTEXT_PATH : window.location.pathname.replace(/\/[^/]*$/, '');
                            const prefix = (ctx.endsWith('/')) ? ctx.slice(0, -1) : ctx;
                            return prefix + '/Images/' + filename;
                        }
                    }
                    // si no contiene Images/, devolver como filename simple
                    return v;
                }
                // para cualquier otro caso, devolver como filename simple
                return v;
            } catch (e) {
                return '';
            }
        }

        // Lógica principal: asignar imagen según valor
        if (imageValue) {
            const raw = String(imageValue).trim();
            // Resolver a URL pública usando helper (si es filename o URL absoluta local)
            const resolved = toPublicImageUrl(raw);
            console.log('Imagen de ruta: asignando URL resuelta ->', resolved, ' (raw:', raw, ')');
            imgEl.src = resolved;
            imgEl.style.display = 'block';
            imgEl.style.objectFit = 'cover';
            imgEl.onerror = function() {
                console.warn('La imagen de ruta falló al cargar, ocultando elemento:', resolved);
                imgEl.style.display = 'none';
            };
        } else {
            imgEl.style.display = 'none';
        }
    }

    // ---------- Video de la ruta (nuevo) ----------
    // Usar videoUrl directamente desde el objeto ruta
    const videoUrl = ruta.videoUrl || ruta.video || '';
    const videoUrlContainer = document.getElementById('videoUrlContainer');
    const videoContainer = document.getElementById('videoContainer');

    // helpers para detectar YouTube/Vimeo/MP4
    function getYouTubeEmbed(url) {
        // soporta https://www.youtube.com/watch?v=ID y https://youtu.be/ID
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

    if (videoUrl) {
        // mostrar enlace
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
            } else {
                // si no se puede embeber, dejamos solo el enlace (ya puesto arriba)
            }
        }
    } else {
        if (videoUrlContainer) videoUrlContainer.style.display = 'none';
        if (videoContainer) videoContainer.innerHTML = '';
    }

    // Asegurarse de mostrar el panel de detalles
    try {
        document.getElementById('infoRuta').style.display = 'block';
    } catch (e) {
        // si no existe, no hacemos nada
    }
}

// Filtrar rutas por categoría (localmente, en el cliente)
function aplicarFiltros() {
    const categoriaSeleccionada = document.getElementById('categoria').value;

    // Si no hay rutas cargadas, no hacer nada
    if (!rutasCargadas || rutasCargadas.length === 0) return;

    // Si no se seleccionó ninguna categoría (valor vacío), mostrar todas las rutas
    if (!categoriaSeleccionada || categoriaSeleccionada.trim() === '') {
        cargarRutas(rutasCargadas);
        return;
    }

    // Filtrar rutas por categoría seleccionada
    const rutasFiltradas = rutasCargadas.filter(ruta => {
        if (!ruta.categorias || ruta.categorias.length === 0) return false; // Sin categoría definida no coincide
        return ruta.categorias.some(cat => cat.toString().toLowerCase() === categoriaSeleccionada.toString().toLowerCase());
    });

    cargarRutas(rutasFiltradas);
}

// Limpiar filtros y recargar todas las rutas
function limpiarFiltros() {
    document.getElementById('categoria').value = '';
    aplicarFiltros();
}

// Cargar vuelos asociados a una ruta
function cargarVuelosRuta(nombreRuta) {
    const vuelosList = document.getElementById('vuelosAsociados');
    vuelosList.innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">Cargando vuelos...</p></div>';

    fetch((window.CONTEXT_PATH || '') + '/api/vuelos?ruta=' + encodeURIComponent(nombreRuta))
        .then(res => res.json())
        .then(data => {
            // Si no hay vuelos, mostrar mensaje
            if (!data || data.length === 0) {
                vuelosList.innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">No hay vuelos asociados a esta ruta.</p></div>';
                return;
            }

            // Limpiar lista de vuelos
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

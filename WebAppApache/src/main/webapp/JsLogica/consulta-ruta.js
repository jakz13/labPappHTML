// Variables globales
let rutaSeleccionada = null;
let vueloSeleccionado = null;
let rutasCargadas = [];
let ultimaAerolineaCargada = null;

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    cargarAerolineas();
    cargarCategorias();
    configurarEventListeners();
});

// Cargar aerolíneas desde backend
function cargarAerolineas() {
    const timestamp = new Date().getTime(); // Anti-cache
    fetch(`${window.CONTEXT_PATH || ''}/api/aerolineas?_=${timestamp}`)
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
    const timestamp = new Date().getTime(); // Anti-cache
    fetch(`listarCategorias?_=${timestamp}`)
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

// Modificar el event listener para usar recarga forzada al cambiar aerolínea
function configurarEventListeners() {
    // Aerolínea -> rutas (ACTUALIZADO)
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
            const timestamp = new Date().getTime();
            fetch(`${window.CONTEXT_PATH || ''}/api/rutas?aerolinea=${encodeURIComponent(aerolinea)}&_=${timestamp}`)
                .then(res => {
                    if (!res.ok) throw new Error('Error ' + res.status);
                    return res.json();
                })
                .then(data => {
                    rutasCargadas = data || [];
                    ultimaAerolineaCargada = aerolinea;
                    cargarRutas(rutasCargadas);
                })
                .catch(err => {
                    console.error('Error cargando rutas:', err);
                    rutasList.innerHTML = '<div class="col-12 text-center py-4"><p class="text-danger">Error al cargar rutas</p></div>';
                    rutasCargadas = [];
                });
        } else {
            rutasList.innerHTML = '<div class="col-12 text-center py-4"><p class="text-muted">Seleccione una aerolínea para ver las rutas</p></div>';
            rutasCargadas = [];
            ultimaAerolineaCargada = null;
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
                <p class="text-muted">No hay rutas disponibles para esta aerolínea.</p>
            </div>
        `;
        return;
    }

    rutas.forEach(ruta => {
        const rutaCard = document.createElement('div');
        rutaCard.className = 'col-md-6 mb-3';
        rutaCard.innerHTML = `
            <div class="card ruta-card h-100" style="cursor: pointer;" data-nombre-ruta="${ruta.nombre}">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <h6 class="card-title text-primary">${ruta.nombre}</h6>
                        <span class="badge bg-success">${ruta.estado || 'Confirmada'}</span>
                    </div>
                    <small class="text-muted">${ruta.origen || ''} → ${ruta.destino || ''}</small>
                </div>
            </div>
        `;

        const card = rutaCard.querySelector('.card');
        // Agregar event listener directamente
        card.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            console.log('Click en ruta:', ruta.nombre);
            seleccionarRuta(ruta.nombre, this);
        });

        container.appendChild(rutaCard);
    });

    console.log(`✅ ${rutas.length} rutas cargadas con eventos click`);
}

function seleccionarRuta(nombreRuta, elementoClickeado) {
    const base = (typeof window !== 'undefined' && window.CONTEXT_PATH) ? window.CONTEXT_PATH : '';
    const timestamp = new Date().getTime(); // Anti-cache
    const url = base + '/consultaRuta?nombreRuta=' + encodeURIComponent(nombreRuta) + '&_=' + timestamp;

    console.log(`🔍 Consultando detalles de ruta: ${nombreRuta}`);

    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            return response.json();
        })
        .then(rutaDetallada => {
            if (rutaDetallada.error) {
                throw new Error(rutaDetallada.error);
            }

            rutaSeleccionada = rutaDetallada;
            mostrarDetallesRuta(rutaDetallada);
            cargarVuelosRuta(nombreRuta);

            // Remover selección anterior y marcar actual
            document.querySelectorAll('.ruta-card').forEach(card => {
                card.classList.remove('border-primary', 'bg-light');
            });

            if (elementoClickeado) {
                elementoClickeado.classList.add('border-primary', 'bg-light');
            }

            console.log(`✅ Detalles de ruta cargados: ${nombreRuta}`);
        })
        .catch(error => {
            console.error('❌ Error cargando detalles de ruta:', error);
            const rutaLocal = rutasCargadas.find(r => r.nombre === nombreRuta);
            if (rutaLocal) {
                rutaSeleccionada = rutaLocal;
                mostrarDetallesRuta(rutaLocal);
                cargarVuelosRuta(nombreRuta);

                // Aplicar estilo de selección incluso con datos locales
                document.querySelectorAll('.ruta-card').forEach(card => {
                    card.classList.remove('border-primary', 'bg-light');
                });
                if (elementoClickeado) {
                    elementoClickeado.classList.add('border-primary', 'bg-light');
                }
            } else {
                mostrarMensajeError('No se pudieron cargar los detalles de la ruta');
            }
        });
}

// NUEVAS FUNCIONES DE MENSAJES
function mostrarMensajeExito(mensaje) {
    mostrarMensaje(mensaje, 'success');
}

function mostrarMensajeInfo(mensaje) {
    mostrarMensaje(mensaje, 'info');
}

function mostrarMensajeError(mensaje) {
    mostrarMensaje(mensaje, 'danger');
}

function mostrarMensaje(mensaje, tipo) {
    // Remover mensajes anteriores
    const alertasAnteriores = document.querySelectorAll('.alert-mensaje-temporal');
    alertasAnteriores.forEach(alerta => alerta.remove());

    // Crear nueva alerta
    const alertHTML = `
        <div class="alert alert-${tipo} alert-dismissible fade show alert-mensaje-temporal" role="alert">
            <i class="bi ${tipo === 'success' ? 'bi-check-circle' : tipo === 'info' ? 'bi-info-circle' : 'bi-exclamation-triangle'} me-2"></i>
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;

    const container = document.querySelector('.card-body') || document.body;
    container.insertAdjacentHTML('afterbegin', alertHTML);

    // Auto-eliminar después de 5 segundos
    setTimeout(() => {
        const alerta = document.querySelector('.alert-mensaje-temporal');
        if (alerta) {
            alerta.remove();
        }
    }, 5000);
}

// El resto de las funciones permanecen igual...
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
    document.getElementById('rutaFechaAlta').textContent = fechaMostrada;
    document.getElementById('rutaCategorias').textContent = ruta.categorias && ruta.categorias.length > 0 ? ruta.categorias.join(', ') : 'No especificadas';
    document.getElementById('costoTurista').textContent = ruta.costoTurista !== undefined ? `$${ruta.costoTurista}` : 'N/A';
    document.getElementById('costoEjecutivo').textContent = ruta.costoEjecutivo !== undefined ? `$${ruta.costoEjecutivo}` : 'N/A';
    document.getElementById('costoEquipaje').textContent = ruta.costoEquipaje !== undefined ? `$${ruta.costoEquipaje}` : 'N/A';

    // Manejar imagen
    const imgElement = document.getElementById('imagenRutaDetalle');
    if (imgElement) {
        if (ruta.imagenUrl && ruta.imagenUrl.trim() !== '') {
            // Asegurar que la ruta de imagen sea correcta
            let imgPath = ruta.imagenUrl;
            // Si la ruta no empieza con /, Images/, o http, agregar Images/
            if (!imgPath.startsWith('/') && !imgPath.startsWith('Images/') && !imgPath.startsWith('http')) {
                imgPath = 'Images/' + imgPath;
            }
            imgElement.src = imgPath;
            imgElement.style.display = 'block';
        } else {
            imgElement.style.display = 'none';
        }
    }

    // Manejar video
    const videoContainer = document.getElementById('videoContainer');
    const videoUrlContainer = document.getElementById('videoUrlContainer');
    const videoUrlLink = document.getElementById('videoUrlLink');

    if (ruta.videoUrl && ruta.videoUrl.trim() !== '') {
        const videoUrl = ruta.videoUrl.trim();

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
            // Para otros videos, mostrar solo el enlace
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

// Filtrar rutas por categoría (localmente, en el cliente)
function aplicarFiltros() {
    const categoria = document.getElementById('categoria') ? document.getElementById('categoria').value : '';

    // Si no hay rutas cargadas, recargar desde servidor si hay aerolínea seleccionada
    if (!rutasCargadas || rutasCargadas.length === 0) {
        const aerolinea = document.getElementById('aerolinea').value;
        if (aerolinea) {
            cargarRutas([]);
        }
        return;
    }

    // Si no hay categoría seleccionada, mostrar todas las rutas cargadas
    if (!categoria) {
        cargarRutas(rutasCargadas);
        return;
    }

    // Filtrar por categoría
    const filtradas = rutasCargadas.filter(ruta => {
        if (!ruta.categorias || ruta.categorias.length === 0) return false;
        return ruta.categorias.includes(categoria);
    });

    cargarRutas(filtradas);
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

    const timestamp = new Date().getTime(); //Anti-cache
    fetch(`${window.CONTEXT_PATH || ''}/api/vuelos?ruta=${encodeURIComponent(nombreRuta)}&_=${timestamp}`)
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
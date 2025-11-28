<%@ page contentType="text/html;charset=UTF-8" %>
<%
    // Obtener datos de sesión primero para evitar errores EL
    String usuarioSession = (String) session.getAttribute("nombreUsuario");
    if (usuarioSession == null) {
        usuarioSession = (String) session.getAttribute("usuario");
    }
    if (usuarioSession == null) {
        usuarioSession = "";
    }

    String errorMsg = (String) session.getAttribute("error");
    String successMsg = (String) session.getAttribute("success");
    session.removeAttribute("error");
    session.removeAttribute("success");

    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Finalizar Ruta de Vuelo - Juan Viajes</title>
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <!-- Estilo local (ruta absoluta dentro de la app) -->
    <link rel="stylesheet" href="<%= contextPath %>/CssLogica/estilo-css.css">
    <style>
        .costo-destacado {
            font-size: 1.1em;
            font-weight: bold;
            color: #2c3e50;
        }
        .ruta-card {
            max-width: 1000px;
            width: 100%;
            margin-left: auto;
            margin-right: auto;
        }
        .ruta-card:hover {
            transform: translateY(-2px);
        }
        .badge-finalizable {
            background-color: #ffc107;
            color: #000;
        }
        .badge-no-finalizable {
            background-color: #6c757d;
            color: #fff;
        }
        .card-body-compact {
            padding: 1.25rem 1.5rem;
        }
        .vuelos-section-compact {
            max-height: 2270px;
            overflow-y: auto;
            overflow-x: hidden;
        }
        .btn-compact {
            padding: 0.375rem 0.75rem;
            font-size: 0.875rem;
        }
    </style>
</head>
<body class="bg-dark">
<header>
    <!-- Incluir navbar usando jsp:include para asegurar inclusión en tiempo de request -->
    <jsp:include page="/navbar.jsp" />
</header>

<div class="container mt-4 mb-5">
    <div class="row">
        <div class="col-lg-12 mx-auto">
            <!-- Mensajes de estado -->
            <% if (errorMsg != null && !errorMsg.isEmpty()) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle me-2"></i>
                <%= errorMsg %>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% } %>

            <% if (successMsg != null && !successMsg.isEmpty()) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle me-2"></i>
                <%= successMsg %>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% } %>

            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">
                        <i class="bi bi-check-circle me-2"></i>Finalizar Ruta de Vuelo
                    </h4>
                    <p class="mb-0 mt-2 small">Solo se pueden finalizar rutas con estado "Confirmada". Las rutas finalizadas no aparecerán en los listados del sistema.</p>
                </div>
                <div class="card-body p-4">
                    <div id="rutasFinalizables">
                        <!-- Loading state -->
                        <div class="text-center py-5">
                            <div class="spinner-border text-primary mb-3" role="status">
                                <span class="visually-hidden">Cargando...</span>
                            </div>
                            <p class="text-muted">Cargando rutas finalizables...</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal de confirmación -->
<div class="modal fade" id="confirmModal" tabindex="-1" aria-labelledby="confirmModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="confirmModalLabel">
                    <i class="bi bi-exclamation-triangle text-warning me-2"></i>
                    Confirmar Finalización
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>¿Está seguro de que desea finalizar la ruta <strong id="rutaNombreModal"></strong>?</p>
                <p class="text-muted small">
                    <i class="bi bi-info-circle me-1"></i>
                    <strong>Esta acción no se puede deshacer.</strong> La ruta cambiará a estado "Finalizada" y:
                <ul class="small">
                    <li>No aparecerá en los listados/búsquedas del sistema</li>
                    <li>No se podrán dar de alta nuevos vuelos para esta ruta</li>
                    <li>No se podrá incluir en paquetes turísticos</li>
                </ul>
                </p>
                <input type="hidden" id="rutaIdModal">
                <!-- Detalle adicional en caso de que la ruta no sea finalizable -->
                <div id="detalleFinalizacionModal" class="alert alert-warning small" style="display:none;">
                    <!-- Mensaje dinámico según la razón por la cual no se puede finalizar -->
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                    <i class="bi bi-x-lg me-1"></i>Cancelar
                </button>
                <button type="button" class="btn btn-success" id="confirmFinalizar">
                    <i class="bi bi-check-lg me-1"></i>Finalizar Ruta
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Container para mensajes toast -->
<div id="toastContainer"></div>

<!-- Scripts -->
<script>
    window.CONTEXT_PATH = '<%= contextPath %>';
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= contextPath %>/JsLogica/session-manager.js"></script>
<script>
    // Variables globales
    let rutaSeleccionada = null;
    let rutaIdSeleccionada = null;
    let rutaEsFinalizable = false; // nuevo flag global

    // Utility: escapar HTML para evitar inyección en strings construidos
    function escapeHtml(unsafe) {
        if (unsafe === null || unsafe === undefined) return '';
        return String(unsafe)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    // Cargar rutas al iniciar
    document.addEventListener('DOMContentLoaded', function() {
        console.log('Iniciando finalizar-ruta.jsp...');

        // Configurar modal de confirmación
        const confirmBtn = document.getElementById('confirmFinalizar');
        if (confirmBtn) {
            confirmBtn.addEventListener('click', finalizarRutaConfirmada);
        }

        // Cargar rutas (todas las rutas de la aerolínea que inició sesión)
        cargarRutasFinalizables();
    });

    function cargarRutasFinalizables() {
        const aerolinea = '<%= usuarioSession %>';
        console.log('Cargando rutas para aerolínea (todas):', aerolinea);

        if (!aerolinea) {
            mostrarError('No se pudo identificar la aerolínea. Por favor, inicie sesión.');
            return;
        }

        const container = document.getElementById('rutasFinalizables');
        if (!container) return;

        // Mostrar loading
        container.innerHTML = '<div class="text-center py-5">'
            + '<div class="spinner-border text-primary mb-3" role="status">'
            + '<span class="visually-hidden">Cargando...</span>'
            + '</div>'
            + '<p class="text-muted">Cargando rutas...</p>'
            + '</div>';

        // Llamar al backend: listar TODAS las rutas de la aerolínea
        const apiBase = window.CONTEXT_PATH;
        const fetchUrl = apiBase + '/api/rutas?aerolinea=' + encodeURIComponent(aerolinea);
        console.log('Llamando a backend (todas las rutas):', fetchUrl);

        fetch(fetchUrl, { credentials: 'include' })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al cargar las rutas: ' + response.status);
                }
                return response.json();
            })
            .then(rutas => {
                console.log('Rutas recibidas:', rutas);
                mostrarRutas(rutas || []);
            })
            .catch(error => {
                console.error('Error:', error);
                mostrarError('Error al cargar las rutas: ' + error.message);
            });
    }

    /**
     * Procesa promesas en lotes secuenciales para evitar sobrecarga del backend
     */
    async function procesarEnLotes(promesas, tamañoLote = 3) {
        const resultados = [];
        for (let i = 0; i < promesas.length; i += tamañoLote) {
            const lote = promesas.slice(i, i + tamañoLote);
            const resultadosLote = await Promise.all(lote);
            resultados.push(...resultadosLote);

            // Pequeña pausa entre lotes para no sobrecargar
            if (i + tamañoLote < promesas.length) {
                await new Promise(resolve => setTimeout(resolve, 300));
            }
        }
        return resultados;
    }

    function mostrarRutas(rutas) {
        const container = document.getElementById('rutasFinalizables');
        if (!container) return;

        // Filtrar solo rutas que NO estén finalizadas
        const rutasNoFinalizadas = (rutas || []).filter(r => {
            const est = (r.estado || '').toString().toUpperCase();
            return est !== 'FINALIZADA';
        });

        console.log('Mostrando', rutasNoFinalizadas.length, 'rutas no finalizadas');

        if (!rutasNoFinalizadas || rutasNoFinalizadas.length === 0) {
            container.innerHTML = '<div class="text-center py-5">'
                + '<i class="bi bi-inbox display-1 text-muted"></i>'
                + '<h4 class="text-muted mt-3">No hay rutas para esta aerolínea</h4>'
                + '<p class="text-muted">No se encontraron rutas para la aerolínea actual.</p>'
                + '<button class="btn btn-primary mt-3" onclick="cargarRutasFinalizables()">'
                + '<i class="bi bi-arrow-repeat me-2"></i>Reintentar'
                + '</button>'
                + '</div>';
            return;
        }

        const promesasRutas = rutasNoFinalizadas.map(ruta => {
            return new Promise((resolve) => {
                const nombreRuta = ruta.nombre;

                const promesaVuelos = fetch(
                    window.CONTEXT_PATH + '/api/vuelos-por-ruta?nombreRuta=' + encodeURIComponent(nombreRuta),
                    {credentials: 'include'}
                )
                    .then(response => {
                        if (!response.ok) {
                            console.warn('Error cargando vuelos para', nombreRuta, '- Status:', response.status);
                            return [];
                        }
                        return response.json();
                    })
                    .catch(error => {
                        console.error('Error fetch vuelos para', nombreRuta, error);
                        return [];
                    });

                const promesaFinalizacion = fetch(
                    window.CONTEXT_PATH + '/api/verificar-finalizacion?nombreRuta=' + encodeURIComponent(nombreRuta),
                    {credentials: 'include'}
                )
                    .then(response => {
                        if (!response.ok) {
                            console.warn('Error verificando finalización para', nombreRuta, '- Status:', response.status);
                            return {puedeFinalizar: false, motivo: 'Error al verificar finalización'};
                        }
                        return response.json();
                    })
                    .catch(error => {
                        console.error('Error fetch finalización para', nombreRuta, error);
                        return {puedeFinalizar: false, motivo: 'Error al verificar finalización'};
                    });

                Promise.all([promesaVuelos, promesaFinalizacion])
                    .then(([vuelos, finalizacionInfo]) => {
                        resolve({
                            ruta: ruta,
                            vuelos: vuelos || [],
                            finalizacionInfo: finalizacionInfo || {puedeFinalizar: false, motivo: 'Error'}
                        });
                    })
                    .catch(error => {
                        console.error('Error procesando ruta:', nombreRuta, error);
                        resolve({
                            ruta: ruta,
                            vuelos: [],
                            finalizacionInfo: {puedeFinalizar: false, motivo: 'Error al cargar información'}
                        });
                    });
            });
        });

        // Mostrar loading
        container.innerHTML = '<div class="text-center py-3">'
            + '<div class="spinner-border text-primary mb-2" role="status">'
            + '<span class="visually-hidden">Cargando información...</span>'
            + '</div>'
            + '<p class="text-muted">Cargando vuelos y verificando finalización...</p>'
            + '</div>';

        // Procesar las rutas en lotes de 3 para evitar sobrecarga
        procesarEnLotes(promesasRutas, 3)
            .then(resultados => {
                let html = '<div class="row g-3">';

                resultados.forEach(resultado => {
                    const tarjetaHTML = crearTarjetaRuta(
                        resultado.ruta,
                        resultado.vuelos,
                        resultado.finalizacionInfo
                    );
                    html += tarjetaHTML;
                });

                html += '</div>';
                container.innerHTML = html;

                // Agregar event listeners a los botones
                container.querySelectorAll('.btn-finalizar').forEach(btn => {
                    btn.addEventListener('click', function() {
                        const id = this.getAttribute('data-id') || '';
                        const nombre = this.getAttribute('data-nombre') || '';
                        const esFinalizable = this.getAttribute('data-finalizable') === 'true';
                        solicitarFinalizarRuta(id, nombre, esFinalizable);
                    });
                });

                console.log('Todas las rutas procesadas correctamente');
            })
            .catch(error => {
                console.error('Error general procesando rutas:', error);
                mostrarError('Error al cargar la información de las rutas: ' + error.message);
            });
    }

    function crearTarjetaRuta(ruta, vuelos, finalizacionInfo) {
        var idRuta = ruta.id || ruta.idRuta;
        var nombre = escapeHtml(ruta.nombre || 'Sin nombre');
        var descripcion = escapeHtml(ruta.descripcion || 'Sin descripción');
        var origen = escapeHtml(ruta.origen || 'N/A');
        var destino = escapeHtml(ruta.destino || 'N/A');
        var costoTurista = escapeHtml(ruta.costoTurista || 0);
        var costoEjecutivo = escapeHtml(ruta.costoEjecutivo || 0);
        var estado = (ruta.estado || 'N/A').toString();
        var estadoUpper = estado.toUpperCase();
        var aerolinea = escapeHtml((ruta.aerolinea && ruta.aerolinea.nombre) || ruta.aerolinea || '<%= usuarioSession %>');

        // Determinar si es finalizable según el backend
        const esFinalizable = finalizacionInfo.puedeFinalizar === true;
        const motivoNoFinalizable = finalizacionInfo.motivo || 'No se puede finalizar';

        // Clase de badge de estado según valor
        let claseEstado = 'badge';
        if (estadoUpper === 'CONFIRMADA') {
            claseEstado += ' badge-estado-confirmada';
        } else if (estadoUpper === 'INGRESADA') {
            claseEstado += ' badge-estado-ingresada';
        } else if (estadoUpper === 'FINALIZADA' || estadoUpper === 'RECHAZADA') {
            claseEstado += ' badge-estado-finalizada';
        } else {
            claseEstado += ' bg-secondary';
        }

        // Construir HTML de vuelos
        let vuelosHTML = '';
        if (vuelos && vuelos.length > 0) {
            vuelosHTML = '<div class="table-responsive">'
                + '<table class="table table-sm table-hover small">'
                + '<thead class="table-light">'
                + '<tr><th>Nombre</th><th>Fecha</th></tr>'
                + '</thead>'
                + '<tbody>';

            vuelos.forEach(vuelo => {
                vuelosHTML += '<tr>'
                    + '<td>' + escapeHtml(vuelo.nombre || 'N/A') + '</td>'
                    + '<td>' + escapeHtml(vuelo.fecha || 'N/A') + '</td>'
                    + '</tr>';
            });

            vuelosHTML += '</tbody></table></div>';
        } else {
            vuelosHTML = '<p class="text-muted small mb-0"><i class="bi bi-info-circle me-1"></i>No hay vuelos asociados a esta ruta.</p>';
        }

        // guardando si es finalizable
        const botonHTML = '<button class="btn btn-success btn-lg btn-finalizar"'
            + ' data-id="' + idRuta + '"'
            + ' data-nombre="' + nombre + '"'
            + ' data-finalizable="' + (esFinalizable ? 'true' : 'false') + '">'
            + '<i class="bi bi-flag-checkered me-2"></i>Finalizar Ruta</button>';

        return '<div class="col-12 col-md-12 col-xl-12">'
            + '<div class="card border-primary ruta-card h-100 w-100">'
            + '<div class="card-header bg-primary text-white d-flex justify-content-between align-items-center py-3">'
            + '<h6 class="mb-0 text-truncate" title="' + nombre + '">' + nombre + '</h6>'
            + '<span class="badge ' + (esFinalizable ? 'badge-finalizable' : 'badge-no-finalizable') + '"'
            + ' title="' + (esFinalizable ? 'Ruta finalizable' : motivoNoFinalizable) + '">'
            + (esFinalizable ? 'Finalizable' : 'No finalizable')
            + '</span>'
            + '</div>'
            + '<div class="card-body card-body-compact">'
            + '<p class="card-text text-muted small mb-2">' + descripcion + '</p>'
            + '<div class="row mb-2">'
            + '<div class="col-12">'
            + '<p class="mb-1 small"><strong><i class="bi bi-route me-1"></i>Ruta:</strong> ' + origen + ' → ' + destino + '</p>'
            + '<p class="mb-1 small"><strong><i class="bi bi-flag me-1"></i>Estado:</strong> <span class="' + claseEstado + '">' + estado + '</span></p>'
            + '<p class="mb-1 small"><strong><i class="bi bi-building me-1"></i>Aerolínea:</strong> ' + aerolinea + '</p>'
            + '</div>'
            + '</div>'
            + '<div class="bg-light rounded p-2 mb-2">'
            + '<h6 class="mb-2 text-center small">Costos</h6>'
            + '<div class="row text-center">'
            + '<div class="col-6">'
            + '<div class="costo-destacado small">$' + costoTurista + '</div>'
            + '<small class="text-muted">Turista</small>'
            + '</div>'
            + '<div class="col-6">'
            + '<div class="costo-destacado small">$' + costoEjecutivo + '</div>'
            + '<small class="text-muted">Ejecutivo</small>'
            + '</div>'
            + '</div>'
            + '</div>'
            + '<div class="vuelos-section-compact">'
            + '<h6 class="mb-2 small"><i class="bi bi-airplane me-1"></i>Vuelos (' + (vuelos ? vuelos.length : 0) + ')</h6>'
            + vuelosHTML
            + '</div>'
            + '<div class="d-flex justify-content-end mt-3">'
            + botonHTML
            + '</div>'
            + '</div>'
            + '</div>'
            + '</div>';
    }

    function solicitarFinalizarRuta(id, nombre, esFinalizable) {
        rutaSeleccionada = nombre;
        rutaIdSeleccionada = id;
        rutaEsFinalizable = esFinalizable;
        console.log('Solicitando finalizar ruta:', { id: id, nombre: nombre, esFinalizable });

        var rutaModal = document.getElementById('rutaNombreModal');
        if (rutaModal) rutaModal.textContent = nombre;

        // No mostramos ningún texto extra en el modal: solo el que ya está en el HTML fijo
        const modalElement = document.getElementById('confirmModal');
        if (modalElement) {
            const modal = new bootstrap.Modal(modalElement);
            modal.show();
        }
    }

    function finalizarRutaConfirmada() {
        if (!rutaIdSeleccionada) {
            mostrarMensaje('danger', 'Error: No se ha seleccionado una ruta válida.');
            return;
        }

        console.log('Finalizando ruta:', { id: rutaIdSeleccionada, nombre: rutaSeleccionada });

        if (!rutaIdSeleccionada) {
            mostrarMensaje('danger', 'Error: No se ha seleccionado una ruta válida.');
            return;
        }

        console.log('Finalizando ruta:', { id: rutaIdSeleccionada, nombre: rutaSeleccionada });

        var confirmBtn = document.getElementById('confirmFinalizar');
        if (confirmBtn) {
            if (!confirmBtn.dataset.originalText) {
                confirmBtn.dataset.originalText = confirmBtn.innerHTML;
            }
            confirmBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span> Procesando...';
            confirmBtn.disabled = true;
        }

        const params = new URLSearchParams();
        params.append('nombreRuta', rutaSeleccionada);

        const apiBase = window.CONTEXT_PATH;
        const fetchUrl = apiBase + '/api/finalizar-ruta';

        fetch(fetchUrl, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
            },
            body: params.toString()
        })
            .then(response => {
                return response.text().then(text => {
                    let data = null;
                    try {
                        data = text ? JSON.parse(text) : null;
                    } catch (e) {
                        data = null;
                    }

                    if (!response.ok) {
                        const msg = data && data.error ? data.error : ('Error al finalizar la ruta: ' + response.status);
                        throw new Error(msg);
                    }
                    return data;
                });
            })
            .then(result => {
                console.log('Resultado finalización:', result);
                manejarRespuestaFinalizacion(result);
            })
            .catch(error => {
                console.error('Error en finalización:', error);
                manejarRespuestaFinalizacion({
                    success: false,
                    error: error.message
                });
            });
    }

    function manejarRespuestaFinalizacion(result) {
        var modalElement = document.getElementById('confirmModal');
        if (modalElement) {
            var modalInst = bootstrap.Modal.getInstance(modalElement);
            if (modalInst) modalInst.hide();
        }

        if (result && result.success) {
            mostrarMensaje('success', '¡Ruta "' + rutaSeleccionada + '" finalizada exitosamente! La ruta ya no aparecerá en los listados del sistema.');
            setTimeout(() => { cargarRutasFinalizables(); }, 1500);
        } else {
            const mensajeError = result && result.error ? result.error : 'Error desconocido al finalizar la ruta';
            mostrarMensaje('danger', 'Error: ' + mensajeError);
        }
        restaurarBotonConfirmacion();
    }

    function restaurarBotonConfirmacion() {
        var confirmBtn = document.getElementById('confirmFinalizar');
        if (confirmBtn) {
            confirmBtn.innerHTML = confirmBtn.dataset.originalText || '<i class="bi bi-check-lg me-1"></i>Finalizar Ruta';
            confirmBtn.disabled = false;
        }
        rutaSeleccionada = null;
        rutaEsFinalizable = false;
    }

    function mostrarError(mensaje) {
        const container = document.getElementById('rutasFinalizables');
        if (!container) return;

        container.innerHTML = '<div class="alert alert-danger">'
            + '<i class="bi bi-exclamation-triangle me-2"></i>'
            + escapeHtml(mensaje)
            + '<button class="btn btn-sm btn-outline-danger ms-3" onclick="cargarRutasFinalizables()">Reintentar</button>'
            + '</div>';
    }

    function mostrarMensaje(tipo, mensaje) {
        const clase = (tipo === 'error') ? 'danger' : tipo;
        const alert = document.createElement('div');
        alert.className = 'alert alert-' + clase + ' alert-dismissible fade show';
        alert.setAttribute('role', 'alert');

        const icono = clase === 'success' ? 'check-circle' : 'exclamation-triangle';
        alert.innerHTML = '<i class="bi bi-' + icono + ' me-2"></i>' + escapeHtml(mensaje)
            + '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>';

        const container = document.querySelector('.container');
        if (container) {
            const firstChild = container.firstChild;
            container.insertBefore(alert, firstChild);

            setTimeout(function() {
                if (alert.parentNode) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                }
            }, 5000);
        }
    }
</script>
</body>
</html>
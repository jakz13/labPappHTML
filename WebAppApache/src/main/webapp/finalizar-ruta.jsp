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
                <div class="text-muted small">
                    <i class="bi bi-info-circle me-1"></i>
                    <strong>Esta acción no se puede deshacer.</strong> La ruta cambiará a estado "Finalizada" y:
                </div>
                <ul class="small">
                    <li>No aparecerá en los listados/búsquedas del sistema</li>
                    <li>No se podrán dar de alta nuevos vuelos para esta ruta</li>
                    <li>No se podrá incluir en paquetes turísticos</li>
                </ul>

                <input type="hidden" id="rutaIdModal">
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
<!-- Script local (ruta absoluta dentro de la app) -->
<script src="<%= contextPath %>/JsLogica/session-manager.js"></script>
<script>
    // Variables globales
    let rutaSeleccionada = null;
    let rutaIdSeleccionada = null;

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

        // Cargar rutas
        cargarRutasFinalizables();
    });

    function cargarRutasFinalizables() {
        const aerolinea = '<%= usuarioSession %>';
        console.log('Cargando rutas para aerolinea:', aerolinea);

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
            + '<p class="text-muted">Cargando rutas finalizables...</p>'
            + '</div>';

        // Llamar al backend real
        const apiBase = window.CONTEXT_PATH;
        const fetchUrl = apiBase + '/api/rutas?aerolinea=' + encodeURIComponent(aerolinea);
        console.log('Llamando a backend:', fetchUrl);

        fetch(fetchUrl, { credentials: 'include' })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al cargar las rutas: ' + response.status);
                }
                return response.json();
            })
            .then(rutas => {
                console.log('Rutas recibidas:', rutas);
                // Filtrar solo rutas con estado "Confirmada" y que no estén "Finalizadas"
                const rutasFiltradas = rutas.filter(ruta =>
                    ruta.estado === 'CONFIRMADA' && ruta.estado !== 'FINALIZADA'
                );
                mostrarRutas(rutasFiltradas);
            })
            .catch(error => {
                console.error('Error:', error);
                mostrarError('Error al cargar las rutas finalizables: ' + error.message);
            });
    }

    function mostrarRutas(rutas) {
        const container = document.getElementById('rutasFinalizables');
        if (!container) return;

        console.log('Mostrando', rutas.length, 'rutas confirmadas');

        if (!rutas || rutas.length === 0) {
            container.innerHTML = '<div class="text-center py-5">'
                + '<i class="bi bi-inbox display-1 text-muted"></i>'
                + '<h4 class="text-muted mt-3">No hay rutas finalizables</h4>'
                + '<p class="text-muted">No se encontraron rutas con estado "Confirmada" que puedan ser finalizadas.</p>'
                + '<button class="btn btn-primary mt-3" onclick="cargarRutasFinalizables()">'
                + '<i class="bi bi-arrow-repeat me-2"></i>Reintentar'
                + '</button>'
                + '</div>';
            return;
        }

        let html = '<div class="row g-3">'; // Reducido el gap entre cards
        rutas.forEach(function(ruta) {
            var idRuta = ruta.id || ruta.idRuta;
            // Asegurar que no se propague el literal 'undefined'
            if (idRuta === undefined || idRuta === null || String(idRuta).toLowerCase() === 'undefined') {
                idRuta = ruta.nombre || '';
            }
            // Guardar versión "raw" del nombre para enviar al servidor (no escapada), codificada en URI
            var nombreRaw = ruta.nombre || '';
            var nombre = escapeHtml(ruta.nombre || 'Sin nombre');
            var descripcion = escapeHtml(ruta.descripcion || 'Sin descripción');
            var origen = escapeHtml(ruta.origen || 'N/A');
            var destino = escapeHtml(ruta.destino || 'N/A');
            var costoTurista = escapeHtml(ruta.costoTurista || 0);
            var costoEjecutivo = escapeHtml(ruta.costoEjecutivo || 0);
            var estado = escapeHtml(ruta.estado || 'N/A');
            var aerolinea = escapeHtml((ruta.aerolinea && ruta.aerolinea.nombre) || ruta.aerolinea || '<%= usuarioSession %>');
            var vuelosHTML = (ruta.vuelos && ruta.vuelos.length > 0)
                ? generarVuelosHTML(ruta.vuelos)
                : '<p class="text-muted small mb-0"><i class="bi bi-info-circle me-1"></i>No hay vuelos asociados</p>';

            var esFinalizable = estado === 'CONFIRMADA';

            html += '<div class="col-11112 col-md-12 col-xl-12">'
                + '<div class="card border-primary ruta-card h-100 w-100">'
                + '<div class="card-header bg-primary text-white d-flex justify-content-between align-items-center py-3">'
                + '<h6 class="mb-0 text-truncate" title="' + nombre + '">' + nombre + '</h6>'
                + '<span class="badge ' + (esFinalizable ? 'badge-finalizable' : 'badge-no-finalizable') + '">'
                + (esFinalizable ? 'Finalizable' : 'No Finalizable')
                + '</span>'
                + '</div>'
                + '<div class="card-body card-body-compact">'
                + '<p class="card-text text-muted small mb-2">' + descripcion + '</p>'

                + '<div class="row mb-2">'
                + '<div class="col-12">'
                + '<p class="mb-1 small"><strong><i class="bi bi-route me-1"></i>Ruta:</strong> ' + origen + ' → ' + destino + '</p>'
                + '<p class="mb-1 small"><strong><i class="bi bi-flag me-1"></i>Estado:</strong> <span class="badge bg-success">' + estado + '</span></p>'
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
                + '<h6 class="mb-2 small"><i class="bi bi-airplane me-1"></i>Vuelos (' + (ruta.vuelos ? ruta.vuelos.length : 0) + ')</h6>'
                + '<div class="row g-1">' + vuelosHTML + '</div>'
                + '</div>'

                + '<div class="d-flex justify-content-end mt-2">'
                + (esFinalizable
                        ? '<button class="btn btn-success btn-compact btn-finalizar" '
                        + 'data-id="' + idRuta + '" '
                        + 'data-nombre="' + nombre + '" '
                        + 'data-nombre-raw="' + encodeURIComponent(nombreRaw) + '">'
                        + '<i class="bi bi-flag-checkered me-1"></i>Finalizar'
                        + '</button>'
                        : '<button class="btn btn-outline-secondary btn-compact" disabled title="Solo rutas con estado \'Confirmada\' pueden finalizarse">'
                        + '<i class="bi bi-slash-circle me-1"></i>No Finalizable'
                        + '</button>'
                )
                + '</div>'
                + '</div>'
                + '</div>'
                + '</div>';
        });

        html += '</div>';
        container.innerHTML = html;

        // Agregar listeners a botones creados dinámicamente
        container.querySelectorAll('.btn-finalizar').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const id = (this.getAttribute('data-id') || '').trim();
                // Preferir el nombre sin escapar si está disponible
                const nombreRawAttr = this.getAttribute('data-nombre-raw');
                const nombre = (nombreRawAttr ? decodeURIComponent(nombreRawAttr) : (this.getAttribute('data-nombre') || '')).trim();
                const finalId = (id && id.toLowerCase() !== 'undefined') ? id : (nombre || '');
                solicitarFinalizarRuta(finalId, nombre);
            });
        });
    }

    function generarVuelosHTML(vuelos) {
        var html = '';
        vuelos.forEach(function(vuelo, index) {
            if (index >= 3) return; // Mostrar máximo 3 vuelos
            var nombreV = escapeHtml(vuelo.nombre || 'Vuelo sin nombre');
            var fechaV = escapeHtml(vuelo.fecha || 'Fecha no disponible');
            var estadoV = escapeHtml(vuelo.estado || 'Programado');
            html += '<div class="col-12">'
                + '<div class="card bg-dark border-secondary">'
                + '<div class="card-body py-1">' // Padding reducido
                + '<div class="d-flex justify-content-between align-items-center">'
                + '<div class="text-truncate">'
                + '<strong class="text-light small">' + nombreV + '</strong>'
                + '<br>'
                + '<small class="text-muted"><i class="bi bi-calendar me-1"></i>' + fechaV + '</small>'
                + '</div>'
                + '<span class="badge bg-primary small">' + estadoV + '</span>'
                + '</div>'
                + '</div>'
                + '</div>'
                + '</div>';
        });

        if (vuelos.length > 3) {
            html += '<div class="col-12">'
                + '<div class="text-center">'
                + '<small class="text-muted">+ ' + (vuelos.length - 3) + ' vuelos más</small>'
                + '</div>'
                + '</div>';
        }

        return html;
    }

    function solicitarFinalizarRuta(id, nombre) {
        // Normalizar valores y evitar 'undefined'
        const finalId = (id && id.toLowerCase && id.toLowerCase() !== 'undefined') ? id : (nombre || '');
        rutaSeleccionada = nombre || finalId;
        rutaIdSeleccionada = finalId;
        console.log('Solicitando finalizar ruta:', { id: rutaIdSeleccionada, nombre: rutaSeleccionada });

        var rutaModal = document.getElementById('rutaNombreModal');
        if (rutaModal) rutaModal.textContent = nombre;

        const modalElement = document.getElementById('confirmModal');
        if (modalElement) {
            const modal = new bootstrap.Modal(modalElement);
            modal.show();
        }
    }

    function finalizarRutaConfirmada() {
        if (!rutaIdSeleccionada && !rutaSeleccionada) {
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
        // Enviar ambos parámetros; el servlet actual utiliza `nombreRuta`.
        params.append('nombreRuta', rutaSeleccionada);
        if (rutaIdSeleccionada) params.append('id', rutaIdSeleccionada);

        // -- DEBUG: si la página se carga con ?debugFinalizar=1 enviaremos debug=1 al servlet
        try {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('debugFinalizar') === '1') {
                console.log('Enviando parametro debug=1 para obtener detalle desde el servlet (modo depuración)');
                params.append('debug', '1');
            }
        } catch (e) {
            // no crítico
        }

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
                    // Log completo de la respuesta para facilitar copia/pegado
                    console.log('Respuesta raw de /api/finalizar-ruta ->', text, ' (status:', response.status, ')');
                    let data = null;
                    try {
                        data = text ? JSON.parse(text) : null;
                    } catch (e) {
                        data = null;
                    }

                    if (!response.ok) {
                        // Si el servlet envió JSON con `error`, lo usamos
                        const msg = data && data.error
                            ? data.error
                            : ('Error al finalizar la ruta: ' + response.status);
                        // Si hay detalle, incluyelo en el error para que el catch lo reciba
                        const detalle = data && data.detail ? '\n\nDetalle:\n' + data.detail : '';
                        throw new Error(msg + detalle);
                    }
                    // OK: devolvemos el JSON parseado
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
        // Cerrar modal
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
        // Mapear alias a clases válidas de bootstrap
        const clase = (tipo === 'error') ? 'danger' : tipo;

        // Crear alerta temporal
        const alert = document.createElement('div');
        alert.className = 'alert alert-' + clase + ' alert-dismissible fade show';
        alert.setAttribute('role', 'alert');

        const icono = clase === 'success' ? 'check-circle' : 'exclamation-triangle';

        alert.innerHTML = '<i class="bi bi-' + icono + ' me-2"></i>' + escapeHtml(mensaje)
            + '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>';

        // Insertar después del header
        const container = document.querySelector('.container');
        if (container) {
            const firstChild = container.firstChild;
            container.insertBefore(alert, firstChild);

            // Auto-remover después de 5 segundos
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


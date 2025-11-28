<!DOCTYPE html>
<html lang="es">
<head>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Consulta de Usuario - Juan Viajes</title>
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="CssLogica/estilo-css.css">
    <style>
        /* Estilos mejorados para las estadísticas de seguimiento */
        .follow-stats {
            background: linear-gradient(135deg, #2c3e50, #3498db);
            border-radius: 12px;
            padding: 15px;
            margin-bottom: 20px;
            color: white;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            border: 1px solid rgba(255, 255, 255, 0.1);
        }

        .follow-stats h5 {
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .follow-stats small {
            color: rgba(255, 255, 255, 0.85);
            font-size: 14px;
        }

        /* Botón de seguir mejorado */
        .follow-btn {
            transition: all 0.3s ease;
            border-radius: 25px;
            padding: 10px 20px;
            font-weight: 600;
            box-shadow: 0 4px 8px rgba(231, 76, 60, 0.3);
            background: linear-gradient(135deg, #e74c3c, #c0392b);
            border: none;
        }

        .follow-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 12px rgba(231, 76, 60, 0.4);
            background: linear-gradient(135deg, #c0392b, #a93226);
        }

        /* Estilo para cuando ya se está siguiendo */
        .follow-btn.following {
            background: linear-gradient(135deg, #f39c12, #e67e22);
            box-shadow: 0 4px 8px rgba(243, 156, 18, 0.3);
        }

        .follow-btn.following:hover {
            background: linear-gradient(135deg, #e67e22, #d35400);
            box-shadow: 0 6px 12px rgba(243, 156, 18, 0.4);
        }

        /* Mejoras visuales generales */
        .usuario-imagen {
            border-radius: 50%;
            border: 4px solid #3498db;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .card-header.bg-success {
            background: linear-gradient(135deg, #27ae60, #2ecc71) !important;
        }

        .tabla-datos th {
            background-color: #FFFFFF19 !important;
            color: #2c3e50;
            font-weight: 600;
        }

        .flight-card {
            border: 1px solid #e9ecef;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 15px;
            transition: all 0.3s ease;
            background: white;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
        }

        .flight-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }

        .flight-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 10px;
        }

        .flight-route {
            font-weight: bold;
            color: #2c3e50;
            font-size: 16px;
        }

        .read-more {
            color: #3498db;
            text-decoration: none;
            font-weight: 500;
            display: inline-flex;
            align-items: center;
            margin-top: 10px;
        }

        .read-more:hover {
            color: #2980b9;
            text-decoration: underline;
        }
    </style>
</head>
<body class="bg-dark">
<header>
    <%@ include file="navbar.jsp"%>
</header>

<div class="container mt-4 mb-5">
    <div class="row">
        <div class="col-lg-10 mx-auto">
            <div class="card shadow-lg">
                <div class="card-header">
                    <h4 class="mb-0">Consulta de Usuario</h4>
                    <p class="mb-0 mt-2 small">Explore los perfiles de usuarios registrados en el sistema</p>
                </div>
                <div class="card-body p-4">

                    <!-- Selección de Usuario -->
                    <div class="mb-4">
                        <label for="usuarioSelect" class="form-label h5">Seleccione un Usuario</label>
                        <select class="form-select form-select-lg" id="usuarioSelect">
                            <option value="">Seleccione un usuario para consultar...</option>
                            <!-- Las opciones se cargarán dinámicamente -->
                        </select>
                    </div>

                    <!-- Información del Usuario -->
                    <div id="infoUsuario" class="mt-4" style="display: none;">
                        <div class="card border-success">
                            <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">Perfil del Usuario</h5>
                                <!-- Botón de Seguir/Dejar de seguir -->
                                <div id="followSection" style="display: none;">
                                    <button id="followBtn" class="btn btn-light follow-btn" onclick="toggleFollow()">
                                        <i class="bi bi-person-plus me-1"></i>Seguir
                                    </button>
                                </div>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <!-- Imagen y datos básicos -->
                                    <div class="col-md-4 text-center">
                                        <img id="imagenUsuario" src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgdmlld0JveD0iMCAwIDE1MCAxNTAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIxNTAiIGhlaWdodD0iMTUwIiByeD0iNzUiIGZpbGw9IiMzNDk4REIiLz4KPHN2ZyB4PSIzOCIgeT0iMzgiIHdpZHRoPSI3NCIgaGVpZ2h0PSI3NCIgdmlld0JveD0iMCAwIDI0IDI0IiBmaWxsPSJ3aGl0ZSIgeG1zbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTEyIDEyYzIuMjEgMCA0LTEuNzkgNC00cy0xLjc5LTQtNC00LTQgMS43OS00IDQgMS43OSA0IDQgNHptMCAyYy0yLjY3IDAtOCAxLjM0LTggNHYyaDE2di0yYzAtMi42Ni01LjMzLTQtOC00eiIvPgo8L3N2Zz4KPC9zdmc+" alt="Imagen de usuario" class="usuario-imagen mb-3">
                                        <h4 id="nombreUsuario" class="text-white"></h4>
                                        <p class="mb-1 text-light"><strong>Nickname:</strong> <span id="nicknameUsuario" class="text-light"></span></p>
                                        <p class="mb-1 text-light"><strong>Tipo:</strong> <span id="tipoUsuario" class="badge bg-primary"></span></p>
                                        <p class="mb-1 text-light"><strong>Correo:</strong> <span id="correoUsuario" class="text-light"></span></p>
                                        <p class="mb-0 text-light"><strong>Fecha registro:</strong> <span id="fechaRegistro" class="text-light"></span></p>

                                        <!-- Estadísticas de Seguidores/Seguidos MEJORADAS -->
                                        <div class="follow-stats mt-3">
                                            <div class="row text-center">
                                                <div class="col-6">
                                                    <h5 id="seguidoresCount" class="mb-0">0</h5>
                                                    <small>Seguidores</small>
                                                </div>
                                                <div class="col-6">
                                                    <h5 id="seguidosCount" class="mb-0">0</h5>
                                                    <small>Seguidos</small>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Información detallada -->
                                    <div class="col-md-8">
                                        <!-- Información específica para Clientes -->
                                        <div id="infoCliente" style="display: none;">
                                            <h6 class="text-primary mb-3">Información Personal</h6>
                                            <div class="table-responsive">
                                                <table class="table table-bordered tabla-datos">
                                                    <tr>
                                                        <th>Apellido</th>
                                                        <td id="clienteApellido"></td>
                                                    </tr>
                                                    <tr>
                                                        <th>Fecha Nacimiento</th>
                                                        <td id="clienteNacimiento"></td>
                                                    </tr>
                                                    <tr>
                                                        <th>Nacionalidad</th>
                                                        <td id="clienteNacionalidad"></td>
                                                    </tr>
                                                    <tr>
                                                        <th>Documento</th>
                                                        <td id="clienteDocumento"></td>
                                                    </tr>
                                                </table>
                                            </div>

                                            <!-- Reservas del Cliente -->
                                            <h6 class="text-primary mt-4 mb-3">Reservas de Vuelo</h6>
                                            <div id="reservasCliente" class="row g-2">
                                                <!-- Las reservas se cargarán dinámicamente -->
                                            </div>

                                            <!-- Paquetes Comprados -->
                                            <h6 class="text-primary mt-4 mb-3">Paquetes Comprados</h6>
                                            <div id="paquetesCliente" class="row g-2">
                                                <!-- Los paquetes se cargarán dinámicamente -->
                                            </div>
                                        </div>

                                        <!-- Información específica para Aerolíneas -->
                                        <div id="infoAerolinea" style="display: none;">
                                            <h6 class="text-primary mb-3">Información de la Aerolínea</h6>
                                            <div class="table-responsive">
                                                <table class="table table-bordered tabla-datos">
                                                    <tr>
                                                        <th>Descripción</th>
                                                        <td id="aerolineaDescripcion"></td>
                                                    </tr>
                                                    <tr>
                                                        <th>Sitio Web</th>
                                                        <td id="aerolineaWeb"></td>
                                                    </tr>
                                                    <tr>
                                                        <th>Fecha Registro</th>
                                                        <td id="aerolineaFechaRegistro"></td>
                                                    </tr>
                                                    <tr>
                                                        <th>Total Rutas</th>
                                                        <td><span id="totalRutas" class="badge bg-primary"></span></td>
                                                    </tr>
                                                </table>
                                            </div>

                                            <!-- Rutas de la Aerolínea -->
                                            <h6 class="text-primary mt-4 mb-3">Rutas de Vuelo</h6>
                                            <div class="mb-3">
                                                <div class="btn-group" role="group">
                                                    <button type="button" class="btn btn-outline-primary active" onclick="filtrarRutas('todas')">Todas</button>
                                                    <button type="button" class="btn btn-outline-primary" onclick="filtrarRutas('confirmada')">Confirmadas</button>
                                                    <button type="button" class="btn btn-outline-primary" onclick="filtrarRutas('ingresada')">Ingresadas</button>
                                                    <button type="button" class="btn btn-outline-primary" onclick="filtrarRutas('rechazada')">Rechazadas</button>
                                                </div>
                                            </div>
                                            <div id="rutasAerolinea" class="row g-2">
                                                <!-- Las rutas se cargarán dinámicamente -->
                                            </div>

                                            <!-- VUELOS DE LA AEROLÍNEA - NUEVA SECCIÓN -->
                                            <h6 class="text-primary mt-4 mb-3">Vuelos Disponibles</h6>
                                            <div class="mb-3">
                                                <button type="button" class="btn btn-outline-info" onclick="cargarVuelosAerolinea()">
                                                    <i class="bi bi-airplane me-2"></i>Cargar Vuelos
                                                </button>
                                            </div>
                                            <div id="vuelosAerolinea" class="row g-2">
                                                <div class="col-12">
                                                    <p class="text-muted">Haga clic en "Cargar Vuelos" para ver los vuelos disponibles de esta aerolínea.</p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script src="JsLogica/consulta-usuario.js"></script>

<script>
    // Variable global para almacenar el usuario actual
    let usuarioActualId = null;
    let usuarioConsultadoId = null;
    let esMiUsuario = false;
    let siguiendoUsuario = false;

    // Función para cargar vuelos de aerolínea
    async function cargarVuelosAerolinea() {
        if (!usuarioConsultadoId) return;

        const vuelosContainer = document.getElementById('vuelosAerolinea');
        vuelosContainer.innerHTML = '<div class="col-12 text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Cargando vuelos...</span></div><p class="text-muted mt-2">Cargando vuelos...</p></div>';

        try {
            const response = await fetch('consulta-usuario?action=obtener-vuelos-aerolinea&usuario=' + encodeURIComponent(usuarioConsultadoId));

            if (!response.ok) {
                throw new Error('Error al cargar vuelos');
            }

            const vuelos = await response.json();
            mostrarVuelosAerolinea(vuelos);

        } catch (error) {
            console.error('Error cargando vuelos:', error);
            vuelosContainer.innerHTML = '<div class="col-12"><div class="alert alert-danger">Error al cargar los vuelos</div></div>';
        }
    }

    // Función para mostrar los vuelos - AHORA CON ENLACE A CONSULTA VUELO
    function mostrarVuelosAerolinea(vuelos) {
        const vuelosContainer = document.getElementById('vuelosAerolinea');

        if (!vuelos || vuelos.length === 0) {
            vuelosContainer.innerHTML = '<div class="col-12"><p class="text-muted">No hay vuelos disponibles para esta aerolínea.</p></div>';
            return;
        }

        let vuelosHTML = '';

        vuelos.forEach(vuelo => {
            const origen = vuelo.origen || 'N/A';
            const destino = vuelo.destino || 'N/A';
            const nombreVuelo = vuelo.nombre || 'N/A';
            const fecha = vuelo.fecha || 'No especificada';
            const duracion = vuelo.duracion || 'N/A';
            const ruta = vuelo.ruta || 'No especificada';
            const aerolinea = vuelo.aerolinea || 'N/A';
            const vueloId = vuelo.id || '';

            vuelosHTML +=
                '<div class="col-md-6">' +
                '    <div class="flight-card">' +
                '        <div class="flight-header">' +
                '            <div>' +
                '                <span class="flight-route">' + origen + ' - ' + destino + '</span>' +
                '                <div class="text-muted small">' +
                '                    <i class="bi bi-airplane me-1"></i>' + nombreVuelo +
                '                </div>' +
                '            </div>' +
                '            <span class="badge bg-info">Vuelo</span>' +
                '        </div>' +
                '        <div class="flight-description">' +
                '            <strong>Fecha:</strong> ' + fecha + ' | ' +
                '            <strong>Duración:</strong> ' + duracion + ' min' +
                '        </div>' +
                '        <div class="flight-details">' +
                '            <small>' +
                '                <strong>Ruta:</strong> ' + ruta + ' | ' +
                '                <strong>Aerolínea:</strong> ' + aerolinea +
                '            </small>' +
                '        </div>' +
                '        <a href="consulta-vuelo.jsp?vuelo=' + encodeURIComponent(vueloId) + '" class="read-more">' +
                '            Ver detalles del vuelo <i class="bi bi-arrow-right"></i>' +
                '        </a>' +
                '    </div>' +
                '</div>';
        });

        vuelosContainer.innerHTML = vuelosHTML;
    }

    // Función para consultar detalle de un vuelo específico
    async function consultarDetalleVuelo(vueloId) {
        try {
            const response = await fetch('consulta-usuario?action=obtener-vuelo&vuelo=' + encodeURIComponent(vueloId));

            if (!response.ok) {
                throw new Error('Vuelo no encontrado');
            }

            const vueloDetalle = await response.json();
            mostrarModalVueloDetalle(vueloDetalle);

        } catch (error) {
            console.error('Error consultando vuelo:', error);
            alert('Error al cargar los detalles del vuelo');
        }
    }

    // Función para mostrar modal con detalles del vuelo - AHORA CON ENLACE A CONSULTA VUELO
    function mostrarModalVueloDetalle(vuelo) {
        // Crear modal dinámicamente usando concatenación de strings en lugar de template literals
        let modalHTML =
            '<div class="modal fade" id="modalVueloDetalle" tabindex="-1" aria-labelledby="modalVueloDetalleLabel" aria-hidden="true">' +
            '    <div class="modal-dialog modal-lg">' +
            '        <div class="modal-content">' +
            '            <div class="modal-header bg-info text-white">' +
            '                <h5 class="modal-title" id="modalVueloDetalleLabel">' +
            '                    <i class="bi bi-airplane me-2"></i>Detalles del Vuelo' +
            '                </h5>' +
            '                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>' +
            '            </div>' +
            '            <div class="modal-body">' +
            '                <div class="row">' +
            '                    <div class="col-md-6">' +
            '                        <h6 class="text-primary mb-3">Información del Vuelo</h6>' +
            '                        <table class="table table-bordered">' +
            '                            <tr><th>Nombre</th><td>' + (vuelo.nombre || 'N/A') + '</td></tr>' +
            '                            <tr><th>Aerolínea</th><td>' + (vuelo.aerolinea || 'N/A') + '</td></tr>' +
            '                            <tr><th>Fecha</th><td>' + (vuelo.fecha || 'No especificada') + '</td></tr>' +
            '                            <tr><th>Duración</th><td>' + (vuelo.duracion || 'N/A') + ' minutos</td></tr>' +
            '                            <tr><th>Estado</th><td><span class="badge bg-success">Confirmado</span></td></tr>' +
            '                        </table>' +
            '                    </div>' +
            '                    <div class="col-md-6">' +
            '                        <h6 class="text-primary mb-3">Disponibilidad</h6>' +
            '                        <table class="table table-bordered">' +
            '                            <tr><th>Asientos Turista</th><td>' + (vuelo.asientosTurista || 'N/A') + '</td></tr>' +
            '                            <tr><th>Turista Disponibles</th><td>' + (vuelo.asientosTuristaDisponibles || 'N/A') + '</td></tr>' +
            '                            <tr><th>Asientos Ejecutivo</th><td>' + (vuelo.asientosEjecutivo || 'N/A') + '</td></tr>' +
            '                            <tr><th>Ejecutivo Disponibles</th><td>' + (vuelo.asientosEjecutivoDisponibles || 'N/A') + '</td></tr>' +
            '                        </table>' +
            '                    </div>' +
            '                </div>';

        // Agregar sección de ruta solo si existe la información
        if (vuelo.rutaDetalle && vuelo.rutaDetalle.origen) {
            modalHTML +=
                '                <div class="row mt-3">' +
                '                    <div class="col-12">' +
                '                        <h6 class="text-primary mb-3">Información de la Ruta</h6>' +
                '                        <div class="card bg-light">' +
                '                            <div class="card-body">' +
                '                                <div class="row text-center">' +
                '                                    <div class="col-md-4">' +
                '                                        <h5 class="text-warning">' + vuelo.rutaDetalle.origen + '</h5>' +
                '                                        <p class="text-muted mb-0">Origen</p>' +
                '                                    </div>' +
                '                                    <div class="col-md-4">' +
                '                                        <i class="bi bi-arrow-right text-primary" style="font-size: 2rem;"></i>' +
                '                                        <p class="text-muted mb-0 mt-2">' + (vuelo.rutaDetalle.hora || '') + '</p>' +
                '                                    </div>' +
                '                                    <div class="col-md-4">' +
                '                                        <h5 class="text-warning">' + vuelo.rutaDetalle.destino + '</h5>' +
                '                                        <p class="text-muted mb-0">Destino</p>' +
                '                                    </div>' +
                '                                </div>' +
                '                                <div class="row mt-3">' +
                '                                    <div class="col-12">' +
                '                                        <p><strong>Descripción:</strong> ' + (vuelo.rutaDetalle.descripcion || 'No disponible') + '</p>' +
                '                                        <p><strong>Costo Turista:</strong> $' + (vuelo.rutaDetalle.costoTurista || 'N/A') + ' | ' +
                '                                           <strong>Costo Ejecutivo:</strong> $' + (vuelo.rutaDetalle.costoEjecutivo || 'N/A') + '</p>' +
                '                                    </div>' +
                '                                </div>' +
                '                            </div>' +
                '                        </div>' +
                '                    </div>' +
                '                </div>';
        }

        modalHTML +=
            '            </div>' +
            '            <div class="modal-footer">' +
            '                <a href="consulta-vuelo.jsp?vuelo=' + encodeURIComponent(vuelo.nombre || vuelo.id) + '" class="btn btn-primary">' +
            '                    <i class="bi bi-search"></i> Ver Detalles Completos' +
            '                </a>' +
            '                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>' +
            '            </div>' +
            '        </div>' +
            '    </div>' +
            '</div>';

        // Remover modal existente si hay
        const modalExistente = document.getElementById('modalVueloDetalle');
        if (modalExistente) {
            modalExistente.remove();
        }

        // Agregar nuevo modal al body
        document.body.insertAdjacentHTML('beforeend', modalHTML);

        // Mostrar modal
        const modal = new bootstrap.Modal(document.getElementById('modalVueloDetalle'));
        modal.show();
    }

    // Función para alternar entre seguir y dejar de seguir
    async function toggleFollow() {
        const action = siguiendoUsuario ? 'dejar-de-seguir' : 'seguir';

        const response = await fetch(`consulta-usuario?action=${action}&usuario=${usuarioConsultadoId}`);

        if (response.ok) {
            siguiendoUsuario = !siguiendoUsuario;
            actualizarBotonSeguir();

            // ✅ FORZAR recarga sin cache
            const timestamp = new Date().getTime();
            await fetch(`consulta-usuario?action=obtener-estadisticas-seguimiento&usuario=${usuarioConsultadoId}&forzarRecarga=true&_=${timestamp}`)
                .then(res => res.json())
                .then(stats => {
                    document.getElementById('seguidoresCount').textContent = stats.seguidores;
                    document.getElementById('seguidosCount').textContent = stats.seguidos;
                });
        }
    }

    // Función para actualizar el botón de seguir/dejar de seguir
    function actualizarBotonSeguir() {
        const followBtn = document.getElementById('followBtn');

        if (siguiendoUsuario) {
            followBtn.innerHTML = '<i class="bi bi-person-dash me-1"></i>Dejar de seguir';
            followBtn.classList.add('following');
        } else {
            followBtn.innerHTML = '<i class="bi bi-person-plus me-1"></i>Seguir';
            followBtn.classList.remove('following');
        }
    }

    // Función para cargar estadísticas de seguidores/seguidos
    async function cargarEstadisticasSeguimiento() {
        if (!usuarioConsultadoId) return;

        try {
            const response = await fetch('consulta-usuario?action=obtener-estadisticas-seguimiento&usuario=' + encodeURIComponent(usuarioConsultadoId));

            if (!response.ok) {
                throw new Error('Error al cargar estadísticas');
            }

            const estadisticas = await response.json();

            document.getElementById('seguidoresCount').textContent = estadisticas.seguidores || 0;
            document.getElementById('seguidosCount').textContent = estadisticas.seguidos || 0;

        } catch (error) {
            document.getElementById('seguidoresCount').textContent = '0';
            document.getElementById('seguidosCount').textContent = '0';
        }
    }

    // Función para verificar si el usuario actual sigue al usuario consultado
    async function verificarEstadoSeguimiento() {
        if (!usuarioConsultadoId || esMiUsuario) return;

        try {
            const response = await fetch('consulta-usuario?action=verificar-seguimiento&usuario=' + encodeURIComponent(usuarioConsultadoId));

            if (!response.ok) {
                throw new Error('Error al verificar estado');
            }

            const resultado = await response.json();
            siguiendoUsuario = resultado.siguiendo || false;
            actualizarBotonSeguir();

        } catch (error) {
            siguiendoUsuario = false;
            actualizarBotonSeguir();
        }
    }

    // Función para mostrar mensaje de éxito
    function mostrarMensajeExito(mensaje) {
        const alertasAnteriores = document.querySelectorAll('.alert');
        alertasAnteriores.forEach(alerta => alerta.remove());

        const alertHTML = '<div class="alert alert-success alert-dismissible fade show" role="alert">' +
            '<i class="bi bi-check-circle me-2"></i>' +
            mensaje +
            '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>' +
            '</div>';
        document.querySelector('.card-body').insertAdjacentHTML('afterbegin', alertHTML);

        setTimeout(() => {
            const alerta = document.querySelector('.alert-success');
            if (alerta) {
                alerta.remove();
            }
        }, 5000);
    }

    // Función para mostrar mensaje de error
    function mostrarMensajeError(mensaje) {
        const alertasAnteriores = document.querySelectorAll('.alert');
        alertasAnteriores.forEach(alerta => alerta.remove());

        const alertHTML = '<div class="alert alert-danger alert-dismissible fade show" role="alert">' +
            '<i class="bi bi-exclamation-triangle me-2"></i>' +
            mensaje +
            '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>' +
            '</div>';
        document.querySelector('.card-body').insertAdjacentHTML('afterbegin', alertHTML);

        setTimeout(() => {
            const alerta = document.querySelector('.alert-danger');
            if (alerta) {
                alerta.remove();
            }
        }, 5000);
    }

    // Configurar event listener para el select de usuarios
    document.addEventListener('DOMContentLoaded', function() {
        const usuarioSelect = document.getElementById('usuarioSelect');

        usuarioSelect.addEventListener('change', function() {
            const usuarioId = this.value;
            if (usuarioId) {
                usuarioConsultadoId = usuarioId;

                // Obtener el ID del usuario actual desde la sesión
                const usuarioActual = obtenerUsuarioActual();
                esMiUsuario = (usuarioActual && usuarioActual.id === usuarioConsultadoId);

                // Mostrar/ocultar sección de seguir
                const followSection = document.getElementById('followSection');
                if (esMiUsuario) {
                    followSection.style.display = 'none';
                } else {
                    followSection.style.display = 'block';
                    verificarEstadoSeguimiento();
                }

                // Cargar estadísticas de seguimiento
                cargarEstadisticasSeguimiento();

                // La función cargarUsuarioSeleccionado está en consulta-usuario.js
                if (typeof cargarUsuarioSeleccionado === 'function') {
                    cargarUsuarioSeleccionado(usuarioId);
                }
            } else {
                document.getElementById('infoUsuario').style.display = 'none';
            }
        });
    });

    // Función global para filtrar rutas (necesaria para los botones)
    function filtrarRutas(filtro) {
        // Actualizar botones activos
        document.querySelectorAll('.btn-group .btn').forEach(btn => {
            btn.classList.remove('active');
        });
        event.target.classList.add('active');

        if (typeof cargarRutasAerolineaInterfaz === 'function') {
            cargarRutasAerolineaInterfaz(filtro);
        }
    }

    // Función auxiliar para obtener el usuario actual desde session-manager.js
    function obtenerUsuarioActual() {
        // Método 1: Desde window.CURRENT_SESSION (que usa tu session-manager.js)
        if (typeof window.CURRENT_SESSION !== 'undefined' && window.CURRENT_SESSION.authenticated) {
            return {
                id: window.CURRENT_SESSION.nickname,
                nickname: window.CURRENT_SESSION.nickname,
                tipo: window.CURRENT_SESSION.tipo
            };
        }

        // Método 2: Desde localStorage (fallback de tu session-manager)
        try {
            const nickname = localStorage.getItem('session_nickname');
            const tipo = localStorage.getItem('session_tipo');
            if (nickname) {
                return {
                    id: nickname,
                    nickname: nickname,
                    tipo: tipo
                };
            }
        } catch (e) {
            console.warn('Error accediendo localStorage');
        }

        return null;
    }
</script>
</body>
</html>
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
                            <div class="card-header bg-success text-white">
                                <h5 class="mb-0">Perfil del Usuario</h5>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <!-- Imagen y datos básicos -->
                                    <div class="col-md-4 text-center">
                                        <img id="imagenUsuario" src="" alt="Imagen de usuario" class="usuario-imagen mb-3">
                                        <h4 id="nombreUsuario" class="text-white"></h4>
                                        <p class="mb-1 text-light"><strong>Nickname:</strong> <span id="nicknameUsuario" class="text-light"></span></p>
                                        <p class="mb-1 text-light"><strong>Tipo:</strong> <span id="tipoUsuario" class="badge bg-primary"></span></p>
                                        <p class="mb-1 text-light"><strong>Correo:</strong> <span id="correoUsuario" class="text-light"></span></p>
                                        <p class="mb-0 text-light"><strong>Fecha registro:</strong> <span id="fechaRegistro" class="text-light"></span></p>
                                    </div>

                                    <!-- Información detallada -->
                                    <div class="col-md-8">
                                        <!-- Información específica para Clientes -->
                                        <div id="infoCliente" style="display: none;">
                                            <h6 class="text-primary mb-3">Información Personal</h6>
                                            <div class="table-responsive">
                                                <table class="table table-bordered tabla-datos">
                                                    <tr>
                                                        <th width="30%">Apellido</th>
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
                                                        <th width="30%">Descripción</th>
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

    // Función para cargar vuelos de aerolínea
    async function cargarVuelosAerolinea() {
        if (!usuarioActualId) return;

        const vuelosContainer = document.getElementById('vuelosAerolinea');
        vuelosContainer.innerHTML = '<div class="col-12 text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Cargando vuelos...</span></div><p class="text-muted mt-2">Cargando vuelos...</p></div>';

        try {
            const response = await fetch('consulta-usuario?action=obtener-vuelos-aerolinea&usuario=' + encodeURIComponent(usuarioActualId));

            if (!response.ok) {
                throw new Error('Error al cargar vuelos');
            }

            const vuelos = await response.json();
            mostrarVuelosAerolinea(vuelos);

        } catch (error) {
            console.error('Error cargando vuelos:', error);
            vuelosContainer.innerHTML = '<div class="col-12"><div class="alert alert-danger">Error al cargar los vuelos: ' + error.message + '</div></div>';
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
            alert('Error al cargar los detalles del vuelo: ' + error.message);
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

    // Configurar event listener para el select de usuarios
    document.addEventListener('DOMContentLoaded', function() {
        const usuarioSelect = document.getElementById('usuarioSelect');

        usuarioSelect.addEventListener('change', function() {
            const usuarioId = this.value;
            if (usuarioId) {
                usuarioActualId = usuarioId;
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
</script>
</body>
</html>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Consulta de Ruta de Vuelo - Juan Viajes</title>
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="CssLogica/estilo-css.css">

    <!-- Inyectar contexto para que los scripts puedan construir URLs públicas sin ambigüedad -->
    <script type="text/javascript">
        // CONTEXT_PATH usado por los scripts cliente para prefijar /Images/
        window.CONTEXT_PATH = '<%= request.getContextPath() %>';
    </script>
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
                    <h4 class="mb-0">Consulta de Ruta de Vuelo</h4>
                    <p class="mb-0 mt-2 small">Explore las rutas de vuelo confirmadas disponibles en el sistema</p>
                </div>
                <div class="card-body p-4">

                    <!-- Filtros de búsqueda -->
                    <div class="filter-section mb-4">
                        <h5 class="mb-3">Filtrar Rutas</h5>
                        <div class="row g-3">
                            <div class="col-md-4">
                                <label for="aerolinea" class="form-label">Aerolínea *</label>
                                <select class="form-select" id="aerolinea">
                                    <option value="">Seleccione aerolínea...</option>
                                    <!-- Las opciones se cargan dinámicamente -->
                                </select>
                            </div>
                            <div class="col-md-4">
                                <label for="categoria" class="form-label">Filtrar por categoría:</label>
                                <select id="categoria" class="form-select">
                                    <option value="">Todas las categorías</option>
                                    <!-- Las opciones se cargarán dinámicamente -->
                                </select>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">&nbsp;</label>
                                <div class="d-grid gap-2">
                                    <button class="btn btn-primary" id="btnAplicarFiltros">
                                        <i class="bi bi-funnel"></i> Aplicar Filtros
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div class="mt-3">
                            <button class="btn btn-outline-secondary" id="btnLimpiar">
                                <i class="bi bi-arrow-clockwise"></i> Limpiar
                            </button>
                        </div>
                    </div>

                    <!-- Listado de rutas -->
                    <div class="mb-4">
                        <h5 class="mb-3">Rutas Disponibles</h5>
                        <div id="listaRutas" class="row g-3">
                            <div class="col-12 text-center py-4">
                                <p class="text-muted">Seleccione una aerolínea para ver las rutas</p>
                            </div>
                        </div>
                    </div>

                    <!-- Información detallada de la ruta seleccionada -->
                    <div id="infoRuta" class="mt-4" style="display: none;">
                        <div class="card border-primary">
                            <div class="card-header bg-primary text-white">
                                <h5 class="mb-0">Información Detallada de la Ruta</h5>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-12">
                                        <!-- Imagen de la ruta -->
                                        <div class="mb-3">
                                            <img id="imagenRutaDetalle" src="" alt="Imagen de la ruta" class="img-fluid rounded w-100" style="max-height:300px; object-fit:cover; display:none;">
                                        </div>

                                        <!-- Contenedor para video de la ruta -->
                                        <div id="videoUrlContainer" class="mb-3" style="display: none;">
                                            <div class="card bg-light">
                                                <div class="card-body">
                                                    <h6 class="card-title">
                                                        <i class="bi bi-camera-video text-primary"></i>
                                                        Enlace de Video de la Ruta
                                                    </h6>
                                                    <p class="mb-1"><strong>URL del video:</strong></p>
                                                    <a id="videoUrlLink" href="#" target="_blank" class="text-break">
                                                        <!-- Aquí se insertará la URL -->
                                                    </a>
                                                    <div class="mt-2">
                                                        <small class="text-muted">
                                                            <i class="bi bi-info-circle"></i>
                                                            Haz clic para ver el video en una nueva pestaña
                                                        </small>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Contenedor para video embebido (para YouTube/Vimeo) -->
                                        <div id="videoContainer" class="mt-3">
                                            <!-- El video embebido se cargará dinámicamente aquí -->
                                        </div>

                                        <h4 id="rutaNombre" class="text-primary mb-3"></h4>
                                        <p class="lead" id="rutaDescripcion"></p>

                                        <div class="row mt-3">
                                            <div class="col-md-6">
                                                <p class="mb-2"><strong>Aerolínea:</strong> <span id="rutaAerolinea"></span></p>
                                                <p class="mb-2"><strong>Origen:</strong> <span id="rutaOrigen"></span></p>
                                                <p class="mb-2"><strong>Destino:</strong> <span id="rutaDestino"></span></p>
                                            </div>
                                            <div class="col-md-6">
                                                <p class="mb-2"><strong>Estado:</strong> <span id="rutaEstado" class="badge bg-success"></span></p>
                                                <p class="mb-2"><strong>Fecha alta:</strong> <span id="rutaFechaAlta"></span></p>
                                                <p class="mb-2"><strong>Categorías:</strong> <span id="rutaCategorias"></span></p>
                                            </div>
                                        </div>

                                        <!-- Costos -->
                                        <div class="mt-3 p-3 bg-light rounded">
                                            <h6>Costos de la Ruta</h6>
                                            <div class="row text-center">
                                                <div class="col-4">
                                                    <div class="costo-destacado"><span id="costoTurista"></span></div>
                                                    <small class="text-muted">Turista</small>
                                                </div>
                                                <div class="col-4">
                                                    <div class="costo-destacado"><span id="costoEjecutivo"></span></div>
                                                    <small class="text-muted">Ejecutivo</small>
                                                </div>
                                                <div class="col-4">
                                                    <div class="costo-destacado"><span id="costoEquipaje"></span></div>
                                                    <small class="text-muted">Equipaje extra</small>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Vuelos asociados -->
                                <div class="mt-4">
                                    <h6>Vuelos Disponibles en esta Ruta</h6>
                                    <div id="vuelosAsociados" class="row g-2">
                                        <!-- Los vuelos se cargarán dinámicamente -->
                                    </div>
                                </div>

                                <!-- Acciones -->
                                <div class="d-flex gap-2 mt-4">
                                    <a href="consulta-vuelo.jsp" class="btn btn-primary">
                                        <i class="bi bi-search"></i> Consultar Vuelos
                                    </a>
                                    <a href="reserva-vuelo.jsp" class="btn btn-success">
                                        <i class="bi bi-calendar-check"></i> Reservar Vuelo
                                    </a>
                                    <button class="btn btn-outline-secondary" onclick="window.print()">
                                        <i class="bi bi-printer"></i> Imprimir
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Información detallada del vuelo seleccionado -->
                    <div id="infoVuelo" class="mt-4" style="display: none;">
                        <div class="card border-warning">
                            <div class="card-header bg-warning text-dark">
                                <h5 class="mb-0">Información del Vuelo Seleccionado</h5>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-12">
                                        <div class="info-card p-3 mb-3 bg-dark border rounded">
                                            <h4 id="nombreVueloDetalle" class="text-warning mb-3"></h4>
                                            <div class="row">
                                                <div class="col-6">
                                                    <strong class="text-light">Aerolínea:</strong>
                                                    <span id="aerolineaVueloDetalle" class="text-light"></span>
                                                </div>
                                                <div class="col-6">
                                                    <strong class="text-light">Ruta:</strong>
                                                    <span id="rutaVueloDetalle" class="text-light"></span>
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

                                        <!-- Acciones -->
                                        <div class="d-flex gap-2 mt-4">
                                            <a href="reserva-vuelo.jsp" class="btn btn-success" id="btnReservar" style="display: none;">
                                                <i class="bi bi-calendar-check"></i> Reservar este Vuelo
                                            </a>
                                            <button class="btn btn-outline-primary" onclick="window.print()">
                                                <i class="bi bi-printer"></i> Imprimir Información
                                            </button>
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

<!-- Container para mensajes toast -->
<div id="toastContainer"></div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script src="JsLogica/consulta-ruta.js"></script>
</body>
</html>
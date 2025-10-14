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
                    <div class="filter-section">
                        <h5 class="mb-3">Filtrar Rutas</h5>
                        <div class="row g-3">
                            <div class="col-md-4">
                                <label for="aerolinea" class="form-label">Aerolínea</label>
                                <select class="form-select" id="aerolinea">
                                    <option value="">Todas las aerolíneas</option>
                                    <option value="zulyfly">ZulyFly</option>
                                    <option value="iberia">Iberia</option>
                                    <option value="copa">Copa Airlines</option>
                                    <option value="american">American Airlines</option>
                                    <option value="aerolineas">Aerolíneas Argentinas</option>
                                    <option value="latam">LATAM</option>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <label for="categoria" class="form-label">Categoría</label>
                                <select class="form-select" id="categoria">
                                    <option value="">Todas las categorías</option>
                                    <option value="nacionales">Nacionales</option>
                                    <option value="internacionales">Internacionales</option>
                                    <option value="europa">Europa</option>
                                    <option value="america">América</option>
                                    <option value="caribe">Caribe</option>
                                    <option value="cortos">Cortos</option>
                                    <option value="exclusivos">Exclusivos</option>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <label for="estado" class="form-label">Estado</label>
                                <select class="form-select" id="estado">
                                    <option value="confirmada">Confirmadas</option>
                                    <option value="todas">Todas las rutas</option>
                                </select>
                            </div>
                        </div>
                        <div class="mt-3">
                            <button class="btn btn-primary" onclick="filtrarRutas()">
                                <i class="bi bi-funnel"></i> Aplicar Filtros
                            </button>
                            <button class="btn btn-outline-secondary" onclick="limpiarFiltros()">
                                <i class="bi bi-arrow-clockwise"></i> Limpiar
                            </button>
                        </div>
                    </div>

                    <!-- Listado de rutas -->
                    <div class="mb-4">
                        <h5 class="mb-3">Rutas Disponibles</h5>
                        <div id="listaRutas" class="row g-3">
                            <!-- Las rutas se cargarán dinámicamente -->
                        </div>
                    </div>

                    <!-- Información detallada de la ruta seleccionada -->
                    <div id="infoRuta" class="mt-4" style="display: none;">
                        <div class="card border-success">
                            <div class="card-header bg-success text-white">
                                <h5 class="mb-0">Información Detallada de la Ruta</h5>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <img id="imagenRuta" src="" alt="Imagen de la ruta" class="img-fluid rounded mb-3">
                                    </div>
                                    <div class="col-md-6">
                                        <h4 id="rutaNombre" class="text-primary"></h4>
                                        <p class="lead" id="rutaDescripcionCorta"></p>

                                        <div class="row mt-3">
                                            <div class="col-6">
                                                <p class="mb-2"><strong>Aerolínea:</strong> <span id="rutaAerolinea"></span></p>
                                                <p class="mb-2"><strong>Origen:</strong> <span id="rutaOrigen"></span></p>
                                                <p class="mb-2"><strong>Destino:</strong> <span id="rutaDestino"></span></p>
                                                <p class="mb-2"><strong>Hora salida:</strong> <span id="rutaHora"></span></p>
                                            </div>
                                            <div class="col-6">
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
                                                    <div class="costo-destacado">$<span id="costoTurista"></span></div>
                                                    <small class="text-muted">Turista</small>
                                                </div>
                                                <div class="col-4">
                                                    <div class="costo-destacado">$<span id="costoEjecutivo"></span></div>
                                                    <small class="text-muted">Ejecutivo</small>
                                                </div>
                                                <div class="col-4">
                                                    <div class="costo-destacado">$<span id="costoEquipaje"></span></div>
                                                    <small class="text-muted">Equipaje extra</small>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Descripción extendida -->
                                <div class="mt-4">
                                    <h6>Descripción Completa</h6>
                                    <p id="rutaDescripcion" class="text-muted"></p>
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

                </div>
            </div>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script src="JsLogica/consulta-ruta.js"></script>
</body>
</html>
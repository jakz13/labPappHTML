<!DOCTYPE html>
<html lang="es">
<head>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Consulta de Paquete de Rutas de Vuelo - Juan Viajes</title>
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
                    <h4 class="mb-0">Consulta de Paquete de Rutas de Vuelo</h4>
                    <p class="mb-0 mt-2 small">Explore los paquetes disponibles y consulte información detallada de cada ruta</p>
                </div>
                <div class="card-body p-4">

                    <!-- Selección de Paquete -->
                    <div class="mb-4">
                        <label for="paqueteSelect" class="form-label h5">Seleccione un Paquete</label>
                        <select class="form-select form-select-lg" id="paqueteSelect">
                            <option value="">Seleccione un paquete para consultar...</option>
                            <option value="sudamerica">Paquete Sudamérica Esencial</option>
                            <option value="europa">Paquete Europa Grand Tour</option>
                            <option value="caribe">Paquete Caribe Paradise</option>
                            <option value="norteamerica">Paquete Norteamérica Explorer</option>
                        </select>
                    </div>

                    <!-- Información del Paquete -->
                    <div id="infoPaquete" class="mt-4" style="display: none;">
                        <div class="card border-success">
                            <div class="card-header bg-success text-white">
                                <h5 class="mb-0">Información del Paquete</h5>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <h4 id="nombrePaquete" class="text-primary"></h4>
                                        <p class="mb-2"><strong>Costo:</strong> $<span id="costoPaquete" class="h5 text-success"></span></p>
                                        <p class="mb-2"><strong>Vigencia:</strong> <span id="vigenciaPaquete" class="badge bg-info"></span></p>
                                        <p class="mb-2"><strong>Rutas incluidas:</strong> <span id="cantidadRutas" class="badge bg-primary"></span></p>
                                        <p class="mb-0"><strong>Descripción:</strong> <span id="descripcionPaquete"></span></p>
                                    </div>
                                    <div class="col-md-6">
                                        <h6>Beneficios del Paquete:</h6>
                                        <ul id="beneficiosPaquete" class="list-unstyled"></ul>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Rutas Incluidas -->
                        <div class="mt-4">
                            <h5 class="mb-3">Rutas Incluidas en el Paquete</h5>
                            <div id="rutasPaquete" class="row g-3">
                                <!-- Las rutas se cargarán dinámicamente -->
                            </div>
                        </div>
                    </div>

                    <!-- Información Detallada de la Ruta -->
                    <div id="infoRuta" class="mt-4" style="display: none;">
                        <div class="card border-primary">
                            <div class="card-header bg-primary text-white">
                                <h5 class="mb-0">Información Detallada de la Ruta</h5>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-8">
                                        <h4 id="rutaNombre" class="text-primary"></h4>
                                        <p class="lead" id="rutaDescripcionCorta"></p>

                                        <div class="row mt-4">
                                            <div class="col-md-6">
                                                <h6>Información de la Ruta</h6>
                                                <p class="mb-2"><strong>Aerolínea:</strong> <span id="rutaAerolinea"></span></p>
                                                <p class="mb-2"><strong>Origen:</strong> <span id="rutaOrigen"></span></p>
                                                <p class="mb-2"><strong>Destino:</strong> <span id="rutaDestino"></span></p>
                                                <p class="mb-2"><strong>Duración:</strong> <span id="rutaDuracion"></span></p>
                                                <p class="mb-2"><strong>Hora salida:</strong> <span id="rutaHoraSalida"></span></p>
                                            </div>
                                            <div class="col-md-6">
                                                <h6>Costos y Categorías</h6>
                                                <p class="mb-2"><strong>Turista:</strong> $<span id="rutaTurista"></span></p>
                                                <p class="mb-2"><strong>Ejecutivo:</strong> $<span id="rutaEjecutivo"></span></p>
                                                <p class="mb-2"><strong>Equipaje extra:</strong> $<span id="rutaEquipaje"></span></p>
                                                <p class="mb-2"><strong>Categorías:</strong> <span id="rutaCategorias"></span></p>
                                                <p class="mb-0"><strong>Estado:</strong> <span id="rutaEstado" class="badge bg-success"></span></p>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <img id="rutaImagen" src="" alt="Imagen de la ruta" class="img-fluid rounded">
                                        <div class="mt-3 text-center">
                                            <button class="btn btn-outline-primary btn-sm" onclick="consultarVuelos()">
                                                <i class="bi bi-search"></i> Ver Vuelos Disponibles
                                            </button>
                                        </div>
                                    </div>
                                </div>

                                <!-- Descripción extendida -->
                                <div class="mt-4">
                                    <h6>Descripción Completa</h6>
                                    <p id="rutaDescripcionCompleta" class="text-muted"></p>
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
<script src="JsLogica/consulta-paquete.js"></script>
</body>
</html>
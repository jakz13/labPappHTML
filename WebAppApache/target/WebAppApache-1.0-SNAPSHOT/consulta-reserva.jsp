<!DOCTYPE html>
<html lang="es">
<head>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Consulta de Reserva de Vuelo - Juan Viajes</title>
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="CssLogica/estilo-css.css">
</head>
<body class="bg-dark text-light">
<header>
    <%@ include file="navbar.jsp"%>
</header>

<div class="container mt-5">
    <div class="row">
        <div class="col-lg-10 mx-auto">
            <div class="card shadow-lg">
                <div class="card-header">
                    <h4 class="mb-0">Consulta de Reserva de Vuelo</h4>
                    <p class="mb-0 mt-2 small">Consulte la información detallada de sus reservas de vuelo</p>
                </div>
                <div class="card-body p-4">

                    <!-- Indicador de tipo de usuario -->
                    <div class="alert alert-info mb-4">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <i class="bi bi-person-check"></i>
                                <strong id="tipoUsuarioTexto" class="text-light">Cliente</strong> -
                                <span id="nombreUsuario" class="text-light">María González</span>
                            </div>
                            <button class="btn btn-outline-primary btn-sm" onclick="cambiarTipoUsuario()">
                                Cambiar a <span id="tipoAlternativo">Aerolínea</span>
                            </button>
                        </div>
                    </div>

                    <!-- Flujo para Cliente -->
                    <div id="flujoCliente">
                        <div class="step-indicator">
                            <div class="step-line"></div>
                            <div class="step active" id="stepCliente1">
                                <div class="step-number">1</div>
                                <div class="step-label">Seleccionar Aerolínea</div>
                            </div>
                            <div class="step" id="stepCliente2">
                                <div class="step-number">2</div>
                                <div class="step-label">Seleccionar Ruta</div>
                            </div>
                            <div class="step" id="stepCliente3">
                                <div class="step-number">3</div>
                                <div class="step-label">Seleccionar Vuelo</div>
                            </div>
                            <div class="step" id="stepCliente4">
                                <div class="step-number">4</div>
                                <div class="step-label">Ver Reserva</div>
                            </div>
                        </div>

                        <form id="formCliente">
                            <!-- Paso 1: Seleccionar Aerolínea -->
                            <div class="form-section active" id="sectionCliente1">
                                <h5 class="text-primary mb-4">Seleccionar Aerolínea</h5>

                                <div class="mb-3">
                                    <label for="aerolineaCliente" class="form-label">Aerolínea</label>
                                    <select class="form-select" id="aerolineaCliente">
                                        <option value="">Seleccione una aerolínea...</option>
                                        <option value="zulyfly">ZulyFly</option>
                                        <option value="iberia">Iberia</option>
                                        <option value="copa">Copa Airlines</option>
                                        <option value="american">American Airlines</option>
                                    </select>
                                </div>

                                <div class="d-flex justify-content-between mt-4">
                                    <div></div>
                                    <button type="button" class="btn btn-primary" onclick="siguientePasoCliente(2)" id="btnCliente1" disabled>
                                        Siguiente <i class="bi bi-arrow-right"></i>
                                    </button>
                                </div>
                            </div>

                            <!-- Paso 2: Seleccionar Ruta -->
                            <div class="form-section" id="sectionCliente2">
                                <h5 class="text-primary mb-4">Seleccionar Ruta</h5>

                                <div class="mb-3">
                                    <label for="rutaCliente" class="form-label">Ruta de Vuelo</label>
                                    <select class="form-select" id="rutaCliente">
                                        <option value="">Seleccione una ruta...</option>
                                    </select>
                                </div>

                                <div class="d-flex justify-content-between mt-4">
                                    <button type="button" class="btn btn-secondary" onclick="siguientePasoCliente(1)">
                                        <i class="bi bi-arrow-left"></i> Anterior
                                    </button>
                                    <button type="button" class="btn btn-primary" onclick="siguientePasoCliente(3)" id="btnCliente2" disabled>
                                        Siguiente <i class="bi bi-arrow-right"></i>
                                    </button>
                                </div>
                            </div>

                            <!-- Paso 3: Seleccionar Vuelo -->
                            <div class="form-section" id="sectionCliente3">
                                <h5 class="text-primary mb-4">Seleccionar Vuelo</h5>

                                <div class="mb-3">
                                    <label for="vueloCliente" class="form-label">Vuelo</label>
                                    <select class="form-select" id="vueloCliente">
                                        <option value="">Seleccione un vuelo...</option>
                                    </select>
                                </div>

                                <div class="d-flex justify-content-between mt-4">
                                    <button type="button" class="btn btn-secondary" onclick="siguientePasoCliente(2)">
                                        <i class="bi bi-arrow-left"></i> Anterior
                                    </button>
                                    <button type="button" class="btn btn-primary" onclick="siguientePasoCliente(4)" id="btnCliente3" disabled>
                                        Consultar Reserva <i class="bi bi-search"></i>
                                    </button>
                                </div>
                            </div>

                            <!-- Paso 4: Ver Reserva -->
                            <div class="form-section" id="sectionCliente4">
                                <h5 class="text-primary mb-4">Detalles de la Reserva</h5>

                                <div id="reservaClienteDetalle">
                                    <!-- La información de la reserva se cargará aquí -->
                                </div>

                                <div class="d-flex justify-content-between mt-4">
                                    <button type="button" class="btn btn-secondary" onclick="siguientePasoCliente(3)">
                                        <i class="bi bi-arrow-left"></i> Anterior
                                    </button>
                                    <button type="button" class="btn btn-success" onclick="nuevaConsulta()">
                                        <i class="bi bi-arrow-repeat"></i> Nueva Consulta
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>

                    <!-- Flujo para Aerolínea -->
                    <div id="flujoAerolinea" style="display: none;">
                        <div class="step-indicator">
                            <div class="step-line"></div>
                            <div class="step active" id="stepAerolinea1">
                                <div class="step-number">1</div>
                                <div class="step-label">Seleccionar Ruta</div>
                            </div>
                            <div class="step" id="stepAerolinea2">
                                <div class="step-number">2</div>
                                <div class="step-label">Seleccionar Vuelo</div>
                            </div>
                            <div class="step" id="stepAerolinea3">
                                <div class="step-number">3</div>
                                <div class="step-label">Seleccionar Reserva</div>
                            </div>
                            <div class="step" id="stepAerolinea4">
                                <div class="step-number">4</div>
                                <div class="step-label">Ver Detalles</div>
                            </div>
                        </div>

                        <form id="formAerolinea">
                            <!-- Paso 1: Seleccionar Ruta -->
                            <div class="form-section active" id="sectionAerolinea1">
                                <h5 class="text-primary mb-4">Seleccionar Ruta</h5>

                                <div class="mb-3">
                                    <label for="rutaAerolinea" class="form-label">Ruta de Vuelo</label>
                                    <select class="form-select" id="rutaAerolinea">
                                        <option value="">Seleccione una ruta...</option>
                                    </select>
                                </div>

                                <div class="d-flex justify-content-between mt-4">
                                    <div></div>
                                    <button type="button" class="btn btn-primary" onclick="siguientePasoAerolinea(2)" id="btnAerolinea1" disabled>
                                        Siguiente <i class="bi bi-arrow-right"></i>
                                    </button>
                                </div>
                            </div>

                            <!-- Paso 2: Seleccionar Vuelo -->
                            <div class="form-section" id="sectionAerolinea2">
                                <h5 class="text-primary mb-4">Seleccionar Vuelo</h5>

                                <div class="mb-3">
                                    <label for="vueloAerolinea" class="form-label">Vuelo</label>
                                    <select class="form-select" id="vueloAerolinea">
                                        <option value="">Seleccione un vuelo...</option>
                                    </select>
                                </div>

                                <div class="d-flex justify-content-between mt-4">
                                    <button type="button" class="btn btn-secondary" onclick="siguientePasoAerolinea(1)">
                                        <i class="bi bi-arrow-left"></i> Anterior
                                    </button>
                                    <button type="button" class="btn btn-primary" onclick="siguientePasoAerolinea(3)" id="btnAerolinea2" disabled>
                                        Siguiente <i class="bi bi-arrow-right"></i>
                                    </button>
                                </div>
                            </div>

                            <!-- Paso 3: Seleccionar Reserva -->
                            <div class="form-section" id="sectionAerolinea3">
                                <h5 class="text-primary mb-4">Seleccionar Reserva</h5>

                                <div id="listaReservasAerolinea" class="row g-3">
                                    <!-- Las reservas se cargarán dinámicamente -->
                                </div>

                                <div class="d-flex justify-content-between mt-4">
                                    <button type="button" class="btn btn-secondary" onclick="siguientePasoAerolinea(2)">
                                        <i class="bi bi-arrow-left"></i> Anterior
                                    </button>
                                    <button type="button" class="btn btn-primary" onclick="siguientePasoAerolinea(4)" id="btnAerolinea3" disabled>
                                        Ver Detalles <i class="bi bi-search"></i>
                                    </button>
                                </div>
                            </div>

                            <!-- Paso 4: Ver Detalles -->
                            <div class="form-section" id="sectionAerolinea4">
                                <h5 class="text-primary mb-4">Detalles de la Reserva</h5>

                                <div id="reservaAerolineaDetalle">
                                    <!-- La información de la reserva se cargará aquí -->
                                </div>

                                <div class="d-flex justify-content-between mt-4">
                                    <button type="button" class="btn btn-secondary" onclick="siguientePasoAerolinea(3)">
                                        <i class="bi bi-arrow-left"></i> Anterior
                                    </button>
                                    <button type="button" class="btn btn-success" onclick="nuevaConsulta()">
                                        <i class="bi bi-arrow-repeat"></i> Nueva Consulta
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<!-- Container para mensajes toast -->
<div id="toastContainer"></div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script src="JsLogica/consulta-reserva.js"></script>
</body>
</html>
<!DOCTYPE html>
<html lang="es">
<head>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Consulta de Check-in - Juan Viajes</title>
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
<div class="container mt-5">
    <div class="row">
        <div class="col-lg-10 mx-auto">
            <div class="card shadow-lg">
                <div class="card-header">
                    <h4 class="mb-0">
                        <i class="bi bi-check-circle me-2"></i>
                        Consulta de Check-in de Reservas
                    </h4>
                    <p class="mb-0 mt-2 small">Consulte la información de check-in realizados y obtenga su tarjeta de embarque</p>
                </div>
                <div class="card-body p-4">

                    <!-- Información del usuario -->
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <div class="alert alert-secondary">
                                <div class="d-flex align-items-center">
                                    <i class="bi bi-person-circle me-2 fs-4 text-primary"></i>
                                    <div>
                                        <strong id="nombreUsuario">Usuario</strong>
                                        <div class="small">
                                            Tipo: <span id="tipoUsuarioTexto" class="badge bg-primary">Cliente</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 text-end">
                            <button class="btn btn-outline-primary" onclick="nuevaConsultaCheckin()">
                                <i class="bi bi-arrow-clockwise me-2"></i>Nueva Consulta
                            </button>
                        </div>
                    </div>

                    <!-- Flujo para clientes -->
                    <div id="flujoCliente" style="display: none;">
                        <!-- Indicador de pasos -->
                        <div class="step-indicator-checkin">
                            <div class="step-checkin active" id="stepCheckin1">
                                <div class="step-number-checkin">1</div>
                                <div class="step-label-checkin">Seleccionar Reserva</div>
                            </div>
                            <div class="step-checkin" id="stepCheckin2">
                                <div class="step-number-checkin">2</div>
                                <div class="step-label-checkin">Ver Detalles</div>
                            </div>
                        </div>

                        <!-- Paso 1: Selección de Reserva -->
                        <div class="form-section-checkin active" id="sectionCheckin1">
                            <h5 class="text-success mb-4">
                                <i class="bi bi-list-check me-2"></i>
                                Reservas con Check-in Realizado
                            </h5>

                            <div class="alert alert-info">
                                <i class="bi bi-info-circle me-2"></i>
                                Seleccione una reserva para ver los detalles del check-in y obtener la tarjeta de embarque.
                            </div>

                            <div id="listaReservasCheckin" class="row mt-4">
                                <!-- Las reservas se cargan aquí dinámicamente -->
                            </div>

                            <div class="d-flex justify-content-between mt-4">
                                <div></div>
                                <div>
                                    <button type="button" class="btn btn-checkin me-2" id="btnVerDetallesCheckin" disabled>
                                        <i class="bi bi-eye me-2"></i>Ver Detalles del Check-in
                                    </button>
                                    <button type="button" class="btn btn-tarjeta-embarque" id="btnTarjetaEmbarque" disabled>
                                        <i class="bi bi-download me-2"></i>Descargar Tarjeta de Embarque
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Paso 2: Detalles del Check-in -->
                        <div class="form-section-checkin" id="sectionCheckin2">
                            <h5 class="text-success mb-4">
                                <i class="bi bi-check-circle-fill me-2"></i>
                                Detalles del Check-in
                            </h5>

                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <button type="button" class="btn btn-secondary" onclick="anteriorPasoCheckin(1)">
                                    <i class="bi bi-arrow-left me-2"></i>Volver a la lista
                                </button>
                                <button type="button" class="btn btn-tarjeta-embarque" id="btnTarjetaEmbarque2">
                                    <i class="bi bi-download me-2"></i>Descargar Tarjeta de Embarque
                                </button>
                            </div>

                            <div id="detallesCheckinContainer">
                                <!-- Los detalles del check-in se cargan aquí dinámicamente -->
                            </div>
                        </div>
                    </div>

                    <!-- Flujo para no clientes -->
                    <div id="flujoNoCliente" style="display: none;">
                        <div class="alert alert-warning text-center">
                            <i class="bi bi-exclamation-triangle-fill me-2 fs-4"></i>
                            <h5 class="text-warning">Acceso Restringido</h5>
                            <p class="text-dark">Esta funcionalidad está disponible únicamente para clientes.</p>
                            <p class="text-dark">Si es una aerolínea, consulte las funcionalidades disponibles en el menú de aerolíneas.</p>
                            <a href="PaginaPrincipal.jsp" class="btn btn-primary mt-2">Volver al Inicio</a>
                        </div>
                    </div>

                    <!-- Flujo para no autenticados -->
                    <div id="flujoNoAutenticado" style="display: none;">
                        <div class="alert alert-danger text-center">
                            <i class="bi bi-person-x-fill me-2 fs-4"></i>
                            <h5 class="text-danger">No Autenticado</h5>
                            <p class="text-dark">Debe iniciar sesión para acceder a esta funcionalidad.</p>
                            <a href="login.jsp" class="btn btn-primary mt-2">Iniciar Sesión</a>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<!-- Container para mensajes toast -->
<div id="toastContainer" class="toast-container position-fixed top-0 end-0 p-3"></div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script src="JsLogica/consulta-checkin.js"></script>
</body>
</html>
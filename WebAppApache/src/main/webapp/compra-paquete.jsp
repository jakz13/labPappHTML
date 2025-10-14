<!DOCTYPE html>
<html lang="es">
<head>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Compra de Paquete de Rutas de Vuelo - Juan Viajes</title>
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
        <div class="col-lg-8 mx-auto">
            <div class="card shadow-lg">
                <div class="card-header">
                    <h4 class="mb-0">Compra de Paquete de Rutas de Vuelo</h4>
                    <p class="mb-0 mt-2 small">Seleccione un paquete disponible para comprar</p>
                </div>
                <div class="card-body p-4">

                    <!-- Información del cliente -->
                    <div class="alert alert-info">
                        <strong>Cliente:</strong> María González |
                        <strong>Paquetes comprados:</strong> <span id="contadorPaquetes">0</span> |
                        <strong>Saldo disponible:</strong> $<span id="saldoCliente">2,500</span>
                    </div>

                    <!-- Lista de Paquetes Disponibles -->
                    <h5 class="mb-3">Paquetes Disponibles</h5>
                    <div id="listaPaquetes" class="row g-3">
                        <!-- Los paquetes se cargarán dinámicamente -->
                    </div>

                    <!-- Información del Paquete Seleccionado -->
                    <div id="infoPaquete" class="mt-4 d-none">
                        <div class="card border-success">
                            <div class="card-header bg-success text-white">
                                <h6 class="mb-0">Paquete Seleccionado</h6>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <h5 id="nombrePaqueteSeleccionado"></h5>
                                        <p class="mb-1"><strong>Costo:</strong> $<span id="costoPaqueteSeleccionado"></span></p>
                                        <p class="mb-1"><strong>Vigencia:</strong> <span id="vigenciaPaquete"></span> días</p>
                                        <p class="mb-1"><strong>Fecha de compra:</strong> <span id="fechaCompra"></span></p>
                                        <p class="mb-1"><strong>Vence el:</strong> <span id="fechaVencimiento"></span></p>
                                    </div>
                                    <div class="col-md-6">
                                        <h6>Rutas Incluidas:</h6>
                                        <div id="rutasPaqueteSeleccionado"></div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Botones de acción -->
                        <div class="d-flex gap-2 mt-3">
                            <button class="btn btn-success" id="btnConfirmarCompra">
                                <i class="bi bi-credit-card"></i> Confirmar Compra
                            </button>
                            <button class="btn btn-secondary" id="btnCancelarSeleccion">
                                <i class="bi bi-x-circle"></i> Cancelar Selección
                            </button>
                        </div>
                    </div>

                    <!-- Mensajes del sistema -->
                    <div id="mensajeCompra" class="mt-3"></div>

                    <!-- Paquetes ya comprados -->
                    <div id="paquetesComprados" class="mt-5">
                        <h5>Tus Paquetes Comprados</h5>
                        <div id="listaPaquetesComprados" class="row g-3">
                            <!-- Se llenará dinámicamente -->
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal de confirmación -->
<div class="modal fade" id="confirmacionModal" tabindex="-1" aria-labelledby="confirmacionModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title" id="confirmacionModalLabel">Confirmar Compra</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>¿Está seguro que desea comprar el paquete <strong id="modalNombrePaquete"></strong> por $<strong id="modalCostoPaquete"></strong>?</p>
                <p>Este paquete tendrá una vigencia de <strong id="modalVigencia"></strong> días a partir de hoy.</p>
                <div class="alert alert-info mt-3">
                    <small><i class="bi bi-info-circle"></i> Después de la compra, podrás utilizar este paquete para reservar vuelos en las rutas incluidas.</small>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-success" id="btnModalConfirmar">Confirmar Compra</button>
            </div>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script src="JsLogica/compra-paquete.js"></script>
</body>
</html>
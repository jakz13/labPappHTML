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
    <style>
        .paquete-card {
            transition: all 0.3s ease;
        }
        .paquete-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .paquete-card.selected {
            border: 2px solid #0d6efd !important;
        }
        .ruta-item {
            background: #f8f9fa;
            border-left: 4px solid #198754;
        }
        .badge-vigente {
            background-color: #198754;
        }
        .badge-vencido {
            background-color: #6c757d;
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
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0"><i class="bi bi-bag-check me-2"></i>Compra de Paquete de Rutas de Vuelo</h4>
                    <p class="mb-0 mt-2 small opacity-75">Seleccione un paquete disponible para comprar</p>
                </div>
                <div class="card-body p-4">

                    <!-- Información del cliente -->
                    <div class="alert alert-info d-flex align-items-center">
                        <i class="bi bi-person-circle me-2 fs-5"></i>
                        <div>
                            <strong>Cliente:</strong> <span id="nombreCliente">María González</span> |
                            <strong>Paquetes comprados:</strong> <span id="contadorPaquetes">0/0</span> |
                            <strong>Saldo disponible:</strong> $<span id="saldoCliente">2,500.00</span>
                        </div>
                    </div>

                    <!-- Estado de carga -->
                    <div id="estadoCarga" class="text-center py-4">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Cargando paquetes...</span>
                        </div>
                        <p class="mt-2 text-muted">Cargando paquetes disponibles...</p>
                    </div>

                    <!-- Lista de Paquetes Disponibles -->
                    <div id="seccionPaquetes" class="d-none">
                        <h5 class="mb-3"><i class="bi bi-box-seam me-2"></i>Paquetes Disponibles</h5>
                        <div id="listaPaquetes" class="row g-3">
                            <!-- Los paquetes se cargarán dinámicamente -->
                        </div>
                    </div>

                    <!-- Información del Paquete Seleccionado -->
                    <div id="infoPaquete" class="mt-4 d-none">
                        <div class="card border-success">
                            <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
                                <h6 class="mb-0"><i class="bi bi-check-circle me-2"></i>Paquete Seleccionado</h6>
                                <span class="badge bg-light text-success" id="badgeSeleccionado">LISTO PARA COMPRAR</span>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <h5 id="nombrePaqueteSeleccionado" class="text-success">-</h5>
                                        <p class="mb-2">
                                            <i class="bi bi-currency-dollar text-success"></i>
                                            <strong>Costo:</strong> $<span id="costoPaqueteSeleccionado">0.00</span>
                                        </p>
                                        <p class="mb-2">
                                            <i class="bi bi-calendar-check text-success"></i>
                                            <strong>Vigencia:</strong> <span id="vigenciaPaquete">0</span> días
                                        </p>
                                        <p class="mb-2">
                                            <i class="bi bi-calendar-date text-success"></i>
                                            <strong>Fecha de compra:</strong> <span id="fechaCompra">-</span>
                                        </p>
                                        <p class="mb-2">
                                            <i class="bi bi-calendar-x text-success"></i>
                                            <strong>Vence el:</strong> <span id="fechaVencimiento">-</span>
                                        </p>
                                    </div>
                                    <div class="col-md-6">
                                        <h6><i class="bi bi-geo-route me-2"></i>Rutas Incluidas:</h6>
                                        <div id="rutasPaqueteSeleccionado" class="mt-2">
                                            <!-- Rutas se cargarán dinámicamente -->
                                        </div>
                                    </div>
                                </div>

                                <!-- Botones de acción -->
                                <div class="d-flex gap-2 mt-4">
                                    <button class="btn btn-success px-4" id="btnConfirmarCompra">
                                        <i class="bi bi-credit-card me-2"></i> Confirmar Compra
                                    </button>
                                    <button class="btn btn-outline-secondary" id="btnCancelarSeleccion">
                                        <i class="bi bi-x-circle me-2"></i> Cancelar Selección
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Mensajes del sistema -->
                    <div id="mensajeCompra" class="mt-3"></div>

                    <!-- Paquetes ya comprados -->
                    <div id="seccionPaquetesComprados" class="mt-5 d-none">
                        <h5><i class="bi bi-bag-check me-2"></i>Tus Paquetes Comprados</h5>
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
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title" id="confirmacionModalLabel">
                    <i class="bi bi-check-circle me-2"></i>Confirmar Compra
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="text-center mb-3">
                    <i class="bi bi-bag-check text-success" style="font-size: 3rem;"></i>
                </div>
                <p class="text-center">¿Está seguro que desea comprar el siguiente paquete?</p>

                <div class="card border-success mb-3">
                    <div class="card-body">
                        <h6 class="card-title text-success" id="modalNombrePaquete">-</h6>
                        <p class="mb-1"><strong>Costo:</strong> $<span id="modalCostoPaquete">0.00</span></p>
                        <p class="mb-0"><strong>Vigencia:</strong> <span id="modalVigencia">0</span> días</p>
                    </div>
                </div>

                <div class="alert alert-info">
                    <small>
                        <i class="bi bi-info-circle me-1"></i>
                        Después de la compra, podrás utilizar este paquete para reservar vuelos en las rutas incluidas.
                    </small>
                </div>
            </div>
            <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">
                    <i class="bi bi-x-circle me-2"></i> Cancelar
                </button>
                <button type="button" class="btn btn-success px-4" id="btnModalConfirmar">
                    <i class="bi bi-check-lg me-2"></i> Confirmar Compra
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Modal de error -->
<div class="modal fade" id="errorModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title"><i class="bi bi-exclamation-triangle me-2"></i>Error</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p id="mensajeError">Ha ocurrido un error inesperado.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
            </div>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script src="JsLogica/compra-paquete.js"></script>

<script>
    // Script de inicialización adicional
    document.addEventListener('DOMContentLoaded', function() {
        console.log('🔄 Inicializando interfaz de compra de paquetes...');

        // Ocultar estado de carga después de un tiempo (fallback)
        setTimeout(() => {
            const estadoCarga = document.getElementById('estadoCarga');
            if (estadoCarga && estadoCarga.style.display !== 'none') {
                estadoCarga.classList.add('d-none');
                document.getElementById('seccionPaquetes').classList.remove('d-none');
            }
        }, 5000);

        // Manejar errores de carga
        window.addEventListener('error', function(e) {
            console.error('Error global:', e.error);
            const estadoCarga = document.getElementById('estadoCarga');
            if (estadoCarga) {
                estadoCarga.innerHTML = `
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-triangle me-2"></i>
                        Error al cargar la página. Por favor, recarga.
                    </div>
                `;
            }
        });
    });
</script>
</body>
</html>
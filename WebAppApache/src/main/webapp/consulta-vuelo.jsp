<!DOCTYPE html>
<html lang="es">
<head>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Consulta de Vuelo - Juan Viajes</title>
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="CssLogica/estilo-css.css">
    <style>
        .flight-image {
            max-height: 300px;
            object-fit: cover;
            border-radius: 8px;
        }
        .info-card {
            background: linear-gradient(135deg, #2c3e50, #34495e);
            border: 1px solid #4a6572;
        }
        .reserva-section {
            border-left: 4px solid #ffc107;
        }
        .fade-in {
            animation: fadeIn 0.5s ease-in;
        }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>
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
                    <h4 class="mb-0">Consulta de Vuelo</h4>
                    <p class="mb-0 mt-2 small">Busque y consulte información detallada de vuelos disponibles</p>
                </div>
                <div class="card-body p-4">

                    <!-- Formulario de búsqueda -->
                    <form id="formConsultaVuelo" novalidate>
                        <div class="row g-3">
                            <!-- Selección de aerolínea -->
                            <div class="col-md-4">
                                <label for="aerolinea" class="form-label">Aerolínea *</label>
                                <select class="form-select" id="aerolinea" required>
                                    <option value="">Seleccione aerolínea...</option>
                                    <!-- Las opciones se cargan dinámicamente -->
                                </select>
                                <div class="invalid-feedback">Por favor seleccione una aerolínea.</div>
                            </div>

                            <!-- Selección de ruta -->
                            <div class="col-md-4">
                                <label for="rutaVuelo" class="form-label">Ruta de Vuelo *</label>
                                <select class="form-select" id="rutaVuelo" required disabled>
                                    <option value="">Primero seleccione aerolínea</option>
                                </select>
                                <div class="invalid-feedback">Por favor seleccione una ruta.</div>
                            </div>

                            <!-- Selección de vuelo -->
                            <div class="col-md-4">
                                <label for="vuelo" class="form-label">Vuelo *</label>
                                <select class="form-select" id="vuelo" required disabled>
                                    <option value="">Primero seleccione ruta</option>
                                </select>
                                <div class="invalid-feedback">Por favor seleccione un vuelo.</div>
                            </div>
                        </div>

                        <!-- Botones de acción -->
                        <div class="d-flex gap-2 mt-4">
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-search"></i> Consultar Vuelo
                            </button>
                            <button type="reset" class="btn btn-secondary" id="btnLimpiar">
                                <i class="bi bi-arrow-clockwise"></i> Limpiar Búsqueda
                            </button>
                            <a href="PaginaPrincipal.jsp" class="btn btn-outline-primary">
                                <i class="bi bi-house"></i> Volver al Inicio
                            </a>
                        </div>
                    </form>

                    <!-- Resultados de la consulta -->
                    <div id="resultadoConsulta" class="mt-5" style="display: none;">
                        <hr>
                        <h5 class="text-primary mb-4">Información del Vuelo</h5>

                        <div class="row">
                            <!-- Imagen del vuelo -->
                            <div class="col-md-6 mb-4">
                                <img id="imagenVueloDetalle" src="" alt="Imagen del vuelo" class="img-fluid flight-image w-100 rounded">
                            </div>

                            <!-- Información principal -->
                            <div class="col-md-6">
                                <div class="info-card p-3 mb-3 bg-dark border rounded">
                                    <h4 id="nombreVueloDetalle" class="text-primary mb-3"></h4>
                                    <div class="row">
                                        <div class="col-6">
                                            <strong class="text-light">Aerolínea:</strong>
                                            <span id="aerolineaDetalle" class="text-light"></span>
                                        </div>
                                        <div class="col-6">
                                            <strong class="text-light">Ruta:</strong>
                                            <span id="rutaDetalle" class="text-light"></span>
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

                                <!-- Sección de gestión de reservas (solo para usuarios logueados) -->
                                <div id="seccionReservas" class="mt-4" style="display: none;">
                                    <hr>
                                    <h5 class="text-warning mb-4">Gestión de Reservas</h5>

                                    <!-- Información para aerolíneas (reservas) -->
                                    <div id="infoAerolinea" class="reserva-section p-3 mt-3 bg-dark border rounded" style="display: none;">
                                        <h6 class="text-warning mb-3">
                                            <i class="bi bi-building"></i> Gestión de Reservas - Panel Aerolínea
                                        </h6>
                                        <p class="mb-2 text-light">Este vuelo tiene <strong id="totalReservas" class="text-warning">0</strong> reservas confirmadas.</p>
                                        <button class="btn btn-sm btn-outline-warning" onclick="mostrarDetallesReservasModal()">
                                            <i class="bi bi-list-ul"></i> Ver Detalles de Reservas
                                        </button>
                                    </div>

                                    <!-- Información para clientes (mi reserva) -->
                                    <div id="infoCliente" class="reserva-section p-3 mt-3 bg-dark border rounded" style="display: none;">
                                        <h6 class="text-success mb-3">
                                            <i class="bi bi-person-check"></i> Tu Reserva Confirmada
                                        </h6>
                                        <p class="mb-2 text-light">Ya tienes una reserva confirmada para este vuelo.</p>
                                        <button class="btn btn-sm btn-outline-success" onclick="verMiReserva()">
                                            <i class="bi bi-ticket-perforated"></i> Ver Mi Reserva
                                        </button>
                                    </div>

                                    <!-- Opción de reserva para usuarios autenticados sin reserva -->
                                    <div id="infoReservar" class="reserva-section p-3 mt-3 bg-dark border rounded" style="display: none;">
                                        <h6 class="text-info mb-3">
                                            <i class="bi bi-calendar-plus"></i> Realizar Reserva
                                        </h6>
                                        <p class="mb-2 text-light">Puedes realizar una reserva para este vuelo.</p>
                                        <a href="reserva-vuelo.jsp" class="btn btn-sm btn-outline-info">
                                            <i class="bi bi-calendar-check"></i> Reservar este Vuelo
                                        </a>
                                    </div>

                                    <!-- Opción de reserva para usuarios no autenticados -->
                                    <div id="infoNoAutenticado" class="reserva-section p-3 mt-3 bg-dark border rounded" style="display: none;">
                                        <h6 class="text-warning mb-3">
                                            <i class="bi bi-person"></i> Iniciar Sesión para Reservar
                                        </h6>
                                        <p class="mb-2 text-light">Inicia sesión para realizar una reserva en este vuelo.</p>
                                        <button type="button" class="btn btn-sm btn-outline-warning" data-bs-toggle="modal" data-bs-target="#loginModal">
                                            <i class="bi bi-box-arrow-in-right"></i> Iniciar Sesión
                                        </button>
                                    </div>
                                </div>

                                <!-- Acciones generales -->
                                <div class="d-flex gap-2 mt-4">
                                    <a href="reserva-vuelo.jsp" class="btn btn-success">
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

<!-- Container para mensajes toast -->
<div id="toastContainer"></div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script src="JsLogica/consulta-vuelo.js"></script>
</body>
</html>
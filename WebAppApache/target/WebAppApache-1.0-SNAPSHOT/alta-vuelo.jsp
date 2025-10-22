<!DOCTYPE html>
<html lang="es">
<head>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alta de Vuelo - Juan Viajes</title>
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
    <div class="card shadow-lg">
        <div class="card-header">
            <h4 class="mb-0">Alta de Vuelo</h4>
            <p class="mb-0 mt-2 small">Complete los datos para crear un nuevo vuelo asociado a una de sus rutas</p>
        </div>
        <div class="card-body">

            <form id="formAltaVuelo" name="formAltaVuelo" enctype="multipart/form-data" method="post" novalidate>
                <!-- Información de la aerolínea -->
                <div class="alert alert-info">
                    <strong>Aerolínea:</strong> <span id="aerolineaName">(no autenticado)</span>
                </div>

                <!-- Selección de ruta -->
                <div class="mb-3">
                    <label for="rutaVuelo" class="form-label">Seleccionar Ruta de Vuelo *</label>
                    <select class="form-select" id="rutaVuelo" name="nombreRuta" required>
                        <option value="">Seleccione una ruta...</option>
                    </select>
                    <div class="invalid-feedback">Por favor seleccione una ruta de vuelo.</div>
                </div>

                <hr class="my-4">

                <!-- Datos del vuelo -->
                <h5 class="mb-3">Datos del Vuelo</h5>

                <div class="mb-3">
                    <label for="nombreVuelo" class="form-label">Nombre del Vuelo *</label>
                    <input type="text" class="form-control" id="nombreVuelo" name="nombreVuelo" placeholder="Ej: ZL1502001" required>
                    <div class="form-text">El nombre debe ser único en la plataforma.</div>
                    <div class="invalid-feedback">Por favor ingrese un nombre para el vuelo.</div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="fechaVuelo" class="form-label">Fecha del Vuelo *</label>
                        <input type="date" class="form-control" id="fechaVuelo" name="fecha" required>
                        <div class="invalid-feedback">Por favor seleccione una fecha.</div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="duracionVuelo" class="form-label">Duración *</label>
                        <div class="input-group">
                            <input type="number" class="form-control" id="horas" placeholder="Horas" min="0" max="23" required>
                            <span class="input-group-text">h</span>
                            <input type="number" class="form-control" id="minutos" placeholder="Minutos" min="0" max="59" required>
                            <span class="input-group-text">min</span>
                        </div>
                        <div class="invalid-feedback">Por favor ingrese una duración válida.</div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="asientosTurista" class="form-label">Asientos turista *</label>
                        <input type="number" class="form-control" id="asientosTurista" name="asientosTurista" min="1" max="500" value="150" required>
                        <div class="form-text">Cantidad máxima de asientos en clase turista.</div>
                        <div class="invalid-feedback">Por favor ingrese un número válido.</div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="asientosEjecutivo" class="form-label">Asientos ejecutivo *</label>
                        <input type="number" class="form-control" id="asientosEjecutivo" name="asientosEjecutivo" min="1" max="100" value="30" required>
                        <div class="form-text">Cantidad máxima de asientos en clase ejecutiva.</div>
                        <div class="invalid-feedback">Por favor ingrese un número válido.</div>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="imagenVuelo" class="form-label">Imagen del vuelo (opcional)</label>
                    <input type="file" class="form-control" id="imagenVuelo" name="imagenVuelo" accept="image/*">
                    <div class="form-text">Formatos aceptados: JPG, PNG, GIF. Tamaño máximo: 5MB.</div>
                </div>

                <!-- campo oculto para enviar la duración en minutos -->
                <input type="hidden" id="duracion" name="duracion" value="0">

                <!-- Información de la ruta seleccionada (se actualiza dinámicamente) -->
                <div id="infoRuta" class="alert alert-secondary d-none">
                    <h6>Información de la ruta seleccionada:</h6>
                    <div id="detallesRuta"></div>
                </div>

                <!-- Botones -->
                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-success">
                        <i class="bi bi-check-lg"></i> Dar de Alta Vuelo
                    </button>
                    <button type="reset" class="btn btn-secondary">
                        <i class="bi bi-arrow-clockwise"></i> Limpiar Formulario
                    </button>
                    <a href="PaginaPrincipal.jsp" class="btn btn-outline-primary">
                        <i class="bi bi-house"></i> Volver al Inicio
                    </a>
                </div>
            </form>

        </div>
    </div>
</div>

<!-- Scripts -->
<script>
    // Establecer SESSION_API_BASE desde la JSP para evitar ambigüedades en context path
    // Ejemplo resultante: http://localhost:8080/WebAppApache_war_exploded
    try {
        window.SESSION_API_BASE = (window.location.origin || (window.location.protocol + '//' + window.location.host)) + '<%= request.getContextPath() %>';
    } catch (e) {
        window.SESSION_API_BASE = window.location.origin || (window.location.protocol + '//' + window.location.host);
    }
    console.log('SESSION_API_BASE (injected):', window.SESSION_API_BASE);
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script src="JsLogica/alta-vuelo.js"></script>
</body>
</html>
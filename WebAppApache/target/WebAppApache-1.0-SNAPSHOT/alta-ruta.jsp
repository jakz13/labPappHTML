<!DOCTYPE html>
<html lang="es">
<head>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alta de Ruta de Vuelo - Juan Viajes</title>
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
            <h4 class="mb-0">Alta de Ruta de Vuelo</h4>
            <p class="mb-0 mt-2 small">Complete los datos para crear una nueva ruta de vuelo</p>
        </div>
        <div class="card-body">

            <form id="formAltaRuta" novalidate>
                <!-- Datos básicos de la ruta -->
                <div class="mb-3">
                    <label for="nombreRuta" class="form-label">Nombre de la ruta *</label>
                    <input type="text" class="form-control" id="nombreRuta" required>
                    <div class="invalid-feedback">Por favor ingrese un nombre para la ruta.</div>
                </div>

                <div class="mb-3">
                    <label for="descripcionCorta" class="form-label">Descripción corta *</label>
                    <input type="text" class="form-control" id="descripcionCorta" required>
                    <div class="invalid-feedback">Por favor ingrese una descripción corta.</div>
                </div>

                <div class="mb-3">
                    <label for="descripcion" class="form-label">Descripción detallada</label>
                    <textarea class="form-control" id="descripcion" rows="3"></textarea>
                    <div class="form-text">Descripción opcional con más detalles sobre la ruta.</div>
                </div>

                <div class="mb-3">
                    <label for="hora" class="form-label">Hora de salida *</label>
                    <input type="time" class="form-control" id="hora" required>
                    <div class="invalid-feedback">Por favor seleccione una hora.</div>
                </div>

                <div class="row">
                    <div class="col-md-4 mb-3">
                        <label for="costoTurista" class="form-label">Costo turista (USD) *</label>
                        <input type="number" class="form-control" id="costoTurista" min="0" step="0.01" required>
                        <div class="invalid-feedback">Por favor ingrese un costo válido.</div>
                    </div>
                    <div class="col-md-4 mb-3">
                        <label for="costoEjecutivo" class="form-label">Costo ejecutivo (USD) *</label>
                        <input type="number" class="form-control" id="costoEjecutivo" min="0" step="0.01" required>
                        <div class="invalid-feedback">Por favor ingrese un costo válido.</div>
                    </div>
                    <div class="col-md-4 mb-3">
                        <label for="costoEquipaje" class="form-label">Costo equipaje extra (USD) *</label>
                        <input type="number" class="form-control" id="costoEquipaje" min="0" step="0.01" required>
                        <div class="invalid-feedback">Por favor ingrese un costo válido.</div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="origen" class="form-label">Ciudad de origen *</label>
                        <input type="text" class="form-control" id="origen" required>
                        <div class="invalid-feedback">Por favor ingrese la ciudad de origen.</div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="destino" class="form-label">Ciudad de destino *</label>
                        <input type="text" class="form-control" id="destino" required>
                        <div class="invalid-feedback">Por favor ingrese la ciudad de destino.</div>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="categorias" class="form-label">Categorías *</label>
                    <select class="form-select" id="categorias" multiple required>
                        </select>
                    <div class="invalid-feedback">Por favor seleccione al menos una categoría.</div>
                </div>

                <div class="mb-3">
                    <label for="imagenRuta" class="form-label">Imagen de la ruta (opcional)</label>
                    <input type="file" class="form-control" id="imagenRuta" accept="image/*">
                    <div class="form-text">Formatos: JPG, PNG. Máx. 2MB.</div>
                </div>

                <!-- Botones -->
                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-success">
                        <i class="bi bi-check-lg"></i> Dar de alta
                    </button>
                    <button type="reset" class="btn btn-secondary">
                        <i class="bi bi-arrow-clockwise"></i> Limpiar formulario
                    </button>
                    <a href="PaginaPrincipal.jsp" class="btn btn-outline-primary">
                        <i class="bi bi-house"></i> Volver al inicio
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script src="JsLogica/alta-ruta.js"></script>
</body>
</html>
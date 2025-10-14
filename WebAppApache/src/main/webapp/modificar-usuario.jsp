<!DOCTYPE html>
<html lang="es">
<head>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Modificar Datos de Usuario - Juan Viajes</title>
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="CssLogica/estilo-css.css">
</head>
<body class="bg-dark modificar-usuario">
<header>
    <%@ include file="navbar.jsp"%>
</header>

<div class="container mt-4 mb-5">
    <div class="row">
        <div class="col-lg-8 mx-auto">
            <div class="card shadow-lg">
                <div class="card-header">
                    <h4 class="mb-0">Modificar Datos de Usuario</h4>
                    <p class="mb-0 mt-2 small">Actualice su información personal en el sistema</p>
                </div>
                <div class="card-body p-4">

                    <!-- Información del usuario actual -->
                    <div class="alert alert-custom mb-4">
                        <div class="d-flex align-items-center">
                            <img src="https://via.placeholder.com/60/3498db/ffffff?text=MG" alt="Imagen de perfil" class="rounded-circle me-3" width="60" height="60">
                            <div>
                                <h6 class="mb-1">María González</h6>
                                <p class="mb-1 small">Cliente | Registrado: 15/03/2024</p>
                                <p class="mb-0 small text-muted">Solo puedes modificar tus propios datos</p>
                            </div>
                        </div>
                    </div>

                    <form id="formModificarUsuario" novalidate>
                        <!-- Imagen de perfil -->
                        <div class="text-center mb-4">
                            <img id="imagenPreview" src="https://via.placeholder.com/150/3498db/ffffff?text=MG"
                                 alt="Vista previa" class="imagen-preview mb-2">
                            <div>
                                <label for="imagenPerfil" class="btn btn-outline-primary btn-sm">
                                    <i class="bi bi-camera"></i> Cambiar Imagen
                                </label>
                                <input type="file" class="d-none" id="imagenPerfil" accept="image/*">
                                <button type="button" class="btn btn-outline-danger btn-sm" onclick="eliminarImagen()">
                                    <i class="bi bi-trash"></i> Eliminar
                                </button>
                            </div>
                            <div class="form-text">Formatos: JPG, PNG, GIF. Máx. 2MB</div>
                        </div>

                        <!-- Sección 1: Datos Básicos -->
                        <div class="form-section">
                            <h5 class="text-primary mb-3">Datos Básicos</h5>

                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="nickname" class="form-label">Nickname</label>
                                    <input type="text" class="form-control campo-bloqueado" id="nickname" value="maria_gonzalez" disabled>
                                    <div class="form-text">El nickname no se puede modificar</div>
                                </div>

                                <div class="col-md-6">
                                    <label for="correo" class="form-label">Correo electrónico</label>
                                    <input type="email" class="form-control campo-bloqueado" id="correo" value="maria.gonzalez@email.com" disabled>
                                    <div class="form-text">El correo no se puede modificar</div>
                                </div>

                                <div class="col-12">
                                    <label for="nombre" class="form-label">Nombre *</label>
                                    <input type="text" class="form-control" id="nombre" value="María" required>
                                    <div class="invalid-feedback">Por favor ingrese su nombre.</div>
                                </div>
                            </div>
                        </div>

                        <!-- Sección 2: Seguridad -->
                        <div class="form-section">
                            <h5 class="text-primary mb-3">Seguridad</h5>

                            <div class="alert alert-info">
                                <small><i class="bi bi-info-circle"></i> Complete solo si desea cambiar su contraseña</small>
                            </div>

                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="password" class="form-label">Nueva contraseña</label>
                                    <input type="password" class="form-control" id="password" minlength="6">
                                    <div class="password-strength" id="passwordStrength"></div>
                                    <div class="form-text">Mínimo 6 caracteres</div>
                                </div>

                                <div class="col-md-6">
                                    <label for="confirmar" class="form-label">Confirmar contraseña</label>
                                    <input type="password" class="form-control" id="confirmar">
                                    <div class="invalid-feedback" id="passwordError">Las contraseñas no coinciden</div>
                                </div>
                            </div>
                        </div>

                        <!-- Sección 3: Información Específica -->
                        <div class="form-section">
                            <h5 class="text-primary mb-3">Información Personal</h5>

                            <!-- Campos para Cliente -->
                            <div id="clienteFields">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label for="apellido" class="form-label">Apellido *</label>
                                        <input type="text" class="form-control" id="apellido" value="González" required>
                                        <div class="invalid-feedback">Por favor ingrese su apellido.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label for="fechaNacimiento" class="form-label">Fecha de nacimiento *</label>
                                        <input type="date" class="form-control" id="fechaNacimiento" value="1990-08-12" required>
                                        <div class="invalid-feedback">Por favor ingrese su fecha de nacimiento.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label for="nacionalidad" class="form-label">Nacionalidad *</label>
                                        <input type="text" class="form-control" id="nacionalidad" value="Uruguaya" required>
                                        <div class="invalid-feedback">Por favor ingrese su nacionalidad.</div>
                                    </div>

                                    <div class="col-md-3">
                                        <label for="tipoDoc" class="form-label">Tipo documento *</label>
                                        <select class="form-select" id="tipoDoc" required>
                                            <option value="">Seleccionar...</option>
                                            <option value="pasaporte">Pasaporte</option>
                                            <option value="cedula" selected>Cédula</option>
                                            <option value="dni">DNI</option>
                                        </select>
                                        <div class="invalid-feedback">Por favor seleccione un tipo de documento.</div>
                                    </div>

                                    <div class="col-md-3">
                                        <label for="numDoc" class="form-label">Número *</label>
                                        <input type="text" class="form-control" id="numDoc" value="4.123.456-7" required>
                                        <div class="invalid-feedback">Por favor ingrese el número de documento.</div>
                                    </div>
                                </div>
                            </div>

                            <!-- Campos para Aerolínea (oculto por defecto) -->
                            <div id="aerolineaFields" style="display: none;">
                                <div class="mb-3">
                                    <label for="descripcion" class="form-label">Descripción general *</label>
                                    <textarea class="form-control" id="descripcion" rows="3" required></textarea>
                                    <div class="invalid-feedback">Por favor ingrese una descripción.</div>
                                </div>

                                <div class="mb-3">
                                    <label for="sitioWeb" class="form-label">Sitio web</label>
                                    <input type="url" class="form-control" id="sitioWeb" placeholder="https://...">
                                    <div class="form-text">Enlace a su sitio web oficial (opcional)</div>
                                </div>
                            </div>
                        </div>

                        <!-- Botones de acción -->
                        <div class="d-flex gap-2 mt-4">
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-lg"></i> Guardar Cambios
                            </button>
                            <button type="reset" class="btn btn-secondary">
                                <i class="bi bi-arrow-clockwise"></i> Restablecer
                            </button>
                            <a href="consulta-usuario.jsp" class="btn btn-outline-primary">
                                <i class="bi bi-x-lg"></i> Cancelar
                            </a>
                        </div>
                    </form>

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
                <h5 class="modal-title" id="confirmacionModalLabel">Cambios Guardados</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body text-center">
                <div class="mb-3">
                    <i class="bi bi-check-circle-fill text-success" style="font-size: 3rem;"></i>
                </div>
                <h5>¡Datos actualizados correctamente!</h5>
                <p>Su información personal ha sido modificada en el sistema.</p>
            </div>
            <div class="modal-footer justify-content-center">
                <a href="consulta-usuario.jsp" class="btn btn-primary">Ver Mi Perfil</a>
                <button type="button" class="btn btn-outline-primary" data-bs-dismiss="modal">Continuar Editando</button>
            </div>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
<script src="JsLogica/modificar-usuario.js"></script>
</body>
</html>
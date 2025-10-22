<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Alta de Paquete - Descontinuado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="CssLogica/estilo-css.css">
</head>
<body class="bg-dark">
<header>
    <%@ include file="navbar.jsp" %>
</header>

<div class="container mt-5">
    <div class="row">
        <div class="col-lg-8 mx-auto">
            <div class="card shadow-lg">
                <div class="card-header">
                    <h4 class="mb-0">Funcionalidad descontinuada</h4>
                </div>
                <div class="card-body p-4">
                    <p class="text-light">La funcionalidad "Alta de Paquete" ya no se encuentra disponible en la plataforma.</p>
                    <p class="text-light">Si eras una aerolínea y necesitas gestionar paquetes, por favor contacta al administrador del sistema o revisa la documentación de la plataforma.</p>
                    <div class="d-flex gap-2 mt-3">
                        <a href="consulta-paquete.jsp" class="btn btn-primary">Ver Paquetes disponibles</a>
                        <a href="PaginaPrincipal.jsp" class="btn btn-outline-light">Volver al Inicio</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="JsLogica/session-manager.js"></script>
</body>
</html>
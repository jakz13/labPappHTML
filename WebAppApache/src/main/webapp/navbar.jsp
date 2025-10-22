<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<nav class="navbar navbar-expand-lg navbar-dark">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/PaginaPrincipal.jsp">
            Juan <span>Viajes</span>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarNav" aria-controls="navbarNav"
                aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item active">
                    <a class="nav-link" href="${pageContext.request.contextPath}/PaginaPrincipal.jsp">Inicio</a>
                </li>

                <!-- Menú Vuelos -->
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="vuelosDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        Vuelos
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="vuelosDropdown">
                        <li data-visible-for="invitado,cliente,aerolinea"><a class="dropdown-item" href="${pageContext.request.contextPath}/consulta-vuelo.jsp"><i class="bi bi-search"></i> Consulta de Vuelo</a></li>
                        <li data-visible-for="cliente,aerolinea"><a class="dropdown-item" href="${pageContext.request.contextPath}/consulta-reserva.jsp"><i class="bi bi-ticket-perforated"></i> Consulta de Reserva</a></li>
                        <li data-visible-for="cliente"><a class="dropdown-item" href="${pageContext.request.contextPath}/reserva-vuelo.jsp"><i class="bi bi-calendar-check"></i> Reserva de Vuelo</a></li>
                        <li data-visible-for="aerolinea"><a class="dropdown-item" href="${pageContext.request.contextPath}/alta-vuelo.jsp"><i class="bi bi-plus-circle"></i> Alta de Vuelo</a></li>
                    </ul>
                </li>

                <!-- Menú Paquetes -->
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="paquetesDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        Paquetes
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="paquetesDropdown">
                        <li data-visible-for="invitado,cliente,aerolinea"><a class="dropdown-item" href="${pageContext.request.contextPath}/consulta-paquete.jsp"><i class="bi bi-box-seam"></i> Consulta de Paquete</a></li>
                        <li data-visible-for="cliente"><a class="dropdown-item" href="${pageContext.request.contextPath}/compra-paquete.jsp"><i class="bi bi-cart-check"></i> Compra de Paquete</a></li>
                        <!-- Alta de Paquete eliminada: antes visible solo para aerolínea -->
                    </ul>
                </li>

                <!-- Menú Aerolíneas -->
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="aerolineasDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        Aerolíneas
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="aerolineasDropdown">
                        <li data-visible-for="aerolinea"><a class="dropdown-item" href="${pageContext.request.contextPath}/alta-ruta.jsp"><i class="bi bi-signpost"></i> Alta de Ruta</a></li>
                        <li data-visible-for="invitado,cliente,aerolinea"><a class="dropdown-item" href="${pageContext.request.contextPath}/consulta-ruta.jsp"><i class="bi bi-map"></i> Consulta de Ruta</a></li>
                        <li data-visible-for="aerolinea"><a class="dropdown-item" href="${pageContext.request.contextPath}/alta-vuelo.jsp"><i class="bi bi-airplane"></i> Alta de Vuelo</a></li>
                    </ul>
                </li>

                <!-- Menú Usuarios -->
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="usuariosDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        Usuarios
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="usuariosDropdown">
                        <li data-visible-for="invitado"><a class="dropdown-item" href="${pageContext.request.contextPath}/alta-usuario.jsp"><i class="bi bi-person-plus"></i> Alta de Usuario</a></li>
                        <li data-visible-for="invitado,cliente,aerolinea"><a class="dropdown-item" href="${pageContext.request.contextPath}/consulta-usuario.jsp"><i class="bi bi-person-vcard"></i> Consulta de Usuario</a></li>
                        <li data-visible-for="cliente,aerolinea"><a class="dropdown-item" href="${pageContext.request.contextPath}/modificar-usuario.jsp"><i class="bi bi-pencil-square"></i> Modificar Datos</a></li>
                    </ul>
                </li>
            </ul>

            <!-- Acciones de usuario -->
            <div class="user-actions">
                <span class="user-info" id="userInfo">
                    <i class="bi bi-person-circle"></i> Invitado
                </span>
                <button class="btn btn-outline-primary" id="loginBtn" data-bs-toggle="modal" data-bs-target="#loginModal">Iniciar Sesión</button>
                <button class="btn btn-primary d-none" id="logoutBtn">Cerrar Sesión</button>
            </div>
        </div>
    </div>
</nav>

<!-- Modal para inicio de sesión -->
<div class="modal fade" id="loginModal" tabindex="-1" aria-labelledby="loginModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="loginModalLabel">Iniciar Sesión</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="loginForm">
                    <div class="mb-3">
                        <label for="loginEmail" class="form-label">Email o Nickname</label>
                        <input type="text" class="form-control" id="loginEmail" required>
                    </div>
                    <div class="mb-3">
                        <label for="loginPassword" class="form-label">Contraseña</label>
                        <input type="password" class="form-control" id="loginPassword" required>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Iniciar Sesión</button>
                </form>
            </div>
            <div class="modal-footer">
                <p class="text-center w-100">¿No tienes cuenta? <a href="alta-usuario.jsp" class="text-accent">Regístrate aquí</a></p>
            </div>
        </div>
    </div>
</div>
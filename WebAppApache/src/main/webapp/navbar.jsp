<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<nav class="navbar navbar-expand-lg navbar-dark">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/PaginaPrincipal.jsp">
            Juan <span>Viajes</span>
        </a>
        <!-- ... resto del navbar ... -->
        <div class="user-actions">
            <span class="user-info" id="userInfo">
                <i class="bi bi-person-circle"></i> Invitado
            </span>
            <button class="btn btn-outline-primary" id="loginBtn" data-bs-toggle="modal" data-bs-target="#loginModal">Iniciar Sesión</button>
            <button class="btn btn-primary d-none" id="logoutBtn">Cerrar Sesión</button>
        </div>
    </div>
</nav>
<script src="JsLogica/session-manager.js"></script>

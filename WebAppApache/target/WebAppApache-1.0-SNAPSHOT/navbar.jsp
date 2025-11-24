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
                        <!--Consulta de Check-in -->
                        <li data-visible-for="cliente"><a class="dropdown-item" href="${pageContext.request.contextPath}/consulta-checkin.jsp"><i class="bi bi-check-circle"></i> Consulta de Check-in</a></li>
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

            <!-- BARRA DE BÚSQUEDA CON ICONO EXPANDIBLE -->
            <div class="navbar-search">
                <div class="search-container position-relative">
                    <!-- Icono de búsqueda (estado inicial) -->
                    <button class="btn btn-outline-light search-icon" id="searchToggle">
                        <i class="bi bi-search"></i>
                    </button>

                    <!-- Barra de búsqueda expandible (oculta inicialmente) -->
                    <div class="search-expandable" id="searchExpandable">
                        <form id="searchForm" action="${pageContext.request.contextPath}/ResultadosBusqueda" method="get" class="d-flex">
                            <div class="input-group">
                                <input type="text"
                                       class="form-control"
                                       id="globalSearch"
                                       name="q"
                                       placeholder="Buscar rutas y paquetes..."
                                       aria-label="Buscar"
                                       autocomplete="off">
                                <button class="btn btn-primary" type="submit">
                                    <i class="bi bi-arrow-right"></i>
                                </button>
                            </div>
                        </form>

                        <!-- SUGERENCIAS EN TIEMPO REAL -->
                        <div id="searchSuggestions" class="search-suggestions">
                            <div class="suggestions-header">
                                <small class="text-muted">Sugerencias</small>
                            </div>
                            <div id="suggestionsList" class="suggestions-list">
                                <!-- Las sugerencias se cargan aquí dinámicamente -->
                            </div>
                        </div>
                    </div>
                </div>
            </div>

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

<!-- JavaScript para el comportamiento expandible -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const searchToggle = document.getElementById('searchToggle');
        const searchExpandable = document.getElementById('searchExpandable');
        const searchInput = document.getElementById('globalSearch');
        const suggestionsContainer = document.getElementById('searchSuggestions');
        const suggestionsList = document.getElementById('suggestionsList');
        const searchForm = document.getElementById('searchForm');
        const navbarNav = document.getElementById('navbarNav');

        let timeoutId;
        let isSearchExpanded = false;

        // Función para expandir la búsqueda
        function expandSearch() {
            searchExpandable.classList.add('expanded');
            searchToggle.classList.add('hidden');
            navbarNav.classList.add('search-active');
            isSearchExpanded = true;

            // Enfocar el input después de la animación
            setTimeout(() => {
                searchInput.focus();
            }, 300);
        }

        // Función para contraer la búsqueda
        function collapseSearch() {
            searchExpandable.classList.remove('expanded');
            searchToggle.classList.remove('hidden');
            navbarNav.classList.remove('search-active');
            isSearchExpanded = false;
            hideSuggestions();
            searchInput.value = '';
        }

        // Función para cargar sugerencias
        function loadSuggestions(query) {
            if (query.length < 2) {
                hideSuggestions();
                return;
            }

            const url = '${pageContext.request.contextPath}/ResultadosBusqueda?action=suggestions&q=' + encodeURIComponent(query);
            console.log('🔍 Cargando sugerencias desde:', url);

            fetch(url)
                .then(response => {
                    console.log('📥 Respuesta recibida, status:', response.status);
                    if (!response.ok) {
                        throw new Error('HTTP error! status: ' + response.status);
                    }
                    return response.text(); // Primero obtener como texto para debug
                })
                .then(text => {
                    console.log('📄 Respuesta texto:', text);
                    try {
                        const data = JSON.parse(text);
                        console.log('✅ JSON parseado correctamente:', data);
                        displaySuggestions(data);
                    } catch (e) {
                        console.error('❌ Error parseando JSON:', e);
                        console.error('Texto recibido:', text);
                        hideSuggestions();
                    }
                })
                .catch(error => {
                    console.error('❌ Error cargando sugerencias:', error);
                    hideSuggestions();
                });
        }

        // Función para mostrar sugerencias
        function displaySuggestions(suggestions) {
            if (!suggestions || suggestions.length === 0) {
                hideSuggestions();
                return;
            }

            suggestionsList.innerHTML = '';

            suggestions.forEach(item => {
                const suggestionItem = document.createElement('a');
                suggestionItem.className = 'suggestion-item';
                suggestionItem.href = '#';
                suggestionItem.innerHTML = `
                    <div class="d-flex align-items-center">
                        <i class="bi ${item.tipo == 'ruta' ? 'bi-geo-alt' : 'bi-box'} me-2 text-primary"></i>
                        <div class="flex-grow-1">
                            <div class="fw-semibold">${item.nombre}</div>
                            <small class="text-muted text-truncate">${item.descripcion}</small>
                        </div>
                        <span class="badge bg-secondary badge-sm ms-2">${item.tipo}</span>
                    </div>
                `;

                suggestionItem.addEventListener('click', function(e) {
                    e.preventDefault();
                    searchInput.value = item.nombre;
                    searchForm.submit();
                });

                suggestionsList.appendChild(suggestionItem);
            });

            showSuggestions();
        }

        function showSuggestions() {
            suggestionsContainer.style.display = 'block';
        }

        function hideSuggestions() {
            suggestionsContainer.style.display = 'none';
        }

        // Event listeners
        searchToggle.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            expandSearch();
        });

        searchInput.addEventListener('input', function() {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => {
                loadSuggestions(this.value);
            }, 300);
        });

        searchInput.addEventListener('focus', function() {
            if (this.value.length >= 2) {
                loadSuggestions(this.value);
            }
        });

        // Cerrar búsqueda al hacer clic fuera
        document.addEventListener('click', function(e) {
            if (isSearchExpanded &&
                !searchExpandable.contains(e.target) &&
                !searchToggle.contains(e.target)) {
                collapseSearch();
            }
        });

        // Prevenir que se cierre al hacer clic dentro de la barra de búsqueda
        searchExpandable.addEventListener('click', function(e) {
            e.stopPropagation();
        });

        // Manejar teclas
        searchInput.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                if (suggestionsContainer.style.display === 'block') {
                    hideSuggestions();
                } else {
                    collapseSearch();
                }
            }
        });

        // Cerrar al enviar el formulario
        searchForm.addEventListener('submit', function() {
            collapseSearch();
        });
    });
</script>

<style>
    .navbar-search {
        position: relative;
        margin-left: 1rem;
    }

    .search-container {
        display: flex;
        align-items: center;
        position: relative;
    }

    .search-icon {
        padding: 0.375rem 0.75rem;
        border-radius: 0.375rem;
        transition: all 0.3s ease;
        z-index: 1001;
        position: relative;
    }

    .search-icon.hidden {
        opacity: 0;
        visibility: hidden;
        width: 0;
        margin: 0;
        padding: 0;
    }

    .search-expandable {
        position: absolute;
        top: 110%; /* aparece justo debajo del icono */
        right: 0;
        width: 0;
        opacity: 0;
        visibility: hidden;
        transition: all 0.3s ease;
        z-index: 1000;
        overflow: hidden;
        border-radius: 0.375rem;
    }

    .search-expandable.expanded {
        width: 350px;
        opacity: 1;
        visibility: visible;
        padding: 0.5rem 0; /* ajustado para no alterar el estilo base */
    }

    /* Ocultar otros elementos del navbar cuando la búsqueda está activa */
    #navbarNav.search-active .navbar-nav,
    #navbarNav.search-active .user-actions {
        opacity: 0.3;
        pointer-events: none;
        transition: opacity 0.3s ease;
    }

    .search-suggestions {
        position: absolute;
        top: 100%;
        left: 0;
        right: 0;
        z-index: 1002;
        max-height: 200px;
        overflow-y: auto;
        background: white;
        border-radius: 0.375rem;
        box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
        display: none;
        margin-top: 0.25rem;
    }

    .suggestions-header {
        background-color: #f8f9fa;
        border-bottom: 1px solid #dee2e6;
        border-radius: 0.375rem 0.375rem 0 0;
        padding: 0.5rem 0.75rem;
    }

    .suggestions-list {
        padding: 0;
    }

    .suggestion-item {
        display: block;
        padding: 0.5rem 0.75rem;
        color: #212529;
        text-decoration: none;
        border-bottom: 1px solid #f8f9fa;
        transition: background-color 0.15s ease-in-out;
        cursor: pointer;
    }

    .suggestion-item:hover {
        background-color: #f8f9fa;
        text-decoration: none;
        color: #212529;
    }

    .suggestion-item:last-child {
        border-bottom: none;
        border-radius: 0 0 0.375rem 0.375rem;
    }

    .badge-sm {
        font-size: 0.65em;
        padding: 0.25em 0.4em;
    }

    /* Asegurar que el input se vea bien */
    .search-expandable .input-group {
        width: 100%;
    }

    .search-expandable .form-control {
        border: 1px solid #dee2e6;
        background: transparent; /* mantiene el fondo del navbar */
        color: inherit;
    }

    /* Responsive */
    @media (max-width: 768px) {
        .search-expandable.expanded {
            width: 280px;
            right: -50px;
        }

        .navbar-search {
            margin-left: 0.5rem;
        }
    }

    @media (max-width: 576px) {
        .search-expandable.expanded {
            width: 220px;
            right: -80px;
        }
    }
</style>

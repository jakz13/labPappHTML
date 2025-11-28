/**
 * Navbar JavaScript - Maneja búsqueda, sugerencias y funcionalidades del navbar
 * Versión unificada y corregida
 */

class NavbarManager {
    constructor() {
        this.searchInput = document.querySelector('input[name="q"]');
        this.searchForm = document.querySelector('form[action*="ResultadosBusqueda"]');
        this.suggestionsContainer = document.getElementById('searchSuggestions');
        this.suggestionsList = document.getElementById('suggestionsList');
        this.loginForm = document.getElementById('loginForm');
        this.loginBtn = document.getElementById('loginBtn');
        this.logoutBtn = document.getElementById('logoutBtn');
        this.userInfo = document.getElementById('userInfo');

        this.timeoutId = null;
        this.init();
    }

    init() {
        this.initSearch();
        this.initSession();
        this.initLoginForm();
    }

    // =================== BÚSQUEDA Y SUGERENCIAS ===================

    initSearch() {
        if (!this.searchInput || !this.searchForm) {
            console.log('❌ Elementos de búsqueda no encontrados');
            return;
        }

        // Búsqueda en tiempo real para sugerencias
        this.searchInput.addEventListener('input', (e) => {
            this.handleSearchInput(e.target.value);
        });

        // Mostrar sugerencias al enfocar
        this.searchInput.addEventListener('focus', () => {
            if (this.searchInput.value.length >= 2) {
                this.loadSuggestions(this.searchInput.value);
            }
        });

        // Cerrar sugerencias al hacer clic fuera
        document.addEventListener('click', (e) => {
            if (this.suggestionsContainer &&
                !this.suggestionsContainer.contains(e.target) &&
                !this.searchInput.contains(e.target)) {
                this.hideSuggestions();
            }
        });

        // Manejar tecla Escape
        this.searchInput.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.hideSuggestions();
            }
        });
    }

    handleSearchInput(query) {
        clearTimeout(this.timeoutId);
        this.timeoutId = setTimeout(() => {
            this.loadSuggestions(query);
        }, 300);
    }

    async loadSuggestions(query) {
        console.log('🔍 Buscando sugerencias para:', query);

        if (!query || query.length < 2) {
            this.hideSuggestions();
            return;
        }

        const contextPath = window.CONTEXT_PATH || '';
        const url = `${contextPath}/ResultadosBusqueda?action=suggestions&q=${encodeURIComponent(query)}`;

        console.log('📡 URL de sugerencias:', url);

        try {
            const response = await fetch(url);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const data = await response.json();
            console.log('✅ Sugerencias recibidas:', data);

            if (Array.isArray(data) && data.length > 0) {
                this.displaySuggestions(data, query);
            } else {
                this.hideSuggestions();
            }
        } catch (error) {
            console.error('❌ Error obteniendo sugerencias:', error);
            this.hideSuggestions();
        }
    }

    displaySuggestions(suggestions, query) {
        if (!this.suggestionsList) return;

        this.suggestionsList.innerHTML = '';

        suggestions.forEach((item) => {
            if (!item || !item.nombre) return;

            const suggestionItem = document.createElement('a');
            suggestionItem.className = 'suggestion-item dropdown-item';
            suggestionItem.href = '#';
            suggestionItem.style.cursor = 'pointer';

            const tipo = item.tipo || 'ruta';
            const icon = tipo === 'ruta' ? 'bi-geo-alt' : 'bi-box';
            const badgeClass = tipo === 'ruta' ? 'bg-primary' : 'bg-success';

            // Resaltar coincidencias
            const nombreResaltado = this.resaltarCoincidencias(item.nombre, query);
            const descripcionResaltada = this.resaltarCoincidencias(item.descripcion || '', query);

            suggestionItem.innerHTML = `
                <div class="d-flex align-items-center">
                    <i class="bi ${icon} me-2"></i>
                    <div class="flex-grow-1">
                        <div class="fw-semibold">${nombreResaltado}</div>
                        ${item.descripcion ? `<small class="text-muted">${descripcionResaltada}</small>` : ''}
                    </div>
                    <span class="badge ${badgeClass} badge-sm ms-2">${tipo}</span>
                </div>
            `;

            suggestionItem.addEventListener('click', (e) => {
                e.preventDefault();
                this.searchInput.value = item.nombre;
                this.hideSuggestions();

                const contextPath = window.CONTEXT_PATH || '';
                const tipo = item.tipo || 'ruta';

                // Si es ruta: incrementar visitas vía un servlet y redirigir a consulta-ruta.jsp
                if (tipo === 'ruta') {
                    // Guardar la ruta para que consulta-ruta.jsp la seleccione al cargar
                    try { sessionStorage.setItem('consultaRuta_rutaSeleccionada', item.nombre); } catch (err) { console.warn('No se pudo setear sessionStorage:', err); }

                    // Intentar incrementar visitas por backend de forma asíncrona, pero no bloquear la redirección
                    (async () => {
                        try {
                            // Intentar llamar a un endpoint REST/servlet que incremente visitas: /incrementarVisitasRuta?nombreRuta=...
                            // Si no existe, el servlet backend debería mapear la llamada o la operación SOAP `incrementarVisitasRuta`.
                            const incUrl = `${contextPath}/incrementarVisitasRuta?nombreRuta=${encodeURIComponent(item.nombre)}`;
                            const resp = await fetch(incUrl, { method: 'POST', credentials: 'include' });
                            if (!resp.ok) console.warn('No fue posible incrementar visitas (HTTP ' + resp.status + ')');
                        } catch (error) {
                            console.warn('Error incrementando visitas:', error);
                        }
                    })();

                    // Redirigir a la página de consulta de rutas
                    window.location.href = contextPath + '/consulta-ruta.jsp';
                    return;
                }

                // Si es paquete: intentar incrementar visita del paquete (si existe endpoint) y redirigir a consulta-paquete.jsp
                if (tipo === 'paquete') {
                    try { sessionStorage.setItem('consultaPaquete_seleccionado', item.id || item.nombre || ''); } catch (err) { console.warn('No se pudo setear sessionStorage:', err); }

                    (async () => {
                        try {
                            const incUrl = `${contextPath}/incrementarVisitasPaquete?id=${encodeURIComponent(item.id || item.nombre)}`;
                            const resp = await fetch(incUrl, { method: 'POST', credentials: 'include' });
                            if (!resp.ok) console.warn('No fue posible incrementar visitas paquete (HTTP ' + resp.status + ')');
                        } catch (error) {
                            console.warn('Error incrementando visitas paquete:', error);
                        }
                    })();

                    window.location.href = contextPath + '/consulta-paquete.jsp';
                    return;
                }

                // Fallback: enviar el formulario de búsqueda
                this.searchForm.submit();
            });

            this.suggestionsList.appendChild(suggestionItem);
        });

        this.showSuggestions();
    }

    resaltarCoincidencias(texto, query) {
        if (!query || !texto) return this.escapeHtml(texto || '');

        const regex = new RegExp(`(${this.escapeRegex(query)})`, 'gi');
        return this.escapeHtml(texto).replace(regex, '<mark class="highlight-suggestion">$1</mark>');
    }

    escapeRegex(string) {
        return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    }

    showSuggestions() {
        if (this.suggestionsContainer) {
            this.suggestionsContainer.style.display = 'block';
        }
    }

    hideSuggestions() {
        if (this.suggestionsContainer) {
            this.suggestionsContainer.style.display = 'none';
        }
    }

    // =================== MANEJO DE SESIÓN ===================

    initSession() {
        this.checkSession();
    }

    async checkSession() {
        try {
            const contextPath = window.CONTEXT_PATH || '';
            const response = await fetch(`${contextPath}/api/check-session`, {
                credentials: 'include'
            });

            if (response.ok) {
                const data = await response.json();
                this.updateUI(data);
            } else {
                this.updateUI({ authenticated: false });
            }
        } catch (error) {
            console.log('⚠️ Error verificando sesión:', error);
            this.updateUI({ authenticated: false });
        }
    }

    initLoginForm() {
        if (this.loginForm) {
            this.loginForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleLogin();
            });
        }

        if (this.logoutBtn) {
            this.logoutBtn.addEventListener('click', () => {
                this.handleLogout();
            });
        }
    }

    async handleLogin() {
        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;

        console.log('🔐 Intentando login:', email);

        try {
            const contextPath = window.CONTEXT_PATH || '';
            const response = await fetch(`${contextPath}/api/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `nickname=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`,
                credentials: 'include'
            });

            const data = await response.json();

            if (data.success) {
                // Cerrar modal
                const modal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
                if (modal) modal.hide();

                // Actualizar UI
                this.updateUI({
                    authenticated: true,
                    nickname: data.nickname,
                    tipo: data.tipo
                });

                // Recargar para actualizar menús
                setTimeout(() => location.reload(), 500);
            } else {
                alert('Error: ' + (data.error || 'Credenciales incorrectas'));
            }
        } catch (error) {
            console.error('❌ Error en login:', error);
            alert('Error de conexión con el servidor');
        }
    }

    async handleLogout() {
        try {
            const contextPath = window.CONTEXT_PATH || '';
            await fetch(`${contextPath}/api/logout`, {
                method: 'POST',
                credentials: 'include'
            });

            // Recargar página
            location.reload();
        } catch (error) {
            console.error('❌ Error en logout:', error);
            location.reload();
        }
    }

    updateUI(sessionData) {
        if (!this.userInfo || !this.loginBtn || !this.logoutBtn) return;

        if (sessionData.authenticated) {
            this.userInfo.innerHTML = `<i class="bi bi-person-circle"></i> ${sessionData.nickname}`;
            this.loginBtn.classList.add('d-none');
            this.logoutBtn.classList.remove('d-none');
        } else {
            this.userInfo.innerHTML = `<i class="bi bi-person-circle"></i> Invitado`;
            this.loginBtn.classList.remove('d-none');
            this.logoutBtn.classList.add('d-none');
        }

        // Actualizar visibilidad del menú
        this.updateMenuVisibility(sessionData);
    }

    updateMenuVisibility(sessionData) {
        let role = 'invitado';
        if (sessionData.authenticated) {
            role = (sessionData.tipo || '').toLowerCase();
        }

        const menuItems = document.querySelectorAll('[data-visible-for]');
        menuItems.forEach(item => {
            const allowedRoles = (item.getAttribute('data-visible-for') || '')
                .split(',')
                .map(r => r.trim().toLowerCase());

            if (allowedRoles.length === 0 || allowedRoles.includes(role)) {
                item.style.display = '';
            } else {
                item.style.display = 'none';
            }
        });

        // Ocultar dropdowns vacíos
        const dropdowns = document.querySelectorAll('.nav-item.dropdown');
        dropdowns.forEach(dd => {
            const visibleItems = dd.querySelectorAll('ul.dropdown-menu > li[style=""]');
            dd.style.display = visibleItems.length > 0 ? '' : 'none';
        });
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    // Configurar contexto global si no existe
    if (!window.CONTEXT_PATH) {
        try {
            const path = window.location.pathname;
            const contextPath = path.substring(0, path.indexOf('/', 1));
            window.CONTEXT_PATH = contextPath;
        } catch (e) {
            window.CONTEXT_PATH = '';
        }
    }

    // Inicializar navbar
    window.navbarManager = new NavbarManager();
    console.log('✅ Navbar inicializado correctamente');
});
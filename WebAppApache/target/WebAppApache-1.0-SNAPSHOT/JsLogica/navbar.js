/**
 * Navbar JavaScript - Maneja búsqueda, sugerencias y funcionalidades del navbar
 */

class NavbarManager {
    constructor() {
        this.searchToggle = document.getElementById('searchToggle');
        this.searchExpandable = document.getElementById('searchExpandable');
        this.searchInput = document.getElementById('globalSearch');
        this.suggestionsContainer = document.getElementById('searchSuggestions');
        this.suggestionsList = document.getElementById('suggestionsList');
        this.searchForm = document.getElementById('searchForm');
        this.navbarNav = document.getElementById('navbarNav');
        this.loginForm = document.getElementById('loginForm');
        this.loginBtn = document.getElementById('loginBtn');
        this.logoutBtn = document.getElementById('logoutBtn');
        this.userInfo = document.getElementById('userInfo');

        this.timeoutId = null;
        this.isSearchExpanded = false;
        this.currentUser = null;

        this.init();
    }

    init() {
        this.initSearch();
        this.initUserSession();
        this.initLoginForm();
        this.initMenuVisibility();
    }

    // =================== BÚSQUEDA Y SUGERENCIAS ===================

    initSearch() {
        // Event listeners para búsqueda
        this.searchToggle.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            this.expandSearch();
        });

        this.searchInput.addEventListener('input', (e) => {
            this.handleSearchInput(e.target.value);
        });

        this.searchInput.addEventListener('focus', () => {
            if (this.searchInput.value.length >= 2) {
                this.loadSuggestions(this.searchInput.value);
            }
        });

        // Cerrar búsqueda al hacer clic fuera
        document.addEventListener('click', (e) => {
            if (this.isSearchExpanded &&
                !this.searchExpandable.contains(e.target) &&
                !this.searchToggle.contains(e.target)) {
                this.collapseSearch();
            }
        });

        // Prevenir que se cierre al hacer clic dentro de la barra de búsqueda
        this.searchExpandable.addEventListener('click', (e) => {
            e.stopPropagation();
        });

        // Manejar teclas
        this.searchInput.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                if (this.suggestionsContainer.style.display === 'block') {
                    this.hideSuggestions();
                } else {
                    this.collapseSearch();
                }
            }
        });

        // Cerrar al enviar el formulario
        this.searchForm.addEventListener('submit', () => {
            this.collapseSearch();
        });
    }

    expandSearch() {
        this.searchExpandable.classList.add('expanded');
        this.searchToggle.classList.add('hidden');
        this.navbarNav.classList.add('search-active');
        this.isSearchExpanded = true;

        // Enfocar el input después de la animación
        setTimeout(() => {
            this.searchInput.focus();
        }, 300);
    }

    collapseSearch() {
        this.searchExpandable.classList.remove('expanded');
        this.searchToggle.classList.remove('hidden');
        this.navbarNav.classList.remove('search-active');
        this.isSearchExpanded = false;
        this.hideSuggestions();
        this.searchInput.value = '';
    }

    handleSearchInput(query) {
        clearTimeout(this.timeoutId);
        this.timeoutId = setTimeout(() => {
            this.loadSuggestions(query);
        }, 300);
    }

    // En navbar.js - función loadSuggestions mejorada
    async loadSuggestions(query) {
        console.log('🔍 [NAVBAR] Buscando sugerencias inteligentes para:', query);

        if (!query || query.length < 2) {
            console.log('❌ [NAVBAR] Query demasiado corta');
            this.hideSuggestions();
            return;
        }

        const url = `${window.CONTEXT_PATH || ''}/busqueda?action=suggestions&q=${encodeURIComponent(query)}`;
        console.log('📡 [NAVBAR] URL:', url);

        try {
            const response = await fetch(url);
            console.log('📥 [NAVBAR] Status:', response.status, 'OK:', response.ok);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const text = await response.text();
            console.log('📄 [NAVBAR] Respuesta texto:', text);

            const cleanText = text.trim();

            try {
                const data = JSON.parse(cleanText);
                console.log('✅ [NAVBAR] JSON parseado:', data);

                if (Array.isArray(data)) {
                    // Ordenar por relevancia si no viene ordenado
                    data.sort((a, b) => {
                        const relevanciaA = a.relevancia || 0;
                        const relevanciaB = b.relevancia || 0;
                        return relevanciaB - relevanciaA;
                    });

                    this.displaySuggestionsInteligentes(data, query);
                } else {
                    console.error('❌ [NAVBAR] La respuesta no es un array:', data);
                    this.hideSuggestions();
                }
            } catch (e) {
                console.error('❌ [NAVBAR] Error parseando JSON:', e);
                console.error('📄 [NAVBAR] Texto que falló:', cleanText);
                this.hideSuggestions();
            }
        } catch (error) {
            console.error('❌ [NAVBAR] Error en fetch:', error);
            this.hideSuggestions();
        }
    }

// Nueva función para mostrar sugerencias inteligentes
    displaySuggestionsInteligentes(suggestions, query) {
        console.log('🎯 [NAVBAR] Mostrando sugerencias inteligentes:', suggestions);

        if (!suggestions || !Array.isArray(suggestions) || suggestions.length === 0) {
            console.log('❌ [NAVBAR] No hay sugerencias para mostrar');
            this.hideSuggestions();
            return;
        }

        this.suggestionsList.innerHTML = '';

        suggestions.forEach((item, index) => {
            console.log(`📝 [NAVBAR] Procesando sugerencia ${index + 1}:`, item);

            // Validar que el item tenga los campos necesarios
            if (!item || typeof item !== 'object') {
                console.warn(`⚠️ [NAVBAR] Sugerencia ${index} inválida:`, item);
                return;
            }

            const tipo = item.tipo || 'ruta';
            const nombre = item.nombre || 'Sin nombre';
            const descripcion = item.descripcion || 'Sin descripción';
            const relevancia = item.relevancia || 0;

            console.log(`🏷️ [NAVBAR] Tipo: ${tipo}, Nombre: ${nombre}, Relevancia: ${relevancia}`);

            const suggestionItem = document.createElement('a');
            suggestionItem.className = 'suggestion-item';
            suggestionItem.href = '#';
            suggestionItem.style.cursor = 'pointer';

            // Resaltar texto coincidente
            const nombreResaltado = this.resaltarCoincidencias(nombre, query);
            const descripcionResaltada = this.resaltarCoincidencias(descripcion, query);

            suggestionItem.innerHTML = `
            <div class="d-flex align-items-center">
                <i class="bi ${tipo === 'ruta' ? 'bi-geo-alt' : 'bi-box'} me-2 
                   ${relevancia > 80 ? 'text-warning' : 'text-primary'}"></i>
                <div class="flex-grow-1">
                    <div class="fw-semibold">${nombreResaltado}</div>
                    <small class="text-muted d-block">${descripcionResaltada}</small>
                    ${relevancia > 0 ? `<small class="text-info">Relevancia: ${relevancia}</small>` : ''}
                </div>
                <span class="badge ${this.getBadgeClass(tipo, relevancia)} badge-sm ms-2">
                    ${this.escapeHtml(tipo)}
                </span>
            </div>
        `;

            suggestionItem.addEventListener('click', (e) => {
                e.preventDefault();
                console.log('🖱️ [NAVBAR] Clic en sugerencia:', nombre);
                this.searchInput.value = nombre;
                this.hideSuggestions();
                this.searchForm.submit();
            });

            this.suggestionsList.appendChild(suggestionItem);
        });

        this.showSuggestions();
        console.log('✅ [NAVBAR] Sugerencias inteligentes mostradas en el DOM');
    }

// Función para resaltar coincidencias en el texto
    resaltarCoincidencias(texto, query) {
        if (!query || !texto) return this.escapeHtml(texto || '');

        const queryLower = query.toLowerCase();
        const textoLower = texto.toLowerCase();

        if (!textoLower.includes(queryLower)) {
            return this.escapeHtml(texto);
        }

        const regex = new RegExp(`(${this.escapeRegex(query)})`, 'gi');
        return texto.replace(regex, '<mark class="highlight-suggestion">$1</mark>');
    }

// Función para escapar caracteres especiales en regex
    escapeRegex(string) {
        return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    }

// Función para determinar la clase del badge según tipo y relevancia
    getBadgeClass(tipo, relevancia) {
        if (relevancia > 80) {
            return 'bg-warning text-dark';
        } else if (relevancia > 50) {
            return 'bg-success';
        } else {
            return tipo === 'ruta' ? 'bg-primary' : 'bg-success';
        }
    }

    showSuggestions() {
        this.suggestionsContainer.style.display = 'block';
    }

    hideSuggestions() {
        this.suggestionsContainer.style.display = 'none';
    }

    // =================== MANEJO DE USUARIO Y SESIÓN ===================

    initUserSession() {
        // Verificar si hay usuario en sessionStorage
        const userData = sessionStorage.getItem('currentUser');
        if (userData) {
            this.currentUser = JSON.parse(userData);
            this.updateUserInterface();
        }

        // También verificar en el servidor (si hay una sesión activa)
        this.checkServerSession();
    }

    async checkServerSession() {
        try {
            const response = await fetch(`${window.CONTEXT_PATH || ''}/api/session`);
            if (response.ok) {
                const userData = await response.json();
                if (userData && userData.nickname) {
                    this.currentUser = userData;
                    sessionStorage.setItem('currentUser', JSON.stringify(userData));
                    this.updateUserInterface();
                }
            }
        } catch (error) {
            console.log('ℹ️ [NAVBAR] No hay sesión activa en el servidor');
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

        console.log('🔐 [NAVBAR] Intentando login:', email);

        try {
            const response = await fetch(`${window.CONTEXT_PATH || ''}/api/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password })
            });

            if (response.ok) {
                const userData = await response.json();
                this.currentUser = userData;
                sessionStorage.setItem('currentUser', JSON.stringify(userData));

                // Cerrar modal
                const loginModal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
                if (loginModal) {
                    loginModal.hide();
                }

                this.updateUserInterface();
                this.showToast('¡Bienvenido!', 'success');

            } else {
                const error = await response.text();
                this.showToast('Error en el login: ' + error, 'error');
            }
        } catch (error) {
            console.error('❌ [NAVBAR] Error en login:', error);
            this.showToast('Error de conexión', 'error');
        }
    }

    async handleLogout() {
        try {
            const response = await fetch(`${window.CONTEXT_PATH || ''}/api/logout`, {
                method: 'POST'
            });

            if (response.ok) {
                this.currentUser = null;
                sessionStorage.removeItem('currentUser');
                this.updateUserInterface();
                this.showToast('Sesión cerrada', 'info');
            }
        } catch (error) {
            console.error('❌ [NAVBAR] Error en logout:', error);
        }
    }

    updateUserInterface() {
        if (this.currentUser) {
            // Usuario logueado
            this.userInfo.innerHTML = `<i class="bi bi-person-circle"></i> ${this.currentUser.nickname}`;
            this.loginBtn.classList.add('d-none');
            this.logoutBtn.classList.remove('d-none');

            // Actualizar visibilidad del menú
            this.updateMenuVisibility(this.currentUser.tipo);
        } else {
            // Usuario invitado
            this.userInfo.innerHTML = '<i class="bi bi-person-circle"></i> Invitado';
            this.loginBtn.classList.remove('d-none');
            this.logoutBtn.classList.add('d-none');

            // Mostrar solo opciones para invitado
            this.updateMenuVisibility('invitado');
        }
    }

    // =================== VISIBILIDAD DEL MENÚ ===================

    initMenuVisibility() {
        // Inicializar visibilidad basada en el usuario actual
        const userType = this.currentUser ? this.currentUser.tipo : 'invitado';
        this.updateMenuVisibility(userType);
    }

    updateMenuVisibility(userType) {
        const menuItems = document.querySelectorAll('[data-visible-for]');

        menuItems.forEach(item => {
            const visibleFor = item.getAttribute('data-visible-for');
            const allowedTypes = visibleFor.split(',');

            if (allowedTypes.includes(userType)) {
                item.style.display = '';
            } else {
                item.style.display = 'none';
            }
        });
    }

    // =================== UTILIDADES ===================

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    showToast(message, type = 'info') {
        // Crear toast dinámicamente
        const toastContainer = document.getElementById('toastContainer') || this.createToastContainer();

        const toastId = 'toast-' + Date.now();
        const toastHtml = `
            <div id="${toastId}" class="toast align-items-center text-bg-${type} border-0" role="alert">
                <div class="d-flex">
                    <div class="toast-body">
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        `;

        toastContainer.insertAdjacentHTML('beforeend', toastHtml);

        const toastElement = document.getElementById(toastId);
        const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
        toast.show();

        // Remover del DOM cuando se oculte
        toastElement.addEventListener('hidden.bs.toast', () => {
            toastElement.remove();
        });
    }

    createToastContainer() {
        const container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
        return container;
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    window.navbarManager = new NavbarManager();
    console.log('✅ Navbar inicializado');
});

// Exportar para uso global
if (typeof module !== 'undefined' && module.exports) {
    module.exports = NavbarManager;
}

// ========================================
// NAVBAR.JS - BÚSQUEDA Y AUTENTICACIÓN
// ========================================

console.log('✅ Navbar inicializado');

// =================== EXPANSIÓN DE BÚSQUEDA ===================
const searchToggle = document.getElementById('searchToggle');
const searchExpandable = document.getElementById('searchExpandable');
const globalSearch = document.getElementById('globalSearch');
const searchForm = document.getElementById('searchForm');
const searchSuggestions = document.getElementById('searchSuggestions');
const suggestionsList = document.getElementById('suggestionsList');

let isSearchExpanded = false;

// Expandir/contraer búsqueda
if (searchToggle) {
    searchToggle.addEventListener('click', function() {
        isSearchExpanded = !isSearchExpanded;

        if (isSearchExpanded) {
            searchExpandable.classList.add('active');
            setTimeout(() => globalSearch.focus(), 300);
        } else {
            searchExpandable.classList.remove('active');
            searchSuggestions.style.display = 'none';
        }
    });
}

// =================== BÚSQUEDA CON SUGERENCIAS ===================
let timeoutId;

if (globalSearch) {
    globalSearch.addEventListener('input', function() {
        const query = this.value.trim();

        // Limpiar timeout anterior
        clearTimeout(timeoutId);

        if (query.length < 2) {
            searchSuggestions.style.display = 'none';
            return;
        }

        // Esperar 300ms después de que el usuario deje de escribir
        timeoutId = setTimeout(() => {
            console.log('🔍 Buscando sugerencias para:', query);
            fetchSuggestions(query);
        }, 300);
    });

    // Cerrar sugerencias al hacer clic fuera
    document.addEventListener('click', function(e) {
        if (!searchExpandable.contains(e.target)) {
            searchSuggestions.style.display = 'none';
        }
    });
}

// 🔥 IMPORTANTE: NO interceptar el submit del formulario
// Dejar que el formulario se envíe normalmente con el método GET
if (searchForm) {
    searchForm.addEventListener('submit', function(e) {
        const query = globalSearch.value.trim();

        console.log('📤 Enviando búsqueda:', query);

        // Si el query está vacío, prevenir el submit
        if (!query) {
            e.preventDefault();
            alert('Por favor ingresa un término de búsqueda');
            return false;
        }

        // Si hay query, dejar que el formulario se envíe normalmente
        // El navegador automáticamente construirá la URL: ResultadosBusqueda?q=valor
        console.log('✅ Formulario enviándose normalmente');

        // NO hacer e.preventDefault() aquí
        // NO usar fetch() aquí
        // Dejar que el navegador haga el submit tradicional
    });
}

// Función para obtener sugerencias
function fetchSuggestions(query) {
    const contextPath = document.querySelector('nav').dataset.contextPath || '';

    fetch(`${contextPath}/ResultadosBusqueda?action=suggestions&q=${encodeURIComponent(query)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor');
            }
            return response.json();
        })
        .then(suggestions => {
            console.log('📊 Sugerencias recibidas:', suggestions.length);
            displaySuggestions(suggestions);
        })
        .catch(error => {
            console.error('❌ Error obteniendo sugerencias:', error);
            searchSuggestions.style.display = 'none';
        });
}

// Función para mostrar sugerencias
function displaySuggestions(suggestions) {
    if (!suggestions || suggestions.length === 0) {
        searchSuggestions.style.display = 'none';
        return;
    }

    suggestionsList.innerHTML = '';

    suggestions.forEach(item => {
        const suggestionItem = document.createElement('div');
        suggestionItem.className = 'suggestion-item';

        const icon = item.tipo === 'ruta'
            ? '<i class="bi bi-geo-alt text-primary me-2"></i>'
            : '<i class="bi bi-box text-success me-2"></i>';

        const badge = item.tipo === 'ruta'
            ? '<span class="badge badge-ruta ms-2">Ruta</span>'
            : '<span class="badge badge-paquete ms-2">Paquete</span>';

        suggestionItem.innerHTML = `
            <div class="d-flex align-items-center">
                ${icon}
                <div class="flex-grow-1">
                    <div class="suggestion-title">${item.nombre} ${badge}</div>
                    <div class="suggestion-desc">${item.descripcion || ''}</div>
                </div>
            </div>
        `;

        suggestionItem.addEventListener('click', function() {
            globalSearch.value = item.nombre;
            searchSuggestions.style.display = 'none';
            searchForm.submit(); // Enviar el formulario al hacer clic en sugerencia
        });

        suggestionsList.appendChild(suggestionItem);
    });

    searchSuggestions.style.display = 'block';
}

// =================== VERIFICACIÓN DE SESIÓN ===================
async function verificarSesion() {
    try {
        const contextPath = window.location.pathname.split('/')[1];
        const response = await fetch(`/${contextPath}/api/session`);

        if (response.ok) {
            const data = await response.json();

            if (data.logged) {
                actualizarUIUsuarioLogueado(data);
            } else {
                actualizarUIInvitado();
            }
        } else {
            actualizarUIInvitado();
        }
    } catch (error) {
        console.log('⚠️ No hay sesión activa o error:', error.message);
        actualizarUIInvitado();
    }
}

function actualizarUIUsuarioLogueado(data) {
    const userInfo = document.getElementById('userInfo');
    const loginBtn = document.getElementById('loginBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    if (userInfo) {
        const tipoUsuario = data.tipo === 'CLIENTE' ? 'Cliente' : 'Aerolínea';
        userInfo.innerHTML = `<i class="bi bi-person-circle"></i> ${data.nickname} (${tipoUsuario})`;
    }

    if (loginBtn) loginBtn.classList.add('d-none');
    if (logoutBtn) logoutBtn.classList.remove('d-none');

    // Mostrar/ocultar elementos según el tipo de usuario
    const menuItems = document.querySelectorAll('[data-visible-for]');
    menuItems.forEach(item => {
        const visibleFor = item.getAttribute('data-visible-for').split(',');

        if (data.tipo === 'CLIENTE' && visibleFor.includes('cliente')) {
            item.style.display = '';
        } else if (data.tipo === 'AEROLINEA' && visibleFor.includes('aerolinea')) {
            item.style.display = '';
        } else if (visibleFor.includes('invitado')) {
            item.style.display = 'none';
        } else {
            item.style.display = 'none';
        }
    });
}

function actualizarUIInvitado() {
    const userInfo = document.getElementById('userInfo');
    const loginBtn = document.getElementById('loginBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    if (userInfo) {
        userInfo.innerHTML = '<i class="bi bi-person-circle"></i> Invitado';
    }

    if (loginBtn) loginBtn.classList.remove('d-none');
    if (logoutBtn) logoutBtn.classList.add('d-none');

    // Mostrar solo elementos para invitados
    const menuItems = document.querySelectorAll('[data-visible-for]');
    menuItems.forEach(item => {
        const visibleFor = item.getAttribute('data-visible-for').split(',');

        if (visibleFor.includes('invitado')) {
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    });
}

// =================== LOGIN ===================
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;

        try {
            const contextPath = window.location.pathname.split('/')[1];
            const response = await fetch(`/${contextPath}/api/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password })
            });

            if (response.ok) {
                const data = await response.json();
                alert('Inicio de sesión exitoso');

                // Cerrar modal
                const modal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
                modal.hide();

                // Actualizar UI
                actualizarUIUsuarioLogueado(data);

                // Recargar página para actualizar menús
                location.reload();
            } else {
                const error = await response.text();
                alert('Error: ' + error);
            }
        } catch (error) {
            console.error('Error en login:', error);
            alert('Error al iniciar sesión');
        }
    });
}

// =================== LOGOUT ===================
const logoutBtn = document.getElementById('logoutBtn');
if (logoutBtn) {
    logoutBtn.addEventListener('click', async function() {
        try {
            const contextPath = window.location.pathname.split('/')[1];
            const response = await fetch(`/${contextPath}/api/logout`, {
                method: 'POST'
            });

            if (response.ok) {
                alert('Sesión cerrada exitosamente');
                actualizarUIInvitado();
                location.reload();
            }
        } catch (error) {
            console.error('Error en logout:', error);
            alert('Error al cerrar sesión');
        }
    });
}

// =================== INICIALIZACIÓN ===================
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Inicializando navbar...');
    verificarSesion();
});
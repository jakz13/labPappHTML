document.addEventListener('DOMContentLoaded', function() {
    console.log('Session manager cargado');
    // Calcular la base API igual que en las JSP: quitar el último segmento del path
    // Si la JSP ya inyectó window.SESSION_API_BASE, respetarla (evita solicitudes a /api sin context path)
    if (!window.SESSION_API_BASE) {
        // Incluir el origin para evitar problemas con rutas relativas que resuelven mal
        // Ejemplo resultante: https://localhost:8080/miApp
        try {
            const origin = window.location.origin || (window.location.protocol + '//' + window.location.host);
            const contextPath = window.location.pathname.replace(/\/[^/]*$/, '');
            window.SESSION_API_BASE = origin + contextPath;
            console.log('SESSION_API_BASE calculado por session-manager:', window.SESSION_API_BASE);
        } catch (e) {
            // fallback conservador: usar sólo el path
            window.SESSION_API_BASE = window.location.pathname.replace(/\/[^/]*$/, '');
            console.log('SESSION_API_BASE fallback por session-manager:', window.SESSION_API_BASE);
        }
    } else {
        console.log('SESSION_API_BASE ya inyectado por la JSP, se usa:', window.SESSION_API_BASE);
    }
    checkSession();

    // Login para modal
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const nickname = document.getElementById('loginEmail').value;
            const password = document.getElementById('loginPassword').value;
            console.log('Intentando login:', nickname);
            performLogin(nickname, password);
        });
    }

    // Logout
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            console.log('Cerrando sesión...');
            fetch(window.SESSION_API_BASE + '/api/logout', {
                method: 'POST',
                credentials: 'include'
            })
                .then(() => {
                    // Limpiar localStorage session fallback
                    try { localStorage.removeItem('session_nickname'); localStorage.removeItem('session_tipo'); localStorage.removeItem('session_timestamp'); } catch (e) {}
                    window.location.reload();
                })
                .catch(error => {
                    console.error('Error en logout:', error);
                    try { localStorage.removeItem('session_nickname'); localStorage.removeItem('session_tipo'); localStorage.removeItem('session_timestamp'); } catch (e) {}
                    window.location.reload();
                });
        });
    }
});

function performLogin(nickname, password) {
    console.log('Ejecutando login para:', nickname);

    fetch(window.SESSION_API_BASE + '/api/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `nickname=${encodeURIComponent(nickname)}&password=${encodeURIComponent(password)}`,
        credentials: 'include'
    })
        .then(res => {
            console.log('Respuesta del servidor:', res.status);
            if (res.status === 401) {
                return { success: false, error: 'Usuario no encontrado' };
            }
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            console.log('Datos recibidos:', data);
            if (data.success) {
                console.log('Login exitoso');
                // Guardar info en localStorage como respaldo
                try {
                    if (data.nickname) localStorage.setItem('session_nickname', data.nickname);
                    if (data.tipo) localStorage.setItem('session_tipo', data.tipo);
                    localStorage.setItem('session_timestamp', String(Date.now()));
                } catch (e) { console.warn('No se pudo guardar session en localStorage:', e); }

                // Cerrar modal
                const modalEl = document.getElementById('loginModal');
                const modal = bootstrap.Modal.getInstance(modalEl) || bootstrap.Modal.getOrCreateInstance(modalEl);
                if (modal) {
                    modal.hide();
                }
                // Actualizar la UI del navbar inmediatamente
                try {
                    updateUI({ authenticated: true, nickname: data.nickname, tipo: data.tipo });
                } catch (e) {
                    console.warn('No se pudo actualizar UI tras login:', e);
                }
                // Notificar a otras partes de la página que la sesión cambió
                try {
                    window.dispatchEvent(new Event('sessionUpdated'));
                } catch (e) {
                    console.warn('No se pudo dispatch sessionUpdated', e);
                }
            } else {
                console.error('Error en login:', data.error);
                alert('Error: ' + (data.error || 'Credenciales incorrectas'));
            }
        })
        .catch(error => {
            console.error('Error en fetch:', error);
            alert('Error de conexión con el servidor');
        });
}

function checkSession() {
    const base = window.SESSION_API_BASE || '';
    const url = base + '/api/check-session';
    console.log('checkSession -> URL:', url);
    fetch(base + '/api/check-session', { credentials: 'include' })
        .then(res => {
            // manejar estados no OK y respuestas inesperadas (HTML 404 de Tomcat, etc.)
            if (!res.ok) throw new Error('HTTP ' + res.status);
            const ct = res.headers.get('content-type') || '';
            if (!ct.includes('application/json')) throw new Error('Respuesta no JSON: ' + ct);
            return res.json();
        })
        .then(data => {
            // exponer la sesión para otros scripts y evitar llamadas redundantes
            try { window.CURRENT_SESSION = data; } catch (e) { /* ignore */ }
            console.log('Estado de sesión:', data);
            updateUI(data);
        })
        .catch(error => {
            console.error('Error verificando sesión:', error);
            // fallback: intentar leer localStorage si existe
            try {
                const nick = localStorage.getItem('session_nickname');
                const tipo = localStorage.getItem('session_tipo');
                if (nick && tipo) {
                    updateUI({ authenticated: true, nickname: nick, tipo: tipo });
                    try { window.CURRENT_SESSION = { authenticated: true, nickname: nick, tipo: tipo }; } catch (e) {}
                    return;
                }
            } catch (e) { /* ignore */ }
            updateUI({ authenticated: false });
        });
}

function updateUI(sessionData) {
    const userInfo = document.getElementById('userInfo');
    const loginBtn = document.getElementById('loginBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    if (sessionData.authenticated) {
        console.log('Usuario autenticado:', sessionData.nickname);
        if (userInfo) {
            userInfo.innerHTML = `<i class="bi bi-person-circle"></i> ${sessionData.nickname}`;
        }
        if (loginBtn) loginBtn.classList.add('d-none');
        if (logoutBtn) logoutBtn.classList.remove('d-none');
    } else {
        console.log('Usuario no autenticado');
        if (userInfo) {
            userInfo.innerHTML = `<i class="bi bi-person-circle"></i> Invitado`;
        }
        if (loginBtn) loginBtn.classList.remove('d-none');
        if (logoutBtn) logoutBtn.classList.add('d-none');
    }

    // Actualizar visibilidad del navbar según el rol/tipo de sesión
    try {
        updateNavByRole(sessionData);
    } catch (e) {
        console.warn('No se pudo actualizar el navbar por rol:', e);
    }
}

// Muestra/oculta elementos del navbar según el tipo de sesión
function updateNavByRole(sessionData) {
    // roles esperados: 'invitado' (no autenticado), 'cliente', 'aerolinea'
    let role = 'invitado';
    if (sessionData && sessionData.authenticated) {
        // intentar obtener tipo desde distintas propiedades posibles; si no existe, no asumir cliente (usar invitado como fallback seguro)
        const raw = (sessionData.tipo || sessionData.role || sessionData.tipoUsuario || '');
        role = raw ? raw.toString().toLowerCase() : 'invitado';
    }

    // Mostrar/ocultar elementos con el atributo data-visible-for
    const elems = document.querySelectorAll('[data-visible-for]');
    elems.forEach(el => {
        const attr = el.getAttribute('data-visible-for') || '';
        const allowed = attr.split(',').map(s => s.trim().toLowerCase()).filter(Boolean);
        if (allowed.length === 0) {
            // si no hay restricción explícita, mostrar por defecto
            el.style.display = '';
            return;
        }
        if (allowed.includes(role)) {
            el.style.display = '';
        } else {
            el.style.display = 'none';
        }
    });

    // Ocultar dropdowns completos si ninguno de sus items está visible
    const dropdowns = document.querySelectorAll('.nav-item.dropdown');
    dropdowns.forEach(dd => {
        const menuItems = dd.querySelectorAll('ul.dropdown-menu > li');
        let anyVisible = false;
        menuItems.forEach(mi => {
            // consideramos visible si no tiene style.display === 'none'
            if (mi.style.display !== 'none') anyVisible = true;
        });
        dd.style.display = anyVisible ? '' : 'none';
    });
}
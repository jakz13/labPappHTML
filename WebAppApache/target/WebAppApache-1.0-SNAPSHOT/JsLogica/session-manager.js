// javascript
document.addEventListener('DOMContentLoaded', function() {
    console.log('Session manager cargado');
    // Calcular la base API igual que en las JSP: quitar el último segmento del path
    window.SESSION_API_BASE = window.location.pathname.replace(/\/[^/]*$/, '');
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
                    window.location.reload();
                })
                .catch(error => {
                    console.error('Error en logout:', error);
                    window.location.reload();
                });
        });
    }
});

// javascript
async function performLogin(nickname, password) {
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
            if (data && data.success) {
                console.log('Login exitoso');
                const modalEl = document.getElementById('loginModal');
                const modal = bootstrap.Modal.getInstance(modalEl) || bootstrap.Modal.getOrCreateInstance(modalEl);
                if (modal) modal.hide();

                try {
                    updateUI({ authenticated: true, nickname: data.nickname, tipo: data.tipo });
                } catch (e) {
                    console.warn('No se pudo actualizar UI tras login:', e);
                }

                try {
                    window.dispatchEvent(new Event('sessionUpdated'));
                } catch (e) {
                    console.warn('No se pudo dispatch sessionUpdated', e);
                }

                // Recargar la página para reflejar estado completo (pequeño retardo para asegurar cookie/sesión)
                setTimeout(() => {
                    window.location.reload();
                }, 150);

            } else {
                console.error('Error en login:', data && data.error);
                alert('Error: ' + (data && data.error ? data.error : 'Credenciales incorrectas'));
            }
        })
        .catch(error => {
            console.error('Error en fetch:', error);
            alert('Error de conexión con el servidor');
        });
}


// Función para registro (si existe flujo de registro en la app)
// Ajustar endpoint/formatos según tu backend.
function performRegister(payloadObj) {
    const body = Object.keys(payloadObj).map(k => `${encodeURIComponent(k)}=${encodeURIComponent(payloadObj[k])}`).join('&');
    fetch(window.SESSION_API_BASE + '/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body,
        credentials: 'include'
    })
        .then(res => {
            if (!res.ok) throw new Error('Registro fallido: ' + res.status);
            return res.json();
        })
        .then(data => {
            if (data && data.success) {
                console.log('Registro exitoso');
                // Si el backend crea sesión automática, avisar
                try {
                    window.dispatchEvent(new Event('sessionUpdated'));
                } catch (e) {
                    console.warn('No se pudo dispatch sessionUpdated tras registro', e);
                }
                return;
            }
            // Si no crea sesión, opcionalmente logear automáticamente:
            // performLogin(payloadObj.nickname, payloadObj.password);
            alert('Registro: ' + (data && data.error ? data.error : 'No se pudo completar'));
        })
        .catch(err => {
            console.error('Error en registro:', err);
            alert('Error de conexión al registrar');
        });
}

function checkSession() {
    const base = window.SESSION_API_BASE || '';
    fetch(base + '/api/check-session', {
        credentials: 'include'
    })
        .then(res => res.json())
        .then(data => {
            console.log('Estado de sesión:', data);
            updateUI(data);
        })
        .catch(error => {
            console.error('Error verificando sesión:', error);
            updateUI({ authenticated: false });
        });
}

function updateUI(sessionData) {
    const userInfo = document.getElementById('userInfo');
    const loginBtn = document.getElementById('loginBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    if (sessionData && sessionData.authenticated) {
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
}

document.addEventListener('DOMContentLoaded', function() {
    console.log('Session manager cargado');
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
            fetch('api/logout', {
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

function performLogin(nickname, password) {
    console.log('Ejecutando login para:', nickname);

    fetch('api/login', {
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
                // Cerrar modal
                const modal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
                if (modal) {
                    modal.hide();
                }
                // Recargar para mostrar cambios en navbar
                setTimeout(() => {
                    window.location.reload();
                }, 100);
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
    fetch('api/check-session', {
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
}
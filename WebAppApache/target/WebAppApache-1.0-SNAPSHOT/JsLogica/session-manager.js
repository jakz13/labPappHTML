document.addEventListener('DOMContentLoaded', function() {
    // Verificar sesión al cargar
    checkSession();

    // Login por AJAX
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const nickname = document.getElementById('loginEmail').value;
            const password = document.getElementById('loginPassword').value;

            fetch('api/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `nickname=${encodeURIComponent(nickname)}&password=${encodeURIComponent(password)}`,
                credentials: 'include'
            })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        // Cerrar modal
                        const modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('loginModal'));
                        modal.hide();
                        checkSession();
                    } else {
                        alert(data.error || 'Credenciales incorrectas');
                    }
                })
                .catch(() => alert('Error de red o del servidor'));
        });
    }

    // Logout
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            fetch('api/logout', { method: 'POST', credentials: 'include' })
                .then(() => location.reload());
        });
    }
});

function checkSession() {
    fetch('api/check-session', { credentials: 'include' })
        .then(res => res.json())
        .then(data => {
            const userInfo = document.getElementById('userInfo');
            const loginBtn = document.getElementById('loginBtn');
            const logoutBtn = document.getElementById('logoutBtn');
            if (data.authenticated) {
                userInfo.innerHTML = `<i class="bi bi-person-circle"></i> ${data.nickname || 'Usuario'}`;
                loginBtn.classList.add('d-none');
                logoutBtn.classList.remove('d-none');
            } else {
                userInfo.innerHTML = `<i class="bi bi-person-circle"></i> Invitado`;
                loginBtn.classList.remove('d-none');
                logoutBtn.classList.add('d-none');
            }
        });
}

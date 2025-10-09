// Gestión de sesión
function checkSession() {
    const user = localStorage.getItem('currentUser');
    if (user) {
        const userData = JSON.parse(user);
        document.getElementById('userInfo').innerHTML = `<i class="bi bi-person-check-fill"></i> ${userData.nickname}`;
        document.getElementById('loginBtn').classList.add('d-none');
        document.getElementById('logoutBtn').classList.remove('d-none');
    } else {
        document.getElementById('userInfo').innerHTML = '<i class="bi bi-person-circle"></i> Invitado';
        document.getElementById('loginBtn').classList.remove('d-none');
        document.getElementById('logoutBtn').classList.add('d-none');
    }
}

// Inicializar verificación de sesión
document.addEventListener('DOMContentLoaded', checkSession);

// Manejo del formulario de inicio de sesión
if (document.getElementById('loginForm')) {
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        e.preventDefault();

        // Simulación de inicio de sesión exitoso
        const userData = {
            nickname: document.getElementById('loginEmail').value,
            nombre: "Usuario Ejemplo"
        };

        localStorage.setItem('currentUser', JSON.stringify(userData));
        checkSession();

        // Cerrar modal
        const loginModal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
        loginModal.hide();

        alert('Inicio de sesión exitoso');
    });
}

// Cerrar sesión
if (document.getElementById('logoutBtn')) {
    document.getElementById('logoutBtn').addEventListener('click', function() {
        localStorage.removeItem('currentUser');
        checkSession();
        alert('Sesión cerrada correctamente');
    });
}

// Asegurar que todos los enlaces funcionen
document.addEventListener('DOMContentLoaded', function() {
    const allLinks = document.querySelectorAll('a');
    allLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (this.getAttribute('href') === '#') {
                e.preventDefault();
            }
        });
    });
});
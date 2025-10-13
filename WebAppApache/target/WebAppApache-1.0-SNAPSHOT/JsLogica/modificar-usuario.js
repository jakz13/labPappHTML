// Variables globales
let imagenActual = "https://via.placeholder.com/150/3498db/ffffff?text=MG";

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    // Configurar validación de contraseña
    configurarValidacionPassword();

    // Configurar preview de imagen
    configurarPreviewImagen();

    // Configurar validación de formulario
    configurarValidacionFormulario();
});

function configurarValidacionPassword() {
    const passwordInput = document.getElementById('password');
    const confirmInput = document.getElementById('confirmar');
    const strengthBar = document.getElementById('passwordStrength');

    passwordInput.addEventListener('input', function() {
        const password = this.value;

        // Calcular fortaleza
        let strength = 0;
        if (password.length >= 6) strength += 25;
        if (password.match(/[a-z]/) && password.match(/[A-Z]/)) strength += 25;
        if (password.match(/\d/)) strength += 25;
        if (password.match(/[^a-zA-Z\d]/)) strength += 25;

        // Actualizar barra de fortaleza
        strengthBar.style.width = strength + '%';
        strengthBar.style.backgroundColor =
            strength < 50 ? '#dc3545' :
                strength < 75 ? '#ffc107' : '#28a745';

        // Validar confirmación
        validarConfirmacionPassword();
    });

    confirmInput.addEventListener('input', validarConfirmacionPassword);

    function validarConfirmacionPassword() {
        const password = passwordInput.value;
        const confirmacion = confirmInput.value;

        if (confirmacion && password !== confirmacion) {
            confirmInput.classList.add('is-invalid');
        } else {
            confirmInput.classList.remove('is-invalid');
        }
    }
}

function configurarPreviewImagen() {
    const imagenInput = document.getElementById('imagenPerfil');
    const preview = document.getElementById('imagenPreview');

    imagenInput.addEventListener('change', function() {
        const file = this.files[0];
        if (file) {
            // Validar tipo y tamaño
            if (!file.type.startsWith('image/')) {
                alert('Por favor seleccione una imagen válida.');
                return;
            }

            if (file.size > 2 * 1024 * 1024) {
                alert('La imagen debe ser menor a 2MB.');
                return;
            }

            const reader = new FileReader();
            reader.onload = function(e) {
                preview.src = e.target.result;
                imagenActual = e.target.result;
            }
            reader.readAsDataURL(file);
        }
    });
}

function eliminarImagen() {
    const preview = document.getElementById('imagenPreview');
    preview.src = 'https://via.placeholder.com/150/6c757d/ffffff?text=SIN+IMAGEN';
    imagenActual = preview.src;
    document.getElementById('imagenPerfil').value = '';
}

function configurarValidacionFormulario() {
    const formulario = document.getElementById('formModificarUsuario');

    formulario.addEventListener('submit', function(event) {
        event.preventDefault();

        // Validar contraseñas
        const password = document.getElementById('password').value;
        const confirmacion = document.getElementById('confirmar').value;

        if (password && password !== confirmacion) {
            document.getElementById('passwordError').style.display = 'block';
            document.getElementById('confirmar').classList.add('is-invalid');
            return;
        }

        if (this.checkValidity()) {
            // Simular guardado exitoso
            const confirmacionModal = new bootstrap.Modal(document.getElementById('confirmacionModal'));
            confirmacionModal.show();

            // Aquí iría la lógica para enviar al servidor
            console.log('Datos a guardar:', {
                nombre: document.getElementById('nombre').value,
                apellido: document.getElementById('apellido').value,
                fechaNacimiento: document.getElementById('fechaNacimiento').value,
                nacionalidad: document.getElementById('nacionalidad').value,
                tipoDocumento: document.getElementById('tipoDoc').value,
                numeroDocumento: document.getElementById('numDoc').value,
                password: password || '*** NO CAMBIADA ***',
                imagen: imagenActual !== 'https://via.placeholder.com/150/3498db/ffffff?text=MG' ? 'NUEVA_IMAGEN' : 'MISMA_IMAGEN'
            });
        } else {
            event.stopPropagation();
        }

        this.classList.add('was-validated');
    });

    // Validación en tiempo real para campos requeridos
    const camposRequeridos = formulario.querySelectorAll('[required]');
    camposRequeridos.forEach(campo => {
        campo.addEventListener('input', function() {
            if (this.value.trim()) {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
            } else {
                this.classList.remove('is-valid');
                this.classList.add('is-invalid');
            }
        });
    });
}

// Cambiar entre tipos de usuario (aunque en la práctica no se debería poder cambiar)
document.getElementById('tipoUsuario')?.addEventListener('change', function() {
    const esCliente = this.value === 'cliente';
    document.getElementById('clienteFields').style.display = esCliente ? 'block' : 'none';
    document.getElementById('aerolineaFields').style.display = esCliente ? 'none' : 'block';
});

// Prevenir que usuarios cambien su tipo (solo para demostración)
const tipoUsuarioSelect = document.getElementById('tipoUsuario');
if (tipoUsuarioSelect) {
    tipoUsuarioSelect.addEventListener('mousedown', function(e) {
        e.preventDefault();
        alert('No puedes cambiar tu tipo de usuario. Contacta al administrador si necesitas modificar esta información.');
        this.blur();
    });
}
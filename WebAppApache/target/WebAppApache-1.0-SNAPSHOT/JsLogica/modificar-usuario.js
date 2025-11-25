// Variables globales
let imagenActual = "https://via.placeholder.com/150/3498db/ffffff?text=MG";
let usuarioActual = null;

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    // Cargar datos del usuario
    cargarDatosUsuario();

    // Configurar validación de contraseña
    configurarValidacionPassword();

    // Configurar preview de imagen
    configurarPreviewImagen();

    // Configurar validación de formulario
    configurarValidacionFormulario();
});

function cargarDatosUsuario() {
    fetch('api/usuario/actual', {
        credentials: 'include'
    })
        .then(res => {
            if (!res.ok) throw new Error('Error al cargar datos del usuario');
            return res.json();
        })
        .then(data => {
            if (data.success) {
                usuarioActual = data;
                llenarFormulario(data);
            } else {
                throw new Error(data.error);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarMensajeError('No se pudieron cargar los datos del usuario: ' + error.message);
        });
}

function llenarFormulario(usuario) {
    // Datos básicos (no editables)
    document.getElementById('nickname').value = usuario.nickname || '';
    document.getElementById('correo').value = usuario.email || '';

    // Actualizar información del header
    const headerInfo = document.querySelector('.alert-custom');
    if (headerInfo) {
        const nombreCompleto = usuario.nombre + (usuario.apellido ? ' ' + usuario.apellido : '');
        headerInfo.querySelector('h6').textContent = nombreCompleto;
        headerInfo.querySelector('p').textContent =
            (usuario.tipo === 'cliente' ? 'Cliente' : 'Aerolínea') +
            ' | Registrado: ' + (usuario.fechaRegistro ? formatFecha(usuario.fechaRegistro) : 'Fecha no disponible');
    }

    // Mostrar campos según el tipo de usuario
    if (usuario.tipo === 'cliente') {
        document.getElementById('clienteFields').style.display = 'block';
        document.getElementById('aerolineaFields').style.display = 'none';

        // Llenar campos de cliente
        document.getElementById('nombre').value = usuario.nombre || '';
        document.getElementById('apellido').value = usuario.apellido || '';
        document.getElementById('fechaNacimiento').value = usuario.fechaNacimiento || '';
        document.getElementById('nacionalidad').value = usuario.nacionalidad || '';
        document.getElementById('tipoDoc').value = usuario.tipoDocumento || '';
        document.getElementById('numDoc').value = usuario.numeroDocumento || '';

    } else if (usuario.tipo === 'aerolinea') {
        document.getElementById('clienteFields').style.display = 'none';
        document.getElementById('aerolineaFields').style.display = 'block';

        // Llenar campos de aerolínea
        document.getElementById('nombre').value = usuario.nombre || '';
        document.getElementById('descripcion').value = usuario.descripcion || '';
        document.getElementById('sitioWeb').value = usuario.sitioWeb || '';
    }
}

function formatFecha(fechaStr) {
    if (!fechaStr) return '';
    const fecha = new Date(fechaStr);
    return fecha.toLocaleDateString('es-ES');
}

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
        if (strengthBar) {
            strengthBar.style.width = strength + '%';
            strengthBar.style.backgroundColor =
                strength < 50 ? '#dc3545' :
                    strength < 75 ? '#ffc107' : '#28a745';
        }

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
            guardarCambios();
        } else {
            event.stopPropagation();
            this.classList.add('was-validated');
        }
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

function guardarCambios() {
    const datos = {
        nombre: document.getElementById('nombre').value
    };

    // Agregar campos según el tipo de usuario
    if (usuarioActual.tipo === 'cliente') {
        datos.apellido = document.getElementById('apellido').value;
        datos.fechaNacimiento = document.getElementById('fechaNacimiento').value;
        datos.nacionalidad = document.getElementById('nacionalidad').value;
        datos.tipoDocumento = document.getElementById('tipoDoc').value;
        datos.numeroDocumento = document.getElementById('numDoc').value;
    } else if (usuarioActual.tipo === 'aerolinea') {
        datos.descripcion = document.getElementById('descripcion').value;
        datos.sitioWeb = document.getElementById('sitioWeb').value;
    }

    // Agregar password si se cambió
    const password = document.getElementById('password').value;
    if (password) {
        datos.password = password;
    }

    console.log('Enviando datos:', datos);

    fetch('api/usuario/actualizar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(datos),
        credentials: 'include'
    })
        .then(res => {
            if (!res.ok) throw new Error('Error al actualizar datos');
            return res.json();
        })
        .then(data => {
            if (data.success) {
                const confirmacionModal = new bootstrap.Modal(document.getElementById('confirmacionModal'));
                confirmacionModal.show();
            } else {
                throw new Error(data.error);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarMensajeError('No se pudieron guardar los cambios: ' + error.message);
        });
}

function mostrarMensajeError(mensaje) {
    const toastHTML = `
        <div class="toast align-items-center text-bg-danger border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>${mensaje}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    const toastContainer = document.getElementById('toastContainer') || (() => {
        const container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '1060';
        document.body.appendChild(container);
        return container;
    })();

    toastContainer.innerHTML = toastHTML;
    const toastElement = toastContainer.querySelector('.toast');
    const toast = new bootstrap.Toast(toastElement);
    toast.show();
}

// Prevenir que usuarios cambien su tipo (solo para demostración)
const tipoUsuarioSelect = document.getElementById('tipoUsuario');
if (tipoUsuarioSelect) {
    tipoUsuarioSelect.addEventListener('mousedown', function(e) {
        e.preventDefault();
        alert('No puedes cambiar tu tipo de usuario. Contacta al administrador si necesitas modificar esta información.');
        this.blur();
    });
}
// Navegación entre pasos
// Navegación entre pasos
function nextStep(step) {
    if (step === 2 && !validateStep1()) return;
    if (step === 3 && !validateStep2()) return;

    // Oculta la sección anterior
    const prevSection = document.getElementById('section' + (step-1));
    prevSection.classList.add('d-none');
    prevSection.classList.remove('active');

    // Muestra la sección actual
    const currSection = document.getElementById('section' + step);
    currSection.classList.remove('d-none');
    currSection.classList.add('active');

    // Actualiza el step indicator
    document.getElementById('step' + (step-1)).classList.remove('active');
    document.getElementById('step' + step).classList.add('active');
}

function prevStep(step) {
    // Oculta la sección siguiente
    const nextSection = document.getElementById('section' + (step+1));
    nextSection.classList.add('d-none');
    nextSection.classList.remove('active');

    // Muestra la sección actual
    const currSection = document.getElementById('section' + step);
    currSection.classList.remove('d-none');
    currSection.classList.add('active');

    // Actualiza el step indicator
    document.getElementById('step' + (step+1)).classList.remove('active');
    document.getElementById('step' + step).classList.add('active');
}

// Selección de tipo de usuario
function selectUserType(tipo) {
    document.getElementById('tipoUsuario').value = tipo;

    // Resaltar la tarjeta seleccionada
    document.querySelectorAll('.user-type-card').forEach(card => {
        card.classList.remove('selected');
    });
    document.getElementById(tipo + 'Card').classList.add('selected');

    // Mostrar campos específicos según el tipo
    if (tipo === 'cliente') {
        document.getElementById('camposCliente').classList.remove('d-none');
        document.getElementById('camposAerolinea').classList.add('d-none');
    } else {
        document.getElementById('camposCliente').classList.add('d-none');
        document.getElementById('camposAerolinea').classList.remove('d-none');
    }

    // Habilitar el botón siguiente
    document.getElementById('nextStep2').disabled = false;

    // Remover validación de error
    document.getElementById('tipoUsuario').classList.remove('is-invalid');

    // Avanzar automáticamente al siguiente paso después de un breve retraso
    setTimeout(() => {
        nextStep(3);
    }, 500);
}

// Validación de contraseña
document.getElementById('password').addEventListener('input', function() {
    const strengthBar = document.getElementById('passwordStrength');
    const password = this.value;

    let strength = 0;
    if (password.length >= 6) strength += 25;
    if (password.match(/[a-z]/) && password.match(/[A-Z]/)) strength += 25;
    if (password.match(/\d/)) strength += 25;
    if (password.match(/[^a-zA-Z\d]/)) strength += 25;

    strengthBar.style.width = strength + '%';
    strengthBar.style.backgroundColor =
        strength < 50 ? '#dc3545' :
            strength < 75 ? '#ffc107' : '#28a745';
});

// Validar confirmación de contraseña
document.getElementById('confirmPassword').addEventListener('input', function() {
    const password = document.getElementById('password').value;
    const confirmPassword = this.value;

    if (password !== confirmPassword && confirmPassword !== '') {
        this.classList.add('is-invalid');
    } else {
        this.classList.remove('is-invalid');
    }
});

// Validaciones de pasos
function validateStep1() {
    const form = document.getElementById('formRegistroUsuario');
    const requiredFields = ['nickname', 'nombre', 'email', 'password'];
    let isValid = true;

    requiredFields.forEach(field => {
        const element = document.getElementById(field);
        if (!element.value) {
            element.classList.add('is-invalid');
            isValid = false;
        }
    });

    // Validar confirmación de contraseña
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    if (password !== confirmPassword) {
        document.getElementById('confirmPassword').classList.add('is-invalid');
        isValid = false;
    }

    return isValid;
}

function validateStep2() {
    const tipoUsuario = document.getElementById('tipoUsuario').value;
    if (!tipoUsuario) {
        document.getElementById('tipoUsuario').classList.add('is-invalid');
        return false;
    }
    return true;
}

// Envío del formulario
document.getElementById('formRegistroUsuario').addEventListener('submit', function(event) {
    event.preventDefault();

    if (this.checkValidity() && validateStep3()) {
        // Simular envío exitoso
        const successModal = new bootstrap.Modal(document.getElementById('successModal'));
        successModal.show();

        // Aquí iría la lógica real para enviar al servidor
        console.log('Datos del usuario:', {
            nickname: document.getElementById('nickname').value,
            nombre: document.getElementById('nombre').value,
            email: document.getElementById('email').value,
            tipo: document.getElementById('tipoUsuario').value,
            // ... otros campos
        });
    } else {
        event.stopPropagation();
    }

    this.classList.add('was-validated');
});

function validateStep3() {
    const tipoUsuario = document.getElementById('tipoUsuario').value;
    let isValid = true;

    if (tipoUsuario === 'cliente') {
        const camposCliente = ['apellido', 'fechaNacimiento', 'nacionalidad', 'tipoDocumento', 'numeroDocumento'];
        camposCliente.forEach(field => {
            const element = document.getElementById(field);
            if (!element.value) {
                element.classList.add('is-invalid');
                isValid = false;
            }
        });
    } else {
        const descripcion = document.getElementById('descripcionAerolinea');
        if (!descripcion.value) {
            descripcion.classList.add('is-invalid');
            isValid = false;
        }
    }

    return isValid;
}

// Validar edad mínima (18 años)
document.getElementById('fechaNacimiento').max = new Date(new Date().setFullYear(new Date().getFullYear() - 18)).toISOString().split('T')[0];
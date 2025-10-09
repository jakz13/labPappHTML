// AL INICIO DEL ARCHIVO - Agrega esta línea
const BASE_URL = '/labPappHTML';

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
        document.getElementById('confirmPassword').nextElementSibling.style.display = 'block';
    } else {
        this.classList.remove('is-invalid');
        document.getElementById('confirmPassword').nextElementSibling.style.display = 'none';
    }
});

// Validaciones de pasos
function validateStep1() {
    const form = document.getElementById('formRegistroUsuario');
    const requiredFields = ['nickname', 'nombre', 'email', 'password'];
    let isValid = true;

    requiredFields.forEach(field => {
        const element = document.getElementById(field);
        if (!element.value.trim()) {
            element.classList.add('is-invalid');
            isValid = false;
        } else {
            element.classList.remove('is-invalid');
        }
    });

    // Validar email
    const email = document.getElementById('email').value;
    if (email && !validateEmail(email)) {
        document.getElementById('email').classList.add('is-invalid');
        isValid = false;
    }

    // Validar confirmación de contraseña
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    if (password !== confirmPassword) {
        document.getElementById('confirmPassword').classList.add('is-invalid');
        isValid = false;
    }

    // Validar longitud de contraseña
    if (password && password.length < 6) {
        document.getElementById('password').classList.add('is-invalid');
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

function validateStep3() {
    const tipoUsuario = document.getElementById('tipoUsuario').value;
    let isValid = true;

    // Validar términos y condiciones
    const terminos = document.getElementById('terminosCondiciones');
    if (!terminos.checked) {
        terminos.classList.add('is-invalid');
        isValid = false;
    } else {
        terminos.classList.remove('is-invalid');
    }

    if (tipoUsuario === 'cliente') {
        const camposCliente = ['apellido', 'fechaNacimiento', 'nacionalidad', 'tipoDocumento', 'numeroDocumento'];
        camposCliente.forEach(field => {
            const element = document.getElementById(field);
            if (!element.value.trim()) {
                element.classList.add('is-invalid');
                isValid = false;
            } else {
                element.classList.remove('is-invalid');
            }
        });
    } else {
        const descripcion = document.getElementById('descripcionAerolinea');
        if (!descripcion.value.trim()) {
            descripcion.classList.add('is-invalid');
            isValid = false;
        } else {
            descripcion.classList.remove('is-invalid');
        }
    }

    return isValid;
}

// Validación de email
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

// Verificar email único - CORREGIDO
document.getElementById('email').addEventListener('blur', function() {
    const email = this.value;
    if (email && validateEmail(email)) {
        verificarEmailUnico(email);
    }
});

function verificarEmailUnico(email) {
    fetch(`${BASE_URL}/verificarEmail?email=${encodeURIComponent(email)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor');
            }
            return response.json();
        })
        .then(data => {
            if (!data.disponible) {
                document.getElementById('email').classList.add('is-invalid');
                // Crear o actualizar mensaje de feedback
                let feedback = document.getElementById('emailFeedback');
                if (!feedback) {
                    feedback = document.createElement('div');
                    feedback.id = 'emailFeedback';
                    feedback.className = 'invalid-feedback';
                    document.getElementById('email').parentNode.appendChild(feedback);
                }
                feedback.textContent = 'Este email ya está registrado';
                feedback.style.display = 'block';
            }
        })
        .catch(error => {
            console.error('Error verificando email:', error);
        });
}

// Envío del formulario - CORREGIDO
document.getElementById('formRegistroUsuario').addEventListener('submit', function(event) {
    event.preventDefault();

    if (this.checkValidity() && validateStep3()) {
        enviarRegistroAlServidor();
    } else {
        event.stopPropagation();
    }

    this.classList.add('was-validated');
});

function enviarRegistroAlServidor() {
    const formData = new FormData();
    const tipoUsuario = document.getElementById('tipoUsuario').value;

    // Datos comunes
    formData.append('nickname', document.getElementById('nickname').value);
    formData.append('nombre', document.getElementById('nombre').value);
    formData.append('email', document.getElementById('email').value);
    formData.append('password', document.getElementById('password').value);
    formData.append('tipoUsuario', tipoUsuario);

    // Imagen (si se seleccionó)
    const imagenInput = document.getElementById('imagenUsuario');
    if (imagenInput.files.length > 0) {
        formData.append('imagen', imagenInput.files[0]);
    }

    // Datos específicos según tipo de usuario
    if (tipoUsuario === 'cliente') {
        formData.append('apellido', document.getElementById('apellido').value);
        formData.append('fechaNacimiento', document.getElementById('fechaNacimiento').value);
        formData.append('nacionalidad', document.getElementById('nacionalidad').value);
        formData.append('tipoDocumento', document.getElementById('tipoDocumento').value);
        formData.append('numeroDocumento', document.getElementById('numeroDocumento').value);
    } else {
        formData.append('descripcionAerolinea', document.getElementById('descripcionAerolinea').value);
        const sitioWeb = document.getElementById('sitioWeb').value;
        if (sitioWeb) {
            formData.append('sitioWeb', sitioWeb);
        }
    }

    // Mostrar loading
    const submitBtn = document.querySelector('#formRegistroUsuario button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status"></span> Registrando...';
    submitBtn.disabled = true;

    // DEBUG: Mostrar datos que se envían
    console.log('Enviando datos a:', `${BASE_URL}/altaUsuario`);
    console.log('Datos:', Object.fromEntries(formData));

    // Enviar al servidor - CORREGIDO
    fetch(`${BASE_URL}/altaUsuario`, {
        method: 'POST',
        body: formData
    })
        .then(response => {
            console.log('Respuesta recibida - Status:', response.status);
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            console.log('Datos recibidos:', data);
            if (data.success) {
                // Éxito
                const successModal = new bootstrap.Modal(document.getElementById('successModal'));
                successModal.show();

                // Redirigir después de éxito
                setTimeout(() => {
                    window.location.href = 'PaginaPrincipal.jsp';
                }, 2000);
            } else {
                // Error
                mostrarError(data.error || 'Error desconocido al registrar usuario');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error de conexión con el servidor: ' + error.message);
        })
        .finally(() => {
            // Restaurar botón
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        });
}

function mostrarError(mensaje) {
    // Limpiar errores anteriores
    const erroresAnteriores = document.querySelectorAll('.alert-danger');
    erroresAnteriores.forEach(error => error.remove());

    // Crear elemento de error
    const form = document.getElementById('formRegistroUsuario');
    const errorElement = document.createElement('div');
    errorElement.className = 'alert alert-danger alert-dismissible fade show mt-3';
    errorElement.innerHTML = `
        <strong>Error:</strong> ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    // Insertar antes del formulario
    form.parentNode.insertBefore(errorElement, form);

    // Scroll al error
    errorElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

// Manejo de imagen
document.getElementById('imagenUsuario').addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
        // Validar tamaño (2MB)
        if (file.size > 2 * 1024 * 1024) {
            mostrarError('La imagen debe ser menor a 2MB');
            this.value = '';
            return;
        }

        // Validar tipo
        if (!file.type.match('image/jpeg') && !file.type.match('image/png')) {
            mostrarError('Solo se permiten imágenes JPG y PNG');
            this.value = '';
            return;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            // Crear preview si no existe
            let preview = document.getElementById('imagenPreview');
            if (!preview) {
                preview = document.createElement('img');
                preview.id = 'imagenPreview';
                preview.className = 'mt-2 rounded d-none';
                preview.style.maxWidth = '150px';
                preview.style.maxHeight = '150px';
                document.getElementById('imagenUsuario').parentNode.appendChild(preview);
            }
            preview.src = e.target.result;
            preview.classList.remove('d-none');
        }
        reader.readAsDataURL(file);
    }
});

// Validar edad mínima (18 años)
document.getElementById('fechaNacimiento').max = new Date(new Date().setFullYear(new Date().getFullYear() - 18)).toISOString().split('T')[0];

// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    // Agregar estilos para las tarjetas seleccionadas
    const style = document.createElement('style');
    style.textContent = `
        .user-type-card {
            cursor: pointer;
            transition: all 0.3s ease;
            border: 2px solid transparent;
        }
        .user-type-card:hover {
            border-color: #0d6efd;
            transform: translateY(-2px);
        }
        .user-type-card.selected {
            border-color: #0d6efd;
            background-color: #f8f9fa;
        }
        .step-indicator {
            display: flex;
            justify-content: space-between;
            margin-bottom: 2rem;
        }
        .step {
            text-align: center;
            flex: 1;
            position: relative;
        }
        .step:not(:last-child)::after {
            content: '';
            position: absolute;
            top: 15px;
            right: -50%;
            width: 100%;
            height: 2px;
            background-color: #dee2e6;
            z-index: 1;
        }
        .step-number {
            width: 30px;
            height: 30px;
            border-radius: 50%;
            background-color: #dee2e6;
            color: #6c757d;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 0.5rem;
            position: relative;
            z-index: 2;
        }
        .step.active .step-number {
            background-color: #0d6efd;
            color: white;
        }
        .step-label {
            color: #6c757d;
        }
        .step.active .step-label {
            color: #0d6efd;
            font-weight: 500;
        }
        .password-strength {
            height: 4px;
            background-color: #e9ecef;
            border-radius: 2px;
            margin-top: 5px;
            transition: all 0.3s ease;
        }
        .ruta-card {
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .ruta-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .ruta-card.selected {
            border-color: #0d6efd;
            box-shadow: 0 0 0 2px rgba(13, 110, 253, 0.25);
        }
    `;
    document.head.appendChild(style);

    console.log('BASE_URL configurada:', BASE_URL);
});
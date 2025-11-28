// Variables para controlar las peticiones asincrónicas
let nicknameTimeout;
let emailTimeout;
let currentNicknameCheck = null;
let currentEmailCheck = null;

// Validación asincrónica de nickname - MEJORADA
document.getElementById('nickname').addEventListener('input', function() {
    const nickname = this.value.trim();

    clearTimeout(nicknameTimeout);

    // Cancelar petición anterior si todavía está pendiente
    if (currentNicknameCheck) {
        currentNicknameCheck.abort();
    }

    // Limpiar estado anterior
    this.classList.remove('is-valid', 'is-invalid');
    hideValidationMessage(this);

    if (nickname.length === 0) {
        return;
    }

    if (nickname.length < 3) {
        showValidationMessage(this, 'Mínimo 3 caracteres', false);
        return;
    }

    // Debounce: esperar 600ms después de que el usuario deje de escribir
    nicknameTimeout = setTimeout(() => {
        checkNicknameAvailability(nickname, this);
    }, 600);
});

// Validación asincrónica de email - MEJORADA
document.getElementById('email').addEventListener('input', function() {
    const email = this.value.trim();

    clearTimeout(emailTimeout);

    // Cancelar petición anterior si todavía está pendiente
    if (currentEmailCheck) {
        currentEmailCheck.abort();
    }

    // Limpiar estado anterior
    this.classList.remove('is-valid', 'is-invalid');
    hideValidationMessage(this);

    if (email.length === 0) {
        return;
    }

    if (!validateEmail(email)) {
        showValidationMessage(this, 'Formato de email inválido', false);
        return;
    }

    // Debounce: esperar 600ms después de que el usuario deje de escribir
    emailTimeout = setTimeout(() => {
        checkEmailAvailability(email, this);
    }, 600);
});

// Función para verificar disponibilidad de nickname - MEJORADA
function checkNicknameAvailability(nickname, inputElement) {
    showLoadingState(inputElement, true);

    const basePath = (window.SESSION_API_BASE && window.SESSION_API_BASE.length > 0) ?
        window.SESSION_API_BASE :
        window.location.pathname.replace(/\/[^/]*$/, '');

    // Crear AbortController para poder cancelar la petición
    const controller = new AbortController();
    currentNicknameCheck = controller;

    fetch(`${basePath}/altaUsuario?action=checkNickname&nickname=${encodeURIComponent(nickname)}`, {
        method: 'GET',
        credentials: 'include',
        signal: controller.signal
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error HTTP: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            // Verificar que todavía es el valor actual (el usuario no ha seguido escribiendo)
            if (inputElement.value.trim() === nickname) {
                if (data.available) {
                    showValidationMessage(inputElement, data.message, true);
                } else {
                    showValidationMessage(inputElement, data.message, false);
                }
            }
        })
        .catch(error => {
            if (error.name !== 'AbortError') {
                console.error('Error verificando nickname:', error);
                // En caso de error, mostramos como disponible para no bloquear al usuario
                if (inputElement.value.trim() === nickname) {
                    showValidationMessage(inputElement, '✓ Disponible (verificación temporal)', true);
                }
            }
        })
        .finally(() => {
            if (inputElement.value.trim() === nickname) {
                showLoadingState(inputElement, false);
            }
            currentNicknameCheck = null;
        });
}

// Función para verificar disponibilidad de email - MEJORADA
function checkEmailAvailability(email, inputElement) {
    showLoadingState(inputElement, true);

    const basePath = (window.SESSION_API_BASE && window.SESSION_API_BASE.length > 0) ?
        window.SESSION_API_BASE :
        window.location.pathname.replace(/\/[^/]*$/, '');

    // Crear AbortController para poder cancelar la petición
    const controller = new AbortController();
    currentEmailCheck = controller;

    fetch(`${basePath}/altaUsuario?action=checkEmail&email=${encodeURIComponent(email)}`, {
        method: 'GET',
        credentials: 'include',
        signal: controller.signal
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error HTTP: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            // Verificar que todavía es el valor actual
            if (inputElement.value.trim() === email) {
                if (data.available) {
                    showValidationMessage(inputElement, data.message, true);
                } else {
                    showValidationMessage(inputElement, data.message, false);
                }
            }
        })
        .catch(error => {
            if (error.name !== 'AbortError') {
                console.error('Error verificando email:', error);
                // En caso de error, mostramos como disponible para no bloquear al usuario
                if (inputElement.value.trim() === email) {
                    showValidationMessage(inputElement, '✓ Disponible (verificación temporal)', true);
                }
            }
        })
        .finally(() => {
            if (inputElement.value.trim() === email) {
                showLoadingState(inputElement, false);
            }
            currentEmailCheck = null;
        });
}

// Función auxiliar para mostrar mensajes - MEJORADA
function showValidationMessage(inputElement, message, isValid) {
    // Limpiar completamente
    inputElement.classList.remove('is-valid', 'is-invalid');

    // Remover mensajes anteriores
    const existingFeedback = inputElement.parentNode.querySelector('.validation-feedback');
    if (existingFeedback) {
        existingFeedback.remove();
    }

    // Crear nuevo mensaje
    const feedbackElement = document.createElement('div');
    feedbackElement.className = `validation-feedback ${isValid ? 'valid-feedback' : 'invalid-feedback'} d-block`;
    feedbackElement.textContent = message;
    feedbackElement.style.fontSize = '0.875rem';
    feedbackElement.style.marginTop = '0.25rem';

    inputElement.parentNode.appendChild(feedbackElement);

    // Aplicar clase de validación
    if (isValid) {
        inputElement.classList.add('is-valid');
    } else {
        inputElement.classList.add('is-invalid');
    }
}

// Función para ocultar mensajes
function hideValidationMessage(inputElement) {
    const feedbackElement = inputElement.parentNode.querySelector('.validation-feedback');
    if (feedbackElement) {
        feedbackElement.remove();
    }
}

// Función para mostrar estado de carga
function showLoadingState(inputElement, isLoading) {
    let spinner = inputElement.parentNode.querySelector('.validation-spinner');

    if (isLoading) {
        if (!spinner) {
            spinner = document.createElement('span');
            spinner.className = 'validation-spinner spinner-border spinner-border-sm';
            spinner.style.position = 'absolute';
            spinner.style.right = '12px';
            spinner.style.top = '50%';
            spinner.style.transform = 'translateY(-50%)';
            spinner.style.zIndex = '5';

            inputElement.parentNode.style.position = 'relative';
            inputElement.parentNode.appendChild(spinner);
        }
        spinner.style.display = 'block';
        inputElement.style.paddingRight = '40px';
    } else {
        if (spinner) {
            spinner.style.display = 'none';
        }
        inputElement.style.paddingRight = '';
    }
}

// Modificar validateStep1 para validar disponibilidad - MEJORADA
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
            // Solo remover invalid si no hay otros errores
            if (!element.classList.contains('is-invalid')) {
                element.classList.remove('is-invalid');
            }
        }
    });

    // Validaciones específicas
    const nickname = document.getElementById('nickname');
    const email = document.getElementById('email');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');

    // Validar que nickname esté disponible Y válido
    if (nickname.value.trim() && !nickname.classList.contains('is-valid')) {
        if (!nickname.classList.contains('is-invalid')) {
            nickname.classList.add('is-invalid');
            showValidationMessage(nickname, 'Verifique la disponibilidad del nickname', false);
        }
        isValid = false;
    }

    // Validar que email esté disponible Y válido
    if (email.value.trim() && !email.classList.contains('is-valid')) {
        if (!email.classList.contains('is-invalid')) {
            email.classList.add('is-invalid');
            showValidationMessage(email, 'Verifique la disponibilidad del email', false);
        }
        isValid = false;
    }

    // Validar email formato
    if (email.value && !validateEmail(email.value)) {
        email.classList.add('is-invalid');
        isValid = false;
    }

    // Validar contraseñas
    if (password.value && password.value.length < 6) {
        password.classList.add('is-invalid');
        isValid = false;
    }

    if (password.value !== confirmPassword.value) {
        confirmPassword.classList.add('is-invalid');
        isValid = false;
    }

    return isValid;
}
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

    if (confirmPassword === '') {
        // Si está vacío, limpiar validación
        this.classList.remove('is-valid', 'is-invalid');
    } else if (password !== confirmPassword) {
        // Si no coinciden, marcar como inválido
        this.classList.add('is-invalid');
        this.classList.remove('is-valid');
    } else {
        // Si coinciden, marcar como válido
        this.classList.add('is-valid');
        this.classList.remove('is-invalid');
    }
});

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

// Envío del formulario
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

    // Enviar al servidor
    const basePath = (window.SESSION_API_BASE && window.SESSION_API_BASE.length > 0) ? window.SESSION_API_BASE : window.location.pathname.replace(/\/[^/]*$/, '');
    fetch(basePath + '/altaUsuario', {
        method: 'POST',
        body: formData,
        credentials: 'include'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                // Éxito
                try {
                    const successModal = new bootstrap.Modal(document.getElementById('successModal'));
                    successModal.show();
                } catch (e) { /* ignore */ }

                // Guardar info de sesión en localStorage como fallback inmediato
                try {
                    if (data.nickname) localStorage.setItem('session_nickname', data.nickname);
                    if (data.tipo) localStorage.setItem('session_tipo', data.tipo);
                    localStorage.setItem('session_timestamp', String(Date.now()));
                } catch (e) {
                    console.warn('No se pudo guardar session en localStorage:', e);
                }

                // Notificar a otras partes de la página que la sesión cambió
                try { window.dispatchEvent(new Event('sessionUpdated')); } catch (e) { console.warn('No se pudo dispatch sessionUpdated', e); }

                // Redirigir según tipo de usuario
                setTimeout(() => {
                    try {
                        if (data.tipo === 'aerolinea' && data.nickname) {
                            window.location.href = 'alta-vuelo.jsp?aerolinea=' + encodeURIComponent(data.nickname);
                        } else {
                            window.location.href = 'PaginaPrincipal.jsp';
                        }
                    } catch (e) { window.location.reload(); }
                }, 900);
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

// Validación asincrónica de nickname
document.getElementById('nickname').addEventListener('input', function() {
    const nickname = this.value.trim();

    clearTimeout(nicknameTimeout);

    // Cancelar petición anterior si todavía está pendiente
    if (currentNicknameCheck) {
        currentNicknameCheck.abort();
    }

    // Limpiar estado anterior
    this.classList.remove('is-valid', 'is-invalid');
    hideValidationMessage(this);

    if (nickname.length === 0) {
        return;
    }

    if (nickname.length < 3) {
        showValidationMessage(this, 'Mínimo 3 caracteres', false);
        return;
    }

    // Debounce: esperar 800ms después de que el usuario deje de escribir
    nicknameTimeout = setTimeout(() => {
        checkNicknameAvailability(nickname, this);
    }, 800);
});

// Validación asincrónica de email
document.getElementById('email').addEventListener('input', function() {
    const email = this.value.trim();

    clearTimeout(emailTimeout);

    // Cancelar petición anterior si todavía está pendiente
    if (currentEmailCheck) {
        currentEmailCheck.abort();
    }

    // Limpiar estado anterior
    this.classList.remove('is-valid', 'is-invalid');
    hideValidationMessage(this);

    if (email.length === 0) {
        return;
    }

    if (!validateEmail(email)) {
        showValidationMessage(this, 'Formato de email inválido', false);
        return;
    }

    // Debounce: esperar 800ms después de que el usuario deje de escribir
    emailTimeout = setTimeout(() => {
        checkEmailAvailability(email, this);
    }, 800);
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
        .validation-feedback {
            display: block !important;
        }
        .validation-spinner {
            color: #0d6efd;
            display: none;
        }
        .form-control.is-valid {
            border-color: #198754;
            padding-right: 40px;
        }
        .form-control.is-invalid {
            border-color: #dc3545;
            padding-right: 40px;
        }
        .valid-feedback {
            color: #198754;
            display: block !important;
        }
        .invalid-feedback {
            color: #dc3545;
            display: block !important;
        }
    `;
    document.head.appendChild(style);
});
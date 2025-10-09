document.addEventListener('DOMContentLoaded', function() {
    // Verificar que el usuario sea una aerolínea logueada
    verificarSesionAerolinea();
});

function verificarSesionAerolinea() {
    // Verificar si hay una sesión de aerolínea activa
    const usuarioSesion = sessionManager.getCurrentUser(); // Asumiendo que tienes un session manager
    if (!usuarioSesion || usuarioSesion.tipo !== 'aerolinea') {
        mostrarError('Debe iniciar sesión como aerolínea para crear rutas');
        // Redirigir al login o página principal
        setTimeout(() => {
            window.location.href = 'PaginaPrincipal.jsp';
        }, 3000);
        return;
    }
}

// Envío del formulario - VERSIÓN CORREGIDA
document.getElementById('formAltaRuta').addEventListener('submit', function(event) {
    event.preventDefault();

    if (this.checkValidity()) {
        enviarRutaAlServidor();
    } else {
        event.stopPropagation();
    }

    this.classList.add('was-validated');
});

function enviarRutaAlServidor() {
    const formData = new FormData();

    // Obtener la aerolínea de la sesión
    const usuarioSesion = sessionManager.getCurrentUser();
    if (!usuarioSesion || usuarioSesion.tipo !== 'aerolinea') {
        mostrarError('No hay una aerolínea logueada');
        return;
    }

    const nombreAerolinea = usuarioSesion.nickname; // O el campo que tenga el nickname

    // Agregar datos del formulario
    formData.append('nombreRuta', document.getElementById('nombreRuta').value);
    formData.append('descripcionCorta', document.getElementById('descripcionCorta').value);
    formData.append('descripcion', document.getElementById('descripcion').value);
    formData.append('aerolinea', nombreAerolinea); // ← Usar la aerolínea de la sesión
    formData.append('origen', document.getElementById('origen').value);
    formData.append('destino', document.getElementById('destino').value);
    formData.append('hora', document.getElementById('hora').value);
    formData.append('costoTurista', document.getElementById('costoTurista').value);
    formData.append('costoEjecutivo', document.getElementById('costoEjecutivo').value);
    formData.append('costoEquipaje', document.getElementById('costoEquipaje').value);

    // Agregar categorías seleccionadas
    const categoriasSelect = document.getElementById('categorias');
    const categoriasSeleccionadas = Array.from(categoriasSelect.selectedOptions).map(option => option.value);
    categoriasSeleccionadas.forEach(categoria => {
        formData.append('categorias', categoria);
    });

    // Agregar imagen si existe
    const imagenInput = document.getElementById('imagenRuta');
    if (imagenInput.files.length > 0) {
        formData.append('imagenRuta', imagenInput.files[0]);
    }

    // Mostrar loading
    const submitBtn = document.querySelector('#formAltaRuta button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status"></span> Creando ruta...';
    submitBtn.disabled = true;

    // Enviar al servidor
    fetch('altaRuta', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                mostrarExito(data.message || 'Ruta creada exitosamente. Estado: Ingresada - Esperando confirmación del administrador.');
                // Limpiar formulario después de éxito
                document.getElementById('formAltaRuta').reset();
                document.getElementById('formAltaRuta').classList.remove('was-validated');
            } else {
                mostrarError(data.error || 'Error al crear la ruta');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error de conexión con el servidor');
        })
        .finally(() => {
            // Restaurar botón
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        });
}

function mostrarExito(mensaje) {
    // Crear alerta de éxito
    const alerta = document.createElement('div');
    alerta.className = 'alert alert-success alert-dismissible fade show';
    alerta.innerHTML = `
        <strong>Éxito:</strong> ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    // Insertar antes del formulario
    const form = document.getElementById('formAltaRuta');
    form.parentNode.insertBefore(alerta, form);

    // Scroll al mensaje
    alerta.scrollIntoView({ behavior: 'smooth', block: 'center' });

    // Auto-eliminar después de 5 segundos
    setTimeout(() => {
        if (alerta.parentNode) {
            alerta.remove();
        }
    }, 5000);
}

function mostrarError(mensaje) {
    // Limpiar errores anteriores
    const erroresAnteriores = document.querySelectorAll('.alert-danger');
    erroresAnteriores.forEach(error => error.remove());

    // Crear alerta de error
    const alerta = document.createElement('div');
    alerta.className = 'alert alert-danger alert-dismissible fade show';
    alerta.innerHTML = `
        <strong>Error:</strong> ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    // Insertar antes del formulario
    const form = document.getElementById('formAltaRuta');
    form.parentNode.insertBefore(alerta, form);

    // Scroll al error
    alerta.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

// Validación de imagen
document.getElementById('imagenRuta').addEventListener('change', function(e) {
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

        // Mostrar preview de la imagen
        const reader = new FileReader();
        reader.onload = function(e) {
            let preview = document.getElementById('imagenPreview');
            if (!preview) {
                preview = document.createElement('img');
                preview.id = 'imagenPreview';
                preview.className = 'mt-2 rounded d-none';
                preview.style.maxWidth = '200px';
                preview.style.maxHeight = '150px';
                document.getElementById('imagenRuta').parentNode.appendChild(preview);
            }
            preview.src = e.target.result;
            preview.classList.remove('d-none');
        };
        reader.readAsDataURL(file);
    }
});

// Validación personalizada para múltiple select
document.getElementById('categorias').addEventListener('change', function() {
    if (this.selectedOptions.length === 0) {
        this.classList.add('is-invalid');
    } else {
        this.classList.remove('is-invalid');
    }
});
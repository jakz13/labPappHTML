// compra-paquete.js - Versión integrada con Session Manager

// Variables globales
let paquetesDisponibles = [];
let paquetesComprados = [];
let paqueteSeleccionado = null;
let clienteActual = null;
let sessionData = null;

// Elementos del DOM
let listaPaquetes, infoPaquete, mensajeCompra, listaPaquetesComprados, contadorPaquetes, nombreClienteElement;

// Inicializar la página
async function inicializarPagina() {
    console.log('🚀 Inicializando compra de paquetes...');

    // Obtener elementos del DOM
    listaPaquetes = document.getElementById('listaPaquetes');
    infoPaquete = document.getElementById('infoPaquete');
    mensajeCompra = document.getElementById('mensajeCompra');
    listaPaquetesComprados = document.getElementById('listaPaquetesComprados');
    contadorPaquetes = document.getElementById('contadorPaquetes');
    nombreClienteElement = document.getElementById('nombreCliente');

    // Configurar event listeners
    document.getElementById('btnConfirmarCompra').addEventListener('click', confirmarCompra);
    document.getElementById('btnCancelarSeleccion').addEventListener('click', cancelarSeleccion);
    document.getElementById('btnModalConfirmar').addEventListener('click', realizarCompraDesdeModal);

    // Esperar a que el session manager cargue y luego obtener datos
    await esperarSessionManager();

    // Verificar sesión y cargar datos
    await verificarSesionYCargarDatos();

    console.log('✅ Compra de paquetes inicializada correctamente');
}

// Esperar a que el session manager esté listo
async function esperarSessionManager() {
    return new Promise((resolve) => {
        const checkSessionManager = () => {
            if (window.SESSION_API_BASE !== undefined) {
                console.log('✅ Session manager detectado');
                resolve();
            } else {
                console.log('⏳ Esperando session manager...');
                setTimeout(checkSessionManager, 100);
            }
        };
        checkSessionManager();
    });
}

// Verificar sesión y cargar datos
async function verificarSesionYCargarDatos() {
    try {
        console.log('🔐 Verificando sesión del usuario...');

        // Obtener datos de sesión del session manager
        const response = await fetch(window.SESSION_API_BASE + '/api/check-session', {
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Error al verificar sesión');
        }

        sessionData = await response.json();
        console.log('📋 Datos de sesión:', sessionData);

        // CORREGIDO: Comparar en mayúsculas para evitar problemas de case
        const tipoUsuario = sessionData.tipo ? sessionData.tipo.toUpperCase() : '';

        if (sessionData && sessionData.authenticated && tipoUsuario === 'CLIENTE') {
            // Usuario autenticado como cliente
            clienteActual = sessionData.nickname;
            console.log('👤 Cliente autenticado:', clienteActual);

            // Actualizar interfaz con datos del cliente
            actualizarInterfazCliente();

            // Cargar datos del servidor
            await cargarDatosIniciales();

        } else if (sessionData && sessionData.authenticated && tipoUsuario !== 'CLIENTE') {
            // Usuario autenticado pero no es cliente
            mostrarEstadoNoCliente();

        } else {
            // Usuario no autenticado
            mostrarEstadoNoAutenticado();
        }

    } catch (error) {
        console.error('❌ Error verificando sesión:', error);
        mostrarEstadoErrorSesion();
    }
}

// Actualizar interfaz con datos del cliente
function actualizarInterfazCliente() {
    if (nombreClienteElement && clienteActual) {
        // Mostrar nickname (podrías obtener el nombre completo del servidor si lo necesitas)
        nombreClienteElement.textContent = clienteActual;
    }

    // Mostrar secciones principales
    mostrarEstadoCarga(false);
}

// Mostrar estado cuando el usuario no es cliente
function mostrarEstadoNoCliente() {
    mostrarEstadoCarga(false);

    const mensaje = `
        <div class="alert alert-warning text-center">
            <i class="bi bi-exclamation-triangle me-2"></i>
            <strong>Acceso restringido</strong><br>
            Esta funcionalidad está disponible solo para clientes.
            <div class="mt-2">
                <small>Tu tipo de usuario: <strong>${sessionData.tipo}</strong></small>
            </div>
            <div class="mt-2">
                <small class="text-muted">
                    <i class="bi bi-info-circle me-1"></i>
                    Si eres un cliente, contacta con soporte técnico.
                </small>
            </div>
        </div>
    `;

    if (mensajeCompra) {
        mensajeCompra.innerHTML = mensaje;
    }

    // Ocultar secciones que no debe ver
    const seccionPaquetes = document.getElementById('seccionPaquetes');
    if (seccionPaquetes) seccionPaquetes.classList.add('d-none');
}

// Mostrar estado cuando el usuario no está autenticado
function mostrarEstadoNoAutenticado() {
    mostrarEstadoCarga(false);

    const mensaje = `
        <div class="alert alert-info text-center">
            <i class="bi bi-person-x me-2"></i>
            <strong>Inicia sesión para comprar paquetes</strong><br>
            Debes iniciar sesión como cliente para acceder a esta funcionalidad.
            <div class="mt-3">
                <button class="btn btn-primary" onclick="abrirModalLogin()">
                    <i class="bi bi-box-arrow-in-right me-2"></i>Iniciar Sesión
                </button>
            </div>
        </div>
    `;

    if (mensajeCompra) {
        mensajeCompra.innerHTML = mensaje;
    }

    // Ocultar secciones que no debe ver
    const seccionPaquetes = document.getElementById('seccionPaquetes');
    if (seccionPaquetes) seccionPaquetes.classList.add('d-none');
}

// Mostrar estado cuando hay error de sesión
function mostrarEstadoErrorSesion() {
    mostrarEstadoCarga(false);

    const mensaje = `
        <div class="alert alert-danger text-center">
            <i class="bi bi-exclamation-triangle me-2"></i>
            <strong>Error al verificar sesión</strong><br>
            No se pudo verificar tu sesión. Por favor, recarga la página.
            <div class="mt-2">
                <button class="btn btn-outline-danger btn-sm" onclick="window.location.reload()">
                    <i class="bi bi-arrow-clockwise me-1"></i>Recargar
                </button>
            </div>
        </div>
    `;

    if (mensajeCompra) {
        mensajeCompra.innerHTML = mensaje;
    }
}

// Función para abrir modal de login
function abrirModalLogin() {
    const loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
    loginModal.show();
}

// Cargar datos iniciales desde el servidor
async function cargarDatosIniciales() {
    try {
        mostrarEstadoCarga(true);
        console.log('📦 Cargando datos iniciales...');

        await cargarPaquetesDisponibles();

        if (clienteActual) {
            await cargarPaquetesComprados();
        }

        actualizarContadores();
        mostrarEstadoCarga(false);

    } catch (error) {
        console.error('❌ Error cargando datos iniciales:', error);
        mostrarEstadoCarga(false);
        mostrarMensaje('Error al cargar los datos: ' + error.message, 'danger');
    }
}

// Cargar paquetes disponibles desde el servidor
async function cargarPaquetesDisponibles() {
    try {
        console.log('🌐 Cargando paquetes disponibles...');

        const response = await fetch('compra-paquete?action=listar-paquetes-disponibles');

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const data = await response.json();
        paquetesDisponibles = Array.isArray(data) ? data : [];
        console.log('📊 Paquetes disponibles cargados:', paquetesDisponibles);

        // Actualizar la lista en la interfaz
        actualizarListaPaquetesDisponibles();

    } catch (error) {
        console.error('💥 Error cargando paquetes disponibles:', error);
        mostrarMensaje('No se pudieron cargar los paquetes disponibles: ' + error.message, 'danger');
        paquetesDisponibles = [];
    }
}

// Cargar paquetes comprados desde el servidor
async function cargarPaquetesComprados() {
    try {
        if (!clienteActual) {
            console.log('👤 No hay cliente logueado, omitiendo carga de paquetes comprados');
            paquetesComprados = [];
            actualizarPaquetesComprados();
            return;
        }

        console.log('🌐 Cargando paquetes comprados para:', clienteActual);

        const response = await fetch(`compra-paquete?action=paquetes-comprados&cliente=${encodeURIComponent(clienteActual)}`);

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const data = await response.json();
        paquetesComprados = Array.isArray(data) ? data : [];
        console.log('📦 Paquetes comprados cargados:', paquetesComprados);

        // Actualizar la lista en la interfaz
        actualizarPaquetesComprados();

    } catch (error) {
        console.error('💥 Error cargando paquetes comprados:', error);
        mostrarMensaje('No se pudieron cargar los paquetes comprados: ' + error.message, 'warning');
        paquetesComprados = [];
        actualizarPaquetesComprados();
    }
}

// Función para mostrar/ocultar estado de carga
function mostrarEstadoCarga(mostrar) {
    const estadoCarga = document.getElementById('estadoCarga');
    const seccionPaquetes = document.getElementById('seccionPaquetes');
    const seccionPaquetesComprados = document.getElementById('seccionPaquetesComprados');

    if (estadoCarga && seccionPaquetes) {
        if (mostrar) {
            estadoCarga.classList.remove('d-none');
            seccionPaquetes.classList.add('d-none');
            if (seccionPaquetesComprados) seccionPaquetesComprados.classList.add('d-none');
        } else {
            estadoCarga.classList.add('d-none');
            seccionPaquetes.classList.remove('d-none');
            if (seccionPaquetesComprados && paquetesComprados.length > 0) {
                seccionPaquetesComprados.classList.remove('d-none');
            }
        }
    }
}

// Actualizar lista de paquetes disponibles en la interfaz
function actualizarListaPaquetesDisponibles() {
    if (!listaPaquetes) return;

    listaPaquetes.innerHTML = '';

    if (paquetesDisponibles.length === 0) {
        listaPaquetes.innerHTML = `
            <div class="col-12">
                <div class="alert alert-info text-center">
                    <p class="mb-0">No hay paquetes disponibles en este momento.</p>
                    <small class="text-muted">Vuelve más tarde o contacta con el administrador.</small>
                </div>
            </div>
        `;
        return;
    }

    paquetesDisponibles.forEach(paquete => {
        // SOLO verificar si el usuario actual ya compró este paquete
        const yaCompradoPorUsuario = paquetesComprados.some(p => p.id === paquete.id && estaVigente(p));
        const costoFinal = paquete.costoFinal || paquete.costoBase || 0;

        const paqueteHTML = `
            <div class="col-md-6">
                <div class="card paquete-card ${yaCompradoPorUsuario ? 'opacity-50' : ''} h-100"
                     onclick="${yaCompradoPorUsuario ? '' : `seleccionarPaquete('${paquete.id}')`}" 
                     style="${yaCompradoPorUsuario ? '' : 'cursor: pointer;'}">
                    <div class="card-body">
                        <h6 class="card-title text-primary">${paquete.nombre || 'Sin nombre'}</h6>
                        <p class="card-text small text-muted">${paquete.descripcion || 'Sin descripción disponible'}</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="h5 text-success">$${costoFinal.toFixed(2)}</span>
                            <span class="badge bg-info">${paquete.vigenciaDias || 0} días</span>
                        </div>
                        <div class="mt-2">
                            <small class="text-muted">
                                <strong>${paquete.cantidadRutas || 0}</strong> rutas incluidas | 
                                <strong>${paquete.descuento || 0}%</strong> descuento
                            </small>
                        </div>
                        ${yaCompradoPorUsuario ? '<div class="text-center mt-2"><span class="badge bg-warning">Ya comprado por ti</span></div>' : ''}
                    </div>
                </div>
            </div>
        `;

        listaPaquetes.innerHTML += paqueteHTML;
    });
}

// Seleccionar un paquete
async function seleccionarPaquete(id) {
    try {
        console.log('🎯 Seleccionando paquete:', id);

        // Verificar que el usuario esté autenticado
        if (!clienteActual) {
            mostrarMensaje('Debe iniciar sesión para seleccionar un paquete.', 'warning');
            return;
        }

        // Buscar el paquete en los disponibles
        const paqueteEnCache = paquetesDisponibles.find(p => p.id === id);
        if (paqueteEnCache) {
            paqueteSeleccionado = paqueteEnCache;
            console.log('📄 Información del paquete seleccionado (desde cache):', paqueteSeleccionado);
        } else {
            // Si no está en cache, obtener del servidor
            const response = await fetch(`compra-paquete?action=info-paquete&paquete=${encodeURIComponent(id)}`);

            if (!response.ok) {
                throw new Error(`Error HTTP: ${response.status}`);
            }

            paqueteSeleccionado = await response.json();
            console.log('📄 Información del paquete seleccionado (desde servidor):', paqueteSeleccionado);
        }

        // Actualizar información del paquete seleccionado
        actualizarInterfazPaqueteSeleccionado();

        // Mostrar sección de información
        infoPaquete.classList.remove('d-none');
        mensajeCompra.innerHTML = '';

        // Remover selección anterior y marcar actual
        document.querySelectorAll('.paquete-card').forEach(card => {
            card.classList.remove('border-primary', 'shadow');
        });

        // Encontrar y marcar la tarjeta seleccionada
        const cards = document.querySelectorAll('.paquete-card');
        for (let card of cards) {
            if (card.querySelector('.card-title').textContent === paqueteSeleccionado.nombre) {
                card.classList.add('border-primary', 'shadow');
                break;
            }
        }

    } catch (error) {
        console.error('💥 Error seleccionando paquete:', error);
        mostrarMensaje('Error al cargar la información del paquete: ' + error.message, 'danger');
    }
}

// Actualizar interfaz con información del paquete seleccionado
function actualizarInterfazPaqueteSeleccionado() {
    if (!paqueteSeleccionado) return;

    document.getElementById('nombrePaqueteSeleccionado').textContent = paqueteSeleccionado.nombre || 'Sin nombre';

    const costoFinal = paqueteSeleccionado.costoFinal || paqueteSeleccionado.costoBase || 0;
    document.getElementById('costoPaqueteSeleccionado').textContent = costoFinal.toFixed(2);

    document.getElementById('vigenciaPaquete').textContent = paqueteSeleccionado.vigenciaDias || 0;

    const fechaCompra = new Date();
    const fechaVencimiento = new Date();
    fechaVencimiento.setDate(fechaVencimiento.getDate() + (paqueteSeleccionado.vigenciaDias || 0));

    document.getElementById('fechaCompra').textContent = fechaCompra.toLocaleDateString();
    document.getElementById('fechaVencimiento').textContent = fechaVencimiento.toLocaleDateString();

    // Mostrar rutas incluidas (información básica)
    const rutasContainer = document.getElementById('rutasPaqueteSeleccionado');
    rutasContainer.innerHTML = '';

    if (paqueteSeleccionado.rutas && paqueteSeleccionado.rutas.length > 0) {
        paqueteSeleccionado.rutas.forEach(ruta => {
            const rutaHTML = `
                <div class="ruta-item p-2 border rounded mb-2 bg-light">
                    <strong class="text-dark">${ruta.nombre || 'Sin nombre'}</strong><br>
                    <small class="text-muted">
                        ${ruta.aerolinea || 'Aerolínea no especificada'} | 
                        ${ruta.origen || 'N/A'} → ${ruta.destino || 'N/A'}
                    </small>
                </div>
            `;
            rutasContainer.innerHTML += rutaHTML;
        });
    } else {
        rutasContainer.innerHTML = '<p class="text-muted">No hay información de rutas disponible para este paquete.</p>';
    }
}

// Confirmar compra
function confirmarCompra() {
    if (!paqueteSeleccionado) {
        mostrarMensaje('Por favor seleccione un paquete primero.', 'warning');
        return;
    }

    if (!clienteActual) {
        mostrarMensaje('Debe iniciar sesión para realizar una compra.', 'warning');
        return;
    }

    // Verificar si ya está comprado
    const yaComprado = paquetesComprados.some(p => p.id === paqueteSeleccionado.id && estaVigente(p));
    if (yaComprado) {
        mostrarMensaje('Ya tienes este paquete vigente en tu cuenta.', 'warning');
        return;
    }

    // Mostrar modal de confirmación
    document.getElementById('modalNombrePaquete').textContent = paqueteSeleccionado.nombre || 'Sin nombre';
    document.getElementById('modalCostoPaquete').textContent = (paqueteSeleccionado.costoFinal || paqueteSeleccionado.costoBase || 0).toFixed(2);
    document.getElementById('modalVigencia').textContent = paqueteSeleccionado.vigenciaDias || 0;

    const confirmacionModal = new bootstrap.Modal(document.getElementById('confirmacionModal'));
    confirmacionModal.show();
}

// Realizar compra desde el modal
async function realizarCompraDesdeModal() {
    try {
        console.log('💰 Realizando compra del paquete:', paqueteSeleccionado.id);

        const fechaCompra = new Date();
        const fechaVencimiento = new Date();
        fechaVencimiento.setDate(fechaVencimiento.getDate() + (paqueteSeleccionado.vigenciaDias || 0));
        const costoFinal = paqueteSeleccionado.costoFinal || paqueteSeleccionado.costoBase || 0;

        // DEBUG: Log los datos que se enviarán
        console.log('📤 Datos a enviar:', {
            action: 'realizar-compra',
            paquete: paqueteSeleccionado.id,
            cliente: clienteActual,
            validezDias: (paqueteSeleccionado.vigenciaDias || 0).toString(),
            fechaCompra: fechaCompra.toISOString().split('T')[0],
            costo: costoFinal.toString()
        });

        // ✅ SOLUCIÓN: Usar URLSearchParams en lugar de FormData
        const params = new URLSearchParams();
        params.append('action', 'realizar-compra');
        params.append('paquete', paqueteSeleccionado.id);
        params.append('cliente', clienteActual);
        params.append('validezDias', (paqueteSeleccionado.vigenciaDias || 0).toString());
        params.append('fechaCompra', fechaCompra.toISOString().split('T')[0]);
        params.append('costo', costoFinal.toString());

        console.log('🌐 Enviando request a compra-paquete...');
        console.log('📝 Body:', params.toString());

        const response = await fetch('compra-paquete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
            },
            body: params
        });

        console.log('📨 Response status:', response.status);
        console.log('📨 Response ok:', response.ok);

        if (!response.ok) {
            const errorText = await response.text();
            console.error('❌ Error response text:', errorText);
            let errorData;
            try {
                errorData = JSON.parse(errorText);
            } catch (e) {
                errorData = { error: errorText };
            }
            throw new Error(errorData.error || `Error HTTP: ${response.status}`);
        }

        const result = await response.json();
        console.log('✅ Resultado compra:', result);

        if (result.success) {
            // Mostrar mensaje de éxito
            mostrarMensaje(`¡Compra realizada con éxito! El paquete "${paqueteSeleccionado.nombre}" ha sido agregado a tu cuenta. Vence el ${fechaVencimiento.toLocaleDateString()}.`, 'success');

            // Actualizar datos
            await cargarPaquetesComprados();
            await cargarPaquetesDisponibles(); // Recargar para actualizar estado "Ya comprado"
            actualizarContadores();

            // Limpiar selección
            cancelarSeleccion();

        } else {
            throw new Error(result.error || 'Error desconocido en la compra');
        }

        // Cerrar modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('confirmacionModal'));
        modal.hide();

    } catch (error) {
        console.error('💥 Error realizando compra:', error);
        mostrarMensaje('Error al realizar la compra: ' + error.message, 'danger');
    }
}

// Cancelar selección
function cancelarSeleccion() {
    paqueteSeleccionado = null;
    infoPaquete.classList.add('d-none');
    document.querySelectorAll('.paquete-card').forEach(card => {
        card.classList.remove('border-primary', 'shadow');
    });
    mensajeCompra.innerHTML = '';
}

// Actualizar lista de paquetes comprados en la interfaz
function actualizarPaquetesComprados() {
    if (!listaPaquetesComprados) return;

    listaPaquetesComprados.innerHTML = '';

    if (paquetesComprados.length === 0) {
        listaPaquetesComprados.innerHTML = `
            <div class="col-12">
                <div class="alert alert-warning text-center">
                    <p class="mb-0 text-light">No has comprado ningún paquete aún.</p>
                    <small class="text-light">Selecciona un paquete disponible para realizar tu primera compra.</small>
                </div>
            </div>
        `;
        return;
    }

    paquetesComprados.forEach(paquete => {
        const vigente = estaVigente(paquete);
        const fechaCompra = paquete.fechaCompra ? new Date(paquete.fechaCompra) : new Date();
        const fechaVencimiento = paquete.fechaVencimiento ? new Date(paquete.fechaVencimiento) : new Date();

        // Usar costoFinal si está disponible, sino usar costo
        const costoMostrar = paquete.costoFinal || paquete.costo || 0;

        const paqueteHTML =
            '<div class="col-md-6">' +
            '    <div class="card ' + (vigente ? 'border-success' : 'border-secondary') + ' h-100">' +
            '        <div class="card-body">' +
            '            <div class="d-flex justify-content-between align-items-start">' +
            '                <h6 class="card-title text-light">' + (paquete.nombre || 'Sin nombre') + '</h6>' +
            '                <span class="badge ' + (vigente ? 'bg-success' : 'bg-secondary') + '">' + (vigente ? 'Vigente' : 'Vencido') + '</span>' +
            '            </div>' +
            '            <p class="small text-light mb-2">' +
            '                <i class="bi bi-calendar-check text-light"></i> Comprado: ' + fechaCompra.toLocaleDateString() +
            '            </p>' +
            '            <p class="small text-light mb-2">' +
            '                <i class="bi bi-calendar-x text-light"></i> Vence: ' + fechaVencimiento.toLocaleDateString() +
            '            </p>' +
            '            <div class="mt-2">' +
            '                <small class="text-light"><strong><i class="bi bi-geo-route text-light"></i> Rutas:</strong> ' + (paquete.cantidadRutas || 0) + ' incluidas</small>' +
            '            </div>' +
            '            <div class="mt-2">' +
            '                <small class="text-light"><strong><i class="bi bi-currency-dollar text-light"></i> Costo final:</strong> $' + costoMostrar.toFixed(2) + '</small>' +
            '            </div>' +
            (vigente ?
                '            <div class="mt-3">' +
                '                <button class="btn btn-outline-primary btn-sm w-100" onclick="utilizarPaquete(\'' + paquete.id + '\')">' +
                '                    <i class="bi bi-airplane"></i> Utilizar Paquete' +
                '                </button>' +
                '            </div>'
                : '') +
            '        </div>' +
            '    </div>' +
            '</div>';
        listaPaquetesComprados.innerHTML += paqueteHTML;
    });
}

// Verificar si un paquete está vigente
function estaVigente(paquete) {
    if (!paquete.fechaVencimiento) return false;
    try {
        return new Date(paquete.fechaVencimiento) > new Date();
    } catch (e) {
        return false;
    }
}

// Actualizar contadores
function actualizarContadores() {
    if (!contadorPaquetes) return;

    const paquetesVigentes = paquetesComprados.filter(estaVigente).length;
    contadorPaquetes.textContent = `${paquetesVigentes}/${paquetesComprados.length}`;
}

// Mostrar mensajes
function mostrarMensaje(mensaje, tipo) {
    if (!mensajeCompra) return;

    const iconos = {
        'success': 'bi-check-circle',
        'danger': 'bi-exclamation-triangle',
        'warning': 'bi-exclamation-circle',
        'info': 'bi-info-circle'
    };

    const icono = iconos[tipo] || 'bi-info-circle';

    mensajeCompra.innerHTML = `
        <div class="alert alert-${tipo} alert-dismissible fade show">
            <i class="bi ${icono} me-2"></i>${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
}

// Utilizar paquete
function utilizarPaquete(id) {
    const paquete = paquetesComprados.find(p => p.id === id);
    if (paquete && estaVigente(paquete)) {
        mostrarMensaje(
            `Redirigiendo a selección de vuelos para el paquete "${paquete.nombre}". ` +
            `Podrás utilizar las ${paquete.cantidadRutas || 0} rutas incluidas.`,
            'info'
        );

        // Simular redirección después de 2 segundos
        setTimeout(() => {
            // En una implementación real, aquí se redirigiría a la página de reservas
            window.location.href = `reserva-vuelo.jsp?paquete=${id}`;
            console.log(`Redirigiendo a reservas con paquete: ${id}`);
        }, 1000);

    } else {
        mostrarMensaje('Este paquete no está disponible para usar.', 'warning');
    }
}

// Escuchar eventos de actualización de sesión
window.addEventListener('sessionUpdated', function() {
    console.log('🔄 Evento sessionUpdated recibido, recargando datos...');
    setTimeout(() => {
        window.location.reload();
    }, 500);
});

// Inicializar la página cuando se carga
document.addEventListener('DOMContentLoaded', function() {
    console.log('📄 DOM cargado, inicializando compra de paquetes...');
    inicializarPagina();
});

// Manejar errores no capturados
window.addEventListener('error', function(e) {
    console.error('💥 Error no capturado:', e.error);
    mostrarMensaje('Ocurrió un error inesperado. Por favor, recarga la página.', 'danger');
});
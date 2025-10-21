// compra-paquete.js - Versión conectada a base de datos

// Variables globales
let paquetesDisponibles = [];
let paquetesComprados = [];
let paqueteSeleccionado = null;
let clienteActual = null; // Esto debería venir de la sesión del usuario

// Elementos del DOM
let listaPaquetes, infoPaquete, mensajeCompra, listaPaquetesComprados, contadorPaquetes, saldoCliente;

// Inicializar la página
async function inicializarPagina() {
    console.log('🚀 Inicializando compra de paquetes...');

    // Obtener elementos del DOM
    listaPaquetes = document.getElementById('listaPaquetes');
    infoPaquete = document.getElementById('infoPaquete');
    mensajeCompra = document.getElementById('mensajeCompra');
    listaPaquetesComprados = document.getElementById('listaPaquetesComprados');
    contadorPaquetes = document.getElementById('contadorPaquetes');
    saldoCliente = document.getElementById('saldoCliente');

    // Configurar event listeners
    document.getElementById('btnConfirmarCompra').addEventListener('click', confirmarCompra);
    document.getElementById('btnCancelarSeleccion').addEventListener('click', cancelarSeleccion);
    document.getElementById('btnModalConfirmar').addEventListener('click', realizarCompraDesdeModal);

    // Obtener cliente de la sesión (simulado por ahora)
    await obtenerClienteDeSesion();

    // Cargar datos iniciales desde el servidor
    await cargarDatosIniciales();

    console.log('✅ Compra de paquetes inicializada correctamente');
}

// Obtener cliente de la sesión (simulado - en producción esto vendría del servidor)
async function obtenerClienteDeSesion() {
    try {
        // Por ahora simulamos un cliente - en producción esto vendría de la sesión
        clienteActual = "maria001"; // Esto debería venir del login real
        console.log('👤 Cliente de sesión:', clienteActual);

        // Actualizar la interfaz con el nombre del cliente
        const clienteInfo = document.querySelector('.alert-info strong');
        if (clienteInfo) {
            clienteInfo.textContent = "María González (maria001)";
        }

    } catch (error) {
        console.error('❌ Error obteniendo sesión:', error);
        // Si no hay sesión, redirigir al login
        // window.location.href = 'login.jsp';
    }
}

// Cargar datos iniciales desde el servidor
async function cargarDatosIniciales() {
    try {
        console.log('📦 Cargando datos iniciales...');

        // Cargar paquetes disponibles
        await cargarPaquetesDisponibles();

        // Cargar paquetes comprados (si hay un cliente logueado)
        if (clienteActual) {
            await cargarPaquetesComprados();
        }

        // Actualizar interfaz
        actualizarContadores();

    } catch (error) {
        console.error('❌ Error cargando datos iniciales:', error);
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
        const yaComprado = paquetesComprados.some(p => p.id === paquete.id && estaVigente(p));
        const costoFinal = paquete.costoFinal || paquete.costoBase || 0;

        const paqueteHTML = `
            <div class="col-md-6">
                <div class="card paquete-card ${yaComprado ? 'opacity-50' : ''} h-100"
                     onclick="${yaComprado ? '' : `seleccionarPaquete('${paquete.id}')`}" 
                     style="${yaComprado ? '' : 'cursor: pointer;'}">
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
                        ${yaComprado ? '<div class="text-center mt-2"><span class="badge bg-warning">Ya comprado</span></div>' : ''}
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

        // Buscar el paquete en los disponibles (evitamos llamada al servidor si ya tenemos los datos)
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

    // Verificar saldo suficiente
    const saldoTexto = saldoCliente.textContent.replace(',', '');
    const saldo = parseFloat(saldoTexto) || 0;
    const costoFinal = paqueteSeleccionado.costoFinal || paqueteSeleccionado.costoBase || 0;

    if (saldo < costoFinal) {
        mostrarMensaje('Saldo insuficiente para realizar esta compra.', 'danger');
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
    document.getElementById('modalCostoPaquete').textContent = costoFinal.toFixed(2);
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

        // Realizar la compra en el servidor
        const formData = new FormData();
        formData.append('action', 'realizar-compra');
        formData.append('paquete', paqueteSeleccionado.id);
        formData.append('cliente', clienteActual);
        formData.append('validezDias', (paqueteSeleccionado.vigenciaDias || 0).toString());
        formData.append('fechaCompra', fechaCompra.toISOString().split('T')[0]);
        formData.append('costo', costoFinal.toString());

        const response = await fetch('compra-paquete', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Error en la compra');
        }

        const result = await response.json();

        if (result.success) {
            // Mostrar mensaje de éxito
            mostrarMensaje(`¡Compra realizada con éxito! El paquete "${paqueteSeleccionado.nombre}" ha sido agregado a tu cuenta. Vence el ${fechaVencimiento.toLocaleDateString()}.`, 'success');

            // Actualizar saldo (simulado)
            const nuevoSaldo = parseFloat(saldoCliente.textContent.replace(',', '')) - costoFinal;
            saldoCliente.textContent = nuevoSaldo.toFixed(2);

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
                    <p class="mb-0">No has comprado ningún paquete aún.</p>
                    <small class="text-muted">Selecciona un paquete disponible para realizar tu primera compra.</small>
                </div>
            </div>
        `;
        return;
    }

    paquetesComprados.forEach(paquete => {
        const vigente = estaVigente(paquete);
        const fechaCompra = paquete.fechaCompra ? new Date(paquete.fechaCompra) : new Date();
        const fechaVencimiento = paquete.fechaVencimiento ? new Date(paquete.fechaVencimiento) : new Date();

        const paqueteHTML = `
            <div class="col-md-6">
                <div class="card ${vigente ? 'border-success' : 'border-secondary'} h-100">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start">
                            <h6 class="card-title">${paquete.nombre || 'Sin nombre'}</h6>
                            <span class="badge ${vigente ? 'bg-success' : 'bg-secondary'}">${vigente ? 'Vigente' : 'Vencido'}</span>
                        </div>
                        <p class="small text-muted mb-2">
                            <i class="bi bi-calendar-check"></i> Comprado: ${fechaCompra.toLocaleDateString()}
                        </p>
                        <p class="small text-muted mb-2">
                            <i class="bi bi-calendar-x"></i> Vence: ${fechaVencimiento.toLocaleDateString()}
                        </p>
                        <div class="mt-2">
                            <small><strong><i class="bi bi-geo-route"></i> Rutas:</strong> ${paquete.cantidadRutas || 0} incluidas</small>
                        </div>
                        <div class="mt-2">
                            <small><strong><i class="bi bi-currency-dollar"></i> Costo:</strong> $${(paquete.costo || 0).toFixed(2)}</small>
                        </div>
                        ${vigente ?
            `<div class="mt-3">
                                <button class="btn btn-outline-primary btn-sm w-100" onclick="utilizarPaquete('${paquete.id}')">
                                    <i class="bi bi-airplane"></i> Utilizar Paquete
                                </button>
                            </div>`
            : ''}
                    </div>
                </div>
            </div>
        `;
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
            // window.location.href = `reserva-vuelo.jsp?paquete=${id}`;
            console.log(`Redirigiendo a reservas con paquete: ${id}`);
        }, 2000);

    } else {
        mostrarMensaje('Este paquete no está disponible para usar.', 'warning');
    }
}

// Función para establecer el cliente actual (puede llamarse desde session-manager.js)
function establecerClienteActual(nickname, nombreCompleto) {
    clienteActual = nickname;
    console.log('👤 Cliente establecido:', clienteActual);

    // Actualizar la interfaz con el nombre del cliente
    const clienteInfo = document.querySelector('.alert-info strong');
    if (clienteInfo && nombreCompleto) {
        clienteInfo.textContent = `${nombreCompleto} (${nickname})`;
    }

    // Recargar datos del cliente
    if (clienteActual) {
        cargarPaquetesComprados();
    }
}

// Función para debug
function debugEstado() {
    console.log('=== 🐛 DEBUG COMPRA PAQUETES ===');
    console.log('Cliente actual:', clienteActual);
    console.log('Paquetes disponibles:', paquetesDisponibles);
    console.log('Paquetes comprados:', paquetesComprados);
    console.log('Paquete seleccionado:', paqueteSeleccionado);
    console.log('================================');
}

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
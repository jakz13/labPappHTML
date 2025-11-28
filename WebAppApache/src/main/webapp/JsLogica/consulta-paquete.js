// Variables globales
let paquetes = {};

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Inicializando consulta de paquetes...');
    inicializarConsultaPaquetes();
});

async function inicializarConsultaPaquetes() {
    try {
        console.log('📦 Cargando paquetes desde backend...');
        await cargarPaquetesDesdeBackend();

        console.log('⚙️ Configurando interfaz...');
        configurarInterfaz();

        console.log('✅ Consulta de paquetes inicializada correctamente');
    } catch (error) {
        console.error('❌ Error en inicialización:', error);
        mostrarError('Error al inicializar la consulta de paquetes: ' + error.message);
    }
}

async function cargarPaquetesDesdeBackend() {
    try {
        console.log('🌐 Haciendo fetch a /api/consulta-paquete...');

        const response = await fetch('consulta-paquete?action=listar-paquetes');
        console.log('📨 Response status:', response.status);
        console.log('📨 Response ok:', response.ok);

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status} - ${response.statusText}`);
        }

        const text = await response.text();
        console.log('📄 Response text:', text);

        let paquetesData;
        try {
            paquetesData = JSON.parse(text);
        } catch (parseError) {
            console.error('❌ Error parseando JSON:', parseError);
            throw new Error('Respuesta del servidor no es JSON válido');
        }

        console.log('📊 Paquetes cargados del backend:', paquetesData);

        // Si no hay paquetes en la base de datos
        if (!paquetesData || paquetesData.length === 0) {
            console.log('📭 No hay paquetes en la BD');
            mostrarMensajeSinPaquetes();
            return;
        }

        // Usar directamente los datos del servidor - SIN TRANSFORMACIÓN COMPLEJA
        paquetes = {};
        paquetesData.forEach(paquete => {
            console.log('📋 Procesando paquete:', paquete);

            // Solo agregar propiedades que necesitamos para la lista
            paquetes[paquete.id] = {
                id: paquete.id,
                nombre: paquete.nombre || 'Sin nombre',
                costo: paquete.costoBase || 0, // Usar costoBase directamente
                costoFinal: calcularCostoFinalCliente(paquete.costoBase, paquete.descuento),
                vigencia: (paquete.vigenciaDias || 0) + " días",
                descripcion: paquete.descripcion || "Sin descripción",
                descuento: paquete.descuento || 0,
                vigenciaDias: paquete.vigenciaDias || 0,
                cantidadRutas: paquete.cantidadRutas || 0,
                rutas: paquete.rutas || [] // Usar rutas directamente del servidor
            };
        });

        console.log('🎯 Paquetes procesados:', paquetes);

    } catch (error) {
        console.error('💥 Error cargando paquetes:', error);
        mostrarError('No se pudieron cargar los paquetes: ' + error.message);
    }
}

function calcularCostoFinalCliente(costoBase, descuento) {
    if (!costoBase || !descuento) return costoBase || 0;
    return costoBase * (1 - descuento / 100.0);
}

function generarBeneficios(paquete) {
    const beneficios = [];

    if (paquete.descuento > 0) {
        beneficios.push(paquete.descuento + "% de descuento");
    }

    if (paquete.vigenciaDias > 0) {
        beneficios.push("Vigencia de " + paquete.vigenciaDias + " días");
    }

    // Usar rutas directamente del paquete
    if (paquete.rutas && paquete.rutas.length > 0) {
        beneficios.push(paquete.rutas.length + " rutas incluidas");
    }

    // Beneficios por defecto
    if (beneficios.length === 0) {
        beneficios.push("Paquete con múltiples destinos");
        beneficios.push("Precio especial");
        beneficios.push("Flexibilidad en fechas");
    }

    return beneficios;
}

function configurarInterfaz() {
    const paqueteSelect = document.getElementById("paqueteSelect");
    const infoPaquete = document.getElementById("infoPaquete");
    const infoRuta = document.getElementById("infoRuta");

    console.log('🔄 Configurando interfaz...');
    console.log('📋 Número de paquetes:', Object.keys(paquetes).length);

    // Limpiar el select
    paqueteSelect.innerHTML = '<option value="">Seleccione un paquete...</option>';

    if (Object.keys(paquetes).length === 0) {
        console.log('📭 No hay paquetes para mostrar en el select');
        paqueteSelect.innerHTML = '<option value="">No hay paquetes disponibles</option>';
        return;
    }

    // Llenar con paquetes reales
    Object.keys(paquetes).forEach(paqueteId => {
        const paquete = paquetes[paqueteId];
        const option = document.createElement('option');
        option.value = paqueteId;
        option.textContent = paquete.nombre;
        paqueteSelect.appendChild(option);
    });

    console.log('✅ Select poblado con', Object.keys(paquetes).length, 'paquetes');

    // Configurar event listener
    paqueteSelect.addEventListener("change", function() {
        const paqueteId = this.value;
        console.log('🎯 Paquete seleccionado:', paqueteId);

        if (paqueteId && paquetes[paqueteId]) {
            mostrarInformacionPaquete(paquetes[paqueteId]);
        } else {
            infoPaquete.style.display = "none";
            infoRuta.style.display = "none";
        }
    });

    // Configurar tooltips
    try {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        const tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
        console.log('🔧 Tooltips configurados');
    } catch (tooltipError) {
        console.warn('⚠️ No se pudieron cargar tooltips:', tooltipError);
    }
}

function mostrarMensajeSinPaquetes() {
    const paqueteSelect = document.getElementById("paqueteSelect");
    const infoPaquete = document.getElementById("infoPaquete");

    console.log('📭 Mostrando mensaje de no hay paquetes');

    paqueteSelect.innerHTML = '<option value="">No hay paquetes disponibles</option>';
    infoPaquete.innerHTML = `
        <div class="alert alert-info">
            <h5>No hay paquetes disponibles</h5>
            <p>Actualmente no hay paquetes de rutas de vuelo registrados en el sistema.</p>
            <p>Por favor, contacte al administrador o vuelva más tarde.</p>
        </div>
    `;
    infoPaquete.style.display = "block";
}

function mostrarInformacionPaquete(paquete) {
    console.log('📖 Mostrando información del paquete:', paquete);

    const infoPaquete = document.getElementById("infoPaquete");
    const infoRuta = document.getElementById("infoRuta");

    // Mostrar información del paquete
    infoPaquete.style.display = "block";
    document.getElementById("nombrePaquete").textContent = paquete.nombre;
    document.getElementById("costoPaquete").textContent = `${paquete.costoFinal.toFixed(2)}`;
    document.getElementById("vigenciaPaquete").textContent = paquete.vigencia;
    document.getElementById("cantidadRutas").textContent = paquete.cantidadRutas + " rutas";
    document.getElementById("descripcionPaquete").textContent = paquete.descripcion;

    // Mostrar beneficios
    const beneficiosList = document.getElementById("beneficiosPaquete");
    beneficiosList.innerHTML = "";
    const beneficios = generarBeneficios(paquete);
    beneficios.forEach(beneficio => {
        const li = document.createElement("li");
        li.innerHTML = `<i class="bi bi-check-circle-fill text-success"></i> ${beneficio}`;
        beneficiosList.appendChild(li);
    });

    // Mostrar rutas - USANDO RUTAS DIRECTAMENTE DEL PAQUETE
    const rutasContainer = document.getElementById("rutasPaquete");
    rutasContainer.innerHTML = "";

    if (paquete.rutas && paquete.rutas.length > 0) {
        console.log('🛣️ Mostrando', paquete.rutas.length, 'rutas');
        paquete.rutas.forEach((ruta, index) => {
            const rutaHTML = `
                <div class="col-md-6">
                    <div class="ruta-item card h-100" onclick="mostrarRutaDetalle('${paquete.id}', '${ruta.id}')" style="cursor: pointer;">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <h6 class="mb-1">${ruta.nombre || 'Sin nombre'}</h6>
                                    <p class="mb-1 text-muted small">${ruta.descripcionCorta || 'Sin descripción'}</p>
                                    <p class="mb-0 small">
                                        <strong>${ruta.aerolinea || 'Aerolínea no especificada'}</strong> | 
                                        ${ruta.origen || 'N/A'} → ${ruta.destino || 'N/A'}
                                    </p>
                                    <p class="mb-0 small text-success">
                                        ${ruta.cantidadAsientos} asientos ${ruta.tipoAsiento || ''} - 
                                        $${(ruta.tipoAsiento === 'EJECUTIVO' ? ruta.costoEjecutivo : ruta.costoTurista) || 0}
                                    </p>
                                </div>
                                <span class="badge bg-primary">${ruta.id || 'N/A'}</span>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            rutasContainer.innerHTML += rutaHTML;
        });
    } else {
        console.log('🛑 No hay rutas para mostrar');
        rutasContainer.innerHTML = `
            <div class="col-12">
                <div class="alert alert-warning text-center">
                    <p class="mb-0">No hay rutas disponibles para este paquete.</p>
                </div>
            </div>
        `;
    }

    // Ocultar información de ruta
    infoRuta.style.display = "none";

    console.log('✅ Información del paquete mostrada correctamente');
}

async function mostrarRutaDetalle(paqueteId, rutaId) {
    try {
        console.log(`🔍 Solicitando detalle de ruta: paquete=${paqueteId}, ruta=${rutaId}`);

        const response = await fetch(`consulta-paquete?action=obtener-ruta&paquete=${encodeURIComponent(paqueteId)}&ruta=${encodeURIComponent(rutaId)}`);

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const ruta = await response.json();
        console.log('📄 Detalle de ruta recibido:', ruta);

        actualizarInterfazRuta(ruta);

    } catch (error) {
        console.error('💥 Error cargando detalle de ruta:', error);
        mostrarError('No se pudo cargar la información detallada de la ruta: ' + error.message);
    }
}

function actualizarInterfazRuta(ruta) {
    console.log('🎨 Actualizando interfaz de ruta:', ruta);

    // Actualizar todos los campos
    document.getElementById("rutaNombre").textContent = ruta.nombre || "Sin nombre";
    document.getElementById("rutaDescripcionCorta").textContent = ruta.descripcionCorta || "Sin descripción corta";
    document.getElementById("rutaDescripcionCompleta").textContent = ruta.descripcionCompleta || ruta.descripcion || "Sin descripción completa";
    document.getElementById("rutaAerolinea").textContent = ruta.aerolinea || "No especificada";
    document.getElementById("rutaOrigen").textContent = ruta.origen || "No especificado";
    document.getElementById("rutaDestino").textContent = ruta.destino || "No especificado";
    document.getElementById("rutaDuracion").textContent = ruta.duracion || "No especificada";
    document.getElementById("rutaHoraSalida").textContent = ruta.horaSalida || "No especificada";
    document.getElementById("rutaTurista").textContent = ruta.costoTurista ? `${ruta.costoTurista}` : "No disponible";
    document.getElementById("rutaEjecutivo").textContent = ruta.costoEjecutivo ? `${ruta.costoEjecutivo}` : "No disponible";
    document.getElementById("rutaEquipaje").textContent = ruta.costoEquipaje ? `${ruta.costoEquipaje}` : "No disponible";
    document.getElementById("rutaCategorias").textContent = ruta.categorias || "No especificadas";
    document.getElementById("rutaEstado").textContent = ruta.estado || "No especificado";

    // Manejar la imagen
    const imagenRuta = document.getElementById("rutaImagen");
    if (ruta.imagen) {
        imagenRuta.src = ruta.imagen;
        imagenRuta.alt = `Imagen de la ruta ${ruta.nombre || ''}`;
    } else {
        imagenRuta.src = "https://via.placeholder.com/400x250/6c757d/ffffff?text=Imagen+No+Disponible";
        imagenRuta.alt = "Imagen no disponible";
    }

    // Mostrar sección de información de ruta
    document.getElementById("infoRuta").style.display = "block";

    // Remover selección anterior y marcar actual
    document.querySelectorAll('.ruta-item').forEach(item => {
        item.classList.remove('border-primary', 'shadow');
    });

    // Scroll a la información de la ruta
    document.getElementById("infoRuta").scrollIntoView({ behavior: 'smooth' });

    console.log('✅ Interfaz de ruta actualizada correctamente');

    // Incrementar visitas en el backend (best-effort) cuando se muestra el detalle de la ruta desde un paquete
    (async () => {
        try {
            const nombre = ruta.nombre || ruta.id || '';
            if (!nombre) return;
            const contextPath = window.CONTEXT_PATH || '';
            const incUrl = `${contextPath}/incrementarVisitasRuta?nombreRuta=${encodeURIComponent(nombre)}`;
            const controller = new AbortController();
            const timeout = setTimeout(() => controller.abort(), 800);
            try {
                await fetch(incUrl, { method: 'POST', credentials: 'include', signal: controller.signal });
                console.log('🔼 Visitas incrementadas (paquete) para ruta:', nombre);
            } catch (e) {
                console.warn('⚠️ No se pudo incrementar visitas (paquete, ruta):', e);
            } finally {
                clearTimeout(timeout);
            }
        } catch (e) {
            console.warn('⚠️ Error iniciando incremento de visitas (paquete):', e);
        }
    })();
}

function consultarVuelos() {
    const rutaNombre = document.getElementById("rutaNombre").textContent;
    console.log('✈️ Consultando vuelos para:', rutaNombre);

    const modalHTML = `
        <div class="modal fade" id="vuelosModal" tabindex="-1" aria-labelledby="vuelosModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="vuelosModalLabel">Consultar Vuelos</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body text-center">
                        <div class="mb-3">
                            <i class="bi bi-airplane text-primary" style="font-size: 3rem;"></i>
                        </div>
                        <h5>Redirigiendo a Consulta de Vuelos</h5>
                        <p>Será redirigido a la página de consulta de vuelos para:</p>
                        <p class="fw-bold">${rutaNombre}</p>
                        <p class="text-muted small">Los filtros se aplicarán automáticamente para mostrar los vuelos disponibles en esta ruta.</p>
                    </div>
                    <div class="modal-footer justify-content-center">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <a href="/consulta-vuelo.jsp" class="btn btn-primary">Continuar</a>
                    </div>
                </div>
            </div>
        </div>
    `;

    if (!document.getElementById('vuelosModal')) {
        document.body.insertAdjacentHTML('beforeend', modalHTML);
    }

    const vuelosModal = new bootstrap.Modal(document.getElementById('vuelosModal'));
    vuelosModal.show();
}

function mostrarError(mensaje) {
    console.error('💥 Mostrando error:', mensaje);

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

    const toastContainer = document.getElementById('toastContainer') || crearToastContainer();
    toastContainer.innerHTML = toastHTML;
    const toastElement = toastContainer.querySelector('.toast');
    const toast = new bootstrap.Toast(toastElement);
    toast.show();
}

function crearToastContainer() {
    const container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container position-fixed top-0 end-0 p-3';
    container.style.zIndex = '9999';
    document.body.appendChild(container);
    return container;
}

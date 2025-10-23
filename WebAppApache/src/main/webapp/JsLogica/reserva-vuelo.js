// Variables globales
let vueloSeleccionado = null;
let sessionUser = null; // info de sesión (nombre, apellido, nickname, tipo)
let paquetesElegiblesGlobal = []; // lista de paquetes reales que el backend indica como elegibles

// Inicialización
document.addEventListener('DOMContentLoaded', async function() {
    await cargarSesionUsuario();
    cargarAerolineas();
    configurarEventListeners();
    inicializarValidacion();

    // Renderizar pasajeros inicial según el valor por defecto del input
    const cantidadInicial = parseInt(document.getElementById('cantidadPasajes').value || '1', 10);
    renderPasajeros(cantidadInicial);
});

// Escuchar cambios de sesión emitidos por session-manager (login sin recarga)
window.addEventListener('sessionUpdated', async function() {
    try {
        await cargarSesionUsuario();
        const cantidad = parseInt(document.getElementById('cantidadPasajes').value || '1', 10);
        renderPasajeros(cantidad);
    } catch (e) {
        console.warn('Error al actualizar sesión en reserva-vuelo:', e);
    }
});

// Cargar información de sesión del servidor o window.CURRENT_SESSION
async function cargarSesionUsuario() {
    try {
        const base = window.SESSION_API_BASE || '';

        // Si la sesión ya fue verificada por session-manager y está almacenada, intentar obtener detalle
        if (window.CURRENT_SESSION && window.CURRENT_SESSION.authenticated) {
            try {
                const resUser = await fetch(base + '/api/usuario/actual', { credentials: 'include' });
                if (resUser.ok) {
                    const userData = await resUser.json();
                    if (userData.success) {
                        sessionUser = userData; // contiene nombre y apellido entre otros
                    } else {
                        sessionUser = window.CURRENT_SESSION; // fallback
                    }
                } else {
                    sessionUser = window.CURRENT_SESSION;
                }
            } catch (e) {
                console.warn('No se pudo obtener usuario actual desde servidor:', e);
                sessionUser = window.CURRENT_SESSION;
            }
            // re-renderizar pasajeros con la información de sesión cargada
            try {
                const cantidadInicial = parseInt(document.getElementById('cantidadPasajes').value || '1', 10);
                renderPasajeros(cantidadInicial);
            } catch (e) { /* ignore */ }
            return;
        }

        // Si no hay window.CURRENT_SESSION, consultar check-session y luego usuario/actual
        const res = await fetch(base + '/api/check-session', { credentials: 'include' });
        if (res.ok) {
            const data = await res.json();
            if (data && data.authenticated) {
                // obtener datos completos del usuario (nombre, apellido, etc.)
                try {
                    const resUser = await fetch(base + '/api/usuario/actual', { credentials: 'include' });
                    if (resUser.ok) {
                        const userData = await resUser.json();
                        sessionUser = userData.success ? userData : data;
                    } else {
                        sessionUser = data;
                    }
                } catch (e) {
                    console.warn('Error al obtener usuario actual:', e);
                    sessionUser = data;
                }
            } else {
                sessionUser = null;
            }
        } else {
            sessionUser = null;
        }

        // Si cargamos sesión, volver a renderizar los formularios de pasajeros
        try {
            const cantidadInicial = parseInt(document.getElementById('cantidadPasajes').value || '1', 10);
            renderPasajeros(cantidadInicial);
        } catch (e) { /* ignore */ }

    } catch (e) {
        console.warn('No se pudo obtener sesión:', e);
        sessionUser = null;
    }
}

// Cargar aerolíneas desde backend
function cargarAerolineas() {
    fetch('api/aerolineas')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('aerolinea');
            select.innerHTML = '<option value="">Seleccione aerolínea...</option>';
            data.forEach(a => {
                select.innerHTML += `<option value="${a.nickname}">${a.nombre}</option>`;
            });
            select.disabled = false;
        })
        .catch(err => {
            console.error("Error al cargar aerolíneas:", err);
        });
}

function configurarEventListeners() {
    // Aerolínea -> rutas
    document.getElementById('aerolinea').addEventListener('change', function() {
        const aerolinea = this.value;
        const rutaSelect = document.getElementById('rutaVuelo');
        const vueloSelect = document.getElementById('vuelo');
        rutaSelect.innerHTML = '<option value="">Cargando rutas...</option>';
        rutaSelect.disabled = true;
        vueloSelect.innerHTML = '<option value="">Seleccione una ruta primero</option>';
        vueloSelect.disabled = true;
        document.getElementById('btnSiguiente1').disabled = true;
        document.getElementById('infoVuelo').style.display = 'none';
        vueloSeleccionado = null;

        if (aerolinea) {
            fetch('api/rutas?aerolinea=' + encodeURIComponent(aerolinea))
                .then(res => res.json())
                .then(data => {
                    rutaSelect.innerHTML = '<option value="">Seleccione ruta...</option>';
                    data.forEach(r => {
                        rutaSelect.innerHTML += `<option value="${r.nombre}">${r.nombre}</option>`;
                    });
                    rutaSelect.disabled = false;
                });
        }
    });

    // Ruta -> vuelos
    document.getElementById('rutaVuelo').addEventListener('change', function() {
        const ruta = this.value;
        const vueloSelect = document.getElementById('vuelo');
        vueloSelect.innerHTML = '<option value="">Cargando vuelos...</option>';
        vueloSelect.disabled = true;
        document.getElementById('btnSiguiente1').disabled = true;
        document.getElementById('infoVuelo').style.display = 'none';
        vueloSeleccionado = null;

        if (ruta) {
            fetch('api/vuelos?ruta=' + encodeURIComponent(ruta))
                .then(res => res.json())
                .then(data => {
                    vueloSelect.innerHTML = '<option value="">Seleccione vuelo...</option>';
                    data.forEach(v => {
                        vueloSelect.innerHTML += `<option value="${v.nombre}">${v.nombre}</option>`;
                    });
                    vueloSelect.disabled = false;
                });
        }
    });

    // Vuelo -> mostrar info y habilitar botón
    document.getElementById('vuelo').addEventListener('change', function() {
        const vuelo = this.value;
        const btnSiguiente = document.getElementById('btnSiguiente1');
        const infoVueloDiv = document.getElementById('infoVuelo');
        const infoVueloCard = infoVueloDiv.querySelector('.card-body');

        if (vuelo) {
            fetch('api/vuelo?nombre=' + encodeURIComponent(vuelo))
                .then(res => res.json())
                .then(data => {
                    vueloSeleccionado = data;
                    const nombre = data.nombre || '-';
                    const fecha = data.fecha || '-';
                    const duracion = data.duracion !== undefined ? data.duracion : '-';
                    const asientosTurista = data.asientosTurista !== undefined ? data.asientosTurista : '-';
                    const asientosEjecutivo = data.asientosEjecutivo !== undefined ? data.asientosEjecutivo : '-';

                    infoVueloCard.innerHTML = `
        <div class="row">
            <div class="col-md-12">
                <h5 class="text-primary mb-2">${nombre}</h5>
                <p class="mb-1"><strong>Fecha:</strong> ${fecha}</p>
                <p class="mb-1"><strong>Duración:</strong> ${duracion}</p>
                <p class="mb-1"><strong>Asientos Turista:</strong> ${asientosTurista} &nbsp; <strong>Ejecutivo:</strong> ${asientosEjecutivo}</p>
            </div>
        </div>
    `;
                    infoVueloDiv.style.display = 'block';
                    btnSiguiente.disabled = false;
                })
                .catch(() => {
                    infoVueloDiv.style.display = 'none';
                    btnSiguiente.disabled = true;
                    vueloSeleccionado = null;
                });
        } else {
            infoVueloDiv.style.display = 'none';
            btnSiguiente.disabled = true;
            vueloSeleccionado = null;
        }
    });

    // Gestión de pasajeros (usar la función renderPasajeros)
    document.getElementById('cantidadPasajes').addEventListener('input', function() {
        const cantidad = parseInt(this.value) || 1;
        renderPasajeros(cantidad);
        calcularCostos();
    });

    // Gestión de forma de pago
    document.getElementById('formaPago').addEventListener('change', function() {
        const selectorPaquete = document.getElementById('selectorPaquete');
        selectorPaquete.style.display = this.value === 'paquete' ? 'block' : 'none';
        // Si el usuario selecciona pago con paquete, pedir al backend los paquetes reales elegibles
        if (this.value === 'paquete') {
            fetchPaquetesElegibles();
        } else {
            // limpiar lista previa
            paquetesElegiblesGlobal = [];
            const paqueteSelect = document.getElementById('paqueteSelect');
            if (paqueteSelect) {
                paqueteSelect.innerHTML = '<option value="">Seleccione un paquete...</option>';
                paqueteSelect.disabled = true;
            }
        }
        calcularCostos();
    });

    // Event listeners para cálculos automáticos
    document.getElementById('tipoAsiento').addEventListener('change', calcularCostos);
    document.getElementById('equipajeExtra').addEventListener('input', calcularCostos);
    document.getElementById('paqueteSelect').addEventListener('change', function() { calcularCostos(); });

    // Envío del formulario
    document.getElementById('formReservaVuelo').addEventListener('submit', function(event) {
        event.preventDefault();
        event.stopPropagation();

        if (this.checkValidity() && validarPaso2() && document.getElementById('confirmarReserva').checked) {
            // recolectar datos de pasajeros
            const pasajeroCards = Array.from(document.querySelectorAll('#pasajerosContainer .pasajero-card'));
            const pasajeros = [];

            // Si hay sesión, el primer pasajero es el usuario autenticado (manejado internamente)
            if (sessionUser) {
                const nombreSesion = (sessionUser.nombre || sessionUser.firstName || '').trim();
                const apellidoSesion = (sessionUser.apellido || sessionUser.lastName || '').trim();
                pasajeros.push({ nombre: nombreSesion || (sessionUser.nickname || '').split(' ')[0], apellido: apellidoSesion || '' });
            }

            // Añadir los pasajeros provenientes de los formularios (son los adicionales si hay sesión)
            pasajeroCards.forEach(card => {
                const nombreInput = card.querySelector('input[name="pasajero-nombre"]');
                const apellidoInput = card.querySelector('input[name="pasajero-apellido"]');
                const nombre = nombreInput ? nombreInput.value.trim() : '';
                const apellido = apellidoInput ? apellidoInput.value.trim() : '';
                pasajeros.push({ nombre, apellido });
            });

            const datosReserva = {
                vuelo: vueloSeleccionado ? vueloSeleccionado.nombre : null,
                tipoAsiento: document.getElementById('tipoAsiento').value,
                cantidadPasajes: parseInt(document.getElementById('cantidadPasajes').value, 10) || 1,
                equipajeExtra: parseInt(document.getElementById('equipajeExtra').value, 10) || 0,
                formaPago: document.getElementById('formaPago').value,
                paquete: document.getElementById('formaPago').value === 'paquete' ? document.getElementById('paqueteSelect').value : null,
                pasajeros: pasajeros
            };

            // Validación adicional en cliente: si se eligió pago con paquete, asegurar que el paquete seleccionado
            // esté en la lista de paquetes elegibles provista por el backend
            if (datosReserva.formaPago === 'paquete') {
                const idSel = datosReserva.paquete;
                const found = paquetesElegiblesGlobal.find(p => p.id === idSel || p.nombre === idSel);
                if (!found) {
                    mostrarMensajeError('El paquete seleccionado no es válido para este vuelo. Por favor elija otro paquete.');
                    return;
                }
            }

            const submitBtn = this.querySelector('button[type="submit"]');
            if (submitBtn) submitBtn.disabled = true;

            fetch('api/reservas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosReserva),
                credentials: 'include'
            })
                .then(async res => {
                    // Intentar parsear JSON devuelto por el servidor para mostrar mensaje claro
                    let text = await res.text();
                    let json;
                    try { json = text ? JSON.parse(text) : null; } catch (e) { json = null; }

                    console.log('Respuesta /api/reservas status=', res.status, 'json=', json, 'text=', text);

                    // Si el servidor devolvió success aunque el status sea != OK, lo aceptamos
                    if (json && json.success === true) {
                        return json;
                    }

                    if (!res.ok) {
                        const serverMsg = json && json.error ? json.error : (json && json.message ? json.message : 'Error al registrar la reserva');
                        mostrarMensajeError(serverMsg);
                        throw new Error(serverMsg);
                    }

                    // si está OK pero no tiene success:true, devolver lo que haya
                    return json;
                })
                .then(data => {
                    // Re-habilitar botón
                    if (submitBtn) submitBtn.disabled = false;

                    // Asegurarse de que el servidor devolvió success:true
                    if (!data || data.success !== true) {
                        const serverMsg = data && data.error ? data.error : 'Error al registrar la reserva';
                        mostrarMensajeError(serverMsg);
                        return;
                    }

                    mostrarModalExito(data.codigoReserva || data.codigo);
                    this.classList.remove('was-validated');
                })
                .catch(err => {
                    if (submitBtn) submitBtn.disabled = false;
                    if (!err || !err.message) {
                        mostrarMensajeError('No se pudo registrar la reserva. Intente nuevamente.');
                    }
                });
        } else {
            if (!document.getElementById('confirmarReserva').checked) {
                mostrarMensajeError('Debe confirmar que los datos son correctos antes de realizar la reserva.');
            }
            event.stopPropagation();
        }

        this.classList.add('was-validated');
    });
}

// Nueva función: solicita paquetes elegibles al backend y puebla el select
function fetchPaquetesElegibles() {
    const paqueteSelect = document.getElementById('paqueteSelect');
    if (!paqueteSelect) return;

    // Asegurarse de que el usuario esté autenticado y tengamos datos de sesión
    if (!sessionUser || (sessionUser && sessionUser.authenticated === false)) {
        // intentar cargar sesión desde servidor
        cargarSesionUsuario().then(() => {
            if (!sessionUser) {
                paqueteSelect.innerHTML = '<option value="">Debe iniciar sesión</option>';
                paqueteSelect.disabled = true;
                mostrarMensajeError('Debe iniciar sesión con una cuenta de cliente para usar un paquete.');
                return;
            } else {
                // reintentar la función ahora que la sesión podría estar disponible
                fetchPaquetesElegibles();
            }
        }).catch(() => {
            paqueteSelect.innerHTML = '<option value="">Debe iniciar sesión</option>';
            paqueteSelect.disabled = true;
            mostrarMensajeError('Debe iniciar sesión con una cuenta de cliente para usar un paquete.');
        });
        return;
    }

    // Requerir que exista un vuelo seleccionado para pedir paquetes relevantes
    if (!vueloSeleccionado || !vueloSeleccionado.nombre) {
        paqueteSelect.innerHTML = '<option value="">Seleccione un vuelo primero</option>';
        paqueteSelect.disabled = true;
        mostrarMensajeError('Seleccione un vuelo válido antes de elegir pago con paquete.');
        return;
    }

    paqueteSelect.innerHTML = '<option value="">Cargando paquetes...</option>';
    paqueteSelect.disabled = true;

    const payload = {
        vuelo: vueloSeleccionado.nombre,
        tipoAsiento: document.getElementById('tipoAsiento').value || 'turista',
        cantidadPasajes: parseInt(document.getElementById('cantidadPasajes').value, 10) || 1,
        equipajeExtra: parseInt(document.getElementById('equipajeExtra').value, 10) || 0,
        formaPago: 'paquete'
    };

    fetch('api/reservas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
        credentials: 'include'
    })
    .then(async res => {
        const text = await res.text();
        let json = null;
        try { json = text ? JSON.parse(text) : null; } catch (e) { json = null; }

        if (!res.ok) {
            const errMsg = json && json.error ? json.error : 'No se pudieron obtener paquetes elegibles';
            paquetesElegiblesGlobal = [];
            paqueteSelect.innerHTML = '<option value="">No hay paquetes disponibles</option>';
            paqueteSelect.disabled = true;
            mostrarMensajeError(errMsg);
            return;
        }

        if (json && json.success === true && Array.isArray(json.paquetesElegibles)) {
            paquetesElegiblesGlobal = json.paquetesElegibles.map(p => ({
                id: p.id || p.nombre,
                nombre: p.nombre || p.id,
                descuentoPorc: Number(p.descuentoPorc || 0),
                descripcion: p.descripcion || '',
                fechaCompra: p.fechaCompra || '',
                fechaVencimiento: p.fechaVencimiento || ''
            }));

            // Poblar select
            if (paquetesElegiblesGlobal.length === 0) {
                paqueteSelect.innerHTML = '<option value="">No hay paquetes elegibles</option>';
                paqueteSelect.disabled = true;
                mostrarMensajeError('No posee paquetes elegibles para este vuelo.');
            } else {
                paqueteSelect.innerHTML = '<option value="">Seleccione un paquete...</option>';
                paquetesElegiblesGlobal.forEach(p => {
                    const label = `${p.nombre} (${p.descuentoPorc}% - ${p.descripcion || 'sin descripción'})`;
                    paqueteSelect.innerHTML += `<option value="${escapeHtml(p.id)}">${escapeHtml(label)}</option>`;
                });
                paqueteSelect.disabled = false;
            }

            // actualizar costos en UI usando info del servidor
            const costoServidor = (json.costoServidor !== undefined) ? Number(json.costoServidor) : null;
            if (costoServidor !== null) {
                // almacenar temporalmente si queremos mostrarlo (no necesario)
            }
            calcularCostos();
        } else {
            paquetesElegiblesGlobal = [];
            paqueteSelect.innerHTML = '<option value="">No hay paquetes elegibles</option>';
            paqueteSelect.disabled = true;
            mostrarMensajeError('No posee paquetes elegibles para este vuelo.');
        }
    })
    .catch(err => {
        paquetesElegiblesGlobal = [];
        paqueteSelect.innerHTML = '<option value="">Error al cargar paquetes</option>';
        paqueteSelect.disabled = true;
        mostrarMensajeError('Error al obtener paquetes elegibles. Intente nuevamente.');
    });
}

// Función para renderizar los formularios de pasajeros
function renderPasajeros(cantidad) {
    const container = document.getElementById('pasajerosContainer');
    const pasajerosDiv = document.getElementById('pasajerosDiv');
    const autoDiv = document.getElementById('autocompletadoUsuario');
    container.innerHTML = '';

    if (!cantidad || cantidad < 1) {
        if (autoDiv) autoDiv.style.display = 'none';
        pasajerosDiv.style.display = 'none';
        return;
    }

    // Si hay sesión, el primer pasajero se maneja internamente (autocompletado)
    // y mostramos formularios solo para los pasajeros adicionales (cantidad - 1).
    // Si no hay sesión, mostramos formularios para todos los pasajeros.
    let formsToRender = cantidad;
    if (sessionUser && sessionUser.success !== false) {
        if (cantidad === 1) {
            // mostrar nota con el usuario autocompletado (no formularios)
            if (autoDiv) {
                const nombre = sessionUser.nombre ? escapeHtml(sessionUser.nombre) : (sessionUser.nickname || '');
                const apellido = sessionUser.apellido ? escapeHtml(sessionUser.apellido) : '';
                autoDiv.innerHTML = `<p class="text-light small">Primer pasajero: ${nombre} ${apellido} (usted)</p>`;
                autoDiv.style.display = 'block';
            }
            pasajerosDiv.style.display = 'none';
            return;
        }
        formsToRender = cantidad - 1;
    } else {
        if (autoDiv) autoDiv.style.display = 'none';
    }

    pasajerosDiv.style.display = 'block';

    for (let i = 1; i <= formsToRender; i++) {
        const index = sessionUser ? i + 1 : i; // si hay sesión, los formularios son para pasajeros 2..N
        const helperNote = sessionUser && i === 1 ? '<small class="text-muted">(El primer pasajero será usted — autocompletado)</small>' : '';

        const pasajeroHTML = document.createElement('div');
        pasajeroHTML.className = 'pasajero-card mb-3 p-3 border rounded bg-dark';
        pasajeroHTML.innerHTML = `
            <h6 class="mb-2 text-light">Pasajero ${index} ${helperNote}</h6>
            <div class="row g-2">
                <div class="col-md-6">
                    <input type="text" name="pasajero-nombre" class="form-control" placeholder="Nombre *" value="" required>
                </div>
                <div class="col-md-6">
                    <input type="text" name="pasajero-apellido" class="form-control" placeholder="Apellido *" value="" required>
                </div>
            </div>
        `;

        container.appendChild(pasajeroHTML);
    }
}

// helper para evitar inyección de HTML al insertar valores
function escapeHtml(str) {
    if (!str) return '';
    return String(str).replace(/[&<>\"']/g, function (c) {
        return {'&':'&amp;','<':'&lt;','>':'&gt;','\"':'&quot;',"'":'&#39;'}[c];
    });
}

// Navegación entre pasos
function nextStep(step) {
    if (step === 2 && !validarPaso1()) return;
    if (step === 3 && !validarPaso2()) return;

    document.querySelectorAll('.form-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById('section' + step).classList.add('active');

    document.querySelectorAll('.step').forEach(stepEl => {
        stepEl.classList.remove('active', 'completed');
    });

    for (let i = 1; i <= step; i++) {
        const stepEl = document.getElementById('step' + i);
        if (i === step) {
            stepEl.classList.add('active');
        } else {
            stepEl.classList.add('completed');
        }
    }

    if (step === 3) {
        generarResumenReserva();
    }
}

function prevStep(step) {
    nextStep(step);
}

// Validaciones
function validarPaso1() {
    if (vueloSeleccionado === null) {
        mostrarMensajeError('Por favor seleccione un vuelo para continuar.');
        return false;
    }
    return true;
}

// Validar que los datos de pasajeros estén completos
function validarPaso2() {
    const tipoAsiento = document.getElementById('tipoAsiento').value;
    const cantidadPasajes = parseInt(document.getElementById('cantidadPasajes').value, 10) || 1;
    const formaPago = document.getElementById('formaPago').value;

    if (!tipoAsiento || !cantidadPasajes || !formaPago) {
        mostrarMensajeError('Por favor complete todos los campos requeridos del paso 2.');
        return false;
    }

    if (formaPago === 'paquete' && !document.getElementById('paqueteSelect').value) {
        mostrarMensajeError('Por favor seleccione un paquete para el pago.');
        return false;
    }

    // validar pasajeros
    const pasajeroCards = Array.from(document.querySelectorAll('#pasajerosContainer .pasajero-card'));

    // Si hay sesión iniciada, el primer pasajero se autocompleta desde la cuenta,
    // por lo que se esperan `cantidadPasajes - 1` formularios visibles (mínimo 0).
    const expectedForms = sessionUser ? Math.max(0, cantidadPasajes - 1) : cantidadPasajes;

    if (pasajeroCards.length !== expectedForms) {
        mostrarMensajeError('La cantidad de formularios de pasajeros no coincide con la cantidad de pasajes.');
        return false;
    }

    for (const card of pasajeroCards) {
        const nombre = (card.querySelector('input[name="pasajero-nombre"]') || {}).value || '';
        const apellido = (card.querySelector('input[name="pasajero-apellido"]') || {}).value || '';
        if (!nombre.trim() || !apellido.trim()) {
            mostrarMensajeError('Por favor complete el nombre y apellido de todos los pasajeros.');
            return false;
        }
    }

    return true;
}

// Cálculo de costos (simulado, debes adaptar si tienes endpoint real)
function calcularCostos() {
    const resumenCostosEl = document.getElementById('resumenCostos');
    const costoPasajesEl = document.getElementById('costoPasajes');
    const costoEquipajeEl = document.getElementById('costoEquipaje');
    const descuentoPaqueteEl = document.getElementById('descuentoPaquete');
    const costoTotalEl = document.getElementById('costoTotal');

    // Valores de entrada
    const tipoAsiento = document.getElementById('tipoAsiento').value;
    const cantidadPasajes = parseInt(document.getElementById('cantidadPasajes').value, 10) || 1;
    const equipajeExtra = parseInt(document.getElementById('equipajeExtra').value, 10) || 0;
    const formaPago = document.getElementById('formaPago').value;
    const paqueteSeleccionado = document.getElementById('paqueteSelect') ? document.getElementById('paqueteSelect').value : '';

    // Precios por defecto si el vuelo no provee datos
    const DEFAULT_TURISTA = 100.0;
    const DEFAULT_EJECUTIVO = 200.0;
    const COSTO_EQUIPAJE_POR_UNIDAD = 25.0;

    // Obtener precios del vueloSeleccionado si están disponibles
    let precioTurista = DEFAULT_TURISTA;
    let precioEjecutivo = DEFAULT_EJECUTIVO;
    try {
        if (vueloSeleccionado) {
            if (typeof vueloSeleccionado.precioTurista === 'number') precioTurista = vueloSeleccionado.precioTurista;
            if (typeof vueloSeleccionado.precioEjecutivo === 'number') precioEjecutivo = vueloSeleccionado.precioEjecutivo;
            // También aceptar cadenas numéricas
            if (!isFinite(precioTurista) && vueloSeleccionado.precioTurista) precioTurista = parseFloat(vueloSeleccionado.precioTurista) || DEFAULT_TURISTA;
            if (!isFinite(precioEjecutivo) && vueloSeleccionado.precioEjecutivo) precioEjecutivo = parseFloat(vueloSeleccionado.precioEjecutivo) || DEFAULT_EJECUTIVO;
        }
    } catch (e) {
        // ignore y usar defaults
    }

    // Precio unitario según tipo de asiento
    let precioUnitario = tipoAsiento === 'ejecutivo' ? precioEjecutivo : precioTurista;
    if (!tipoAsiento) precioUnitario = precioTurista; // fallback visual

    // Cálculos
    const costoPasajes = Math.max(0, precioUnitario * cantidadPasajes);
    const costoEquipaje = Math.max(0, equipajeExtra * COSTO_EQUIPAJE_POR_UNIDAD);

    // Descuento por paquete: usar valor real si el paquete está en paquetesElegiblesGlobal
    let descuento = 0;
    if (formaPago === 'paquete' && paqueteSeleccionado) {
        const paqueteObj = paquetesElegiblesGlobal.find(p => p.id === paqueteSeleccionado || p.nombre === paqueteSeleccionado);
        if (paqueteObj) {
            descuento = (paqueteObj.descuentoPorc / 100.0) * costoPasajes;
        } else {
            // si no está en la lista local, no aplicar descuento (el backend validará)
            descuento = 0;
        }
    }

    const total = Math.max(0, costoPasajes + costoEquipaje - descuento);

    // Mostrar resultados con formato
    const format = v => '$' + Number(v).toFixed(2);
    if (costoPasajesEl) costoPasajesEl.textContent = format(costoPasajes);
    if (costoEquipajeEl) costoEquipajeEl.textContent = format(costoEquipaje);
    if (descuentoPaqueteEl) descuentoPaqueteEl.textContent = '-' + format(descuento);
    if (costoTotalEl) costoTotalEl.textContent = format(total);

    // Mostrar el panel de resumen de costos
    if (resumenCostosEl) resumenCostosEl.style.display = 'block';

    // Habilitar siguiente (se asume que validaciones adicionales se realizan en validarPaso2)
    const btnSiguiente2 = document.getElementById('btnSiguiente2');
    if (btnSiguiente2) btnSiguiente2.disabled = false;
}

// Generar resumen de reserva
function generarResumenReserva() {
    const resumen = document.getElementById('resumenReserva');
    const tipoAsiento = document.getElementById('tipoAsiento').value;
    const cantidadPasajes = document.getElementById('cantidadPasajes').value;
    const equipajeExtra = document.getElementById('equipajeExtra').value;
    const formaPago = document.getElementById('formaPago').value;

    resumen.innerHTML = `
        <p class="text-light"><strong>Vuelo:</strong> ${vueloSeleccionado ? vueloSeleccionado.nombre : ''}</p>
        <p class="text-light"><strong>Fecha:</strong> ${vueloSeleccionado ? vueloSeleccionado.fecha : ''}</p>
        <p class="text-light"><strong>Tipo de asiento:</strong> ${tipoAsiento}</p>
        <p class="text-light"><strong>Cantidad de pasajes:</strong> ${cantidadPasajes}</p>
        <p class="text-light"><strong>Equipaje extra:</strong> ${equipajeExtra} unidades</p>
        <p class="text-light"><strong>Forma de pago:</strong> ${formaPago === 'general' ? 'Pago General' : 'Pago con Paquete'}</p>
        <p class="text-light"><strong>Costo total:</strong> ${document.getElementById('costoTotal').textContent}</p>
    `;
}

function mostrarModalExito(codigoReserva) {
    let codigo = codigoReserva;
    if (!codigo) {
        codigo = 'RES-' + Math.floor(1000 + Math.random() * 9000) + '-' + new Date().getFullYear();
    }
    const el = document.getElementById('codigoReserva');
    if (el) el.textContent = codigo;

    const successModal = new bootstrap.Modal(document.getElementById('successModal'));
    successModal.show();
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

function inicializarValidacion() {
    const form = document.getElementById('formReservaVuelo');
    if (!form) return;
    // Ya usamos validación manual en el submit; esta función sólo evita el ReferenceError
    form.setAttribute('novalidate', '');
    form.addEventListener('submit', function (e) {
        // Si el formulario no es válido, prevenir y marcar
        if (!form.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
            form.classList.add('was-validated');
        }
    });
}
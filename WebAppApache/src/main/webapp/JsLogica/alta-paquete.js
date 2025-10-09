// Validación del formulario y funcionalidad
document.addEventListener('DOMContentLoaded', function() {
    // Establecer fecha actual como fecha de alta por defecto
    document.getElementById('fechaAlta').valueAsDate = new Date();

    // Cargar lista de paquetes para la pestaña de agregar rutas
    cargarPaquetesDisponibles();

    // Cargar rutas disponibles
    cargarRutasDisponibles();

    // Inicializar validación de formularios
    inicializarValidacionFormularios();

    // Configurar filtros para rutas
    document.getElementById('aerolineaSelect').addEventListener('change', cargarRutasDisponibles);
    document.getElementById('categoriaSelect').addEventListener('change', cargarRutasDisponibles);
});

// Datos de ejemplo
const paquetesExistentes = [
    {
        id: 1,
        nombre: "Sudamérica Esencial",
        descripcion: "Paquete básico para explorar Sudamérica",
        validez: 180,
        descuento: 20,
        fechaAlta: "2024-01-15",
        estado: "disponible",
        rutasIncluidas: 2
    },
    {
        id: 2,
        nombre: "Europa Grand Tour",
        descripcion: "Recorrido por las capitales europeas",
        validez: 365,
        descuento: 30,
        fechaAlta: "2024-02-01",
        estado: "disponible",
        rutasIncluidas: 1
    },
    {
        id: 3,
        nombre: "Caribe Paradise",
        descripcion: "Escapada a destinos caribeños",
        validez: 90,
        descuento: 25,
        fechaAlta: "2024-01-20",
        estado: "comprado",
        rutasIncluidas: 1
    }
];

const rutasDisponibles = [
    {
        id: "ZL1502",
        nombre: "Montevideo - Rio de Janeiro",
        aerolinea: "ZulyFly",
        origen: "Montevideo (MVD)",
        destino: "Rio de Janeiro (GIG)",
        duracion: "2h 30m",
        categorias: ["Internacionales", "América", "Cortos"],
        costoTurista: 320,
        costoEjecutivo: 550,
        estado: "Confirmada"
    },
    {
        id: "IB6012",
        nombre: "Montevideo - Madrid",
        aerolinea: "Iberia",
        origen: "Montevideo (MVD)",
        destino: "Madrid (MAD)",
        duracion: "12h",
        categorias: ["Internacionales", "Europa"],
        costoTurista: 750,
        costoEjecutivo: 1200,
        estado: "Confirmada"
    },
    {
        id: "AR2050",
        nombre: "Buenos Aires - Santiago",
        aerolinea: "Aerolíneas Argentinas",
        origen: "Buenos Aires (EZE)",
        destino: "Santiago (SCL)",
        duracion: "2h 15m",
        categorias: ["Internacionales", "América"],
        costoTurista: 280,
        costoEjecutivo: 480,
        estado: "Confirmada"
    },
    {
        id: "AA904",
        nombre: "Miami - Cancún",
        aerolinea: "American Airlines",
        origen: "Miami (MIA)",
        destino: "Cancún (CUN)",
        duracion: "2h 30m",
        categorias: ["Internacionales", "Caribe"],
        costoTurista: 380,
        costoEjecutivo: 650,
        estado: "Confirmada"
    }
];

// Variables globales
let paqueteSeleccionado = null;
let rutaSeleccionada = null;

function inicializarValidacionFormularios() {
    const formCrearPaquete = document.getElementById('formCrearPaquete');
    const formAgregarRuta = document.getElementById('formAgregarRuta');

    // Validación en tiempo real para campos requeridos
    const camposRequeridos = document.querySelectorAll('[required]');
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

    // Manejo de envío de formularios
    formCrearPaquete.addEventListener('submit', function(event) {
        event.preventDefault();
        event.stopPropagation();

        if (this.checkValidity() && validarPasoCrear1() && document.getElementById('confirmarCreacion').checked) {
            const datosPaquete = {
                nombre: document.getElementById('nombrePaquete').value,
                descripcion: document.getElementById('descripcionPaquete').value,
                validez: document.getElementById('validezPaquete').value,
                descuento: document.getElementById('descuentoPaquete').value,
                fechaAlta: document.getElementById('fechaAlta').value
            };

            console.log('Nuevo paquete a guardar:', datosPaquete);

            // Simular envío exitoso
            mostrarModalExito(
                'Paquete Creado Exitosamente',
                `El paquete "${datosPaquete.nombre}" ha sido creado y está ahora disponible en el sistema.`
            );

            // Limpiar formulario
            this.reset();
            document.getElementById('fechaAlta').valueAsDate = new Date();
            siguientePasoCrear(1);
            this.classList.remove('was-validated');
        } else {
            if (!document.getElementById('confirmarCreacion').checked) {
                mostrarMensajeError('Debe confirmar la creación del paquete antes de continuar.');
            }
            event.stopPropagation();
        }

        this.classList.add('was-validated');
    });

    formAgregarRuta.addEventListener('submit', function(event) {
        event.preventDefault();
        event.stopPropagation();

        if (this.checkValidity() && validarPasoAgregar1() && validarPasoAgregar2() && document.getElementById('confirmarAgregar').checked) {
            const datosAgregar = {
                paquete: paqueteSeleccionado.nombre,
                ruta: rutaSeleccionada.nombre,
                cantidad: document.getElementById('cantidadRuta').value,
                tipoAsiento: document.getElementById('tipoAsientoRuta').value
            };

            console.log('Ruta agregada:', datosAgregar);

            // Simular envío exitoso
            mostrarModalExito(
                'Ruta Agregada al Paquete',
                `La ruta "${rutaSeleccionada.nombre}" ha sido agregada al paquete "${paqueteSeleccionado.nombre}".`
            );

            // Limpiar selecciones
            paqueteSeleccionado = null;
            rutaSeleccionada = null;
            document.querySelectorAll('#agregar .paquete-card, #agregar .ruta-item').forEach(el => {
                el.classList.remove('selected');
            });
            document.getElementById('btnSiguienteAgregar1').disabled = true;
            document.getElementById('btnSiguienteAgregar2').disabled = true;
            siguientePasoAgregar(1);
            this.classList.remove('was-validated');
        } else {
            if (!document.getElementById('confirmarAgregar').checked) {
                mostrarMensajeError('Debe confirmar la adición de la ruta al paquete.');
            }
            event.stopPropagation();
        }

        this.classList.add('was-validated');
    });
}

// Funciones para la pestaña "Crear Nuevo Paquete"
function siguientePasoCrear(paso) {
    if (paso === 2 && !validarPasoCrear1()) return;

    document.querySelectorAll('#crear .form-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById('sectionCrear' + paso).classList.add('active');

    document.querySelectorAll('#crear .step').forEach(stepEl => {
        stepEl.classList.remove('active', 'completed');
    });

    for (let i = 1; i <= paso; i++) {
        const stepEl = document.getElementById('stepCrear' + i);
        if (i === paso) {
            stepEl.classList.add('active');
        } else {
            stepEl.classList.add('completed');
        }
    }

    if (paso === 2) {
        generarResumenPaquete();
    }
}

function validarPasoCrear1() {
    const nombre = document.getElementById('nombrePaquete').value;
    const descripcion = document.getElementById('descripcionPaquete').value;

    if (!nombre || !descripcion) {
        mostrarMensajeError('Por favor complete todos los campos requeridos.');
        return false;
    }

    // Verificar si el nombre ya existe
    const nombreExiste = paquetesExistentes.some(p => p.nombre.toLowerCase() === nombre.toLowerCase());
    if (nombreExiste) {
        document.getElementById('nombreError').textContent = 'Ya existe un paquete con este nombre. Por favor elija otro.';
        document.getElementById('nombrePaquete').classList.add('is-invalid');
        return false;
    }

    return true;
}

function generarResumenPaquete() {
    const resumen = document.getElementById('resumenPaquete');
    const html = `
        <p><strong>Nombre:</strong> ${document.getElementById('nombrePaquete').value}</p>
        <p><strong>Descripción:</strong> ${document.getElementById('descripcionPaquete').value}</p>
        <p><strong>Validez:</strong> ${document.getElementById('validezPaquete').value} días</p>
        <p><strong>Descuento:</strong> ${document.getElementById('descuentoPaquete').value}%</p>
        <p><strong>Fecha de alta:</strong> ${document.getElementById('fechaAlta').value}</p>
    `;
    resumen.innerHTML = html;
}

// Funciones para la pestaña "Agregar Rutas a Paquete"
function siguientePasoAgregar(paso) {
    if (paso === 2 && !validarPasoAgregar1()) return;
    if (paso === 3 && !validarPasoAgregar2()) return;

    document.querySelectorAll('#agregar .form-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById('sectionAgregar' + paso).classList.add('active');

    document.querySelectorAll('#agregar .step').forEach(stepEl => {
        stepEl.classList.remove('active', 'completed');
    });

    for (let i = 1; i <= paso; i++) {
        const stepEl = document.getElementById('stepAgregar' + i);
        if (i === paso) {
            stepEl.classList.add('active');
        } else {
            stepEl.classList.add('completed');
        }
    }

    if (paso === 3) {
        mostrarDetallesRutaSeleccionada();
    }
}

function validarPasoAgregar1() {
    if (paqueteSeleccionado === null) {
        mostrarMensajeError('Por favor seleccione un paquete para continuar.');
        return false;
    }
    return true;
}

function validarPasoAgregar2() {
    if (rutaSeleccionada === null) {
        mostrarMensajeError('Por favor seleccione una ruta para continuar.');
        return false;
    }
    return true;
}

function cargarPaquetesDisponibles() {
    const container = document.getElementById('listaPaquetes');
    container.innerHTML = '';

    const paquetesDisponibles = paquetesExistentes.filter(p => p.estado === 'disponible');

    if (paquetesDisponibles.length === 0) {
        container.innerHTML = '<div class="col-12"><p class="text-muted text-center">No hay paquetes disponibles para modificar.</p></div>';
        return;
    }

    paquetesDisponibles.forEach(paquete => {
        const paqueteHTML = `
            <div class="col-md-6">
                <div class="card paquete-card" onclick="seleccionarPaquete(${paquete.id})">
                    <div class="card-body">
                        <h6 class="card-title text-light">${paquete.nombre}</h6>
                        <p class="card-text small text-muted">${paquete.descripcion}</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="badge badge-disponible">Disponible</span>
                            <small class="text-muted">${paquete.rutasIncluidas} rutas</small>
                        </div>
                        <div class="mt-2">
                            <small class="text-light"><strong>Validez:</strong> ${paquete.validez} días</small><br>
                            <small class="text-light"><strong>Descuento:</strong> ${paquete.descuento}%</small>
                        </div>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += paqueteHTML;
    });
}

function cargarRutasDisponibles() {
    const container = document.getElementById('listaRutas');
    container.innerHTML = '';

    const aerolineaFiltro = document.getElementById('aerolineaSelect').value;
    const categoriaFiltro = document.getElementById('categoriaSelect').value;

    const rutasFiltradas = rutasDisponibles.filter(ruta => {
        const coincideAerolinea = !aerolineaFiltro ||
            ruta.aerolinea.toLowerCase().includes(aerolineaFiltro.toLowerCase());
        const coincideCategoria = !categoriaFiltro ||
            ruta.categorias.some(cat => cat.toLowerCase().includes(categoriaFiltro.toLowerCase()));

        return coincideAerolinea && coincideCategoria;
    });

    if (rutasFiltradas.length === 0) {
        container.innerHTML = '<div class="col-12"><p class="text-muted text-center">No se encontraron rutas con los filtros seleccionados.</p></div>';
        return;
    }

    rutasFiltradas.forEach(ruta => {
        const rutaHTML = `
            <div class="col-md-6">
                <div class="ruta-item" onclick="seleccionarRuta('${ruta.id}')">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <h6 class="mb-1 text-light">${ruta.nombre}</h6>
                            <p class="mb-1 text-muted small">${ruta.aerolinea}</p>
                            <p class="mb-1 small text-light"><strong>${ruta.origen}</strong> → <strong>${ruta.destino}</strong></p>
                            <p class="mb-0 small text-light">Duración: ${ruta.duracion}</p>
                        </div>
                        <span class="badge bg-primary">${ruta.id}</span>
                    </div>
                    <div class="mt-2">
                        ${ruta.categorias.map(cat => `<span class="badge bg-secondary me-1">${cat}</span>`).join('')}
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += rutaHTML;
    });
}

function seleccionarPaquete(id) {
    paqueteSeleccionado = paquetesExistentes.find(p => p.id === id);

    // Remover selección anterior y marcar actual
    document.querySelectorAll('#agregar .paquete-card').forEach(card => {
        card.classList.remove('selected');
    });
    event.currentTarget.classList.add('selected');

    document.getElementById('btnSiguienteAgregar1').disabled = false;
}

function seleccionarRuta(id) {
    rutaSeleccionada = rutasDisponibles.find(r => r.id === id);

    // Remover selección anterior y marcar actual
    document.querySelectorAll('#agregar .ruta-item').forEach(item => {
        item.classList.remove('selected');
    });
    event.currentTarget.classList.add('selected');

    document.getElementById('btnSiguienteAgregar2').disabled = false;
}

function mostrarDetallesRutaSeleccionada() {
    if (!rutaSeleccionada) return;

    document.getElementById('rutaSeleccionadaNombre').textContent = rutaSeleccionada.nombre;
    document.getElementById('rutaSeleccionadaInfo').textContent =
        `${rutaSeleccionada.aerolinea} | ${rutaSeleccionada.origen} → ${rutaSeleccionada.destino} | ${rutaSeleccionada.duracion}`;
}

function mostrarModalExito(titulo, mensaje) {
    document.getElementById('modalMensajeTitulo').textContent = titulo;
    document.getElementById('modalMensajeDetalle').textContent = mensaje;
    const successModal = new bootstrap.Modal(document.getElementById('successModal'));
    successModal.show();
}

function mostrarMensajeError(mensaje) {
    // Crear toast de error dinámicamente
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
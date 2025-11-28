// Variables globales
let usuarios = {};
let rutasActuales = [];
let usuarioActual = null;
let usuarioConsultadoId = null;
let esMiUsuario = false;
let siguiendoUsuario = false;

// Placeholder SVG
const DEFAULT_USER_PLACEHOLDER = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUwIiBoZWlnaHQ9IjE1MCIgdmlld0JveD0iMCAwIDE1MCAxNTAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIxNTAiIGhlaWdodD0iMTUwIiByeD0iNzUiIGZpbGw9IiMzNDk4REIiLz4KPHN2ZyB4PSIzOCIgeT0iMzgiIHdpZHRoPSI3NCIgaGVpZ2h0PSI3NCIgdmlld0JveD0iMCAwIDI0IDI0IiBmaWxsPSJ3aGl0ZSIgeG1zbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTEyIDEyYzIuMjEgMCA0LTEuNzkgNC00cy0xLjc5LTQtNC00LTQgMS43OS00IDQgMS43OSA0IDQgNHptMCAyYy0yLjY3IDAtOCAxLjM0LTggNHYyaDE2di0yYzAtMi42Ni01LjMzLTQtOC00eiIvPgo8L3N2Zz4KPC9zdmc+';

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Inicializando consulta de usuarios...');
    inicializarConsultaUsuarios();
});

async function inicializarConsultaUsuarios() {
    try {
        console.log('Cargando usuarios desde backend...');
        await cargarUsuariosDesdeBackend();

        console.log('⚙ Configurando interfaz...');
        configurarInterfaz();

        console.log('Consulta de usuarios inicializada correctamente');
    } catch (error) {
        console.error('Error en inicialización:', error);
        mostrarError('Error al inicializar la consulta de usuarios: ' + error.message);
    }
}

async function cargarUsuariosDesdeBackend() {
    try {
        console.log('🌐 Haciendo fetch a /consulta-usuario...');

        const response = await fetch('consulta-usuario?action=listar-usuarios');
        console.log('📨 Response status:', response.status);
        console.log('📨 Response ok:', response.ok);

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status} - ${response.statusText}`);
        }

        const text = await response.text();
        console.log('📄 Response text:', text);

        let usuariosData;
        try {
            usuariosData = JSON.parse(text);
        } catch (parseError) {
            console.error('❌ Error parseando JSON:', parseError);
            throw new Error('Respuesta del servidor no es JSON válido');
        }

        console.log('📊 Usuarios cargados del backend:', usuariosData);

        // Verificar si no hay usuarios en la base de datos
        if (!usuariosData || (!usuariosData.clientes && !usuariosData.aerolineas)) {
            console.log('📭 No hay usuarios en la BD');
            mostrarMensajeSinUsuarios();
            return;
        }

        // Transformar datos del servidor al formato esperado
        usuarios = {};

        // Procesar clientes
        if (usuariosData.clientes && Array.isArray(usuariosData.clientes)) {
            usuariosData.clientes.forEach(cliente => {
                console.log('📋 Procesando cliente:', cliente);

                usuarios[cliente.id] = {
                    tipo: "Cliente",
                    nombre: cliente.nombre || 'Sin nombre',
                    nickname: cliente.id || 'Sin nickname',
                    correo: cliente.correo || 'Sin email',
                    fechaRegistro: cliente.fechaRegistro || 'No especificada',
                    imagen: (cliente.imagen && cliente.imagen.toString().trim()) ? cliente.imagen : DEFAULT_USER_PLACEHOLDER,
                    datosPersonales: {
                        apellido: cliente.nombre ? cliente.nombre.split(' ').slice(1).join(' ') : 'No especificado',
                        nacimiento: 'No especificada',
                        nacionalidad:  'No especificada',
                        documento: 'No especificado'
                    },
                    reservas: [],
                    paquetes: []
                };
            });
        }

        // Procesar aerolíneas
        if (usuariosData.aerolineas && Array.isArray(usuariosData.aerolineas)) {
            usuariosData.aerolineas.forEach(aerolinea => {
                console.log('📋 Procesando aerolínea:', aerolinea);

                usuarios[aerolinea.id] = {
                    tipo: "Aerolínea",
                    nombre: aerolinea.nombre || 'Sin nombre',
                    nickname: aerolinea.id || 'Sin nickname',
                    correo: aerolinea.correo || 'Sin email',
                    fechaRegistro: aerolinea.fechaRegistro || 'No especificada',
                    imagen: (aerolinea.imagen && aerolinea.imagen.toString().trim()) ? aerolinea.imagen : DEFAULT_USER_PLACEHOLDER,
                    descripcion: aerolinea.descripcion ||'Sin descripción',
                    sitioWeb: aerolinea.sitioWeb|| 'No especificado',
                    rutas: []
                };
            });
        }

        console.log('🎯 Usuarios procesados:', usuarios);

    } catch (error) {
        console.error('💥 Error cargando usuarios:', error);
        mostrarError('No se pudieron cargar los usuarios: ' + error.message);
    }
}

async function cargarUsuarioSeleccionado(usuarioId) {
    try {
        const usuario = usuarios[usuarioId];
        if (!usuario) return;

        console.log('👤 Cargando detalles del usuario:', usuarioId);
        usuarioConsultadoId = usuarioId;

        // Resetear estado de la interfaz antes de cargar nuevo usuario
        resetearInterfaz();

        // Configurar botones de seguimiento
        await configurarSeguimiento(usuarioId);

        // Cargar información detallada del usuario
        const response = await fetch(`consulta-usuario?action=obtener-usuario&usuario=${encodeURIComponent(usuarioId)}&tipo=${usuario.tipo.toLowerCase() === 'cliente' ? 'cliente' : 'aerolinea'}`);

        if (!response.ok) {
            if (response.status === 500) {
                console.warn('⚠️ Error 500 del servidor, usando datos básicos');
                usuarioActual = usuario;
                mostrarInformacionUsuario(usuario);
                return;
            }
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const usuarioDetalle = await response.json();
        console.log('📄 Detalle de usuario recibido:', usuarioDetalle);

        // Actualizar usuario con información detallada
        if (usuario.tipo === 'Cliente') {
            usuario.datosPersonales.nacimiento = usuarioDetalle.fechaNacimiento || usuario.datosPersonales.nacimiento;
            usuario.datosPersonales.nacionalidad = usuarioDetalle.nacionalidad || usuario.datosPersonales.nacionalidad;

            // Cargar reservas y paquetes
            await cargarReservasCliente(usuarioId);
            await cargarPaquetesCliente(usuarioId);
        } else if (usuario.tipo === 'Aerolínea') {
            usuario.descripcion = usuarioDetalle.descripcion || usuario.descripcion;
            usuario.sitioWeb = usuarioDetalle.sitioWeb || usuario.sitioWeb;

            // Cargar rutas
            await cargarRutasAerolinea(usuarioId);
        }

        usuarioActual = usuario;
        mostrarInformacionUsuario(usuario);

    } catch (error) {
        console.error('💥 Error cargando detalles del usuario:', error);
        if (usuarios[usuarioId]) {
            usuarioActual = usuarios[usuarioId];
            mostrarInformacionUsuario(usuarios[usuarioId]);
        }
    }
}

// Nueva función para configurar seguimiento
async function configurarSeguimiento(usuarioId) {
    try {
        // Obtener usuario actual de la sesión
        const usuarioActual = obtenerUsuarioActual();
        esMiUsuario = (usuarioActual && usuarioActual.id === usuarioId);

        console.log('🔍 Configurando seguimiento:', {
            usuarioConsultado: usuarioId,
            usuarioActual: usuarioActual?.id,
            esMiUsuario: esMiUsuario
        });

        // Mostrar/ocultar sección de seguir
        const followSection = document.getElementById('followSection');
        if (esMiUsuario) {
            followSection.style.display = 'none';
            console.log('👤 Ocultando botón de seguir (es mi propio usuario)');
        } else {
            followSection.style.display = 'block';
            console.log('👤 Mostrando botón de seguir');

            // Verificar estado de seguimiento
            await verificarEstadoSeguimiento(usuarioId);
        }

        // Cargar estadísticas de seguimiento
        await cargarEstadisticasSeguimiento(usuarioId);

    } catch (error) {
        console.error('Error configurando seguimiento:', error);
    }
}

async function verificarEstadoSeguimiento(usuarioId) {
    try {
        // ✅ FORZAR recarga sin cache usando timestamp
        const timestamp = new Date().getTime();
        const url = `consulta-usuario?action=verificar-seguimiento&usuario=${encodeURIComponent(usuarioId)}&forzarRecarga=true&_=${timestamp}`;

        console.log('🔍 Verificando estado de seguimiento:', url);

        const response = await fetch(url);

        if (response.ok) {
            const resultado = await response.json();
            siguiendoUsuario = resultado.siguiendo || false;
            actualizarBotonSeguir();
            console.log('✅ Estado de seguimiento verificado:', siguiendoUsuario);
        } else {
            console.error('❌ Error en respuesta:', response.status);
            siguiendoUsuario = false;
            actualizarBotonSeguir();
        }
    } catch (error) {
        console.error('Error verificando estado de seguimiento:', error);
        siguiendoUsuario = false;
        actualizarBotonSeguir();
    }
}


// Función para cargar estadísticas de seguimiento - CON CACHE FORZADO
async function cargarEstadisticasSeguimiento(usuarioId) {
    try {
        // ✅ FORZAR recarga sin cache usando timestamp
        const timestamp = new Date().getTime();
        const url = `consulta-usuario?action=obtener-estadisticas-seguimiento&usuario=${encodeURIComponent(usuarioId)}&forzarRecarga=true&_=${timestamp}`;

        console.log('🔍 Cargando estadísticas de seguimiento:', url);

        const response = await fetch(url);

        if (response.ok) {
            const estadisticas = await response.json();
            document.getElementById('seguidoresCount').textContent = estadisticas.seguidores || 0;
            document.getElementById('seguidosCount').textContent = estadisticas.seguidos || 0;
            console.log('✅ Estadísticas cargadas:', estadisticas);
        } else {
            console.error('❌ Error cargando estadísticas:', response.status);
            document.getElementById('seguidoresCount').textContent = '0';
            document.getElementById('seguidosCount').textContent = '0';
        }
    } catch (error) {
        console.error('Error cargando estadísticas:', error);
        document.getElementById('seguidoresCount').textContent = '0';
        document.getElementById('seguidosCount').textContent = '0';
    }
}

// Función para seguir/dejar de seguir - MEJORADA
async function toggleFollow() {
    if (!usuarioConsultadoId) {
        console.error('❌ usuarioConsultadoId no está definido');
        return;
    }

    console.log('🔄 toggleFollow llamado para usuario:', usuarioConsultadoId);

    try {
        const action = siguiendoUsuario ? 'dejar-de-seguir' : 'seguir';

        // ✅ FORZAR recarga sin cache
        const timestamp = new Date().getTime();
        const url = `consulta-usuario?action=${action}&usuario=${encodeURIComponent(usuarioConsultadoId)}&_=${timestamp}`;

        console.log('🔍 URL:', url);

        const response = await fetch(url);
        console.log('📨 Response status:', response.status);

        if (response.ok) {
            const result = await response.json();
            console.log('✅ Resultado:', result);

            if (result.success) {
                siguiendoUsuario = !siguiendoUsuario;
                actualizarBotonSeguir();

                // Mostrar mensaje
                if (siguiendoUsuario) {
                    mostrarExito('¡Ahora sigues a este usuario!');
                } else {
                    mostrarExito('Has dejado de seguir a este usuario');
                }

                // ✅ FORZAR recarga de estadísticas sin cache
                await cargarEstadisticasSeguimiento(usuarioConsultadoId);

                console.log('✅ Estado actualizado en interfaz');
            } else {
                mostrarError(result.error || 'Error en la operación');
            }
        } else {
            const errorText = await response.text();
            console.error('❌ Error response:', errorText);
            mostrarError('Error: ' + response.status);
        }
    } catch (error) {
        console.error('💥 Error en toggleFollow:', error);
        mostrarError('Error de conexión: ' + error.message);
    }
}

// Función para actualizar botón de seguir
function actualizarBotonSeguir() {
    const followBtn = document.getElementById('followBtn');
    if (!followBtn) {
        console.error('❌ Botón followBtn no encontrado');
        return;
    }

    if (siguiendoUsuario) {
        followBtn.innerHTML = '<i class="bi bi-person-dash me-1"></i>Dejar de seguir';
        followBtn.classList.remove('btn-light');
        followBtn.classList.add('btn-warning');
    } else {
        followBtn.innerHTML = '<i class="bi bi-person-plus me-1"></i>Seguir';
        followBtn.classList.remove('btn-warning');
        followBtn.classList.add('btn-light');
    }

    console.log('✅ Botón actualizado - siguiendoUsuario:', siguiendoUsuario);
}

// Resto de las funciones existentes (cargarReservasCliente, cargarPaquetesCliente, etc.)
// ... [Mantén todas las otras funciones que ya tenías] ...
async function cargarReservasCliente(usuarioId) {
    try {
        // Verificar si el usuario actual es el cliente propietario
        const usuarioActualObj = obtenerUsuarioActual();
        const esClientePropietario = usuarioActualObj && usuarioActualObj.id === usuarioId;

        if (!esClientePropietario) {
            console.log('No mostrar reservas - no es el cliente propietario');
            usuarios[usuarioId].reservas = [];
            return;
        }

        const response = await fetch(`consulta-usuario?action=obtener-reservas-cliente&usuario=${encodeURIComponent(usuarioId)}`);

        if (!response.ok) {
            console.warn('⚠No se pudieron cargar las reservas');
            return;
        }

        const reservasData = await response.json();
        console.log('Reservas cargadas:', reservasData);

        usuarios[usuarioId].reservas = reservasData.map(reserva => ({
            id: "RES-" + reserva.id,
            vuelo: reserva.vuelo || 'Vuelo no especificado',
            fecha: reserva.fechaReserva || 'No especificada',
            estado: 'Confirmada'
        }));

    } catch (error) {
        console.error('Error cargando reservas:', error);
    }
}

async function cargarPaquetesCliente(usuarioId) {
    try {
        // Verificar si el usuario actual es el cliente propietario
        const usuarioActualObj = obtenerUsuarioActual();
        const esClientePropietario = usuarioActualObj && usuarioActualObj.id === usuarioId;
        console.log('esClientePropietario:', esClientePropietario);
        console.log('Comparación:', usuarioActualObj?.id, '===', usuarioId);

        if (!esClientePropietario) {
            console.log('No mostrar paquetes - no es el cliente propietario');
            usuarios[usuarioId].paquetes = [];
            return;
        }

        const response = await fetch(`consulta-usuario?action=obtener-paquetes-cliente&usuario=${encodeURIComponent(usuarioId)}`);

        if (!response.ok) {
            console.warn('No se pudieron cargar los paquetes - Status:', response.status);
            return;
        }

        const paquetesData = await response.json();
        console.log('Paquetes recibidos del servidor:', paquetesData);
        console.log('Número de paquetes:', paquetesData.length);

        usuarios[usuarioId].paquetes = paquetesData.map(paquete => ({
            id: "PKG-" + paquete.id,
            nombre: paquete.nombre || 'Paquete sin nombre',
            compra: paquete.fechaCompra || 'No especificada',
            vencimiento: calcularVencimiento(paquete.fechaCompra, paquete.periodoValidezDias),
            estado: 'Vigente'
        }));

        console.log('✅ Paquetes procesados y guardados:', usuarios[usuarioId].paquetes);

    } catch (error) {
        console.error('❌ Error cargando paquetes:', error);
    }
}

async function cargarRutasAerolinea(usuarioId) {
    try {
        const response = await fetch(`consulta-usuario?action=obtener-rutas-aerolinea&usuario=${encodeURIComponent(usuarioId)}`);

        if (!response.ok) {
            console.warn('No se pudieron cargar las rutas');
            return;
        }

        const rutasData = await response.json();
        console.log('Rutas cargadas del servidor:', rutasData.length);
        console.log('Estados de rutas:', rutasData.map(r => r.estado));

        // Verificar si el usuario actual es la aerolínea propietaria
        const usuarioActualObj = obtenerUsuarioActual();
        const esAerolineaPropietaria = usuarioActualObj && usuarioActualObj.id === usuarioId;

        console.log('👤 Usuario actual:', usuarioActualObj?.id);
        console.log('🏢 Aerolínea consultada:', usuarioId);
        console.log('🔑 Es aerolínea propietaria:', esAerolineaPropietaria);

        // Si NO es la aerolínea propietaria, filtrar solo rutas confirmadas (case-insensitive)
        const rutasFiltradas = esAerolineaPropietaria
            ? rutasData
            : rutasData.filter(ruta => {
                const estado = (ruta.estado || '').toLowerCase();
                const esConfirmada = estado === 'confirmada';
                console.log(`Ruta "${ruta.nombre}" - Estado: "${ruta.estado}" - Es confirmada: ${esConfirmada}`);
                return esConfirmada;
            });

        console.log('✅ Rutas filtradas:', rutasFiltradas.length, 'de', rutasData.length);

        usuarios[usuarioId].rutas = rutasFiltradas.map(ruta => ({
            id: ruta.id || 'Sin ID',
            nombre: ruta.nombre || 'Ruta sin nombre',
            estado: ruta.estado || 'No especificado',
            fechaAlta: ruta.fechaAlta || 'No especificada'
        }));

    } catch (error) {
        console.error('Error cargando rutas:', error);
    }
}

function calcularVencimiento(fechaCompra, diasValidez) {
    if (!fechaCompra || !diasValidez) return 'No especificado';
    try {
        const fecha = new Date(fechaCompra);
        fecha.setDate(fecha.getDate() + diasValidez);
        return fecha.toISOString().split('T')[0];
    } catch (e) {
        return 'No especificado';
    }
}

function configurarInterfaz() {
    const usuarioSelect = document.getElementById("usuarioSelect");

    console.log('Configurando interfaz...');
    console.log('Número de usuarios:', Object.keys(usuarios).length);

    // Limpiar el select
    usuarioSelect.innerHTML = '<option value="">Seleccione un usuario...</option>';

    if (Object.keys(usuarios).length === 0) {
        console.log('No hay usuarios para mostrar en el select');
        usuarioSelect.innerHTML = '<option value="">No hay usuarios disponibles</option>';
        return;
    }

    // Llenar con usuarios reales
    Object.keys(usuarios).forEach(usuarioId => {
        const usuario = usuarios[usuarioId];
        const option = document.createElement('option');
        option.value = usuarioId;
        option.textContent = `${usuario.nombre} (${usuario.tipo})`;
        option.setAttribute('data-tipo', usuario.tipo.toLowerCase());
        usuarioSelect.appendChild(option);
    });

    console.log('Select poblado con', Object.keys(usuarios).length, 'usuarios');

    // Configurar event listener
    usuarioSelect.addEventListener("change", function() {
        const usuarioId = this.value;
        console.log('Usuario seleccionado:', usuarioId);

        if (usuarioId && usuarios[usuarioId]) {
            resetearInterfaz();
            cargarUsuarioSeleccionado(usuarioId);
        } else {
            document.getElementById('infoUsuario').style.display = 'none';
        }
    });
}

function resetearInterfaz() {
    const filterButtons = document.querySelectorAll('.btn-group .btn');
    filterButtons.forEach(btn => {
        btn.classList.remove('active');
    });
    const todasBtn = document.querySelector('.btn-group .btn[onclick*="todas"]');
    if (todasBtn) {
        todasBtn.classList.add('active');
    }
    rutasActuales = [];
}

function mostrarMensajeSinUsuarios() {
    const usuarioSelect = document.getElementById("usuarioSelect");
    const infoUsuario = document.getElementById("infoUsuario");

    console.log('Mostrando mensaje de no hay usuarios');

    usuarioSelect.innerHTML = '<option value="">No hay usuarios disponibles</option>';
    infoUsuario.innerHTML = `
        <div class="alert alert-info">
            <h5>No hay usuarios disponibles</h5>
            <p>Actualmente no hay usuarios registrados en el sistema.</p>
            <p>Por favor, contacte al administrador o vuelva más tarde.</p>
        </div>
    `;
    infoUsuario.style.display = "block";
}

function mostrarInformacionUsuario(usuario) {
    console.log('Mostrando información del usuario:', usuario);

    document.getElementById('infoUsuario').style.display = 'block';
    document.getElementById('nombreUsuario').textContent = usuario.nombre;
    document.getElementById('nicknameUsuario').textContent = usuario.nickname;
    document.getElementById('correoUsuario').textContent = usuario.correo;
    document.getElementById('tipoUsuario').textContent = usuario.tipo;
    document.getElementById('fechaRegistro').textContent = usuario.fechaRegistro;

    const imgEl = document.getElementById('imagenUsuario');
    imgEl.onerror = function() {
        imgEl.src = DEFAULT_USER_PLACEHOLDER;
        imgEl.style.objectFit = 'contain';
    };
    imgEl.src = (usuario.imagen && usuario.imagen.toString().trim()) ? usuario.imagen : DEFAULT_USER_PLACEHOLDER;
    imgEl.style.objectFit = 'cover';
    imgEl.style.display = 'block';

    if (usuario.tipo === 'Cliente') {
        mostrarInfoCliente(usuario);
    } else if (usuario.tipo === 'Aerolínea') {
        mostrarInfoAerolinea(usuario);
    }
}

function mostrarInfoCliente(usuario) {
    document.getElementById('infoCliente').style.display = 'block';
    document.getElementById('infoAerolinea').style.display = 'none';

    document.getElementById('clienteApellido').textContent = usuario.datosPersonales.apellido;
    document.getElementById('clienteNacimiento').textContent = usuario.datosPersonales.nacimiento;
    document.getElementById('clienteNacionalidad').textContent = usuario.datosPersonales.nacionalidad;
    document.getElementById('clienteDocumento').textContent = usuario.datosPersonales.documento;

    const reservasContainer = document.getElementById('reservasCliente');
    reservasContainer.innerHTML = '';

    if (usuario.reservas.length === 0) {
        reservasContainer.innerHTML = '<div class="col-12"><p class="text-muted">No tiene reservas registradas.</p></div>';
    } else {
        usuario.reservas.forEach(reserva => {
            const reservaHTML = `
                <div class="col-md-6">
                    <div class="reserva-item">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <h6 class="mb-1">${reserva.vuelo}</h6>
                                <p class="mb-1 small">Código: ${reserva.id}</p>
                                <p class="mb-0 small">Fecha: ${reserva.fecha}</p>
                            </div>
                            <span class="badge ${reserva.estado === 'Confirmada' ? 'bg-success' : 'bg-warning'} badge-estado">
                                ${reserva.estado}
                            </span>
                        </div>
                        <div class="mt-2">
                            <a href="consulta-reserva.jsp?reserva=${encodeURIComponent(reserva.id)}" 
                               class="btn btn-sm btn-outline-primary">
                                <i class="bi bi-search"></i> Ver Detalles
                            </a>
                        </div>
                    </div>
                </div>
            `;
            reservasContainer.innerHTML += reservaHTML;
        });
    }

    const paquetesContainer = document.getElementById('paquetesCliente');
    paquetesContainer.innerHTML = '';

    if (usuario.paquetes.length === 0) {
        paquetesContainer.innerHTML = '<div class="col-12"><p class="text-muted">No ha comprado ningún paquete.</p></div>';
    } else {
        usuario.paquetes.forEach(paquete => {
            const paqueteHTML = `
                <div class="col-md-6">
                    <div class="paquete-item">
                        <h6 class="mb-1">${paquete.nombre}</h6>
                        <p class="mb-1 small">Comprado: ${paquete.fechaCompra}</p>
                        <p class="mb-1 small">Vence: ${paquete.vencimiento}</p>
                        <span class="badge ${paquete.estado === 'Vigente' ? 'bg-success' : 'bg-secondary'} badge-estado">
                            ${paquete.estado}
                        </span>
                        <div class="mt-2">
                            <a href="compra-paquete.jsp?paquete=${encodeURIComponent(paquete.nombre)}" 
                               class="btn btn-sm btn-outline-primary" data-inc-type="paquete" data-inc-nombre="${paquete.nombre}">
                                <i class="bi bi-search"></i> Ver Detalles
                            </a>
                        </div>
                    </div>
                </div>
            `;
            paquetesContainer.innerHTML += paqueteHTML;
        });
    }
}

function mostrarInfoAerolinea(usuario) {
    document.getElementById('infoCliente').style.display = 'none';
    document.getElementById('infoAerolinea').style.display = 'block';

    document.getElementById('aerolineaDescripcion').textContent = usuario.descripcion;
    document.getElementById('aerolineaWeb').innerHTML = usuario.sitioWeb ?
        `<a href="${usuario.sitioWeb}" target="_blank">${usuario.sitioWeb}</a>` : 'No especificado';
    document.getElementById('aerolineaFechaRegistro').textContent = usuario.fechaRegistro;
    document.getElementById('totalRutas').textContent = usuario.rutas.length;

    rutasActuales = usuario.rutas;

    // Verificar si el usuario actual es la aerolínea propietaria
    const usuarioActualObj = obtenerUsuarioActual();
    const esAerolineaPropietaria = usuarioActualObj && usuarioActualObj.id === usuario.nickname;

    // Mostrar/ocultar filtros de estado de rutas según permisos
    const filtrosRutas = document.querySelector('.btn-group');
    if (filtrosRutas) {
        if (esAerolineaPropietaria) {
            filtrosRutas.style.display = 'inline-flex';
            console.log('Mostrando filtros de rutas - es aerolínea propietaria');
        } else {
            filtrosRutas.style.display = 'none';
            console.log('Ocultando filtros de rutas - no es aerolínea propietaria');
        }
    }

    document.getElementById('vuelosAerolinea').innerHTML = `
        <div class="col-12">
            <p class="text-muted">Haga clic en "Cargar Vuelos" para ver los vuelos disponibles de esta aerolínea.</p>
        </div>
    `;

    resetearFiltrosRutas();
    cargarRutasAerolineaInterfaz('todas');
}

function resetearFiltrosRutas() {
    document.querySelectorAll('.btn-group .btn').forEach(btn => {
        btn.classList.remove('active');
    });
    const todasBtn = document.querySelector('.btn[onclick*="todas"]');
    if (todasBtn) {
        todasBtn.classList.add('active');
    }
}

function cargarRutasAerolineaInterfaz(filtro) {
    const rutasContainer = document.getElementById('rutasAerolinea');
    rutasContainer.innerHTML = '';

    const rutasMostrar = filtro === 'todas' ?
        rutasActuales :
        rutasActuales.filter(ruta => ruta.estado.toLowerCase() === filtro);

    if (rutasMostrar.length === 0) {
        rutasContainer.innerHTML = '<div class="col-12"><p class="text-muted">No hay rutas con el filtro seleccionado.</p></div>';
        return;
    }

    rutasMostrar.forEach(ruta => {
        let badgeClass = '';
        switch(ruta.estado) {
            case 'Confirmada': badgeClass = 'bg-success'; break;
            case 'Ingresada': badgeClass = 'bg-warning'; break;
            case 'Rechazada': badgeClass = 'bg-danger'; break;
            case 'Finalizada': badgeClass = 'bg-info'; break;
            default: badgeClass = 'bg-secondary';
        }

        const rutaHTML = `
            <div class="col-md-6">
                <div class="ruta-item">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <h6 class="mb-1">${ruta.nombre}</h6>
                            <p class="mb-1 small">Código: ${ruta.id}</p>
                            <p class="mb-0 small">Alta: ${ruta.fechaAlta}</p>
                        </div>
                        <span class="badge ${badgeClass} badge-estado">
                            ${ruta.estado}
                        </span>
                    </div>
                    <div class="mt-2">
                        <a href="consulta-ruta.jsp?ruta=${encodeURIComponent(ruta.nombre)}" 
                           class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-search"></i> Ver Detalles
                        </a>
                    </div>
                </div>
            </div>
        `;
        rutasContainer.innerHTML += rutaHTML;
    });
}

function filtrarRutas(filtro) {
    document.querySelectorAll('.btn-group .btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    cargarRutasAerolineaInterfaz(filtro);
}

// Funciones para vuelos
async function cargarVuelosAerolinea() {
    if (!usuarioConsultadoId) return;

    const vuelosContainer = document.getElementById('vuelosAerolinea');
    vuelosContainer.innerHTML = '<div class="col-12 text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Cargando vuelos...</span></div><p class="text-muted mt-2">Cargando vuelos...</p></div>';

    try {
        const response = await fetch(`consulta-usuario?action=obtener-vuelos-aerolinea&usuario=${encodeURIComponent(usuarioConsultadoId)}`);

        if (!response.ok) {
            throw new Error('Error al cargar vuelos');
        }

        const vuelos = await response.json();
        mostrarVuelosAerolinea(vuelos);

    } catch (error) {
        console.error('Error cargando vuelos:', error);
        vuelosContainer.innerHTML = '<div class="col-12"><div class="alert alert-danger">Error al cargar los vuelos</div></div>';
    }
}

function mostrarVuelosAerolinea(vuelos) {
    const vuelosContainer = document.getElementById('vuelosAerolinea');

    if (!vuelos || vuelos.length === 0) {
        vuelosContainer.innerHTML = '<div class="col-12"><p class="text-muted">No hay vuelos disponibles para esta aerolínea.</p></div>';
        return;
    }

    let vuelosHTML = '';

    vuelos.forEach(vuelo => {
        const origen = vuelo.origen || 'N/A';
        const destino = vuelo.destino || 'N/A';
        const nombreVuelo = vuelo.nombre || 'N/A';
        const fecha = vuelo.fecha || 'No especificada';
        const duracion = vuelo.duracion || 'N/A';
        const ruta = vuelo.ruta || 'No especificada';
        const aerolinea = vuelo.aerolinea || 'N/A';
        const vueloId = vuelo.id || '';

        vuelosHTML +=
            '<div class="col-md-6">' +
            '    <div class="flight-card">' +
            '        <div class="flight-header">' +
            '            <div>' +
            '                <span class="flight-route">' + origen + ' - ' + destino + '</span>' +
            '                <div class="text-muted small">' +
            '                    <i class="bi bi-airplane me-1"></i>' + nombreVuelo +
            '                </div>' +
            '            </div>' +
            '            <span class="badge bg-info">Vuelo</span>' +
            '        </div>' +
            '        <div class="flight-description">' +
            '            <strong>Fecha:</strong> ' + fecha + ' | ' +
            '            <strong>Duración:</strong> ' + duracion + ' min' +
            '        </div>' +
            '        <div class="flight-details">' +
            '            <small>' +
            '                <strong>Ruta:</strong> ' + ruta + ' | ' +
            '                <strong>Aerolínea:</strong> ' + aerolinea +
            '            </small>' +
            '        </div>' +
            '        <a href="consulta-vuelo.jsp?vuelo=' + encodeURIComponent(vueloId) + '" class="read-more">' +
            '            Ver detalles del vuelo <i class="bi bi-arrow-right"></i>' +
            '        </a>' +
            '    </div>' +
            '</div>';
    });

    vuelosContainer.innerHTML = vuelosHTML;
}

// Funciones de utilidad
function obtenerUsuarioActual() {
    if (typeof window.CURRENT_SESSION !== 'undefined' && window.CURRENT_SESSION.authenticated) {
        return {
            id: window.CURRENT_SESSION.nickname,
            nickname: window.CURRENT_SESSION.nickname,
            tipo: window.CURRENT_SESSION.tipo
        };
    }
    return null;
}

function mostrarError(mensaje) {
    console.error('Mostrando error:', mensaje);
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
    mostrarToast(toastHTML);
}

function mostrarExito(mensaje) {
    const toastHTML = `
        <div class="toast align-items-center text-bg-success border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi bi-check-circle-fill me-2"></i>${mensaje}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;
    mostrarToast(toastHTML);
}

function mostrarToast(toastHTML) {
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
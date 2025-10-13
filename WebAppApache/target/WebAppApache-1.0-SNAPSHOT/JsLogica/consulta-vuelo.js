// src/main/webapp/JsLogica/consulta-vuelo.js
console.log('🔴 DEBUG: Script inline ejecutándose');
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 DOM cargado - Iniciando consulta-vuelo.js');
    inicializarConsultaVuelo();
});
function inicializarConsultaVuelo() {
    console.log('🔧 Inicializando consulta de vuelos...');
    const aerolineaSelect = document.getElementById('aerolinea');
    const rutaSelect = document.getElementById('rutaVuelo');
    const vueloSelect = document.getElementById('vuelo');
    const resultadoConsulta = document.getElementById('resultadoConsulta');
    const formConsulta = document.getElementById('formConsultaVuelo');

    // Debug de elementos del DOM
    console.log('📋 Elementos del DOM encontrados:');
    console.log('  - aerolineaSelect:', aerolineaSelect);
    console.log('  - rutaSelect:', rutaSelect);
    console.log('  - vueloSelect:', vueloSelect);
    console.log('  - resultadoConsulta:', resultadoConsulta);
    console.log('  - formConsulta:', formConsulta);

    if (!aerolineaSelect || !rutaSelect || !vueloSelect || !formConsulta) {
        console.error('❌ Error: Faltan elementos esenciales del DOM');
        return;
    }

    // Cargar aerolíneas
    console.log('📡 Iniciando fetch de aerolíneas...');
    const aerolineasUrl = 'WebAppApache_war_exploded/api/aerolineas';
    console.log('🌐 URL de aerolíneas:', aerolineasUrl);

    fetch(aerolineasUrl)
        .then(response => {
            console.log('📨 Respuesta recibida - Status:', response.status, response.statusText);
            console.log('🔗 URL completa:', response.url);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(aerolineas => {
            console.log('✅ Datos de aerolíneas recibidos:', aerolineas);
            console.log('📊 Número de aerolíneas:', aerolineas.length);

            aerolineaSelect.innerHTML = '<option value="">Seleccione aerolínea...</option>';

            if (aerolineas.length === 0) {
                console.warn('⚠️ No se recibieron aerolíneas (array vacío)');
                mostrarMensajeError('No hay aerolíneas disponibles en el sistema');
                return;
            }

            aerolineas.forEach((a, index) => {
                console.log(`   ✈️ Aerolínea ${index + 1}:`, a.nickname, '-', a.nombre);
                const option = document.createElement('option');
                option.value = a.nickname;
                option.textContent = a.nombre;
                aerolineaSelect.appendChild(option);
            });

            console.log('🎉 Aerolíneas cargadas exitosamente en el select');
        })
        .catch(error => {
            console.error('❌ Error crítico al cargar aerolíneas:', error);
            console.error('🔍 Detalles del error:', {
                name: error.name,
                message: error.message,
                stack: error.stack
            });
            mostrarMensajeError('Error al cargar aerolíneas: ' + error.message);
        });

    // Event listener para cambio de aerolínea
    aerolineaSelect.addEventListener('change', function() {
        const nickname = this.value;
        console.log('🔄 Aerolínea cambiada:', nickname);

        rutaSelect.innerHTML = '<option value="">Seleccione ruta...</option>';
        vueloSelect.innerHTML = '<option value="">Primero seleccione ruta</option>';
        rutaSelect.disabled = true;
        vueloSelect.disabled = true;
        resultadoConsulta.style.display = 'none';

        if (nickname) {
            const rutasUrl = `WebAppApache_war_exploded/api/rutas?nombreAerolinea=${encodeURIComponent(nickname)}`;
            console.log('📡 Solicitando rutas para aerolínea:', nickname);
            console.log('🌐 URL de rutas:', rutasUrl);

            fetch(rutasUrl)
                .then(response => {
                    console.log('📨 Respuesta de rutas - Status:', response.status);
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(rutas => {
                    console.log('✅ Rutas recibidas:', rutas);
                    console.log('📊 Número de rutas:', rutas.length);

                    rutaSelect.disabled = false;

                    if (rutas.length === 0) {
                        console.warn('⚠️ No hay rutas para esta aerolínea');
                        rutaSelect.innerHTML = '<option value="">No hay rutas disponibles</option>';
                        return;
                    }

                    rutas.forEach((r, index) => {
                        console.log(`   🛣️ Ruta ${index + 1}:`, r.nombre, '-', r.descripcion);
                        const option = document.createElement('option');
                        option.value = r.nombre;
                        option.textContent = `${r.nombre} - ${r.descripcion}`;
                        rutaSelect.appendChild(option);
                    });

                    console.log('🎉 Rutas cargadas exitosamente');
                })
                .catch(error => {
                    console.error('❌ Error al cargar rutas:', error);
                    mostrarMensajeError('Error al cargar rutas: ' + error.message);
                    rutaSelect.innerHTML = '<option value="">Error al cargar rutas</option>';
                });
        } else {
            console.log('🔽 Aerolínea deseleccionada');
        }
    });

    // Event listener para cambio de ruta
    rutaSelect.addEventListener('change', function() {
        const nombreRuta = this.value;
        console.log('🔄 Ruta cambiada:', nombreRuta);

        vueloSelect.innerHTML = '<option value="">Seleccione vuelo...</option>';
        vueloSelect.disabled = true;
        resultadoConsulta.style.display = 'none';

        if (nombreRuta) {
            const vuelosUrl = `WebAppApache_war_exploded/api/vuelos?nombreRuta=${encodeURIComponent(nombreRuta)}`;
            console.log('📡 Solicitando vuelos para ruta:', nombreRuta);
            console.log('🌐 URL de vuelos:', vuelosUrl);

            fetch(vuelosUrl)
                .then(response => {
                    console.log('📨 Respuesta de vuelos - Status:', response.status);
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(vuelos => {
                    console.log('✅ Vuelos recibidos:', vuelos);
                    console.log('📊 Número de vuelos:', vuelos.length);

                    vueloSelect.disabled = false;

                    if (vuelos.length === 0) {
                        console.warn('⚠️ No hay vuelos para esta ruta');
                        vueloSelect.innerHTML = '<option value="">No hay vuelos disponibles</option>';
                        return;
                    }

                    vuelos.forEach((v, index) => {
                        console.log(`   ✈️ Vuelo ${index + 1}:`, v.nombre, '-', v.fecha);
                        const option = document.createElement('option');
                        option.value = v.nombre;
                        option.textContent = `${v.nombre} - ${v.fecha}`;
                        vueloSelect.appendChild(option);
                    });

                    console.log('🎉 Vuelos cargados exitosamente');
                })
                .catch(error => {
                    console.error('❌ Error al cargar vuelos:', error);
                    mostrarMensajeError('Error al cargar vuelos: ' + error.message);
                    vueloSelect.innerHTML = '<option value="">Error al cargar vuelos</option>';
                });
        } else {
            console.log('🔽 Ruta deseleccionada');
        }
    });

    // Event listener para submit del formulario
    formConsulta.addEventListener('submit', function(event) {
        event.preventDefault();
        event.stopPropagation();

        console.log('📝 Formulario enviado');
        console.log('✅ Validación del formulario:', this.checkValidity());

        if (this.checkValidity()) {
            const nombreRuta = rutaSelect.value;
            const nombreVuelo = vueloSelect.value;

            console.log('🔍 Buscando detalles del vuelo:', {
                ruta: nombreRuta,
                vuelo: nombreVuelo
            });

            if (nombreRuta && nombreVuelo) {
                const detalleVueloUrl = `WebAppApache_war_exploded/api/vuelos?nombreRuta=${encodeURIComponent(nombreRuta)}`;
                console.log('🌐 URL para detalles del vuelo:', detalleVueloUrl);

                fetch(detalleVueloUrl)
                    .then(response => {
                        console.log('📨 Respuesta de detalles - Status:', response.status);
                        if (!response.ok) {
                            throw new Error(`HTTP error! status: ${response.status}`);
                        }
                        return response.json();
                    })
                    .then(vuelos => {
                        console.log('✅ Todos los vuelos recibidos para búsqueda:', vuelos);

                        const vueloData = vuelos.find(v => v.nombre === nombreVuelo);
                        console.log('🔍 Resultado de búsqueda del vuelo:', vueloData);

                        if (!vueloData) {
                            console.error('❌ Vuelo no encontrado:', nombreVuelo);
                            mostrarMensajeError('No se encontró el vuelo seleccionado');
                            return;
                        }

                        console.log('🎯 Mostrando detalles del vuelo:', vueloData);

                        // Actualizar la UI con los datos del vuelo
                        document.getElementById('nombreVueloDetalle').textContent = vueloData.nombre;
                        document.getElementById('aerolineaDetalle').textContent = aerolineaSelect.options[aerolineaSelect.selectedIndex].text;
                        document.getElementById('rutaDetalle').textContent = nombreRuta;
                        document.getElementById('fechaVueloDetalle').textContent = vueloData.fecha || 'No disponible';
                        document.getElementById('duracionVueloDetalle').textContent = vueloData.duracion || 'No disponible';
                        document.getElementById('horaSalidaDetalle').textContent = vueloData.horaSalida || 'No disponible';
                        document.getElementById('horaLlegadaDetalle').textContent = vueloData.horaLlegada || 'No disponible';
                        document.getElementById('asientosTuristaDetalle').textContent = vueloData.asientosTurista || '0';
                        document.getElementById('asientosEjecutivoDetalle').textContent = vueloData.asientosEjecutivo || '0';
                        document.getElementById('estadoVueloDetalle').textContent = vueloData.estado || 'Confirmado';
                        document.getElementById('imagenVueloDetalle').src = vueloData.imagen || '';
                        document.getElementById('totalReservas').textContent = vueloData.reservas ? vueloData.reservas.length : 0;

                        document.getElementById('infoAerolinea').style.display = 'none';
                        document.getElementById('infoCliente').style.display = 'none';
                        document.getElementById('btnReservar').style.display = 'block';

                        resultadoConsulta.style.display = 'block';
                        resultadoConsulta.classList.add('fade-in');
                        resultadoConsulta.scrollIntoView({ behavior: 'smooth' });

                        console.log('🎉 Detalles del vuelo mostrados exitosamente');
                    })
                    .catch(error => {
                        console.error('❌ Error al consultar vuelo:', error);
                        mostrarMensajeError('Error al consultar vuelo: ' + error.message);
                    });
            } else {
                console.warn('⚠️ Faltan datos para consultar el vuelo');
            }
        } else {
            console.warn('⚠️ Formulario no válido');
        }
        this.classList.add('was-validated');
    });

    // Event listener para botón limpiar
    document.getElementById('btnLimpiar').addEventListener('click', function() {
        console.log('🧹 Limpiando formulario');
        resultadoConsulta.style.display = 'none';
        formConsulta.classList.remove('was-validated');
        aerolineaSelect.value = '';
        rutaSelect.innerHTML = '<option value="">Primero seleccione aerolínea</option>';
        rutaSelect.disabled = true;
        vueloSelect.innerHTML = '<option value="">Primero seleccione ruta</option>';
        vueloSelect.disabled = true;
        console.log('✅ Formulario limpiado');
    });

    // Validación en tiempo real de campos requeridos
    const camposRequeridos = formConsulta.querySelectorAll('[required]');
    console.log('📝 Campos requeridos encontrados:', camposRequeridos.length);

    camposRequeridos.forEach((campo, index) => {
        console.log(`   📋 Campo ${index + 1}:`, campo.name || campo.id);
        campo.addEventListener('change', function() {
            const isValid = this.value.trim() !== '';
            console.log(`🔄 Campo ${this.name || this.id} cambiado:`, {
                valor: this.value,
                válido: isValid
            });

            if (isValid) {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
            } else {
                this.classList.remove('is-valid');
                this.classList.add('is-invalid');
            }
        });
    });

    console.log('🎊 Inicialización de consulta de vuelos completada');
}

function mostrarMensajeError(mensaje) {
    console.error('💥 Mostrando mensaje de error:', mensaje);

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
        console.log('📦 Contenedor de toasts creado');
        return container;
    })();

    toastContainer.innerHTML = toastHTML;
    const toastElement = toastContainer.querySelector('.toast');
    const toast = new bootstrap.Toast(toastElement);
    toast.show();

    console.log('📤 Toast de error mostrado');
}

// Función de utilidad para debug
function debugEstadoActual() {
    console.log('🐛 DEBUG - Estado actual:');
    console.log('  - aerolineaSelect:', document.getElementById('aerolinea')?.value);
    console.log('  - rutaSelect:', document.getElementById('rutaVuelo')?.value);
    console.log('  - vueloSelect:', document.getElementById('vuelo')?.value);
    console.log('  - resultadoConsulta visible:', document.getElementById('resultadoConsulta')?.style.display);
}

// Hacer disponible para debugging en consola
window.debugConsultaVuelo = debugEstadoActual;
console.log('🔧 Función de debug disponible: window.debugConsultaVuelo()');
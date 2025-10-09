// Datos simulados de usuarios
const usuarios = {
    "maria_gonzalez": {
        tipo: "Cliente",
        nombre: "María González",
        nickname: "maria_gonzalez",
        correo: "maria.gonzalez@email.com",
        fechaRegistro: "15/03/2024",
        imagen: "https://via.placeholder.com/120/3498db/ffffff?text=MG",
        datosPersonales: {
            apellido: "González",
            nacimiento: "12/08/1990",
            nacionalidad: "Uruguaya",
            documento: "Pasaporte: AB123456"
        },
        reservas: [
            { id: "RES-001", vuelo: "ZL1502001", fecha: "25/10/2024", estado: "Confirmada" },
            { id: "RES-002", vuelo: "IB6012201", fecha: "28/10/2024", estado: "Pendiente" }
        ],
        paquetes: [
            { id: "PKG-001", nombre: "Sudamérica Esencial", compra: "20/03/2024", vencimiento: "20/09/2024", estado: "Vigente" }
        ]
    },
    "carlos_rodriguez": {
        tipo: "Cliente",
        nombre: "Carlos Rodríguez",
        nickname: "carlos_rodr",
        correo: "carlos.rodriguez@email.com",
        fechaRegistro: "22/02/2024",
        imagen: "https://via.placeholder.com/120/e74c3c/ffffff?text=CR",
        datosPersonales: {
            apellido: "Rodríguez",
            nacimiento: "03/11/1985",
            nacionalidad: "Argentina",
            documento: "DNI: 35.123.456"
        },
        reservas: [],
        paquetes: []
    },
    "zulyfly": {
        tipo: "Aerolínea",
        nombre: "ZulyFly Airlines",
        nickname: "zulyfly",
        correo: "info@zulyfly.com",
        fechaRegistro: "10/01/2024",
        imagen: "https://via.placeholder.com/120/2ecc71/ffffff?text=ZF",
        descripcion: "Aerolínea uruguaya especializada en vuelos regionales dentro de Sudamérica.",
        sitioWeb: "https://www.zulyfly.com",
        rutas: [
            { id: "ZL1502", nombre: "Montevideo - Rio de Janeiro", estado: "Confirmada", fechaAlta: "15/01/2024" },
            { id: "ZL2001", nombre: "Montevideo - São Paulo", estado: "Confirmada", fechaAlta: "20/01/2024" },
            { id: "ZL3005", nombre: "Buenos Aires - Santiago", estado: "Ingresada", fechaAlta: "05/03/2024" },
            { id: "ZL4002", nombre: "Lima - Bogotá", estado: "Rechazada", fechaAlta: "12/02/2024" }
        ]
    },
    "iberia": {
        tipo: "Aerolínea",
        nombre: "Iberia",
        nickname: "iberia",
        correo: "contacto@iberia.com",
        fechaRegistro: "05/01/2024",
        imagen: "https://via.placeholder.com/120/9b59b6/ffffff?text=IB",
        descripcion: "Aerolínea bandera de España con conexiones a todo el mundo.",
        sitioWeb: "https://www.iberia.com",
        rutas: [
            { id: "IB6012", nombre: "Montevideo - Madrid", estado: "Confirmada", fechaAlta: "10/02/2024" },
            { id: "IB6015", nombre: "Madrid - París", estado: "Confirmada", fechaAlta: "15/02/2024" }
        ]
    },
    "copa_airlines": {
        tipo: "Aerolínea",
        nombre: "Copa Airlines",
        nickname: "copa_airlines",
        correo: "info@copaairlines.com",
        fechaRegistro: "08/01/2024",
        imagen: "https://via.placeholder.com/120/f39c12/ffffff?text=CA",
        descripcion: "Aerolínea panameña con conexiones en Centro y Norteamérica.",
        sitioWeb: "https://www.copaair.com",
        rutas: [
            { id: "CM804", nombre: "Ciudad de Panamá - Nueva York", estado: "Confirmada", fechaAlta: "12/02/2024" }
        ]
    }
};

let rutasActuales = [];

// Cargar información del usuario seleccionado
document.getElementById('usuarioSelect').addEventListener('change', function() {
    const usuarioId = this.value;
    const usuario = usuarios[usuarioId];

    if (usuario) {
        // Mostrar información básica
        document.getElementById('infoUsuario').style.display = 'block';
        document.getElementById('nombreUsuario').textContent = usuario.nombre;
        document.getElementById('nicknameUsuario').textContent = usuario.nickname;
        document.getElementById('correoUsuario').textContent = usuario.correo;
        document.getElementById('tipoUsuario').textContent = usuario.tipo;
        document.getElementById('fechaRegistro').textContent = usuario.fechaRegistro;
        document.getElementById('imagenUsuario').src = usuario.imagen;

        // Mostrar información específica según el tipo
        if (usuario.tipo === 'Cliente') {
            mostrarInfoCliente(usuario);
        } else if (usuario.tipo === 'Aerolínea') {
            mostrarInfoAerolinea(usuario);
        }
    } else {
        document.getElementById('infoUsuario').style.display = 'none';
    }
});

function mostrarInfoCliente(usuario) {
    // Mostrar sección de cliente y ocultar aerolínea
    document.getElementById('infoCliente').style.display = 'block';
    document.getElementById('infoAerolinea').style.display = 'none';

    // Datos personales
    document.getElementById('clienteApellido').textContent = usuario.datosPersonales.apellido;
    document.getElementById('clienteNacimiento').textContent = usuario.datosPersonales.nacimiento;
    document.getElementById('clienteNacionalidad').textContent = usuario.datosPersonales.nacionalidad;
    document.getElementById('clienteDocumento').textContent = usuario.datosPersonales.documento;

    // Reservas
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
                    </div>
                </div>
            `;
            reservasContainer.innerHTML += reservaHTML;
        });
    }

    // Paquetes
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
                        <p class="mb-1 small">Comprado: ${paquete.compra}</p>
                        <p class="mb-1 small">Vence: ${paquete.vencimiento}</p>
                        <span class="badge ${paquete.estado === 'Vigente' ? 'bg-success' : 'bg-secondary'} badge-estado">
                            ${paquete.estado}
                        </span>
                    </div>
                </div>
            `;
            paquetesContainer.innerHTML += paqueteHTML;
        });
    }
}

function mostrarInfoAerolinea(usuario) {
    // Mostrar sección de aerolínea y ocultar cliente
    document.getElementById('infoCliente').style.display = 'none';
    document.getElementById('infoAerolinea').style.display = 'block';

    // Información de la aerolínea
    document.getElementById('aerolineaDescripcion').textContent = usuario.descripcion;
    document.getElementById('aerolineaWeb').innerHTML = usuario.sitioWeb ?
        `<a href="${usuario.sitioWeb}" target="_blank">${usuario.sitioWeb}</a>` : 'No especificado';
    document.getElementById('aerolineaFechaRegistro').textContent = usuario.fechaRegistro;
    document.getElementById('totalRutas').textContent = usuario.rutas.length;

    // Guardar rutas para filtrado
    rutasActuales = usuario.rutas;
    cargarRutasAerolinea('todas');
}

function cargarRutasAerolinea(filtro) {
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
                </div>
            </div>
        `;
        rutasContainer.innerHTML += rutaHTML;
    });
}

function filtrarRutas(filtro) {
    // Actualizar botones activos
    document.querySelectorAll('.btn-group .btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');

    cargarRutasAerolinea(filtro);
}

// Mejorar la experiencia de usuario
document.addEventListener('DOMContentLoaded', function() {
    // Agregar tooltips si es necesario
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Debug: Verificar que el evento se está registrando
    console.log('Consulta de Usuario - Script cargado correctamente');
});
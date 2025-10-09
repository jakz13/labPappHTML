// Datos simulados de paquetes mejorados
const paquetes = {
    "sudamerica": {
        nombre: "Sudamérica Esencial",
        costo: 1200,
        vigencia: "180 días",
        descripcion: "Descubre los destinos más emblemáticos de Sudamérica con este paquete esencial que incluye las rutas más populares.",
        beneficios: [
            "20% de descuento vs compra individual",
            "Flexibilidad para cambiar fechas",
            "Asistencia al viajero incluida",
            "Traslados aeropuerto-hotel"
        ],
        rutas: [
            {
                id: "ZL1502",
                nombre: "Montevideo - Rio de Janeiro",
                descripcionCorta: "Vuelo directo a las playas de Brasil",
                descripcionCompleta: "Disfrute de un vuelo directo desde Montevideo hasta la maravillosa ciudad de Rio de Janeiro. Con una duración de apenas 2 horas y 30 minutos, llegará listo para explorar las famosas playas de Copacabana e Ipanema, el Cristo Redentor y el Pan de Azúcar.",
                aerolinea: "ZulyFly",
                origen: "Montevideo, Uruguay (MVD)",
                destino: "Rio de Janeiro, Brasil (GIG)",
                duracion: "2h 30m",
                horaSalida: "07:15",
                turista: 320,
                ejecutivo: 550,
                equipaje: 25,
                categorias: "Internacionales, América, Cortos",
                estado: "Confirmada",
                imagen: "https://via.placeholder.com/400x250/3498db/ffffff?text=Montevideo-Rio"
            },
            {
                id: "AR2050",
                nombre: "Buenos Aires - Santiago",
                descripcionCorta: "Cruzando la cordillera de los Andes",
                descripcionCompleta: "Experimente la majestuosidad de los Andes en este vuelo entre Buenos Aires y Santiago. Disfrute de vistas panorámicas de la cordillera mientras cruza hacia Chile, donde le esperan viñedos, montañas y la vibrante capital Santiago.",
                aerolinea: "Aerolíneas Argentinas",
                origen: "Buenos Aires, Argentina (EZE)",
                destino: "Santiago, Chile (SCL)",
                duracion: "2h 15m",
                horaSalida: "14:30",
                turista: 280,
                ejecutivo: 480,
                equipaje: 30,
                categorias: "Internacionales, América",
                estado: "Confirmada",
                imagen: "https://via.placeholder.com/400x250/e74c3c/ffffff?text=BsAs-Santiago"
            },
            {
                id: "LA3040",
                nombre: "Lima - Bogotá",
                descripcionCorta: "Conectando capitales sudamericanas",
                descripcionCompleta: "Conecte dos de las capitales más importantes de Sudamérica en este vuelo directo. Desde la riqueza histórica de Lima hasta la energía moderna de Bogotá, explore la diversidad cultural del continente.",
                aerolinea: "LATAM",
                origen: "Lima, Perú (LIM)",
                destino: "Bogotá, Colombia (BOG)",
                duracion: "3h 45m",
                horaSalida: "10:20",
                turista: 350,
                ejecutivo: 620,
                equipaje: 28,
                categorias: "Internacionales, América",
                estado: "Confirmada",
                imagen: "https://via.placeholder.com/400x250/2ecc71/ffffff?text=Lima-Bogotá"
            }
        ]
    },
    "europa": {
        nombre: "Europa Grand Tour",
        costo: 2500,
        vigencia: "365 días",
        descripcion: "Vive la experiencia europea con este paquete premium que incluye las capitales más emblemáticas del viejo continente.",
        beneficios: [
            "30% de descuento vs compra individual",
            "Alojamiento 4 estrellas incluido",
            "Tours guiados en cada ciudad",
            "Transporte entre ciudades",
            "Seguro de viaje premium"
        ],
        rutas: [
            {
                id: "IB6012",
                nombre: "Madrid - París",
                descripcionCorta: "De la capital española a la ciudad luz",
                descripcionCompleta: "Viaje desde el vibrante Madrid hasta la romántica París en este vuelo que conecta dos de las capitales más fascinantes de Europa. Disfrute de la fusión entre la pasión española y la elegancia francesa.",
                aerolinea: "Iberia",
                origen: "Madrid, España (MAD)",
                destino: "París, Francia (CDG)",
                duracion: "2h 10m",
                horaSalida: "08:45",
                turista: 450,
                ejecutivo: 780,
                equipaje: 35,
                categorias: "Internacionales, Europa",
                estado: "Confirmada",
                imagen: "https://via.placeholder.com/400x250/9b59b6/ffffff?text=Madrid-París"
            }
        ]
    },
    "caribe": {
        nombre: "Caribe Paradise",
        costo: 1800,
        vigencia: "90 días",
        descripcion: "Escape tropical a los destinos más exclusivos del Caribe con todo incluido para unas vacaciones perfectas.",
        beneficios: [
            "Todo incluido: alojamiento y comidas",
            "Actividades acuáticas gratuitas",
            "Traslados privados",
            "Asistencia VIP en destino"
        ],
        rutas: [
            {
                id: "AA904",
                nombre: "Miami - Cancún",
                descripcionCorta: "Del glamour americano al paraíso mexicano",
                descripcionCompleta: "Escape desde la vibrante Miami hasta el paraíso caribeño de Cancún. Disfrute de aguas turquesas, playas de arena blanca y la rica cultura mexicana en este vuelo directo.",
                aerolinea: "American Airlines",
                origen: "Miami, USA (MIA)",
                destino: "Cancún, México (CUN)",
                duracion: "2h 30m",
                horaSalida: "11:20",
                turista: 380,
                ejecutivo: 650,
                equipaje: 32,
                categorias: "Internacionales, Caribe, Exclusivos",
                estado: "Confirmada",
                imagen: "https://via.placeholder.com/400x250/f39c12/ffffff?text=Miami-Cancún"
            }
        ]
    },
    "norteamerica": {
        nombre: "Norteamérica Explorer",
        costo: 2200,
        vigencia: "240 días",
        descripcion: "Descubre la diversidad de Norteamérica con este paquete que combina ciudades vibrantes y paisajes naturales impresionantes.",
        beneficios: [
            "25% de descuento vs compra individual",
            "Alquiler de auto incluido",
            "Entradas a parques nacionales",
            "Asistencia 24/7 en inglés y español"
        ],
        rutas: [
            {
                id: "UA845",
                nombre: "Nueva York - Los Ángeles",
                descripcionCorta: "Cruzando Estados Unidos de costa a costa",
                descripcionCompleta: "Vuele desde la vibrante Nueva York hasta el soleado Los Ángeles en este vuelo transcontinental. Experimente la diversidad cultural y geográfica de Estados Unidos en un solo viaje.",
                aerolinea: "United Airlines",
                origen: "Nueva York, USA (JFK)",
                destino: "Los Ángeles, USA (LAX)",
                duracion: "6h 30m",
                horaSalida: "09:15",
                turista: 420,
                ejecutivo: 720,
                equipaje: 30,
                categorias: "Nacionales, América",
                estado: "Confirmada",
                imagen: "https://via.placeholder.com/400x250/3498db/ffffff?text=NY-LA"
            }
        ]
    }
};

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    const paqueteSelect = document.getElementById("paqueteSelect");
    const infoPaquete = document.getElementById("infoPaquete");
    const infoRuta = document.getElementById("infoRuta");

    paqueteSelect.addEventListener("change", () => {
        const paqueteId = paqueteSelect.value;
        const paquete = paquetes[paqueteId];

        if(paquete) {
            // Mostrar información del paquete
            infoPaquete.style.display = "block";
            document.getElementById("nombrePaquete").textContent = paquete.nombre;
            document.getElementById("costoPaquete").textContent = paquete.costo;
            document.getElementById("vigenciaPaquete").textContent = paquete.vigencia;
            document.getElementById("cantidadRutas").textContent = paquete.rutas.length + " rutas";
            document.getElementById("descripcionPaquete").textContent = paquete.descripcion;

            // Mostrar beneficios
            const beneficiosList = document.getElementById("beneficiosPaquete");
            beneficiosList.innerHTML = "";
            paquete.beneficios.forEach(beneficio => {
                const li = document.createElement("li");
                li.innerHTML = `<i class="bi bi-check-circle-fill text-success"></i> ${beneficio}`;
                beneficiosList.appendChild(li);
            });

            // Mostrar rutas
            const rutasContainer = document.getElementById("rutasPaquete");
            rutasContainer.innerHTML = "";
            paquete.rutas.forEach((ruta, index) => {
                const rutaHTML = `
                    <div class="col-md-6">
                        <div class="ruta-item" onclick="mostrarRutaDetalle('${paqueteId}', ${index})">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <h6 class="mb-1">${ruta.nombre}</h6>
                                    <p class="mb-1 text-muted small">${ruta.descripcionCorta}</p>
                                    <p class="mb-0 small"><strong>${ruta.aerolinea}</strong> | ${ruta.duracion}</p>
                                </div>
                                <span class="badge bg-primary">${ruta.id}</span>
                            </div>
                        </div>
                    </div>
                `;
                rutasContainer.innerHTML += rutaHTML;
            });

            // Ocultar información de ruta
            infoRuta.style.display = "none";
        } else {
            infoPaquete.style.display = "none";
            infoRuta.style.display = "none";
        }
    });

    // Mejorar la experiencia de usuario con tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

function mostrarRutaDetalle(paqueteId, rutaIndex) {
    const ruta = paquetes[paqueteId].rutas[rutaIndex];

    if (ruta) {
        // Actualizar información de la ruta
        document.getElementById("rutaNombre").textContent = ruta.nombre;
        document.getElementById("rutaDescripcionCorta").textContent = ruta.descripcionCorta;
        document.getElementById("rutaDescripcionCompleta").textContent = ruta.descripcionCompleta;
        document.getElementById("rutaAerolinea").textContent = ruta.aerolinea;
        document.getElementById("rutaOrigen").textContent = ruta.origen;
        document.getElementById("rutaDestino").textContent = ruta.destino;
        document.getElementById("rutaDuracion").textContent = ruta.duracion;
        document.getElementById("rutaHoraSalida").textContent = ruta.horaSalida;
        document.getElementById("rutaTurista").textContent = ruta.turista;
        document.getElementById("rutaEjecutivo").textContent = ruta.ejecutivo;
        document.getElementById("rutaEquipaje").textContent = ruta.equipaje;
        document.getElementById("rutaCategorias").textContent = ruta.categorias;
        document.getElementById("rutaEstado").textContent = ruta.estado;
        document.getElementById("rutaImagen").src = ruta.imagen;

        // Mostrar sección de información de ruta
        document.getElementById("infoRuta").style.display = "block";

        // Remover selección anterior y marcar actual
        document.querySelectorAll('.ruta-item').forEach(item => {
            item.classList.remove('selected');
        });
        event.currentTarget.classList.add('selected');

        // Scroll a la información de la ruta
        document.getElementById("infoRuta").scrollIntoView({ behavior: 'smooth' });
    }
}

function consultarVuelos() {
    const rutaNombre = document.getElementById("rutaNombre").textContent;

    // Crear modal de confirmación
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
                        <a href="consulta-vuelo.html" class="btn btn-primary">Continuar</a>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Agregar modal al documento si no existe
    if (!document.getElementById('vuelosModal')) {
        document.body.insertAdjacentHTML('beforeend', modalHTML);
    }

    // Mostrar modal
    const vuelosModal = new bootstrap.Modal(document.getElementById('vuelosModal'));
    vuelosModal.show();
}
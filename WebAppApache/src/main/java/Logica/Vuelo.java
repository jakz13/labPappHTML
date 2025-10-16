package Logica;

import DataTypes.DtReserva;
import DataTypes.DtVuelo;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "vuelos")
public class Vuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String NombreAereolinea;
    private LocalDate fecha;
    private int duracion;
    private int asientosTurista;
    private int asientosEjecutivo;
    private LocalDate fechaAlta;

    @ManyToOne
    @JoinColumn(name = "ruta_vuelo_id")
    private RutaVuelo rutaVuelo;

    @OneToMany(mappedBy = "vuelo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reserva> reservasList = new ArrayList<>();

    @Transient
    private Map<String, Reserva> reservas = new HashMap<>();

    public Vuelo() {}

    public Vuelo(String nombre, String nombreAereolinea, RutaVuelo rutaVuelo, LocalDate fecha, int duracion, int asientosTurista, int asientosEjecutivo, LocalDate fechaAlta) {
        this.nombre = nombre;
        this.NombreAereolinea = nombreAereolinea;
        this.rutaVuelo = rutaVuelo;
        this.fecha = fecha;
        this.duracion = duracion;
        this.asientosTurista = asientosTurista;
        this.asientosEjecutivo = asientosEjecutivo;
        this.fechaAlta = fechaAlta;
    }

    // ===== Getters y Setters =====
    public String getNombre() { return nombre; }
    public String getNombreAerolinea() { return NombreAereolinea; }
    public LocalDate getFecha() { return fecha; }
    public int getDuracion() { return duracion; }
    public int getAsientosTurista() { return asientosTurista; }
    public int getAsientosEjecutivo() { return asientosEjecutivo; }
    public LocalDate getFechaAlta() { return fechaAlta; }
    public RutaVuelo getRutaVuelo() { return rutaVuelo; }

    public Map<String, Reserva> getReservas() {
        reservas.clear();
        for (Reserva r : reservasList) {
            reservas.put(r.getId(), r);
        }
        return reservas;
    }

    public void agregarReserva(String idReserva, Reserva reserva) {
        reservasList.add(reserva);
        reservas.put(idReserva, reserva);
    }

    public List<Reserva> getReservasList() { return reservasList; }

    public String getRutaVueloNombre() {
        return rutaVuelo.getNombre();
    }

    @Override
    public String toString() {
        return nombre;
    }

    public List<DtReserva> getDtReservas() {
        return reservasList.stream()
                .map(reserva -> new DtReserva(
                        reserva.getId(),
                        reserva.getFecha(),
                        reserva.getCosto(),
                        reserva.getTipoAsiento(),
                        reserva.getCantidadPasajes(),
                        reserva.getUnidadesEquipajeExtra(),
                        reserva.getDtPasajeros(),
                        this.getNombre()
                ))
                .toList();
    }

    public DtVuelo getDtVuelo() {
        // Crear un DtRutaVuelo simplificado sin vuelos para evitar recursión
        DataTypes.DtRutaVuelo dtRutaSimplificada = new DataTypes.DtRutaVuelo(
                rutaVuelo.getNombre(),
                rutaVuelo.getDescripcion(),
                rutaVuelo.getDescripcionCorta(),
                rutaVuelo.getAerolinea() != null ? rutaVuelo.getAerolinea().getNombre() : null,
                rutaVuelo.getCiudadOrigen(),
                rutaVuelo.getCiudadDestino(),
                rutaVuelo.getHora(),
                rutaVuelo.getFechaAlta(),
                rutaVuelo.getCostoTurista(),
                rutaVuelo.getCostoEjecutivo(),
                rutaVuelo.getCostoEquipajeExtra(),
                rutaVuelo.getEstado() != null ? rutaVuelo.getEstado().toString() : "INGRESADA",
                rutaVuelo.getCategorias() != null ? rutaVuelo.getCategorias() : new ArrayList<>(),
                new ArrayList<>() // Lista vacía de vuelos para evitar recursión
        );

        return new DtVuelo(
                this.nombre,
                this.NombreAereolinea,
                this.fecha,
                this.duracion,
                this.asientosTurista,
                this.asientosEjecutivo,
                this.fechaAlta,
                dtRutaSimplificada, // Usar la versión simplificada
                this.getDtReservas()
        );
    }

    // Método alternativo sin información de ruta (para casos donde no se necesita)
    public DtVuelo getDtVueloSinRuta() {
        return new DtVuelo(
                this.nombre,
                this.NombreAereolinea,
                this.fecha,
                this.duracion,
                this.asientosTurista,
                this.asientosEjecutivo,
                this.fechaAlta,
                null, // Sin información de ruta
                this.getDtReservas()
        );
    }
}
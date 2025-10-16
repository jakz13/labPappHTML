package Logica;

import DataTypes.DtRutaVuelo;
import DataTypes.DtVuelo;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rutas_vuelo")
public class RutaVuelo {

    public enum EstadoRuta {
        INGRESADA,
        CONFIRMADA,
        RECHAZADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String descripcionCorta;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @ManyToOne
    @JoinColumn(name = "aerolinea_id")
    private Aerolinea aerolinea;

    private String ciudadOrigen;
    private String ciudadDestino;
    private String hora;
    private LocalDate fechaAlta;

    private double costoTurista;
    private double costoEjecutivo;
    private double costoEquipajeExtra;

    @Enumerated(EnumType.STRING)
    private EstadoRuta estado = EstadoRuta.INGRESADA;

    @ElementCollection
    @CollectionTable(name = "ruta_categorias", joinColumns = @JoinColumn(name = "ruta_id"))
    @Column(name = "categoria")
    private List<String> categorias = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ruta_vuelo_id")
    private List<Vuelo> vuelos = new ArrayList<>();

    public RutaVuelo() {
        this.estado = EstadoRuta.INGRESADA;
        this.categorias = new ArrayList<>();
        this.vuelos = new ArrayList<>();
        this.imagenUrl = null;
    }

    public RutaVuelo(String nombre, String descripcion, String descripcionCorta, Aerolinea aerolinea,
                     String ciudadOrigen, String ciudadDestino, String hora, LocalDate fechaAlta,
                     double costoTurista, double costoEjecutivo, double costoEquipajeExtra,
                     List<String> categorias) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.descripcionCorta = descripcionCorta;
        this.aerolinea = aerolinea;
        this.ciudadOrigen = ciudadOrigen;
        this.ciudadDestino = ciudadDestino;
        this.hora = hora;
        this.fechaAlta = fechaAlta;
        this.costoTurista = costoTurista;
        this.costoEjecutivo = costoEjecutivo;
        this.costoEquipajeExtra = costoEquipajeExtra;
        this.categorias = (categorias != null) ? categorias : new ArrayList<>();
        this.estado = EstadoRuta.INGRESADA;
        this.vuelos = new ArrayList<>();
        this.imagenUrl = null;
    }

    // ===== Getters y Setters =====
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getDescripcionCorta() {
        return (descripcionCorta != null) ? descripcionCorta : "";
    }
    public String getImagenUrl() { return imagenUrl; }
    public Aerolinea getAerolinea() { return aerolinea; }
    public String getCiudadOrigen() { return ciudadOrigen; }
    public String getCiudadDestino() { return ciudadDestino; }
    public String getHora() { return hora; }
    public LocalDate getFechaAlta() { return fechaAlta; }
    public double getCostoTurista() { return costoTurista; }
    public double getCostoEjecutivo() { return costoEjecutivo; }
    public double getCostoEquipajeExtra() { return costoEquipajeExtra; }
    public EstadoRuta getEstado() {
        return (estado != null) ? estado : EstadoRuta.INGRESADA;
    }
    public List<String> getCategorias() {
        return (categorias != null) ? categorias : new ArrayList<>();
    }
    public List<Vuelo> getVuelos() {
        return (vuelos != null) ? vuelos : new ArrayList<>();
    }

    public void setEstado(EstadoRuta estado) { this.estado = estado; }
    public void setDescripcionCorta(String descripcionCorta) { this.descripcionCorta = descripcionCorta; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public void agregarVuelo(Vuelo vuelo) {
        if (vuelos == null) {
            vuelos = new ArrayList<>();
        }
        vuelos.add(vuelo);
    }

    public Vuelo getVuelo(String nombreVuelo) {
        if (vuelos == null) return null;

        for (Vuelo v : vuelos) {
            if (v.getNombre().equals(nombreVuelo)) return v;
        }
        return null;
    }

    @Override
    public String toString() { return nombre; }

    public List<DtVuelo> getDtVuelos() {
        List<DtVuelo> dtVuelos = new ArrayList<>();
        if (vuelos != null) {
            for (Vuelo v : vuelos) {
                dtVuelos.add(v.getDtVueloSinRuta());
            }
        }
        return dtVuelos;
    }

    public DtRutaVuelo getDtRutaVuelo() {
        String nombreAerolinea = (aerolinea != null) ? aerolinea.getNombre() : null;
        List<DtVuelo> dtVuelos = getDtVuelos();
        String estadoStr = (estado != null) ? estado.toString() : "INGRESADA";
        String descCorta = (descripcionCorta != null) ? descripcionCorta : "";
        String imgUrl = (imagenUrl != null) ? imagenUrl : null;

        return new DtRutaVuelo(
                nombre,
                descripcion,
                descCorta,
                imgUrl,
                nombreAerolinea,
                ciudadOrigen,
                ciudadDestino,
                hora,
                fechaAlta,
                costoTurista,
                costoEjecutivo,
                costoEquipajeExtra,
                estadoStr,
                categorias != null ? categorias : new ArrayList<>(),
                dtVuelos
        );
    }
}
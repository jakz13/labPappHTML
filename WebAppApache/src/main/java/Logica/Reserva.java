package Logica;

import DataTypes.DtPasajero;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    private String id; // asumimos que tu id ya es único y se genera externamente

    private LocalDate fecha;
    private double costo;

    @Enumerated(EnumType.STRING)
    private TipoAsiento tipoAsiento;

    private int cantidadPasajes;
    private int unidadesEquipajeExtra;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "reserva_pasajeros",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "pasajero_id")
    )
    private List<Pasajero> pasajeros = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "vuelo_id")
    private Vuelo vuelo;

    public Reserva() {} // Constructor vacío para JPA

    public Reserva(String id, double costo, TipoAsiento tipoAsiento, int cantidadPasajes, int unidadesEquipajeExtra, List<Pasajero> pasajeros, Vuelo vuelo) {
        this.id = id;
        this.fecha = LocalDate.now();
        this.costo = costo;
        this.tipoAsiento = tipoAsiento;
        this.cantidadPasajes = cantidadPasajes;
        this.unidadesEquipajeExtra = unidadesEquipajeExtra;
        this.pasajeros = pasajeros;
        this.vuelo = vuelo;
    }

    // ===== Getters y Setters =====
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public double getCosto() { return costo; }
    public TipoAsiento getTipoAsiento() { return tipoAsiento; }
    public int getCantidadPasajes() { return cantidadPasajes; }
    public int getUnidadesEquipajeExtra() { return unidadesEquipajeExtra; }
    public List<Pasajero> getPasajeros() { return pasajeros; }
    public Vuelo getVuelo() { return vuelo; }
    public void setVuelo(Vuelo vuelo) { this.vuelo = vuelo; }
    public String toString() {
        return "Reserva #" + id +
                " | Vuelo: " + (vuelo != null ? vuelo.getNombre() : "N/A");
    }

    public List<DtPasajero> getDtPasajeros() {
        List<DtPasajero> dtPasajeros = new ArrayList<>();
        for (Pasajero p : pasajeros) {
            dtPasajeros.add(new DtPasajero(p.getNombre(), p.getApellido()));
        }
        return dtPasajeros;
    }
}

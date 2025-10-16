package DataTypes;

import Logica.TipoAsiento;

import java.time.LocalDate;
import java.util.List;

public class DtReserva {
    private String id;
    private LocalDate fecha;
    private double costo;
    private TipoAsiento tipoAsiento;
    private int cantidadPasajes;
    private int unidadesEquipajeExtra;
    private List<DtPasajero> pasajeros;
    private String vuelo; // nombre del vuelo, para no exponer la entidad completa

    public DtReserva(String id, LocalDate fecha, double costo, TipoAsiento tipoAsiento,
                     int cantidadPasajes, int unidadesEquipajeExtra,
                     List<DtPasajero> pasajeros, String vuelo) {
        this.id = id;
        this.fecha = fecha;
        this.costo = costo;
        this.tipoAsiento = tipoAsiento;
        this.cantidadPasajes = cantidadPasajes;
        this.unidadesEquipajeExtra = unidadesEquipajeExtra;
        this.pasajeros = pasajeros;
        this.vuelo = vuelo;
    }

    // ===== Getters =====

    public LocalDate getFecha() { return fecha; }
    public double getCosto() { return costo; }
    public TipoAsiento getTipoAsiento() { return tipoAsiento; }
    public int getCantidadPasajes() { return cantidadPasajes; }
    public int getUnidadesEquipajeExtra() { return unidadesEquipajeExtra; }
    public List<DtPasajero> getPasajeros() { return pasajeros; }
    public String getVuelo() { return vuelo; }
    public String getId() { return id; } // usar el nombre del vuelo como ID

    @Override
    public String toString() {
        return "Reserva #" +
                " | Vuelo: " + vuelo;
    }
}

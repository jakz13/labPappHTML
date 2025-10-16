package Logica;

import jakarta.persistence.*;

@Entity
@Table(name = "item_paquete")
public class ItemPaquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ruta_vuelo_id", nullable = false)
    private RutaVuelo rutaVuelo;

    @Column(nullable = false)
    private int cantAsientos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAsiento tipoAsiento;

    public ItemPaquete() {
    }

    public ItemPaquete(RutaVuelo rutaVuelo, int cantAsientos, TipoAsiento tipoAsiento) {
        this.rutaVuelo = rutaVuelo;
        this.cantAsientos = cantAsientos;
        this.tipoAsiento = tipoAsiento;
    }

    public RutaVuelo getRutaVuelo() {
        return rutaVuelo;
    }

    public int getCantAsientos() {
        return cantAsientos;
    }

    public TipoAsiento getTipoAsiento() {
        return tipoAsiento;
    }

    public void incrementarCantidad(int cantidad) {
        this.cantAsientos += cantidad;
    }

    public double calcularCostoItem() {
        if (rutaVuelo == null) {
            throw new IllegalStateException("Ruta de vuelo no definida para este item");
        }

        double costoBase;
        if (tipoAsiento == TipoAsiento.TURISTA) {
            costoBase = rutaVuelo.getCostoTurista();
        } else {
            costoBase = rutaVuelo.getCostoEjecutivo();
        }

        return costoBase * cantAsientos;
    }
}

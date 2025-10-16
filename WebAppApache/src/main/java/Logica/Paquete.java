package Logica;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paquetes")
public class Paquete {

    @Id
    private String nombre; // usamos el nombre como PK

    @Column(nullable = false)
    private String descripcion;

    private double costo;

    private LocalDate fechaAlta;

    private int descuentoPorc;

    private int periodoValidezDias;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "paquete_id") // FK en item_paquete
    private List<ItemPaquete> ItemPaquetes = new ArrayList<>();

    @OneToMany(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompraPaqLogica> Compras = new ArrayList<>();

    public Paquete() {
    }

    public Paquete(String nombre, String descripcion, int descuentoPorc, int periodoValidezDias) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costo = 0.0;
        this.fechaAlta = LocalDate.now();
        this.descuentoPorc = descuentoPorc;
        this.periodoValidezDias = periodoValidezDias;
    }

    public void setCosto(double costo) { this.costo = costo; }

    public String getNombre(){ return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getCosto() {
        double costoTotal = 0;
        for (ItemPaquete item : this.getItemPaquetes()) {
            double costoRuta = item.getTipoAsiento() == TipoAsiento.TURISTA
                    ? item.getRutaVuelo().getCostoTurista()
                    : item.getRutaVuelo().getCostoEjecutivo();
            costoTotal += costoRuta * item.getCantAsientos();
        }
        return costoTotal * (1 - this.getDescuentoPorc() / 100.0);
    }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public int getDescuentoPorc() { return descuentoPorc; }
    public int getPeriodoValidezDias() { return periodoValidezDias; }
    public List<ItemPaquete> getItemPaquetes() { return ItemPaquetes; }
    public List<CompraPaqLogica> getCompras() { return Compras; }

    public String getDescuento() {
        if (descuentoPorc > 0) {
            return "Descuento: " + descuentoPorc + "%";
        } else {
            return "Sin descuento";
        }
    }

    public String getPeriodoValidez() {
        if (periodoValidezDias > 0) {
            return "Periodo de validez: " + periodoValidezDias + " días";
        } else {
            return "Sin periodo de validez";
        }
    }

    public boolean estaComprado() {
        return !Compras.isEmpty();
    }

    public double calcularCostoReservaPaquete() {
        double costoTotal = 0.0;

        for (ItemPaquete item : ItemPaquetes) {
            costoTotal += item.calcularCostoItem();
        }

        if (descuentoPorc > 0) {
            double descuento = costoTotal * (descuentoPorc / 100.0);
            costoTotal -= descuento;
        }

        this.costo = costoTotal;
        return costoTotal;
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}

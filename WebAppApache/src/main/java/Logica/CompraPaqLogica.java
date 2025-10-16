package Logica;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "compras_paquetes")
public class CompraPaqLogica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paquete_id")
    private Paquete paquete;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVenc;

    private double costo;

    public CompraPaqLogica() {}

    public CompraPaqLogica(Cliente cliente, Paquete paquete, LocalDate fechaCompra, int validezDias, double costo) {
        this.cliente = cliente;
        this.paquete = paquete;
        this.fechaCompra = fechaCompra;
        this.fechaVenc = fechaCompra.plusDays(validezDias);
        this.costo = costo;
    }

    // --- Getters ---
    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Paquete getPaquete() { return paquete; }
    public LocalDate getFechaCompra() { return fechaCompra; }
    public LocalDate getFechaVenc() { return fechaVenc; }
    public double getCosto() { return costo; }

    // --- Setters ---
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public void setPaquete(Paquete paquete) { this.paquete = paquete; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; }
    public void setFechaVenc(LocalDate fechaVenc) { this.fechaVenc = fechaVenc; }
    public void setCosto(double costo) { this.costo = costo; }
}



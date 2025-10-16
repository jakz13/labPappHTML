package DataTypes;

import Logica.Paquete;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DtPaquete {
    private String nombre;
    private String descripcion;
    private double costo;
    private LocalDate fechaAlta;
    private int descuentoPorc;
    private int periodoValidezDias;
    private List<DtItemPaquete> items = new ArrayList<>();

    public DtPaquete(String nombre, String descripcion, double costo,
                     LocalDate fechaAlta, int descuentoPorc,
                     int periodoValidezDias, List<DtItemPaquete> items) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costo = costo;
        this.fechaAlta = fechaAlta;
        this.descuentoPorc = descuentoPorc;
        this.periodoValidezDias = periodoValidezDias;
        if (items != null) {
            this.items = items;
        }
    }

    public DtPaquete(Paquete p) {
        this.nombre = p.getNombre();
        this.descripcion = p.getDescripcion();
        this.costo = p.getCosto();
        this.fechaAlta = p.getFechaAlta();
        this.descuentoPorc = p.getDescuentoPorc();
        this.periodoValidezDias = p.getPeriodoValidezDias();

        if (p.getItemPaquetes() != null) {
            for (Logica.ItemPaquete item : p.getItemPaquetes()) {
                Logica.RutaVuelo ruta = item.getRutaVuelo();

                // Manejar valores nulos
                String descripcionCorta = (ruta.getDescripcionCorta() != null) ? ruta.getDescripcionCorta() : "";
                String estado = (ruta.getEstado() != null) ? ruta.getEstado().toString() : "INGRESADA";
                String nombreAerolinea = (ruta.getAerolinea() != null) ? ruta.getAerolinea().getNombre() : null;
                List<DtVuelo> dtVuelos = (ruta.getDtVuelos() != null) ? ruta.getDtVuelos() : new ArrayList<>();

                DtRutaVuelo dtRuta = new DtRutaVuelo(
                        ruta.getNombre(),
                        ruta.getDescripcion(),
                        descripcionCorta, // Campo manejado contra nulos
                        nombreAerolinea,  // Campo manejado contra nulos
                        ruta.getCiudadOrigen(),
                        ruta.getCiudadDestino(),
                        ruta.getHora(),
                        ruta.getFechaAlta(),
                        ruta.getCostoTurista(),
                        ruta.getCostoEjecutivo(),
                        ruta.getCostoEquipajeExtra(),
                        estado, // Campo manejado contra nulos
                        ruta.getCategorias() != null ? ruta.getCategorias() : new ArrayList<>(),
                        dtVuelos // Campo manejado contra nulos
                );

                DtItemPaquete dtItem = new DtItemPaquete(
                        dtRuta,
                        item.getCantAsientos(),
                        item.getTipoAsiento().toString()
                );

                this.items.add(dtItem);
            }
        }
    }

    // --- Getters y Setters ---
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public int getDescuentoPorc() {
        return descuentoPorc;
    }

    public void setDescuentoPorc(int descuentoPorc) {
        this.descuentoPorc = descuentoPorc;
    }

    public int getPeriodoValidezDias() {
        return periodoValidezDias;
    }

    public void setPeriodoValidezDias(int periodoValidezDias) {
        this.periodoValidezDias = periodoValidezDias;
    }

    public List<DtItemPaquete> getItems() {
        return items;
    }

    public void setItems(List<DtItemPaquete> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}
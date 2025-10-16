package DataTypes;

import java.time.LocalDate;

public class DtCiudad {
    private String nombre;
    private String pais;
    private String aeropuerto;
    private String descripcion;
    private String sitioWeb;
    private LocalDate fechaAlta;

    public DtCiudad() {
    }

    public DtCiudad(Long id, String nombre, String pais, String aeropuerto, String descripcion, String sitioWeb, LocalDate fechaAlta) {
        this.nombre = nombre;
        this.pais = pais;
        this.aeropuerto = aeropuerto;
        this.descripcion = descripcion;
        this.sitioWeb = sitioWeb;
        this.fechaAlta = fechaAlta;
    }

    public DtCiudad(String nombre, String pais) {
        this.nombre = nombre;
        this.pais = pais;
    }

    // --- Getters y Setters ---




    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getAeropuerto() {
        return aeropuerto;
    }

    public void setAeropuerto(String aeropuerto) {
        this.aeropuerto = aeropuerto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}

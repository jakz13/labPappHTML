package Logica;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre; // clave primaria

    public Categoria() {
    }

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    // --- Getters y Setters ---
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}


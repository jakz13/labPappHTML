package Logica;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Usuario {

    @Id
    @Column(unique = true, nullable = false)
    private String nickname;

    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "password_salt")
    private String passwordSalt;

    @Column(name = "imagen_url")
    private String imagenUrl;

    public Usuario() {
        this.nickname = "";
        this.nombre = "";
        this.email = "";
        this.imagenUrl = null; // Opcional - puede ser null
    }

    public Usuario(String nickname, String nombre, String email, String password) {
        this.nickname = nickname;
        this.nombre = nombre;
        this.email = email;
        this.imagenUrl = null; // Por defecto sin imagen
        setPassword(password);
    }

    // ===== Getters y Setters =====
    public String getNickname() { return nickname; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getPasswordSalt() { return passwordSalt; }
    public String getImagenUrl() { return imagenUrl; }

    public void setEmail(String email) { this.email = email; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        this.passwordSalt = PasswordManager.generarSalt();
        this.passwordHash = PasswordManager.hashPassword(password, this.passwordSalt);
    }

    public boolean verificarPassword(String password) {
        return PasswordManager.verificarPassword(password, this.passwordSalt, this.passwordHash);
    }

    @Override
    public String toString() {
        return nickname;
    }
}
package DataTypes;

public class DtUsuario {
    private String nickname;
    private String nombre;
    private String email;
    private String imagenUrl; 

    public DtUsuario() {
    }

    public DtUsuario(String nickname, String nombre, String email) {
        this.nickname = nickname;
        this.nombre = nombre;
        this.email = email;
        this.imagenUrl = null;
    }

    public DtUsuario(String nickname, String nombre, String email, String imagenUrl) {
        this.nickname = nickname;
        this.nombre = nombre;
        this.email = email;
        this.imagenUrl = imagenUrl;
    }

    // --- Getters y Setters ---
    public String getNickname() { return nickname; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getImagenUrl() { return imagenUrl; }

    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    @Override
    public String toString() {
        return this.nombre;
    }
}
package DataTypes;

import Logica.Cliente;
import Logica.TipoDoc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DtCliente extends DtUsuario {
    private String apellido;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
    private TipoDoc tipoDocumento;
    private String numeroDocumento;
    private List<DtReserva> reservas = new ArrayList<>();
    private List<DtPaquete> paquetesComprados = new ArrayList<>();

    public DtCliente() {
    }

    public DtCliente(String nickname, String nombre, String email,
                     String apellido, LocalDate fechaNacimiento,
                     String nacionalidad, TipoDoc tipoDocumento,
                     String numeroDocumento) {
        super(nickname, nombre, email);
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.nacionalidad = nacionalidad;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
    }

    public DtCliente(String nickname, String nombre, String email, String imagenUrl,
                     String apellido, LocalDate fechaNacimiento,
                     String nacionalidad, TipoDoc tipoDocumento,
                     String numeroDocumento) {
        super(nickname, nombre, email, imagenUrl);
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.nacionalidad = nacionalidad;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
    }

    public DtCliente(Cliente cliente) {
        super(cliente.getNickname(), cliente.getNombre(), cliente.getEmail(), cliente.getImagenUrl());
        this.apellido = cliente.getApellido();
        this.fechaNacimiento = cliente.getFechaNacimiento();
        this.nacionalidad = cliente.getNacionalidad();
        this.tipoDocumento = cliente.getTipoDocumento();
        this.numeroDocumento = cliente.getNumeroDocumento();
        this.reservas = cliente.getDtReservas();
        this.paquetesComprados = cliente.getDtPaquetesComprados();
    }

    // --- Getters y Setters ---
    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public TipoDoc getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDoc tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public List<DtReserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<DtReserva> reservas) {
        this.reservas = reservas;
    }

    public List<DtPaquete> getPaquetesComprados() {
        return paquetesComprados;
    }

    public void setPaquetesComprados(List<DtPaquete> paquetesComprados) {
        this.paquetesComprados = paquetesComprados;
    }

    @Override
    public String toString() {
        return this.getNombre();
    }
}
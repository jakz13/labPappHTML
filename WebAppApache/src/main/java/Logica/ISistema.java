package Logica;

import DataTypes.*;

import java.time.LocalDate;
import java.util.List;

public interface ISistema {

    // =================== Inicialización ===================
    void cargarDesdeBd();

    // =================== Usuarios ===================
    void altaCliente(String nickname, String nombre, String apellido, String correo,
                     LocalDate fechaNac, String nacionalidad, TipoDoc tipoDoc,
                     String numDoc, String password);

    void altaAerolinea(String nickname, String nombre, String descripcion,
                       String email, String sitioWeb, String password);

    // Métodos de login
    boolean verificarLogin(String email, String password);
    String obtenerTipoUsuario(String email);

    List<DtCliente> listarClientes();
    Cliente verInfoCliente(String nickname);
    DtCliente obtenerCliente(String nickname);

    Aerolinea verInfoAerolinea(String nickname);
    DtAerolinea obtenerAerolinea(String nickname);
    List<DtAerolinea> listarAerolineas();

    // =================== Ciudades ===================
    void altaCiudad(String nombre, String pais);
    List<DtCiudad> listarCiudades();

    // =================== Rutas de Vuelo ===================
    void altaRutaVuelo(String nombre, String descripcion, String descripcionCorta,
                       DtAerolinea aerolinea, String ciudadOrigen, String ciudadDestino,
                       String hora, LocalDate fechaAlta, double costoTurista,
                       double costoEjecutivo, double costoEquipajeExtra, String[] categorias);

    RutaVuelo obtenerRuta(String nombreRuta);
    List<DtRutaVuelo> listarRutasPorAerolinea(String nombreAerolinea);

    // Métodos para aceptar/rechazar rutas
    List<DtAerolinea> obtenerAerolineasConRutasPendientes();
    List<DtRutaVuelo> obtenerRutasPendientesPorAerolinea(String nombreAerolinea);
    void aceptarRutaVuelo(String nombreRuta);
    void rechazarRutaVuelo(String nombreRuta);

    // =================== Vuelos ===================
    void altaVuelo(String nombreVuelo, String nombreAereolinea, String nombreRuta,
                   LocalDate fecha, int duracion, int asientosTurista,
                   int asientosEjecutivo, LocalDate fechaAlta);

    Vuelo obtenerVuelo(String nombreVuelo);
    List<DtVuelo> listarVuelosPorRuta(String nombreRuta);
    Vuelo verInfoVuelo(String nombreVuelo);

    // =================== Reservas ===================
    void crearYRegistrarReserva(String nicknameCliente, String nombreVuelo, LocalDate fechaReserva, double costo,
                                TipoAsiento tipoAsiento, int cantidadPasajes, int unidadesEquipajeExtra,
                                List<Pasajero> pasajeros);

    void registrarReservaVuelo(String nicknameCliente, String nombreVuelo, Reserva reserva);
    DtReserva obtenerReserva(String idReserva, String nicknameCliente);
    double calcularCostoReserva(String nombreVuelo, TipoAsiento tipoAsiento, int cantidadPasajes, int unidadesEquipajeExtra);

    // =================== Paquetes ===================
    void altaPaquete(String nombre, String descripcion, int descuentoPorc, int periodoValidezDias);
    List<DtPaquete> listarPaquetes();
    void altaRutaPaquete(String nombrePaquete, String nomRuta, int cantidadAsientos, TipoAsiento tipoAsiento);
    Paquete obtenerPaquete(String nombrePaquete);
    DtPaquete obtenerDtPaquete(String nombrePaquete);
    List<DtPaquete> listarPaquetesDisp();
    void compraPaquete(String nomPaquete, String nomCliente, int validezDias, LocalDate fechaC, double costo);

    // =================== Categorías ===================
    void altaCategoria(String nomCat);
    List<DtCategoria> listarCategorias();

    // =================== Pasajeros ===================
    Pasajero crearPasajero(String nombre, String apellido);

    // =================== Modificación de datos ===================
    void modificarDatosDeCliente(String nickname, String nombre, String apellido, String nacionalidad,
                                 LocalDate fechaNacimiento, TipoDoc tipoDoc, String numeroDocumento);

    void modificarDatosAerolinea(String nickname, String Descripcion, String URL);

    // =================== Consultas adicionales ===================
    List<Object> obtenerDatosAdicionalesUsuario(String nickname);
    List<DtAerolinea> getAerolineas();
    List<DtPaquete> getPaquetesDisp();
    List<DtCliente> getClientes();
    List<DtReserva> getReservasCliente(String nickname);
    List<DtItemPaquete> getDtItemRutasPaquete(String nombrePaquete);

    // =================== MÉTODOS PARA IMÁGENES ===================
    void actualizarImagenCliente(String nickname, String imagenUrl);
    void actualizarImagenAerolinea(String nickname, String imagenUrl);
    void actualizarImagenRuta(String nombreRuta, String imagenUrl);

    //para obtener imágenes
    String obtenerImagenCliente(String nickname);
    String obtenerImagenAerolinea(String nickname);
    String obtenerImagenRuta(String nombreRuta);

}


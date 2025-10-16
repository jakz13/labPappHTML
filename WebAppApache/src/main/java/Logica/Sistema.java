package Logica;

import DataTypes.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sistema implements ISistema {

    private EntityManagerFactory emf;
    private EntityManager em;

    private ManejadorCliente manejadorCliente;
    private ManejadorAerolinea manejadorAerolinea;
    private ManejadorCiudad manejadorCiudad;
    private ManejadorRutaVuelo manejadorRutaVuelo;
    private ManejadorVuelo manejadorVuelo;
    private ManejadorPaquete manejadorPaquete;
    private ManejadorCategoria manejadorCategoria;
    private static int idReservaCounter = 0;

    public Sistema() {
        this.emf = Persistence.createEntityManagerFactory("LAB_PA");
        this.em = emf.createEntityManager();
        this.manejadorCliente = ManejadorCliente.getInstance();
        this.manejadorPaquete = ManejadorPaquete.getInstance();
        this.manejadorAerolinea = ManejadorAerolinea.getInstance();
        this.manejadorRutaVuelo = ManejadorRutaVuelo.getInstance();
        this.manejadorVuelo = ManejadorVuelo.getInstance();
        this.manejadorCiudad = ManejadorCiudad.getInstance();
        this.manejadorCategoria = ManejadorCategoria.getInstance();
    }

    @Override
    public void cargarDesdeBd() {
        manejadorCliente.cargarClientesDesdeBD(em);
        manejadorAerolinea.cargarAerolineasDesdeBD(em);
        manejadorCiudad.cargarCiudadesDesdeBD(em);
        manejadorRutaVuelo.cargarRutasDesdeBD(em);
        manejadorVuelo.cargarVuelosDesdeBD(em);
        manejadorPaquete.cargarPaquetesDesdeBD(em);
        manejadorCategoria.cargarCategoriasDesdeBD(em);
    }

    // =================== USUARIOS CON CONTRASEÑA ===================

    @Override
    public void altaCliente(String nickname, String nombre, String apellido, String correo,
                            LocalDate fechaNac, String nacionalidad, TipoDoc tipoDoc,
                            String numDoc, String password) {
        if (manejadorAerolinea.obtenerAerolinea(nickname) == null
                && manejadorCliente.obtenerCliente(nickname) == null) {
            Cliente c = new Cliente(nickname, nombre, apellido, correo, fechaNac, nacionalidad, tipoDoc, numDoc, password);
            manejadorCliente.agregarCliente(c, em);
        } else {
            throw new IllegalArgumentException("Ya existe un usuario con ese nickname");
        }
    }

    // Método antiguo sin contraseña (para backward compatibility si es necesario)
    public void altaCliente(String nickname, String nombre, String apellido, String correo,
                            LocalDate fechaNac, String nacionalidad, TipoDoc tipoDoc, String numDoc) {
        throw new UnsupportedOperationException("Use el método con contraseña: altaCliente(..., password)");
    }

    @Override
    public void altaAerolinea(String nickname, String nombre, String descripcion,
                              String email, String sitioWeb, String password) {
        if (manejadorAerolinea.obtenerAerolinea(nickname) == null
                && manejadorCliente.obtenerCliente(nickname) == null) {
            Aerolinea a = new Aerolinea(nickname, nombre, email, descripcion, sitioWeb, password);
            manejadorAerolinea.agregarAerolinea(a, em);
        } else {
            throw new IllegalArgumentException("Ya existe un usuario con ese nickname");
        }
    }

    // Método antiguo sin contraseña (para backward compatibility si es necesario)
    public void altaAerolinea(String nickname, String nombre, String descripcion,
                              String email, String sitioWeb) {
        throw new UnsupportedOperationException("Use el método con contraseña: altaAerolinea(..., password)");
    }

    // =================== LOGIN ===================

    @Override
    public boolean verificarLogin(String email, String password) {
        // Primero buscar como cliente
        if (manejadorCliente.verificarLogin(email, password)) {
            return true;
        }
        // Si no es cliente, buscar como aerolínea
        return manejadorAerolinea.verificarLogin(email, password);
    }

    @Override
    public String obtenerTipoUsuario(String email) {
        if (manejadorCliente.obtenerClientePorEmail(email) != null) {
            return "CLIENTE";
        } else if (manejadorAerolinea.obtenerAerolineaPorEmail(email) != null) {
            return "AEROLINEA";
        }
        return null;
    }
// =================== MÉTODOS PARA IMÁGENES ===================

    @Override
    public void actualizarImagenCliente(String nickname, String imagenUrl) {
        manejadorCliente.actualizarImagenCliente(nickname, imagenUrl, em);
    }

    @Override
    public void actualizarImagenAerolinea(String nickname, String imagenUrl) {
        manejadorAerolinea.actualizarImagenAerolinea(nickname, imagenUrl, em);
    }

    @Override
    public void actualizarImagenRuta(String nombreRuta, String imagenUrl) {
        manejadorRutaVuelo.actualizarImagenRuta(nombreRuta, imagenUrl, em);
    }

    // Métodos para obtener URLs de imagen (si los necesitan)
    public String obtenerImagenCliente(String nickname) {
        DtCliente cliente = manejadorCliente.obtenerCliente(nickname);
        return cliente != null ? cliente.getImagenUrl() : null;
    }

    public String obtenerImagenAerolinea(String nickname) {
        DtAerolinea aerolinea = manejadorAerolinea.getDtAerolineas().stream()
                .filter(a -> a.getNickname().equals(nickname))
                .findFirst()
                .orElse(null);
        return aerolinea != null ? aerolinea.getImagenUrl() : null;
    }

    public String obtenerImagenRuta(String nombreRuta) {
        RutaVuelo ruta = manejadorRutaVuelo.getRuta(nombreRuta);
        return ruta != null ? ruta.getImagenUrl() : null;
    }

    // ======================================


    @Override
    public List<DtCliente> listarClientes() {
        return manejadorCliente.getClientes();
    }

    @Override
    public Cliente verInfoCliente(String nickname) {
        DtCliente cliente = manejadorCliente.obtenerCliente(nickname);
        Cliente c = manejadorCliente.obtenerClientePorEmail(cliente.getEmail());
        if (c != null) {
            return c;
        } else {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
    }

    @Override
    public DtCliente obtenerCliente(String nickname) {
        DtCliente cliente = manejadorCliente.obtenerCliente(nickname);
        return cliente;
    }

    @Override
    public Aerolinea verInfoAerolinea(String nickname) {
        Aerolinea aerolinea = manejadorAerolinea.obtenerAerolinea(nickname);
        if (aerolinea != null) {
            return aerolinea;
        } else {
            throw new IllegalArgumentException("Aerolínea no encontrada");
        }
    }

    @Override
    public DtAerolinea obtenerAerolinea(String nickname) {
        Aerolinea aerolinea = manejadorAerolinea.obtenerAerolinea(nickname);
        if (aerolinea == null) {
            throw new IllegalArgumentException("Aerolínea no encontrada");
        }
        return new DtAerolinea(
                aerolinea.getNickname(),
                aerolinea.getNombre(),
                aerolinea.getEmail(),
                aerolinea.getDescripcion(),
                aerolinea.getSitioWeb(),
                aerolinea.getRutasVuelo()
        );
    }

    @Override
    public List<DtAerolinea> listarAerolineas() {
        return manejadorAerolinea.getDtAerolineas();
    }

    // --- CIUDADES ---
    @Override
    public void altaCiudad(String nombre, String pais) {
        String clave = nombre.trim().toLowerCase();

        if (manejadorCiudad.obtenerCiudad(clave) != null) {
            throw new IllegalArgumentException("Ya existe una ciudad con el nombre: " + nombre);
        }

        Ciudad c = new Ciudad(nombre, pais);
        manejadorCiudad.agregarCiudad(c, em);
    }

    @Override
    public void altaRutaVuelo(String nombre, String descripcion, String descripcionCorta, DtAerolinea aerolinea,
                              String ciudadOrigen, String ciudadDestino, String hora,
                              LocalDate fechaAlta, double costoTurista, double costoEjecutivo,
                              double costoEquipajeExtra, String[] categorias) {

        Aerolinea aero = manejadorAerolinea.obtenerAerolinea(aerolinea.getNickname());
        if (aero == null) {
            throw new IllegalArgumentException("No existe Aerolínea con nickname: " + aerolinea.getNickname());
        }

        if (manejadorRutaVuelo.getRuta(nombre) != null) {
            throw new IllegalArgumentException("Ya existe una ruta de vuelo con el nombre: " + nombre);
        }

        if (ciudadOrigen.equalsIgnoreCase(ciudadDestino)) {
            throw new IllegalArgumentException("La ciudad de origen y destino no pueden ser iguales.");
        }

        List<String> categoriaValida = new ArrayList<>();
        for (String cat : categorias) {
            Categoria c = manejadorCategoria.buscarCategorias(cat);
            if (c != null) {
                categoriaValida.add(cat);
            }
        }
        if (categoriaValida.isEmpty()) {
            throw new IllegalArgumentException("La ruta debe pertenecer a al menos una categoría válida.");
        }

        RutaVuelo r = new RutaVuelo(nombre, descripcion, descripcionCorta, aero, ciudadOrigen, ciudadDestino,
                hora, fechaAlta, costoTurista, costoEjecutivo,
                costoEquipajeExtra, categoriaValida);

        manejadorRutaVuelo.agregarRutaVuelo(r, em);
        manejadorAerolinea.agregarRutaVueloAAerolinea(aero.getNickname(), r, em);
    }

    @Override
    public List<DtAerolinea> obtenerAerolineasConRutasPendientes() {
        List<DtAerolinea> aerolineasConPendientes = new ArrayList<>();
        for (DtAerolinea aero : manejadorAerolinea.getDtAerolineas()) {
            List<RutaVuelo> rutasPendientes = manejadorRutaVuelo.getRutasPorEstadoYAerolinea(
                    aero.getNombre(), RutaVuelo.EstadoRuta.INGRESADA);
            if (!rutasPendientes.isEmpty()) {
                aerolineasConPendientes.add(aero);
            }
        }
        return aerolineasConPendientes;
    }

    @Override
    public List<DtRutaVuelo> obtenerRutasPendientesPorAerolinea(String nombreAerolinea) {
        List<RutaVuelo> rutasPendientes = manejadorRutaVuelo.getRutasPorEstadoYAerolinea(
                nombreAerolinea, RutaVuelo.EstadoRuta.INGRESADA);

        List<DtRutaVuelo> dtRutasPendientes = new ArrayList<>();
        for (RutaVuelo ruta : rutasPendientes) {
            dtRutasPendientes.add(ruta.getDtRutaVuelo());
        }
        return dtRutasPendientes;
    }

    @Override
    public void aceptarRutaVuelo(String nombreRuta) {
        manejadorRutaVuelo.cambiarEstadoRuta(nombreRuta, RutaVuelo.EstadoRuta.CONFIRMADA, em);
    }

    @Override
    public void rechazarRutaVuelo(String nombreRuta) {
        manejadorRutaVuelo.cambiarEstadoRuta(nombreRuta, RutaVuelo.EstadoRuta.RECHAZADA, em);
    }

    @Override
    public RutaVuelo obtenerRuta(String nombreRuta) {
        return manejadorRutaVuelo.getRuta(nombreRuta);
    }

    @Override
    public List<DtRutaVuelo> listarRutasPorAerolinea(String nombreAerolinea) {
        return manejadorAerolinea.obtenerRutaVueloDeAerolinea(nombreAerolinea);
    }

    @Override
    public void altaVuelo(String nombreVuelo, String nombreAereolinea, String nombreRuta,
                          LocalDate fecha, int duracion, int asientosTurista,
                          int asientosEjecutivo, LocalDate fechaAlta) {

        RutaVuelo ruta = manejadorRutaVuelo.getRuta(nombreRuta);
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta " + nombreRuta + " no existe.");
        }

        if (manejadorRutaVuelo.getVueloDeRuta(nombreRuta, nombreVuelo) != null) {
            throw new IllegalArgumentException("Ya existe un vuelo con el nombre " + nombreVuelo + " en la ruta " + nombreRuta);
        }

        Vuelo vuelo = new Vuelo(nombreVuelo, nombreAereolinea, ruta, fecha, duracion,
                asientosTurista, asientosEjecutivo, fechaAlta);

        manejadorVuelo.agregarVuelo(vuelo, em);
        manejadorRutaVuelo.agregarVueloARuta(nombreRuta, vuelo, em);
    }

    public Vuelo obtenerVuelo(String nombreVuelo) {
        return manejadorVuelo.getVuelo(nombreVuelo);
    }

    @Override
    public List<DtVuelo> listarVuelosPorRuta(String nombreRuta) {
        return manejadorRutaVuelo.obtenerVuelosPorRuta(nombreRuta);
    }

    @Override
    public Vuelo verInfoVuelo(String nombreVuelo) {
        Vuelo vuelo = manejadorVuelo.getVuelo(nombreVuelo);
        if (vuelo == null) {
            throw new IllegalArgumentException("Vuelo no encontrado");
        }
        return vuelo;
    }

    // --- RESERVA ---
    @Override
    public void crearYRegistrarReserva(String nicknameCliente, String nombreVuelo, LocalDate fechaReserva, double costo,
                                       TipoAsiento tipoAsiento, int cantidadPasajes, int unidadesEquipajeExtra,
                                       List<Pasajero> pasajeros) {
        Vuelo vuelo = manejadorVuelo.getVuelo(nombreVuelo);
        if (vuelo == null) {
            throw new IllegalArgumentException("El vuelo no existe: " + nombreVuelo);
        }

        if (vuelo.getReservas().containsKey(nicknameCliente)) {
            throw new IllegalArgumentException(
                    "El cliente " + nicknameCliente + " ya tiene una reserva para el vuelo " + nombreVuelo +
                            ". Debe elegir otro vuelo o ruta."
            );
        }

        DtCliente cliente = manejadorCliente.obtenerCliente(nicknameCliente);
        if (cliente != null) {
            for (DtReserva r : cliente.getReservas()) {
                if (r.getVuelo().equals(nombreVuelo)) {
                    throw new IllegalArgumentException(
                            "El cliente " + nicknameCliente + " ya tiene una reserva para el vuelo " + nombreVuelo + "."
                    );
                }
            }
        }

        String idReserva = "RES" + (++idReservaCounter);
        Reserva reserva = new Reserva(
                idReserva, costo, tipoAsiento, cantidadPasajes, unidadesEquipajeExtra, pasajeros, vuelo
        );

        registrarReservaVuelo(nicknameCliente, nombreVuelo, reserva);
    }

    @Override
    public void registrarReservaVuelo(String nicknameCliente, String nombreVuelo, Reserva reserva) {
        DtCliente cliente = manejadorCliente.obtenerCliente(nicknameCliente);
        Vuelo vuelo = manejadorVuelo.getVuelo(nombreVuelo);

        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
        if (vuelo == null) {
            throw new IllegalArgumentException("Vuelo no encontrado");
        }

        if (manejadorVuelo.tieneReservaDeCliente(nicknameCliente, vuelo)) {
            throw new IllegalArgumentException("El cliente ya tiene una reserva para este vuelo");
        }
        String idReserva = reserva.getId();
        vuelo.agregarReserva(idReserva, reserva);
        manejadorCliente.agregarReserva(reserva, nicknameCliente, idReserva, em);

        manejadorVuelo.actualizarVuelo(vuelo, em);
    }

    @Override
    public DtReserva obtenerReserva(String idReserva, String nicknameCliente) {
        DtCliente cliente = manejadorCliente.obtenerCliente(nicknameCliente);
        try {
            if (cliente == null) {
                throw new IllegalArgumentException("Cliente no encontrado");
            }
            for (DtReserva r : cliente.getReservas()) {
                if (r.getId().equals(idReserva)) {
                    return r;
                }
            }
            throw new IllegalArgumentException("Reserva no encontrada");
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al obtener la reserva: " + e.getMessage());
        }
    }

    // --- PAQUETES ---
    @Override
    public void altaPaquete(String nombre, String descripcion, int descuentoPorc, int periodoValidezDias) {
        if (manejadorPaquete.obtenerPaquete(nombre) != null) {
            throw new IllegalArgumentException("Ya existe un paquete con el nombre: " + nombre);
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del paquete no puede estar vacío.");
        }
        if (descuentoPorc < 0 || descuentoPorc > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100 por ciento.");
        }
        if (periodoValidezDias < 0) {
            throw new IllegalArgumentException("El período de validez no puede ser negativo.");
        }

        Paquete p = new Paquete(nombre.trim(), descripcion, descuentoPorc, periodoValidezDias);
        manejadorPaquete.agregarPaquete(p, em);
    }

    @Override
    public List<DtPaquete> listarPaquetes() {
        return (List<DtPaquete>) manejadorPaquete.getPaquetes();
    }

    @Override
    public void modificarDatosDeCliente(String nickname, String nombre, String apellido, String nacionalidad, LocalDate fechaNacimiento, TipoDoc tipoDoc, String numeroDocumento) {
        DtCliente cliente = manejadorCliente.obtenerCliente(nickname);
        if (cliente != null) {
            cliente.setApellido(apellido);
            cliente.setNombre(nombre);
            cliente.setNacionalidad(nacionalidad);
            cliente.setFechaNacimiento(fechaNacimiento);
            cliente.setTipoDocumento(tipoDoc);
            cliente.setNumeroDocumento(numeroDocumento);
            manejadorCliente.modificarDatosCliente(cliente, em);
        } else {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
    }

    @Override
    public void modificarDatosAerolinea(String nickname, String Descripcion, String URL) {
        Aerolinea aerolinea = manejadorAerolinea.obtenerAerolinea(nickname);
        if (aerolinea != null) {
            aerolinea.setDescripcion(Descripcion);
            aerolinea.setSitioWeb(URL);
            manejadorAerolinea.modificarDatosAerolinea(aerolinea, em);
        } else {
            throw new IllegalArgumentException("Aerolínea no encontrada");
        }
    }

    @Override
    public List<DtPaquete> listarPaquetesDisp() {
        return manejadorPaquete.getPaquetesDisp();
    }

    @Override
    public double calcularCostoReserva(String nombreVuelo, TipoAsiento tipoAsiento, int cantidadPasajes, int unidadesEquipajeExtra) {
        ManejadorVuelo manejadorVuelo = ManejadorVuelo.getInstance();
        Vuelo vuelo = manejadorVuelo.getVuelo(nombreVuelo);

        if (vuelo == null) {
            throw new IllegalArgumentException("Vuelo no encontrado: " + nombreVuelo);
        }

        double costoBase;
        if (tipoAsiento == TipoAsiento.TURISTA) {
            costoBase = 100.0;
        } else {
            costoBase = 200.0;
        }

        double costoPasajes = costoBase * cantidadPasajes;
        double costoEquipaje = 30.0 * unidadesEquipajeExtra;

        return costoPasajes + costoEquipaje;
    }

    @Override
    public Pasajero crearPasajero(String nombre, String apellido) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del pasajero no puede estar vacío.");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del pasajero no puede estar vacío.");
        }
        return new Pasajero(nombre.trim(), apellido.trim());
    }

    @Override
    public void compraPaquete(String nomPaquete, String nomCliente, int validezDias, LocalDate fechaC, double costo) {
        Paquete p = manejadorPaquete.obtenerPaquete(nomPaquete);
        if (p == null) {
            throw new IllegalArgumentException("Paquete no encontrado");
        }

        DtCliente c = manejadorCliente.obtenerCliente(nomCliente);
        if (c == null) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        for (CompraPaqLogica cp : p.getCompras()) {
            if (cp.getCliente().getNickname().equals(c.getNickname())) {
                throw new IllegalArgumentException("Error, el cliente " + c.getNombre() + " ya compró el paquete " + p.getNombre() + ". Elija otro o cancele.");
            }
        }

        try {
            manejadorPaquete.compraPaquete(p, c, validezDias, fechaC, costo, em);
            System.out.println("Paquete comprado correctamente.");
        } catch (IllegalStateException e) {
            System.out.println("ERROR.");
        }
    }

    @Override
    public void altaCategoria(String nomCat) {
        Categoria cat = manejadorCategoria.buscarCategorias(nomCat);
        if (cat != null) {
            throw new IllegalArgumentException("Error, la categoria ya existe.");
        }

        try {
            Categoria c = new Categoria(nomCat);
            manejadorCategoria.agregarCategoria(c, em);
            System.out.println("Categoria creada correctamente.");
        } catch (IllegalStateException e) {
            System.out.println("ERROR.");
        }
    }

    @Override
    public List<Object> obtenerDatosAdicionalesUsuario(String nickname) {
        List<Object> adicionales = new ArrayList<>();
        DtCliente cliente = manejadorCliente.obtenerCliente(nickname);
        if (cliente != null) {
            adicionales.addAll(cliente.getReservas());
            adicionales.addAll(cliente.getPaquetesComprados());
            return adicionales;
        }
        Aerolinea aerolinea = manejadorAerolinea.obtenerAerolinea(nickname);
        if (aerolinea != null) {
            adicionales.addAll(aerolinea.getRutasVuelo());
            return adicionales;
        }
        return adicionales;
    }

    @Override
    public List<DtCiudad> listarCiudades() {
        List<DtCiudad> dtCiudades = new ArrayList<>();
        for (Ciudad ciudad : manejadorCiudad.getCiudades()) {
            dtCiudades.add(new DtCiudad(ciudad.getNombre(), ciudad.getPais()));
        }
        return dtCiudades;
    }

    @Override
    public List<DtCategoria> listarCategorias() {
        List<DtCategoria> lista = new ArrayList<>();
        for (Categoria c : manejadorCategoria.getCategorias().values()) {
            lista.add(new DtCategoria(c.getNombre()));
        }
        return lista;
    }

    @Override
    public void altaRutaPaquete(String nombrePaquete, String nomRuta, int cantidadAsientos, TipoAsiento tipoAsiento) {
        Paquete p = manejadorPaquete.obtenerPaquete(nombrePaquete);
        if (p == null) {
            throw new IllegalArgumentException("Paquete no encontrado");
        }

        if (p.estaComprado()) {
            throw new IllegalArgumentException("No se pueden agregar rutas a un paquete que ya ha sido comprado.");
        }

        RutaVuelo ruta = manejadorRutaVuelo.getRuta(nomRuta);
        if (ruta == null) {
            throw new IllegalArgumentException("No se encontró la ruta con ese nombre.");
        }

        if (ruta.getEstado() != RutaVuelo.EstadoRuta.CONFIRMADA) {
            throw new IllegalArgumentException("No se puede agregar una ruta que no esté CONFIRMADA al paquete. Ruta actual: " + ruta.getEstado());
        }

        manejadorPaquete.agregarRutaAPaquete(p, ruta, cantidadAsientos, tipoAsiento, em);

        DtPaquete dtPaquete = obtenerDtPaquete(nombrePaquete);
        if (dtPaquete != null) {
            DtRutaVuelo dtRuta = ruta.getDtRutaVuelo();
            DtItemPaquete dtItem = new DtItemPaquete(dtRuta, cantidadAsientos, tipoAsiento.toString());
            dtPaquete.getItems().add(dtItem);
        }

        System.out.println("Ruta agregada al paquete correctamente.");
    }

    @Override
    public Paquete obtenerPaquete(String nombrePaquete) {
        Paquete paquete = manejadorPaquete.obtenerPaquete(nombrePaquete);
        if (paquete == null) {
            throw new IllegalArgumentException("Paquete no encontrado");
        }
        return paquete;
    }

    @Override
    public DtPaquete obtenerDtPaquete(String nombrePaquete) {
        DtPaquete paquete = manejadorPaquete.obtenerDtPaquete(nombrePaquete);
        if (paquete == null) {
            throw new IllegalArgumentException("Paquete no encontrado");
        }
        return paquete;
    }

    @Override
    public List<DtAerolinea> getAerolineas() {
        return manejadorAerolinea.getDtAerolineas();
    }

    @Override
    public List<DtPaquete> getPaquetesDisp() {
        return manejadorPaquete.getPaquetes();
    }

    @Override
    public List<DtCliente> getClientes() {
        return manejadorCliente.getClientes();
    }

    @Override
    public List<DtReserva> getReservasCliente(String nickname) {
        DtCliente cliente = manejadorCliente.obtenerCliente(nickname);
        if (cliente != null) {
            return cliente.getReservas();
        } else {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
    }

    @Override
    public List<DtItemPaquete> getDtItemRutasPaquete(String nombrePaquete) {
        List<DtItemPaquete> items = manejadorPaquete.obtenerDtItemsPaquete(nombrePaquete);
        if (items != null && !items.isEmpty()) {
            return items;
        } else {
            throw new IllegalArgumentException("Paquete no encontrado o sin rutas asociadas");
        }
    }
}
package Logica;

import DataTypes.DtCliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManejadorCliente {

    private final Map<String, Cliente> clientes;
    private static volatile ManejadorCliente instancia = null;

    private ManejadorCliente() {
        clientes = new HashMap<>();
    }

    public static ManejadorCliente getInstance() {
        if (instancia == null) {
            synchronized (ManejadorCliente.class) {
                if (instancia == null) {
                    instancia = new ManejadorCliente();
                }
            }
        }
        return instancia;
    }

    // =================== CRUD BD ===================
    public void cargarClientesDesdeBD(EntityManager em) {
        TypedQuery<Cliente> query = em.createQuery("SELECT c FROM Cliente c", Cliente.class);
        List<Cliente> clientesPersistidos = query.getResultList();
        for (Cliente c : clientesPersistidos) {
            clientes.put(c.getNickname(), c);
        }
    }

    public void agregarCliente(Cliente c, EntityManager em) {
        if (c == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }
        if (this.obtenerCliente(c.getNickname()) == null &&
                this.obtenerClientePorDocumento(c.getNumeroDocumento()) == null &&
                this.obtenerClientePorEmail(c.getEmail()) == null) {

            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(c);
                clientes.put(c.getNickname(), c);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                clientes.remove(c.getNickname());
                throw new RuntimeException("Error al persistir el cliente: " + e.getMessage(), e);
            }

        } else {
            throw new IllegalArgumentException("Ya existe un cliente con el mismo nickname, documento o email.");
        }
    }

    public void modificarDatosCliente(DtCliente clienteTemporal, EntityManager em) {
        if (clienteTemporal == null) {
            throw new IllegalArgumentException("Los datos del cliente no pueden ser null");
        }
        Cliente clienteOriginal = obtenerClienteReal(clienteTemporal.getNickname());
        if (clienteOriginal == null) throw new IllegalArgumentException("Cliente no encontrado");

        if (clienteTemporal.getNombre() != null) clienteOriginal.setNombre(clienteTemporal.getNombre());
        if (clienteTemporal.getApellido() != null) clienteOriginal.setApellido(clienteTemporal.getApellido());
        if (clienteTemporal.getNacionalidad() != null) clienteOriginal.setNacionalidad(clienteTemporal.getNacionalidad());
        if (clienteTemporal.getTipoDocumento() != null) clienteOriginal.setTipoDocumento(clienteTemporal.getTipoDocumento());
        if (clienteTemporal.getNumeroDocumento() != null) clienteOriginal.setNumeroDocumento(clienteTemporal.getNumeroDocumento());
        if (clienteTemporal.getFechaNacimiento() != null) clienteOriginal.setFechaNacimiento(clienteTemporal.getFechaNacimiento());

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(clienteOriginal);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al modificar el cliente: " + e.getMessage(), e);
        }
    }

    // =================== Consultas en memoria ===================
    public DtCliente obtenerCliente(String nickname) {
        Cliente cliente = clientes.get(nickname);
        return (cliente != null) ? new DtCliente(cliente) : null;
    }

    public Cliente obtenerClienteReal(String nickname) {
        Cliente cliente = clientes.get(nickname);
        return cliente;
    }

    public Cliente obtenerClientePorDocumento(String numeroDocumento) {
        return clientes.values().stream()
                .filter(c -> c.getNumeroDocumento().equals(numeroDocumento))
                .findFirst().orElse(null);
    }

    public Cliente obtenerClientePorEmail(String email) {
        return clientes.values().stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst().orElse(null);
    }

    // Método para verificar login
    public boolean verificarLogin(String email, String password) {
        Cliente cliente = obtenerClientePorEmail(email);
        if (cliente != null) {
            return cliente.verificarPassword(password);
        }
        return false;
    }

    public List<DtCliente> getClientes() {
        List<DtCliente> lista = new ArrayList<>();
        for (Cliente c : clientes.values()) {
            lista.add(new DtCliente(c));
        }
        return lista;
    }

    public void actualizarImagenCliente(String nickname, String imagenUrl, EntityManager em) {
        Cliente cliente = clientes.get(nickname);
        if (cliente != null) {
            cliente.setImagenUrl(imagenUrl);
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(cliente);
                tx.commit();
                System.out.println("Imagen actualizada para cliente: " + nickname);
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new RuntimeException("Error actualizando imagen del cliente: " + e.getMessage(), e);
            }
        } else {
            throw new IllegalArgumentException("Cliente no encontrado: " + nickname);
        }
    }

    public void agregarReserva(Reserva reserva, String nicknameCliente, String idReserva, EntityManager em) {
        DtCliente cliente = obtenerCliente(nicknameCliente);
        Cliente clienteObj = clientes.get(nicknameCliente);
        if (cliente == null || clienteObj == null) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            em.persist(reserva);

            clienteObj.agregarReserva(reserva);

            em.merge(clienteObj);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al agregar la reserva: " + e.getMessage(), e);
        }
    }
}
package Logica;

import DataTypes.DtCliente;
import DataTypes.DtItemPaquete;
import DataTypes.DtPaquete;
import DataTypes.DtRutaVuelo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManejadorPaquete {

    private Map<String, Paquete> paquetes;
    private static ManejadorPaquete instancia = null;

    private ManejadorPaquete() {
        paquetes = new HashMap<>();
    }

    public static ManejadorPaquete getInstance() {
        if (instancia == null) {
            instancia = new ManejadorPaquete();
        }
        return instancia;
    }

    // =================== CRUD BD ===================
    public void cargarPaquetesDesdeBD(EntityManager em) {
        TypedQuery<Paquete> query = em.createQuery("SELECT p FROM Paquete p", Paquete.class);
        List<Paquete> paquetesPersistidos = query.getResultList();
        for (Paquete p : paquetesPersistidos) {
            paquetes.put(p.getNombre(), p);
        }
    }

    public void agregarPaquete(Paquete p, EntityManager em) {
        if (!paquetes.containsKey(p.getNombre())) {
            paquetes.put(p.getNombre(), p);
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(p);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("El paquete con el nombre " + p.getNombre() + " ya existe.");
        }
    }

    public void agregarRutaAPaquete(Paquete paquete, RutaVuelo ruta, int cantidadAsientos, TipoAsiento tipoAsiento, EntityManager em) {
        // Verificar que la ruta no esté ya en el paquete con el mismo tipo de asiento
        for (ItemPaquete item : paquete.getItemPaquetes()) {
            if (item.getRutaVuelo().equals(ruta) && item.getTipoAsiento() == tipoAsiento) {
                throw new IllegalArgumentException("La ruta ya fue agregada previamente al paquete con ese tipo de asiento.");
            }
        }

        // Crear y agregar el nuevo item
        ItemPaquete nuevoItem = new ItemPaquete(ruta, cantidadAsientos, tipoAsiento);
        paquete.getItemPaquetes().add(nuevoItem);

        // Recalcular costo
        double costoTotal = 0;
        for (ItemPaquete item : paquete.getItemPaquetes()) {
            double costoRuta = item.getTipoAsiento() == TipoAsiento.TURISTA
                    ? item.getRutaVuelo().getCostoTurista()
                    : item.getRutaVuelo().getCostoEjecutivo();
            costoTotal += costoRuta * item.getCantAsientos();
        }
        double costoConDescuento = costoTotal * (1 - paquete.getDescuentoPorc() / 100.0);
        paquete.setCosto(costoConDescuento);

        // Persistir en BD
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(paquete);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new IllegalArgumentException("Error al guardar la ruta en el paquete: " + e.getMessage());
        }
    }

    public void compraPaquete(Paquete p, DtCliente dtCliente, int validezDias, LocalDate fechaC, double costo, EntityManager em) {
        // Obtener el objeto Cliente real, no DtCliente
        Cliente clienteObj = ManejadorCliente.getInstance().obtenerClienteReal(dtCliente.getNickname());
        if (clienteObj == null) {
            throw new IllegalArgumentException("El cliente con nickname " + dtCliente.getNickname() + " no existe.");
        }

        CompraPaqLogica nuevaCompra = new CompraPaqLogica(clienteObj, p, fechaC, validezDias, costo);

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // Persistir la compra
            em.persist(nuevaCompra);

            // Agregar a las listas
            p.getCompras().add(nuevaCompra);
            clienteObj.getComprasPaquetes().add(nuevaCompra);

            em.merge(p);
            em.merge(clienteObj);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al comprar el paquete: " + e.getMessage(), e);
        }
    }

    // =================== Consultas en memoria ===================
    public Paquete obtenerPaquete(String nombre) { return paquetes.get(nombre); }


    public List<DtPaquete> getPaquetes() {
        List<DtPaquete> lista = new ArrayList<>();
        for (Paquete p : paquetes.values()) {
            lista.add(new DtPaquete(p)); // Asegúrate de que exista un constructor adecuado en DtPaquete
        }
        return lista;
    }


    public List<DtPaquete> getPaquetesDisp() {
        List<DtPaquete> disponibles = new ArrayList<>();
        for (Paquete p : paquetes.values()) {
            if (!p.estaComprado()) {
                disponibles.add(new DtPaquete(p));
            }
        }
        return disponibles;
    }

    public DtPaquete obtenerDtPaquete(String nombrePaquete) {
        Paquete p = paquetes.get(nombrePaquete);
        if (p != null) {
            return new DtPaquete(p);
        }
        return null;
    }

    public List<DtItemPaquete> obtenerDtItemsPaquete(String nombrePaquete) {
        Paquete p = paquetes.get(nombrePaquete);
        if (p != null) {
            List<DtItemPaquete> items = new ArrayList<>();
            for (ItemPaquete item : p.getItemPaquetes()) {
                // Crear el DtRutaVuelo correspondiente al Item
                RutaVuelo ruta = item.getRutaVuelo();
                DtRutaVuelo dtRuta = new DtRutaVuelo(
                        ruta.getNombre(),
                        ruta.getDescripcion(),
                        ruta.getDescripcionCorta(), // Nuevo campo: descripción corta
                        ruta.getAerolinea().getNombre(),
                        ruta.getCiudadOrigen(),
                        ruta.getCiudadDestino(),
                        ruta.getHora(),
                        ruta.getFechaAlta(),
                        ruta.getCostoTurista(),
                        ruta.getCostoEjecutivo(),
                        ruta.getCostoEquipajeExtra(),
                        ruta.getEstado().toString(),
                        ruta.getCategorias(),
                        ruta.getDtVuelos()
                );

                // Crear el DtItemPaquete con la ruta, cantidad y tipo
                DtItemPaquete dtItem = new DtItemPaquete(
                        dtRuta,
                        item.getCantAsientos(),
                        item.getTipoAsiento().toString()
                );

                items.add(dtItem);
            }
            return items;
        }
        return new ArrayList<>(); // devolver lista vacía si no existe
    }

}

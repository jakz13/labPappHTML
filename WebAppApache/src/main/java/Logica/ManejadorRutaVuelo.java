package Logica;

import DataTypes.DtVuelo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.*;

public class ManejadorRutaVuelo {

    private Map<String, RutaVuelo> rutasVuelo;
    public static ManejadorRutaVuelo instancia = null;

    private ManejadorRutaVuelo() {
        rutasVuelo = new HashMap<>();
    }

    public static ManejadorRutaVuelo getInstance() {
        if (instancia == null) {
            instancia = new ManejadorRutaVuelo();
        }
        return instancia;
    }

    // =================== CRUD BD ===================
    public void cargarRutasDesdeBD(EntityManager em) {
        TypedQuery<RutaVuelo> query = em.createQuery("SELECT r FROM RutaVuelo r", RutaVuelo.class);
        List<RutaVuelo> rutasPersistidas = query.getResultList();
        for (RutaVuelo r : rutasPersistidas) {
            // Inicializar estado si es nulo
            if (r.getEstado() == null) {
                r.setEstado(RutaVuelo.EstadoRuta.INGRESADA);
            }
            rutasVuelo.put(r.getNombre(), r);
        }
    }

    public void agregarRutaVuelo(RutaVuelo ruta, EntityManager em) {
        rutasVuelo.put(ruta.getNombre(), ruta);
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(ruta);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public void agregarVueloARuta(String nombreRuta, Vuelo vuelo, EntityManager em) {
        RutaVuelo ruta = rutasVuelo.get(nombreRuta);
        if (ruta != null) {
            ruta.agregarVuelo(vuelo);
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(ruta);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                e.printStackTrace();
            }
        } else {
            System.out.println("Ruta de vuelo no encontrada.");
        }
    }

    // =================== Consultas en memoria ===================
    public RutaVuelo getRuta(String nombre) {
        return rutasVuelo.get(nombre);
    }

    public Vuelo getVueloDeRuta(String nombreRuta, String nombreVuelo) {
        RutaVuelo ruta = rutasVuelo.get(nombreRuta);
        if (ruta != null) return ruta.getVuelo(nombreVuelo);
        return null;
    }

    public List<RutaVuelo> getRutasPorAerolinea(String nombreAerolinea) {
        List<RutaVuelo> rutas = new ArrayList<>();
        for (RutaVuelo ruta : rutasVuelo.values()) {
            if (ruta.getAerolinea() != null &&
                    ruta.getAerolinea().getNombre().equals(nombreAerolinea)) {
                rutas.add(ruta);
            }
        }
        return rutas;
    }

    // Nuevo método para obtener rutas por estado y aerolínea
    public List<RutaVuelo> getRutasPorEstadoYAerolinea(String nombreAerolinea, RutaVuelo.EstadoRuta estado) {
        List<RutaVuelo> rutasFiltradas = new ArrayList<>();
        for (RutaVuelo ruta : rutasVuelo.values()) {
            if (ruta.getAerolinea() != null &&
                    ruta.getAerolinea().getNombre().equals(nombreAerolinea) &&
                    ruta.getEstado() == estado) {
                rutasFiltradas.add(ruta);
            }
        }
        return rutasFiltradas;
    }

    // Método para cambiar estado de una ruta
    public void cambiarEstadoRuta(String nombreRuta, RutaVuelo.EstadoRuta nuevoEstado, EntityManager em) {
        RutaVuelo ruta = rutasVuelo.get(nombreRuta);
        if (ruta != null) {
            ruta.setEstado(nuevoEstado);
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(ruta);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Ruta de vuelo no encontrada: " + nombreRuta);
        }
    }

    public List<RutaVuelo> getRutas() {
        return new ArrayList<>(rutasVuelo.values());
    }

    public void actualizarImagenRuta(String nombreRuta, String imagenUrl, EntityManager em) {
        RutaVuelo ruta = rutasVuelo.get(nombreRuta);
        if (ruta != null) {
            ruta.setImagenUrl(imagenUrl);
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(ruta);
                tx.commit();
                System.out.println("Imagen actualizada para ruta: " + nombreRuta);
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new RuntimeException("Error actualizando imagen de la ruta: " + e.getMessage(), e);
            }
        } else {
            throw new IllegalArgumentException("Ruta no encontrada: " + nombreRuta);
        }
    }

    public List<DtVuelo> obtenerVuelosPorRuta(String nombreRuta) {
        RutaVuelo ruta = rutasVuelo.get(nombreRuta);
        if (ruta != null) {
            List<DtVuelo> dtVuelos = new ArrayList<>();
            for (Vuelo vuelo : ruta.getVuelos()) {
                dtVuelos.add(vuelo.getDtVuelo());
            }
            return dtVuelos;
        }
        return Collections.emptyList();
    }
}
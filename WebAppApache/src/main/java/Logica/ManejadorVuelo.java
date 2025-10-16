package Logica;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManejadorVuelo {

    private Map<String, Vuelo> vuelos;
    public static ManejadorVuelo instancia = null;

    private ManejadorVuelo() {
        vuelos = new HashMap<>();
    }

    public static ManejadorVuelo getInstance() {
        if (instancia == null) {
            instancia = new ManejadorVuelo();
        }
        return instancia;
    }

    // =================== CRUD BD ===================
    public void cargarVuelosDesdeBD(EntityManager em) {
        TypedQuery<Vuelo> query = em.createQuery("SELECT v FROM Vuelo v LEFT JOIN FETCH v.reservasList", Vuelo.class);
        List<Vuelo> vuelosPersistidos = query.getResultList();
        for (Vuelo v : vuelosPersistidos) {
            // Sincronizar el Map con las reservas cargadas
            v.getReservas(); // Esto sincroniza el Map
            vuelos.put(v.getNombre(), v);
        }
    }

    public void agregarVuelo(Vuelo vuelo, EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(vuelo);
            vuelos.put(vuelo.getNombre(), vuelo);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al agregar el vuelo: " + e.getMessage(), e);
        }
    }

    public void actualizarVuelo(Vuelo vuelo, EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(vuelo);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error al actualizar el vuelo: " + e.getMessage(), e);
        }
    }

    public boolean tieneReservaDeCliente(String nicknameCliente, Vuelo vuelo) {
        return vuelo.getReservas().containsKey(nicknameCliente);
    }

    public Vuelo getVuelo(String nombre) {
        return vuelos.get(nombre);
    }

    public List<Vuelo> getVuelos() {
        return new ArrayList<>(vuelos.values());
    }
}

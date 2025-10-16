package Logica;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManejadorCiudad {
    private static ManejadorCiudad instancia;
    private Map<String, Ciudad> ciudades;

    private ManejadorCiudad() {
        ciudades = new HashMap<>();
    }

    public static ManejadorCiudad getInstance() {
        if (instancia == null) {
            instancia = new ManejadorCiudad();
        }
        return instancia;
    }

    // === Cargar todas las ciudades desde la BD a memoria ===
    public void cargarCiudadesDesdeBD(EntityManager em) {
        TypedQuery<Ciudad> query = em.createQuery("SELECT c FROM Ciudad c", Ciudad.class);
        List<Ciudad> ciudadesPersistidas = query.getResultList();
        for (Ciudad c : ciudadesPersistidas) {
            ciudades.put(c.getNombre(), c);
        }
    }

    // === Agregar ciudad en memoria y en BD ===
    public void agregarCiudad(Ciudad c, EntityManager em) {
        String clave = c.getNombre().trim().toLowerCase();

        if (ciudades.containsKey(clave)) {
            throw new IllegalArgumentException("La ciudad '" + c.getNombre() + "' ya existe.");
        }
        ciudades.put(clave, c);
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(c);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    // === Obtener ciudad por nombre (normalizado) ===
    public Ciudad obtenerCiudad(String nombre) {
        return ciudades.get(nombre.trim().toLowerCase());
    }
    // === Listar todas las ciudades ===
    public List<Ciudad> getCiudades() {
        return new ArrayList<>(ciudades.values());
    }
}

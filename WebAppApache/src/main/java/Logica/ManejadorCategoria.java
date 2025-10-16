package Logica;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManejadorCategoria {

    private Map<String, Categoria> categorias;
    private static ManejadorCategoria instancia;

    private ManejadorCategoria() {
        categorias = new HashMap<>();
    }

    public static ManejadorCategoria getInstance() {
        if (instancia == null) {
            instancia = new ManejadorCategoria();
        }
        return instancia;
    }

    // === Cargar todas las categorías desde la BD a memoria ===
    public void cargarCategoriasDesdeBD(EntityManager em) {
        TypedQuery<Categoria> query = em.createQuery("SELECT c FROM Categoria c", Categoria.class);
        List<Categoria> categoriasPersistidas = query.getResultList();
        for (Categoria c : categoriasPersistidas) {
            categorias.put(c.getNombre(), c);
        }
    }

    // === Agregar categoría en memoria y en BD ===
    public void agregarCategoria(Categoria categoria, EntityManager em) {
        categorias.put(categoria.getNombre(), categoria);

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(categoria);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    // === Buscar categoría en memoria ===
    public Categoria buscarCategorias(String nombre) {
        return categorias.get(nombre);
    }

    // === Obtener todas las categorías ===
    public Map<String, Categoria> getCategorias() {
        return categorias;
    }

}

package com.example.usecases;

import jakarta.servlet.http.HttpServletRequest;
import serviciosweb.JuanViajesWS;
import com.example.util.PortUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Proveedor centralizado de categorías. Permite a "casos de uso" y servlets
 * pedir la lista de categorías al puerto SOAP de forma consistente.
 */
public class CategoriasUseCase {

    private static final List<String> DEFAULT_CATEGORIES = Arrays.asList(
            "nacionales", "internacionales", "europa", "america", "exclusivos", "temporada", "cortos"
    );

    public static List<String> getCategorias(HttpServletRequest request, boolean allowDefault) {
        JuanViajesWS port = PortUtils.getPort(request);
        return sanitize(getCategorias(port, allowDefault));
    }

    public static List<String> getCategorias(JuanViajesWS port, boolean allowDefault) {
        if (port == null) return new ArrayList<>(allowDefault ? DEFAULT_CATEGORIES : Collections.emptyList());

        try { port.cargarDesdeBd(); } catch (Exception ignored) {}

        Object raw = null;
        try {
            java.lang.reflect.Method m = port.getClass().getMethod("listarCategorias");
            raw = m.invoke(port);
        } catch (NoSuchMethodException nsme) {
            if (allowDefault) {
                System.out.println("CategoriasUseCase: listarCategorias no disponible en el servicio remoto; devolviendo categorías por defecto.");
                return new ArrayList<>(DEFAULT_CATEGORIES);
            } else {
                return Collections.emptyList();
            }
        } catch (Exception ex) {
            System.err.println("CategoriasUseCase: error llamando listarCategorias: " + (ex == null ? "(null)" : ex.getMessage()));
            if (ex != null) ex.printStackTrace();
            return Collections.emptyList();
        }

        List<String> categorias = new ArrayList<>();
        if (raw instanceof List) {
            for (Object elem : (List<?>) raw) {
                if (elem == null) continue;
                if (elem instanceof String) {
                    categorias.add((String) elem);
                } else {
                    try {
                        java.lang.reflect.Method getter = elem.getClass().getMethod("getNombre");
                        Object val = getter.invoke(elem);
                        categorias.add(val != null ? String.valueOf(val) : null);
                    } catch (Exception e) {
                        // fallback a toString() si no tiene getNombre
                        try { categorias.add(elem.toString()); } catch (Exception ignore) { /* skip */ }
                    }
                }
            }
        }
        return sanitize(categorias);
    }

    // Normaliza: trim, filtra nulos/empty, elimina duplicados y preserva orden
    private static List<String> sanitize(List<String> list) {
        if (list == null) return Collections.emptyList();
        List<String> cleaned = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        for (String s : list) {
            if (s == null) continue;
            String t = s.trim();
            if (t.isEmpty()) continue;
            // Filtrar literales que muchas veces aparecen por serialización errónea
            if (t.equalsIgnoreCase("undefined") || t.equalsIgnoreCase("null")) continue;
            if (!seen.contains(t)) {
                seen.add(t);
                cleaned.add(t);
            }
        }
        return cleaned;
    }
}

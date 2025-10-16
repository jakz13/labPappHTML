package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class CargarDatosBD implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(CargarDatosBD.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("CargarDatosBD: contexto iniciado, cargando datos desde BD si es posible...");
        try {
            // Obtener el sistema vía la fábrica y pedir carga desde BD
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();
            logger.info("CargarDatosBD: carga desde BD ejecutada correctamente.");
        } catch (Throwable t) {
            // No fallar el arranque del servidor por este paso; solo loguear
            logger.log(Level.WARNING, "CargarDatosBD: no se pudo cargar datos desde BD: " + t.getMessage(), t);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No se requiere acción al destruir el contexto
    }
}

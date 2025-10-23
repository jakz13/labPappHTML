package com.example.servlets;

import logica.Fabrica;
import logica.ISistema;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;

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

        // Asegurar que exista la carpeta /Images dentro del contexto y guardarla en el ServletContext
        try {
            String imagesRealPath = sce.getServletContext().getRealPath("/Images");
            if (imagesRealPath == null) {
                // Fallback a temp dir
                imagesRealPath = System.getProperty("java.io.tmpdir") + File.separator + "WebAppApache_Images";
            }
            File imagesDir = new File(imagesRealPath);
            if (!imagesDir.exists()) {
                boolean ok = imagesDir.mkdirs();
                if (ok) logger.info("CargarDatosBD: carpeta Images creada en " + imagesDir.getAbsolutePath());
            }
            sce.getServletContext().setAttribute("IMAGES_DIR", imagesDir.getAbsolutePath());
            logger.info("CargarDatosBD: IMAGES_DIR inicializado -> " + imagesDir.getAbsolutePath());
        } catch (Throwable t) {
            logger.log(Level.WARNING, "CargarDatosBD: no se pudo inicializar IMAGES_DIR: " + t.getMessage(), t);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No se requiere acción al destruir el contexto
    }
}

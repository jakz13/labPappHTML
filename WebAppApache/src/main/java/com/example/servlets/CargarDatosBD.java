package com.example.servlets;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import serviciosweb.JuanViajesWS;
import serviciosweb.WebServicesService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;

@WebListener
public class CargarDatosBD implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(CargarDatosBD.class.getName());
    private WebServicesService service = new WebServicesService();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("CargarDatosBD: contexto iniciado, intentando cargar datos desde WS...");
        try {
            JuanViajesWS port = service.getJuanViajesWSPort();
            // configurar endpoint dinámico si hace falta (opcional)
            port.cargarDesdeBd();
            // Guardar el port en el ServletContext para que los servlets lo reutilicen
            try {
                sce.getServletContext().setAttribute("port", port);
                logger.info("CargarDatosBD: port del WS guardado en ServletContext attribute 'port'.");
            } catch (Throwable t2) {
                logger.log(Level.WARNING, "CargarDatosBD: no se pudo almacenar el port en el ServletContext: " + t2.getMessage(), t2);
            }
            logger.info("CargarDatosBD: carga desde WS ejecutada correctamente.");
        } catch (Throwable t) {
            // No fallar el arranque del servidor por este paso; solo loguear
            logger.log(Level.WARNING, "CargarDatosBD: no se pudo cargar datos desde WS: " + t.getMessage(), t);
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

// src/main/java/com/example/listeners/AppContextListener.java
package com.example.listeners;  // Cambia el paquete

import Logica.Fabrica;
import Logica.ISistema;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("🚀 AppContextListener - INICIANDO aplicación");
        try {
            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();
            System.out.println("✅ Datos cargados desde BD - Contexto inicializado");
        } catch (Exception e) {
            System.err.println("❌ Error al cargar datos en listener: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 AppContextListener - aplicación finalizada");
    }
}
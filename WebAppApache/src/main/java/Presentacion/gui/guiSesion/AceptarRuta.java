package Presentacion.gui.guiSesion;

import DataTypes.DtAerolinea;
import DataTypes.DtRutaVuelo;
import Logica.Fabrica;
import Logica.ISistema;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AceptarRuta {
    private JPanel panel1;
    private JComboBox<DtAerolinea> comboBoxAerolineas;
    private JComboBox<DtRutaVuelo> comboBoxRutas;
    private JButton aceptarButton;
    private JButton rechazarButton;
    private JButton cancelarButton;
    private JTextArea infoRutaTextArea;

    private ISistema sistema;

    public AceptarRuta() {
        this.sistema = Fabrica.getInstance().getISistema();

        // Cargar aerolíneas con rutas pendientes
        cargarAerolineasConRutasPendientes();

        // Configurar listeners
        comboBoxAerolineas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarRutasPendientes();
            }
        });

        comboBoxRutas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarInfoRuta();
            }
        });

        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aceptarRutaSeleccionada();
            }
        });

        rechazarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rechazarRutaSeleccionada();
            }
        });

        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarVentana();
            }
        });

        // Estado inicial de los botones
        actualizarEstadoBotones();
    }

    private void cargarAerolineasConRutasPendientes() {
        DefaultComboBoxModel<DtAerolinea> modelo = new DefaultComboBoxModel<>();
        for (DtAerolinea aero : sistema.obtenerAerolineasConRutasPendientes()) {
            modelo.addElement(aero);
        }
        comboBoxAerolineas.setModel(modelo);
    }

    private void cargarRutasPendientes() {
        DtAerolinea aerolineaSeleccionada = (DtAerolinea) comboBoxAerolineas.getSelectedItem();
        if (aerolineaSeleccionada != null) {
            DefaultComboBoxModel<DtRutaVuelo> modelo = new DefaultComboBoxModel<>();
            for (DtRutaVuelo ruta : sistema.obtenerRutasPendientesPorAerolinea(aerolineaSeleccionada.getNombre())) {
                modelo.addElement(ruta);
            }
            comboBoxRutas.setModel(modelo);
        }
        actualizarEstadoBotones();
    }

    private void mostrarInfoRuta() {
        DtRutaVuelo rutaSeleccionada = (DtRutaVuelo) comboBoxRutas.getSelectedItem();
        if (rutaSeleccionada != null) {
            StringBuilder info = new StringBuilder();
            info.append("Nombre: ").append(rutaSeleccionada.getNombre()).append("\n");
            info.append("Descripción: ").append(rutaSeleccionada.getDescripcion()).append("\n");
            info.append("Descripción Corta: ").append(rutaSeleccionada.getDescripcionCorta()).append("\n");
            info.append("Origen: ").append(rutaSeleccionada.getCiudadOrigen()).append("\n");
            info.append("Destino: ").append(rutaSeleccionada.getCiudadDestino()).append("\n");
            info.append("Costo Turista: $").append(rutaSeleccionada.getCostoTurista()).append("\n");
            info.append("Costo Ejecutivo: $").append(rutaSeleccionada.getCostoEjecutivo()).append("\n");
            info.append("Estado: ").append(rutaSeleccionada.getEstado()).append("\n");

            infoRutaTextArea.setText(info.toString());
        }
        actualizarEstadoBotones();
    }

    private void aceptarRutaSeleccionada() {
        DtRutaVuelo rutaSeleccionada = (DtRutaVuelo) comboBoxRutas.getSelectedItem();
        if (rutaSeleccionada != null) {
            int confirmacion = JOptionPane.showConfirmDialog(
                    panel1,
                    "¿Está seguro que desea ACEPTAR la ruta: " + rutaSeleccionada.getNombre() + "?",
                    "Confirmar Aceptación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    sistema.aceptarRutaVuelo(rutaSeleccionada.getNombre());
                    JOptionPane.showMessageDialog(panel1, "Ruta aceptada correctamente");
                    cargarRutasPendientes(); // Recargar la lista
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel1, "Error al aceptar la ruta: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void rechazarRutaSeleccionada() {
        DtRutaVuelo rutaSeleccionada = (DtRutaVuelo) comboBoxRutas.getSelectedItem();
        if (rutaSeleccionada != null) {
            int confirmacion = JOptionPane.showConfirmDialog(
                    panel1,
                    "¿Está seguro que desea RECHAZAR la ruta: " + rutaSeleccionada.getNombre() + "?",
                    "Confirmar Rechazo",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    sistema.rechazarRutaVuelo(rutaSeleccionada.getNombre());
                    JOptionPane.showMessageDialog(panel1, "Ruta rechazada correctamente");
                    cargarRutasPendientes(); // Recargar la lista
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel1, "Error al rechazar la ruta: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void actualizarEstadoBotones() {
        boolean tieneRutaSeleccionada = comboBoxRutas.getSelectedItem() != null;
        aceptarButton.setEnabled(tieneRutaSeleccionada);
        rechazarButton.setEnabled(tieneRutaSeleccionada);
    }

    private void cerrarVentana() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel1);
        if (topFrame != null) {
            topFrame.dispose();
        }
    }

    public JPanel getPanel() {
        return panel1;
    }
}
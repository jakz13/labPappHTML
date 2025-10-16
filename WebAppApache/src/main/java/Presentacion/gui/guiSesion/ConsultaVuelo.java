package Presentacion.gui.guiSesion;

import DataTypes.DtAerolinea;
import DataTypes.DtReserva;
import DataTypes.DtRutaVuelo;
import DataTypes.DtVuelo;
import Logica.Fabrica;
import Logica.ISistema;

import javax.swing.*;
import java.awt.*;

public class ConsultaVuelo {
    private JComboBox comboBoxAerolinea;
    private JList listRutaVuelo;
    private JList listVuelo;
    private JTextArea textAreaDatosVuelo;
    private JButton cerrarButton;
    private JPanel panelConsultaVeulo;

    public ConsultaVuelo() {
        ISistema sistema = Fabrica.getInstance().getISistema();

        // Cargar aerolíneas al iniciar
        cargarAerolineas();

        // Actualizar la lista de rutas cuando se selecciona otra aerolínea
        comboBoxAerolinea.addActionListener(e -> actualizarRutas());

        listRutaVuelo.addListSelectionListener(e -> actualizarVuelos());

        listVuelo.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // evita que se dispare dos veces
                DtVuelo seleccionada = (DtVuelo) listVuelo.getSelectedValue();
                if (seleccionada != null) {
                    StringBuilder info = new StringBuilder();
                    info.append("Nombre: ").append(seleccionada.getNombre()).append("\n")
                            .append("Nombre de Ruta: ").append(seleccionada.getRutaVuelo()).append("\n")
                            .append("Duración: ").append(seleccionada.getDuracion()).append(" Horas").append("\n")
                            .append("Fecha: ").append(seleccionada.getFecha()).append("\n")
                            .append("Asientos Ejecutivos: ").append(seleccionada.getAsientosEjecutivo()).append("\n")
                            .append("Asientos Turista: ").append(seleccionada.getAsientosTurista()).append("\n")
                            .append("Fecha de Alta: ").append(seleccionada.getFechaAlta()).append("\n")
                            .append("Reservas:\n");

                    // Recorrer el mapa de reservas
                    java.util.List<DtReserva> reservas = seleccionada.getReservas();
                    if (reservas.isEmpty()) {
                        info.append("  Ninguna reserva\n");
                    } else {
                        for (DtReserva r : reservas) {
                            info.append("  ID: ").append(r.getId())
                                    .append(", Costo: ").append(r.getCosto())
                                    .append(", Tipo: ").append(r.getTipoAsiento())
                                    .append(", Pasajes: ").append(r.getCantidadPasajes())
                                    .append("\n");
                        }
                    }

                    textAreaDatosVuelo.setText(info.toString());
                } else {
                    textAreaDatosVuelo.setText(""); // limpiar si no hay selección
                }
            }
        });


        // Acción del botón Cerrar
        cerrarButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(comboBoxAerolinea);
            if (topFrame != null) {
                topFrame.dispose();
            }
        });
    }



    // Carga aerolíneas disponibles
    private void cargarAerolineas() {
        ISistema sistema = Fabrica.getInstance().getISistema();
        DefaultComboBoxModel<DtAerolinea> modeloAerolinea = new DefaultComboBoxModel<>();
        for (DtAerolinea a : sistema.getAerolineas()) {
            modeloAerolinea.addElement(a);
        }
        comboBoxAerolinea.setModel(modeloAerolinea);
        comboBoxAerolinea.setSelectedIndex(-1);
    }

    private void actualizarRutas() {
        DtAerolinea seleccionada = (DtAerolinea) comboBoxAerolinea.getSelectedItem();
        DefaultListModel<DtRutaVuelo> modeloRutas = new DefaultListModel<>();
        if (seleccionada != null) {
            for (DtRutaVuelo r : seleccionada.getRutas()) {
                modeloRutas.addElement(r);
            }
        }
        listRutaVuelo.setModel(modeloRutas);
    }

    private void actualizarVuelos() {
        DtRutaVuelo seleccionada = (DtRutaVuelo) listRutaVuelo.getSelectedValue();
        DefaultListModel<DtVuelo> modeloVuelos = new DefaultListModel<>();
        if (seleccionada != null) {
            ISistema sistema = Fabrica.getInstance().getISistema();
            for (DtVuelo v : sistema.listarVuelosPorRuta(seleccionada.getNombre())) {
                modeloVuelos.addElement(v);
            }
        }
        listVuelo.setModel(modeloVuelos);
    }




    public Container getPanelConsultaVeulo() {
        return panelConsultaVeulo;
    }
}

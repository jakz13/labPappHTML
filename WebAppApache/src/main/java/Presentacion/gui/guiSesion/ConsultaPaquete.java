package Presentacion.gui.guiSesion;

import DataTypes.DtItemPaquete;
import DataTypes.DtPaquete;
import DataTypes.DtRutaVuelo;
import DataTypes.DtVuelo;
import Logica.Fabrica;
import Logica.ISistema;

import javax.swing.*;
import java.awt.*;

public class ConsultaPaquete {
    private JPanel PanelConsultaPaquete;
    private JComboBox<DtPaquete> comboboxPaquetes;
    private JTextPane InfoPaquetes;
    private JComboBox<DtRutaVuelo> comboBoxRutaVuelo;
    private JTextArea textAreaInfoRuta;
    private JButton cerrarButton;

    private ISistema sistema;

    public ConsultaPaquete() {
        sistema = Fabrica.getInstance().getISistema();

        // Cargar paquetes al iniciar
        cargarPaquetes();

        // Al seleccionar un paquete, mostrar su información
        comboboxPaquetes.addActionListener(e -> actualizarInfoPaquete());

        // Al seleccionar una ruta dentro del paquete, mostrar su info detallada
        comboBoxRutaVuelo.addActionListener(e -> actualizarInfoRuta());

        // Botón cerrar
        cerrarButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(PanelConsultaPaquete);
            if (topFrame != null) {
                topFrame.dispose();
            }
        });
    }

    private void cargarPaquetes() {
        DefaultComboBoxModel<DtPaquete> modeloPaquetes = new DefaultComboBoxModel<>();
        for (DtPaquete p : sistema.listarPaquetes()) {
            modeloPaquetes.addElement(p);
        }
        comboboxPaquetes.setModel(modeloPaquetes);
        comboboxPaquetes.setSelectedIndex(-1);
    }

    private void actualizarInfoPaquete() {
        DtPaquete seleccionado = (DtPaquete) comboboxPaquetes.getSelectedItem();
        if (seleccionado != null) {
            StringBuilder info = new StringBuilder();
            info.append("Nombre: ").append(seleccionado.getNombre()).append("\n")
                    .append("Descripción: ").append(seleccionado.getDescripcion()).append("\n")
                    .append("Costo base: ").append(seleccionado.getCosto()).append("\n")
                    .append("Descuento: ").append(seleccionado.getDescuentoPorc()).append("%\n")
                    .append("Validez: ").append(seleccionado.getPeriodoValidezDias()).append(" días\n")
                    .append("Rutas incluidas:\n");

            // Llenar ComboBox de rutas
            DefaultComboBoxModel<DtRutaVuelo> modeloRutas = new DefaultComboBoxModel<>();
            for (DtItemPaquete item : sistema.getDtItemRutasPaquete(seleccionado.getNombre())) {
                DtRutaVuelo ruta = item.getRutaVuelo();
                if (ruta != null) {
                    modeloRutas.addElement(ruta);
                    info.append("  - ").append(ruta.getNombre())
                            .append(" (").append(item.getTipoAsiento())
                            .append(", Asientos: ").append(item.getCantAsientos())
                            .append(")\n");
                }
            }
            comboBoxRutaVuelo.setModel(modeloRutas);

            InfoPaquetes.setText(info.toString());
        } else {
            InfoPaquetes.setText("");
            comboBoxRutaVuelo.setModel(new DefaultComboBoxModel<>());
        }
        textAreaInfoRuta.setText(""); // limpiar info de ruta
    }

    private void actualizarInfoRuta() {
        DtRutaVuelo seleccionada = (DtRutaVuelo) comboBoxRutaVuelo.getSelectedItem();
        if (seleccionada != null) {
            StringBuilder info = new StringBuilder();
            info.append("Nombre Ruta: ").append(seleccionada.getNombre()).append("\n")
                    .append("Origen: ").append(seleccionada.getCiudadOrigen()).append("\n")
                    .append("Destino: ").append(seleccionada.getCiudadDestino()).append("\n")
                    .append("Duración estimada: ").append(seleccionada.getHora()).append("\n")
                    .append("Fecha de Alta: ").append(seleccionada.getFechaAlta()).append("\n")
                    .append("Costo Turista: ").append(seleccionada.getCostoTurista()).append("\n")
                    .append("Costo Ejecutivo: ").append(seleccionada.getCostoEjecutivo()).append("\n")
                    .append("Vuelos asociados:\n");

            // Mostrar los vuelos de la ruta
            for (DtVuelo v : sistema.listarVuelosPorRuta(seleccionada.getNombre())) {
                info.append("  - ").append(v.getNombre())
                        .append(" | Fecha: ").append(v.getFecha())
                        .append(" | Duración: ").append(v.getDuracion()).append("h\n");
            }

            textAreaInfoRuta.setText(info.toString());
        } else {
            textAreaInfoRuta.setText("");
        }
    }

    public Container getPanelConsultaPaquete() {
        return PanelConsultaPaquete;
    }
}

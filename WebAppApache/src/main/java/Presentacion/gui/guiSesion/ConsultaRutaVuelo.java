package Presentacion.gui.guiSesion;

import DataTypes.DtAerolinea;
import DataTypes.DtReserva;
import DataTypes.DtRutaVuelo;
import DataTypes.DtVuelo;
import Logica.Fabrica;
import Logica.ISistema;

import javax.swing.*;

public class ConsultaRutaVuelo {
    private JPanel panel1;
    private JPanel panelConsultaRuta;
    private JComboBox<DtAerolinea> comboBoxAerolinea;
    private JList<DtRutaVuelo> listRutas;
    private JList<DtVuelo> listVuelosAsociados;
    private JTextArea textAreaDatosRuta;
    private JButton cerrarButton;
    private JTextArea textAreaDatosVuelo;

    public ConsultaRutaVuelo() {
        ISistema sistema = Fabrica.getInstance().getISistema();

        DefaultComboBoxModel<DtAerolinea> modeloAerolinea = new DefaultComboBoxModel<>();
        for (DtAerolinea a : sistema.listarAerolineas()) {
            modeloAerolinea.addElement(a);
        }
        comboBoxAerolinea.setModel(modeloAerolinea);
        comboBoxAerolinea.setSelectedIndex(-1);

        comboBoxAerolinea.addActionListener(e -> {
            DtAerolinea seleccionada = (DtAerolinea) comboBoxAerolinea.getSelectedItem();
            DefaultListModel<DtRutaVuelo> modeloRutas = new DefaultListModel<>();
            if (seleccionada != null) {
                for (DtRutaVuelo r : seleccionada.getRutas()) {
                    modeloRutas.addElement(r);
                }
            }
            listRutas.setModel(modeloRutas);
            textAreaDatosRuta.setText("");
            textAreaDatosVuelo.setText("");
        });

        listRutas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                DtRutaVuelo seleccionada = listRutas.getSelectedValue();
                DefaultListModel<DtVuelo> modeloVuelos = new DefaultListModel<>();
                if (seleccionada != null) {

                    StringBuilder infoRuta = new StringBuilder();
                    infoRuta.append("=== INFORMACIÓN DE LA RUTA ===\n\n");

                    // Información básica
                    infoRuta.append("📌 INFORMACIÓN BÁSICA:\n");
                    infoRuta.append("  Nombre: ").append(seleccionada.getNombre()).append("\n");
                    infoRuta.append("  Estado: ").append(seleccionada.getEstado()).append("\n");
                    infoRuta.append("  Aerolínea: ").append(seleccionada.getAerolinea()).append("\n");
                    infoRuta.append("  Ruta: ").append(seleccionada.getCiudadOrigen())
                            .append(" → ").append(seleccionada.getCiudadDestino()).append("\n");
                    infoRuta.append("  Hora: ").append(seleccionada.getHora()).append("\n");
                    infoRuta.append("  Fecha de Alta: ").append(seleccionada.getFechaAlta()).append("\n\n");

                    // Descripciones
                    infoRuta.append("📝 DESCRIPCIONES:\n");
                    infoRuta.append("  Descripción Corta: ").append(seleccionada.getDescripcionCorta()).append("\n");
                    if (seleccionada.getDescripcion() != null && !seleccionada.getDescripcion().isEmpty()) {
                        infoRuta.append("  Descripción Completa: ").append(seleccionada.getDescripcion()).append("\n");
                    }
                    infoRuta.append("\n");

                    // Costos
                    infoRuta.append("💰 COSTOS:\n");
                    infoRuta.append("  Turista: $").append(String.format("%.2f", seleccionada.getCostoTurista())).append("\n");
                    infoRuta.append("  Ejecutivo: $").append(String.format("%.2f", seleccionada.getCostoEjecutivo())).append("\n");
                    infoRuta.append("  Equipaje Extra: $").append(String.format("%.2f", seleccionada.getCostoEquipajeExtra())).append("\n\n");

                    // Categorías
                    infoRuta.append("🏷️ CATEGORÍAS:\n");
                    if (seleccionada.getCategorias() != null && !seleccionada.getCategorias().isEmpty()) {
                        for (String categoria : seleccionada.getCategorias()) {
                            infoRuta.append("  • ").append(categoria).append("\n");
                        }
                    } else {
                        infoRuta.append("  No tiene categorías asignadas\n");
                    }
                    infoRuta.append("\n");

                    // Vuelos asociados
                    infoRuta.append("✈️ VUELOS ASOCIADOS:\n");
                    if (seleccionada.getVuelos() != null && !seleccionada.getVuelos().isEmpty()) {
                        infoRuta.append("  Total: ").append(seleccionada.getVuelos().size()).append(" vuelo(s)\n");
                    } else {
                        infoRuta.append("  No hay vuelos programados\n");
                    }

                    textAreaDatosRuta.setText(infoRuta.toString());

                    // Cargar vuelos asociados
                    for (DtVuelo v : sistema.listarVuelosPorRuta(seleccionada.getNombre())) {
                        modeloVuelos.addElement(v);
                    }
                }
                listVuelosAsociados.setModel(modeloVuelos);
                textAreaDatosVuelo.setText(""); // Limpiar info de vuelo al cambiar ruta
            }
        });

        listVuelosAsociados.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                DtVuelo vuelo = listVuelosAsociados.getSelectedValue();
                System.out.println("Vuelo seleccionado: " + vuelo); // DEBUG

                if (vuelo != null) {
                    System.out.println("Entró al if con vuelo " + vuelo.getNombre()); // DEBUG
                    StringBuilder infoVuelo = new StringBuilder();
                    infoVuelo.append("=== INFORMACIÓN DEL VUELO ===\n\n");

                    // Información básica del vuelo
                    infoVuelo.append("📌 INFORMACIÓN BÁSICA:\n");
                    infoVuelo.append("  Nombre: ").append(vuelo.getNombre()).append("\n");
                    infoVuelo.append("  Aerolínea: ").append(vuelo.getNombreAerolinea()).append("\n");
                    infoVuelo.append("  Fecha: ").append(vuelo.getFecha()).append("\n");
                    infoVuelo.append("  Duración: ").append(vuelo.getDuracion()).append(" minutos\n");
                    infoVuelo.append("  Fecha de Alta: ").append(vuelo.getFechaAlta()).append("\n\n");

                    // Capacidad
                    infoVuelo.append("💺 CAPACIDAD:\n");
                    infoVuelo.append("  Asientos Turista: ").append(vuelo.getAsientosTurista()).append("\n");
                    infoVuelo.append("  Asientos Ejecutivos: ").append(vuelo.getAsientosEjecutivo()).append("\n");
                    infoVuelo.append("  Total asientos: ").append(vuelo.getAsientosTurista() + vuelo.getAsientosEjecutivo()).append("\n\n");

                    // Información de la ruta (si está disponible)
                    if (vuelo.getRutaVuelo() != null) {
                        infoVuelo.append("🛣️ INFORMACIÓN DE RUTA:\n");
                        infoVuelo.append("  Ruta: ").append(vuelo.getRutaVuelo().getNombre()).append("\n");
                        infoVuelo.append("  Trayecto: ").append(vuelo.getRutaVuelo().getCiudadOrigen())
                                .append(" → ").append(vuelo.getRutaVuelo().getCiudadDestino()).append("\n\n");
                    }

                    // Reservas
                    infoVuelo.append("🎫 RESERVAS:\n");
                    java.util.List<DtReserva> reservas = vuelo.getReservas();
                    if (reservas.isEmpty()) {
                        infoVuelo.append("  No hay reservas para este vuelo\n");
                    } else {
                        infoVuelo.append("  Total reservas: ").append(reservas.size()).append("\n\n");
                        for (DtReserva r : reservas) {
                            infoVuelo.append("  📋 Reserva ID: ").append(r.getId()).append("\n");
                            infoVuelo.append("     Fecha: ").append(r.getFecha()).append("\n");
                            infoVuelo.append("     Costo: $").append(String.format("%.2f", r.getCosto())).append("\n");
                            infoVuelo.append("     Tipo Asiento: ").append(r.getTipoAsiento()).append("\n");
                            infoVuelo.append("     Cant. Pasajes: ").append(r.getCantidadPasajes()).append("\n");
                            infoVuelo.append("     Equipaje Extra: ").append(r.getUnidadesEquipajeExtra()).append("\n");
                            infoVuelo.append("     Pasajeros: ").append(r.getPasajeros().size()).append("\n");
                            infoVuelo.append("     --------------------\n");
                        }
                    }
                    textAreaDatosVuelo.setText(infoVuelo.toString());
                }
            }
        });

        cerrarButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panelConsultaRuta);
            if (topFrame != null) topFrame.dispose();
        });
    }

    public JPanel getPanelConsultaRuta() {
        return panelConsultaRuta;
    }
}
package Presentacion.gui.guiSesion;

import DataTypes.DtAerolinea;
import DataTypes.DtRutaVuelo;
import Logica.Fabrica;
import Logica.ISistema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class AltaVuelo {
    private JPanel panelDeVuelo;
    private JTextField campoAereolinea;
    private JTextField campoRuta;
    private JButton crearButton;
    private JButton cancelarButton;
    private JList ListaAereolineas;
    private JList ListaRutasDeVuelo;
    private JTextField NOmVuelo;
    private JComboBox Dia;
    private JComboBox Ano;
    private JComboBox Mes;
    private JTextField Duracion;
    private JTextField CantTuristas;
    private JTextField CantEjecutivo;

    public AltaVuelo() {
        ISistema sistema = Fabrica.getInstance().getISistema();
        DefaultListModel<String> modeloAerolineas = new DefaultListModel<>();
        for (DtAerolinea a : sistema.listarAerolineas()) {
            String item = a.getNickname() + " (" + a.getNombre() + ")";
            modeloAerolineas.addElement(item);
        }
        ListaAereolineas.setModel(modeloAerolineas);

        ListaAereolineas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String item = (String) ListaAereolineas.getSelectedValue();
                if (item != null) {
                    String nickname = item.split(" ")[0];
                    String nombre = item.substring(item.indexOf("(") + 1, item.indexOf(")"));

                    campoAereolinea.setText(nombre);

                    List<DtRutaVuelo> rutas = sistema.listarRutasPorAerolinea(nickname);
                    DefaultListModel<String> modeloRutas = new DefaultListModel<>();
                    for (DtRutaVuelo r : rutas) {
                        modeloRutas.addElement(r.getNombre());
                    }
                    ListaRutasDeVuelo.setModel(modeloRutas);
                }
            }
        });
        ListaRutasDeVuelo.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String rutaSeleccionada = (String) ListaRutasDeVuelo.getSelectedValue();
                if (rutaSeleccionada != null) {
                    campoRuta.setText(rutaSeleccionada);
                }
            }
        });

        crearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aereolinea = (String) ListaAereolineas.getSelectedValue();
                int duracion = Integer.parseInt(Duracion.getText());
                String Vuelo = NOmVuelo.getText();
                int turista = Integer.parseInt(CantTuristas.getText());
                int ejecutivo = Integer.parseInt(CantEjecutivo.getText());
                String Ruta = (String) ListaRutasDeVuelo.getSelectedValue();
                String diaStr = (String) Dia.getSelectedItem();
                String mesStr = (String) Mes.getSelectedItem();
                String anioStr = (String) Ano.getSelectedItem();

                LocalDate fechaAlta = LocalDate.now();

                if (Vuelo.isEmpty() || turista <= 0 || duracion <= 0 || ejecutivo <= 0) {
                    JOptionPane.showMessageDialog(null,
                            "Debe completar todos los campos.",
                            "Error de salame", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (diaStr.equals("Día") || mesStr.equals("Mes") || anioStr.equals("Año")) {
                    JOptionPane.showMessageDialog(null,
                            "Debe seleccionar una fecha válida.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int dia = Integer.parseInt(diaStr);
                int mes = Integer.parseInt(mesStr);
                int anio = Integer.parseInt(anioStr);

                LocalDate fecha = LocalDate.of(anio, mes, dia);

                try {
                    sistema.altaVuelo(Vuelo, aereolinea, Ruta,fecha,  duracion, turista, ejecutivo, fechaAlta);
                    JOptionPane.showMessageDialog(null,
                            "Vuelo creado correctamente.",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null,
                            ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panelDeVuelo);
                topFrame.dispose();
            }
        });
    }

    public Container getpanelDeVuelo() {
        return panelDeVuelo;
    }
}


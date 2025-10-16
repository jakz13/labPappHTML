package Presentacion.gui.guiSesion;

import DataTypes.DtAerolinea;
import DataTypes.DtCliente;
import DataTypes.DtRutaVuelo;
import DataTypes.DtVuelo;
import Logica.Fabrica;
import Logica.ISistema;
import Logica.Pasajero;
import Logica.TipoAsiento;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaVuelo {
    private JPanel panelReserva;
    private JList<String> ListaAereolineas;
    private JList<String> ListaRutasDeVuelo;
    private JList<String> ListaVuelos;
    private JTextField campoAereolinea;
    private JTextField campoRuta;
    private JTextField campoVuelo;
    private JTextField NOmVuelo;
    private JComboBox<String> clienteElegido;
    private JRadioButton ejecutivoRadioButton;
    private JRadioButton turistaRadioButton;
    private JTextField CantPasaje;
    private JTextField cantEquipaje;
    private JButton crearButton;
    private JButton cancelarButton;
    private JTextField Duracion;
    private JPanel pasajerosPanel;
    private JComboBox Dia;
    private JComboBox Ano;
    private JComboBox Mes;
    private JTextField CantTuristas;
    private JTextField CantEjecutivo;

    private final ISistema sistema;
    private String vueloSeleccionado;
    private String aerolineaSeleccionada;
    private String rutaSeleccionada;

    private List<JTextField> listaNombres = new ArrayList<>();
    private List<JTextField> listaApellidos = new ArrayList<>();

    private JPanel dinamico;
    private JPanel panel1;
    private JScrollPane scrollPasajeros;

    public ReservaVuelo() {
        sistema = Fabrica.getInstance().getISistema();
        ButtonGroup grupoTipo = new ButtonGroup();
        grupoTipo.add(turistaRadioButton);
        grupoTipo.add(ejecutivoRadioButton);

        DefaultListModel<String> modeloAerolineas = new DefaultListModel<>();
        for (DtAerolinea a : sistema.listarAerolineas()) {
            String item = a.getNickname() + " (" + a.getNombre() + ")";
            modeloAerolineas.addElement(item);
        }
        ListaAereolineas.setModel(modeloAerolineas);

        ListaAereolineas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String item = ListaAereolineas.getSelectedValue();
                if (item != null) {
                    aerolineaSeleccionada = item.split(" ")[0];
                    campoAereolinea.setText(item);

                    DefaultListModel<String> modeloRutas = new DefaultListModel<>();
                    for (DtRutaVuelo r : sistema.listarRutasPorAerolinea(aerolineaSeleccionada)) {
                        modeloRutas.addElement(r.getNombre());
                    }
                    ListaRutasDeVuelo.setModel(modeloRutas);
                }
            }
        });

        ListaRutasDeVuelo.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                rutaSeleccionada = ListaRutasDeVuelo.getSelectedValue();
                campoRuta.setText(rutaSeleccionada);

                DefaultListModel<String> modeloVuelos = new DefaultListModel<>();
                for (DtVuelo v : sistema.listarVuelosPorRuta(rutaSeleccionada)) {
                    modeloVuelos.addElement(v.getNombre());
                }
                ListaVuelos.setModel(modeloVuelos);
            }
        });

        ListaVuelos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                vueloSeleccionado = ListaVuelos.getSelectedValue();
                if (vueloSeleccionado != null) {
                    campoVuelo.setText(vueloSeleccionado);
                    NOmVuelo.setText(vueloSeleccionado);

                    DtVuelo vuelo = sistema.obtenerVuelo(vueloSeleccionado).getDtVuelo();
                    if (vuelo != null) {
                        Duracion.setText(String.valueOf(vuelo.getDuracion()));

                        LocalDate fecha = vuelo.getFecha();
                        Dia.setSelectedItem(String.valueOf(fecha.getDayOfMonth()));
                        Mes.setSelectedItem(String.valueOf(fecha.getMonthValue()));
                        Ano.setSelectedItem(String.valueOf(fecha.getYear()));

                        // Asientos
                        CantTuristas.setText(String.valueOf(vuelo.getAsientosTurista()));
                        CantEjecutivo.setText(String.valueOf(vuelo.getAsientosEjecutivo()));
                    }
                }
            }
        });

        clienteElegido.removeAllItems();
        for (DtCliente c : sistema.listarClientes()) {
            clienteElegido.addItem(c.getNickname() + " (" + c.getNombre() + ")");
        }

        dinamico = new JPanel();
        dinamico.setLayout(new BoxLayout(dinamico, BoxLayout.Y_AXIS));
        pasajerosPanel.setLayout(new BorderLayout());
        pasajerosPanel.add(new JScrollPane(dinamico), BorderLayout.CENTER);

        CantPasaje.addActionListener(e -> generarCamposPasajeros());

        cancelarButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panelReserva);
            topFrame.dispose();
        });

        ListaVuelos.addComponentListener(new ComponentAdapter() {});

        CantPasaje.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { generarCamposPasajeros(); }
            @Override
            public void removeUpdate(DocumentEvent e) { generarCamposPasajeros(); }
            @Override
            public void changedUpdate(DocumentEvent e) { generarCamposPasajeros(); }
        });

        crearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    TipoAsiento tipoPasaje = null;
                    if (vueloSeleccionado == null || clienteElegido.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(null, "Debe seleccionar vuelo y cliente.");
                        return;
                    }

                    if (turistaRadioButton.isSelected()) {
                        tipoPasaje = TipoAsiento.TURISTA;
                    } else if (ejecutivoRadioButton.isSelected()) {
                        tipoPasaje = TipoAsiento.EJECUTIVO;
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Debe seleccionar el tipo de pasaje (Turista o Ejecutivo).",
                                "Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String clienteItem = (String) clienteElegido.getSelectedItem();
                    String nicknameCliente = clienteItem.split(" ")[0];

                    int cantidadPasajes = Integer.parseInt(CantPasaje.getText());
                    int equipajeExtra = Integer.parseInt(cantEquipaje.getText());

                    List<Pasajero> pasajeros = obtenerPasajeros();
                    if (pasajeros.isEmpty()) return;

                    LocalDate fechaReserva = LocalDate.now();
                    double costo = sistema.calcularCostoReserva(vueloSeleccionado, tipoPasaje, cantidadPasajes, equipajeExtra);

                    try {
                        sistema.crearYRegistrarReserva(
                                nicknameCliente, vueloSeleccionado, fechaReserva,
                                costo, tipoPasaje, cantidadPasajes, equipajeExtra, pasajeros
                        );
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    JOptionPane.showMessageDialog(null, "Reserva creada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al crear reserva: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private List<Pasajero> obtenerPasajeros() {
        List<Pasajero> pasajeros = new ArrayList<>();
        for (int i = 0; i < listaNombres.size(); i++) {
            String nombre = listaNombres.get(i).getText().trim();
            String apellido = listaApellidos.get(i).getText().trim();
            try {
                Pasajero p = sistema.crearPasajero(nombre, apellido);
                pasajeros.add(p);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
                return new ArrayList<>(); // corto si hay error
            }
        }
        return pasajeros;
    }

    private void generarCamposPasajeros() {
        listaNombres.clear();
        listaApellidos.clear();
        dinamico.removeAll();

        try {
            String texto = CantPasaje.getText().trim();
            if (texto.isEmpty()) return;

            int cantidad = Integer.parseInt(texto);
            if (cantidad <= 0) return;

            for (int i = 0; i < cantidad; i++) {
                JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JTextField nombreField = new JTextField(10);
                JTextField apellidoField = new JTextField(10);

                fila.add(new JLabel("Pasajero " + (i + 1) + ":"));
                fila.add(new JLabel("Nombre:"));
                fila.add(nombreField);
                fila.add(new JLabel("Apellido:"));
                fila.add(apellidoField);

                dinamico.add(fila);

                listaNombres.add(nombreField);
                listaApellidos.add(apellidoField);
            }

            dinamico.revalidate();
            dinamico.repaint();
        } catch (NumberFormatException ex) {
            dinamico.removeAll();
            dinamico.revalidate();
            dinamico.repaint();
        }
    }

    public JPanel getPanelReserva() {
        return panelReserva;
    }
}

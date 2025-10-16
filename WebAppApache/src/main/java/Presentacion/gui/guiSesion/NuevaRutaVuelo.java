package Presentacion.gui.guiSesion;

import DataTypes.DtAerolinea;
import Logica.Fabrica;
import Logica.ISistema;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class NuevaRutaVuelo {
    private String aerolineaSeleccionada;
    private JPanel PanelAltaRuta;
    private JTextField NombreRuta;
    private JTextArea descripcion;
    private JTextField CostoTurista;
    private JTextField CostoEjecutivo;
    private JTextField Equipaje;
    private JButton crear;
    private JButton cancelarButton;
    private JList<String> ListaAereolinea;
    private JComboBox<String> comboBoxCiudadOrigen;
    private JComboBox<String> comboBoxDestino;
    private JComboBox<String> comboBoxCategoria;
    private JTextArea DescripcionCorta; // Campo para descripción corta (JTextArea)
    private JScrollPane scrollPane;
    private JScrollPane scrollPaneDescripcionCorta; // Scroll para la descripción corta

    public NuevaRutaVuelo() {
        ISistema sistema = Fabrica.getInstance().getISistema();

        // --- Aerolíneas ---
        DefaultListModel<String> modeloAerolineas = new DefaultListModel<>();
        for (DtAerolinea a : sistema.listarAerolineas()) {
            String item = a.getNickname() + " (" + a.getNombre() + ")";
            modeloAerolineas.addElement(item);
        }
        ListaAereolinea.setModel(modeloAerolineas);

        ListaAereolinea.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String seleccionado = ListaAereolinea.getSelectedValue();
                if (seleccionado != null) {
                    aerolineaSeleccionada = seleccionado.split(" ")[0]; // nickname
                }
            }
        });

        // --- Ciudades ---
        DefaultComboBoxModel<String> modeloOrigen = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> modeloDestino = new DefaultComboBoxModel<>();
        for (var c : sistema.listarCiudades()) {
            modeloOrigen.addElement(c.getNombre());
            modeloDestino.addElement(c.getNombre());
        }
        comboBoxCiudadOrigen.setModel(modeloOrigen);
        comboBoxDestino.setModel(modeloDestino);

        // --- Categorías ---
        DefaultComboBoxModel<String> modeloCategorias = new DefaultComboBoxModel<>();
        for (var cat : sistema.listarCategorias()) {
            modeloCategorias.addElement(cat.getNombre());
        }
        comboBoxCategoria.setModel(modeloCategorias);

        // --- Configurar el JTextArea para descripción corta ---
        DescripcionCorta.setLineWrap(true); // Salto de línea automático
        DescripcionCorta.setWrapStyleWord(true); // Salto por palabras completas
        DescripcionCorta.setRows(3); // Altura de 3 líneas

        // --- Crear ruta ---
        crear.addActionListener(e -> {
            String nombreRuta = NombreRuta.getText().trim();
            String Descripcion = descripcion.getText().trim();
            String descripcionCorta = DescripcionCorta.getText().trim(); // Usar el JTextArea
            String origen = (String) comboBoxCiudadOrigen.getSelectedItem();
            String destino = (String) comboBoxDestino.getSelectedItem();
            String categoriaSel = (String) comboBoxCategoria.getSelectedItem();
            String[] categorias = categoriaSel != null ? new String[]{categoriaSel} : new String[0];

            String hora = "00:00";
            double costoTurista, costoEjecutivo, equipaje;

            try {
                costoTurista = Double.parseDouble(CostoTurista.getText().trim());
                costoEjecutivo = Double.parseDouble(CostoEjecutivo.getText().trim());
                equipaje = Double.parseDouble(Equipaje.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Costos inválidos. Deben ser números.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate fecha = LocalDate.now();

            // Validaciones más específicas
            if (nombreRuta.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "El nombre de la ruta es obligatorio.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (descripcionCorta.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "La descripción corta es obligatoria.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (aerolineaSeleccionada == null) {
                JOptionPane.showMessageDialog(null,
                        "Debe seleccionar una aerolínea.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (origen == null || destino == null) {
                JOptionPane.showMessageDialog(null,
                        "Debe seleccionar ciudad de origen y destino.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (origen.equals(destino)) {
                JOptionPane.showMessageDialog(null,
                        "La ciudad de origen y destino no pueden ser iguales.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (categorias.length == 0) {
                JOptionPane.showMessageDialog(null,
                        "Debe seleccionar al menos una categoría.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar longitud de descripción corta (opcional)
            if (descripcionCorta.length() > 255) {
                JOptionPane.showMessageDialog(null,
                        "La descripción corta no puede exceder los 255 caracteres.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Llamar al método actualizado con descripción corta
                sistema.altaRutaVuelo(nombreRuta, Descripcion, descripcionCorta,
                        sistema.obtenerAerolinea(aerolineaSeleccionada),
                        origen, destino, hora, fecha,
                        costoTurista, costoEjecutivo, equipaje, categorias);

                JOptionPane.showMessageDialog(null,
                        "Ruta creada correctamente.\nEstado: INGRESADA - Esperando aprobación del administrador.\n\n" +
                                "Nombre: " + nombreRuta + "\n" +
                                "Descripción corta: " + descripcionCorta + "\n" +
                                "Aerolínea: " + aerolineaSeleccionada + "\n" +
                                "Ruta: " + origen + " → " + destino,
                        "Ruta Creada - Pendiente de Aprobación",
                        JOptionPane.INFORMATION_MESSAGE);

                // Limpiar campos después de crear
                limpiarCampos();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error al crear la ruta:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- Cancelar ---
        cancelarButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(PanelAltaRuta);
            if (topFrame != null) topFrame.dispose();
        });
    }

    private void limpiarCampos() {
        NombreRuta.setText("");
        descripcion.setText("");
        DescripcionCorta.setText(""); // Limpiar el JTextArea
        CostoTurista.setText("");
        CostoEjecutivo.setText("");
        Equipaje.setText("");
        ListaAereolinea.clearSelection();
        comboBoxCiudadOrigen.setSelectedIndex(0);
        comboBoxDestino.setSelectedIndex(0);
        comboBoxCategoria.setSelectedIndex(0);
        aerolineaSeleccionada = null;
    }

    public Container getPanelAltaRuta() {
        return PanelAltaRuta;
    }

    // Método para crear los componentes de la GUI (necesario para el diseñador de GUI)
    private void createUIComponents() {
        // Inicialización de componentes si es necesario
        DescripcionCorta = new JTextArea();
        DescripcionCorta.setLineWrap(true);
        DescripcionCorta.setWrapStyleWord(true);
        DescripcionCorta.setRows(3);

        // Crear scroll pane para la descripción corta
        scrollPaneDescripcionCorta = new JScrollPane(DescripcionCorta);
        scrollPaneDescripcionCorta.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneDescripcionCorta.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
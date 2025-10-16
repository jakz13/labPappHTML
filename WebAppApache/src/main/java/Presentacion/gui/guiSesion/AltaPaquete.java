package Presentacion.gui.guiSesion;

import Logica.Fabrica;
import Logica.ISistema;

import javax.swing.*;
import java.awt.*;

public class AltaPaquete {
    private JPanel panel1;
    private JTextField campoNombre;
    private JTextField campoDescripcion;
    private JTextField campoCosto;
    private JTextField campoDescuento;
    private JTextField campoValidez;
    private JButton crearButton;
    private JButton cancelarButton;
    private JLabel Nombre;
    private JLabel Descripcion;
    private JLabel Descuento;
    private JLabel Validez;


    public AltaPaquete() {
        ISistema sistema = Fabrica.getInstance().getISistema();
        crearButton.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            String descripcion = campoDescripcion.getText().trim();
            String descuentoStr = campoDescuento.getText().trim();
            String validezStr = campoValidez.getText().trim();

            // Validaciones de campos obligatorios
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "El nombre del paquete es obligatorio.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "La descripción del paquete es obligatoria.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar campos numéricos
            int descuento = 0;
            int validez = 0;

            if (descuentoStr.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "El descuento es obligatorio.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (validezStr.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "El período de validez es obligatorio.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                descuento = Integer.parseInt(descuentoStr);
                validez = Integer.parseInt(validezStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Descuento y validez deben ser números enteros válidos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                sistema.altaPaquete(nombre, descripcion, descuento, validez);
                JOptionPane.showMessageDialog(null,
                        "Paquete creado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                // Limpiar campos después del éxito
                campoNombre.setText("");
                campoDescripcion.setText("");
                campoDescuento.setText("");
                campoValidez.setText("");

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Error inesperado al crear el paquete: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelarButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel1);
            topFrame.dispose();
        });
    }

    public Container getPanel1() {
        return panel1;
    }
}

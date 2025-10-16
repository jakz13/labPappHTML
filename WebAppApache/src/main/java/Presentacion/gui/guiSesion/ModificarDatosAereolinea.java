package Presentacion.gui.guiSesion;

import Logica.Fabrica;
import Logica.ISistema;

import javax.swing.*;
import java.awt.*;

public class ModificarDatosAereolinea {
    private JPanel panelDeModificacion;
    private JTextField URL;
    private JButton modificarButton;
    private JButton cancelarButton;
    private JTextArea Descripcion;

    private final String nickname;

    public ModificarDatosAereolinea(String nickname) {
        this.nickname = nickname;
        ISistema sistema = Fabrica.getInstance().getISistema();

        // Traer los datos actuales de la aerolínea PERO HAY QUE CAMBIARLO POR DATATYPE SALAME
        var aerolinea = sistema.obtenerAerolinea(nickname);
        if (aerolinea != null) {
            Descripcion.setText(aerolinea.getDescripcion());
            URL.setText(aerolinea.getSitioWeb());
        }

        modificarButton.addActionListener(e -> {
            String nuevaDescripcion = Descripcion.getText().trim();
            String nuevaURL = URL.getText().trim();

            if (nuevaDescripcion.isEmpty() || nuevaURL.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Debe completar todos los campos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                sistema.modificarDatosAerolinea(nickname, nuevaDescripcion, nuevaURL);

                JOptionPane.showMessageDialog(null,
                        "Datos de la aerolínea modificados correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelarButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(panelDeModificacion);
            if (window != null) {
                window.dispose();
            }
        });
    }

    public Container getPanelDeModificacion() {
        return panelDeModificacion;
    }
}

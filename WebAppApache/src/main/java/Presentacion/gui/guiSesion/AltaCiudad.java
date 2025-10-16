package Presentacion.gui.guiSesion;

import Logica.Fabrica;
import Logica.ISistema;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AltaCiudad {
    private JPanel panelDeCiudad;
    private JTextField campoCiudad;
    private JTextField campoPais;
    private JButton crearButton;
    private JButton cancelarButton;
    private JLabel Ciudad;
    private JLabel Pais;

    public AltaCiudad() {
        ISistema sistema = Fabrica.getInstance().getISistema();
        crearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreCiudad = campoCiudad.getText().trim();
                String pais = campoPais.getText().trim();

                if (nombreCiudad.isEmpty() || pais.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Debe completar todos los campos.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    try{
                        sistema.altaCiudad(nombreCiudad, pais);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(null,
                                ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    JOptionPane.showMessageDialog(null,
                            "Ciudad creada correctamente.",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panelDeCiudad);
                topFrame.dispose();
            }
        });
    }

    public JPanel getPanelDeCiudad() {
        return panelDeCiudad;
    }
}

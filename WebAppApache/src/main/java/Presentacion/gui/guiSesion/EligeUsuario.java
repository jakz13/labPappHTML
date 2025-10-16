package Presentacion.gui.guiSesion;

import DataTypes.DtAerolinea;
import DataTypes.DtCliente;
import Logica.Fabrica;
import Logica.ISistema;

import javax.swing.*;
import java.awt.*;

public class EligeUsuario {
    private JTextField campoUsuarios;
    private JList ListaUsuarios;
    private JButton seleccionarButton;
    private JButton cancelarButton;
    private JPanel JPanelModificar;
    private String nicknameSeleccionado;
    private boolean esCliente;

    public EligeUsuario() {
        ISistema sistema = Fabrica.getInstance().getISistema();
        DefaultListModel<String> modelo = new DefaultListModel<>();
            for (DtCliente c : sistema.listarClientes()) {
                String item = c.getNickname() + " (" + c.getNombre() + ")";
                modelo.addElement(item);
            }
            for (DtAerolinea a : sistema.listarAerolineas()) {
                String item = a.getNickname() + " (" + a.getNombre() + ")";
                modelo.addElement(item);
            }

            ListaUsuarios.setModel(modelo);

            ListaUsuarios.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String item = (String) ListaUsuarios.getSelectedValue();
                    if (item != null) {
                        nicknameSeleccionado = item.split(" ")[0]; // guardo nickname
                        String nombre = item.substring(item.indexOf("(") + 1, item.indexOf(")"));
                        campoUsuarios.setText(nombre); // muestro solo el nombre
                    }
                }
            });

        seleccionarButton.addActionListener(e -> {
            if (nicknameSeleccionado == null || nicknameSeleccionado.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Debe seleccionar un usuario.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFrame framePrincipal = (JFrame) SwingUtilities.getWindowAncestor(campoUsuarios);
            framePrincipal.setVisible(false);

            JFrame frame;
            if (sistema.obtenerCliente(nicknameSeleccionado) != null) {
                frame = new JFrame("Modificar Cliente");
                frame.setContentPane(new ModificarDatosCliente(nicknameSeleccionado).getPanelDeVuelo());
            } else {
                frame = new JFrame("Modificar Aerolínea");
                frame.setContentPane(new ModificarDatosAereolinea(nicknameSeleccionado).getPanelDeModificacion());
            }

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setBounds(framePrincipal.getBounds());

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    framePrincipal.setVisible(true);
                }
            });

            frame.setVisible(true);
        });


        cancelarButton.addActionListener(e -> {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(campoUsuarios);
                topFrame.dispose();
            });
        }
    public Container getpanelModificar() {
        return JPanelModificar;
    }
    }



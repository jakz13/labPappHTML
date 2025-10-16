package Presentacion.gui.guiSesion;

import Logica.Fabrica;
import Logica.ISistema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AltaAereolinea {
    private JPanel PanelDeSesion;
    private JTextField NombreUsuario;   // -> será el "nickname"
    private JTextField NombreAereolinea;
    private JTextArea Descripcion;
    private JTextField EmailUsuario;
    private JButton crearButton;
    private JButton cancelarButton;
    private JTextField Link;
    private JPasswordField passwordField;
    private JPasswordField Confirmacionpassword;

    public AltaAereolinea() {
        ISistema sistema = Fabrica.getInstance().getISistema();

        crearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname   = NombreUsuario.getText().trim();
                String nombre     = NombreAereolinea.getText().trim();
                String descripcion = Descripcion.getText().trim();
                String correo     = EmailUsuario.getText().trim();
                String sitioWeb = Link.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String confirmacion = new String(Confirmacionpassword.getPassword()).trim();

                // Validaciones de campos obligatorios
                if (nickname.isEmpty() || nombre.isEmpty() || correo.isEmpty() ||
                        password.isEmpty() || confirmacion.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Debe completar todos los campos.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validación de formato de email
                if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                    JOptionPane.showMessageDialog(null,
                            "Correo electrónico no válido.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validación de contraseñas
                if (!password.equals(confirmacion)) {
                    JOptionPane.showMessageDialog(null,
                            "Las contraseñas no coinciden.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                    Confirmacionpassword.setText("");
                    return;
                }

                // Validación de fortaleza de contraseña (mínimo 6 caracteres)
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(null,
                            "La contraseña debe tener al menos 6 caracteres.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                    Confirmacionpassword.setText("");
                    return;
                }

                // Validación de caracteres especiales en contraseña (opcional)
                if (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*\\d.*")) {
                    JOptionPane.showMessageDialog(null,
                            "La contraseña debe contener al menos una mayúscula, una minúscula y un número.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                    Confirmacionpassword.setText("");
                    return;
                }

                try {
                    sistema.altaAerolinea(nickname, nombre, descripcion, correo, sitioWeb, password);

                    JOptionPane.showMessageDialog(null,
                            "Aerolínea creada correctamente.\n\n" +
                                    "Nickname: " + nickname + "\n" +
                                    "Nombre: " + nombre + "\n" +
                                    "Email: " + correo + "\n" +
                                    "Sitio Web: " + (sitioWeb.isEmpty() ? "No especificado" : sitioWeb),
                            "Aerolínea Creada - Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Limpiar campos después de crear
                    limpiarCampos();

                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Error al crear la aerolínea:\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window window = SwingUtilities.getWindowAncestor(PanelDeSesion);
                if (window != null) {
                    window.dispose();
                }
            }
        });

        // Agregar listener para limpiar campos de contraseña cuando se detecta error
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Confirmacionpassword.requestFocus();
            }
        });

        Confirmacionpassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearButton.doClick(); // Simular click en crear al presionar Enter
            }
        });
    }

    private void limpiarCampos() {
        NombreUsuario.setText("");
        NombreAereolinea.setText("");
        Descripcion.setText("");
        EmailUsuario.setText("");
        Link.setText("");
        passwordField.setText("");
        Confirmacionpassword.setText("");
        NombreUsuario.requestFocus(); // Volver al primer campo
    }

    public Container getPanelDeSesion() {
        return PanelDeSesion;
    }

    private void createUIComponents() {
        // Inicialización de componentes si es necesario
        Descripcion = new JTextArea();
        Descripcion.setLineWrap(true);
        Descripcion.setWrapStyleWord(true);
        Descripcion.setRows(3);

        passwordField = new JPasswordField();
        Confirmacionpassword = new JPasswordField();
    }
}
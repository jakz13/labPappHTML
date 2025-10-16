package Presentacion.gui.guiSesion;

import Logica.Fabrica;
import Logica.ISistema;
import Logica.TipoDoc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;

public class InicioSesion {
    private JPanel PanelDeSesion;
    private JButton ALTAVUELOButton;
    private JButton ALTACIUDADButton;
    private JTextField NombreUsuario;  // Nickname (clave)
    private JTextField NombreCliente;
    private JTextField Apellido;
    private JButton crearButton;
    private JButton cancelarButton;
    private JTextField EmailUsuario;
    private JTextField Documento;
    private JRadioButton PasaporteRadioButton;
    private JRadioButton CiRadioButton;
    private JTextField NacionalidadCliente;
    private JComboBox DiaNacimiento;
    private JComboBox MesNacimiento;
    private JComboBox AnoNacimiento;
    private JPasswordField passwordField;
    private JPasswordField ConfirmacionPassword;

    public InicioSesion() {
        ISistema sistema = Fabrica.getInstance().getISistema();
        ButtonGroup grupoDocumento = new ButtonGroup();
        grupoDocumento.add(PasaporteRadioButton);
        grupoDocumento.add(CiRadioButton);

        PanelDeSesion.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
            }
        });

        crearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = NombreUsuario.getText().trim();  // Nickname (clave)
                String nombreCliente = NombreCliente.getText().trim();
                String apellido = Apellido.getText().trim();
                String email = EmailUsuario.getText().trim();
                String documento = Documento.getText().trim();
                String nacionalidad = NacionalidadCliente.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String confirmacion = new String(ConfirmacionPassword.getPassword()).trim();

                TipoDoc tipoDoc;

                String diaStr = (String) DiaNacimiento.getSelectedItem();
                String mesStr = (String) MesNacimiento.getSelectedItem();
                String anioStr = (String) AnoNacimiento.getSelectedItem();

                // =================== VALIDACIONES ===================

                // Validaciones de campos obligatorios
                if (nickname.isEmpty() || nombreCliente.isEmpty() || apellido.isEmpty() ||
                        email.isEmpty() || documento.isEmpty() || nacionalidad.isEmpty() ||
                        password.isEmpty() || confirmacion.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Debe completar todos los campos.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validación de contraseñas
                if (!password.equals(confirmacion)) {
                    JOptionPane.showMessageDialog(null,
                            "Las contraseñas no coinciden.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                    ConfirmacionPassword.setText("");
                    return;
                }

                // Validación de fortaleza de contraseña
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(null,
                            "La contraseña debe tener al menos 6 caracteres.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                    ConfirmacionPassword.setText("");
                    return;
                }

                // Validación de caracteres especiales en contraseña
                if (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*\\d.*")) {
                    JOptionPane.showMessageDialog(null,
                            "La contraseña debe contener al menos:\n- Una letra mayúscula\n- Una letra minúscula\n- Un número",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                    ConfirmacionPassword.setText("");
                    return;
                }

                // Validación de tipo de documento
                if (!CiRadioButton.isSelected() && !PasaporteRadioButton.isSelected()) {
                    JOptionPane.showMessageDialog(null,
                            "Debe seleccionar un tipo de documento.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validación de fecha
                if (diaStr.equals("Día") || mesStr.equals("Mes") || anioStr.equals("Año")) {
                    JOptionPane.showMessageDialog(null,
                            "Debe seleccionar una fecha válida.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validación de email
                if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                    JOptionPane.showMessageDialog(null,
                            "Correo electrónico no válido.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validaciones específicas por tipo de documento
                if (CiRadioButton.isSelected()) {
                    if (!documento.matches("\\d{8}")) {
                        JOptionPane.showMessageDialog(null,
                                "La CI debe tener exactamente 8 números.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    tipoDoc = TipoDoc.CI;
                } else {
                    if (!documento.matches("[A-Z]{2}\\d{6}")) {
                        JOptionPane.showMessageDialog(null,
                                "El pasaporte debe tener el formato: 2 letras + 6 números\nEjemplo: AB123456",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    tipoDoc = TipoDoc.PASAPORTE;
                }

                // Validación de fecha de nacimiento
                try {
                    int dia = Integer.parseInt(diaStr);
                    int mes = Integer.parseInt(mesStr);
                    int anio = Integer.parseInt(anioStr);

                    LocalDate fechaNacimiento = LocalDate.of(anio, mes, dia);
                    LocalDate hoy = LocalDate.now();

                    // Validar que la fecha no sea en el futuro
                    if (fechaNacimiento.isAfter(hoy)) {
                        JOptionPane.showMessageDialog(null,
                                "La fecha de nacimiento no puede ser futura.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Validar edad mínima (por ejemplo, 18 años)
                    LocalDate fechaMinima = hoy.minusYears(18);
                    if (fechaNacimiento.isAfter(fechaMinima)) {
                        JOptionPane.showMessageDialog(null,
                                "Debe ser mayor de 18 años para registrarse.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // =================== CREAR CLIENTE ===================
                    sistema.altaCliente(nickname, nombreCliente, apellido, email,
                            fechaNacimiento, nacionalidad, tipoDoc, documento, password);

                    JOptionPane.showMessageDialog(null,
                            "✅ Cliente creado correctamente\n\n" +
                                    "Nickname: " + nickname + "\n" +
                                    "Nombre: " + nombreCliente + " " + apellido + "\n" +
                                    "Email: " + email + "\n" +
                                    "Fecha Nac: " + fechaNacimiento + "\n" +
                                    "🌍 Nacionalidad: " + nacionalidad + "\n" +
                                    "Documento: " + tipoDoc + " " + documento,
                            "Cliente Creado - Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Limpiar campos después del éxito
                    limpiarCampos();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Error en el formato de la fecha.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Error al crear el cliente:\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Error inesperado: " + ex.getMessage(),
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

        // Mejoras de usabilidad
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfirmacionPassword.requestFocus();
            }
        });

        ConfirmacionPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearButton.doClick();
            }
        });
    }

    private void limpiarCampos() {
        NombreUsuario.setText("");
        NombreCliente.setText("");
        Apellido.setText("");
        EmailUsuario.setText("");
        Documento.setText("");
        NacionalidadCliente.setText("");
        passwordField.setText("");
        ConfirmacionPassword.setText("");

        // Limpiar selección de radio buttons
        PasaporteRadioButton.setSelected(false);
        CiRadioButton.setSelected(false);

        // Resetear comboboxes de fecha
        DiaNacimiento.setSelectedItem("Día");
        MesNacimiento.setSelectedItem("Mes");
        AnoNacimiento.setSelectedItem("Año");

        // Volver al primer campo
        NombreUsuario.requestFocus();
    }

    public Container getPanelDeSesion() {
        return PanelDeSesion;
    }

    // Método para inicializar componentes si usas diseñador de GUI
    private void createUIComponents() {
        // Inicialización de componentes si es necesario
        passwordField = new JPasswordField();
        ConfirmacionPassword = new JPasswordField();
    }
}
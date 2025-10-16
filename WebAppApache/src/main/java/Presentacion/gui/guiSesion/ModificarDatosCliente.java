package Presentacion.gui.guiSesion;

import Logica.Fabrica;
import Logica.ISistema;
import Logica.TipoDoc;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class ModificarDatosCliente {
    private JPanel panelDeModificacion;
    private JTextField NomNuevo;
    private JTextField Apellido;
    private JTextField Nacionalidad;
    private JButton modificarButton;
    private JButton cancelarButton;
    private JRadioButton CiRadioButton;
    private JRadioButton PasaporteRadioButton;
    private JTextField Documento;
    private JComboBox DiaNacimiento;
    private JComboBox MesNacimiento;
    private JComboBox AnoNacimiento;

    private final String nickname;

    public ModificarDatosCliente(String nickname) {
        this.nickname = nickname;
        ISistema sistema = Fabrica.getInstance().getISistema();

        var cliente = sistema.obtenerCliente(nickname);
        if (cliente != null) {
            NomNuevo.setText(cliente.getNombre());
            Apellido.setText(cliente.getApellido());
            Nacionalidad.setText(cliente.getNacionalidad());
            Documento.setText(cliente.getNumeroDocumento());

            if (cliente.getTipoDocumento() == TipoDoc.CI) {
                CiRadioButton.setSelected(true);
            } else if (cliente.getTipoDocumento() == TipoDoc.PASAPORTE) {
                PasaporteRadioButton.setSelected(true);
            }

            LocalDate fecha = cliente.getFechaNacimiento();
            if (fecha != null) {
                DiaNacimiento.setSelectedItem(String.valueOf(fecha.getDayOfMonth()));
                MesNacimiento.setSelectedItem(String.valueOf(fecha.getMonthValue()));
                AnoNacimiento.setSelectedItem(String.valueOf(fecha.getYear()));
            }
        }

        modificarButton.addActionListener(e -> {
            String nombre = NomNuevo.getText();
            String apellido = Apellido.getText();
            String nacionalidad = Nacionalidad.getText();
            String documento = Documento.getText();

            String diaStr = (String) DiaNacimiento.getSelectedItem();
            String mesStr = (String) MesNacimiento.getSelectedItem();
            String anioStr = (String) AnoNacimiento.getSelectedItem();

            if (nombre.isEmpty() || apellido.isEmpty() || nacionalidad.isEmpty() || documento.isEmpty() ||
                    (!CiRadioButton.isSelected() && !PasaporteRadioButton.isSelected())) {
                JOptionPane.showMessageDialog(null,
                        "Debe completar todos los campos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
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

            TipoDoc tipoDoc;
            if (CiRadioButton.isSelected()) {
                if (!documento.matches("\\d{8}")) {
                    JOptionPane.showMessageDialog(null,
                            "La CI debe tener 8 números.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                tipoDoc = TipoDoc.CI;
            } else {
                if (!documento.matches("[A-Z]{2}\\d{6}")) {
                    JOptionPane.showMessageDialog(null,
                            "El pasaporte debe tener el formato: 2 letras y 6 números (ej: AB123456).",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                tipoDoc = TipoDoc.PASAPORTE;
            }

            sistema.modificarDatosDeCliente(nickname, nombre, apellido, nacionalidad, fecha, tipoDoc, documento);

            JOptionPane.showMessageDialog(null,
                    "Datos modificados correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelarButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(panelDeModificacion);
            if (window != null) {
                window.dispose();
            }
        });
    }

    public Container getPanelDeVuelo() {
        return panelDeModificacion;
    }
}

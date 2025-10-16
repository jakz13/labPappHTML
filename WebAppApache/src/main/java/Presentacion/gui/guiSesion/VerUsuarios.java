package Presentacion.gui.guiSesion;

import DataTypes.*;
import Logica.Fabrica;
import Logica.ISistema;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VerUsuarios {
    private JPanel panelConsulta;
    private JComboBox<String> comboBoxUsuarios;
    private JTextArea textAreaDatosUsuario;
    private JButton cerrarButton;
    private JPanel JPanelDinamico;

    private final ISistema sistema;

    public VerUsuarios() {
        sistema = Fabrica.getInstance().getISistema();

        JPanelDinamico.setLayout(new FlowLayout());
        textAreaDatosUsuario.setEditable(false);

        DefaultComboBoxModel<String> modeloUsuarios = new DefaultComboBoxModel<>();
        for (DtCliente c : sistema.listarClientes()) {
            modeloUsuarios.addElement("Cliente:" + c.getNickname());
        }
        for (DtAerolinea a : sistema.listarAerolineas()) {
            modeloUsuarios.addElement("Aerolinea:" + a.getNickname());
        }
        comboBoxUsuarios.setModel(modeloUsuarios);
        comboBoxUsuarios.setSelectedIndex(-1);

        comboBoxUsuarios.addActionListener(e -> {
            String seleccionado = (String) comboBoxUsuarios.getSelectedItem();
            if (seleccionado == null) return;

            JPanelDinamico.removeAll();

            if (seleccionado.startsWith("Cliente:")) {
                String nick = seleccionado.substring("Cliente:".length()).trim();
                mostrarCliente(nick);
                generarBotonesCliente(nick);
            } else if (seleccionado.startsWith("Aerolinea:")) {
                String nick = seleccionado.substring("Aerolinea:".length()).trim();
                mostrarAerolinea(nick);
                generarBotonesAerolinea(nick);
            }

            JPanelDinamico.revalidate();
            JPanelDinamico.repaint();
        });

        cerrarButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panelConsulta);
            if (topFrame != null) topFrame.dispose();
        });
    }

    private void mostrarCliente(String nickname) {
        DtCliente c = sistema.obtenerCliente(nickname);
        if (c != null) {
            textAreaDatosUsuario.setText(
                    "Cliente: " + c.getNombre() + " " + c.getApellido() + "\n" +
                            "Email: " + c.getEmail() + "\n" +
                            "Fecha Nacimiento: " + c.getFechaNacimiento() + "\n" +
                            "Nacionalidad: " + c.getNacionalidad() + "\n" +
                            "Tipo documento: " + c.getTipoDocumento() + "\n" +
                            "Numero documento: " + c.getNumeroDocumento() + "\n"
            );
        }
    }

    private void mostrarAerolinea(String nickname) {
        DtAerolinea a = sistema.obtenerAerolinea(nickname);
        if (a != null) {
            textAreaDatosUsuario.setText(
                    "Aerolinea: " + a.getNombre() + "\n" +
                            "Email: " + a.getEmail() + "\n" +
                            "Sitio Web: " + a.getSitioWeb() + "\n" +
                            "Descripcion: " + a.getDescripcion() + "\n"
            );
        }
    }

    private void generarBotonesCliente(String nickname) {
        JButton btnReservas = new JButton("Ver Reservas");
        btnReservas.addActionListener(e -> {
            DtCliente c = sistema.obtenerCliente(nickname);
            mostrarListaInteractiva("Reservas del cliente", c.getReservas().toArray()); // ACA
        });

        JButton btnPaquetes = new JButton("Ver Paquetes");
        btnPaquetes.addActionListener(e -> {
            DtCliente c = sistema.obtenerCliente(nickname);
            List<DtPaquete> paquetesComprados = c.getPaquetesComprados();
            if (paquetesComprados == null || paquetesComprados.isEmpty()) {
                JOptionPane.showMessageDialog(panelConsulta, "El cliente no tiene paquetes comprados.");
                return;
            }
            mostrarListaInteractiva("Paquetes del cliente", paquetesComprados.toArray());
        });

        JPanelDinamico.add(btnReservas);
        JPanelDinamico.add(btnPaquetes);
    }


    private void generarBotonesAerolinea(String nickname) {
        JButton btnRutas = new JButton("Ver Rutas de Vuelo");
        btnRutas.addActionListener(e -> {
            List<DtRutaVuelo> rutas = sistema.listarRutasPorAerolinea(nickname);
            mostrarListaInteractiva("Rutas de la aerolínea", rutas.toArray());
        });

        JPanelDinamico.add(btnRutas);
    }

    private void mostrarListaInteractiva(String titulo, Object[] elementos) {
        if (elementos == null || elementos.length == 0) {
            JOptionPane.showMessageDialog(panelConsulta, "No hay elementos para mostrar.");
            return;
        }

        JList<Object> lista = new JList<>(elementos);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLista = new JScrollPane(lista);
        scrollLista.setPreferredSize(new Dimension(400, 150));

        JTextArea areaDetalle = new JTextArea(8, 40);
        areaDetalle.setEditable(false);
        JScrollPane scrollDetalle = new JScrollPane(areaDetalle);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(scrollLista, BorderLayout.NORTH);
        panel.add(scrollDetalle, BorderLayout.CENTER);

        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !lista.isSelectionEmpty()) {
                Object seleccionado = lista.getSelectedValue();
                String detalle = obtenerDetalle(seleccionado);
                areaDetalle.setText(detalle);
            }
        });

        // Mostrar el panel dentro de JOptionPane
        JOptionPane.showMessageDialog(panelConsulta, panel, titulo, JOptionPane.PLAIN_MESSAGE);
    }

    private String obtenerDetalle(Object obj) {
        StringBuilder detalle = new StringBuilder();

        if (obj instanceof DtRutaVuelo r) {

            detalle.append("Ruta: ").append(r.getNombre()).append("\n")
                    .append("Origen: ").append(r.getCiudadOrigen()).append("\n")
                    .append("Destino: ").append(r.getCiudadDestino()).append("\n")
                    .append("Duración: ").append(r.getHora()).append(" horas\n");

            detalle.append("=== Vuelos ===\n");

            for (DtVuelo v : sistema.listarVuelosPorRuta(r.getNombre())) {
                detalle.append("Vuelo: ").append(v.getNombre()).append("\n")
                        .append("Fecha: ").append(v.getFecha()).append("\n")
                        .append("Duración: ").append(v.getDuracion()).append(" Horas\n")
                        .append("Asientos Ejecutivos: ").append(v.getAsientosEjecutivo()).append("\n")
                        .append("Asientos Turista: ").append(v.getAsientosTurista()).append("\n")
                        .append("Reservas: ").append(v.getReservas().size()).append("\n")
                        .append("-------------------------\n");
            }
        }
        if (obj instanceof DtReserva r) {
            // ACA
            detalle.append("Reserva ID: ").append(r.getId()).append("\n")
                    .append("Costo: $").append(r.getCosto()).append("\n")
                    .append("Tipo Asiento: ").append(r.getTipoAsiento()).append("\n")
                    .append("Cantidad Pasajes: ").append(r.getCantidadPasajes()).append("\n")
                    .append("Cantidad Equipaje extra: ").append(r.getUnidadesEquipajeExtra()).append("\n");
        }
        if (obj instanceof DtPaquete p) {
            detalle.append("Paquete: ").append(p.getNombre()).append("\n")
                    .append("Descripción: ").append(p.getDescripcion()).append("\n")
                    .append("Costo: $").append(String.format("%.2f", p.getCosto())).append("\n")
                    .append("Descuento: ").append(p.getDescuentoPorc()).append("%\n")
                    .append("Periodo de validez: ").append(p.getPeriodoValidezDias()).append(" días\n")
                    .append("Fecha de alta: ").append(p.getFechaAlta()).append("\n");
        }

        return detalle.toString();
    }

    public Container getPanelConsulta() {
        return panelConsulta;
    }
}

package Presentacion.gui;

import Presentacion.gui.guiSesion.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IntentoDiseño {
    public JPanel Ventana;
    public JPanel Recuadro1;
    public JLabel NombreEmpresa;
    public JPanel RecuadroCentral;
    public JPanel PanelMedIzq;
    public JLabel label1;
    public JPanel PanelInferiorizq;
    public JButton AltaClienteButton;
    private JPanel PanelUsuarios;
    private JButton AltaAereolinea;
    private JButton ALTACIUDADButton;
    private JButton AltaRutaVuelo;
    private JButton ALTAVUELO;
    private JButton MODIFICARUSUARIOS;
    private JButton agregarRutaVuelo;
    private JButton crearPaquete;
    private JButton consultaVuelo;
    private JButton HacerReservaVuelo;
    private JButton ANIADIRPAQUETE;
    private JButton CONSULTARUTA;
    private JButton USUARIOS;
    private JButton CREARCATEGORIA;
    private JButton CONSULTARPAQUETE;
    private JButton AceptarRuta;

    public IntentoDiseño(JFrame framePrincipal) {
        AltaClienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                InicioSesion IS = new InicioSesion();
                JFrame frameSesion = new JFrame("Inicio de Sesión");
                frameSesion.setContentPane(IS.getPanelDeSesion());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                // Escucha el cierre de la ventana de inicio de sesión
                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
        AltaAereolinea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                AltaAereolinea AA = new AltaAereolinea();
                JFrame frameSesion = new JFrame("Alta Aereolinea");
                frameSesion.setContentPane(AA.getPanelDeSesion());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
        ALTACIUDADButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                AltaCiudad AC = new AltaCiudad();
                JFrame frameSesion = new JFrame("Alta Aereolinea");
                frameSesion.setContentPane(AC.getPanelDeCiudad());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
        AltaRutaVuelo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                NuevaRutaVuelo NR = new NuevaRutaVuelo();
                JFrame frameSesion = new JFrame("Alta Ruta Vuelo");
                frameSesion.setContentPane(NR.getPanelAltaRuta());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
        ALTAVUELO.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                AltaVuelo AVU = new AltaVuelo();
                JFrame frameSesion = new JFrame("Alta Vuelo");
                frameSesion.setContentPane(AVU.getpanelDeVuelo());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
        MODIFICARUSUARIOS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                EligeUsuario EG = new EligeUsuario();
                JFrame frameSesion = new JFrame("Modificar Usuario");
                frameSesion.setContentPane(EG.getpanelModificar());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });

        agregarRutaVuelo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                AgregarRutaVuelo ARV = new AgregarRutaVuelo();
                JFrame frameSesion = new JFrame("Agregar Ruta Vuelo a Paquete");
                frameSesion.setContentPane(ARV.getPanelDeSesion());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });

        crearPaquete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                AltaPaquete APQ = new AltaPaquete();
                JFrame frameSesion = new JFrame("Crear Paquete");
                frameSesion.setContentPane(APQ.getPanel1());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });

        consultaVuelo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                ConsultaVuelo CV = new ConsultaVuelo();
                JFrame frameSesion = new JFrame("Consulta Vuelo");
                frameSesion.setContentPane(CV.getPanelConsultaVeulo());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
        HacerReservaVuelo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                ReservaVuelo RV = new ReservaVuelo();
                JFrame frameSesion = new JFrame("Reservar Vuelo");
                frameSesion.setContentPane(RV.getPanelReserva());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
        CONSULTARUTA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                ConsultaRutaVuelo CRV = new ConsultaRutaVuelo();
                JFrame frameSesion = new JFrame("Consulta de ruta de vuelo");
                frameSesion.setContentPane(CRV.getPanelConsultaRuta());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
        ANIADIRPAQUETE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                CompraPaquete CPQ = new CompraPaquete();
                JFrame frameSesion = new JFrame("Compra Paquete");
                frameSesion.setContentPane(CPQ.getPanelDeSesion());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });

        USUARIOS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framePrincipal.setVisible(false);

                VerUsuarios VU = new VerUsuarios();
                JFrame frameSesion = new JFrame("Consulta usuario");
                frameSesion.setContentPane(VU.getPanelConsulta());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
        CREARCATEGORIA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                framePrincipal.setVisible(false);

                AltaCategoria ACE = new AltaCategoria();
                JFrame frameSesion = new JFrame("Crear categoria");
                frameSesion.setContentPane(ACE.getPanel1());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
        CONSULTARPAQUETE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                framePrincipal.setVisible(false);

                ConsultaPaquete CPP = new ConsultaPaquete();
                JFrame frameSesion = new JFrame("Consulta paquete");
                frameSesion.setContentPane(CPP.getPanelConsultaPaquete());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
        CONSULTARPAQUETE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                framePrincipal.setVisible(false);

                ConsultaPaquete CPP = new ConsultaPaquete();
                JFrame frameSesion = new JFrame("Consulta paquete");
                frameSesion.setContentPane(CPP.getPanelConsultaPaquete());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
        AceptarRuta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                framePrincipal.setVisible(false);

                AceptarRuta AR = new AceptarRuta();
                JFrame frameSesion = new JFrame("Consulta paquete");
                frameSesion.setContentPane(AR.getPanel());
                frameSesion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameSesion.setBounds(framePrincipal.getBounds());

                frameSesion.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        framePrincipal.setVisible(true);
                    }
                });

                frameSesion.setVisible(true);
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}

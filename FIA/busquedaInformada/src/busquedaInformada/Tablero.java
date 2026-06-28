package busquedaInformada;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.*;
import java.util.Random;

public class Tablero extends JFrame {
    private final int dim = 15;
    private JLabel[][] tablero;
    private int[][] matrix;

    // Íconos para objetos
    private ImageIcon sampleIcon;
    private ImageIcon actualIcon;
    private ImageIcon motherICon;
    private ImageIcon obstacleIcon;
    private ImageIcon agente1Img;
    private ImageIcon agente2Img;
    private ImageIcon torch;

    // estados del cluster
    public static ImageIcon clusterSampleIcon4;  // 4 unidades (valor 40)
    public static ImageIcon clusterSampleIcon3;  // 3 unidades (valor 30)
    public static ImageIcon clusterSampleIcon2;  // 2 unidades (valor 20)
    public static ImageIcon clusterSampleIcon1;  // 1 unidad  (valor 10)

    // Variable para obtener el ícono actualmente seleccionado para insertar objetos
    private ImageIcon actualSeleccionado = null;

    // Botones de configuración
    private final JRadioButtonMenuItem obstacleItem = new JRadioButtonMenuItem("Obstacle");
    private final JRadioButtonMenuItem mothershipItem = new JRadioButtonMenuItem("Mothership");
    private final JRadioButtonMenuItem sampleItem = new JRadioButtonMenuItem("Sample");
    private final JRadioButtonMenuItem clusterItem = new JRadioButtonMenuItem("Cluster");

    // Menús y controles
    private final JMenu generate = new JMenu("Random generate");
    private final JMenu settings = new JMenu("Settings");
    private final JMenuItem randomCluster = new JMenuItem("Random cluster");
    private final JMenuItem randomObstacle = new JMenuItem("Random Obstacle");
    private final JMenuItem music = new JMenuItem("Stop Music");
    private Clip clip;

    // Variables para la mothership
    private boolean naveColocada = false;
    private int naveFila = -1;
    private int navecColumna = -1;

    // Agentes
    private Agente agente1;
    private Agente agente2;

    private final BackGroundPanel fondo = new BackGroundPanel(new ImageIcon("imagenes/minecra.png"));

    public Tablero() {
        this.setContentPane(fondo);
        this.setTitle("Practica 3, búsqueda informada");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setBounds(50, 50, dim * 50 + 35, dim * 50 + 35);
        initComponentes();

        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("audio/cancion.wav"));
            clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initComponentes() {
        ButtonGroup settingOptions = new ButtonGroup();
        settingOptions.add(sampleItem);
        settingOptions.add(obstacleItem);
        settingOptions.add(mothershipItem);
        settingOptions.add(clusterItem);

        JMenuBar barraMenus = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem run = new JMenuItem("Run");
        JMenuItem exit = new JMenuItem("Exit");

        this.setJMenuBar(barraMenus);
        barraMenus.add(file);
        barraMenus.add(settings);
        barraMenus.add(generate);
        file.add(run);
        file.add(exit);
        file.add(music);
        settings.add(mothershipItem);
        settings.add(obstacleItem);
        settings.add(clusterItem);
        generate.add(randomCluster);
        generate.add(randomObstacle);

        // Cargar y escalar los íconos
        torch = new ImageIcon("imagenes/antorcha.png");
        torch = new ImageIcon(torch.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));

        agente1Img = new ImageIcon("imagenes/agente1.png");
        agente1Img = new ImageIcon(agente1Img.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));

        agente2Img = new ImageIcon("imagenes/agente2.png");
        agente2Img = new ImageIcon(agente2Img.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));

        obstacleIcon = new ImageIcon("imagenes/obstacle.png");
        obstacleIcon = new ImageIcon(obstacleIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));


        motherICon = new ImageIcon("imagenes/motherICon.png");
        motherICon = new ImageIcon(motherICon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));

        // Cargar íconos para el cluster
        clusterSampleIcon4 = new ImageIcon("imagenes/clusterSample4.png");
        clusterSampleIcon4 = new ImageIcon(clusterSampleIcon4.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        clusterSampleIcon3 = new ImageIcon("imagenes/clusterSample3.png");
        clusterSampleIcon3 = new ImageIcon(clusterSampleIcon3.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        clusterSampleIcon2 = new ImageIcon("imagenes/clusterSample2.png");
        clusterSampleIcon2 = new ImageIcon(clusterSampleIcon2.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        clusterSampleIcon1 = new ImageIcon("imagenes/clusterSample1.png");
        clusterSampleIcon1 = new ImageIcon(clusterSampleIcon1.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));

        actualSeleccionado = null;

        this.setLayout(null);
        formaPlano();

        exit.addActionListener(evt -> gestionaSalir(evt));
        run.addActionListener(evt -> gestionaRun(evt));
        obstacleItem.addItemListener(evt -> {
            if (obstacleItem.isSelected()) {
                actualSeleccionado = obstacleIcon;
            } else {
                actualSeleccionado = null;
            }
        });
        mothershipItem.addItemListener(evt -> {
            if (mothershipItem.isSelected()) {
                actualSeleccionado = motherICon;
            } else {
                actualSeleccionado = null;
            }
        });
        clusterItem.addItemListener(evt -> {
            if (clusterItem.isSelected()) {
                // Para cluster, se usa el ícono con 4 unidades (valor 40)
                actualSeleccionado = clusterSampleIcon4;
            } else {
                actualSeleccionado = null;
            }
        });
        randomCluster.addActionListener(evt -> generarAleatorio(clusterSampleIcon4,40));
        randomObstacle.addActionListener(evt -> generarAleatorio(obstacleIcon, 1));
        music.addActionListener(evt -> {
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.stop();
                    music.setText("Play music");
                } else {
                    clip.start();
                    music.setText("Stop music");
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                goodBye();
            }
        });

        // Instanciar agentes
        agente1 = new Agente("Steve", agente1Img, matrix, tablero, torch);
        agente2 = new Agente("Vegetta 777", agente2Img, matrix, tablero, torch);

        Agente.setNavePosicion(naveFila, navecColumna);
    }

    private void gestionaSalir(ActionEvent evt) {
        goodBye();
    }

    private void goodBye() {
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Deseas Salir?", "Aviso", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // Crea el tablero y asigna listeners a cada celda
    private void formaPlano() {
        tablero = new JLabel[dim][dim];
        matrix = new int[dim][dim];

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                matrix[i][j] = 0;
                tablero[i][j] = new JLabel();
                tablero[i][j].setBounds(j * 50 + 15, i * 50 + 15, 50, 50);
                tablero[i][j].setBorder(BorderFactory.createDashedBorder(Color.white));
                tablero[i][j].setOpaque(false);
                this.add(tablero[i][j]);

                tablero[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        insertaObjeto(e);
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        insertaObjeto(e);
                    }
                });
            }
        }
    }

    private void generarAleatorio(Icon icon, int tipo) {
        int x, y;
        for (int i = 0; i < 7; i++) {
            x = (int) (Math.random() * dim);
            y = (int) (Math.random() * dim);
            if (matrix[x][y] == 0) {
                matrix[x][y] = tipo;
                tablero[x][y].setIcon(icon);
            }
        }
    }
    private void gestionaRun(ActionEvent evt) {
        if (!agente1.isAlive()) {
            agente1.start();
        }
        if (!agente2.isAlive()) {
            agente2.start();
        }
        settings.setEnabled(false);
        generate.setEnabled(false);
    }

    // Inserta el objeto que corresponde en la celda acorde a lo seleccionado
    public void insertaObjeto(MouseEvent e) {
        JLabel casilla = (JLabel) e.getSource();
        int fila = (casilla.getY() - 15) / 50;
        int columna = (casilla.getX() - 15) / 50;

        if (actualSeleccionado == motherICon && matrix[fila][columna] != 2) {
            if (!naveColocada) {
                casilla.setIcon(motherICon);
                naveColocada = true;
                matrix[fila][columna] = 2; // Mothership
                naveFila = fila;
                navecColumna = columna;
                System.out.println("La mothership está en la posición: Fila " + naveFila + ", Columna " + navecColumna);
                Agente.setNavePosicion(naveFila, navecColumna);
            } else {
                JOptionPane.showMessageDialog(this, "Ya existe una mothership en el tablero");
            }
        } else if (actualSeleccionado == obstacleIcon) {
            casilla.setIcon(obstacleIcon);
            matrix[fila][columna] = 1; // Obstáculo
        } else if (actualSeleccionado == sampleIcon) {
            casilla.setIcon(sampleIcon);
            matrix[fila][columna] = 3; // Sample normal
        } else if (actualSeleccionado == clusterSampleIcon4) {
            // Se inserta un cluster sample: valor 40 representa 4 unidades
            casilla.setIcon(clusterSampleIcon4);
            matrix[fila][columna] = 40;
        } else if (actualSeleccionado == null) {
            casilla.setIcon(null);
            matrix[fila][columna] = 0; // Celda vacía
        }
    }
//obtiene los clusters en cada caso
    public static ImageIcon getClusterIcon(int value) {
        switch(value) {
            case 40: return clusterSampleIcon4;
            case 30: return clusterSampleIcon3;
            case 20: return clusterSampleIcon2;
            case 10: return clusterSampleIcon1;
            default: return null;
        }
    }

}

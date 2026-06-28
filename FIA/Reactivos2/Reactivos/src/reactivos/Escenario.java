
package reactivos;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author macario
 */
public class Escenario extends JFrame {

    private JLabel[][] tablero;
    private int[][] matrix;
    private final int dim = 15;


    private ImageIcon robot1;
    private ImageIcon robot2;
    private ImageIcon obstacleIcon;
    private ImageIcon sampleIcon;
    private ImageIcon actualIcon;
    private ImageIcon motherIcon;

    private int bandera = 0;
    private boolean naveColocada = false; //variable para raestrar si se ha colocado una nave
    private int naveFila = -1; //Fila donde está la nave nodriza
    private int naveColumna = -1; //Columnda donde está la nodriza

    private Agente wallE;
    private Agente eva;

    private final BackGroundPanel fondo = new BackGroundPanel(new ImageIcon("imagenes/surface.jpg"));


    private final JMenu generate = new JMenu("Random generate");
    private final JMenu settings = new JMenu("Settigs");
    private final JRadioButtonMenuItem obstacle = new JRadioButtonMenuItem("Obstacle");
    private final JRadioButtonMenuItem sample = new JRadioButtonMenuItem("Sample");
    private final JRadioButtonMenuItem motherShip = new JRadioButtonMenuItem("MotherShip");
    private final JMenuItem randomSample = new JMenuItem("Random Sample");
    private final JMenuItem randomObstacle = new JMenuItem("Random Obstacle");
    private JMenuItem apagar = new JMenuItem ("Stop Music");
    private Clip clip;


    public Escenario() {
        this.setContentPane(fondo);
        this.setTitle("Agentes");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setBounds(50, 50, dim * 50 + 35, dim * 50 + 85);
        initComponents();

        // Clip de audio
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("audio/cancion.wav"));
            clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    private void initComponents() {
        ButtonGroup settingsOptions = new ButtonGroup();
        settingsOptions.add(sample);
        settingsOptions.add(obstacle);
        settingsOptions.add(motherShip);

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
        file.add(apagar);
        settings.add(motherShip);
        settings.add(obstacle);
        settings.add(sample);
        generate.add(randomSample);
        generate.add(randomObstacle);


        robot1 = new ImageIcon("imagenes/terrorist1.png");
        robot1 = new ImageIcon(robot1.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));

        robot2 = new ImageIcon("imagenes/terrorist2.png");
        robot2 = new ImageIcon(robot2.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));

        obstacleIcon = new ImageIcon("imagenes/fire.png");
        obstacleIcon = new ImageIcon(obstacleIcon.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));

        sampleIcon = new ImageIcon("imagenes/bomba.png");
        sampleIcon = new ImageIcon(sampleIcon.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));

        motherIcon = new ImageIcon("imagenes/site.png");
        motherIcon = new ImageIcon(motherIcon.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));



        this.setLayout(null);
        formaPlano();

        exit.addActionListener(evt -> gestionaSalir(evt));
        run.addActionListener(evt -> gestionaRun(evt));
        obstacle.addItemListener(evt -> gestionaObstacle(evt));
        sample.addItemListener(evt -> gestionaSample(evt));
        motherShip.addItemListener(evt -> gestionaMotherShip(evt));
        randomSample.addActionListener(evt -> generarAleatorio(sampleIcon,3));
        randomObstacle.addActionListener(evt-> generarAleatorio(obstacleIcon,1));
        apagar.addActionListener(evt -> {
            if(clip != null){
                if(clip.isRunning()){
                    clip.stop();
                    apagar.setText("Play Music");
                }else{
                    clip.start();
                    apagar.setText("Stop Music");
                }
            }
        });


        class MyWindowAdapter extends WindowAdapter {
            @Override
            public void windowClosing(WindowEvent eventObject) {
                goodBye();
            }
        }
        addWindowListener(new MyWindowAdapter());

        // Crea 2 agentes
        wallE = new Agente("terrorist1", robot1, matrix, tablero);
        eva = new Agente("terrorist2", robot2, matrix, tablero);

        Agente.setNavePosicion(naveFila, naveColumna);
    }

    private void gestionaSalir(ActionEvent eventObject) {
        goodBye();
    }

    private void goodBye() {
        int respuesta = JOptionPane.showConfirmDialog(rootPane, "Desea salir?", "Aviso", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) System.exit(0);
    }

    private void formaPlano() {
        tablero = new JLabel[dim][dim];
        matrix = new int[dim][dim];

        int i, j;

        for (i = 0; i < dim; i++) {  // ← Aquí estaba el error
            for (j = 0; j < dim; j++) {
                matrix[i][j] = 0;
                tablero[i][j] = new JLabel();
                tablero[i][j].setBounds(j * 50 + 15, i * 50 + 15, 50, 50);
                tablero[i][j].setBorder(BorderFactory.createDashedBorder(Color.white));
                tablero[i][j].setOpaque(false);
                this.add(tablero[i][j]);

                tablero[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
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


    private void generarAleatorio(ImageIcon imagen, int tipo){
        int x = 0, y = 0;
        for(int i  = 0 ; i < 7  ; i ++){   
            x = (int) (Math.random() * dim );
            y = (int) (Math.random() * dim);
            if (matrix[x][y] == 0){
                matrix[x][y] = tipo;
                tablero[x][y].setIcon(imagen);
            }
        }
    } //metodo para generar de manera aleatoria obstaculos o samples
        

    private void gestionaObstacle(ItemEvent eventObject) {
        JRadioButtonMenuItem opt = (JRadioButtonMenuItem) eventObject.getSource();
        if (opt.isSelected())
            actualIcon = obstacleIcon;
        else actualIcon = null;
    }

    private void gestionaSample(ItemEvent eventObject) {
        JRadioButtonMenuItem opt = (JRadioButtonMenuItem) eventObject.getSource();
        if (opt.isSelected())
            actualIcon = sampleIcon;
        else actualIcon = null;
    }

    private void gestionaMotherShip(ItemEvent eventObject) {
        JRadioButtonMenuItem opt = (JRadioButtonMenuItem) eventObject.getSource();
        if (opt.isSelected())
            actualIcon = motherIcon;
        else actualIcon = null;
    }

    private void gestionaRun(ActionEvent eventObject) {
        if (!wallE.isAlive()) wallE.start();
        if (!eva.isAlive()) eva.start();
        settings.setEnabled(false);
        generate.setEnabled(false);
    }

    public void insertaObjeto(MouseEvent e) {
        JLabel casilla = (JLabel) e.getSource();
        int fila = (casilla.getY() - 15) / 50;
        int columna = (casilla.getX() - 15) / 50;

        if (actualIcon == motherIcon && matrix[fila][columna] != 2) {
            if (!naveColocada) {
                casilla.setIcon(motherIcon);
                naveColocada = true;
                matrix[fila][columna] = 2;
                naveFila = fila;
                naveColumna = columna;

                System.out.println("La nave nodriza está en la posición: Fila " + naveFila + " Columna " + ", Columna" + naveColumna);

                Agente.setNavePosicion(naveFila, naveColumna);
            } else {
                JOptionPane.showMessageDialog(this, "Ya existe una nave en el tablero");
            }
        } else if (actualIcon == obstacleIcon) {
            casilla.setIcon(obstacleIcon);
            matrix[fila][columna] = 1; //obstáculo
        } else if (actualIcon == sampleIcon) {
            casilla.setIcon(sampleIcon);
            matrix[fila][columna] = 3; //el premio
        } else if (actualIcon == null) {
            casilla.setIcon(null);
            matrix[fila][columna] = 0; //voidsianin
        }

    }
}
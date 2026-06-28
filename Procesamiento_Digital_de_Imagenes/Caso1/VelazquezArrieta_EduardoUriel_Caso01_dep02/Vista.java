import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Vista extends JFrame {

    private JLabel labelImagen;
    private BufferedImage imagenOriginal;
    private Controlador controlador;


    private JSlider sliderSalPimienta;
    private JSlider sliderGaussiano;
    private JLabel lblValSp;
    private JLabel lblValGauss;
    private JButton btnRestaurar; 

    public Vista() {
        setTitle("Control de Ruido en Imagen - MVC");
        setSize(800, 600); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        labelImagen = new JLabel();
        labelImagen.setHorizontalAlignment(JLabel.CENTER);
        add(new JScrollPane(labelImagen), BorderLayout.CENTER); 
        
        JPanel panelPrincipalControl = new JPanel();
        panelPrincipalControl.setLayout(new BoxLayout(panelPrincipalControl, BoxLayout.Y_AXIS));
        panelPrincipalControl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Fila de Carga y Restaurar
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCargar = new JButton("Cargar Imagen");
        btnRestaurar = new JButton("Ver Original");
        btnRestaurar.setEnabled(false); // Deshabilitado hasta que haya imagen
        panelBotones.add(btnCargar);
        panelBotones.add(btnRestaurar);
        panelPrincipalControl.add(panelBotones);

        // Separador
        panelPrincipalControl.add(new JSeparator());

        // 2. Panel de Deslizadores (GridLayout para orden)
        JPanel panelSliders = new JPanel(new GridLayout(2, 3, 10, 5));
        panelSliders.setBorder(BorderFactory.createTitledBorder("Controles de Ruido (sobre original)"));

        // -- Controles Sal y Pimienta --
        JLabel lblSp = new JLabel("Ruido S&P (Probabilidad %):");
        // Slider de 0 a 100 representing 0.0 a 0.2 de probabilidad aprox
        sliderSalPimienta = new JSlider(JSlider.HORIZONTAL, 0, 100, 0); 
        sliderSalPimienta.setMajorTickSpacing(20);
        sliderSalPimienta.setPaintTicks(true);
        sliderSalPimienta.setEnabled(false);
        lblValSp = new JLabel("0%");
        
        panelSliders.add(lblSp);
        panelSliders.add(sliderSalPimienta);
        panelSliders.add(lblValSp);

        // -- Controles Gaussianos --
        JLabel lblGauss = new JLabel("Ruido Gaussiano (Sigma):");
        // Slider de 0 a 100 para Sigma
        sliderGaussiano = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        sliderGaussiano.setMajorTickSpacing(20);
        sliderGaussiano.setPaintTicks(true);
        sliderGaussiano.setEnabled(false);
        lblValGauss = new JLabel("0");

        panelSliders.add(lblGauss);
        panelSliders.add(sliderGaussiano);
        panelSliders.add(lblValGauss);

        panelPrincipalControl.add(panelSliders);

        add(panelPrincipalControl, BorderLayout.SOUTH);

        // --- Eventos ---

        // Botón Cargar
        btnCargar.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                controlador.cargarImagen(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        // Botón Restaurar
        btnRestaurar.addActionListener(e -> {
            if (imagenOriginal != null) {
                resetSliders(); // Ponemos sliders a 0
                mostrarImagenProcesada(imagenOriginal); // Mostramos la original pura
            }
        });

        // Evento Slider Sal y Pimienta (ChangeListener)
        sliderSalPimienta.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (imagenOriginal != null && !sliderSalPimienta.getValueIsAdjusting()) {
                    int valor = sliderSalPimienta.getValue();
                    lblValSp.setText(valor + "%");
                    
                    // Si movemos este slider, reseteamos el otro para no mezclar ruidos
                    // (Opcional, depende de si quieres mezclar ruidos o no)
                    if (valor > 0) sliderGaussiano.setValue(0); 
                    
                    controlador.actualizarRuidoSalPimienta(imagenOriginal, valor);
                }
            }
        });

        // Evento Slider Gaussiano (ChangeListener)
        sliderGaussiano.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (imagenOriginal != null && !sliderGaussiano.getValueIsAdjusting()) {
                    int valor = sliderGaussiano.getValue();
                    lblValGauss.setText(String.valueOf(valor));
                    
                    // Si movemos este slider, reseteamos el otro
                    if (valor > 0) sliderSalPimienta.setValue(0);

                    controlador.actualizarRuidoGaussiano(imagenOriginal, valor);
                }
            }
        });
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    // Método que llama el controlador al cargar la imagen por primera vez
    public void setImagenOriginal(BufferedImage img) {
        this.imagenOriginal = img;
        resetSliders();
        // Habilitar controles
        sliderSalPimienta.setEnabled(true);
        sliderGaussiano.setEnabled(true);
        btnRestaurar.setEnabled(true);
        // Mostrar la original inicialmente
        mostrarImagenProcesada(img);
    }

    // Método para actualizar la imagen que se ve en pantalla
    public void mostrarImagenProcesada(BufferedImage img) {
        if (img != null) {
            labelImagen.setIcon(new ImageIcon(img));
            // Forzar recálculo del scroll pane si la imagen cambia de tamaño
            labelImagen.revalidate(); 
        }
    }
    
    private void resetSliders() {
        sliderSalPimienta.setValue(0);
        sliderGaussiano.setValue(0);
        lblValSp.setText("0%");
        lblValGauss.setText("0");
    }
}
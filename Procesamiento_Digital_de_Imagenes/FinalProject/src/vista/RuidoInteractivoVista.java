package vista;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.image.BufferedImage;

public class RuidoInteractivoVista extends JFrame {

    public JButton btnCargar = new JButton("Cargar Imagen");
    public JButton btnRestaurar = new JButton("Ver Original");
    public JSlider sliderSalPimienta = new JSlider(0, 100, 0);
    public JSlider sliderGaussiano = new JSlider(0, 100, 0);
    public JLabel lblValSp = new JLabel("0%");
    public JLabel lblValGauss = new JLabel("0");
    public JLabel labelImagen = new JLabel();
    public BufferedImage imagenOriginal;

    public RuidoInteractivoVista() {
        super("Caso 1 - Control de Ruido en Tiempo Real");
        setSize(900, 650);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        labelImagen.setHorizontalAlignment(JLabel.CENTER);
        add(new JScrollPane(labelImagen), BorderLayout.CENTER);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRestaurar.setEnabled(false);
        panelBotones.add(btnCargar);
        panelBotones.add(btnRestaurar);
        panelPrincipal.add(panelBotones);
        panelPrincipal.add(new JSeparator());

        JPanel panelSliders = new JPanel(new GridLayout(2, 3, 10, 5));
        panelSliders.setBorder(BorderFactory.createTitledBorder("Controles de Ruido"));

        JLabel lblSp = new JLabel("Ruido S&P (%):");
        sliderSalPimienta.setMajorTickSpacing(20);
        sliderSalPimienta.setPaintTicks(true);
        sliderSalPimienta.setEnabled(false);
        panelSliders.add(lblSp);
        panelSliders.add(sliderSalPimienta);
        panelSliders.add(lblValSp);

        JLabel lblGauss = new JLabel("Ruido Gaussiano (Sigma):");
        sliderGaussiano.setMajorTickSpacing(20);
        sliderGaussiano.setPaintTicks(true);
        sliderGaussiano.setEnabled(false);
        panelSliders.add(lblGauss);
        panelSliders.add(sliderGaussiano);
        panelSliders.add(lblValGauss);

        panelPrincipal.add(panelSliders);
        add(panelPrincipal, BorderLayout.SOUTH);
    }

    public void setImagenOriginal(BufferedImage img) {
        this.imagenOriginal = img;
        resetSliders();
        sliderSalPimienta.setEnabled(true);
        sliderGaussiano.setEnabled(true);
        btnRestaurar.setEnabled(true);
        mostrarImagen(img);
    }

    public void mostrarImagen(BufferedImage img) {
        if (img != null) {
            labelImagen.setIcon(new ImageIcon(img));
            labelImagen.revalidate();
        }
    }

    public void resetSliders() {
        sliderSalPimienta.setValue(0);
        sliderGaussiano.setValue(0);
        lblValSp.setText("0%");
        lblValGauss.setText("0");
    }

    public void onSliderChanged(ChangeEvent e, Runnable handler) {
        if (imagenOriginal != null) {
            Object src = e.getSource();
            if (src == sliderSalPimienta && sliderSalPimienta.getValueIsAdjusting()) return;
            if (src == sliderGaussiano && sliderGaussiano.getValueIsAdjusting()) return;
            handler.run();
        }
    }
}

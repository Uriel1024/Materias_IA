package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class EspaciosColorVista extends JFrame {

    public JButton btnCargar = new JButton("Cargar Imagen");
    public JButton btnAplicar = new JButton("Aplicar");
    public JComboBox<String> comboOpciones = new JComboBox<>(new String[] {
        "Canal R", "Canal G", "Canal B",
        "RGB a CMY", "CMY a RGB",
        "RGB a HSV", "HSV a RGB",
        "RGB a HSI", "HSI a RGB",
        "RGB a YIQ", "YIQ a RGB",
        "Convertir a lαβ", "LAB a RGB"
    });
    public JLabel lblOriginal = new JLabel("Original", SwingConstants.CENTER);
    public JLabel lblResultado = new JLabel("Resultado", SwingConstants.CENTER);

    public EspaciosColorVista() {
        super("Practica 3 - Espacios de Color");
        setSize(1000, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.add(btnCargar);
        top.add(comboOpciones);
        top.add(btnAplicar);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2));
        center.add(new JScrollPane(lblOriginal));
        center.add(new JScrollPane(lblResultado));
        add(center, BorderLayout.CENTER);
    }

    public void mostrarOriginal(BufferedImage img) { ImageUtils.displayOn(lblOriginal, img, 500, 500); }
    public void mostrarResultado(BufferedImage img) { ImageUtils.displayOn(lblResultado, img, 500, 500); }
}

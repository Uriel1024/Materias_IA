package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class BrilloContrasteVista extends JFrame {

    public JButton btnAbrir = new JButton("Abrir Imagen");
    public JButton btnGuardarRGB = new JButton("Guardar RGB");
    public JButton btnGuardarGris = new JButton("Guardar Gris");
    public JSlider br = new JSlider(-256, 255, 0);
    public JSlider contr = new JSlider(1, 200, 100);
    public JLabel lvlbrillo = new JLabel("Brillo: 0");
    public JLabel lvlcontraste = new JLabel("Contraste: 1.0");
    public JLabel lblOriginal = new JLabel("Original", SwingConstants.CENTER);
    public JLabel lblModificada = new JLabel("RGB", SwingConstants.CENTER);
    public JLabel lblGris = new JLabel("Gris", SwingConstants.CENTER);

    public BrilloContrasteVista() {
        super("Practica 1 - Brillo y Contraste");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlControles = new JPanel(new GridLayout(3, 1));
        JPanel pnlBotones = new JPanel();
        pnlBotones.add(btnAbrir);
        pnlBotones.add(btnGuardarRGB);
        pnlBotones.add(btnGuardarGris);
        JPanel pnlB = new JPanel(new FlowLayout()); pnlB.add(lvlbrillo); pnlB.add(br);
        JPanel pnlC = new JPanel(new FlowLayout()); pnlC.add(lvlcontraste); pnlC.add(contr);
        br.setPreferredSize(new Dimension(400, 25));
        contr.setPreferredSize(new Dimension(400, 25));
        pnlControles.add(pnlBotones);
        pnlControles.add(pnlB);
        pnlControles.add(pnlC);

        JPanel pnlImagenes = new JPanel(new GridLayout(1, 3, 10, 10));
        pnlImagenes.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlImagenes.add(crearScroll(lblOriginal));
        pnlImagenes.add(crearScroll(lblModificada));
        pnlImagenes.add(crearScroll(lblGris));

        add(pnlControles, BorderLayout.NORTH);
        add(pnlImagenes, BorderLayout.CENTER);

        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    private JScrollPane crearScroll(JLabel label) {
        JScrollPane scroll = new JScrollPane(label);
        scroll.setPreferredSize(new Dimension(310, 310));
        return scroll;
    }

    public void mostrarOriginal(BufferedImage img) { ImageUtils.displayOn(lblOriginal, img, 300, 300); }
    public void mostrarModificada(BufferedImage img) { ImageUtils.displayOn(lblModificada, img, 300, 300); }
    public void mostrarGris(BufferedImage img) { ImageUtils.displayOn(lblGris, img, 300, 300); }
}

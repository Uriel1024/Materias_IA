package practica1;

import javax.swing.*;
import java.awt.*;

public class Interfaz extends JFrame {
    public JLabel lblOriginal, lblModificada, lblGris;
    public JSlider br, contr;
    public JLabel lvlbrillo, lvlcontraste;
    public JButton btnAbrir, btngris, btnrgb;
    public JPanel pnlExtra; // Espacio para tus nuevos objetos

    public Interfaz() {
        super("Editor MVC - Vista Redimensionada");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Panel Superior: Controles ---
        JPanel pnlControles = new JPanel(new GridLayout(3, 1));
        btnAbrir = new JButton("Abrir Imagen");
        btnrgb = new JButton("Guardar RGB");
        btngris = new JButton("Guardar Gris");
        br = new JSlider(-256, 255, 0);
        contr = new JSlider(1, 200, 100);
        lvlbrillo = new JLabel("Brillo: 0");
        lvlcontraste = new JLabel("Contraste: 1.0");

        JPanel pnlBotones = new JPanel();
        pnlBotones.add(btnAbrir); 
        pnlBotones.add(btnrgb);
        pnlBotones.add(btngris);
        JPanel pnlB = new JPanel(); pnlB.add(lvlbrillo); pnlB.add(br);
        JPanel pnlC = new JPanel(); pnlC.add(lvlcontraste); pnlC.add(contr);

        pnlControles.add(pnlBotones);
        pnlControles.add(pnlB);
        pnlControles.add(pnlC);

        // --- Panel Central: Imágenes ---
        JPanel pnlImagenes = new JPanel(new GridLayout(1, 3, 10, 10));
        pnlImagenes.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        lblOriginal = new JLabel("Original", SwingConstants.CENTER);
        lblModificada = new JLabel("RGB", SwingConstants.CENTER);
        lblGris = new JLabel("Gris", SwingConstants.CENTER);
        br.setPreferredSize(new Dimension(800,25));
        contr.setPreferredSize(new Dimension(800,25));

        pnlImagenes.add(crearScroll(lblOriginal));
        pnlImagenes.add(crearScroll(lblModificada));
        pnlImagenes.add(crearScroll(lblGris));

        // --- Panel Inferior: Espacio para nuevos objetos ---
        pnlExtra = new JPanel();
        pnlExtra.setBorder(BorderFactory.createTitledBorder("Otros Objetos Aquí"));
        pnlExtra.setPreferredSize(new Dimension(1200, 100));
        pnlExtra.add(new JLabel("Puedes agregar tablas, histogramas o más botones aquí."));

        add(pnlControles, BorderLayout.NORTH);
        add(pnlImagenes, BorderLayout.CENTER);
        add(pnlExtra, BorderLayout.SOUTH);

        setSize(1000, 750);
        setLocationRelativeTo(null);
    }

    private JScrollPane crearScroll(JLabel label) {
        JScrollPane scroll = new JScrollPane(label);
        scroll.setPreferredSize(new Dimension(310, 310));
        return scroll;
    }
}
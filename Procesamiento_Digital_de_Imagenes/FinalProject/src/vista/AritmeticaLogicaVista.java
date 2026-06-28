package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class AritmeticaLogicaVista extends JFrame {

    public JButton btnCargar1 = new JButton("Cargar Img 1");
    public JButton btnCargar2 = new JButton("Cargar Img 2");
    public JButton btnAplicar = new JButton("Ejecutar");
    public JComboBox<String> ops = new JComboBox<>(new String[] {
        "Suma", "Resta", "Multi", "Division",
        "AND", "OR", "XOR", "NOT",
        "<", "<=", ">", ">=", "==", "!=",
        "Rotar 45", "Escalar 2x"
    });
    public JLabel lbl1 = new JLabel(), lbl2 = new JLabel(), lblRes = new JLabel();

    public AritmeticaLogicaVista() {
        super("Practica 5 - Operaciones Aritmeticas, Logicas y Geometria");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel top = new JPanel();
        top.add(btnCargar1); top.add(btnCargar2); top.add(ops); top.add(btnAplicar);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 5, 5));
        center.add(crearScroll(lbl1, "Imagen 1"));
        center.add(crearScroll(lbl2, "Imagen 2"));
        center.add(crearScroll(lblRes, "Resultado"));
        add(center, BorderLayout.CENTER);

        setSize(1200, 500);
        setLocationRelativeTo(null);
    }

    private JScrollPane crearScroll(JLabel lbl, String title) {
        JScrollPane sp = new JScrollPane(lbl);
        sp.setBorder(BorderFactory.createTitledBorder(title));
        return sp;
    }

    public void mostrarImg1(BufferedImage img) { ImageUtils.displayOn(lbl1, img, 400, 400); }
    public void mostrarImg2(BufferedImage img) { ImageUtils.displayOn(lbl2, img, 400, 400); }
    public void mostrarRes(BufferedImage img) { ImageUtils.displayOn(lblRes, img, 400, 400); }
}

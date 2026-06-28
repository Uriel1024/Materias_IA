package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class MorfologiaBinariaVista extends JFrame {

    public JButton btnCargar = new JButton("Cargar Imagen");
    public JButton btnAplicar = new JButton("Aplicar");
    public JButton btnUsarResultado = new JButton("Usar Resultado como Original");
    public JComboBox<String> comboOperaciones;
    public JLabel lblOriginal = new JLabel();
    public JLabel lblResultado = new JLabel();
    public JTextArea txtInfo = new JTextArea();
    public BufferedImage imagenResultadoActual = null;

    public static final String[] OPERACIONES = {
        "Aniadir Ruido Sal", "Aniadir Ruido Pimienta",
        "Erosion", "Dilatacion", "Apertura", "Clausura", "Esqueletizado"
    };

    public MorfologiaBinariaVista() {
        super("Practica 10 - Morfologia Binaria");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        comboOperaciones = new JComboBox<>(OPERACIONES);
        top.add(btnCargar);
        top.add(new JLabel("Operacion:"));
        top.add(comboOperaciones);
        top.add(btnAplicar);
        top.add(btnUsarResultado);
        add(top, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 4, 4));
        centro.add(crearPanel("Imagen Original (Binaria)", lblOriginal));
        centro.add(crearPanel("Imagen Resultado", lblResultado));
        add(centro, BorderLayout.CENTER);

        txtInfo.setRows(4);
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(new JScrollPane(txtInfo), BorderLayout.SOUTH);
    }

    private JPanel crearPanel(String titulo, JLabel lbl) {
        JPanel p = new JPanel(new BorderLayout());
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        p.add(lbl);
        return p;
    }

    public void mostrarOriginal(BufferedImage img) { ImageUtils.displayOn(lblOriginal, img, 400, 400); }
    public void mostrarResultado(BufferedImage img) { ImageUtils.displayOn(lblResultado, img, 400, 400); }
}

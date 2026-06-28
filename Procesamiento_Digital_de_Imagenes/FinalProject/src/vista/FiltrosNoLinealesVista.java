package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class FiltrosNoLinealesVista extends JFrame {

    public JButton btnCargar = new JButton("Cargar Imagen");
    public JButton btnAplicar = new JButton("Aplicar Filtro");
    public JComboBox<String> comboFiltros;
    public JLabel lblMask = new JLabel("Mascara:");
    public JSpinner spnMask = new JSpinner(new SpinnerNumberModel(3, 3, 11, 2));
    public JLabel lblParamP = new JLabel("P (Alfa/C.Armonico):");
    public JSpinner spnParamP = new JSpinner(new SpinnerNumberModel(2.0, -10.0, 10.0, 0.5));
    public JLabel lblOriginal = new JLabel();
    public JLabel lblResultado = new JLabel();
    public JLabel lblHistOriginal = new JLabel();
    public JLabel lblHistResultado = new JLabel();
    public JTextArea txtInfo = new JTextArea();

    public static final String[] FILTROS = {
        "Filtro de la Mediana", "Filtro Alfa-Trimmed",
        "Filtro del Maximo", "Filtro del Minimo",
        "Filtro del Punto Medio", "Filtro Inferior Armonico",
        "Filtro Contra Armonico", "Filtro Geometrico",
        "Filtro Maximo-Minimo", "Filtro Media Aritmetica"
    };

    public FiltrosNoLinealesVista() {
        super("Practica 9 - Filtros No Lineales");
        setSize(1300, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        comboFiltros = new JComboBox<>(FILTROS);
        top.add(btnCargar);
        top.add(new JLabel("Filtro:"));
        top.add(comboFiltros);
        top.add(lblMask);
        top.add(spnMask);
        top.add(lblParamP);
        top.add(spnParamP);
        top.add(btnAplicar);
        add(top, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 2, 4, 4));
        centro.add(crearPanel("Imagen Original", lblOriginal));
        centro.add(crearPanel("Histograma Original", lblHistOriginal));
        centro.add(crearPanel("Resultado", lblResultado));
        centro.add(crearPanel("Histograma Resultado", lblHistResultado));
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

    public void mostrarOriginal(BufferedImage img) { ImageUtils.displayOn(lblOriginal, img, 400, 300); }
    public void mostrarResultado(BufferedImage img) { ImageUtils.displayOn(lblResultado, img, 400, 300); }
    public void mostrarHistOriginal(BufferedImage img) { ImageUtils.displayOn(lblHistOriginal, img, 400, 300); }
    public void mostrarHistResultado(BufferedImage img) { ImageUtils.displayOn(lblHistResultado, img, 400, 300); }
}

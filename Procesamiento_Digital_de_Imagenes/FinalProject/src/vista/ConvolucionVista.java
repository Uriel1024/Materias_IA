package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class ConvolucionVista extends JFrame {

    public JButton btnCargar = new JButton("Cargar Imagen");
    public JButton btnAplicar = new JButton("Aplicar Filtro");
    public JComboBox<String> comboFiltros;
    public JCheckBox chkBinarizar = new JCheckBox("Binarizar");
    public JSpinner spnUmbral = new JSpinner(new SpinnerNumberModel(128, 0, 255, 1));
    public JLabel lblUmbral = new JLabel("Umbral:");
    public JLabel lblOriginal = new JLabel();
    public JLabel lblResultado = new JLabel();
    public JLabel lblHistOriginal = new JLabel();
    public JLabel lblHistResultado = new JLabel();
    public JTextArea txtInfo = new JTextArea();

    public static final String[] FILTROS = {
        "Negativo", "Binarizacion (umbral 128)",
        "PB | Promedio 3x3", "PB | Promedio 5x5", "PB | Promedio 7x7",
        "PB | Gaussiano 3x3", "PB | Gaussiano 5x5",
        "PB | Definicion Suave", "PB | Definicion Media", "PB | Definicion Fuerte",
        "PA | Laplaciano 4 vecinos", "PA | Laplaciano 8 vecinos",
        "PA | Sobel Hr", "PA | Sobel Hc", "PA | Sobel Combinado (Hr+Hc)",
        "PA | Prewitt Hr", "PA | Prewitt Hc", "PA | Prewitt Combinado (Hr+Hc)",
        "PA | Roberts Hr", "PA | Roberts Hc", "PA | Roberts Combinado (Hr+Hc)",
        "PA | Frei-Chen Hr", "PA | Frei-Chen Hc", "PA | Frei-Chen Combinado (Hr+Hc)",
        "COMP | Prewitt H1 (Este)", "COMP | Prewitt H2 (Noreste)",
        "COMP | Prewitt H3 (Norte)", "COMP | Prewitt H4 (Noroeste)",
        "COMP | Prewitt H5 (Oeste)", "COMP | Prewitt H6 (Suroeste)",
        "COMP | Prewitt H7 (Sur)", "COMP | Prewitt H8 (Sureste)",
        "COMP | Prewitt Suma (8 dirs)", "COMP | Prewitt OR (8 dirs)",
        "COMP | Kirsch H1 (Este)", "COMP | Kirsch H2 (Noreste)",
        "COMP | Kirsch H3 (Norte)", "COMP | Kirsch H4 (Noroeste)",
        "COMP | Kirsch H5 (Oeste)", "COMP | Kirsch H6 (Suroeste)",
        "COMP | Kirsch H7 (Sur)", "COMP | Kirsch H8 (Sureste)",
        "COMP | Kirsch Suma (8 dirs)", "COMP | Kirsch OR (8 dirs)",
        "COMP | Robinson3 H1 (Este)", "COMP | Robinson3 H2 (Noreste)",
        "COMP | Robinson3 H3 (Norte)", "COMP | Robinson3 H4 (Noroeste)",
        "COMP | Robinson3 H5 (Oeste)", "COMP | Robinson3 H6 (Suroeste)",
        "COMP | Robinson3 H7 (Sur)", "COMP | Robinson3 H8 (Sureste)",
        "COMP | Robinson3 Suma (8 dirs)", "COMP | Robinson3 OR (8 dirs)",
        "COMP | Robinson5 H1 (Este)", "COMP | Robinson5 H2 (Noreste)",
        "COMP | Robinson5 H3 (Norte)", "COMP | Robinson5 H4 (Noroeste)",
        "COMP | Robinson5 H5 (Oeste)", "COMP | Robinson5 H6 (Suroeste)",
        "COMP | Robinson5 H7 (Sur)", "COMP | Robinson5 H8 (Sureste)",
        "COMP | Robinson5 Suma (8 dirs)", "COMP | Robinson5 OR (8 dirs)",
        "LoG | Gauss 7x7", "LoG | Gauss 9x9"
    };

    public ConvolucionVista() {
        super("Practica 8 - Convolucion y Filtros");
        setSize(1300, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        comboFiltros = new JComboBox<>(FILTROS);
        comboFiltros.setPreferredSize(new Dimension(310, 28));
        top.add(btnCargar);
        top.add(new JLabel("Filtro:"));
        top.add(comboFiltros);
        top.add(chkBinarizar);
        top.add(lblUmbral);
        spnUmbral.setPreferredSize(new Dimension(70, 28));
        top.add(spnUmbral);
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

        lblUmbral.setVisible(false);
        spnUmbral.setVisible(false);
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

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * VISTA — Interfaz gráfica de la aplicación de convolución.
 * Organiza los filtros en categorías por grupo (pasa bajas, pasa altas, compás, LoG).
 */
public class ConvolucionView extends JFrame {

    JButton btnCargar  = new JButton("Cargar Imagen");
    JButton btnAplicar = new JButton("Aplicar Filtro");

    JComboBox<String> comboFiltros;

    JCheckBox chkBinarizar = new JCheckBox("Binarizar");

    JSpinner spnUmbral = new JSpinner(
    new SpinnerNumberModel(128, 0, 255, 1)
    );

    JLabel lblUmbral = new JLabel("Umbral:");

    JLabel lblOriginal      = new JLabel();
    JLabel lblResultado     = new JLabel();
    JLabel lblHistOriginal  = new JLabel();
    JLabel lblHistResultado = new JLabel();


    JTextArea txtInfo = new JTextArea();

    // -------------------------------------------------------
    // Todos los nombres de filtro disponibles.
    // El orden es el mismo que usa el Controller para el switch.
    // -------------------------------------------------------
    static final String[] FILTROS = {
        // --- Conversiones básicas ---
        "Negativo",
        "Binarización (umbral 128)",

        // --- Pasa Bajas — Suavizado / Aberración ---
        "PB | Promedio 3x3",
        "PB | Promedio 5x5",
        "PB | Promedio 7x7",
        "PB | Gaussiano 3x3",
        "PB | Gaussiano 5x5",

        // --- Pasa Bajas — Definición / Encrespamiento ---
        "PB | Definición Suave",
        "PB | Definición Media",
        "PB | Definición Fuerte",

        // --- Pasa Altas — Laplaciano ---
        "PA | Laplaciano 4 vecinos",
        "PA | Laplaciano 8 vecinos",

        // --- Pasa Altas — Sobel (Hr + Hc combinados) ---
        "PA | Sobel Hr",
        "PA | Sobel Hc",
        "PA | Sobel Combinado (Hr+Hc)",

        // --- Pasa Altas — Prewitt (Hr + Hc) ---
        "PA | Prewitt Hr",
        "PA | Prewitt Hc",
        "PA | Prewitt Combinado (Hr+Hc)",

        // --- Pasa Altas — Roberts ---
        "PA | Roberts Hr",
        "PA | Roberts Hc",
        "PA | Roberts Combinado (Hr+Hc)",

        // --- Pasa Altas — Frei-Chen ---
        "PA | Frei-Chen Hr",
        "PA | Frei-Chen Hc",
        "PA | Frei-Chen Combinado (Hr+Hc)",
        
        // --- Gradiente Compás — Prewitt (8 dirs) ---
        "COMP | Prewitt H1 (Este)",
        "COMP | Prewitt H2 (Noreste)",
        "COMP | Prewitt H3 (Norte)",
        "COMP | Prewitt H4 (Noroeste)",
        "COMP | Prewitt H5 (Oeste)",
        "COMP | Prewitt H6 (Suroeste)",
        "COMP | Prewitt H7 (Sur)",
        "COMP | Prewitt H8 (Sureste)",
        "COMP | Prewitt Suma (8 dirs)",
        "COMP | Prewitt OR (8 dirs)",

        // --- Gradiente Compás — Kirsch (8 dirs) ---
        "COMP | Kirsch H1 (Este)",
        "COMP | Kirsch H2 (Noreste)",
        "COMP | Kirsch H3 (Norte)",
        "COMP | Kirsch H4 (Noroeste)",
        "COMP | Kirsch H5 (Oeste)",
        "COMP | Kirsch H6 (Suroeste)",
        "COMP | Kirsch H7 (Sur)",
        "COMP | Kirsch H8 (Sureste)",
        "COMP | Kirsch Suma (8 dirs)",
        "COMP | Kirsch OR (8 dirs)",

        // --- Gradiente Compás — Robinson 3-nivel (8 dirs) ---
        "COMP | Robinson3 H1 (Este)",
        "COMP | Robinson3 H2 (Noreste)",
        "COMP | Robinson3 H3 (Norte)",
        "COMP | Robinson3 H4 (Noroeste)",
        "COMP | Robinson3 H5 (Oeste)",
        "COMP | Robinson3 H6 (Suroeste)",
        "COMP | Robinson3 H7 (Sur)",
        "COMP | Robinson3 H8 (Sureste)",
        "COMP | Robinson3 Suma (8 dirs)",
        "COMP | Robinson3 OR (8 dirs)",

        // --- Gradiente Compás — Robinson 5-nivel (8 dirs) ---
        "COMP | Robinson5 H1 (Este)",
        "COMP | Robinson5 H2 (Noreste)",
        "COMP | Robinson5 H3 (Norte)",
        "COMP | Robinson5 H4 (Noroeste)",
        "COMP | Robinson5 H5 (Oeste)",
        "COMP | Robinson5 H6 (Suroeste)",
        "COMP | Robinson5 H7 (Sur)",
        "COMP | Robinson5 H8 (Sureste)",
        "COMP | Robinson5 Suma (8 dirs)",
        "COMP | Robinson5 OR (8 dirs)",

        // --- Derivada de 2do Orden — LoG (Laplaciano de Gaussiano) ---
        "LoG | Gauss 7x7",
        "LoG | Gauss 9x9"
    };

    // -------------------------------------------------------

    public ConvolucionView() {
        super("Convolución y Filtros de Imagen");
        setSize(1300, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));

        // ---- BARRA SUPERIOR ----
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

        // ---- PANEL CENTRAL (4 cuadrantes) ----
        JPanel centro = new JPanel(new GridLayout(2, 2, 4, 4));
        centro.add(crearPanel("Imagen Original",      lblOriginal));
        centro.add(crearPanel("Histograma Original",  lblHistOriginal));
        centro.add(crearPanel("Resultado",            lblResultado));
        centro.add(crearPanel("Histograma Resultado", lblHistResultado));
        add(centro, BorderLayout.CENTER);

        // ---- ÁREA DE INFORMACIÓN ----
        txtInfo.setRows(4);
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(new JScrollPane(txtInfo), BorderLayout.SOUTH);

        lblUmbral.setVisible(false);
        spnUmbral.setVisible(false);

        comboFiltros.addActionListener(e -> {
            String filtro = (String) comboFiltros.getSelectedItem();

            boolean mostrar =
                    filtro.contains("Sobel")
                 || filtro.contains("Prewitt")
                 || filtro.contains("Roberts")
                 || filtro.contains("Frei-Chen")
                 || filtro.contains("Laplaciano")
                 || filtro.contains("Kirsch")
                 || filtro.contains("Robinson")
                 || filtro.contains("LoG");

            lblUmbral.setVisible(mostrar);
            spnUmbral.setVisible(mostrar);
        });

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

    /** Escala la imagen al tamaño del JLabel y la muestra. */
    public void mostrarImagen(JLabel lbl, BufferedImage img) {
        int w = Math.max(lbl.getWidth(),  380);
        int h = Math.max(lbl.getHeight(), 260);
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        lbl.setIcon(new ImageIcon(scaled));
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
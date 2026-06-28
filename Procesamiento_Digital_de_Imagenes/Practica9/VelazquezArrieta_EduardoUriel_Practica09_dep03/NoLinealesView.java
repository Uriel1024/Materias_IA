import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NoLinealesView extends JFrame {

    JButton btnCargar = new JButton("Cargar Imagen");
    JButton btnAplicar = new JButton("Aplicar Filtro");

    JComboBox<String> comboFiltros;
    
    // Controles para variables dinámicas
    JLabel lblMask = new JLabel("Máscara:");
    JSpinner spnMask = new JSpinner(new SpinnerNumberModel(3, 3, 11, 2)); // 3x3, 5x5...
    
    JLabel lblParamP = new JLabel("P (Alfa/C.Armónico):");
    JSpinner spnParamP = new JSpinner(new SpinnerNumberModel(2.0, -10.0, 10.0, 0.5));

    JLabel lblOriginal = new JLabel();
    JLabel lblResultado = new JLabel();
    JLabel lblHistOriginal = new JLabel();
    JLabel lblHistResultado = new JLabel();

    JTextArea txtInfo = new JTextArea();

    static final String[] FILTROS = {
            "Filtro de la Mediana",
            "Filtro Alfa-Trimmed",
            "Filtro del Máximo",
            "Filtro del Mínimo",
            "Filtro del Punto Medio",
            "Filtro Inferior Armónico",
            "Filtro Contra Armónico",
            "Filtro Geométrico",
            "Filtro Máximo-Mínimo",
            "Filtro Media Aritmética"
    };

    public NoLinealesView() {
        super("Filtros No Lineales de Imagen");
        setSize(1300, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));

        // ---- BARRA SUPERIOR ----
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

        // ---- PANEL CENTRAL ----
        JPanel centro = new JPanel(new GridLayout(2, 2, 4, 4));
        centro.add(crearPanel("Imagen Original", lblOriginal));
        centro.add(crearPanel("Histograma Original", lblHistOriginal));
        centro.add(crearPanel("Resultado", lblResultado));
        centro.add(crearPanel("Histograma Resultado", lblHistResultado));
        add(centro, BorderLayout.CENTER);

        // ---- INFO ----
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

    public void mostrarImagen(JLabel lbl, BufferedImage img) {
        int w = Math.max(lbl.getWidth(), 380);
        int h = Math.max(lbl.getHeight(), 260);
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        lbl.setIcon(new ImageIcon(scaled));
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
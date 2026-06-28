import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MorfologiaGrisView extends JFrame {

    JButton btnCargar = new JButton("Cargar Imagen");
    JButton btnAplicar = new JButton("Aplicar");
    JButton btnMandarAResultado = new JButton("Usar Resultado como Original");

    JComboBox<String> comboOperaciones;

    JLabel lblOriginal = new JLabel();
    JLabel lblResultado = new JLabel();

    JTextArea txtInfo = new JTextArea();
    public BufferedImage imagenResultadoActual = null;

    static final String[] OPERACIONES = {
            "Añadir Ruido Sal",
            "Añadir Ruido Pimienta",
            "Erosión Gris",
            "Dilatación Gris",
            "Apertura Gris",
            "Clausura Gris"
    };

    public MorfologiaGrisView() {
        super("Morfología Matemática en Niveles de Gris (Teoría de Lattices)");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));

        // ---- BARRA SUPERIOR ----
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        comboOperaciones = new JComboBox<>(OPERACIONES);
        
        top.add(btnCargar);
        top.add(new JLabel("Operación:"));
        top.add(comboOperaciones);
        top.add(btnAplicar);
        top.add(btnMandarAResultado);
        
        add(top, BorderLayout.NORTH);

        // ---- PANEL CENTRAL ----
        JPanel centro = new JPanel(new GridLayout(1, 2, 4, 4));
        centro.add(crearPanel("Imagen Original (Niveles de Gris)", lblOriginal));
        centro.add(crearPanel("Imagen Resultado", lblResultado));
        add(centro, BorderLayout.CENTER);

        // ---- CUADRO DE TEXTO INFORMATIVO ----
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
        int w = Math.max(lbl.getWidth(), 450);
        int h = Math.max(lbl.getHeight(), 450);
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        lbl.setIcon(new ImageIcon(scaled));
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
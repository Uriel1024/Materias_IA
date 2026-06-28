import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    public JButton btnLoad = new JButton("Cargar Imagen");
    public JCheckBox chkInvert = new JCheckBox("Invertir");
    
    // Dos etiquetas para mostrar el "Antes" y "Después"
    public JLabel originalCanvas = new JLabel();
    public JLabel processedCanvas = new JLabel();
    
    public JComboBox<String> opciones = new JComboBox<>(new String[]{
        "1 Umbral ", "2 Umbrales", "3 Umbrales", "4 Umbrales"
    });

    public JButton aplicar = new JButton("Aplicar");

    public View() {
        super("Procesamiento YIQ - Antes y Después");
        setLayout(new BorderLayout());

        // Panel superior de controles
        JPanel top = new JPanel();
        top.add(btnLoad);
        top.add(opciones);
        top.add(chkInvert);
        top.add(aplicar);
        add(top, BorderLayout.NORTH);

        // Panel central con división para dos imágenes
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 fila, 2 columnas
        
        // Contenedor para imagen Original
        JPanel pnlOriginal = new JPanel(new BorderLayout());
        pnlOriginal.setBorder(BorderFactory.createTitledBorder("Original"));
        pnlOriginal.add(new JScrollPane(originalCanvas), BorderLayout.CENTER);

        // Contenedor para imagen Procesada
        JPanel pnlProcessed = new JPanel(new BorderLayout());
        pnlProcessed.setBorder(BorderFactory.createTitledBorder("Procesada (Binarizada)"));
        pnlProcessed.add(new JScrollPane(processedCanvas), BorderLayout.CENTER);

        centerPanel.add(pnlOriginal);
        centerPanel.add(pnlProcessed);

        add(centerPanel, BorderLayout.CENTER);
        
        setSize(1000, 600); // Un poco más ancho para las dos imágenes
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
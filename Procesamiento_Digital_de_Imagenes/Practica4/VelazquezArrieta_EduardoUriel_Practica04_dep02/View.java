import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    public JButton btnLoad = new JButton("Cargar Imagen");
    public JCheckBox chkInvert = new JCheckBox("Invertir");
    public JLabel canvas = new JLabel();
    // Cambiamos estas variables a públicas para que el Controller las vea
    public JComboBox<String> opciones = new JComboBox<>(new String[]{
        "1 Umbral (127)", "2 Umbrales", "3 Umbrales", "4 Umbrales"
    });
    public JButton aplicar = new JButton("Aplicar");
    public View() {
        super("Procesamiento YIQ - Binarización");
        setLayout(new BorderLayout());
        


        JPanel panelButtons = new JPanel(new GridLayout(2, 3));
        panelButtons.add(aplicar);
        panelButtons.add(btnLoad);
        panelButtons.add(opciones);
        panelButtons.add(chkInvert);

        add(panelButtons, BorderLayout.NORTH);
        add(new JScrollPane(canvas), BorderLayout.CENTER);
        
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    public JButton btnLoad1 = new JButton("Cargar Img 1");
    public JButton btnLoad2 = new JButton("Cargar Img 2");

    public JComboBox<String> ops = new JComboBox<>(new String[]{
    "Suma", "Resta", "Multi", "División", 
    "AND", "OR", "XOR", "NOT", 
    "<", "<=", ">", ">=", "==", "!=", 
    "Rotar 45°", "Escalar 2x"
});
    public JButton btnAplicar = new JButton("Ejecutar");
    public JLabel lbl1 = new JLabel(), lbl2 = new JLabel(), lblRes = new JLabel();

    public View() {
        super("Procesador de Imágenes Avanzado");
        setLayout(new BorderLayout());
        JPanel top = new JPanel();
        top.add(btnLoad1); top.add(btnLoad2); top.add(ops); top.add(btnAplicar);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 5, 5));
        center.add(crearScroll(lbl1, "Imagen 1"));
        center.add(crearScroll(lbl2, "Imagen 2"));
        center.add(crearScroll(lblRes, "Resultado"));
        add(center, BorderLayout.CENTER);

        setSize(1200, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JScrollPane crearScroll(JLabel lbl, String title) {
        JScrollPane sp = new JScrollPane(lbl);
        sp.setBorder(BorderFactory.createTitledBorder(title));
        return sp;
    }
}
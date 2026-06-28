import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ModeloHisto1 m = new ModeloHisto1();
            VistaHisto1 v = new VistaHisto1();
            new ControladorHisto1(m, v);
            v.setVisible(true);
        });
    }
}
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ModeloHisto2 m = new ModeloHisto2();
            VistaHisto2 v = new VistaHisto2();
            new ControladorHisto2(m, v);
            v.setVisible(true);
        });
    }
}
import javax.swing.SwingUtilities;

public class MainHisto {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ModeloHisto m = new ModeloHisto();
            VistaHisto v = new VistaHisto();
            new ControladorHisto(m, v);
            v.setVisible(true);
        });
    }
}
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConvolucionModel      model      = new ConvolucionModel();
            ConvolucionView       view       = new ConvolucionView();
            new ConvolucionController(model, view);
            view.setVisible(true);
        });
    }
}
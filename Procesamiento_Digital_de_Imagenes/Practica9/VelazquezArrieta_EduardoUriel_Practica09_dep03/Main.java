import javax.swing.SwingUtilities;

public class MainNoLineales {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NoLinealesModel model = new NoLinealesModel();
            NoLinealesView view = new NoLinealesView();
            new NoLinealesController(model, view);
            view.setVisible(true);
        });
    }
}
import javax.swing.SwingUtilities;

public class MainMorfologiaGris {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MorfologiaGrisModel model = new MorfologiaGrisModel();
            MorfologiaGrisView view = new MorfologiaGrisView();
            new MorfologiaGrisController(model, view);
            view.setVisible(true);
        });
    }
}
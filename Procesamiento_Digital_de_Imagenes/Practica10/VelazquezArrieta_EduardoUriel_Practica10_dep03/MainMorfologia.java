import javax.swing.SwingUtilities;

public class MainMorfologia {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MorfologiaModel model = new MorfologiaModel();
            MorfologiaView view = new MorfologiaView();
            new MorfologiaController(model, view);
            view.setVisible(true);
        });
    }
}
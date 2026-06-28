import controlador.MenuPrincipalControlador;
import vista.MenuPrincipalVista;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuPrincipalVista menu = new MenuPrincipalVista();
            new MenuPrincipalControlador(menu);
            menu.setVisible(true);
        });
    }
}

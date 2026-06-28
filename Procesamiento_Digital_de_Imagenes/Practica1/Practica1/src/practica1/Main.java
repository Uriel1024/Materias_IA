package practica1;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImagenModelo modelo = new ImagenModelo();
            Interfaz vista = new Interfaz();
            new ImagenControlador(modelo, vista);
            vista.setVisible(true);
        });
    }
}
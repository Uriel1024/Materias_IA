package vista;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipalVista extends JFrame {

    public JButton[] botones = new JButton[13];
    public JButton btnSalir = new JButton("Salir");

    public MenuPrincipalVista() {
        super("Procesamiento Digital de Imagenes - Menu Principal (13 Practicas)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Procesamiento Digital de Imagenes", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        JLabel subtitulo = new JLabel("Selecciona la practica a ejecutar", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel pnlTitulo = new JPanel(new GridLayout(2, 1));
        pnlTitulo.add(titulo);
        pnlTitulo.add(subtitulo);
        pnlTitulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(pnlTitulo, BorderLayout.NORTH);

        String[] etiquetas = {
            "1. Brillo y Contraste",
            "2. Histogramas (RGB / YIQ / HSV / HSI)",
            "3. Espacios de Color (CMY, HSV, HSI, YIQ, L\u03b1\u03b2)",
            "4. Umbrales en YIQ (1 a 4 niveles)",
            "5. Aritmetica, Logica y Geometria",
            "6. Operaciones con Histograma",
            "7. Ecualizacion con PDF Operador",
            "8. Convolucion y Filtros",
            "9. Filtros No Lineales",
            "10. Morfologia Binaria y Esqueletizado",
            "11. Morfologia en Niveles de Gris",
            "12. Transformada de Fourier",
            "13. Caso 1 - Ruido Interactivo"
        };

        JPanel pnlBotones = new JPanel(new GridLayout(14, 1, 5, 5));
        pnlBotones.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        for (int i = 0; i < 13; i++) {
            botones[i] = new JButton(etiquetas[i]);
            botones[i].setFont(new Font("Arial", Font.PLAIN, 13));
            botones[i].setHorizontalAlignment(SwingConstants.LEFT);
            pnlBotones.add(botones[i]);
        }
        pnlBotones.add(btnSalir);
        add(pnlBotones, BorderLayout.CENTER);

        setSize(700, 650);
        setLocationRelativeTo(null);
    }
}

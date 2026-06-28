import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class ColorView extends JFrame {

    private ColorController controller;
    private JLabel lblOriginal, lblResultado;
    private BufferedImage imagenActual;

    public ColorView() {
        controller = new ColorController();
        setTitle("Procesador de Color MVC");
        setSize(1000,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JButton cargar = new JButton("Cargar Imagen");

        JComboBox<String> opciones = new JComboBox<>(new String[]{
            "Canal R","Canal G","Canal B",
            "RGB a CMY","CMY a RGB",
            "RGB a HSV","HSV a RGB",
            "RGB a HSI","HSI a RGB",
            "RGB a YIQ","YIQ a RGB",
            "Convertir a lαβ","LAB a RGB"
        });

        JButton aplicar = new JButton("Aplicar");

        top.add(cargar);
        top.add(opciones);
        top.add(aplicar);

        add(top,BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1,2));
        lblOriginal = new JLabel("Original",SwingConstants.CENTER);
        lblResultado = new JLabel("Resultado",SwingConstants.CENTER);

        center.add(new JScrollPane(lblOriginal));
        center.add(new JScrollPane(lblResultado));

        add(center,BorderLayout.CENTER);

        cargar.addActionListener(e -> {
            try {
                JFileChooser fc = new JFileChooser();
                if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
                    imagenActual = ImageIO.read(fc.getSelectedFile());
                    lblOriginal.setIcon(new ImageIcon(imagenActual));
                }
            } catch(Exception ex){
                JOptionPane.showMessageDialog(this,"Error");
            }
        });

        aplicar.addActionListener(e -> {
            String op = (String) opciones.getSelectedItem();
            BufferedImage res = controller.procesarImagen(imagenActual, op);
            if(res!=null) lblResultado.setIcon(new ImageIcon(res));
        });
    }

    public void ejecutar(){
        setVisible(true);
    }
}
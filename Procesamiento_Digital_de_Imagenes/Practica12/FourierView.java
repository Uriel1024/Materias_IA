import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FourierView extends JFrame {

    JButton btnCargar = new JButton("Cargar Imagen");
    JButton btnTrans = new JButton("Transformada de Fourier");
    JButton btnFiltro = new JButton("Aplicar Filtro");
    JButton btnTransInv = new JButton("Transformada Inversa");
    public JSpinner filtro; 
   

    JLabel lblOriginal = new JLabel();
    JLabel lblResultado = new JLabel();
    JLabel lblEspectro = new JLabel();

    JTextArea txtInfo = new JTextArea();
    public BufferedImage imagenResultadoActual = null;


    public FourierView() {
        super("Transformada de Fourier aplicada a imanges.");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));
        filtro = new JSpinner(new SpinnerNumberModel(50.0, 1.0, 100.0, 1.0));

        // ---- BARRA SUPERIOR ----
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        
        top.add(btnCargar);
        top.add(btnTrans);
        top.add(btnFiltro);
        top.add(btnTransInv);
        top.add(filtro);


        add(top, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 3, 4, 4));
        centro.add(crearPanel("Imagen Original (Niveles de Gris)", lblOriginal));
        centro.add(crearPanel("Espectro", lblEspectro));
        centro.add(crearPanel("Imagen Resultado",lblResultado));
        
        add(centro, BorderLayout.CENTER);

    }

    private JPanel crearPanel(String titulo, JLabel lbl) {
        JPanel p = new JPanel(new BorderLayout());
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        p.add(lbl);
        return p;
    }

    public void mostrarImagen(JLabel lbl, BufferedImage img) {
        int w = Math.max(lbl.getWidth(), 450);
        int h = Math.max(lbl.getHeight(), 450);
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        lbl.setIcon(new ImageIcon(scaled));
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
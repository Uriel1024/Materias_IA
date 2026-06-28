package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class FourierVista extends JFrame {

    public JButton btnCargar = new JButton("Cargar Imagen");
    public JButton btnTrans = new JButton("Transformada de Fourier");
    public JButton btnFiltro = new JButton("Aplicar Filtro");
    public JButton btnTransInv = new JButton("Transformada Inversa");
    public JSpinner filtro = new JSpinner(new SpinnerNumberModel(50.0, 1.0, 100.0, 1.0));
    public JLabel lblOriginal = new JLabel();
    public JLabel lblResultado = new JLabel();
    public JLabel lblEspectro = new JLabel();
    public JTextArea txtInfo = new JTextArea();
    public BufferedImage imagenResultadoActual = null;

    public FourierVista() {
        super("Practica 12 - Transformada de Fourier");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));

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
        centro.add(crearPanel("Imagen Resultado", lblResultado));
        add(centro, BorderLayout.CENTER);

        txtInfo.setRows(3);
        txtInfo.setEditable(false);
        add(new JScrollPane(txtInfo), BorderLayout.SOUTH);
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

    public void mostrarOriginal(BufferedImage img) { ImageUtils.displayOn(lblOriginal, img, 350, 350); }
    public void mostrarResultado(BufferedImage img) { ImageUtils.displayOn(lblResultado, img, 350, 350); }
    public void mostrarEspectro(BufferedImage img) { ImageUtils.displayOn(lblEspectro, img, 350, 350); }
}

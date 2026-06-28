package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class UmbralesVista extends JFrame {

    public JButton btnCargar = new JButton("Cargar Imagen");
    public JButton btnAplicar = new JButton("Aplicar");
    public JCheckBox chkInvertir = new JCheckBox("Invertir");
    public JComboBox<String> opciones = new JComboBox<>(new String[] {
        "1 Umbral", "2 Umbrales", "3 Umbrales", "4 Umbrales"
    });
    public JLabel lblOriginal = new JLabel();
    public JLabel lblProcesada = new JLabel();

    public UmbralesVista() {
        super("Practica 4 - Umbrales en YIQ");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel top = new JPanel();
        top.add(btnCargar);
        top.add(opciones);
        top.add(chkInvertir);
        top.add(btnAplicar);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 0));
        JPanel pnlOriginal = new JPanel(new BorderLayout());
        pnlOriginal.setBorder(BorderFactory.createTitledBorder("Original"));
        pnlOriginal.add(new JScrollPane(lblOriginal), BorderLayout.CENTER);
        JPanel pnlProc = new JPanel(new BorderLayout());
        pnlProc.setBorder(BorderFactory.createTitledBorder("Procesada (Binarizada)"));
        pnlProc.add(new JScrollPane(lblProcesada), BorderLayout.CENTER);
        center.add(pnlOriginal);
        center.add(pnlProc);
        add(center, BorderLayout.CENTER);

        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    public void mostrarOriginal(BufferedImage img) { ImageUtils.displayOn(lblOriginal, img, 500, 500); }
    public void mostrarProcesada(BufferedImage img) { ImageUtils.displayOn(lblProcesada, img, 500, 500); }
}

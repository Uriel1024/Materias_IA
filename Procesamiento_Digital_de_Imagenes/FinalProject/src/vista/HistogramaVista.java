package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class HistogramaVista extends JFrame {

    public JButton btnCargar = new JButton("Cargar Imagen");
    public JComboBox<String> comboMetodo = new JComboBox<>(new String[] {"Original (RGB)", "YIQ", "HSV", "HSI"});
    public JLabel lblOriginal = new JLabel("Imagen Original", SwingConstants.CENTER);
    public JLabel lblProcesada = new JLabel("Imagen Procesada", SwingConstants.CENTER);
    public JTextArea txtStats = new JTextArea(3, 30);
    public PanelHistograma panelR, panelG, panelB;

    public HistogramaVista() {
        super("Practica 2 - Analisis de Histogramas (RGB / YIQ / HSV / HSI)");
        setSize(1200, 800);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.add(btnCargar);
        top.add(new JLabel("Espacio de Color:"));
        top.add(comboMetodo);
        add(top, BorderLayout.NORTH);

        JPanel pImagenes = new JPanel(new GridLayout(1, 2, 10, 10));
        pImagenes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pImagenes.add(lblOriginal);
        pImagenes.add(lblProcesada);
        add(pImagenes, BorderLayout.CENTER);

        panelR = new PanelHistograma("Canal R / Intensidad");
        panelG = new PanelHistograma("Canal G / Intensidad");
        panelB = new PanelHistograma("Canal B / Intensidad");
        JPanel pHistogramas = new JPanel(new GridLayout(1, 3, 5, 5));
        pHistogramas.setPreferredSize(new Dimension(1100, 220));
        pHistogramas.add(panelR);
        pHistogramas.add(panelG);
        pHistogramas.add(panelB);

        txtStats.setEditable(false);
        txtStats.setFont(new Font("Monospaced", Font.BOLD, 12));
        JPanel pInferior = new JPanel(new BorderLayout());
        pInferior.add(pHistogramas, BorderLayout.CENTER);
        pInferior.add(new JScrollPane(txtStats), BorderLayout.SOUTH);
        add(pInferior, BorderLayout.SOUTH);
    }

    public void mostrarOriginal(BufferedImage img) { ImageUtils.displayOn(lblOriginal, img, 450, 450); }
    public void mostrarProcesada(BufferedImage img) { ImageUtils.displayOn(lblProcesada, img, 450, 450); }

    public class PanelHistograma extends JPanel {
        private int[] datos;
        private Color colorBarra = Color.GRAY;

        public PanelHistograma(String titulo) {
            setBorder(BorderFactory.createTitledBorder(titulo));
            setBackground(Color.WHITE);
        }

        public void setDatos(int[] d, Color c) {
            this.datos = d;
            this.colorBarra = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (datos == null) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            int padding = 30;
            int max = 0;
            for (int v : datos) if (v > max) max = v;
            if (max == 0) max = 1;
            double anchoBarra = (double) (width - 2 * padding) / 256.0;
            g2.setColor(colorBarra);
            for (int i = 0; i < 256; i++) {
                int hBarra = (int) (((double) datos[i] / max) * (height - 2 * padding));
                int x = padding + (int) (i * anchoBarra);
                int y = height - padding - hBarra;
                g2.drawLine(x, height - padding, x, y);
            }
            g2.setColor(Color.BLACK);
            g2.drawLine(padding, height - padding, width - padding, height - padding);
        }
    }
}

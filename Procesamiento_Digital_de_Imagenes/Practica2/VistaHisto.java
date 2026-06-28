import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VistaHisto extends JFrame {
    public JButton btnCargar;
    public JComboBox<String> comboMetodo;
    public JLabel lblOriginal, lblProcesada;
    public JTextArea txtStats;
    
    // Paneles para los canales R, G y B (o Intensidad)
    public PanelHistograma panelR, panelG, panelB;

    public VistaHisto() {
        setTitle("Analizador de Histogramas (RGB / YIQ / HSV / HSI)");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel Superior: Controles ---
        btnCargar = new JButton("Cargar Imagen");
        comboMetodo = new JComboBox<>(new String[]{"Original (RGB)", "YIQ", "HSV", "HSI"});
        JPanel top = new JPanel();
        top.add(btnCargar);
        top.add(new JLabel("Espacio de Color:"));
        top.add(comboMetodo);
        add(top, BorderLayout.NORTH);

        // --- Panel Central: Imágenes ---
        lblOriginal = new JLabel("Imagen Original", SwingConstants.CENTER);
        lblProcesada = new JLabel("Imagen Procesada", SwingConstants.CENTER);
        lblOriginal.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblProcesada.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JPanel pImagenes = new JPanel(new GridLayout(1, 2, 10, 10));
        pImagenes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pImagenes.add(lblOriginal);
        pImagenes.add(lblProcesada);
        add(pImagenes, BorderLayout.CENTER);

        // --- Panel Inferior: Histogramas y Estadísticas ---
        panelR = new PanelHistograma("Canal R / Intensidad");
        panelG = new PanelHistograma("Canal G / Intensidad");
        panelB = new PanelHistograma("Canal B / Intensidad");

        JPanel pHistogramas = new JPanel(new GridLayout(1, 3, 5, 5));
        pHistogramas.setPreferredSize(new Dimension(1100, 220));
        pHistogramas.add(panelR);
        pHistogramas.add(panelG);
        pHistogramas.add(panelB);

        txtStats = new JTextArea(3, 30);
        txtStats.setEditable(false);
        txtStats.setFont(new Font("Monospaced", Font.BOLD, 12));

        JPanel pInferior = new JPanel(new BorderLayout());
        pInferior.add(pHistogramas, BorderLayout.CENTER);
        pInferior.add(new JScrollPane(txtStats), BorderLayout.SOUTH);
        add(pInferior, BorderLayout.SOUTH);
    }

    public void mostrarImagen(BufferedImage img, JLabel label) {
        if (img == null) return;
        // Ajuste proporcional de la imagen para el contenedor
        int w = 450;
        int h = 450;
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled));
        label.setText("");
    }

    // Clase interna para dibujar cada histograma de forma independiente
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

            // Encontrar el valor máximo para normalizar la altura
            int max = 0;
            for (int v : datos) if (v > max) max = v;
            if (max == 0) max = 1;

            double anchoBarra = (double) (width - 2 * padding) / 256.0;
            g2.setColor(colorBarra);

            for (int i = 0; i < 256; i++) {
                int hBarra = (int) (((double) datos[i] / max) * (height - 2 * padding));
                int x = padding + (int) (i * anchoBarra);
                int y = height - padding - hBarra;
                
                // Dibujar línea o rectángulo por cada nivel de intensidad
                g2.drawLine(x, height - padding, x, y);
            }
            
            // Dibujar eje base
            g2.setColor(Color.BLACK);
            g2.drawLine(padding, height - padding, width - padding, height - padding);
        }
    }
}
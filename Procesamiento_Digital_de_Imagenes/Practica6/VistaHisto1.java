import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VistaHisto1 extends JFrame {
    public JButton btnCargar, btnCargarRef;
    public JComboBox<String> comboMetodo;
    public JLabel lblOriginal, lblProcesada, lblReferencia;
    public JTextArea txtStats;
    public JSlider sliderDesplazamiento, sliderContraste;
    public PanelHistograma panelR, panelG, panelB;

    public VistaHisto1() {
        setTitle("Analizador de Histogramas Avanzado");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel Superior: Controles ---
        JPanel top = new JPanel(new FlowLayout());
        btnCargar = new JButton("Cargar Imagen");
        btnCargarRef = new JButton("Cargar Referencia");
        
        comboMetodo = new JComboBox<>(new String[]{
            "Original (RGB)", "Escala de Grises", "Desplazamiento", 
            "Contraste", "Ecualización", "Correspondencia"
        });

        sliderDesplazamiento = new JSlider(-100, 100, 0);
        sliderDesplazamiento.setToolTipText("Desplazamiento");
        
        // Contraste de 0.1x a 3.0x (mapeado de 1 a 30)
        sliderContraste = new JSlider(1, 30, 10); 
        sliderContraste.setToolTipText("Contraste");

        top.add(btnCargar);
        top.add(btnCargarRef);
        top.add(new JLabel("Método:"));
        top.add(comboMetodo);
        top.add(new JLabel("Desplazamiento:"));
        top.add(sliderDesplazamiento);
        top.add(new JLabel("Contraste:"));
        top.add(sliderContraste);
        add(top, BorderLayout.NORTH);

        // --- Panel Central: Imágenes ---
        lblOriginal = new JLabel("Imagen Original", SwingConstants.CENTER);
        lblProcesada = new JLabel("Imagen Procesada", SwingConstants.CENTER);
        lblReferencia = new JLabel("Imagen Referencia", SwingConstants.CENTER);
        
        lblOriginal.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblProcesada.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblReferencia.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JPanel pImagenes = new JPanel(new GridLayout(1, 3, 10, 10));
        pImagenes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pImagenes.add(lblOriginal);
        pImagenes.add(lblProcesada);
        pImagenes.add(lblReferencia);
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

        txtStats = new JTextArea(4, 30);
        txtStats.setEditable(false);
        txtStats.setFont(new Font("Monospaced", Font.BOLD, 12));

        JPanel pInferior = new JPanel(new BorderLayout());
        pInferior.add(pHistogramas, BorderLayout.CENTER);
        pInferior.add(new JScrollPane(txtStats), BorderLayout.SOUTH);
        add(pInferior, BorderLayout.SOUTH);
    }

    public void mostrarImagen(BufferedImage img, JLabel label) {
        if (img == null) return;
        int w = 400;
        int h = 400;
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled));
        label.setText("");
    }

    // [Mantén tu clase interna PanelHistograma exactamente igual]
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
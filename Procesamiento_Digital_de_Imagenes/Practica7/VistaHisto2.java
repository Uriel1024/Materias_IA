import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VistaHisto2 extends JFrame {
    public JButton btnCargar, btnAplicar;
    public JComboBox<String> comboMetodo;
    public JLabel lblOriginal, lblProcesada;
    public JSpinner spinnerAlpha, spinnerPotencia; // Cambiado de JSlider a JSpinner
    public PanelHistograma panelHistoOri, panelHistoProc;

    public VistaHisto2() {
        setTitle("Ecualización de Histogramas - PDF Op.");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Panel de Controles ---
        JPanel pnlControl = new JPanel();
        btnCargar = new JButton("1. Cargar Imagen");
        
        comboMetodo = new JComboBox<>(new String[]{"Uniforme", "Exponencial", "Rayleigh", "Hiperbólica", "Logarítmica"});
        
        // Configuración de Spinner para Alpha (0.01 a 1.0)
        // SpinnerNumberModel(valorInicial, min, max, paso)
        spinnerAlpha = new JSpinner(new SpinnerNumberModel(0.5, 0.01, 1.0, 0.05));
        spinnerAlpha.setBorder(BorderFactory.createTitledBorder("Alpha"));

        // Configuración de Spinner para Potencia
        spinnerPotencia = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
        spinnerPotencia.setBorder(BorderFactory.createTitledBorder("Potencia (n)"));

        btnAplicar = new JButton("2. Aplicar Filtro");
        btnAplicar.setBackground(new Color(173, 216, 230)); // Un color para resaltar

        pnlControl.add(btnCargar);
        pnlControl.add(new JLabel("  Método:"));
        pnlControl.add(comboMetodo);
        pnlControl.add(spinnerAlpha);
        pnlControl.add(spinnerPotencia);
        pnlControl.add(btnAplicar);

        // --- Panel de Visualización ---
        JPanel pnlImagenes = new JPanel(new GridLayout(2, 2, 10, 10));
        lblOriginal = new JLabel("Esperando imagen...", SwingConstants.CENTER);
        lblProcesada = new JLabel("Imagen Procesada", SwingConstants.CENTER);
        
        // Bordes para identificar los espacios
        lblOriginal.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblProcesada.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        panelHistoOri = new PanelHistograma("Histograma Original");
        panelHistoProc = new PanelHistograma("Histograma Destino");

        pnlImagenes.add(lblOriginal);
        pnlImagenes.add(lblProcesada);
        pnlImagenes.add(panelHistoOri);
        pnlImagenes.add(panelHistoProc);

        add(pnlControl, BorderLayout.NORTH);
        add(pnlImagenes, BorderLayout.CENTER);
    }

    public void mostrarImagen(BufferedImage img, JLabel label) {
        if (img == null) return;
        // Ajustar tamaño para que quepa bien en el grid
        Image escalada = img.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(escalada));
        label.setText(""); // Quitar el texto cuando hay imagen
    }

    // Clase interna para dibujar el histograma (sin cambios mayores)
    public class PanelHistograma extends JPanel {
        private int[] datos;
        public PanelHistograma(String t) { setBorder(BorderFactory.createTitledBorder(t)); }
        public void setDatos(int[] d) { this.datos = d; repaint(); }
        
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (datos == null) return;
            int max = 0;
            for (int v : datos) if (v > max) max = v;
            
            g.setColor(new Color(50, 100, 200));
            for (int i = 0; i < 256; i++) {
                if (max > 0) {
                    int h = (int) (((double) datos[i] / max) * (getHeight() - 60));
                    g.drawLine(i + 40, getHeight() - 30, i + 40, getHeight() - 30 - h);
                }
            }
        }
    }
}
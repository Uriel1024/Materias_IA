package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class EcualizacionPDFVista extends JFrame {

    public JButton btnCargar = new JButton("1. Cargar Imagen");
    public JButton btnAplicar = new JButton("2. Aplicar Filtro");
    public JComboBox<String> comboMetodo = new JComboBox<>(new String[] {
        "Uniforme", "Exponencial", "Rayleigh", "Hiperbolica", "Logaritmica"
    });
    public JSpinner spinnerAlpha = new JSpinner(new SpinnerNumberModel(0.5, 0.01, 1.0, 0.05));
    public JSpinner spinnerPotencia = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
    public JLabel lblOriginal = new JLabel("Esperando imagen...", SwingConstants.CENTER);
    public JLabel lblProcesada = new JLabel("Imagen Procesada", SwingConstants.CENTER);
    public PanelHistograma panelHistoOri, panelHistoProc;

    public EcualizacionPDFVista() {
        super("Practica 7 - Ecualizacion con PDF Operador");
        setSize(1200, 800);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel pnlControl = new JPanel();
        pnlControl.add(btnCargar);
        pnlControl.add(new JLabel("  Metodo:"));
        pnlControl.add(comboMetodo);
        spinnerAlpha.setBorder(BorderFactory.createTitledBorder("Alpha"));
        spinnerPotencia.setBorder(BorderFactory.createTitledBorder("Potencia (n)"));
        pnlControl.add(spinnerAlpha);
        pnlControl.add(spinnerPotencia);
        pnlControl.add(btnAplicar);
        add(pnlControl, BorderLayout.NORTH);

        JPanel pnlImagenes = new JPanel(new GridLayout(2, 2, 10, 10));
        panelHistoOri = new PanelHistograma("Histograma Original");
        panelHistoProc = new PanelHistograma("Histograma Destino");
        pnlImagenes.add(lblOriginal);
        pnlImagenes.add(lblProcesada);
        pnlImagenes.add(panelHistoOri);
        pnlImagenes.add(panelHistoProc);
        add(pnlImagenes, BorderLayout.CENTER);
    }

    public void mostrarOriginal(BufferedImage img) { ImageUtils.displayOn(lblOriginal, img, 400, 300); }
    public void mostrarProcesada(BufferedImage img) { ImageUtils.displayOn(lblProcesada, img, 400, 300); }

    public class PanelHistograma extends JPanel {
        private int[] datos;
        public PanelHistograma(String t) { setBorder(BorderFactory.createTitledBorder(t)); }
        public void setDatos(int[] d) { this.datos = d; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
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

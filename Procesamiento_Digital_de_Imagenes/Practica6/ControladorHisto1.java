import javax.swing.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ControladorHisto1 {
    private ModeloHisto1 modelo;
    private VistaHisto1 vista;
    private BufferedImage imgOri;
    private BufferedImage imgRef; // Para la correspondencia
    private BufferedImage imgProc;

    public ControladorHisto1(ModeloHisto1 m, VistaHisto1 v) {
        this.modelo = m;
        this.vista = v;
        
        this.vista.btnCargar.addActionListener(e -> cargarPrincipal());
        this.vista.btnCargarRef.addActionListener(e -> cargarReferencia());
        this.vista.comboMetodo.addActionListener(e -> procesar());
        
        // Listeners para procesar en tiempo real al mover los sliders
        this.vista.sliderDesplazamiento.addChangeListener(e -> procesar());
        this.vista.sliderContraste.addChangeListener(e -> procesar());
    }

    private void cargarPrincipal() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                imgOri = ImageIO.read(fc.getSelectedFile());
                vista.mostrarImagen(imgOri, vista.lblOriginal);
                procesar();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void cargarReferencia() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                imgRef = ImageIO.read(fc.getSelectedFile());
                vista.mostrarImagen(imgRef, vista.lblReferencia);
                if(vista.comboMetodo.getSelectedItem().equals("Correspondencia")) {
                    procesar();
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
    private void procesar() {
        if (imgOri == null) return;
        String metodo = (String) vista.comboMetodo.getSelectedItem();

        switch (metodo) {
            case "Desplazamiento": 
                int offset = vista.sliderDesplazamiento.getValue();
                imgProc = modelo.desplazar(imgOri, offset); 
                break;
            case "Contraste": 
                double alpha = vista.sliderContraste.getValue() / 10.0;
                imgProc = modelo.modificarContraste(imgOri, alpha); 
                break;
            case "Ecualización": 
                imgProc = modelo.ecualizar(imgOri); 
                break;
            case "Correspondencia":
                if(imgRef != null) {
                    imgProc = modelo.correspondencia(imgOri, imgRef);
                } else {
                    imgProc = imgOri;
                }
                break;
            case "Escala de Grises": // AQUÍ ESTABA EL ERROR, AHORA SÍ HACE LA CONVERSIÓN
                imgProc = modelo.convertirEscalaGrises(imgOri);
                break;
            case "Original (RGB)":
            default: 
                imgProc = imgOri;
        }

        vista.mostrarImagen(imgProc, vista.lblProcesada);
        actualizarEstadisticas();
    }
    private void actualizarEstadisticas() {
        int total = imgProc.getWidth() * imgProc.getHeight();
        String metodo = (String) vista.comboMetodo.getSelectedItem();

        if (metodo.equals("Original (RGB)")) {
            int[][] hists = modelo.calcularHistogramasRGB(imgProc);
            vista.panelR.setDatos(hists[0], new Color(255, 0, 0, 200));
            vista.panelG.setDatos(hists[1], new Color(0, 200, 0, 200));
            vista.panelB.setDatos(hists[2], new Color(0, 0, 255, 200));

            double medR = modelo.media(hists[0], total);
            double medG = modelo.media(hists[1], total);
            double medB = modelo.media(hists[2], total);

            vista.txtStats.setText(
                "Estadísticas RGB (Asimetría no calculada en multi-canal):\n" +
                "Media R: " + String.format("%.2f", medR) +
                " | Media G: " + String.format("%.2f", medG) +
                " | Media B: " + String.format("%.2f", medB)
            );
        } else {
            // Asumimos comportamiento de escala de grises para los cálculos matemáticos
            int[] hist = modelo.calcularHistograma(imgProc);
            vista.panelR.setDatos(hist, Color.GRAY);
            vista.panelG.setDatos(hist, Color.GRAY);
            vista.panelB.setDatos(hist, Color.GRAY);

            double media = modelo.media(hist, total);
            double var = modelo.varianza(hist, total, media);
            double asimetria = modelo.asimetria(hist, total, media, var);

            vista.txtStats.setText(
                "Análisis del Histograma (Intensidad):\n" +
                "Media: " + String.format("%.2f", media) + "\n" +
                "Varianza (Contraste): " + String.format("%.2f", var) + "\n" +
                "Asimetría (Desplazamiento): " + String.format("%.4f", asimetria)
            );
        }
    }
}
import javax.swing.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ControladorHisto {
    private ModeloHisto modelo;
    private VistaHisto vista;
    private BufferedImage imgOri;
    private BufferedImage imgProc;

    public ControladorHisto(ModeloHisto m, VistaHisto v) {
        this.modelo = m;
        this.vista = v;
        this.vista.btnCargar.addActionListener(e -> cargar());
        this.vista.comboMetodo.addActionListener(e -> procesar());
    }

    private void cargar() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                imgOri = ImageIO.read(fc.getSelectedFile());
                vista.mostrarImagen(imgOri, vista.lblOriginal);
                procesar();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void procesar() {
        if (imgOri == null) return;
        String metodo = (String) vista.comboMetodo.getSelectedItem();

        switch (metodo) {
            case "YIQ": imgProc = modelo.convertirYIQ(imgOri); break;
            case "HSV": imgProc = modelo.convertirHSV(imgOri); break;
            case "HSI": imgProc = modelo.convertirHSI(imgOri); break;
            default: imgProc = imgOri;
        }

        vista.mostrarImagen(imgProc, vista.lblProcesada);
        int total = imgProc.getWidth() * imgProc.getHeight();

        if (metodo.equals("Original (RGB)")) {
            int[][] hists = modelo.calcularHistogramasRGB(imgProc);
            
            // Actualizar los 3 paneles por separado
            vista.panelR.setDatos(hists[0], new Color(255, 0, 0, 200));
            vista.panelG.setDatos(hists[1], new Color(0, 200, 0, 200));
            vista.panelB.setDatos(hists[2], new Color(0, 0, 255, 200));

            double medR = modelo.media(hists[0], total);
            double medG = modelo.media(hists[1], total);
            double medB = modelo.media(hists[2], total);

            vista.txtStats.setText(
                "Media R: " + String.format("%.2f", medR) +
                " | Media G: " + String.format("%.2f", medG) +
                " | Media B: " + String.format("%.2f", medB)
            );
        } else {
            int[] hist = modelo.calcularHistograma(imgProc);
            
            // En escala de grises, los tres paneles muestran lo mismo en gris
            vista.panelR.setDatos(hist, Color.GRAY);
            vista.panelG.setDatos(hist, Color.GRAY);
            vista.panelB.setDatos(hist, Color.GRAY);

            double media = modelo.media(hist, total);
            double var = modelo.varianza(hist, total, media);

            vista.txtStats.setText(
                "Media: " + String.format("%.2f", media) +
                " | Varianza: " + String.format("%.2f", var)
            );
        }
    }
}

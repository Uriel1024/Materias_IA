package controlador;

import modelo.HistoOperacionesModelo;
import vista.HistoOperacionesVista;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HistoOperacionesControlador {
    private final HistoOperacionesModelo modelo;
    private final HistoOperacionesVista vista;
    private BufferedImage imgOri;
    private BufferedImage imgRef;
    private BufferedImage imgProc;

    public HistoOperacionesControlador(HistoOperacionesModelo modelo, HistoOperacionesVista vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.btnCargar.addActionListener(e -> cargarPrincipal());
        vista.btnCargarRef.addActionListener(e -> cargarReferencia());
        vista.comboMetodo.addActionListener(e -> procesar());
        vista.sliderDesplazamiento.addChangeListener(e -> procesar());
        vista.sliderContraste.addChangeListener(e -> procesar());
    }

    private void cargarPrincipal() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                imgOri = ImageIO.read(fc.getSelectedFile());
                vista.mostrarOriginal(imgOri);
                procesar();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void cargarReferencia() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                imgRef = ImageIO.read(fc.getSelectedFile());
                vista.mostrarReferencia(imgRef);
                if (vista.comboMetodo.getSelectedItem().equals("Correspondencia")) procesar();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void procesar() {
        if (imgOri == null) return;
        String metodo = (String) vista.comboMetodo.getSelectedItem();
        switch (metodo) {
            case "Desplazamiento":
                imgProc = modelo.desplazar(imgOri, vista.sliderDesplazamiento.getValue());
                break;
            case "Contraste":
                imgProc = modelo.modificarContraste(imgOri, vista.sliderContraste.getValue() / 10.0);
                break;
            case "Ecualizacion":
                imgProc = modelo.ecualizar(imgOri);
                break;
            case "Correspondencia":
                imgProc = (imgRef != null) ? modelo.correspondencia(imgOri, imgRef) : imgOri;
                break;
            case "Escala de Grises":
                imgProc = modelo.convertirEscalaGrises(imgOri);
                break;
            case "Original (RGB)":
            default:
                imgProc = imgOri;
        }
        vista.mostrarProcesada(imgProc);
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
                "Estadisticas RGB:\n" +
                "Media R: " + String.format("%.2f", medR) +
                " | Media G: " + String.format("%.2f", medG) +
                " | Media B: " + String.format("%.2f", medB)
            );
        } else {
            int[] hist = modelo.calcularHistograma(imgProc);
            vista.panelR.setDatos(hist, Color.GRAY);
            vista.panelG.setDatos(hist, Color.GRAY);
            vista.panelB.setDatos(hist, Color.GRAY);
            double media = modelo.media(hist, total);
            double var = modelo.varianza(hist, total, media);
            double asim = modelo.asimetria(hist, total, media, var);
            vista.txtStats.setText(
                "Analisis del Histograma:\n" +
                "Media: " + String.format("%.2f", media) + "\n" +
                "Varianza: " + String.format("%.2f", var) + "\n" +
                "Asimetria: " + String.format("%.4f", asim)
            );
        }
    }
}

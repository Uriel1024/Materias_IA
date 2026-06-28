package controlador;

import modelo.RuidoInteractivoModelo;
import vista.RuidoInteractivoVista;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class RuidoInteractivoControlador {
    private final RuidoInteractivoModelo modelo;
    private final RuidoInteractivoVista vista;

    public RuidoInteractivoControlador(RuidoInteractivoModelo modelo, RuidoInteractivoVista vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.btnCargar.addActionListener(e -> cargar());
        vista.btnRestaurar.addActionListener(e -> restaurar());
        vista.sliderSalPimienta.addChangeListener(this::cambioSlider);
        vista.sliderGaussiano.addChangeListener(this::cambioSlider);
    }

    private void cargar() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(new File(fc.getSelectedFile().getAbsolutePath()));
                vista.setImagenOriginal(img);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(vista, "Error al cargar.");
            }
        }
    }

    private void restaurar() {
        if (vista.imagenOriginal != null) {
            vista.resetSliders();
            vista.mostrarImagen(vista.imagenOriginal);
        }
    }

    private void cambioSlider(ChangeEvent e) {
        if (vista.imagenOriginal == null) return;
        Object src = e.getSource();
        if (src == vista.sliderSalPimienta && vista.sliderSalPimienta.getValueIsAdjusting()) return;
        if (src == vista.sliderGaussiano && vista.sliderGaussiano.getValueIsAdjusting()) return;

        if (src == vista.sliderSalPimienta) {
            int valor = vista.sliderSalPimienta.getValue();
            vista.lblValSp.setText(valor + "%");
            if (valor > 0) vista.sliderGaussiano.setValue(0);
            double prob = valor / 500.0;
            BufferedImage r = modelo.agregarRuidoSalPimienta(vista.imagenOriginal, prob);
            vista.mostrarImagen(r);
        } else if (src == vista.sliderGaussiano) {
            int valor = vista.sliderGaussiano.getValue();
            vista.lblValGauss.setText(String.valueOf(valor));
            if (valor > 0) vista.sliderSalPimienta.setValue(0);
            BufferedImage r = modelo.agregarRuidoGaussiano(vista.imagenOriginal, valor);
            vista.mostrarImagen(r);
        }
    }
}

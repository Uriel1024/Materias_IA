package controlador;

import modelo.UmbralesModelo;
import vista.UmbralesVista;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class UmbralesControlador {
    private final UmbralesModelo modelo;
    private final UmbralesVista vista;

    public UmbralesControlador(UmbralesModelo modelo, UmbralesVista vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.btnCargar.addActionListener(e -> cargar());
        vista.btnAplicar.addActionListener(e -> aplicar());
    }

    private void cargar() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(fc.getSelectedFile());
                modelo.setOriginal(img);
                vista.mostrarOriginal(modelo.getOriginal());
                vista.mostrarProcesada(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al cargar.");
            }
        }
    }

    private void aplicar() {
        if (modelo.getOriginal() == null) {
            JOptionPane.showMessageDialog(vista, "Carga una imagen primero.");
            return;
        }
        int sel = vista.opciones.getSelectedIndex();
        int[] thresholds = UmbralesModelo.presets[Math.max(0, Math.min(3, sel))];
        modelo.aplicar(thresholds, vista.chkInvertir.isSelected());
        vista.mostrarProcesada(modelo.getProcesada());
    }
}

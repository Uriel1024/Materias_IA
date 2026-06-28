package controlador;

import modelo.EcualizacionPDFModelo;
import vista.EcualizacionPDFVista;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class EcualizacionPDFControlador {
    private final EcualizacionPDFModelo modelo;
    private final EcualizacionPDFVista vista;
    private BufferedImage imagen;

    public EcualizacionPDFControlador(EcualizacionPDFModelo modelo, EcualizacionPDFVista vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.btnCargar.addActionListener(e -> cargar());
        vista.btnAplicar.addActionListener(e -> aplicar());
    }

    private void cargar() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                imagen = ImageIO.read(fc.getSelectedFile());
                vista.mostrarOriginal(imagen);
                vista.panelHistoOri.setDatos(modelo.calcularHistograma(imagen));
                vista.lblProcesada.setIcon(null);
                vista.panelHistoProc.setDatos(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al cargar");
            }
        }
    }

    private void aplicar() {
        if (imagen == null) {
            JOptionPane.showMessageDialog(vista, "Carga una imagen primero.");
            return;
        }
        String metodo = (String) vista.comboMetodo.getSelectedItem();
        double alpha = (Double) vista.spinnerAlpha.getValue();
        int pot = (Integer) vista.spinnerPotencia.getValue();
        BufferedImage procesada = modelo.aplicarEcualizacion(imagen, metodo, alpha, pot);
        vista.mostrarProcesada(procesada);
        vista.panelHistoProc.setDatos(modelo.calcularHistograma(procesada));
    }
}

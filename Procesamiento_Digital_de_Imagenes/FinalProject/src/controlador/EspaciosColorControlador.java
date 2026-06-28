package controlador;

import modelo.EspaciosColorModelo;
import vista.EspaciosColorVista;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class EspaciosColorControlador {
    private final EspaciosColorModelo modelo;
    private final EspaciosColorVista vista;
    private BufferedImage imagen;

    public EspaciosColorControlador(EspaciosColorModelo modelo, EspaciosColorVista vista) {
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
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al cargar.");
            }
        }
    }

    private void aplicar() {
        if (imagen == null) {
            JOptionPane.showMessageDialog(vista, "Carga una imagen primero.");
            return;
        }
        String op = (String) vista.comboOpciones.getSelectedItem();
        BufferedImage res = modelo.procesar(imagen, op);
        if (res != null) vista.mostrarResultado(res);
    }
}

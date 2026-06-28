package controlador;

import modelo.AritmeticaLogicaModelo;
import vista.AritmeticaLogicaVista;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class AritmeticaLogicaControlador {
    private final AritmeticaLogicaModelo modelo;
    private final AritmeticaLogicaVista vista;

    public AritmeticaLogicaControlador(AritmeticaLogicaModelo modelo, AritmeticaLogicaVista vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.btnCargar1.addActionListener(e -> cargar(1));
        vista.btnCargar2.addActionListener(e -> cargar(2));
        vista.btnAplicar.addActionListener(e -> aplicar());
    }

    private void cargar(int num) {
        JFileChooser jf = new JFileChooser();
        if (jf.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(jf.getSelectedFile());
                if (num == 1) {
                    modelo.setImage1(img);
                    vista.mostrarImg1(modelo.getImg1());
                    if (modelo.getImg2() != null) {
                        modelo.equalizeSizes();
                        vista.mostrarImg2(modelo.getImg2());
                    }
                } else {
                    modelo.setImage2(img);
                    if (modelo.getImg1() != null) modelo.equalizeSizes();
                    vista.mostrarImg2(modelo.getImg2());
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void aplicar() {
        String op = (String) vista.ops.getSelectedItem();
        BufferedImage res = null;
        try {
            if (op.contains("Rotar")) res = modelo.rotate(45);
            else if (op.contains("Escalar")) res = modelo.scale(2.0, 2.0);
            else res = modelo.combine(op);
            vista.mostrarRes(res);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Asegurate de cargar las imagenes necesarias");
        }
    }
}

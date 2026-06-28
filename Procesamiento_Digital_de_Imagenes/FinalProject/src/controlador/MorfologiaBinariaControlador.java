package controlador;

import modelo.MorfologiaBinariaModelo;
import vista.MorfologiaBinariaVista;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class MorfologiaBinariaControlador {
    private final MorfologiaBinariaModelo modelo;
    private final MorfologiaBinariaVista vista;
    private BufferedImage imagen;

    public MorfologiaBinariaControlador(MorfologiaBinariaModelo modelo, MorfologiaBinariaVista vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.btnCargar.addActionListener(e -> cargar());
        vista.btnAplicar.addActionListener(e -> aplicar());
        vista.btnUsarResultado.addActionListener(e -> usarResultado());
    }

    private void cargar() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) != JFileChooser.APPROVE_OPTION) return;
        try {
            imagen = ImageIO.read(fc.getSelectedFile());
            imagen = modelo.convertirBinaria(imagen);
            vista.mostrarOriginal(imagen);
            vista.txtInfo.setText("Imagen cargada y binarizada: " + fc.getSelectedFile().getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void aplicar() {
        if (imagen == null) { JOptionPane.showMessageDialog(vista, "Carga una imagen primero."); return; }
        String op = (String) vista.comboOperaciones.getSelectedItem();
        BufferedImage resultado = null;
        String descripcion = "";
        switch (op) {
            case "Erosion": resultado = modelo.erosion(imagen); descripcion = "EROSION: Adelgaza blancos."; break;
            case "Dilatacion": resultado = modelo.dilatacion(imagen); descripcion = "DILATACION: Engrosa blancos."; break;
            case "Apertura": resultado = modelo.apertura(imagen); descripcion = "APERTURA: Remueve ruido SAL."; break;
            case "Clausura": resultado = modelo.clausura(imagen); descripcion = "CLAUSURA: Remueve ruido PIMIENTA."; break;
            case "Aniadir Ruido Sal": resultado = modelo.aplicarRuidoSal(imagen, 0.05); descripcion = "RUIDO SAL 5%."; break;
            case "Aniadir Ruido Pimienta": resultado = modelo.aplicarRuidoPimienta(imagen, 0.05); descripcion = "RUIDO PIMIENTA 5%."; break;
            case "Esqueletizado": resultado = modelo.esqueletizado(imagen); descripcion = "ESQUELETIZADO Zhang-Suen."; break;
        }
        if (resultado != null) {
            vista.mostrarResultado(resultado);
            vista.imagenResultadoActual = resultado;
            vista.txtInfo.setText("Operacion: " + op + "\n" + descripcion);
        }
    }

    private void usarResultado() {
        if (vista.imagenResultadoActual != null) {
            imagen = vista.imagenResultadoActual;
            vista.mostrarOriginal(imagen);
            vista.txtInfo.setText("Resultado movido al origen. Listo para la siguiente operacion.");
        } else {
            JOptionPane.showMessageDialog(vista, "No hay resultado para usar como original.");
        }
    }
}

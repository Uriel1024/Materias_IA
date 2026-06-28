package controlador;

import modelo.MorfologiaGrisModelo;
import vista.MorfologiaGrisVista;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class MorfologiaGrisControlador {
    private final MorfologiaGrisModelo modelo;
    private final MorfologiaGrisVista vista;
    private BufferedImage imagen;

    public MorfologiaGrisControlador(MorfologiaGrisModelo modelo, MorfologiaGrisVista vista) {
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
            imagen = modelo.convertirEscalaGrises(imagen);
            vista.mostrarOriginal(imagen);
            vista.txtInfo.setText("Imagen cargada y convertida a gris: " + fc.getSelectedFile().getName());
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
            case "Erosion Gris": resultado = modelo.erosionGris(imagen); descripcion = "EROSION (Infimo): MINIMO local."; break;
            case "Dilatacion Gris": resultado = modelo.dilatacionGris(imagen); descripcion = "DILATACION (Supremo): MAXIMO local."; break;
            case "Apertura Gris": resultado = modelo.aperturaGris(imagen); descripcion = "APERTURA: Erosion->Dilatacion. Quita SAL."; break;
            case "Clausura Gris": resultado = modelo.clausuraGris(imagen); descripcion = "CLAUSURA: Dilatacion->Erosion. Quita PIMIENTA."; break;
            case "Aniadir Ruido Sal": resultado = modelo.aplicarRuidoSal(imagen, 0.05); descripcion = "RUIDO SAL 5%."; break;
            case "Aniadir Ruido Pimienta": resultado = modelo.aplicarRuidoPimienta(imagen, 0.05); descripcion = "RUIDO PIMIENTA 5%."; break;
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
            vista.txtInfo.setText("Resultado movido al origen.");
        } else {
            JOptionPane.showMessageDialog(vista, "No hay resultado para mover.");
        }
    }
}

package controlador;

import modelo.Complejo;
import modelo.FourierModelo;
import vista.FourierVista;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class FourierControlador {
    private final FourierModelo modelo;
    private final FourierVista vista;
    private BufferedImage imagen;
    private Complejo[][] espectro;
    private BufferedImage resultado;

    public FourierControlador(FourierModelo modelo, FourierVista vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.btnCargar.addActionListener(e -> cargar());
        vista.btnTrans.addActionListener(e -> calcularDFT());
        vista.btnFiltro.addActionListener(e -> aplicarFiltro());
        vista.btnTransInv.addActionListener(e -> calcularIDFT());
    }

    private void cargar() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) != JFileChooser.APPROVE_OPTION) return;
        try {
            imagen = ImageIO.read(fc.getSelectedFile());
            imagen = modelo.convertirGrises(imagen);
            vista.mostrarOriginal(imagen);
            vista.txtInfo.setText("Imagen cargada y convertida a gris: " + fc.getSelectedFile().getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void calcularDFT() {
        if (imagen == null) { JOptionPane.showMessageDialog(vista, "Carga una imagen primero."); return; }
        espectro = modelo.dft2D(imagen);
        BufferedImage mag = modelo.magnitud(espectro);
        vista.mostrarEspectro(mag);
        vista.txtInfo.setText("DFT calculada.");
    }

    private void aplicarFiltro() {
        if (espectro == null) { JOptionPane.showMessageDialog(vista, "Primero calcula la DFT."); return; }
        double alpha = (Double) vista.filtro.getValue();
        modelo.filtroGaussPasaBajas(espectro, alpha);
        vista.mostrarEspectro(modelo.magnitud(espectro));
        vista.txtInfo.setText("Filtro Gaussiano Pasa-Bajas aplicado con D0=" + alpha);
    }

    private void calcularIDFT() {
        if (espectro == null) { JOptionPane.showMessageDialog(vista, "Primero calcula la DFT."); return; }
        resultado = modelo.idft2D(espectro);
        vista.imagenResultadoActual = resultado;
        vista.mostrarResultado(resultado);
        vista.txtInfo.setText("IDFT calculada.");
    }
}

import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ControladorHisto2 {
    private ModeloHisto2 modelo;
    private VistaHisto2 vista;
    private BufferedImage imgOriginal;

    public ControladorHisto2(ModeloHisto2 m, VistaHisto2 v) {
        this.modelo = m;
        this.vista = v;
        
        // Listeners
        this.vista.btnCargar.addActionListener(e -> cargarImagen());
        // El procesamiento solo ocurre cuando se presiona este botón
        this.vista.btnAplicar.addActionListener(e -> aplicarProcesamiento());
    }

    private void cargarImagen() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                imgOriginal = ImageIO.read(fc.getSelectedFile());
                vista.mostrarImagen(imgOriginal, vista.lblOriginal);
                vista.panelHistoOri.setDatos(modelo.calcularHistograma(imgOriginal));
                // Limpiamos la imagen procesada anterior al cargar una nueva
                vista.lblProcesada.setIcon(null);
                vista.panelHistoProc.setDatos(null);
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(vista, "Error al cargar la imagen");
                ex.printStackTrace(); 
            }
        }
    }

    private void aplicarProcesamiento() {
        if (imgOriginal == null) {
            JOptionPane.showMessageDialog(vista, "Primero debes cargar una imagen.");
            return;
        }

        String metodo = (String) vista.comboMetodo.getSelectedItem();
        // Obtenemos los valores de los spinners
        double alpha = (Double) vista.spinnerAlpha.getValue();
        int pot = (Integer) vista.spinnerPotencia.getValue();

        BufferedImage procesada = modelo.aplicarEcualizacion(imgOriginal, metodo, alpha, pot);
        
        // Actualizamos la vista con los resultados
        vista.mostrarImagen(procesada, vista.lblProcesada);
        vista.panelHistoProc.setDatos(modelo.calcularHistograma(procesada));
    }
}
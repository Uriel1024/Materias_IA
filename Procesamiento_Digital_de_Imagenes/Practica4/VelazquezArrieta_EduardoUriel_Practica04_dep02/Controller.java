import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

// Dentro de Controller.java
public class Controller {
    private Model model;
    private View view;

    public Controller(Model m, View v) {
        this.model = m;
        this.view = v;

        // Listener para cargar imagen
        view.btnLoad.addActionListener(e -> loadImage());

        // Listener UNIFICADO para el botón Aplicar
        view.aplicar.addActionListener(e -> {
            int seleccion = view.opciones.getSelectedIndex();
            int[] thresholds;

            switch (seleccion) {
                case 0: // 1 Umbral
                    thresholds = new int[]{127};
                    break;
                case 1: // 2 Umbrales
                    thresholds = new int[]{85, 170};
                    break;
                case 2: // 3 Umbrales
                    thresholds = new int[]{64, 128, 192};
                    break;
                case 3: // 4 Umbrales
                    thresholds = new int[]{51, 102, 153, 204};
                    break;
                default:
                    thresholds = new int[]{127};
            }
            
            process(thresholds);
        });
    }

    private void loadImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                model.setImage(ImageIO.read(chooser.getSelectedFile()));
                updateDisplay();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Error al cargar imagen");
            }
        }
    }

    private void process(int[] thresholds) {
        // Validar que haya una imagen cargada antes de procesar
        try {
            model.applyThresholds(thresholds, view.chkInvert.isSelected());
            updateDisplay();
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(view, "Primero debe cargar una imagen");
        }
    }

    private void updateDisplay() {
        if (model.getProcessedImage() != null) {
            view.canvas.setIcon(new ImageIcon(model.getProcessedImage()));
        }
    }
}
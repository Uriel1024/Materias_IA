package practica1;

import javax.swing.*;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;

public class ImagenControlador {
    private ImagenModelo modelo;
    private Interfaz vista;

    public ImagenControlador(ImagenModelo modelo, Interfaz vista) {
        this.modelo = modelo;
        this.vista = vista;

        this.vista.btnAbrir.addActionListener(e -> abrir());
        this.vista.btnrgb.addActionListener(e -> guardarRGB());
        this.vista.btngris.addActionListener(e -> guardarGris());   
        this.vista.br.addChangeListener(e -> { if(!vista.br.getValueIsAdjusting()) actualizar(); });
        this.vista.contr.addChangeListener(e -> { if(!vista.contr.getValueIsAdjusting()) actualizar(); });
    }

    private void abrir() {
        JFileChooser selector = new JFileChooser();
        if (selector.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                modelo.setImgOriginal(ImageIO.read(selector.getSelectedFile()));
                actualizar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al cargar.");
            }
        }
    }

    private void actualizar() {
        if (modelo.getImgOriginal() == null) return;
        
        int b = vista.br.getValue();
        float c = (float) vista.contr.getValue() / 100.0f;
        modelo.procesarImagen(b, c);

        vista.lvlbrillo.setText("Brillo: " + b);
        vista.lvlcontraste.setText("Contraste: " + c);

        // Redimensionamos visualmente a 300x300 píxeles
        vista.lblOriginal.setIcon(escalar(modelo.getImgOriginal()));
        vista.lblModificada.setIcon(escalar(modelo.getImgModificada()));
        vista.lblGris.setIcon(escalar(modelo.getImgGris()));
    }

    private ImageIcon escalar(java.awt.image.BufferedImage img) {
        if (img == null) return null;
        Image imgEscalada = img.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        return new ImageIcon(imgEscalada);
    }


    private void guardarGris() {
        if (modelo.getImgModificada() == null) return;
        JFileChooser selector = new JFileChooser();
        if (selector.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = selector.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".jpg")) path += ".jpg";
                ImageIO.write(modelo.getImgGris(), "jpg", new File(path.replace(".jpg", "_gris.jpg")));
                JOptionPane.showMessageDialog(vista, "¡Guardado!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al guardar.");
            }
        }
    }

    private void guardarRGB() {
        if (modelo.getImgModificada() == null) return;
        JFileChooser selector = new JFileChooser();
        if (selector.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = selector.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".jpg")) path += ".jpg";
                ImageIO.write(modelo.getImgModificada(), "jpg", new File(path));
                JOptionPane.showMessageDialog(vista, "¡Guardado!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al guardar.");
            }
        }
    }
}
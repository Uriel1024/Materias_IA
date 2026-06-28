package controlador;

import modelo.BrilloContrasteModelo;
import vista.BrilloContrasteVista;
import utils.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;

public class BrilloContrasteControlador {
    private final BrilloContrasteModelo modelo;
    private final BrilloContrasteVista vista;

    public BrilloContrasteControlador(BrilloContrasteModelo modelo, BrilloContrasteVista vista) {
        this.modelo = modelo;
        this.vista = vista;
        init();
    }

    private void init() {
        vista.btnAbrir.addActionListener(e -> abrir());
        vista.btnGuardarRGB.addActionListener(e -> guardarRGB());
        vista.btnGuardarGris.addActionListener(e -> guardarGris());
        vista.br.addChangeListener(e -> { if (!vista.br.getValueIsAdjusting()) actualizar(); });
        vista.contr.addChangeListener(e -> { if (!vista.contr.getValueIsAdjusting()) actualizar(); });
    }

    private void abrir() {
        JFileChooser sel = new JFileChooser();
        if (sel.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                modelo.setOriginal(ImageIO.read(sel.getSelectedFile()));
                actualizar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al cargar.");
            }
        }
    }

    private void actualizar() {
        if (modelo.getOriginal() == null) return;
        int b = vista.br.getValue();
        float c = vista.contr.getValue() / 100.0f;
        modelo.procesar(b, c);
        vista.lvlbrillo.setText("Brillo: " + b);
        vista.lvlcontraste.setText("Contraste: " + c);
        vista.mostrarOriginal(modelo.getOriginal());
        vista.mostrarModificada(modelo.getModificada());
        vista.mostrarGris(modelo.getGris());
    }

    private void guardarRGB() {
        if (modelo.getModificada() == null) return;
        JFileChooser sel = new JFileChooser();
        if (sel.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = sel.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".jpg")) path += ".jpg";
                ImageIO.write(modelo.getModificada(), "jpg", new File(path));
                JOptionPane.showMessageDialog(vista, "Guardado!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al guardar.");
            }
        }
    }

    private void guardarGris() {
        if (modelo.getGris() == null) return;
        JFileChooser sel = new JFileChooser();
        if (sel.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = sel.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".jpg")) path += ".jpg";
                ImageIO.write(modelo.getGris(), "jpg", new File(path.replace(".jpg", "_gris.jpg")));
                JOptionPane.showMessageDialog(vista, "Guardado!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al guardar.");
            }
        }
    }
}

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class Controller {
    private Model model;
    private View view;

    public Controller(Model m, View v) {
        this.model = m; this.view = v;
        view.btnLoad1.addActionListener(e -> load(1));
        view.btnLoad2.addActionListener(e -> load(2));
        view.btnAplicar.addActionListener(e -> aplicar());
    }


private void load(int num) {
        JFileChooser jf = new JFileChooser();
        if (jf.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(jf.getSelectedFile());
                if(num == 1) { 
                    model.setImage1(img); 
                    view.lbl1.setIcon(new ImageIcon(img)); 
                    
                    // Opcional: si ya había una imagen 2, la re-escalamos a la nueva imagen 1
                    if(model.getImg2() != null) {
                        model.equalizeSizes();
                        view.lbl2.setIcon(new ImageIcon(model.getImg2()));
                    }
                } 
                else { 
                    model.setImage2(img); 
                    
                    // Si ya hay una imagen 1 cargada, igualamos el tamaño de esta nueva imagen 2
                    if(model.getImg1() != null) {
                        model.equalizeSizes();
                    }
                    
                    // Mostramos la imagen (que ya viene redimensionada) en la vista
                    view.lbl2.setIcon(new ImageIcon(model.getImg2())); 
                }
            } catch (Exception ex) { 
                ex.printStackTrace(); 
            }
        }
    }
    
    private void aplicar() {
        String op = (String) view.ops.getSelectedItem();
        BufferedImage res = null;
        try {
            if(op.contains("Rotar")) res = model.rotate(45);
            else if(op.contains("Escalar")) res = model.scale(2.0, 2.0);
            else res = model.combine(op);
            
            view.lblRes.setIcon(new ImageIcon(res));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Asegúrate de cargar las imágenes necesarias");
        }
    }
}
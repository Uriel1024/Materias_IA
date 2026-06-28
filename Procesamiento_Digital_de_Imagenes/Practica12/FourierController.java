import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;


public class FourierController {

    private final FourierModel model;
    private final FourierView view;
    private BufferedImage imagen;
    private Complex[][] espectro;
    private BufferedImage resultado;

    public FourierController(FourierModel model, FourierView view) {
        this.model = model;
        this.view = view;
        init();
    }

    private void init() {
        view.btnCargar.addActionListener(e -> cargarImagen());
        view.btnTrans.addActionListener(e -> calcularDFT());
        view.btnFiltro.addActionListener(e -> aplicarFiltro());
        view.btnTransInv.addActionListener(e -> calcularIDFT());
    }



    private void calcularDFT() {
    if (imagen == null) {
        view.mostrarError("Carga una imagen primero");
        return;
    }

    espectro = model.dft2D(imagen);

    BufferedImage magnitud = model.magnitud(espectro);

    view.mostrarImagen(view.lblEspectro, magnitud);

    view.txtInfo.setText("DFT calculada.");
    }

    private void aplicarFiltro() {

    if (espectro == null) {
        view.mostrarError("Primero calcula la DFT.");
        return;
    }

    double alpha = (Double) view.filtro.getValue();

    model.filtroGaussPasaBajas(espectro, alpha);

    BufferedImage magnitudFiltrada =
            model.magnitud(espectro);

    view.mostrarImagen(view.lblEspectro,magnitudFiltrada);

    view.txtInfo.setText("Filtro aplicado.");
    }





    private void calcularIDFT() {

    if (espectro == null) {
        view.mostrarError("Primero calcula la DFT.");
        return;
    }

    resultado = model.idft2D(espectro);

    view.imagenResultadoActual = resultado;

    view.mostrarImagen(view.lblResultado,resultado);

    view.txtInfo.setText("IDFT calculada.");
    }   

    private void cargarImagen() {
        try {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(view) != JFileChooser.APPROVE_OPTION) return;
            imagen = ImageIO.read(fc.getSelectedFile());
            
            imagen = model.convertirGrises(imagen);

            view.mostrarImagen(view.lblOriginal, imagen);
            view.txtInfo.setText("Imagen cargada y binarizada: " + fc.getSelectedFile().getName());
        } catch (Exception ex) {
            view.mostrarError("Error al cargar la imagen: " + ex.getMessage());
        }
    }





    private void usarResultadoComoOriginal() {
        if (view.imagenResultadoActual != null) {
            imagen = view.imagenResultadoActual;
            view.mostrarImagen(view.lblOriginal, imagen);
            view.txtInfo.setText("El resultado ahora es la imagen original. Listo para la siguiente operación.");
        } else {
            view.mostrarError("No hay un resultado para usar como imagen original.");
        }
    }
}
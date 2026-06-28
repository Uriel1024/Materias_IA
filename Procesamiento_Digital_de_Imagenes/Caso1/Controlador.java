import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Controlador {

    private Modelo modelo;
    private Vista vista;

    public Controlador(Modelo modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;
    }

    public void iniciar() {
        vista.setControlador(this);
    }

    public void cargarImagen(String ruta) {
        try {
            BufferedImage img = ImageIO.read(new File(ruta));
            // Guardamos la original y mostramos la original inicialmente
            vista.setImagenOriginal(img);
        } catch (Exception e) {
            System.out.println("Error al cargar imagen: " + e.getMessage());
        }
    }

    // Método para cuando cambia el slider de Sal y Pimienta
    public void actualizarRuidoSalPimienta(BufferedImage imgOriginal, int valorSlider) {
        if (imgOriginal == null) return;
        // Convertimos el valor del slider (0-100) a probabilidad (0.0 - 0.2)
        double probabilidad = valorSlider / 500.0; 
        BufferedImage resultado = modelo.agregarRuidoSalPimienta(imgOriginal, probabilidad);
        vista.mostrarImagenProcesada(resultado);
    }

    // Método para cuando cambia el slider Gaussiano
    public void actualizarRuidoGaussiano(BufferedImage imgOriginal, int valorSlider) {
        if (imgOriginal == null) return;
        // El slider va de 0 a 100, que es un buen rango para sigma
        double sigma = (double) valorSlider;
        BufferedImage resultado = modelo.agregarRuidoGaussiano(imgOriginal, sigma);
        vista.mostrarImagenProcesada(resultado);
    }
}
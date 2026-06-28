    import java.awt.image.BufferedImage;
import java.util.Random;

public class Modelo {

    // Ruido Sal y Pimienta
    public BufferedImage agregarRuidoSalPimienta(BufferedImage imagen, double probabilidad) {
        int width = imagen.getWidth();
        int height = imagen.getHeight();
        BufferedImage salida = new BufferedImage(width, height, imagen.getType());

        Random rand = new Random();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double r = rand.nextDouble();

                if (r < probabilidad) {
                    salida.setRGB(x, y, 0x000000); // negro
                } else if (r > 1 - probabilidad) {
                    salida.setRGB(x, y, 0xFFFFFF); // blanco
                } else {
                    salida.setRGB(x, y, imagen.getRGB(x, y));
                }
            }
        }
        return salida;
    }

    // Ruido Gaussiano
    public BufferedImage agregarRuidoGaussiano(BufferedImage imagen, double sigma) {
        int width = imagen.getWidth();
        int height = imagen.getHeight();
        BufferedImage salida = new BufferedImage(width, height, imagen.getType());

        Random rand = new Random();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int rgb = imagen.getRGB(x, y);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                r = clamp((int) (r + rand.nextGaussian() * sigma));
                g = clamp((int) (g + rand.nextGaussian() * sigma));
                b = clamp((int) (b + rand.nextGaussian() * sigma));

                int nuevoRGB = (r << 16) | (g << 8) | b;
                salida.setRGB(x, y, nuevoRGB);
            }
        }
        return salida;
    }

    private int clamp(int valor) {
        return Math.max(0, Math.min(255, valor));
    }
}
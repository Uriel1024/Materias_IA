package modelo;

import java.awt.image.BufferedImage;
import java.util.Random;
import utils.ImageUtils;

public class RuidoInteractivoModelo {

    public BufferedImage agregarRuidoSalPimienta(BufferedImage imagen, double probabilidad) {
        int w = imagen.getWidth();
        int h = imagen.getHeight();
        BufferedImage salida = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Random rand = new Random();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double r = rand.nextDouble();
                if (r < probabilidad) salida.setRGB(x, y, 0x000000);
                else if (r > 1 - probabilidad) salida.setRGB(x, y, 0xFFFFFF);
                else salida.setRGB(x, y, imagen.getRGB(x, y));
            }
        }
        return salida;
    }

    public BufferedImage agregarRuidoGaussiano(BufferedImage imagen, double sigma) {
        int w = imagen.getWidth();
        int h = imagen.getHeight();
        BufferedImage salida = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Random rand = new Random();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = imagen.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                r = ImageUtils.clamp((int) (r + rand.nextGaussian() * sigma));
                g = ImageUtils.clamp((int) (g + rand.nextGaussian() * sigma));
                b = ImageUtils.clamp((int) (b + rand.nextGaussian() * sigma));
                salida.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return salida;
    }
}

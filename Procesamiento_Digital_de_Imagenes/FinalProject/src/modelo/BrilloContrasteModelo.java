package modelo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class BrilloContrasteModelo {

    private BufferedImage original;
    private BufferedImage modificada;
    private BufferedImage gris;

    public void setOriginal(BufferedImage img) {
        this.original = ImageUtils.toRGB(img);
    }

    public BufferedImage getOriginal() { return original; }
    public BufferedImage getModificada() { return modificada; }
    public BufferedImage getGris() { return gris; }

    public void procesar(int brillo, float contraste) {
        if (original == null) return;
        this.modificada = ejecutar(brillo, contraste, false);
        this.gris = ejecutar(brillo, contraste, true);
    }

    private BufferedImage ejecutar(int brillo, float contraste, boolean esGris) {
        int w = original.getWidth();
        int h = original.getHeight();
        BufferedImage resultado = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        int max = 255, min = -256;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = original.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int fp = esGris ? (int)(((r + g + b) / 3 + brillo) * contraste)
                                : (int)(g * contraste) + brillo;
                if (fp > max) max = fp;
                if (fp < min) min = fp;
            }
        }

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = original.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                if (esGris) {
                    int val = (r + g + b) / 3;
                    val = calcularPixel(val, brillo, contraste, min, max);
                    resultado.setRGB(x, y, new Color(val, val, val).getRGB());
                } else {
                    r = calcularPixel(r, brillo, contraste, min, max);
                    g = calcularPixel(g, brillo, contraste, min, max);
                    b = calcularPixel(b, brillo, contraste, min, max);
                    resultado.setRGB(x, y, new Color(r, g, b).getRGB());
                }
            }
        }
        return resultado;
    }

    private int calcularPixel(int valor, int brillo, float contraste, int min, int max) {
        int v = (int) ((valor + brillo) * contraste);
        if ((max - min) != 0) v = ((v - min) * 255) / (max - min);
        return ImageUtils.clamp(v);
    }
}

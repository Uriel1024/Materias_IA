package practica1;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImagenModelo {
    private BufferedImage imgOriginal;
    private BufferedImage imgModificada;
    private BufferedImage imgGris;

    public void procesarImagen(int brillo, float contraste) {
        if (imgOriginal == null) return;
        this.imgModificada = ejecutarAlgoritmo(brillo, contraste, false);
        this.imgGris = ejecutarAlgoritmo(brillo, contraste, true);
    }

    private BufferedImage ejecutarAlgoritmo(int brillo, float contraste, boolean esGris) {
        int width = imgOriginal.getWidth();
        int height = imgOriginal.getHeight();
        int[] red = new int[256];
        int[] green = new int[256];
        int[] blue = new int[256];
        int[] gray = new int[256];

        BufferedImage resultado = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        

        int max = 255, min = -256;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = imgOriginal.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int fp = esGris ? (int)(((r + g + b) / 3 + brillo) * contraste) 
                                : (int)(g * contraste) + brillo;
                if (fp > max) max = fp;
                if (fp < min) min = fp;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = imgOriginal.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                if (esGris) {
                    int gris = (r + g + b) / 3;
                    gris = calcularPixel(gris, brillo, contraste, min, max);
                    gray[gris] ++;
                    resultado.setRGB(x, y, new Color(gris, gris, gris).getRGB());
                } else {
                    r = calcularPixel(r, brillo, contraste, min, max);
                    g = calcularPixel(g, brillo, contraste, min, max);
                    b = calcularPixel(b, brillo, contraste, min, max);
                    red[r] ++ ;
                    green[g] ++;
                    blue[b] ++;
                    resultado.setRGB(x, y, new Color(r, g, b).getRGB());
                }
            }
        }
        return resultado;
    }

    private int calcularPixel(int valor, int brillo, float contraste, int min, int max) {
        int v = (int)((valor + brillo) * contraste);
        if ((max - min) != 0) v = ((v - min) * 255) / (max - min);
        return Math.max(0, Math.min(255, v));
    }

    public void setImgOriginal(BufferedImage img) { this.imgOriginal = img; }
    public BufferedImage getImgOriginal() { return imgOriginal; }
    public BufferedImage getImgModificada() { return imgModificada; }
    public BufferedImage getImgGris() { return imgGris; }
}
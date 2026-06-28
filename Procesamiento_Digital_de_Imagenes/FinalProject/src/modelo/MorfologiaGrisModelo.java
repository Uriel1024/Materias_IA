package modelo;

import java.awt.image.BufferedImage;
import java.util.Random;
import utils.ImageUtils;

public class MorfologiaGrisModelo {

    public BufferedImage convertirEscalaGrises(BufferedImage img) {
        return ImageUtils.toGrayscale(img);
    }

    private int[] obtenerVecindadGris(BufferedImage img, int cx, int cy) {
        int[] vec = new int[9];
        int idx = 0;
        int w = img.getWidth();
        int h = img.getHeight();
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                int nx = Math.max(0, Math.min(w - 1, cx + x));
                int ny = Math.max(0, Math.min(h - 1, cy + y));
                vec[idx++] = img.getRGB(nx, ny) & 0xFF;
            }
        }
        return vec;
    }

    public BufferedImage erosionGris(BufferedImage img) {
        BufferedImage gris = convertirEscalaGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindadGris(gris, x, y);
                int min = vec[0];
                for (int val : vec) if (val < min) min = val;
                out.setRGB(x, y, (min << 16) | (min << 8) | min);
            }
        }
        return out;
    }

    public BufferedImage dilatacionGris(BufferedImage img) {
        BufferedImage gris = convertirEscalaGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindadGris(gris, x, y);
                int max = vec[0];
                for (int val : vec) if (val > max) max = val;
                out.setRGB(x, y, (max << 16) | (max << 8) | max);
            }
        }
        return out;
    }

    public BufferedImage aperturaGris(BufferedImage img) { return dilatacionGris(erosionGris(img)); }
    public BufferedImage clausuraGris(BufferedImage img) { return erosionGris(dilatacionGris(img)); }

    public BufferedImage aplicarRuidoSal(BufferedImage img, double probabilidad) {
        BufferedImage gris = convertirEscalaGrises(img);
        Random rand = new Random();
        int w = gris.getWidth(), h = gris.getHeight();
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                if (rand.nextDouble() < probabilidad) gris.setRGB(x, y, 0xFFFFFF);
        return gris;
    }

    public BufferedImage aplicarRuidoPimienta(BufferedImage img, double probabilidad) {
        BufferedImage gris = convertirEscalaGrises(img);
        Random rand = new Random();
        int w = gris.getWidth(), h = gris.getHeight();
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                if (rand.nextDouble() < probabilidad) gris.setRGB(x, y, 0);
        return gris;
    }
}

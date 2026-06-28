package modelo;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class HistogramaModelo {

    public int[][] calcularHistogramasRGB(BufferedImage img) {
        int[][] hists = new int[3][256];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                hists[0][c.getRed()]++;
                hists[1][c.getGreen()]++;
                hists[2][c.getBlue()]++;
            }
        }
        return hists;
    }

    public int[] calcularHistograma(BufferedImage img) {
        int[] hist = new int[256];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                hist[c.getRed()]++;
            }
        }
        return hist;
    }

    public BufferedImage convertirYIQ(BufferedImage img) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                int Y = (int) (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue());
                out.setRGB(x, y, new Color(Y, Y, Y).getRGB());
            }
        }
        return out;
    }

    public BufferedImage convertirHSV(BufferedImage img) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                int v = (int) (hsv[2] * 255);
                out.setRGB(x, y, new Color(v, v, v).getRGB());
            }
        }
        return out;
    }

    public BufferedImage convertirHSI(BufferedImage img) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                int I = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
                out.setRGB(x, y, new Color(I, I, I).getRGB());
            }
        }
        return out;
    }

    public double media(int[] hist, int total) {
        double m = 0;
        for (int i = 0; i < 256; i++) m += i * (double) hist[i];
        return m / total;
    }

    public double varianza(int[] hist, int total, double media) {
        double v = 0;
        for (int i = 0; i < 256; i++) v += Math.pow(i - media, 2) * hist[i];
        return v / total;
    }
}

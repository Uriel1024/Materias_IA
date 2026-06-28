package modelo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class HistoOperacionesModelo {

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

    public double asimetria(int[] hist, int total, double media, double varianza) {
        if (varianza == 0) return 0;
        double asim = 0;
        double sd = Math.sqrt(varianza);
        for (int i = 0; i < 256; i++) asim += Math.pow(i - media, 3) * hist[i];
        return (asim / total) / Math.pow(sd, 3);
    }

    public BufferedImage desplazar(BufferedImage img, int offset) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                int r = ImageUtils.clamp(c.getRed() + offset);
                int g = ImageUtils.clamp(c.getGreen() + offset);
                int b = ImageUtils.clamp(c.getBlue() + offset);
                out.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return out;
    }

    public BufferedImage modificarContraste(BufferedImage img, double alpha) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                int r = ImageUtils.clamp((int) (128 + alpha * (c.getRed() - 128)));
                int g = ImageUtils.clamp((int) (128 + alpha * (c.getGreen() - 128)));
                int b = ImageUtils.clamp((int) (128 + alpha * (c.getBlue() - 128)));
                out.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return out;
    }

    public BufferedImage ecualizar(BufferedImage img) {
        int[] hist = calcularHistograma(img);
        int total = img.getWidth() * img.getHeight();
        int[] mapa = new int[256];
        double probAcumulada = 0;
        for (int i = 0; i < 256; i++) {
            probAcumulada += (double) hist[i] / total;
            mapa[i] = (int) Math.round(255 * probAcumulada);
        }
        return aplicarMapa(img, mapa);
    }

    public BufferedImage correspondencia(BufferedImage imgOri, BufferedImage imgRef) {
        int[] hist1 = calcularHistograma(imgOri);
        int[] hist2 = calcularHistograma(imgRef);
        int total1 = imgOri.getWidth() * imgOri.getHeight();
        int total2 = imgRef.getWidth() * imgRef.getHeight();
        double[] D1 = new double[256];
        double[] D2 = new double[256];
        double sum1 = 0, sum2 = 0;
        for (int i = 0; i < 256; i++) {
            sum1 += (double) hist1[i] / total1;
            D1[i] = sum1;
            sum2 += (double) hist2[i] / total2;
            D2[i] = sum2;
        }
        int[] mapa = new int[256];
        for (int i = 0; i < 256; i++) {
            int mejor = 0;
            double menor = Double.MAX_VALUE;
            for (int j = 0; j < 256; j++) {
                double diff = Math.abs(D1[i] - D2[j]);
                if (diff < menor) { menor = diff; mejor = j; }
            }
            mapa[i] = mejor;
        }
        return aplicarMapa(imgOri, mapa);
    }

    public BufferedImage convertirEscalaGrises(BufferedImage img) {
        return ImageUtils.toGrayscale(img);
    }

    private BufferedImage aplicarMapa(BufferedImage img, int[] mapa) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                int r = mapa[c.getRed()];
                int g = mapa[c.getGreen()];
                int b = mapa[c.getBlue()];
                out.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return out;
    }
}

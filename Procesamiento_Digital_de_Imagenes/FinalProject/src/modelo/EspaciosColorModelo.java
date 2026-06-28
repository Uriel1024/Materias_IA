package modelo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class EspaciosColorModelo {

    public static double[] rgbToCmy(int r, int g, int b) {
        return new double[] { 1 - (r / 255.0), 1 - (g / 255.0), 1 - (b / 255.0) };
    }

    public static int[] cmyToRgb(double c, double m, double y) {
        return new int[] {
            (int) ((1 - c) * 255),
            (int) ((1 - m) * 255),
            (int) ((1 - y) * 255)
        };
    }

    public static double[] rgbToYiq(int r, int g, int b) {
        double R = r / 255.0, G = g / 255.0, B = b / 255.0;
        double Y = 0.299 * R + 0.587 * G + 0.114 * B;
        double I = 0.596 * R - 0.274 * G - 0.322 * B;
        double Q = 0.211 * R - 0.523 * G + 0.312 * B;
        return new double[] { Y, I, Q };
    }

    public static int[] yiqToRgb(double Y, double I, double Q) {
        double R = Y + 0.956 * I + 0.621 * Q;
        double G = Y - 0.272 * I - 0.647 * Q;
        double B = Y - 1.106 * I + 1.703 * Q;
        return clampRGB(R, G, B);
    }

    public static double[] rgbToHsv(int r, int g, int b) {
        float[] hsv = Color.RGBtoHSB(r, g, b, null);
        return new double[] { hsv[0], hsv[1], hsv[2] };
    }

    public static int[] hsvToRgb(double h, double s, double v) {
        int rgb = Color.HSBtoRGB((float) h, (float) s, (float) v);
        return new int[] { (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF };
    }

    public static double[] rgbToHsi(int r, int g, int b) {
        double R = r / 255.0, G = g / 255.0, B = b / 255.0;
        double I = (R + G + B) / 3;
        double min = Math.min(R, Math.min(G, B));
        double S = (I == 0) ? 0 : 1 - (min / I);
        double num = 0.5 * ((R - G) + (R - B));
        double den = Math.sqrt((R - G) * (R - G) + (R - B) * (G - B));
        double theta = Math.acos(num / (den + 1e-6));
        double H = (B <= G) ? theta : (2 * Math.PI - theta);
        H /= (2 * Math.PI);
        return new double[] { H, S, I };
    }

    public static int[] hsiToRgb(double H, double S, double I) {
        double R = 0, G = 0, B = 0;
        double h = H * 2 * Math.PI;
        if (h < 2 * Math.PI / 3) {
            B = I * (1 - S);
            R = I * (1 + S * Math.cos(h) / Math.cos(Math.PI / 3 - h));
            G = 3 * I - (R + B);
        } else if (h < 4 * Math.PI / 3) {
            h -= 2 * Math.PI / 3;
            R = I * (1 - S);
            G = I * (1 + S * Math.cos(h) / Math.cos(Math.PI / 3 - h));
            B = 3 * I - (R + G);
        } else {
            h -= 4 * Math.PI / 3;
            G = I * (1 - S);
            B = I * (1 + S * Math.cos(h) / Math.cos(Math.PI / 3 - h));
            R = 3 * I - (G + B);
        }
        return clampRGB(R, G, B);
    }

    public static double[] rgbToLms(int r, int g, int b) {
        double R = r / 255.0, G = g / 255.0, B = b / 255.0;
        double L = 0.3811 * R + 0.5783 * G + 0.0402 * B;
        double M = 0.1967 * R + 0.7244 * G + 0.0782 * B;
        double S = 0.0241 * R + 0.1288 * G + 0.8444 * B;
        return new double[] { L, M, S };
    }

    public static double[] lmsToLab(double L, double M, double S) {
        L = Math.log10(L + 1e-6);
        M = Math.log10(M + 1e-6);
        S = Math.log10(S + 1e-6);
        double l = (L + M + S) / Math.sqrt(3);
        double a = (L + M - 2 * S) / Math.sqrt(6);
        double b = (L - M) / Math.sqrt(2);
        return new double[] { l, a, b };
    }

    public static double[] labToLms(double l, double a, double b) {
        double L = (l / Math.sqrt(3)) + (a / Math.sqrt(6)) + (b / Math.sqrt(2));
        double M = (l / Math.sqrt(3)) + (a / Math.sqrt(6)) - (b / Math.sqrt(2));
        double S = (l / Math.sqrt(3)) - (2 * a / Math.sqrt(6));
        return new double[] { Math.pow(10, L), Math.pow(10, M), Math.pow(10, S) };
    }

    public static int[] lmsToRgb(double L, double M, double S) {
        double R = 4.4679 * L - 3.5873 * M + 0.1193 * S;
        double G = -1.2186 * L + 2.3809 * M - 0.1624 * S;
        double B = 0.0497 * L - 0.2439 * M + 1.2045 * S;
        return clampRGB(R, G, B);
    }

    public static BufferedImage extractChannel(BufferedImage img, char channel) {
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(img.getRGB(x, y));
                int r = 0, g = 0, b = 0;
                if (channel == 'R') r = c.getRed();
                if (channel == 'G') g = c.getGreen();
                if (channel == 'B') b = c.getBlue();
                out.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return out;
    }

    public static BufferedImage extractGrayChannel(BufferedImage img, char channel) {
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(img.getRGB(x, y));
                int val = 0;
                if (channel == 'R') val = c.getRed();
                if (channel == 'G') val = c.getGreen();
                if (channel == 'B') val = c.getBlue();
                out.setRGB(x, y, new Color(val, val, val).getRGB());
            }
        }
        return out;
    }

    private static int[] clampRGB(double R, double G, double B) {
        int r = (int) (Math.max(0, Math.min(1, R)) * 255);
        int g = (int) (Math.max(0, Math.min(1, G)) * 255);
        int b = (int) (Math.max(0, Math.min(1, B)) * 255);
        return new int[] { r, g, b };
    }

    public BufferedImage procesar(BufferedImage img, String operacion) {
        if (img == null) return null;
        switch (operacion) {
            case "Canal R": return extractChannel(img, 'R');
            case "Canal G": return extractChannel(img, 'G');
            case "Canal B": return extractChannel(img, 'B');
            case "Gris R":  return extractGrayChannel(img, 'R');

            case "RGB a CMY": return rgbToCmy(img);
            case "CMY a RGB": return cmyToRgb(img);

            case "RGB a HSV": return rgbToHsv(img);
            case "HSV a RGB": return hsvToRgb(img);

            case "RGB a HSI": return rgbToHsi(img);
            case "HSI a RGB": return hsiToRgb(img);

            case "RGB a YIQ": return rgbToYiq(img);
            case "YIQ a RGB": return yiqToRgb(img);

            case "Convertir a lαβ": return convertirRGBaLAB(img);
            case "LAB a RGB":      return convertirLABaRGB(img);

            default: return img;
        }
    }

    private BufferedImage rgbToCmy(BufferedImage img) {
        return recorrer(img, (r, g, b) -> {
            double[] cmy = rgbToCmy(r, g, b);
            return new int[] { (int) (cmy[0] * 255), (int) (cmy[1] * 255), (int) (cmy[2] * 255) };
        });
    }

    private BufferedImage cmyToRgb(BufferedImage img) {
        return recorrer(img, (r, g, b) -> cmyToRgb(r / 255.0, g / 255.0, b / 255.0));
    }

    private BufferedImage rgbToHsv(BufferedImage img) {
        return recorrer(img, (r, g, b) -> {
            double[] hsv = rgbToHsv(r, g, b);
            return new int[] { (int) (hsv[0] * 255), (int) (hsv[1] * 255), (int) (hsv[2] * 255) };
        });
    }

    private BufferedImage hsvToRgb(BufferedImage img) {
        return recorrer(img, (r, g, b) -> hsvToRgb(r / 255.0, g / 255.0, b / 255.0));
    }

    private BufferedImage rgbToHsi(BufferedImage img) {
        return recorrer(img, (r, g, b) -> {
            double[] hsi = rgbToHsi(r, g, b);
            return new int[] { (int) (hsi[0] * 255), (int) (hsi[1] * 255), (int) (hsi[2] * 255) };
        });
    }

    private BufferedImage hsiToRgb(BufferedImage img) {
        return recorrer(img, (r, g, b) -> hsiToRgb(r / 255.0, g / 255.0, b / 255.0));
    }

    private BufferedImage rgbToYiq(BufferedImage img) {
        return recorrer(img, (r, g, b) -> {
            double[] yiq = rgbToYiq(r, g, b);
            return new int[] { (int) (yiq[0] * 255), (int) ((yiq[1] + 1) * 127), (int) ((yiq[2] + 1) * 127) };
        });
    }

    private BufferedImage yiqToRgb(BufferedImage img) {
        return recorrer(img, (r, g, b) -> yiqToRgb(r / 255.0, (g / 127.0) - 1, (b / 127.0) - 1));
    }

    private BufferedImage convertirRGBaLAB(BufferedImage img) {
        return recorrer(img, (r, g, b) -> {
            double[] lms = rgbToLms(r, g, b);
            double[] lab = lmsToLab(lms[0], lms[1], lms[2]);
            int gray = (int) ((lab[0] + 1) * 127);
            return new int[] { gray, gray, gray };
        });
    }

    private BufferedImage convertirLABaRGB(BufferedImage img) {
        return recorrer(img, (r, g, b) -> {
            double l = (r / 127.0) - 1;
            double a = (g / 127.0) - 1;
            double bb = (b / 127.0) - 1;
            double[] lms = labToLms(l, a, bb);
            return lmsToRgb(lms[0], lms[1], lms[2]);
        });
    }

    private BufferedImage recorrer(BufferedImage img, PixelOperator op) {
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int[] res = op.apply(r, g, b);
                int nr = ImageUtils.clamp(res[0]);
                int ng = ImageUtils.clamp(res[1]);
                int nb = ImageUtils.clamp(res[2]);
                out.setRGB(x, y, (nr << 16) | (ng << 8) | nb);
            }
        }
        return out;
    }

    @FunctionalInterface
    public interface PixelOperator {
        int[] apply(int r, int g, int b);
    }
}

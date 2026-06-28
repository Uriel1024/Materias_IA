package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public final class ImageUtils {

    private ImageUtils() {}

    public static BufferedImage clonar(BufferedImage img) {
        if (img == null) return null;
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        out.createGraphics().drawImage(img, 0, 0, null);
        return out;
    }

    public static BufferedImage toRGB(BufferedImage img) {
        if (img == null) return null;
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        out.createGraphics().drawImage(img, 0, 0, null);
        return out;
    }

    public static BufferedImage toGrayscale(BufferedImage img) {
        if (img == null) return null;
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                out.setRGB(x, y, (gray << 16) | (gray << 8) | gray);
            }
        }
        return out;
    }

    public static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    public static int[] getRGB(int rgb) {
        return new int[] { (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF };
    }

    public static int packRGB(int r, int g, int b) {
        return (ImageUtils.clamp(r) << 16) | (ImageUtils.clamp(g) << 8) | ImageUtils.clamp(b);
    }

    public static int packGray(int v) {
        v = clamp(v);
        return (v << 16) | (v << 8) | v;
    }

    public static void displayOn(JLabel label, BufferedImage img, int maxW, int maxH) {
        if (img == null) {
            label.setIcon(null);
            label.setText("");
            return;
        }
        int w = (label.getWidth() > 0) ? label.getWidth() : maxW;
        int h = (label.getHeight() > 0) ? label.getHeight() : maxH;
        if (w < 50) w = maxW;
        if (h < 50) h = maxH;
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled));
        label.setText("");
    }

    public static BufferedImage generateHistogram(BufferedImage img) {
        if (img == null) return null;
        int[] hist = new int[256];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int gray = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
                hist[gray]++;
            }
        }
        int W = 256, H = 160;
        BufferedImage out = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        int max = 1;
        for (int v : hist) if (v > max) max = v;

        g.setColor(new Color(230, 230, 230));
        for (int i = 0; i <= 4; i++) {
            int yLine = H - 10 - i * (H - 10) / 4;
            g.drawLine(0, yLine, W, yLine);
        }

        g.setColor(new Color(30, 100, 200));
        for (int i = 0; i < 256; i++) {
            int bar = (int) ((double) hist[i] / max * (H - 10));
            g.drawLine(i, H - 1, i, H - 1 - bar);
        }
        g.setColor(Color.BLACK);
        g.drawLine(0, H - 1, W - 1, H - 1);
        g.dispose();
        return out;
    }
}

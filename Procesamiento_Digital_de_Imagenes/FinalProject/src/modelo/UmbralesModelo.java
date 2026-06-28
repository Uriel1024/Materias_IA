package modelo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class UmbralesModelo {

    private BufferedImage original;
    private BufferedImage procesada;

    public void setOriginal(BufferedImage img) {
        this.original = ImageUtils.toRGB(img);
        this.procesada = null;
    }

    public BufferedImage getOriginal() { return original; }
    public BufferedImage getProcesada() { return procesada; }

    public void aplicar(int[] thresholds, boolean invert) {
        if (original == null) return;
        int w = original.getWidth();
        int h = original.getHeight();
        procesada = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(original.getRGB(x, y));
                double Y = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
                int newValue = calcular(Y, thresholds);
                if (invert) newValue = 255 - newValue;
                procesada.setRGB(x, y, new Color(newValue, newValue, newValue).getRGB());
            }
        }
    }

    private int calcular(double yValue, int[] thresholds) {
        int levels = thresholds.length;
        int step = 255 / levels;
        for (int i = 0; i < thresholds.length; i++) {
            if (yValue < thresholds[i]) return i * step;
        }
        return 255;
    }

    public static int[][] presets = {
        {127},
        {85, 170},
        {64, 128, 192},
        {51, 102, 153, 204}
    };
}

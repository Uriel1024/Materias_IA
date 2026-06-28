import java.awt.image.BufferedImage;
import java.awt.Color;

public class Model {
    private BufferedImage originalImage;
    private BufferedImage processedImage;

    public void setImage(BufferedImage img) {
        this.originalImage = img;
    }

    public BufferedImage getProcessedImage() {
        return processedImage;
    }

    // Método núcleo: Convierte a Y y aplica binarización n-umbrales
    public void applyThresholds(int[] thresholds, boolean invert) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        processedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(originalImage.getRGB(x, y));
                
                // Conversión YIQ (Luminancia Y)
                double Y = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
                
                int newValue = calculateNThresholds(Y, thresholds);

                if (invert) {
                    newValue = 255 - newValue;
                }

                // e) Regresar a RGB usando el valor binarizado como Yb
                // Para escalas de grises/binario, R = G = B = Yb
                int rgb = new Color(newValue, newValue, newValue).getRGB();
                processedImage.setRGB(x, y, rgb);
            }
        }
    }

    private int calculateNThresholds(double yValue, int[] thresholds) {
        // Divide el rango 0-255 en N+1 niveles según los umbrales
        int levels = thresholds.length;
        int step = 255 / levels;

        for (int i = 0; i < thresholds.length; i++) {
            if (yValue < thresholds[i]) {
                return i * step;
            }
        }
        return 255;
    }
}
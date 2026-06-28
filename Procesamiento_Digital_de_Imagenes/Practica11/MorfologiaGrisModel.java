import java.awt.image.BufferedImage;
import java.util.Random;

public class MorfologiaGrisModel {

    // Convierte la imagen a escala de grises real (0-255)
    public BufferedImage convertirEscalaGrises(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                // Fórmula estándar de luminancia
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                out.setRGB(x, y, (gray << 16) | (gray << 8) | gray);
            }
        }
        return out;
    }

    // Extrae los valores de intensidad gris de una vecindad 3x3 (Manejo de bordes por Clamp)
    private int[] obtenerVecindadGris(BufferedImage img, int cx, int cy) {
        int[] vec = new int[9];
        int idx = 0;
        int w = img.getWidth();
        int h = img.getHeight();

        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                int nx = Math.max(0, Math.min(w - 1, cx + x));
                int ny = Math.max(0, Math.min(h - 1, cy + y));
                vec[idx++] = img.getRGB(nx, ny) & 0xFF; // Extrae canal azul (da igual, r=g=b en gris)
            }
        }
        return vec;
    }

    // 1. EROSIÓN GRIS: Basada en el operador Ínfimo (Mínimo local)
    public BufferedImage erosionGris(BufferedImage img) {
        BufferedImage gris = convertirEscalaGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindadGris(gris, x, y);
                int min = vec[0];
                for (int val : vec) {
                    if (val < min) min = val;
                }
                out.setRGB(x, y, (min << 16) | (min << 8) | min);
            }
        }
        return out;
    }

    // 2. DILATACIÓN GRIS: Basada en el operador Supremo (Máximo local)
    public BufferedImage dilatacionGris(BufferedImage img) {
        BufferedImage gris = convertirEscalaGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindadGris(gris, x, y);
                int max = vec[0];
                for (int val : vec) {
                    if (val > max) max = val;
                }
                out.setRGB(x, y, (max << 16) | (max << 8) | max);
            }
        }
        return out;
    }

    // 3. APERTURA GRIS: Erosión seguida de Dilatación (Corta picos brillantes)
    public BufferedImage aperturaGris(BufferedImage img) {
        return dilatacionGris(erosionGris(img));
    }

    // 4. CLAUSURA GRIS: Dilatación seguida de Erosión (Llena valles oscuros)
    public BufferedImage clausuraGris(BufferedImage img) {
        return erosionGris(dilatacionGris(img));
    }

    // APLICACIÓN DE RUIDO EN NIVELES DE GRIS
    
    // Ruido Sal: Fuerza píxeles aleatorios al Supremo absoluto (255)
    public BufferedImage aplicarRuidoSal(BufferedImage img, double probabilidad) {
        BufferedImage gris = convertirEscalaGrises(img);
        Random rand = new Random();
        int w = gris.getWidth(), h = gris.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (rand.nextDouble() < probabilidad) {
                    gris.setRGB(x, y, (255 << 16) | (255 << 8) | 255);
                }
            }
        }
        return gris;
    }

    // Ruido Pimienta: Fuerza píxeles aleatorios al Ínfimo absoluto (0)
    public BufferedImage aplicarRuidoPimienta(BufferedImage img, double probabilidad) {
        BufferedImage gris = convertirEscalaGrises(img);
        Random rand = new Random();
        int w = gris.getWidth(), h = gris.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (rand.nextDouble() < probabilidad) {
                    gris.setRGB(x, y, 0); // Negro absoluto
                }
            }
        }
        return gris;
    }
}
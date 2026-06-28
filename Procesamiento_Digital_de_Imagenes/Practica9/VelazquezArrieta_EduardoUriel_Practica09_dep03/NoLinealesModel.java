import java.awt.image.BufferedImage;
import java.util.Arrays;

public class NoLinealesModel {

    // Convierte la imagen a escala de grises
    public BufferedImage convertirGrises(BufferedImage img) {
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

    // Extrae la vecindad de un píxel manejando los bordes (espejo/clamp)
    private int[] obtenerVecindad(BufferedImage img, int cx, int cy, int size) {
        int k = size / 2;
        int[] vec = new int[size * size];
        int idx = 0;
        int w = img.getWidth();
        int h = img.getHeight();

        for (int y = -k; y <= k; y++) {
            for (int x = -k; x <= k; x++) {
                int nx = Math.max(0, Math.min(w - 1, cx + x));
                int ny = Math.max(0, Math.min(h - 1, cy + y));
                vec[idx++] = img.getRGB(nx, ny) & 0xFF;
            }
        }
        return vec;
    }

    // =========================================================
    // FILTROS DE ORDEN
    // =========================================================

    public BufferedImage filtroMediana(BufferedImage img, int maskSize) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindad(gris, x, y, maskSize);
                Arrays.sort(vec);
                int v = vec[vec.length / 2];
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    public BufferedImage filtroMaximo(BufferedImage img, int maskSize) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindad(gris, x, y, maskSize);
                Arrays.sort(vec);
                int v = vec[vec.length - 1]; // Toma el último (máximo)
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    public BufferedImage filtroMinimo(BufferedImage img, int maskSize) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindad(gris, x, y, maskSize);
                Arrays.sort(vec);
                int v = vec[0]; // Toma el primero (mínimo)
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    public BufferedImage filtroPuntoMedio(BufferedImage img, int maskSize) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindad(gris, x, y, maskSize);
                Arrays.sort(vec);
                int v = (vec[0] + vec[vec.length - 1]) / 2;
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    public BufferedImage filtroAlfaTrimmed(BufferedImage img, int maskSize, int P) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int N = maskSize * maskSize;
        
        // Evitar que P recorte toda la ventana
        if (2 * P >= N) P = (N - 1) / 2;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindad(gris, x, y, maskSize);
                Arrays.sort(vec);
                int suma = 0;
                for (int i = P; i < N - P; i++) {
                    suma += vec[i];
                }
                int v = suma / (N - 2 * P);
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    public BufferedImage filtroMaxMin(BufferedImage img, int maskSize) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindad(gris, x, y, maskSize);
                int pixelCentral = gris.getRGB(x, y) & 0xFF;
                Arrays.sort(vec);
                int min = vec[0];
                int max = vec[vec.length - 1];

                int v = (Math.abs(pixelCentral - max) <= Math.abs(pixelCentral - min)) ? max : min;
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    // =========================================================
    // FILTROS ESTADÍSTICOS / MATEMÁTICOS
    // =========================================================

    public BufferedImage filtroInferiorArmonico(BufferedImage img, int maskSize) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int N = maskSize * maskSize;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindad(gris, x, y, maskSize);
                double suma = 0;
                for (int val : vec) {
                    suma += 1.0 / Math.max(1, val); // max(1) previene división por 0
                }
                int v = clamp((int) (N / suma));
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    public BufferedImage filtroContraArmonico(BufferedImage img, int maskSize, double P) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindad(gris, x, y, maskSize);
                double sum1 = 0;
                double sum2 = 0;
                for (int val : vec) {
                    double valSeguro = Math.max(1, val); // Previene 0^negativo = Infinity
                    sum1 += Math.pow(valSeguro, P + 1);
                    sum2 += Math.pow(valSeguro, P);
                }
                int v = clamp((int) (sum1 / sum2));
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    public BufferedImage filtroGeometrico(BufferedImage img, int maskSize) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int N = maskSize * maskSize;
        double potencia = 1.0 / N;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindad(gris, x, y, maskSize);
                double prod = 1.0;
                for (int val : vec) {
                    // Calculamos la raíz N de cada elemento antes de multiplicar para evitar desbordamiento double
                    prod *= Math.pow(Math.max(1, val), potencia); 
                }
                int v = clamp((int) prod);
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    public BufferedImage mediaAritmetica(BufferedImage img, int maskSize) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int N = maskSize * maskSize;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int[] vec = obtenerVecindad(gris, x, y, maskSize);
                int suma = 0;
                for (int val : vec) suma += val;
                int v = clamp(suma / N);
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    private int clamp(int v) { return Math.max(0, Math.min(255, v)); }
}
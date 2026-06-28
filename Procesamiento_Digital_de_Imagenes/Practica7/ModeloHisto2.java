import java.awt.image.BufferedImage;
import java.awt.Color;

public class ModeloHisto2 {

    public int[] calcularHistograma(BufferedImage img) {
        int[] hist = new int[256];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                // Se procesa sobre el promedio de los canales (escala de grises)
                int gris = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
                hist[gris]++;
            }
        }
        return hist;
    }

    public BufferedImage aplicarEcualizacion(BufferedImage img, String tipo, double alpha, int pot) {
        int[] hist = calcularHistograma(img);
        int totalPixeles = img.getWidth() * img.getHeight();
        int[] mapa = new int[256];
        double sumaProb = 0;

        for (int i = 0; i < 256; i++) {
            sumaProb += (double) hist[i] / totalPixeles;
            double f = 0;
            double fMin = 0;
            double fMax = 255;

            switch (tipo) {
                case "Uniforme":
                    f = fMax * sumaProb;
                    break;
                case "Exponencial":
                    // Evitar logaritmo de 0
                    f = fMin - (1.0 / alpha) * Math.log(Math.max(0.0001, 1.0 - sumaProb));
                    break;
                case "Rayleigh":
                    double logTerm = Math.log(1.0 / Math.max(0.0001, 1.0 - sumaProb));
                    f = alpha * Math.sqrt(2.0 * logTerm);
                    f = f * 10;
                    break;
                case "Hiperbólica":
                    double exp = 1.0 / pot;
                    f = Math.pow((Math.pow(fMax, exp) - Math.pow(fMin, exp)) * sumaProb + Math.pow(fMin, exp), pot);
                    break;
                case "Logarítmica":
                    f = 1 + (fMax * (Math.log(1 + sumaProb) / Math.log(2)));
                    break;
                default:
                    f = fMax * sumaProb;
            }
            mapa[i] = (int) Math.max(0, Math.min(255, Math.round(f)));
        }
        return aplicarMapa(img, mapa);
    }

    private BufferedImage aplicarMapa(BufferedImage img, int[] mapa) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                int r = mapa[c.getRed()];
                int g = mapa[c.getGreen()];
                int b = mapa[c.getBlue()];
                res.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return res;
    }
}
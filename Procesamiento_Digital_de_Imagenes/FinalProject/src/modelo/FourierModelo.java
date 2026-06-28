package modelo;

import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class FourierModelo {

    public BufferedImage convertirGrises(BufferedImage img) {
        return ImageUtils.toGrayscale(img);
    }

    private int siguientePotenciaDe2(int n) {
        int p = 1;
        while (p < n) p <<= 1;
        return p;
    }

    private Complejo[] fft1D(Complejo[] x, boolean invert) {
        int n = x.length;
        if (n == 1) return new Complejo[] { new Complejo(x[0].real, x[0].imag) };
        Complejo[] even = new Complejo[n / 2];
        Complejo[] odd = new Complejo[n / 2];
        for (int k = 0; k < n / 2; k++) {
            even[k] = x[2 * k];
            odd[k] = x[2 * k + 1];
        }
        Complejo[] q = fft1D(even, invert);
        Complejo[] r = fft1D(odd, invert);
        Complejo[] y = new Complejo[n];
        double dir = invert ? 1 : -1;
        for (int k = 0; k < n / 2; k++) {
            double kth = dir * 2 * k * Math.PI / n;
            Complejo wk = new Complejo(Math.cos(kth), Math.sin(kth));
            Complejo term = wk.multiplicar(r[k]);
            y[k] = q[k].sumar(term);
            y[k + n / 2] = q[k].sumar(term.escalar(-1));
        }
        return y;
    }

    public Complejo[][] dft2D(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int M = siguientePotenciaDe2(w);
        int N = siguientePotenciaDe2(h);
        Complejo[][] F = new Complejo[M][N];
        for (int x = 0; x < M; x++) {
            for (int y = 0; y < N; y++) {
                if (x < w && y < h) {
                    int gray = img.getRGB(x, y) & 255;
                    int shift = ((x + y) % 2 == 0) ? 1 : -1;
                    F[x][y] = new Complejo(gray * shift, 0);
                } else {
                    F[x][y] = new Complejo(0, 0);
                }
            }
        }
        for (int x = 0; x < M; x++) F[x] = fft1D(F[x], false);
        for (int y = 0; y < N; y++) {
            Complejo[] row = new Complejo[M];
            for (int x = 0; x < M; x++) row[x] = F[x][y];
            row = fft1D(row, false);
            for (int x = 0; x < M; x++) F[x][y] = row[x];
        }
        return F;
    }

    public BufferedImage magnitud(Complejo[][] F) {
        int M = F.length;
        int N = F[0].length;
        BufferedImage out = new BufferedImage(M, N, BufferedImage.TYPE_BYTE_GRAY);
        double max = 0;
        for (int u = 0; u < M; u++)
            for (int v = 0; v < N; v++)
                max = Math.max(max, Math.log(1 + F[u][v].magnitud()));
        if (max == 0) max = 1;
        for (int u = 0; u < M; u++) {
            for (int v = 0; v < N; v++) {
                int val = (int) (255 * Math.log(1 + F[u][v].magnitud()) / max);
                int rgb = (val << 16) | (val << 8) | val;
                out.setRGB(u, v, rgb);
            }
        }
        return out;
    }

    public void filtroGaussPasaBajas(Complejo[][] F, double D0) {
        int M = F.length;
        int N = F[0].length;
        int cx = M / 2;
        int cy = N / 2;
        for (int u = 0; u < M; u++) {
            for (int v = 0; v < N; v++) {
                double D = Math.sqrt((u - cx) * (u - cx) + (v - cy) * (v - cy));
                double H = Math.exp(-(D * D) / (2 * D0 * D0));
                F[u][v] = F[u][v].escalar(H);
            }
        }
    }

    public BufferedImage idft2D(Complejo[][] F) {
        int M = F.length;
        int N = F[0].length;
        for (int x = 0; x < M; x++) F[x] = fft1D(F[x], true);
        for (int y = 0; y < N; y++) {
            Complejo[] row = new Complejo[M];
            for (int x = 0; x < M; x++) row[x] = F[x][y];
            row = fft1D(row, true);
            for (int x = 0; x < M; x++) F[x][y] = row[x];
        }
        BufferedImage out = new BufferedImage(M, N, BufferedImage.TYPE_BYTE_GRAY);
        int mn = M * N;
        for (int x = 0; x < M; x++) {
            for (int y = 0; y < N; y++) {
                double val = F[x][y].real / mn;
                int shift = ((x + y) % 2 == 0) ? 1 : -1;
                val *= shift;
                int pixel = (int) Math.round(val);
                pixel = ImageUtils.clamp(pixel);
                int rgb = (pixel << 16) | (pixel << 8) | pixel;
                out.setRGB(x, y, rgb);
            }
        }
        return out;
    }
}

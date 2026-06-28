import java.awt.image.BufferedImage;

public class FourierModel {

    public BufferedImage convertirGrises(BufferedImage img) {
        BufferedImage out = new BufferedImage(
                img.getWidth(),
                img.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 255;
                int g = (rgb >> 8) & 255;
                int b = rgb & 255;
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                int p = (gray << 16) | (gray << 8) | gray;
                out.setRGB(x, y, p);
            }
        }
        return out;
    }

    // Encuentra la siguiente potencia de 2 (necesario para la FFT)
    private int nextPowerOf2(int n) {
        int p = 1;
        while (p < n) p <<= 1;
        return p;
    }

    // Algoritmo Fast Fourier Transform (Cooley-Tukey en 1D)
    private Complex[] fft1D(Complex[] x, boolean invert) {
        int n = x.length;
        if (n == 1) return new Complex[]{new Complex(x[0].real, x[0].imag)};

        Complex[] even = new Complex[n / 2];
        Complex[] odd = new Complex[n / 2];
        for (int k = 0; k < n / 2; k++) {
            even[k] = x[2 * k];
            odd[k] = x[2 * k + 1];
        }

        Complex[] q = fft1D(even, invert);
        Complex[] r = fft1D(odd, invert);

        Complex[] y = new Complex[n];
        double dir = invert ? 1 : -1;
        for (int k = 0; k < n / 2; k++) {
            double kth = dir * 2 * k * Math.PI / n;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            Complex term = wk.multiply(r[k]);
            
            y[k] = q[k].add(term);
            y[k + n / 2] = q[k].add(term.scale(-1));
        }
        return y;
    }

    // Transformada 2D Optimizada
    public Complex[][] dft2D(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        
        // Ajustamos las dimensiones a la siguiente potencia de 2
        int M = nextPowerOf2(w);
        int N = nextPowerOf2(h);

        Complex[][] F = new Complex[M][N];

        for (int x = 0; x < M; x++) {
            for (int y = 0; y < N; y++) {
                if (x < w && y < h) {
                    int gray = img.getRGB(x, y) & 255;
                    // Shift (-1)^(x+y) para centrar la frecuencia 0 en (M/2, N/2)
                    int shift = ((x + y) % 2 == 0) ? 1 : -1;
                    F[x][y] = new Complex(gray * shift, 0);
                } else {
                    F[x][y] = new Complex(0, 0); // Padding de ceros
                }
            }
        }

        // FFT 1D a lo largo del eje Y (columnas)
        for (int x = 0; x < M; x++) {
            F[x] = fft1D(F[x], false);
        }

        // FFT 1D a lo largo del eje X (filas)
        for (int y = 0; y < N; y++) {
            Complex[] row = new Complex[M];
            for (int x = 0; x < M; x++) {
                row[x] = F[x][y];
            }
            row = fft1D(row, false);
            for (int x = 0; x < M; x++) {
                F[x][y] = row[x];
            }
        }

        return F;
    }

    public BufferedImage magnitud(Complex[][] F) {
        int M = F.length;
        int N = F[0].length;

        BufferedImage out = new BufferedImage(M, N, BufferedImage.TYPE_BYTE_GRAY);
        double max = 0;

        for (int u = 0; u < M; u++) {
            for (int v = 0; v < N; v++) {
                max = Math.max(max, Math.log(1 + F[u][v].magnitude()));
            }
        }

        // Evitar división por 0 si la imagen es negra
        if (max == 0) max = 1;

        for (int u = 0; u < M; u++) {
            for (int v = 0; v < N; v++) {
                int val = (int) (255 * Math.log(1 + F[u][v].magnitude()) / max);
                int rgb = (val << 16) | (val << 8) | val;
                out.setRGB(u, v, rgb);
            }
        }
        return out;
    }

    public void filtroGaussPasaBajas(Complex[][] F, double D0) {
        int M = F.length;
        int N = F[0].length;
        
        // El espectro ahora está centrado nativamente gracias al shift previo
        int cx = M / 2;
        int cy = N / 2;

        for (int u = 0; u < M; u++) {
            for (int v = 0; v < N; v++) {
                double D = Math.sqrt((u - cx) * (u - cx) + (v - cy) * (v - cy));
                double H = Math.exp(-(D * D) / (2 * D0 * D0));
                F[u][v] = F[u][v].scale(H);
            }
        }
    }

    // Transformada Inversa 2D Optimizada
    public BufferedImage idft2D(Complex[][] F) {
        int M = F.length;
        int N = F[0].length;

        // IFFT 1D a lo largo del eje Y (columnas)
        for (int x = 0; x < M; x++) {
            F[x] = fft1D(F[x], true);
        }

        // IFFT 1D a lo largo del eje X (filas)
        for (int y = 0; y < N; y++) {
            Complex[] row = new Complex[M];
            for (int x = 0; x < M; x++) {
                row[x] = F[x][y];
            }
            row = fft1D(row, true);
            for (int x = 0; x < M; x++) {
                F[x][y] = row[x];
            }
        }

        BufferedImage out = new BufferedImage(M, N, BufferedImage.TYPE_BYTE_GRAY);
        int mn = M * N;

        for (int x = 0; x < M; x++) {
            for (int y = 0; y < N; y++) {
                // Escalar por 1/MN acorde a la definición matemática
                double val = F[x][y].real / mn;
                
                // Deshacer el centrado de frecuencias (-1)^(x+y)
                int shift = ((x + y) % 2 == 0) ? 1 : -1;
                val *= shift; 

                int pixel = (int) Math.round(val);
                pixel = Math.max(0, Math.min(255, pixel));
                int rgb = (pixel << 16) | (pixel << 8) | pixel;
                
                out.setRGB(x, y, rgb);
            }
        }
        return out;
    }
}
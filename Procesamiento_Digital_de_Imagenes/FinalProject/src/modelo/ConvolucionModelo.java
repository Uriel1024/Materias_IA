package modelo;

import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class ConvolucionModelo {

    public BufferedImage convertirGrises(BufferedImage img) {
        return ImageUtils.toGrayscale(img);
    }

    public BufferedImage negativo(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = 255 - ((rgb >> 16) & 0xFF);
                int g = 255 - ((rgb >> 8) & 0xFF);
                int bv = 255 - (rgb & 0xFF);
                out.setRGB(x, y, (r << 16) | (g << 8) | bv);
            }
        return out;
    }

    public BufferedImage binarizar(BufferedImage img, int umbral) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int v = (gris.getRGB(x, y) & 0xFF) >= umbral ? 255 : 0;
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        return out;
    }

    public BufferedImage convolucion(BufferedImage img, double[][] kernel) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int k = kernel.length / 2;
        double sumaKernel = 0, sumaPositivos = 0;
        for (int i = 0; i < kernel.length; i++)
            for (int j = 0; j < kernel[i].length; j++) {
                sumaKernel += kernel[i][j];
                if (kernel[i][j] > 0) sumaPositivos += kernel[i][j];
            }
        boolean esBordes = Math.abs(sumaKernel) < 0.1;
        double divisor = (esBordes && sumaPositivos > 1.0) ? sumaPositivos : 1.0;
        for (int y = k; y < h - k; y++) {
            for (int x = k; x < w - k; x++) {
                double suma = 0;
                for (int ky = -k; ky <= k; ky++)
                    for (int kx = -k; kx <= k; kx++)
                        suma += (gris.getRGB(x + kx, y + ky) & 0xFF) * kernel[ky + k][kx + k];
                int v = esBordes ? ImageUtils.clamp((int) (Math.abs(suma) / divisor))
                                 : ImageUtils.clamp((int) suma);
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    public BufferedImage convolucionDoble(BufferedImage img, double[][] kH, double[][] kV) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int k = kH.length / 2;
        double sumaPositivos = 0;
        for (int i = 0; i < kH.length; i++)
            for (int j = 0; j < kH[i].length; j++)
                if (kH[i][j] > 0) sumaPositivos += kH[i][j];
        if (sumaPositivos == 0) sumaPositivos = 1.0;
        for (int y = k; y < h - k; y++) {
            for (int x = k; x < w - k; x++) {
                double sh = 0, sv = 0;
                for (int ky = -k; ky <= k; ky++)
                    for (int kx = -k; kx <= k; kx++) {
                        int pix = gris.getRGB(x + kx, y + ky) & 0xFF;
                        sh += pix * kH[ky + k][kx + k];
                        sv += pix * kV[ky + k][kx + k];
                    }
                int v = ImageUtils.clamp((int) ((Math.abs(sh) + Math.abs(sv)) / sumaPositivos));
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    public BufferedImage compassOR(BufferedImage img, double[][][] kernels) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int k = kernels[0].length / 2;
        double sumaPositivos = 0;
        for (int i = 0; i < kernels[0].length; i++)
            for (int j = 0; j < kernels[0][i].length; j++)
                if (kernels[0][i][j] > 0) sumaPositivos += kernels[0][i][j];
        if (sumaPositivos == 0) sumaPositivos = 1.0;
        for (int y = k; y < h - k; y++) {
            for (int x = k; x < w - k; x++) {
                int maxV = 0;
                for (double[][] kernel : kernels) {
                    double s = 0;
                    for (int ky = -k; ky <= k; ky++)
                        for (int kx = -k; kx <= k; kx++)
                            s += (gris.getRGB(x + kx, y + ky) & 0xFF) * kernel[ky + k][kx + k];
                    maxV = Math.max(maxV, (int) Math.abs(s));
                }
                int v = ImageUtils.clamp((int) (maxV / sumaPositivos));
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    public BufferedImage compassSuma(BufferedImage img, double[][][] kernels) {
        BufferedImage gris = convertirGrises(img);
        int w = gris.getWidth(), h = gris.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int k = kernels[0].length / 2;
        double sumaPositivos = 0;
        for (int i = 0; i < kernels[0].length; i++)
            for (int j = 0; j < kernels[0][i].length; j++)
                if (kernels[0][i][j] > 0) sumaPositivos += kernels[0][i][j];
        if (sumaPositivos == 0) sumaPositivos = 1.0;
        for (int y = k; y < h - k; y++) {
            for (int x = k; x < w - k; x++) {
                int total = 0;
                for (double[][] kernel : kernels) {
                    double s = 0;
                    for (int ky = -k; ky <= k; ky++)
                        for (int kx = -k; kx <= k; kx++)
                            s += (gris.getRGB(x + kx, y + ky) & 0xFF) * kernel[ky + k][kx + k];
                    total += (int) Math.abs(s);
                }
                int v = ImageUtils.clamp((int) (total / (sumaPositivos * 2.5)));
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        }
        return out;
    }

    public double[][] promedio3x3() { double v = 1.0/9.0; return new double[][]{{v,v,v},{v,v,v},{v,v,v}}; }
    public double[][] promedio5x5() { double v = 1.0/25.0; double[][] k = new double[5][5]; for (int i=0;i<5;i++) for (int j=0;j<5;j++) k[i][j]=v; return k; }
    public double[][] promedio7x7() { double v = 1.0/49.0; double[][] k = new double[7][7]; for (int i=0;i<7;i++) for (int j=0;j<7;j++) k[i][j]=v; return k; }
    public double[][] gaussiano3x3() { return new double[][]{ {1/16.0, 2/16.0, 1/16.0}, {2/16.0, 4/16.0, 2/16.0}, {1/16.0, 2/16.0, 1/16.0} }; }
    public double[][] gaussiano5x5() {
        double[][] k = { {1,4,6,4,1}, {4,16,24,16,4}, {6,24,36,24,6}, {4,16,24,16,4}, {1,4,6,4,1} };
        for (int i=0;i<5;i++) for (int j=0;j<5;j++) k[i][j] /= 256.0;
        return k;
    }
    public double[][] definicionSuave() { return new double[][]{{1,-2,1},{-2,5,-2},{1,-2,1}}; }
    public double[][] definicionMedia() { return new double[][]{{0,-1,0},{-1,5,-1},{0,-1,0}}; }
    public double[][] definicionFuerte() { return new double[][]{{-1,-1,-1},{-1,9,-1},{-1,-1,-1}}; }
    public double[][] laplaciano4() { return new double[][]{{0,-1,0},{-1,4,-1},{0,-1,0}}; }
    public double[][] laplaciano8() { return new double[][]{{-1,-1,-1},{-1,8,-1},{-1,-1,-1}}; }
    public double[][] sobelX() { return new double[][]{{-1,0,1},{-2,0,2},{-1,0,1}}; }
    public double[][] sobelY() { return new double[][]{{-1,-2,-1},{0,0,0},{1,2,1}}; }
    public double[][] prewittX() { return new double[][]{{-1,0,1},{-1,0,1},{-1,0,1}}; }
    public double[][] prewittY() { return new double[][]{{-1,-1,-1},{0,0,0},{1,1,1}}; }
    public double[][] robertsX() { return new double[][]{{0,0,-1},{0,1,0},{0,0,0}}; }
    public double[][] robertsY() { return new double[][]{{-1,0,0},{0,1,0},{0,0,0}}; }
    public double[][] freiChenX() { double s=Math.sqrt(2); return new double[][]{{1,0,-1},{s,0,-s},{1,0,-1}}; }
    public double[][] freiChenY() { double s=Math.sqrt(2); return new double[][]{{1,s,1},{0,0,0},{-1,-s,-1}}; }

    public double[][] prewittCompassH1() { return new double[][]{{1,1,-1},{1,-2,-1},{1,1,-1}}; }
    public double[][] prewittCompassH2() { return new double[][]{{1,-1,-1},{1,-2,-1},{1,1,1}}; }
    public double[][] prewittCompassH3() { return new double[][]{{-1,-1,-1},{1,-2,1},{1,1,1}}; }
    public double[][] prewittCompassH4() { return new double[][]{{-1,-1,1},{-1,-2,1},{1,1,1}}; }
    public double[][] prewittCompassH5() { return new double[][]{{-1,1,1},{-1,-2,1},{-1,1,1}}; }
    public double[][] prewittCompassH6() { return new double[][]{{1,1,1},{-1,-2,1},{-1,-1,1}}; }
    public double[][] prewittCompassH7() { return new double[][]{{1,1,1},{1,-2,1},{-1,-1,-1}}; }
    public double[][] prewittCompassH8() { return new double[][]{{1,1,1},{1,-2,-1},{1,-1,-1}}; }

    public double[][] kirschH1() { return new double[][]{{5,-3,-3},{5,0,-3},{5,-3,-3}}; }
    public double[][] kirschH2() { return new double[][]{{-3,-3,-3},{5,0,-3},{5,5,-3}}; }
    public double[][] kirschH3() { return new double[][]{{-3,-3,-3},{-3,0,-3},{5,5,5}}; }
    public double[][] kirschH4() { return new double[][]{{-3,-3,-3},{-3,0,5},{-3,5,5}}; }
    public double[][] kirschH5() { return new double[][]{{-3,-3,5},{-3,0,5},{-3,-3,5}}; }
    public double[][] kirschH6() { return new double[][]{{-3,5,5},{-3,0,5},{-3,-3,-3}}; }
    public double[][] kirschH7() { return new double[][]{{5,5,5},{-3,0,-3},{-3,-3,-3}}; }
    public double[][] kirschH8() { return new double[][]{{5,5,-3},{5,0,-3},{-3,-3,-3}}; }

    public double[][] robinson3H1() { return new double[][]{{1,0,-1},{1,0,-1},{1,0,-1}}; }
    public double[][] robinson3H2() { return new double[][]{{0,-1,-1},{1,0,-1},{1,1,0}}; }
    public double[][] robinson3H3() { return new double[][]{{-1,-1,-1},{0,0,0},{1,1,1}}; }
    public double[][] robinson3H4() { return new double[][]{{-1,-1,0},{-1,0,1},{0,1,1}}; }
    public double[][] robinson3H5() { return new double[][]{{-1,0,1},{-1,0,1},{-1,0,1}}; }
    public double[][] robinson3H6() { return new double[][]{{0,1,1},{-1,0,1},{-1,-1,0}}; }
    public double[][] robinson3H7() { return new double[][]{{1,1,1},{0,0,0},{-1,-1,-1}}; }
    public double[][] robinson3H8() { return new double[][]{{1,1,0},{1,0,-1},{0,-1,-1}}; }

    public double[][] robinson5H1() { return new double[][]{{1,0,-1},{2,0,-2},{1,0,-1}}; }
    public double[][] robinson5H2() { return new double[][]{{0,-1,-2},{1,0,-1},{2,1,0}}; }
    public double[][] robinson5H3() { return new double[][]{{-1,-2,-1},{0,0,0},{1,2,1}}; }
    public double[][] robinson5H4() { return new double[][]{{-2,-1,0},{-1,0,1},{0,1,2}}; }
    public double[][] robinson5H5() { return new double[][]{{-1,0,1},{-2,0,2},{-1,0,1}}; }
    public double[][] robinson5H6() { return new double[][]{{0,1,2},{-1,0,1},{-2,-1,0}}; }
    public double[][] robinson5H7() { return new double[][]{{1,2,1},{0,0,0},{-1,-2,-1}}; }
    public double[][] robinson5H8() { return new double[][]{{2,1,0},{1,0,-1},{0,-1,-2}}; }

    public double[][] log7x7() {
        return new double[][] {
            { 0, 0,-1,-1,-1, 0, 0}, { 0,-2,-3,-3,-3,-2, 0}, {-1,-3, 5, 5, 5,-3,-1},
            {-1,-3, 5,16, 5,-3,-1}, {-1,-3, 5, 5, 5,-3,-1}, { 0,-2,-3,-3,-3,-2, 0},
            { 0, 0,-1,-1,-1, 0, 0}
        };
    }

    public double[][] log9x9() {
        return new double[][] {
            { 0, 0, 0,-1,-1,-1, 0, 0, 0}, { 0,-2,-3,-3,-3,-3,-3,-2, 0}, { 0,-3,-2,-1,-1,-1,-2,-3, 0},
            {-1,-3,-1, 9, 9, 9,-1,-3,-1}, {-1,-3,-1, 9,19, 9,-1,-3,-1}, {-1,-3,-1, 9, 9, 9,-1,-3,-1},
            { 0,-3,-2,-1,-1,-1,-2,-3, 0}, { 0,-2,-3,-3,-3,-3,-3,-2, 0}, { 0, 0, 0,-1,-1,-1, 0, 0, 0}
        };
    }
}

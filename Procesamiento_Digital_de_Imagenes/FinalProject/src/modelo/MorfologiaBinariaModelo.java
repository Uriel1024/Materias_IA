package modelo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import utils.ImageUtils;

public class MorfologiaBinariaModelo {

    public BufferedImage convertirBinaria(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                int bin = (gray > 127) ? 255 : 0;
                out.setRGB(x, y, new Color(bin, bin, bin).getRGB());
            }
        }
        return out;
    }

    public BufferedImage dilatacion(BufferedImage img) {
        BufferedImage bin = convertirBinaria(img);
        int w = bin.getWidth(), h = bin.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                boolean hasWhite = false;
                for (int j = -1; j <= 1; j++)
                    for (int i = -1; i <= 1; i++)
                        if ((bin.getRGB(x + i, y + j) & 0xFF) == 255) hasWhite = true;
                int val = hasWhite ? 255 : 0;
                out.setRGB(x, y, new Color(val, val, val).getRGB());
            }
        }
        return out;
    }

    public BufferedImage erosion(BufferedImage img) {
        BufferedImage bin = convertirBinaria(img);
        int w = bin.getWidth(), h = bin.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                boolean allWhite = true;
                for (int j = -1; j <= 1; j++)
                    for (int i = -1; i <= 1; i++)
                        if ((bin.getRGB(x + i, y + j) & 0xFF) == 0) allWhite = false;
                int val = allWhite ? 255 : 0;
                out.setRGB(x, y, new Color(val, val, val).getRGB());
            }
        }
        return out;
    }

    public BufferedImage apertura(BufferedImage img) { return dilatacion(erosion(img)); }
    public BufferedImage clausura(BufferedImage img) { return erosion(dilatacion(img)); }

    public BufferedImage aplicarRuidoSal(BufferedImage img, double probabilidad) {
        BufferedImage bin = convertirBinaria(img);
        Random rand = new Random();
        for (int y = 0; y < bin.getHeight(); y++)
            for (int x = 0; x < bin.getWidth(); x++)
                if (rand.nextDouble() < probabilidad) bin.setRGB(x, y, Color.WHITE.getRGB());
        return bin;
    }

    public BufferedImage aplicarRuidoPimienta(BufferedImage img, double probabilidad) {
        BufferedImage bin = convertirBinaria(img);
        Random rand = new Random();
        for (int y = 0; y < bin.getHeight(); y++)
            for (int x = 0; x < bin.getWidth(); x++)
                if (rand.nextDouble() < probabilidad) bin.setRGB(x, y, Color.BLACK.getRGB());
        return bin;
    }

    public BufferedImage esqueletizado(BufferedImage img) {
        BufferedImage bin = convertirBinaria(img);
        int w = bin.getWidth(), h = bin.getHeight();
        int[][] grid = new int[w][h];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                grid[x][y] = ((bin.getRGB(x, y) & 0xFF) == 255) ? 1 : 0;
        boolean changed;
        do {
            changed = false;
            List<int[]> toDelete = new ArrayList<>();
            for (int y = 1; y < h - 1; y++) {
                for (int x = 1; x < w - 1; x++) {
                    if (grid[x][y] == 1 && zhangSuenStep(grid, x, y, 1))
                        toDelete.add(new int[] {x, y});
                }
            }
            if (!toDelete.isEmpty()) changed = true;
            for (int[] p : toDelete) grid[p[0]][p[1]] = 0;
            toDelete.clear();
            for (int y = 1; y < h - 1; y++) {
                for (int x = 1; x < w - 1; x++) {
                    if (grid[x][y] == 1 && zhangSuenStep(grid, x, y, 2))
                        toDelete.add(new int[] {x, y});
                }
            }
            if (!toDelete.isEmpty()) changed = true;
            for (int[] p : toDelete) grid[p[0]][p[1]] = 0;
        } while (changed);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int val = (grid[x][y] == 1) ? 255 : 0;
                out.setRGB(x, y, new Color(val, val, val).getRGB());
            }
        return out;
    }

    private boolean zhangSuenStep(int[][] grid, int x, int y, int step) {
        int[] p = new int[9];
        p[1] = grid[x][y - 1];
        p[2] = grid[x + 1][y - 1];
        p[3] = grid[x + 1][y];
        p[4] = grid[x + 1][y + 1];
        p[5] = grid[x][y + 1];
        p[6] = grid[x - 1][y + 1];
        p[7] = grid[x - 1][y];
        p[8] = grid[x - 1][y - 1];
        int A = 0;
        for (int i = 1; i <= 7; i++) if (p[i] == 0 && p[i + 1] == 1) A++;
        if (p[8] == 0 && p[1] == 1) A++;
        int B = 0;
        for (int i = 1; i <= 8; i++) B += p[i];
        boolean m1 = (step == 1) ? (p[1] * p[3] * p[5] == 0) : (p[1] * p[3] * p[7] == 0);
        boolean m2 = (step == 1) ? (p[3] * p[5] * p[7] == 0) : (p[1] * p[5] * p[7] == 0);
        return (B >= 2 && B <= 6 && A == 1 && m1 && m2);
    }
}

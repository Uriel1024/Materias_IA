import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;

public class Model {
    private BufferedImage img1;
    private BufferedImage img2;

    public void setImage1(BufferedImage img) { this.img1 = img; }
    public void setImage2(BufferedImage img) { this.img2 = img; }
    public BufferedImage getImg1() { return img1; }
    public BufferedImage getImg2() { return img2; }

    // --- 1. TRANSFORMACIONES GEOMÉTRICAS ---
    public BufferedImage scale(double sX, double sY) {
        int newW = (int)(img1.getWidth() * sX);
        int newH = (int)(img1.getHeight() * sY);
        BufferedImage res = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < newH; y++) {
            for (int x = 0; x < newW; x++) {
                int oldX = (int)(x / sX);
                int oldY = (int)(y / sY);
                if (oldX < img1.getWidth() && oldY < img1.getHeight()) 
                    res.setRGB(x, y, img1.getRGB(oldX, oldY));
            }
        }
        return res;
    }

    public BufferedImage rotate(double degrees) {
        double rads = Math.toRadians(degrees);
        int w = img1.getWidth(), h = img1.getHeight();
        int newW = (int)Math.abs(w*Math.cos(rads)) + (int)Math.abs(h*Math.sin(rads));
        int newH = (int)Math.abs(h*Math.cos(rads)) + (int)Math.abs(w*Math.sin(rads));
        BufferedImage res = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        
        for(int y=0; y<newH; y++) {
            for(int x=0; x<newW; x++) {
                int tx = x - newW/2, ty = y - newH/2;
                int oldX = (int)(tx*Math.cos(rads) + ty*Math.sin(rads) + w/2);
                int oldY = (int)(-tx*Math.sin(rads) + ty*Math.cos(rads) + h/2);
                if(oldX>=0 && oldX<w && oldY>=0 && oldY<h) res.setRGB(x, y, img1.getRGB(oldX, oldY));
            }
        }
        return res;
    }

    // --- MÉTODO AUXILIAR PARA IGUALAR TAMAÑOS ---
    private BufferedImage resizeToMatch(BufferedImage src, int targetWidth, int targetHeight) {
        if (src.getWidth() == targetWidth && src.getHeight() == targetHeight) {
            return src; // Si ya son del mismo tamaño, no hace nada
        }
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        // Dibuja la imagen estirándola o encogiéndola al nuevo tamaño
        g2d.drawImage(src, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resized;
    }

// --- NUEVO MÉTODO: Igualar tamaños ---
    // Toma la Imagen 1 como base y redimensiona la Imagen 2 a esas dimensiones
    public void equalizeSizes() {
        if (img1 != null && img2 != null) {
            int targetW = img1.getWidth();
            int targetH = img1.getHeight();
            
            // Solo redimensiona si los tamaños son diferentes
            if (img2.getWidth() != targetW || img2.getHeight() != targetH) {
                BufferedImage resized = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D g2d = resized.createGraphics();
                g2d.drawImage(img2, 0, 0, targetW, targetH, null);
                g2d.dispose();
                
                this.img2 = resized; // Sobrescribimos la img2 con la versión redimensionada
            }
        }
    }

    // --- EL MÉTODO COMBINE AHORA ES MÁS LIMPIO ---
    public BufferedImage combine(String op) {
        // Como ya las igualamos antes, ambas miden lo mismo
        int w = img1.getWidth();
        int h = img1.getHeight();
        BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c1 = new Color(img1.getRGB(x, y));
                Color c2 = new Color(img2.getRGB(x, y));
                int r=0;

                int v1 = c1.getRed();
                int v2 = c2.getRed();

                switch(op) {
                    case "Suma": r = Math.min(255, v1 + v2); break;
                    case "Resta": r = Math.max(0, v1 - v2); break;
                    case "Multi": r = Math.min(255, (v1 * v2) / 255); break; 
                    case "División": r = (v2 == 0) ? 255 : Math.min(255, (v1 * 255) / v2); break; 
                    case "AND": r = v1 & v2; break;
                    case "OR":  r = v1 | v2; break;
                    case "XOR": r = v1 ^ v2; break;
                    case "NOT": r = 255 - v1; break; 
                    case "<":  r = (v1 < v2) ? 255 : 0; break;
                    case "<=": r = (v1 <= v2) ? 255 : 0; break;
                    case ">":  r = (v1 > v2) ? 255 : 0; break;
                    case ">=": r = (v1 >= v2) ? 255 : 0; break;
                    case "==": r = (v1 == v2) ? 255 : 0; break;
                    case "!=": r = (v1 != v2) ? 255 : 0; break;
                }
                res.setRGB(x, y, new Color(r, r, r).getRGB()); 
            }
        }
        return res;
    }
}
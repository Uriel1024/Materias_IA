import java.awt.image.BufferedImage;

public class ColorController {

    public BufferedImage procesarImagen(BufferedImage img, String operacion) {
        if (img == null) return null;

        switch (operacion) {
            case "Canal R": return ColorModel.extractChannel(img, 'R');
            case "Canal G": return ColorModel.extractChannel(img, 'G');
            case "Canal B": return ColorModel.extractChannel(img, 'B');
            case "Gris R": return ColorModel.extractGrayChannel(img, 'R');

            case "RGB a CMY": return rgbToCmy(img);
            case "CMY a RGB": return cmyToRgb(img);

            case "RGB a HSV": return rgbToHsv(img);
            case "HSV a RGB": return hsvToRgb(img);

            case "RGB a HSI": return rgbToHsi(img);
            case "HSI a RGB": return hsiToRgb(img);

            case "RGB a YIQ": return rgbToYiq(img);
            case "YIQ a RGB": return yiqToRgb(img);

            case "Convertir a lαβ": return convertirRGBaLAB(img);
            case "LAB a RGB": return convertirLABaRGB(img);

            default: return img;
        }
    }

    // ==============================
    // IMPLEMENTACIONES
    // ==============================

    private BufferedImage rgbToCmy(BufferedImage img) {
        return recorrer(img, (r,g,b) -> {
            double[] cmy = ColorModel.rgbToCmy(r,g,b);
            return new int[]{
                (int)(cmy[0]*255),
                (int)(cmy[1]*255),
                (int)(cmy[2]*255)
            };
        });
    }

    private BufferedImage cmyToRgb(BufferedImage img) {
        return recorrer(img, (r,g,b) -> {
            return ColorModel.cmyToRgb(r/255.0, g/255.0, b/255.0);
        });
    }

    private BufferedImage rgbToHsv(BufferedImage img) {
        return recorrer(img, (r,g,b) -> {
            double[] hsv = ColorModel.rgbToHsv(r,g,b);
            return new int[]{
                (int)(hsv[0]*255),
                (int)(hsv[1]*255),
                (int)(hsv[2]*255)
            };
        });
    }

    private BufferedImage hsvToRgb(BufferedImage img) {
        return recorrer(img, (r,g,b) -> {
            return ColorModel.hsvToRgb(r/255.0, g/255.0, b/255.0);
        });
    }

    private BufferedImage rgbToHsi(BufferedImage img) {
        return recorrer(img, (r,g,b) -> {
            double[] hsi = ColorModel.rgbToHsi(r,g,b);
            return new int[]{
                (int)(hsi[0]*255),
                (int)(hsi[1]*255),
                (int)(hsi[2]*255)
            };
        });
    }

    private BufferedImage hsiToRgb(BufferedImage img) {
        return recorrer(img, (r,g,b) -> {
            return ColorModel.hsiToRgb(r/255.0, g/255.0, b/255.0);
        });
    }

    private BufferedImage rgbToYiq(BufferedImage img) {
        return recorrer(img, (r,g,b) -> {
            double[] yiq = ColorModel.rgbToYiq(r,g,b);
            return new int[]{
                (int)(yiq[0]*255),
                (int)((yiq[1]+1)*127),
                (int)((yiq[2]+1)*127)
            };
        });
    }

    private BufferedImage yiqToRgb(BufferedImage img) {
        return recorrer(img, (r,g,b) -> {
            return ColorModel.yiqToRgb(
                r/255.0,
                (g/127.0)-1,
                (b/127.0)-1
            );
        });
    }

    private BufferedImage convertirRGBaLAB(BufferedImage img) {
        return recorrer(img, (r,g,b) -> {
            double[] lms = ColorModel.rgbToLms(r,g,b);
            double[] lab = ColorModel.lmsToLab(lms[0],lms[1],lms[2]);
            int gray = (int)((lab[0]+1)*127);
            return new int[]{gray,gray,gray};
        });
    }

    private BufferedImage convertirLABaRGB(BufferedImage img) {
        return recorrer(img, (r,g,b) -> {
            double l = (r/127.0)-1;
            double a = (g/127.0)-1;
            double bb = (b/127.0)-1;

            double[] lms = ColorModel.labToLms(l,a,bb);
            return ColorModel.lmsToRgb(lms[0],lms[1],lms[2]);
        });
    }

    // ==============================
    // FUNCIÓN GENÉRICA (clave del diseño)
    // ==============================
    private BufferedImage recorrer(BufferedImage img, PixelOperator op) {
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage out = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

        for(int y=0;y<h;y++){
            for(int x=0;x<w;x++){
                int rgb = img.getRGB(x,y);
                int r = (rgb>>16)&0xFF;
                int g = (rgb>>8)&0xFF;
                int b = rgb&0xFF;

                int[] res = op.apply(r,g,b);

                int nr = clamp(res[0]);
                int ng = clamp(res[1]);
                int nb = clamp(res[2]);

                out.setRGB(x,y,(nr<<16)|(ng<<8)|nb);
            }
        }
        return out;
    }

    private int clamp(int v){
        return Math.max(0, Math.min(255, v));
    }

    interface PixelOperator {
        int[] apply(int r, int g, int b);
    }
}
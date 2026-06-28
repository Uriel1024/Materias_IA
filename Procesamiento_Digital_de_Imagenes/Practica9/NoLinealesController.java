import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NoLinealesController {

    private final NoLinealesModel model;
    private final NoLinealesView view;
    private BufferedImage imagen;

    public NoLinealesController(NoLinealesModel model, NoLinealesView view) {
        this.model = model;
        this.view = view;
        init();
    }

    private void init() {
        view.btnCargar.addActionListener(e -> cargarImagen());
        view.btnAplicar.addActionListener(e -> aplicarFiltro());
    }

    private void cargarImagen() {
        try {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(view) != JFileChooser.APPROVE_OPTION) return;
            imagen = ImageIO.read(fc.getSelectedFile());
            view.mostrarImagen(view.lblOriginal, imagen);
            view.mostrarImagen(view.lblHistOriginal, histograma(imagen));
            view.txtInfo.setText("Imagen cargada: " + fc.getSelectedFile().getName());
        } catch (Exception ex) {
            view.mostrarError("Error al cargar la imagen: " + ex.getMessage());
        }
    }

    private void aplicarFiltro() {
        if (imagen == null) { view.mostrarError("Carga una imagen primero."); return; }

        String filtro = (String) view.comboFiltros.getSelectedItem();
        int maskSize = (Integer) view.spnMask.getValue();
        double paramP = (Double) view.spnParamP.getValue();
        
        BufferedImage resultado = null;
        String descripcion = "";

        switch (filtro) {
            case "Filtro de la Mediana":
                resultado = model.filtroMediana(imagen, maskSize);
                descripcion = "MEDIANA: Ordena la vecindad y toma el valor central. Excelente para ruido Sal y Pimienta preservando contornos.";
                break;
            case "Filtro Alfa-Trimmed":
                resultado = model.filtroAlfaTrimmed(imagen, maskSize, (int) paramP);
                descripcion = "ALFA-TRIMMED: Elimina los " + (int)paramP + " valores más altos y más bajos, promediando el resto. Combinación de Media y Mediana.";
                break;
            case "Filtro del Máximo":
                resultado = model.filtroMaximo(imagen, maskSize);
                descripcion = "MÁXIMO: Toma el valor más alto de la vecindad. Bueno para remover ruido negativo (oscuro).";
                break;
            case "Filtro del Mínimo":
                resultado = model.filtroMinimo(imagen, maskSize);
                descripcion = "MÍNIMO: Toma el valor más bajo de la vecindad. Bueno para remover ruido positivo (puntos blancos).";
                break;
            case "Filtro del Punto Medio":
                resultado = model.filtroPuntoMedio(imagen, maskSize);
                descripcion = "PUNTO MEDIO: Promedia el valor mínimo y máximo de la máscara. Ideal para ruido Gaussiano simétrico.";
                break;
            case "Filtro Inferior Armónico":
                resultado = model.filtroInferiorArmonico(imagen, maskSize);
                descripcion = "ARMÓNICO: N / Sum(1/w). Eficaz contra ruido Gamma (picos de ruido positivos).";
                break;
            case "Filtro Contra Armónico":
                resultado = model.filtroContraArmonico(imagen, maskSize, paramP);
                descripcion = "CONTRA ARMÓNICO con P=" + paramP + ". Si P>0 elimina ruido pimienta. Si P<0 elimina ruido sal.";
                break;
            case "Filtro Geométrico":
                resultado = model.filtroGeometrico(imagen, maskSize);
                descripcion = "GEOMÉTRICO: Raíz N-ésima del producto de vecindades. Remueve ruido Gaussiano, pero es propenso a oscurecer la imagen.";
                break;
            case "Filtro Máximo-Mínimo":
                resultado = model.filtroMaxMin(imagen, maskSize);
                descripcion = "MÁXIMO-MÍNIMO: Compara el pixel con el max y min de la vecindad y lo sustituye por el más cercano. Realza contornos.";
                break;
            case "Filtro Media Aritmética":
                resultado = model.mediaAritmetica(imagen, maskSize);
                descripcion = "MEDIA ARITMÉTICA: Promedio estándar de la vecindad. Suaviza pero emborrona contornos.";
                break;
        }

        if (resultado != null) {
            view.mostrarImagen(view.lblResultado, resultado);
            view.mostrarImagen(view.lblHistResultado, histograma(resultado));
            view.txtInfo.setText("Filtro aplicado: " + filtro + " (" + maskSize + "x" + maskSize + ")\n" + descripcion);
        }
    }

    private BufferedImage histograma(BufferedImage img) {
        int[] hist = new int[256];
        for (int y = 0; y < img.getHeight(); y++)
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int gray = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
                hist[gray]++;
            }

        int W = 256, H = 160;
        BufferedImage out = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        int max = 1;
        for (int v : hist) if (v > max) max = v;

        g.setColor(new Color(230, 230, 230));
        for (int i = 0; i <= 4; i++) {
            int yLine = H - 10 - i * (H - 10) / 4;
            g.drawLine(0, yLine, W, yLine);
        }

        g.setColor(new Color(200, 50, 50)); // Rojo para diferenciar del anterior
        for (int i = 0; i < 256; i++) {
            int bar = (int)((double) hist[i] / max * (H - 10));
            g.drawLine(i, H - 1, i, H - 1 - bar);
        }
        g.setColor(Color.BLACK);
        g.drawLine(0, H - 1, W - 1, H - 1);
        g.dispose();
        return out;
    }
}
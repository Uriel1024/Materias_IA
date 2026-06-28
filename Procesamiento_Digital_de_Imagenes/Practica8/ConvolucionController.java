import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

    /**
     * CONTROLADOR — Conecta la Vista con el Modelo.
 * Gestiona la carga de imagen, la selección de filtro,
 * la aplicación del mismo y la visualización del resultado con su histograma.
 */
public class ConvolucionController {

    private final ConvolucionModel model;
    private final ConvolucionView  view;
    private BufferedImage imagen;

    public ConvolucionController(ConvolucionModel model, ConvolucionView view) {
        this.model = model;
        this.view  = view;
        init();
    }

    // =========================================================
    // INICIALIZACIÓN DE EVENTOS
    // =========================================================

    private void init() {
        view.btnCargar.addActionListener(e -> cargarImagen());
        view.btnAplicar.addActionListener(e -> aplicarFiltro());
    }

    // =========================================================
    // CARGA DE IMAGEN
    // =========================================================

    private void cargarImagen() {
        try {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(view) != JFileChooser.APPROVE_OPTION) return;
            imagen = ImageIO.read(fc.getSelectedFile());
            view.mostrarImagen(view.lblOriginal,     imagen);
            view.mostrarImagen(view.lblHistOriginal, histograma(imagen));
            view.txtInfo.setText(
                "Imagen cargada: " + fc.getSelectedFile().getName() +
                "\nDimensiones: " + imagen.getWidth() + " x " + imagen.getHeight() + " px"
            );
        } catch (Exception ex) {
            view.mostrarError("Error al cargar la imagen: " + ex.getMessage());
        }
    }

    // =========================================================
    // APLICAR FILTRO
    // =========================================================

    private void aplicarFiltro() {
        if (imagen == null) { view.mostrarError("Carga una imagen primero."); return; }

        String filtro = (String) view.comboFiltros.getSelectedItem();
        BufferedImage resultado = null;
        String descripcion = "";
        int umbral = (Integer) view.spnUmbral.getValue();

        switch (filtro) {

            // ---- Conversiones básicas ----

            case "Negativo":
                resultado   = model.negativo(imagen);
                descripcion = "NEGATIVO: Invierte cada canal de color (255 - valor). "
                            + "Los tonos claros se vuelven oscuros y viceversa.";
                break;

            case "Binarización (umbral 128)":
                resultado   = model.binarizar(imagen, 128);
                descripcion = "BINARIZACIÓN (umbral=128): Convierte la imagen a blanco/negro puro. "
                            + "Píxeles >= 128 → blanco, resto → negro.";
                break;

            // ---- Pasa Bajas — Suavizado ----

            case "PB | Promedio 3x3":
                resultado   = model.convolucion(imagen, model.promedio3x3());
                descripcion = "PROMEDIADOR 3x3: Reemplaza cada píxel con el promedio de su vecindad 3×3. "
                            + "Suaviza la imagen eliminando ruido de alta frecuencia. "
                            + "Efecto: ligero desenfoque (aberración).";
                break;

            case "PB | Promedio 5x5":
                resultado   = model.convolucion(imagen, model.promedio5x5());
                descripcion = "PROMEDIADOR 5x5: Igual que 3×3 pero con ventana mayor. "
                            + "Mayor suavizado y pérdida de detalle fino.";
                break;

            case "PB | Promedio 7x7":
                resultado   = model.convolucion(imagen, model.promedio7x7());
                descripcion = "PROMEDIADOR 7x7: Desenfoque fuerte. A mayor área de máscara, "
                            + "más operaciones (sumas y multiplicaciones) y más tiempo de cómputo.";
                break;

            case "PB | Gaussiano 3x3":
                resultado   = model.convolucion(imagen, model.gaussiano3x3());
                descripcion = "GAUSSIANO 3x3: Suavizado ponderado; el píxel central tiene más peso. "
                            + "Menos artefactos de cuadrícula que el promediador uniforme.";
                break;

            case "PB | Gaussiano 5x5":
                resultado   = model.convolucion(imagen, model.gaussiano5x5());
                descripcion = "GAUSSIANO 5x5: Versión más suave. Muy utilizado como paso previo "
                            + "a detectores de borde para reducir ruido.";
                break;

            // ---- Pasa Bajas — Definición ----

            case "PB | Definición Suave":
                resultado   = model.convolucion(imagen, model.definicionSuave());
                descripcion = "DEFINICIÓN SUAVE (sharpening): Realce leve de contornos. "
                            + "Incrementa el contraste local sin saturar. Máscara: centro=5, esquinas=+1/-2.";
                break;

            case "PB | Definición Media":
                resultado   = model.convolucion(imagen, model.definicionMedia());
                descripcion = "DEFINICIÓN MEDIA: Realce moderado. Máscara: centro=5, vecinos en cruz=-1. "
                            + "Equivale a sumar el Laplaciano 4 a la imagen original.";
                break;

            case "PB | Definición Fuerte":
                resultado   = model.convolucion(imagen, model.definicionFuerte());
                descripcion = "DEFINICIÓN FUERTE: Realce agresivo de bordes. "
                            + "Máscara: centro=9, todos los vecinos=-1. Puede saturar en imágenes ruidosas.";
                break;

            // ---- Pasa Altas — Laplaciano ----

            case "PA | Laplaciano 4 vecinos":
                resultado   = model.convolucion(imagen, model.laplaciano4());
                descripcion = "LAPLACIANO 4 vecinos: Operador de 2da derivada omnidireccional. "
                            + "Detecta bordes en todas las direcciones usando 4 vecinos (cruz). "
                            + "La suma de coeficientes es 0 → salida nula en zonas uniformes.";
                break;

            case "PA | Laplaciano 8 vecinos":
                resultado   = model.convolucion(imagen, model.laplaciano8());
                descripcion = "LAPLACIANO 8 vecinos: Igual que el de 4 pero incluye diagonales. "
                            + "Mayor sensibilidad a bordes diagonales. Más ruidoso en texturas.";
                break;

            // ---- Pasa Altas — Sobel ----

            case "PA | Sobel Hr":
                resultado   = model.convolucion(imagen, model.sobelX());
                descripcion = "SOBEL Hr (horizontal): Detecta bordes verticales. "
                            + "Realza cambios de izquierda a derecha. Pesos: 1,0,-1 / 2,0,-2 / 1,0,-1.";
                break;

            case "PA | Sobel Hc":
                resultado   = model.convolucion(imagen, model.sobelY());
                descripcion = "SOBEL Hc (vertical): Detecta bordes horizontales. "
                            + "Realza cambios de arriba hacia abajo.";
                break;

            case "PA | Sobel Combinado (Hr+Hc)":
                resultado   = model.convolucionDoble(imagen, model.sobelX(), model.sobelY());
                descripcion = "SOBEL COMBINADO: Magnitud = |Hr| + |Hc|. "
                            + "Detecta bordes en ambas direcciones simultáneamente. "
                            + "Resultado más completo que cada máscara por separado.";
                break;

            // ---- Pasa Altas — Prewitt ----

            case "PA | Prewitt Hr":
                resultado   = model.convolucion(imagen, model.prewittX());
                descripcion = "PREWITT Hr: Similar a Sobel pero sin el doble peso en la fila central. "
                            + "Detecta bordes verticales con sensibilidad uniforme en las filas.";
                break;

            case "PA | Prewitt Hc":
                resultado   = model.convolucion(imagen, model.prewittY());
                descripcion = "PREWITT Hc: Detecta bordes horizontales.";
                break;

            case "PA | Prewitt Combinado (Hr+Hc)":
                resultado   = model.convolucionDoble(imagen, model.prewittX(), model.prewittY());
                descripcion = "PREWITT COMBINADO: Magnitud = |Hr| + |Hc|. Detección bidireccional.";
                break;

            // ---- Pasa Altas — Roberts ----

            case "PA | Roberts Hr":
                resultado   = model.convolucion(imagen, model.robertsX());
                descripcion = "ROBERTS Hr: Operador más simple de gradiente. "
                            + "Diferencia diagonal; muy sensible al ruido.";
                break;

            case "PA | Roberts Hc":
                resultado   = model.convolucion(imagen, model.robertsY());
                descripcion = "ROBERTS Hc: Diferencia en la otra diagonal.";
                break;

            case "PA | Roberts Combinado (Hr+Hc)":
                resultado   = model.convolucionDoble(imagen, model.robertsX(), model.robertsY());
                descripcion = "ROBERTS COMBINADO: Magnitud bidireccional. "
                            + "Detector más rápido pero más ruidoso que Sobel/Prewitt.";
                break;

            // ---- Pasa Altas — Frei-Chen ----

            case "PA | Frei-Chen Hr":
                resultado   = model.convolucion(imagen, model.freiChenX());
                descripcion = "FREI-CHEN Hr: Variante con peso √2 en la fila central. "
                            + "Compensación isotrópica más precisa que Sobel.";
                break;

            case "PA | Frei-Chen Hc":
                resultado   = model.convolucion(imagen, model.freiChenY());
                descripcion = "FREI-CHEN Hc: Detecta bordes horizontales con ponderación isotrópica.";
                break;

            case "PA | Frei-Chen Combinado (Hr+Hc)":
                resultado   = model.convolucionDoble(imagen, model.freiChenX(), model.freiChenY());
                descripcion = "FREI-CHEN COMBINADO: Alta precisión en orientación de bordes diagonales.";
                break;

            // ---- Compás Prewitt ----

            case "COMP | Prewitt H1 (Este)":   resultado = model.convolucion(imagen, model.prewittCompassH1());
                descripcion = "PREWITT COMPÁS H1 (Este): Máxima respuesta a bordes orientados al Este."; break;
            case "COMP | Prewitt H2 (Noreste)": resultado = model.convolucion(imagen, model.prewittCompassH2());
                descripcion = "PREWITT COMPÁS H2 (Noreste): Sensible a bordes en dirección NE."; break;
            case "COMP | Prewitt H3 (Norte)":  resultado = model.convolucion(imagen, model.prewittCompassH3());
                descripcion = "PREWITT COMPÁS H3 (Norte): Detecta bordes hacia el Norte."; break;
            case "COMP | Prewitt H4 (Noroeste)": resultado = model.convolucion(imagen, model.prewittCompassH4());
                descripcion = "PREWITT COMPÁS H4 (Noroeste): Detecta bordes NW."; break;
            case "COMP | Prewitt H5 (Oeste)":  resultado = model.convolucion(imagen, model.prewittCompassH5());
                descripcion = "PREWITT COMPÁS H5 (Oeste): Detecta bordes hacia el Oeste."; break;
            case "COMP | Prewitt H6 (Suroeste)": resultado = model.convolucion(imagen, model.prewittCompassH6());
                descripcion = "PREWITT COMPÁS H6 (Suroeste): Detecta bordes SW."; break;
            case "COMP | Prewitt H7 (Sur)":    resultado = model.convolucion(imagen, model.prewittCompassH7());
                descripcion = "PREWITT COMPÁS H7 (Sur): Detecta bordes hacia el Sur."; break;
            case "COMP | Prewitt H8 (Sureste)": resultado = model.convolucion(imagen, model.prewittCompassH8());
                descripcion = "PREWITT COMPÁS H8 (Sureste): Detecta bordes SE."; break;

            case "COMP | Prewitt Suma (8 dirs)":
                resultado = model.compassSuma(imagen, new double[][][]{
                    model.prewittCompassH1(), model.prewittCompassH2(),
                    model.prewittCompassH3(), model.prewittCompassH4(),
                    model.prewittCompassH5(), model.prewittCompassH6(),
                    model.prewittCompassH7(), model.prewittCompassH8()});
                descripcion = "PREWITT COMPÁS SUMA: Acumula la respuesta de las 8 direcciones. "
                            + "Mayor cobertura de bordes pero puede saturar."; break;

            case "COMP | Prewitt OR (8 dirs)":
                resultado = model.compassOR(imagen, new double[][][]{
                    model.prewittCompassH1(), model.prewittCompassH2(),
                    model.prewittCompassH3(), model.prewittCompassH4(),
                    model.prewittCompassH5(), model.prewittCompassH6(),
                    model.prewittCompassH7(), model.prewittCompassH8()});
                descripcion = "PREWITT COMPÁS OR (máximo): Toma el máximo de las 8 respuestas. "
                            + "Equivale al OR lógico; bordes definidos sin saturación."; break;

            // ---- Compás Kirsch ----

            case "COMP | Kirsch H1 (Este)":    resultado = model.convolucion(imagen, model.kirschH1());
                descripcion = "KIRSCH H1 (Este): Pesos mayores (5/-3) dan más contraste que Prewitt."; break;
            case "COMP | Kirsch H2 (Noreste)": resultado = model.convolucion(imagen, model.kirschH2());
                descripcion = "KIRSCH H2 (Noreste)."; break;
            case "COMP | Kirsch H3 (Norte)":   resultado = model.convolucion(imagen, model.kirschH3());
                descripcion = "KIRSCH H3 (Norte)."; break;
            case "COMP | Kirsch H4 (Noroeste)": resultado = model.convolucion(imagen, model.kirschH4());
                descripcion = "KIRSCH H4 (Noroeste)."; break;
            case "COMP | Kirsch H5 (Oeste)":   resultado = model.convolucion(imagen, model.kirschH5());
                descripcion = "KIRSCH H5 (Oeste)."; break;
            case "COMP | Kirsch H6 (Suroeste)": resultado = model.convolucion(imagen, model.kirschH6());
                descripcion = "KIRSCH H6 (Suroeste)."; break;
            case "COMP | Kirsch H7 (Sur)":     resultado = model.convolucion(imagen, model.kirschH7());
                descripcion = "KIRSCH H7 (Sur)."; break;
            case "COMP | Kirsch H8 (Sureste)": resultado = model.convolucion(imagen, model.kirschH8());
                descripcion = "KIRSCH H8 (Sureste)."; break;

            case "COMP | Kirsch Suma (8 dirs)":
                resultado = model.compassSuma(imagen, new double[][][]{
                    model.kirschH1(), model.kirschH2(), model.kirschH3(), model.kirschH4(),
                    model.kirschH5(), model.kirschH6(), model.kirschH7(), model.kirschH8()});
                descripcion = "KIRSCH SUMA: Respuesta acumulada de las 8 máscaras Kirsch."; break;

            case "COMP | Kirsch OR (8 dirs)":
                resultado = model.compassOR(imagen, new double[][][]{
                    model.kirschH1(), model.kirschH2(), model.kirschH3(), model.kirschH4(),
                    model.kirschH5(), model.kirschH6(), model.kirschH7(), model.kirschH8()});
                descripcion = "KIRSCH OR: Máximo de las 8 respuestas Kirsch. "
                            + "Kirsch tiende a producir bordes más gruesos y contrastados que Prewitt."; break;

            // ---- Compás Robinson 3-nivel ----

            case "COMP | Robinson3 H1 (Este)":    resultado = model.convolucion(imagen, model.robinson3H1());
                descripcion = "ROBINSON 3-nivel H1 (Este)."; break;
            case "COMP | Robinson3 H2 (Noreste)": resultado = model.convolucion(imagen, model.robinson3H2());
                descripcion = "ROBINSON 3-nivel H2 (Noreste)."; break;
            case "COMP | Robinson3 H3 (Norte)":   resultado = model.convolucion(imagen, model.robinson3H3());
                descripcion = "ROBINSON 3-nivel H3 (Norte)."; break;
            case "COMP | Robinson3 H4 (Noroeste)": resultado = model.convolucion(imagen, model.robinson3H4());
                descripcion = "ROBINSON 3-nivel H4 (Noroeste)."; break;
            case "COMP | Robinson3 H5 (Oeste)":   resultado = model.convolucion(imagen, model.robinson3H5());
                descripcion = "ROBINSON 3-nivel H5 (Oeste)."; break;
            case "COMP | Robinson3 H6 (Suroeste)": resultado = model.convolucion(imagen, model.robinson3H6());
                descripcion = "ROBINSON 3-nivel H6 (Suroeste)."; break;
            case "COMP | Robinson3 H7 (Sur)":     resultado = model.convolucion(imagen, model.robinson3H7());
                descripcion = "ROBINSON 3-nivel H7 (Sur)."; break;
            case "COMP | Robinson3 H8 (Sureste)": resultado = model.convolucion(imagen, model.robinson3H8());
                descripcion = "ROBINSON 3-nivel H8 (Sureste)."; break;

            case "COMP | Robinson3 Suma (8 dirs)":
                resultado = model.compassSuma(imagen, new double[][][]{
                    model.robinson3H1(), model.robinson3H2(), model.robinson3H3(), model.robinson3H4(),
                    model.robinson3H5(), model.robinson3H6(), model.robinson3H7(), model.robinson3H8()});
                descripcion = "ROBINSON 3-nivel SUMA: Acumulado de 8 dirs."; break;

            case "COMP | Robinson3 OR (8 dirs)":
                resultado = model.compassOR(imagen, new double[][][]{
                    model.robinson3H1(), model.robinson3H2(), model.robinson3H3(), model.robinson3H4(),
                    model.robinson3H5(), model.robinson3H6(), model.robinson3H7(), model.robinson3H8()});
                descripcion = "ROBINSON 3-nivel OR: Máximo de 8 dirs. "
                            + "Coeficientes 0/1/−1 dan respuesta más tenue que Kirsch."; break;

            // ---- Compás Robinson 5-nivel ----

            case "COMP | Robinson5 H1 (Este)":    resultado = model.convolucion(imagen, model.robinson5H1());
                descripcion = "ROBINSON 5-nivel H1 (Este): Pesos 0/1/2/−1/−2."; break;
            case "COMP | Robinson5 H2 (Noreste)": resultado = model.convolucion(imagen, model.robinson5H2());
                descripcion = "ROBINSON 5-nivel H2 (Noreste)."; break;
            case "COMP | Robinson5 H3 (Norte)":   resultado = model.convolucion(imagen, model.robinson5H3());
                descripcion = "ROBINSON 5-nivel H3 (Norte)."; break;
            case "COMP | Robinson5 H4 (Noroeste)": resultado = model.convolucion(imagen, model.robinson5H4());
                descripcion = "ROBINSON 5-nivel H4 (Noroeste)."; break;
            case "COMP | Robinson5 H5 (Oeste)":   resultado = model.convolucion(imagen, model.robinson5H5());
                descripcion = "ROBINSON 5-nivel H5 (Oeste)."; break;
            case "COMP | Robinson5 H6 (Suroeste)": resultado = model.convolucion(imagen, model.robinson5H6());
                descripcion = "ROBINSON 5-nivel H6 (Suroeste)."; break;
            case "COMP | Robinson5 H7 (Sur)":     resultado = model.convolucion(imagen, model.robinson5H7());
                descripcion = "ROBINSON 5-nivel H7 (Sur)."; break;
            case "COMP | Robinson5 H8 (Sureste)": resultado = model.convolucion(imagen, model.robinson5H8());
                descripcion = "ROBINSON 5-nivel H8 (Sureste)."; break;

            case "COMP | Robinson5 Suma (8 dirs)":
                resultado = model.compassSuma(imagen, new double[][][]{
                    model.robinson5H1(), model.robinson5H2(), model.robinson5H3(), model.robinson5H4(),
                    model.robinson5H5(), model.robinson5H6(), model.robinson5H7(), model.robinson5H8()});
                descripcion = "ROBINSON 5-nivel SUMA."; break;

            case "COMP | Robinson5 OR (8 dirs)":
                resultado = model.compassOR(imagen, new double[][][]{
                    model.robinson5H1(), model.robinson5H2(), model.robinson5H3(), model.robinson5H4(),
                    model.robinson5H5(), model.robinson5H6(), model.robinson5H7(), model.robinson5H8()});
                descripcion = "ROBINSON 5-nivel OR: Mayor contraste que el de 3-nivel gracias "
                            + "a los pesos ±2. Bordes más definidos."; break;

            // ---- LoG ----

            case "LoG | Gauss 7x7":
                resultado   = model.convolucion(imagen, model.log7x7());
                descripcion = "LoG 7x7 (Laplaciano de Gaussiano): Detecta contornos de 2do orden. "
                            + "Omnidireccional. Localiza bordes al centro de zonas de cambio. "
                            + "Útil para segmentación (paso-por-cero). Máscara 7×7.";
                break;

            case "LoG | Gauss 9x9":
                resultado   = model.convolucion(imagen, model.log9x9());
                descripcion = "LoG 9x9: Mayor ventana → bordes más suavizados antes de derivar. "
                            + "Menos sensible al ruido que el 7×7. Máscara 9×9.";
                break;

            default:
                view.mostrarError("Filtro no reconocido: " + filtro);
                return;
        }

        // ---- MOSTRAR RESULTADO ----
        if (resultado != null) {
            if (view.chkBinarizar.isSelected()) {
                resultado = model.binarizar(resultado, umbral);
            }
            view.mostrarImagen(view.lblResultado,     resultado);
            view.mostrarImagen(view.lblHistResultado, histograma(resultado));
            view.txtInfo.setText(
                "Filtro aplicado: " + filtro + "\n\n" + descripcion
            );
        }
    }

    // =========================================================
    // GENERACIÓN DEL HISTOGRAMA
    // =========================================================

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

        // Fondo gris claro para la cuadrícula
        g.setColor(new Color(230, 230, 230));
        for (int i = 0; i <= 4; i++) {
            int yLine = H - 10 - i * (H - 10) / 4;
            g.drawLine(0, yLine, W, yLine);
        }

        g.setColor(new Color(30, 100, 200));
        for (int i = 0; i < 256; i++) {
            int bar = (int)((double) hist[i] / max * (H - 10));
            g.drawLine(i, H - 1, i, H - 1 - bar);
        }
        // Eje X
        g.setColor(Color.BLACK);
        g.drawLine(0, H - 1, W - 1, H - 1);
        g.dispose();
        return out;
    }
}
package controlador;

import modelo.ConvolucionModelo;
import vista.ConvolucionVista;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class ConvolucionControlador {
    private final ConvolucionModelo modelo;
    private final ConvolucionVista vista;
    private BufferedImage imagen;

    public ConvolucionControlador(ConvolucionModelo modelo, ConvolucionVista vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.btnCargar.addActionListener(e -> cargar());
        vista.btnAplicar.addActionListener(e -> aplicar());
        vista.comboFiltros.addActionListener(e -> {
            String f = (String) vista.comboFiltros.getSelectedItem();
            boolean mostrar = f.contains("Sobel") || f.contains("Prewitt") || f.contains("Roberts")
                || f.contains("Frei-Chen") || f.contains("Laplaciano") || f.contains("Kirsch")
                || f.contains("Robinson") || f.contains("LoG");
            vista.lblUmbral.setVisible(mostrar);
            vista.spnUmbral.setVisible(mostrar);
        });
    }

    private void cargar() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) != JFileChooser.APPROVE_OPTION) return;
        try {
            imagen = ImageIO.read(fc.getSelectedFile());
            vista.mostrarOriginal(imagen);
            vista.mostrarHistOriginal(ImageUtils.generateHistogram(imagen));
            vista.txtInfo.setText("Imagen cargada: " + fc.getSelectedFile().getName()
                + "\nDimensiones: " + imagen.getWidth() + " x " + imagen.getHeight() + " px");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void aplicar() {
        if (imagen == null) { JOptionPane.showMessageDialog(vista, "Carga una imagen primero."); return; }
        String filtro = (String) vista.comboFiltros.getSelectedItem();
        BufferedImage resultado = null;
        String descripcion = "";
        int umbral = (Integer) vista.spnUmbral.getValue();

        switch (filtro) {
            case "Negativo":
                resultado = modelo.negativo(imagen);
                descripcion = "NEGATIVO: Invierte cada canal (255 - valor).";
                break;
            case "Binarizacion (umbral 128)":
                resultado = modelo.binarizar(imagen, 128);
                descripcion = "BINARIZACION: Convierte a blanco/negro puro.";
                break;
            case "PB | Promedio 3x3": resultado = modelo.convolucion(imagen, modelo.promedio3x3()); descripcion = "PROMEDIO 3x3: Suavizado por promedio."; break;
            case "PB | Promedio 5x5": resultado = modelo.convolucion(imagen, modelo.promedio5x5()); descripcion = "PROMEDIO 5x5: Mayor suavizado."; break;
            case "PB | Promedio 7x7": resultado = modelo.convolucion(imagen, modelo.promedio7x7()); descripcion = "PROMEDIO 7x7: Desenfoque fuerte."; break;
            case "PB | Gaussiano 3x3": resultado = modelo.convolucion(imagen, modelo.gaussiano3x3()); descripcion = "GAUSS 3x3: Suavizado ponderado."; break;
            case "PB | Gaussiano 5x5": resultado = modelo.convolucion(imagen, modelo.gaussiano5x5()); descripcion = "GAUSS 5x5: Suavizado amplio."; break;
            case "PB | Definicion Suave": resultado = modelo.convolucion(imagen, modelo.definicionSuave()); descripcion = "DEFINICION SUAVE: Realce leve."; break;
            case "PB | Definicion Media": resultado = modelo.convolucion(imagen, modelo.definicionMedia()); descripcion = "DEFINICION MEDIA: Realce moderado."; break;
            case "PB | Definicion Fuerte": resultado = modelo.convolucion(imagen, modelo.definicionFuerte()); descripcion = "DEFINICION FUERTE: Realce agresivo."; break;
            case "PA | Laplaciano 4 vecinos": resultado = modelo.convolucion(imagen, modelo.laplaciano4()); descripcion = "LAPLACIANO 4: Detector omnidireccional."; break;
            case "PA | Laplaciano 8 vecinos": resultado = modelo.convolucion(imagen, modelo.laplaciano8()); descripcion = "LAPLACIANO 8: Incluye diagonales."; break;
            case "PA | Sobel Hr": resultado = modelo.convolucion(imagen, modelo.sobelX()); descripcion = "SOBEL Hr: Bordes verticales."; break;
            case "PA | Sobel Hc": resultado = modelo.convolucion(imagen, modelo.sobelY()); descripcion = "SOBEL Hc: Bordes horizontales."; break;
            case "PA | Sobel Combinado (Hr+Hc)": resultado = modelo.convolucionDoble(imagen, modelo.sobelX(), modelo.sobelY()); descripcion = "SOBEL Combinado: Bidireccional."; break;
            case "PA | Prewitt Hr": resultado = modelo.convolucion(imagen, modelo.prewittX()); descripcion = "PREWITT Hr: Bordes verticales."; break;
            case "PA | Prewitt Hc": resultado = modelo.convolucion(imagen, modelo.prewittY()); descripcion = "PREWITT Hc: Bordes horizontales."; break;
            case "PA | Prewitt Combinado (Hr+Hc)": resultado = modelo.convolucionDoble(imagen, modelo.prewittX(), modelo.prewittY()); descripcion = "PREWITT Combinado."; break;
            case "PA | Roberts Hr": resultado = modelo.convolucion(imagen, modelo.robertsX()); descripcion = "ROBERTS Hr."; break;
            case "PA | Roberts Hc": resultado = modelo.convolucion(imagen, modelo.robertsY()); descripcion = "ROBERTS Hc."; break;
            case "PA | Roberts Combinado (Hr+Hc)": resultado = modelo.convolucionDoble(imagen, modelo.robertsX(), modelo.robertsY()); descripcion = "ROBERTS Combinado."; break;
            case "PA | Frei-Chen Hr": resultado = modelo.convolucion(imagen, modelo.freiChenX()); descripcion = "FREI-CHEN Hr."; break;
            case "PA | Frei-Chen Hc": resultado = modelo.convolucion(imagen, modelo.freiChenY()); descripcion = "FREI-CHEN Hc."; break;
            case "PA | Frei-Chen Combinado (Hr+Hc)": resultado = modelo.convolucionDoble(imagen, modelo.freiChenX(), modelo.freiChenY()); descripcion = "FREI-CHEN Combinado."; break;
            case "COMP | Prewitt H1 (Este)": resultado = modelo.convolucion(imagen, modelo.prewittCompassH1()); descripcion = "PREWITT COMPAS H1 (Este)."; break;
            case "COMP | Prewitt H2 (Noreste)": resultado = modelo.convolucion(imagen, modelo.prewittCompassH2()); descripcion = "PREWITT COMPAS H2 (Noreste)."; break;
            case "COMP | Prewitt H3 (Norte)": resultado = modelo.convolucion(imagen, modelo.prewittCompassH3()); descripcion = "PREWITT COMPAS H3 (Norte)."; break;
            case "COMP | Prewitt H4 (Noroeste)": resultado = modelo.convolucion(imagen, modelo.prewittCompassH4()); descripcion = "PREWITT COMPAS H4 (Noroeste)."; break;
            case "COMP | Prewitt H5 (Oeste)": resultado = modelo.convolucion(imagen, modelo.prewittCompassH5()); descripcion = "PREWITT COMPAS H5 (Oeste)."; break;
            case "COMP | Prewitt H6 (Suroeste)": resultado = modelo.convolucion(imagen, modelo.prewittCompassH6()); descripcion = "PREWITT COMPAS H6 (Suroeste)."; break;
            case "COMP | Prewitt H7 (Sur)": resultado = modelo.convolucion(imagen, modelo.prewittCompassH7()); descripcion = "PREWITT COMPAS H7 (Sur)."; break;
            case "COMP | Prewitt H8 (Sureste)": resultado = modelo.convolucion(imagen, modelo.prewittCompassH8()); descripcion = "PREWITT COMPAS H8 (Sureste)."; break;
            case "COMP | Prewitt Suma (8 dirs)":
                resultado = modelo.compassSuma(imagen, new double[][][]{
                    modelo.prewittCompassH1(), modelo.prewittCompassH2(), modelo.prewittCompassH3(), modelo.prewittCompassH4(),
                    modelo.prewittCompassH5(), modelo.prewittCompassH6(), modelo.prewittCompassH7(), modelo.prewittCompassH8()});
                descripcion = "PREWITT COMPAS SUMA: Acumula 8 dirs."; break;
            case "COMP | Prewitt OR (8 dirs)":
                resultado = modelo.compassOR(imagen, new double[][][]{
                    modelo.prewittCompassH1(), modelo.prewittCompassH2(), modelo.prewittCompassH3(), modelo.prewittCompassH4(),
                    modelo.prewittCompassH5(), modelo.prewittCompassH6(), modelo.prewittCompassH7(), modelo.prewittCompassH8()});
                descripcion = "PREWITT COMPAS OR: Maximo de 8 dirs."; break;
            case "COMP | Kirsch H1 (Este)": resultado = modelo.convolucion(imagen, modelo.kirschH1()); descripcion = "KIRSCH H1."; break;
            case "COMP | Kirsch H2 (Noreste)": resultado = modelo.convolucion(imagen, modelo.kirschH2()); descripcion = "KIRSCH H2."; break;
            case "COMP | Kirsch H3 (Norte)": resultado = modelo.convolucion(imagen, modelo.kirschH3()); descripcion = "KIRSCH H3."; break;
            case "COMP | Kirsch H4 (Noroeste)": resultado = modelo.convolucion(imagen, modelo.kirschH4()); descripcion = "KIRSCH H4."; break;
            case "COMP | Kirsch H5 (Oeste)": resultado = modelo.convolucion(imagen, modelo.kirschH5()); descripcion = "KIRSCH H5."; break;
            case "COMP | Kirsch H6 (Suroeste)": resultado = modelo.convolucion(imagen, modelo.kirschH6()); descripcion = "KIRSCH H6."; break;
            case "COMP | Kirsch H7 (Sur)": resultado = modelo.convolucion(imagen, modelo.kirschH7()); descripcion = "KIRSCH H7."; break;
            case "COMP | Kirsch H8 (Sureste)": resultado = modelo.convolucion(imagen, modelo.kirschH8()); descripcion = "KIRSCH H8."; break;
            case "COMP | Kirsch Suma (8 dirs)":
                resultado = modelo.compassSuma(imagen, new double[][][]{
                    modelo.kirschH1(), modelo.kirschH2(), modelo.kirschH3(), modelo.kirschH4(),
                    modelo.kirschH5(), modelo.kirschH6(), modelo.kirschH7(), modelo.kirschH8()});
                descripcion = "KIRSCH SUMA: Acumula 8 dirs."; break;
            case "COMP | Kirsch OR (8 dirs)":
                resultado = modelo.compassOR(imagen, new double[][][]{
                    modelo.kirschH1(), modelo.kirschH2(), modelo.kirschH3(), modelo.kirschH4(),
                    modelo.kirschH5(), modelo.kirschH6(), modelo.kirschH7(), modelo.kirschH8()});
                descripcion = "KIRSCH OR: Maximo de 8 dirs."; break;
            case "COMP | Robinson3 H1 (Este)": resultado = modelo.convolucion(imagen, modelo.robinson3H1()); descripcion = "ROBINSON3 H1."; break;
            case "COMP | Robinson3 H2 (Noreste)": resultado = modelo.convolucion(imagen, modelo.robinson3H2()); descripcion = "ROBINSON3 H2."; break;
            case "COMP | Robinson3 H3 (Norte)": resultado = modelo.convolucion(imagen, modelo.robinson3H3()); descripcion = "ROBINSON3 H3."; break;
            case "COMP | Robinson3 H4 (Noroeste)": resultado = modelo.convolucion(imagen, modelo.robinson3H4()); descripcion = "ROBINSON3 H4."; break;
            case "COMP | Robinson3 H5 (Oeste)": resultado = modelo.convolucion(imagen, modelo.robinson3H5()); descripcion = "ROBINSON3 H5."; break;
            case "COMP | Robinson3 H6 (Suroeste)": resultado = modelo.convolucion(imagen, modelo.robinson3H6()); descripcion = "ROBINSON3 H6."; break;
            case "COMP | Robinson3 H7 (Sur)": resultado = modelo.convolucion(imagen, modelo.robinson3H7()); descripcion = "ROBINSON3 H7."; break;
            case "COMP | Robinson3 H8 (Sureste)": resultado = modelo.convolucion(imagen, modelo.robinson3H8()); descripcion = "ROBINSON3 H8."; break;
            case "COMP | Robinson3 Suma (8 dirs)":
                resultado = modelo.compassSuma(imagen, new double[][][]{
                    modelo.robinson3H1(), modelo.robinson3H2(), modelo.robinson3H3(), modelo.robinson3H4(),
                    modelo.robinson3H5(), modelo.robinson3H6(), modelo.robinson3H7(), modelo.robinson3H8()});
                descripcion = "ROBINSON3 SUMA."; break;
            case "COMP | Robinson3 OR (8 dirs)":
                resultado = modelo.compassOR(imagen, new double[][][]{
                    modelo.robinson3H1(), modelo.robinson3H2(), modelo.robinson3H3(), modelo.robinson3H4(),
                    modelo.robinson3H5(), modelo.robinson3H6(), modelo.robinson3H7(), modelo.robinson3H8()});
                descripcion = "ROBINSON3 OR."; break;
            case "COMP | Robinson5 H1 (Este)": resultado = modelo.convolucion(imagen, modelo.robinson5H1()); descripcion = "ROBINSON5 H1."; break;
            case "COMP | Robinson5 H2 (Noreste)": resultado = modelo.convolucion(imagen, modelo.robinson5H2()); descripcion = "ROBINSON5 H2."; break;
            case "COMP | Robinson5 H3 (Norte)": resultado = modelo.convolucion(imagen, modelo.robinson5H3()); descripcion = "ROBINSON5 H3."; break;
            case "COMP | Robinson5 H4 (Noroeste)": resultado = modelo.convolucion(imagen, modelo.robinson5H4()); descripcion = "ROBINSON5 H4."; break;
            case "COMP | Robinson5 H5 (Oeste)": resultado = modelo.convolucion(imagen, modelo.robinson5H5()); descripcion = "ROBINSON5 H5."; break;
            case "COMP | Robinson5 H6 (Suroeste)": resultado = modelo.convolucion(imagen, modelo.robinson5H6()); descripcion = "ROBINSON5 H6."; break;
            case "COMP | Robinson5 H7 (Sur)": resultado = modelo.convolucion(imagen, modelo.robinson5H7()); descripcion = "ROBINSON5 H7."; break;
            case "COMP | Robinson5 H8 (Sureste)": resultado = modelo.convolucion(imagen, modelo.robinson5H8()); descripcion = "ROBINSON5 H8."; break;
            case "COMP | Robinson5 Suma (8 dirs)":
                resultado = modelo.compassSuma(imagen, new double[][][]{
                    modelo.robinson5H1(), modelo.robinson5H2(), modelo.robinson5H3(), modelo.robinson5H4(),
                    modelo.robinson5H5(), modelo.robinson5H6(), modelo.robinson5H7(), modelo.robinson5H8()});
                descripcion = "ROBINSON5 SUMA."; break;
            case "COMP | Robinson5 OR (8 dirs)":
                resultado = modelo.compassOR(imagen, new double[][][]{
                    modelo.robinson5H1(), modelo.robinson5H2(), modelo.robinson5H3(), modelo.robinson5H4(),
                    modelo.robinson5H5(), modelo.robinson5H6(), modelo.robinson5H7(), modelo.robinson5H8()});
                descripcion = "ROBINSON5 OR."; break;
            case "LoG | Gauss 7x7": resultado = modelo.convolucion(imagen, modelo.log7x7()); descripcion = "LoG 7x7: Laplaciano de Gaussiano."; break;
            case "LoG | Gauss 9x9": resultado = modelo.convolucion(imagen, modelo.log9x9()); descripcion = "LoG 9x9: Mayor ventana."; break;
        }

        if (resultado != null) {
            if (vista.chkBinarizar.isSelected()) resultado = modelo.binarizar(resultado, umbral);
            vista.mostrarResultado(resultado);
            vista.mostrarHistResultado(ImageUtils.generateHistogram(resultado));
            vista.txtInfo.setText("Filtro aplicado: " + filtro + "\n\n" + descripcion);
        }
    }
}

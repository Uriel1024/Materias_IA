package controlador;

import modelo.FiltrosNoLinealesModelo;
import vista.FiltrosNoLinealesVista;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class FiltrosNoLinealesControlador {
    private final FiltrosNoLinealesModelo modelo;
    private final FiltrosNoLinealesVista vista;
    private BufferedImage imagen;

    public FiltrosNoLinealesControlador(FiltrosNoLinealesModelo modelo, FiltrosNoLinealesVista vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.btnCargar.addActionListener(e -> cargar());
        vista.btnAplicar.addActionListener(e -> aplicar());
    }

    private void cargar() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(vista) != JFileChooser.APPROVE_OPTION) return;
        try {
            imagen = ImageIO.read(fc.getSelectedFile());
            vista.mostrarOriginal(imagen);
            vista.mostrarHistOriginal(ImageUtils.generateHistogram(imagen));
            vista.txtInfo.setText("Imagen cargada: " + fc.getSelectedFile().getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void aplicar() {
        if (imagen == null) { JOptionPane.showMessageDialog(vista, "Carga una imagen primero."); return; }
        String filtro = (String) vista.comboFiltros.getSelectedItem();
        int maskSize = (Integer) vista.spnMask.getValue();
        double paramP = (Double) vista.spnParamP.getValue();
        BufferedImage resultado = null;
        String descripcion = "";

        switch (filtro) {
            case "Filtro de la Mediana":
                resultado = modelo.filtroMediana(imagen, maskSize);
                descripcion = "MEDIANA: Excelente para ruido Sal y Pimienta.";
                break;
            case "Filtro Alfa-Trimmed":
                resultado = modelo.filtroAlfaTrimmed(imagen, maskSize, (int) paramP);
                descripcion = "ALFA-TRIMMED: Elimina " + (int)paramP + " valores extremos.";
                break;
            case "Filtro del Maximo":
                resultado = modelo.filtroMaximo(imagen, maskSize);
                descripcion = "MAXIMO: Bueno contra ruido negativo.";
                break;
            case "Filtro del Minimo":
                resultado = modelo.filtroMinimo(imagen, maskSize);
                descripcion = "MINIMO: Bueno contra ruido positivo.";
                break;
            case "Filtro del Punto Medio":
                resultado = modelo.filtroPuntoMedio(imagen, maskSize);
                descripcion = "PUNTO MEDIO: Ideal para ruido Gaussiano simetrico.";
                break;
            case "Filtro Inferior Armonico":
                resultado = modelo.filtroInferiorArmonico(imagen, maskSize);
                descripcion = "ARMONICO: Eficaz contra ruido Gamma.";
                break;
            case "Filtro Contra Armonico":
                resultado = modelo.filtroContraArmonico(imagen, maskSize, paramP);
                descripcion = "CONTRA ARMONICO P=" + paramP + ". P>0 elimina pimienta, P<0 elimina sal.";
                break;
            case "Filtro Geometrico":
                resultado = modelo.filtroGeometrico(imagen, maskSize);
                descripcion = "GEOMETRICO: Raiz N del producto.";
                break;
            case "Filtro Maximo-Minimo":
                resultado = modelo.filtroMaxMin(imagen, maskSize);
                descripcion = "MAX-MIN: Sustituye por el mas cercano. Realza contornos.";
                break;
            case "Filtro Media Aritmetica":
                resultado = modelo.mediaAritmetica(imagen, maskSize);
                descripcion = "MEDIA ARITMETICA: Suaviza pero emborrona.";
                break;
        }
        if (resultado != null) {
            vista.mostrarResultado(resultado);
            vista.mostrarHistResultado(ImageUtils.generateHistogram(resultado));
            vista.txtInfo.setText("Filtro: " + filtro + " (" + maskSize + "x" + maskSize + ")\n" + descripcion);
        }
    }
}

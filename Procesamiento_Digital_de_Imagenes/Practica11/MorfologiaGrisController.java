import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class MorfologiaGrisController {

    private final MorfologiaGrisModel model;
    private final MorfologiaGrisView view;
    private BufferedImage imagen;

    public MorfologiaGrisController(MorfologiaGrisModel model, MorfologiaGrisView view) {
        this.model = model;
        this.view = view;
        init();
    }

    private void init() {
        view.btnCargar.addActionListener(e -> cargarImagen());
        view.btnAplicar.addActionListener(e -> aplicarOperacion());
        view.btnMandarAResultado.addActionListener(e -> usarResultadoComoOriginal());
    }

    private void cargarImagen() {
        try {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(view) != JFileChooser.APPROVE_OPTION) return;
            imagen = ImageIO.read(fc.getSelectedFile());
            
            // Convertimos inmediatamente a niveles de gris
            imagen = model.convertirEscalaGrises(imagen);
            view.mostrarImagen(view.lblOriginal, imagen);
            view.txtInfo.setText("Imagen cargada y convertida a Niveles de Gris: " + fc.getSelectedFile().getName());
        } catch (Exception ex) {
            view.mostrarError("Error al cargar la imagen: " + ex.getMessage());
        }
    }

    private void aplicarOperacion() {
        if (imagen == null) { view.mostrarError("Carga una imagen primero."); return; }

        String operacion = (String) view.comboOperaciones.getSelectedItem();
        BufferedImage resultado = null;
        String descripcion = "";

        switch (operacion) {
            case "Erosión Gris":
                resultado = model.erosionGris(imagen);
                descripcion = "EROSIÓN (Ínfimo): Reemplaza cada píxel con el valor MÍNIMO de la vecindad. Oscurece y elimina puntos brillantes de SAL.";
                break;
            case "Dilatación Gris":
                resultado = model.dilatacionGris(imagen);
                descripcion = "DILATACIÓN (Supremo): Reemplaza cada píxel con el valor MÁXIMO de la vecindad. Aclara y elimina puntos oscuros de PIMIENTA.";
                break;
            case "Apertura Gris":
                resultado = model.aperturaGris(imagen);
                descripcion = "APERTURA (Erosión -> Dilatación): Filtro ideal para remover ruido de SAL (impulsos brillantes) preservando las formas grises.";
                break;
            case "Clausura Gris":
                resultado = model.clausuraGris(imagen);
                descripcion = "CLAUSURA (Dilatación -> Erosión): Filtro ideal para remover ruido de PIMIENTA (impulsos oscuros) preservando las formas grises.";
                break;
            case "Añadir Ruido Sal":
                resultado = model.aplicarRuidoSal(imagen, 0.05); // 5% de ruido blanco
                descripcion = "RUIDO SAL AÑADIDO (Puntos blancos sobre la escala de grises). Según la teoría, aplique APERTURA GRIS para removerlo.";
                break;
            case "Añadir Ruido Pimienta":
                resultado = model.aplicarRuidoPimienta(imagen, 0.05); // 5% de ruido negro
                descripcion = "RUIDO PIMIENTA AÑADIDO (Puntos negros sobre la escala de grises). Según la teoría, aplique CLAUSURA GRIS para removerlo.";
                break;
        }

        if (resultado != null) {
            view.mostrarImagen(view.lblResultado, resultado);
            view.imagenResultadoActual = resultado;
            view.txtInfo.setText("Operación aplicada: " + operacion + "\nExplicación: " + descripcion);
        }
    }

    private void usarResultadoComoOriginal() {
        if (view.imagenResultadoActual != null) {
            imagen = view.imagenResultadoActual;
            view.mostrarImagen(view.lblOriginal, imagen);
            view.txtInfo.setText("El resultado se movió al origen. Listo para aplicar el filtro de limpieza.");
        } else {
            view.mostrarError("No hay un resultado procesado para mover.");
        }
    }
}
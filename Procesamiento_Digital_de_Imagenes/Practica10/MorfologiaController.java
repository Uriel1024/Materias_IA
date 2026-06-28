import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class MorfologiaController {

    private final MorfologiaModel model;
    private final MorfologiaView view;
    private BufferedImage imagen;

    public MorfologiaController(MorfologiaModel model, MorfologiaView view) {
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
            
            // Forzar binarización visual para el usuario
            imagen = model.convertirBinaria(imagen);
            view.mostrarImagen(view.lblOriginal, imagen);
            view.txtInfo.setText("Imagen cargada y binarizada: " + fc.getSelectedFile().getName());
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
            case "Erosión":
                resultado = model.erosion(imagen);
                descripcion = "EROSIÓN: Adelgaza objetos blancos. Útil para remover ruido de SAL (puntos blancos aislados).";
                break;
            case "Dilatación":
                resultado = model.dilatacion(imagen);
                descripcion = "DILATACIÓN: Engrosa objetos blancos. Útil para rellenar agujeros o remover ruido de PIMIENTA (puntos negros).";
                break;
            case "Apertura":
                resultado = model.apertura(imagen);
                descripcion = "APERTURA (Erosión -> Dilatación): Remueve ruido de SAL sin afectar drásticamente el tamaño del objeto original.";
                break;
            case "Clausura":
                resultado = model.clausura(imagen);
                descripcion = "CLAUSURA (Dilatación -> Erosión): Remueve ruido de PIMIENTA cerrando pequeños agujeros negros.";
                break;
            case "Añadir Ruido Sal":
                resultado = model.aplicarRuidoSal(imagen, 0.05); // 5% de ruido
                descripcion = "RUIDO SAL AÑADIDO (5%). Para limpiarlo usa Apertura o Erosión.";
                break;
            case "Añadir Ruido Pimienta":
                resultado = model.aplicarRuidoPimienta(imagen, 0.05); // 5% de ruido
                descripcion = "RUIDO PIMIENTA AÑADIDO (5%). Para limpiarlo usa Clausura o Dilatación.";
                break;
            case "Esqueletizado":
                resultado = model.esqueletizado(imagen);
                descripcion = "ESQUELETIZADO: Algoritmo de Zhang-Suen aplicado. Reduce la imagen a su esqueleto estructural.";
                break;
        }

        if (resultado != null) {
            view.mostrarImagen(view.lblResultado, resultado);
            view.imagenResultadoActual = resultado; // Guardar para el botón de retroalimentación
            view.txtInfo.setText("Operación aplicada: " + operacion + "\nTeoría: " + descripcion);
        }
    }

    private void usarResultadoComoOriginal() {
        if (view.imagenResultadoActual != null) {
            imagen = view.imagenResultadoActual;
            view.mostrarImagen(view.lblOriginal, imagen);
            view.txtInfo.setText("El resultado ahora es la imagen original. Listo para la siguiente operación.");
        } else {
            view.mostrarError("No hay un resultado para usar como imagen original.");
        }
    }
}
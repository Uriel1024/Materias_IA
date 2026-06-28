
package reactivos;

import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 *
 * @author macario
 */
public class Agente extends Thread
{
    private final String nombre;
    private int i;
    private int j;
    private final ImageIcon icon;
    private final int[][] matrix;
    private final JLabel tablero[][];
    private boolean bombaPlantada = false;
    private Clip clip;

    private int contadorSamples = 0; //contador de samples recogidos
    private JLabel casillaAnterior;

    Random aleatorio = new Random(System.currentTimeMillis());

    private static int naveFila = -1;
    private static int naveColumna = -1;

    public Agente(String nombre, ImageIcon icon, int[][] matrix, JLabel tablero[][])
    {
        this.nombre = nombre;
        this.icon = icon;
        this.matrix = matrix;
        this.tablero = tablero;

        
        this.i = aleatorio.nextInt(matrix.length);
        this.j = aleatorio.nextInt(matrix.length);
        tablero[i][j].setIcon(icon);        
    }
    
    @Override
    public void run()
    {
        int dirRow=1;
        int dirCol=1;


        while(true)
        {

            casillaAnterior = tablero[i][j];

            if(contadorSamples > 0){
                moverHaciaNave();
            } else {
                moverAleatoriamente();
            }

            actualizarPosicion();

            try
            {
               sleep(100+aleatorio.nextInt(100));
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace(System.out);
            }
        }

                      
    }

    private void moverHaciaNave(){
        if(naveFila == -1 || naveColumna == -1){
            moverAleatoriamente();
            return;
        }

        int dirFila = (naveFila > i) ? 1 : (naveFila < i)  ? -1 : 0;
        int dirColumna = (naveColumna > j) ? 1 : (naveColumna < j) ? -1 : 0;

        if(dirFila != 0 && dirColumna != 0){
            dirColumna = 0;
        }
        int newI = i + dirFila;
        int newJ = j + dirColumna;

        if(isPosicionValida(newI, newJ)){
            if(matrix[newI][newJ] == 3 && !(newI == naveFila && newJ == naveColumna)){
            contadorSamples++;
            System.out.println(nombre + "ha recogido un sample. Total: " + contadorSamples);
            matrix[newI][newJ] = 0;
            bombaPlantada = false;
            }
        i = newI;
        j = newJ;

        if(i == naveFila && j == naveColumna){
            if(contadorSamples > 0){
                contadorSamples--;
                System.out.println(nombre + "ha dejado un sample. Total: " + contadorSamples);
                plantada();
                bombaPlantada = true;
            }
        }
            actualizarPosicion();
        } else {
            moverAleatoriamente();
        }
    }


     private void plantada(){
        if(bombaPlantada){
            try {
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("audio/bomb.wav"));
                clip = AudioSystem.getClip();
                clip.open(inputStream);
                clip.start();
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        }    

    }   //funcion para que diga bomb has been planted  

    private boolean isPosicionValida(int newI, int newJ){
        if(newI < 0 || newI >= matrix.length || newJ < 0 || newJ >= matrix[0].length){
            return false;
        }
        return matrix[newI][newJ] != 1;
    }
    private void moverAleatoriamente(){
        int direccion = aleatorio.nextInt(5);
        int newI = i;
        int newJ = j;

        switch(direccion){
            case 0: //norte
                newI = i > 0 ? i - 1 : i;
                break;
            case 1: //sur
                newI = i < matrix.length - 1 ? i + 1 : i;
                break;
            case 2: // este
                newJ = j > 0 ? j - 1 : j;
                break;
            case 3: //oeste
                newJ = j < matrix.length - 1 ? j + 1 : j;
                break;
        }
        if(isPosicionValida(newI, newJ) && (newI != (casillaAnterior.getY() - 10) /50 || newJ != (casillaAnterior.getX() -10) / 50)){
            if(matrix[newI][newJ] == 3){
                contadorSamples++;
                System.out.println(nombre + " ha recogido un sample. Total: " + contadorSamples);
                matrix[newI][newJ] = 0;
            }
            casillaAnterior = tablero[i][j];
            i = newI;
            j = newJ;

            actualizarPosicion();
        } else {
            moverAleatoriamente();
        }
    }

    private void agregarPosicionVisitada(int i, int j){
        casillaAnterior = tablero[i][j];

    }

    public synchronized void actualizarPosicion()
    {
        JLabel casillaActual = tablero[i][j];

        if(casillaAnterior != null){
            int filaAnterior = (casillaAnterior.getY() - 10) /50;
            int columnaAnterior = (casillaAnterior.getX() -10) / 50;

            if(matrix[filaAnterior][columnaAnterior] != 2){
                casillaAnterior.setIcon(null);
            }
        }

        int filaActual = i;
        int columnaActual = j;

        if(matrix[filaActual][columnaActual] != 2){
            casillaActual.setIcon(icon);
        }

        casillaAnterior = casillaActual;

        System.out.println(nombre + " en -> Fila: " + filaActual + ", Columna: " + columnaActual);
    }
    public static void setNavePosicion(int fila, int columna){
        naveFila = fila;
        naveColumna = columna;
}
}

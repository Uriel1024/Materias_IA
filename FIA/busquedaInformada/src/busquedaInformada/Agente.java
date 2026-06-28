package busquedaInformada;

import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.util.List;

public class Agente extends Thread {
    private final String nombre;
    private int i;
    private int j;
    private final ImageIcon icon;
    private final int[][] matrix;
    private final JLabel tablero[][];
    private final ImageIcon torch;

    private int charged = 0;  
    private int contadorSamples = 0;

    private int mothershipX = -1;
    private int mothershipY = -1;

    private int sampleX = -1;
    private int sampleY = -1;

    // Coordenadas previas para dejar trail
    private int prevI, prevJ;

    private static int naveFila = -1;
    private static int naveColumna = -1;

    Random aleatorio = new Random(System.currentTimeMillis());

    public Agente(String nombre, ImageIcon icon, int[][] matrix, JLabel tablero[][], ImageIcon torch) {
        this.nombre = nombre;
        this.icon = icon;
        this.matrix = matrix;
        this.tablero = tablero;
        this.torch = torch;
        this.i = aleatorio.nextInt(matrix.length);
        this.j = aleatorio.nextInt(matrix[0].length);
        this.prevI = i;
        this.prevJ = j;

        tablero[i][j].setIcon(icon);
    }

    @Override
    public void run() {
        // Configura la mothership si ya fue establecida
        findMothership();
        System.out.println(nombre + " inicia en -> Fila: " + i + ", Columna: " + j);

        // Chachareando
        while (true) {
            // Explora
            while (charged == 0) {
                if (matrix[i][j] == 3 || matrix[i][j] >= 10) {
                    charged = 1;
                    contadorSamples++;
                    sampleX = i;
                    sampleY = j;
                    if (matrix[i][j] >= 10) {
                        // Es un sampleCluster
                        matrix[i][j] -= 10;
                        if (matrix[i][j] == 0) {
                            tablero[i][j].setIcon(null);
                            System.out.println(nombre + " recogió la última unidad de un cluster sample en -> (" + i + ", " + j + ")");
                        } else {
                            // actualiza el ícono del sampleCluster
                            tablero[i][j].setIcon(Tablero.getClusterIcon(matrix[i][j]));
                            System.out.println(nombre + " recogió una unidad de un cluster sample en -> (" + i + ", " + j + ") Restantes: " + (matrix[i][j] / 10));
                        }
                    } else {
                        // sample normal
                        matrix[i][j] = 0;
                        tablero[i][j].setIcon(null);
                        System.out.println(nombre + " recogió un sample normal en -> (" + i + ", " + j + ")");
                    }
                    break;
                } else {
                    moverAleatoriamente();
                }
                actualizarPosicion(false); // Modo exploración
                try {
                    sleep(200 + aleatorio.nextInt(200));
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }

            // Regresar a la mothership usando el modo charged
            if (charged == 1) {
                System.out.println(nombre + " cargado, buscando camino a la mothership...");
                moveCharged();  // Cada movimiento en modo charged deja trail en celdas vacías
                System.out.println(nombre + " llegó a la mothership.");
                // Entrega el sample y se reinicia el estado.
                charged = 0;
                // Para evitar superposición en la mothership se realiza un movimiento aleatorio extra.
                moverAleatoriamente();
                actualizarPosicion(false);
            }
        }
    }

    public void findMothership() {
        if (naveFila != -1 && naveColumna != -1) {
            mothershipX = naveFila;
            mothershipY = naveColumna;
            System.out.println("Mothership configurada en: (" + mothershipX + ", " + mothershipY + ")");
        } else {
            System.out.println("Mothership no configurada mediante setNavePosicion.");
        }
    }

    // Regresa a la mothership usando A* y dejando trail en celdas vacías
    public void moveCharged() {
        BusquedaStar bStar = new BusquedaStar(matrix, i, j, mothershipX, mothershipY);
        List<Node> route = bStar.findPath();
        if (route != null) {
            System.out.println("Ruta encontrada:");
            for (Node node : route) {
                System.out.println("-> (" + node.x + ", " + node.y + ")");
                prevI = i;
                prevJ = j;
                i = node.x;
                j = node.y;
                actualizarPosicion(true); // En modo charged se deja trail (si la celda de origen está vacía)
                try {
                    sleep(200);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else {
            System.out.println("No se encontró ruta a la mothership.");
        }
    }


    private void moverAleatoriamente() {
        int[] dFila = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};

        for (int k = 0; k < 4; k++) {
            int newI = i + dFila[k];
            int newJ = j + dCol[k];
            if (posicionValida(newI, newJ) && matrix[newI][newJ] == 4) {
                prevI = i;
                prevJ = j;
                i = newI;
                j = newJ;
                System.out.println(nombre + " sigue trail en -> (" + i + ", " + j + ")");
                return;
            }
        }
        // EN caso de no chocar con un trail, entonces se moverá aleatoriamente
        while (true) {
            int dir = aleatorio.nextInt(4);
            int newI = i + dFila[dir];
            int newJ = j + dCol[dir];
            if (posicionValida(newI, newJ)) {
                prevI = i;
                prevJ = j;
                i = newI;
                j = newJ;
                break;
            }
        }
    }

    // Verifica que la nueva posición esté dentro del tablero y no sea obstáculo
    private boolean posicionValida(int newI, int newJ) {
        if (newI < 0 || newI >= matrix.length || newJ < 0 || newJ >= matrix[0].length) {
            return false;
        }
        return matrix[newI][newJ] != 1;
    }

    public synchronized void actualizarPosicion(boolean dejarRastro) {
        SwingUtilities.invokeLater(() -> {
            // Procesar celda de origen
            if (matrix[prevI][prevJ] == 2 || matrix[prevI][prevJ] == 1) {

            } else if (matrix[prevI][prevJ] == 3) {
                // Si es sample normal y ya se recogió, se elimina.
                if (charged == 1) {
                    matrix[prevI][prevJ] = 0;
                    tablero[prevI][prevJ].setIcon(null);
                }
            } else if (matrix[prevI][prevJ] >= 10) {
                //se actualiza con el ícono del según el valor restante
                if (matrix[prevI][prevJ] == 0) {
                    tablero[prevI][prevJ].setIcon(null);
                } else {
                    tablero[prevI][prevJ].setIcon(Tablero.getClusterIcon(matrix[prevI][prevJ]));
                }
            } else {
                // Si la celda estaba vacía o tenía trail
                if (dejarRastro && matrix[prevI][prevJ] == 0) {
                    matrix[prevI][prevJ] = 4;
                    tablero[prevI][prevJ].setIcon(torch);
                } else {
                    tablero[prevI][prevJ].setIcon(null);
                }
            }

            // Procesa  celda destino
            if (matrix[i][j] == 2) {
            } else {
                // Si la celda destino tenía trail entonces se elimina
                if (matrix[i][j] == 4) {
                    matrix[i][j] = 0;
                    tablero[i][j].setIcon(null);
                }
                tablero[i][j].setIcon(icon);
            }
            tablero[i][j].getParent().revalidate();
            tablero[i][j].getParent().repaint();
        });
        System.out.println(nombre + " se mueve a -> Fila: " + i + ", Columna: " + j);
    }

    // establece la posición de la nave
    public static void setNavePosicion(int fila, int columna) {
        naveFila = fila;
        naveColumna = columna;
        System.out.println("Posición de la mothership configurada a: (" + fila + ", " + columna + ")");
    }
}

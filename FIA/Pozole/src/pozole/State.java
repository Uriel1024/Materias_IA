package pozole;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author 
 */
public class State 
{
    private byte[][] board; 
    private int posI; // Fila del hueco
    private int posJ; // Columna del hueco
    private char movement='n';
    private State parent=null;
    

    //primer constructor, sirve para hacer      
    public State(String sts)
    {   
        int n=0;
        this.board = new byte[4][4]; //para que el this?
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            {   
                char c=sts.charAt(n);       
                board[i][j]=(byte)Character.getNumericValue(c); 
                if(board[i][j]==0)
                {
                    posI = i;
                    posJ = j;
                }
                n++;
            }
    }

    //la clase maneja sobrecarga de constructores
    public State (byte[][] board, char movement, State parent) 
    /*este constructor genera una nueva matriz a partir
    de un estado anterior */ 
    {
        this.movement = movement;
        this.board = new byte[4][4];
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            {
                this.board[i][j]=board[i][j];
                if(board[i][j]==0)
                {
                    posI = i;
                    posJ = j;
                }
            }
        this.parent = parent;
    }
    
    public void show()
    //solo muestra el movimiento de la matriz y la accion realizada
    {
        switch(movement)
        {
            case 'u' -> System.out.println("Move UP");
            case 'd' -> System.out.println("Move DOWN");
            case 'l' -> System.out.println("Move LEFT");
            case 'r' -> System.out.println("Move RIGHT");
            case 'n' -> System.out.println("START");
        }
        System.out.println("+-+-+-+");
        for(int i=0;i<4;i++)
        {   System.out.print("|");
            for(int j=0;j<4;j++)
            {
                System.out.print(board[i][j]+"|");
            }
            System.out.println("\n+-+-+-+");
        }
    }


    
    public void swap(int i,int j)
    //solo hace un cambio de variables xd
    {
        int aux=board[i][j];
        board[i][j]=0;
        board[posI][posJ]=(byte)aux;
        posI=i; // i y j tienen la posicion del espacio vacio 
        posJ=j;
    }
    
    
    // Función sucesor
    public List<State> getNeighbors()
    {
        List<State> neighbors =  new ArrayList<>(); //lista para guardar a los vecinos
        byte[][] newBoard = new byte[4][4]; // otra matriz para clonar y poder modificar
        
        // Clone board
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                newBoard[i][j]=this.board[i][j];
     
                       
        // Move up
        if(posI>0)
        {
            int newI = posI-1;
            State newState = new State(newBoard,'u', this); //crea un nuevo estado                 
            newState.swap(newI, posJ); // 
            neighbors.add(newState);                        
        }
        
        // Move down
        if(posI<3)
        {
            int newI = posI+1;
            State newState = new State(newBoard,'d', this);
            newState.swap(newI, posJ);
            neighbors.add(newState);        
        }
                
        // Move left
        if(posJ>0)
        {
            int newJ = posJ-1;
            State newState = new State(newBoard,'l', this);
            newState.swap(posI, newJ);
            neighbors.add(newState);
        }
        
        // Mover right
        if(posJ<3)
        {
            int newJ = posJ+1;
            State newState =  new State(newBoard,'r', this);                
            newState.swap(posI, newJ);
            neighbors.add(newState);
        }
                
        return neighbors;
    }
     
    public byte[][] getBoard()
    {
        return board;
    }
    
    // Test objetivo
    public boolean isGoal(State goal)
    //solo valida que la matriz actual y la meta sean igualaes para poder parar la busqueda
    {
        boolean success=true; 
        byte[][] goalBoard = goal.getBoard();
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            {
                if(goalBoard[i][j]!=board[i][j])
                {   
                    success = false;
                    break;
                }
            }
        return success;        
    }
    
    public int getI()
    {
        return posI;
    }
    
    public int getJ()
    {
        return posJ;
    }
    
    public State getParent()
    {
        return parent;
    }
    
    public char getMovement()
    {
        return movement;
    }
    
 /*    public boolean isEqual(State s)
    {
        boolean isEqual=true;
        byte[][] sBoard = s.getBoard();
        for(int i=0;i<4;i++)
            for(int j=0;j<   ;j++)
            {
                if(sBoard[i][j]!=board[i][j])
                {
                    isEqual = false;
                    break;
                }
            }
        return isEqual;        
    } 
    Este metodo es igual a isGoal, solo se hace 1 llamada en el tablero, es preferible reutilizar el metodo de is goal
    para ahorra recursos
    */ 
 }

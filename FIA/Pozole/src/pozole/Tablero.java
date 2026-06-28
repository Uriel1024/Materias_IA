package pozole;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayDeque;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSlider;

/**
 *
 * @author 
 */
public class Tablero extends JFrame
{
    private final JButton[][] jBoard = new JButton[4][4];   
    private final LinkedHashMap <String, BufferedImage> puzzle = new LinkedHashMap<>();
    private BufferedImage empty;
    private boolean depth = false;
    


    private final String start = "123456789abcd0ef";
    private final String goal  = "123456789abcdef0"; 
    

    private final JMenuItem solveB = new JMenuItem("Solve BFS");
    private final JMenuItem solveD = new JMenuItem("Solve DFS");
    private final JSlider velocity = new JSlider(1,200,1500);
    

    private long startTime; 
    private int totalNodes;
    private int totalLoops;
    private String thePath = "";
    
    
    public Tablero()  // Constructor
    {       
        initComponents();
    }
      
    private void initComponents() 
    {
        this.setTitle("8-Puzzle");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int width = pantalla.width;
        int height = pantalla.height;
        this.setBounds((width-650)/2,(height-700)/2,610,700 );


        velocity.setBounds(10,610,485,20);
        velocity.setInverted(true);
        
        JMenuBar mainMenu = new JMenuBar();
        JMenu    file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        
        mainMenu.add(file);
        file.add(solveB);
        file.add(solveD);
        file.add(exit);
        this.setJMenuBar(mainMenu);

        
        this.setLayout(null);
        this.imagePieces("imagenes/background.jpg");
        paintPieces();
        this.add(velocity);



        exit.addActionListener(evt -> gestionarExit(evt));  
        solveB.addActionListener(evt -> whichAlgorithm(evt)); 
        solveD.addActionListener(evt -> whichAlgorithm(evt));  
               
        // Handle the X-Button 
        class MyWindowAdapter extends WindowAdapter
        {
            @Override
            public void windowClosing(WindowEvent eventObject)
            {
		goodBye();
            }
        }
        addWindowListener(new MyWindowAdapter());       
    }
    
    private void goodBye()
    {
        int respuesta = JOptionPane.showConfirmDialog(rootPane, "Are you sure?","Exit",JOptionPane.YES_NO_OPTION);
        if(respuesta==JOptionPane.YES_OPTION) System.exit(0);
    }
        
    private void gestionarExit(ActionEvent e)
    {
        goodBye();
    }

   
   // Parte la imagen en piezas 
    private void imagePieces(String pathName)
    {
        try  // Bloque "try" por que hay una tarea de lectura de archivo
        {      
            BufferedImage buffer= ImageIO.read(new File(pathName));
            BufferedImage subImage;
            int n=0;
            for(int i=0;i<4;i++)
                for(int j=0;j<4;j++)
                {
                    subImage = buffer.getSubimage((j)*150, (i)*150, 150, 150);  // Extrae un fragmento de la imagen 
                    String k = goal.substring(n,n+1);
                    puzzle.put(k, subImage); // Almacena las piezas etiquetándolas con base en el estado final
                    n++;
                }
        }
        catch (Exception ex)
        {
            ex.printStackTrace(System.out);
        }
              
    } 
    
    public void paintPieces()
    {
        int n=0;
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            {
                String  k=start.substring(n,n+1);
                BufferedImage subImage = (BufferedImage) puzzle.get(k);   
                jBoard[i][j] = new JButton();
                jBoard[i][j].setBounds(j*150+1, i*150+1,150,150); // Calcula la posición del botón i,j
                this.add(jBoard[i][j]);                                                               
                if(!k.equals("0"))jBoard[i][j].setIcon(new ImageIcon(subImage));
                else empty = subImage;
                n++;                 
            }

    }
    
    
    private void whichAlgorithm(ActionEvent e)
    {
        if(e.getSource()==solveD)  
            solveDFS(); // En caso de que se trate de búsqueda en profundidad
        else
            solveBFS();
    }
    
    // Breadth First Search 
    
    private void solveBFS()
    {
        boolean success = false;
        totalNodes = 0;
        totalLoops = 0;
        State initialState = new State(start); /*constructor para generar la matriz de estado inicial, esta esta
        indefinida y depende completamente del atributo start */     
        State goalState   = new State(goal);
        /*constructor 1 para iniciar la matriz meta que queda representada de la siguiente manera por el metodo
        Character.getNumericValue(c); 
        [1 , 2 , 3 , 4]
        [5 , 6 , 7 , 8]
        [9, 10, 11 ,12] A = 10 ... , 15 = E es en codigo hexadecimal    
        [13 , 14, 15,0]
         */
        ArrayList<State> visited = new ArrayList <>();
        ArrayDeque<State> queue = new ArrayDeque <>();

        solveB.setEnabled(false);
        solveD.setEnabled(false);
        
        queue.offer(initialState);
        
        // Ciclo de búsqueda
        
        startTime = System.currentTimeMillis();
        
        while(!queue.isEmpty() && !success)
        { 
            totalLoops ++;
            State current = (State) queue.poll();
            
            if(current.isGoal(goalState)) 
            {    
                success = true;
                System.out.println("Path found!");
                showBoard(current);
            }
            else
            {
                List<State> neighbors = current.getNeighbors(); // Expandir el nodo
                                        
                visited.add(current);
                 
                 for(State neighbor: neighbors) // Para cada nuevo nodo
                 {
                    totalNodes++;
                    if (!repetido(visited, neighbor)) 
                    {
                        queue.offer(neighbor); // Agrega el nodo al final de la cola
                    }
                 }                 
            }    
        } 
        if (!success) 
        {
            JOptionPane.showMessageDialog(rootPane, "Path not found", "Sorry!!!", JOptionPane.WARNING_MESSAGE);
           System.out.println("Path not found");
        }
    }
    
    
    // Depth First Search
    
    private void solveDFS()
    {
        
        depth=true;
        boolean success = false;
        totalNodes = 0;
        totalLoops = 0;
        State initialState = new State(start);
        State goalState   = new State(goal);
        ArrayList <State> visited = new ArrayList<>();
        ArrayDeque<State> stack = new ArrayDeque<>();

        solveB.setEnabled(false);
        solveD.setEnabled(false);
            
        stack.push(initialState);
        
        // Ciclo de búsqueda
        
        startTime = System.currentTimeMillis();
        
        while (!stack.isEmpty() && !success) 
        {
            totalLoops ++;
            State current = stack.pop();
            
            if (current.isGoal(goalState)) {
                success = true;
                System.out.println("Path found!");
                showBoard(current);
            }

            for (State neighbor : current.getNeighbors()) 
            {   
                totalNodes++;
                if (!repetido(visited, neighbor)) 
                {   
                    visited.add(current);
                    stack.push(neighbor);
                }
            }
        } 
        if (!success) 
        {
           JOptionPane.showMessageDialog(rootPane, "Path not found", "Sorry!!!", JOptionPane.WARNING_MESSAGE);
           System.out.println("Path not found");
        }
    }
    


    
    
    private void showBoard(State lastNode)
    {
        // Recuperar la ruta
                        
        long elapsed = System.currentTimeMillis()-startTime;
        if(depth)
        {
            this.setTitle("8-Puzzle (Depth-First Search)"); 
            buildSolution(lastNode);
        }
        else
        { 
            this.setTitle("8-Puzzle (Breadth-First Search)"); 
            buildPath(lastNode);
        }

        JOptionPane.showMessageDialog(rootPane, "Success!! \nPath with "+thePath.length()+" nodes"+"\nGenerated nodes: "+totalNodes+"\nLoops: "+totalLoops+"\nElapsed time: " + elapsed + " milliseconds", 
                                                    "Good News!!!", JOptionPane.INFORMATION_MESSAGE);
        
        State initialState = new State(start);
        int i=initialState.getI();
        int j=initialState.getJ();
       
        Executor exec = new Executor(jBoard,i,j,thePath, empty, velocity);
        exec.start();
    }  
    
    private void buildPath(State node) {
        if (node == null) return;
        buildPath(node.getParent());
        node.show();
        if(node.getMovement()!='n') thePath=thePath+node.getMovement();
    }
    
    private  void buildSolution(State node) 
    {
        thePath = "";
        State n = node;
        while(n!=null) 
        {  
            thePath = thePath + n.getMovement();         
            n = n.getParent();         
        }
        StringBuilder sb = new StringBuilder(thePath);
        thePath = sb.reverse().toString();
    }
        
    // Compara para evitar nodos repetidos
    public boolean repetido(ArrayList<State> l, State s)
    {
        boolean exist = false;
        for(State ns: l)
        { 
            //se cambio isEqual por isGoal ya que son exactamente el mismo metodo 
            if(ns.isGoal(s)) // Un método propio para comparar estados
            {
                exist = true;
                break;
            }
        }
        return exist;
    }
        
}

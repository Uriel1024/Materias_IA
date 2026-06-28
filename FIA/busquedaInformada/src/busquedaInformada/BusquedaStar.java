package busquedaInformada;

import java.util.*;

class Node implements Comparable<Node> {
    int x; // Coordenada x
    int y; // Coordenada y
    int g; // Costo acumulado desde el inicio
    int h; // Heurística
    int f; // f = g + h
    Node parent; // Nodo padre para reconstruir la ruta

    public Node(int i, int j) {
        this.x = i;
        this.y = j;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.f, other.f);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Node))
            return false;
        Node other = (Node) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

public class BusquedaStar {
    private int[][] matrix;
    private int startX, startY, endX, endY;
    private int[][] directions = { {0, 1}, {1, 0}, {0, -1}, {-1, 0} };
    private int width, height;

    public BusquedaStar(int[][] grid, int startX, int startY, int endX, int endY) {
        this.matrix = grid;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.width = grid.length;
        this.height = grid[0].length;
    }

    public List<Node> findPath() {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<Node, Node> parents = new HashMap<>();
        Map<Node, Integer> gCosts = new HashMap<>();

        Node startNode = new Node(startX, startY);
        startNode.g = 0;
        startNode.h = heuristic(startNode);
        startNode.f = startNode.g + startNode.h;

        openSet.add(startNode);
        gCosts.put(startNode, 0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.x == endX && current.y == endY) {
                return buildPath(parents, current);
            }
            for (int[] dir : directions) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];

                if (newX < 0 || newX >= width || newY < 0 || newY >= height ||
                        matrix[newX][newY] == 1) { // Se considera obstáculo el valor 1
                    continue;
                }
                int newCost = current.g + 1;
                Node neighbor = new Node(newX, newY);
                neighbor.h = heuristic(neighbor);

                if (!gCosts.containsKey(neighbor) || newCost < gCosts.get(neighbor)) {
                    neighbor.parent = current;
                    parents.put(neighbor, current);
                    gCosts.put(neighbor, newCost);
                    neighbor.g = newCost;
                    neighbor.f = neighbor.g + neighbor.h;
                    openSet.add(neighbor);
                }
            }
        }
        return null; // en caso de no encontrar una ruta
    }

    private List<Node> buildPath(Map<Node, Node> parents, Node actualNode) {
        List<Node> route = new ArrayList<>();
        while (actualNode != null) {
            route.add(actualNode);
            actualNode = parents.get(actualNode);
        }
        Collections.reverse(route);
        return route;
    }

    private int heuristic(Node actual) {
        //ESta es la distancia Manhattan
        return Math.abs(actual.x - endX) + Math.abs(actual.y - endY);
    }
}

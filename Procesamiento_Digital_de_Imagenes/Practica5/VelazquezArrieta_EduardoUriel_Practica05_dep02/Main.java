public class Main {
    public static void main(String[] args) {
        Model m = new Model();
        View v = new View();
        new Controller(m, v);
        v.setVisible(true);
    }
}
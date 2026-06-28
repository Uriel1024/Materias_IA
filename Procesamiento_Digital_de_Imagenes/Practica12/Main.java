public class Main {

    public static void main(String[] args) {

        FourierModel model =
                new FourierModel();

        FourierView view =
                new FourierView();

        new FourierController(
                model,
                view);

        view.setVisible(true);
    }
}
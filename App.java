import model.Parser;
import controller.AppController;
import view.Gui;

public class App{

    public static void main (String[] args) {
        Parser parser = new Parser();
        Gui view = new Gui();
        AppController controller = new AppController(parser, view);
        view.setController(controller);

        try {
            view.Gui();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package model;
import java.util.*;
import view.Gui;

public class Printer {

    private static Printer instance = null;
    private Gui view;
    
    public static Printer getInstance() {
        if (instance == null) {
            instance = new Printer();
            System.out.println("Printer initialized");
        }


        return instance;
    }

    public void addView(Gui view) {
        this.view = view;
    }

    public void print(String msg) {
        view.printToConsole(msg);
    }

    public void print(String msg, int line) {
        view.printToConsole("Runtime Error: [Line " + line + "] " + msg);
    }

}
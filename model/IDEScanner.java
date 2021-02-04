
package model;
import java.util.*;
import view.Gui;

public class IDEScanner {

    private static IDEScanner instance = null;
    private Gui view;
    
    public static IDEScanner getInstance() {
        if (instance == null) {
            instance = new IDEScanner();
            System.out.println("Scanner initialized");
        }

        return instance;
    }

    public void addView(Gui view) {
        this.view = view;
    }

    public String scan(String prompt) {
        return this.view.getInput(prompt);
    }

}
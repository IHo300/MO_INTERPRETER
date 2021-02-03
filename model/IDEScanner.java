
package model;
import java.util.*;
import view.AppView;

public class IDEScanner {

    private static IDEScanner instance = null;
    private AppView view;
    
    public static IDEScanner getInstance() {
        if (instance == null) {
            instance = new IDEScanner();
            System.out.println("Scanner initialized");
        }

        return instance;
    }

    public void addView(AppView view) {
        this.view = view;
    }

    public String scan(String prompt) {
        return this.view.getInput(prompt);
    }

}
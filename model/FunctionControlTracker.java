package model;

import java.util.*;
import model.objects.*;

public class FunctionControlTracker {

    private static FunctionControlTracker instance = null;
    private Stack<PseudoFunction> stack;
    
    public FunctionControlTracker() {
        this.stack = new Stack<PseudoFunction>();
    }

    public static FunctionControlTracker getInstance() {
        if (instance == null) {
            instance = new FunctionControlTracker();
            System.out.println("Function Control Tracker initialized");
        }
        
        return instance;
    }

    public void reset() {
        instance = null;
    } 

    public void enterFunction(PseudoFunction pseudoFunction) {
        this.stack.push(pseudoFunction);
        PrintStack(stack);
    }

    public void exitFunction() {
        this.stack.pop();
        PrintStack(stack);
    }

    public boolean isInsideFunction() {
        return this.stack.size() != 0;
    }

    public PseudoFunction getCurFunction() {
        return this.stack.peek();
    }

    public void PrintStack(Stack<PseudoFunction> s) 
    { 
        if (s.size() < 8) {
            Object[] p = s.toArray();
            
            System.out.println("----STACK----");
            for (int i = 0; i < p.length; i++) {
                PseudoFunction f = (PseudoFunction) p[i];
                System.out.print(f + " = " + f.getReturnValue() + " = " +  f.getReturnValue().getValue() +"\n");
            }
            System.out.println("------------");

        }
    } 

}
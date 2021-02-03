package model;

import model.objects.*;


public class FunctionReturnTracker {

    private static FunctionReturnTracker instance = null;
    private PseudoFunction curFunction;
    private boolean hasReturn;

    public FunctionReturnTracker() {
        curFunction = null;
        hasReturn = false;
    }

    public static FunctionReturnTracker getInstance() {
        if (instance == null) {
            instance = new FunctionReturnTracker();
            System.out.println("Function Declaration Tracker initialized");
        }
        
        return instance;
    }

    public void reset() {
        instance = null;
        hasReturn = false;
        curFunction = null;
    } 

    public void setCurFunction(PseudoFunction curFunction) {
        this.curFunction = curFunction;
    }

    public PseudoFunction getCurFunction() {
        return curFunction;
    }

    public boolean hasReturn() {
        return hasReturn;
    }

    public void setHasReturn(boolean hasReturn) {
        this.hasReturn = hasReturn;
    }

}
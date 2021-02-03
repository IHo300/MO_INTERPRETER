package model;

import java.util.*;

import model.objects.*;


public class ScopeManager {

    private static ScopeManager instance = null;

    private HashMap<String, PseudoFunction> funcList;
    private Scope curScope;

    private ScopeManager() {
        this.funcList = new HashMap<String, PseudoFunction>();
        this.curScope = null;
    }

    public static ScopeManager getInstance() {
        if (instance == null) {
            instance = new ScopeManager();
            System.out.println("Scope Manager initialized");
        }

        return instance;
    }

    public void reset() {
        instance = null;
        funcList = new HashMap<String, PseudoFunction>();
    }

    public void setScope(Scope curScope) {
        this.curScope = curScope;
    }

    public Scope getScope() {
        return this.curScope;
    }

    public void goToParent() {
        if (curScope != null) {
            curScope = curScope.getParent();  
        }
    }

    public void addFunction(String id, PseudoFunction pseudoFunction) {
        this.funcList.put(id, pseudoFunction);
    }

    public PseudoFunction getFunction(String id) {
        if (this.funcList.containsKey(id)) {
            return this.funcList.get(id);
        } else {
            return null;
        }
    }

    public PseudoValue searchMyScopeVariable(String id) {

        Scope myScope = curScope;
        PseudoValue pseudoValue = null;

        // null = it is the outer most scope
        while (myScope != null) {
            pseudoValue = myScope.getVariable(id);

            if (pseudoValue != null) {
                return pseudoValue;
            } 

            myScope = myScope.getParent();
        }

        return pseudoValue;
    }






    

    


}
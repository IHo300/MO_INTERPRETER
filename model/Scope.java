package model;

import java.util.*;
import model.objects.*;

public class Scope {

    private Scope parent;
    private HashMap<String, PseudoValue> localVars = null;

    public Scope() {
        this.parent = null;
        this.localVars = new HashMap<String, PseudoValue>();
    }

    public Scope(Scope parent) {
        this.parent = parent;
        this.localVars = new HashMap<String, PseudoValue>();
    }

    public Scope getParent() {
        return this.parent;
    }

    public void setParent(Scope parent) {
        this.parent = parent;
    }

    public void addVariable(String type, String id) {
        PseudoValue pseudoValue = new PseudoValue(null, type);
        this.localVars.put(id, pseudoValue);
    }

    public void addVariable(String type, String id, String value) {
        PseudoValue pseudoValue = new PseudoValue(value, type);
        this.localVars.put(id, pseudoValue);
    }

    public void addVariable(String id, PseudoValue pseudoValue) {
        this.localVars.put(id, pseudoValue);
    }

    public PseudoValue getVariable(String id) {
        if (this.localVars.containsKey(id)) {
            return this.localVars.get(id);
        } else {
            return null;
        }
    }

    public PseudoValue getVariableAllScope(String id) {
        
        Scope current = this;
        PseudoValue pseudoValue = null;

        while (current != null) {
            pseudoValue = current.getVariable(id);

            if (pseudoValue != null) {
                return pseudoValue;
            } 

            current = current.getParent();
        }
        
        return pseudoValue;

    }




    

}


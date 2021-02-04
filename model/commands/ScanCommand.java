package model.commands;

import parser.ThanosParser.PrintParamsContext;
import parser.ThanosParser.PrintParamsSelectorContext;
import model.*;
import javafx.application.Platform;
import model.objects.*;

public class ScanCommand implements Command {

    private String id;
    private String val = "";
    private Scope scope;

    public ScanCommand(String val, String id) {
        this.val = val.replaceAll("^\"+|\"+$", "");
        this.id = id;

        this.scope = ScopeManager.getInstance().getScope();
    }

    @Override
    public void execute() {
        RuntimeManager.getInstance().pauseExecution();

        Platform.runLater(new Runnable() {
            @Override public void run() {
                String input = IDEScanner.getInstance().scan(val);
                
                PseudoValue value = scope.getVariableAllScope(id);
                
                try {
                    switch(value.getPrimitiveType()) {
                        case INT: value.setValue(Integer.valueOf(input));
                        case FLOAT: value.setValue(Float.valueOf(input));
                        case BOOLEAN: value.setValue(Boolean.valueOf(input));
                        case STRING: value.setValue(input.replaceAll("^\"+|\"+$", ""));
                        default: value.setValue(input);
                    }
                    RuntimeManager.getInstance().resumeExecution();

                } catch (NumberFormatException e) {
                    Printer.getInstance().print("Program Terminated. Scan value mismatch.");
                    RuntimeManager.getInstance().killExecution();
                }
                
            }
        });
        
    }





    
}
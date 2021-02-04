package model.commands;

import parser.ThanosParser.WhileStatementContext;
import parser.ThanosParser.LoopDeclarationContext;
import parser.ThanosParser.IterationToStatementContext;
import parser.ThanosParser.SimpleExpressionContext;

import model.*;
import model.objects.*;
import model.SemCheck.*;
import model.commands.*;
import java.util.*;


public class WhileCommand implements ControlledCommand {

    private List<Command> commandList;
    //private IterationToStatementContext iterationToStatementCtx;
    private SimpleExpressionContext simpleExpressionCtx;
    private Scope scope;
    private PseudoValue pseudoValue;
    private int counter = 0;
    private String iteratorIdentifier ;
    private String strExpression = "";
    private boolean isLessThan = false;

    public WhileCommand(WhileStatementContext whileStatementContext) {
        this.commandList = new ArrayList<>();
        this.iteratorIdentifier = whileStatementContext.IDENTIFIER().getText();
//        this.iterationToStatementCtx = whileStatementContext.iterationToStatement();
        this.simpleExpressionCtx = whileStatementContext.simpleExpression();
        this.scope = ScopeManager.getInstance().getScope();
        
        this.pseudoValue = scope.getVariableAllScope(iteratorIdentifier);


//        if (iterationToStatementCtx.getText().contains("up")){
//            this.isLessThan = true;
//        } else {
//            this.isLessThan = false;
//        }
    }

    @Override
    public void execute() {
        this.counter = Integer.parseInt(pseudoValue.getValue().toString());

        EvaluateCommand evalCommand = new EvaluateCommand(this.simpleExpressionCtx, this.scope);
        evalCommand.execute();

        if (this.isLessThan) {
            while (this.counter < evalCommand.getEvaluated().intValue()) {
                int index = 0;
                while (index < commandList.size() ) {
                    if (RuntimeManager.getInstance().canExec()) {
                        commandList.get(index).execute();
                        index ++;
                    } 
                }
                evalCommand = new EvaluateCommand(this.simpleExpressionCtx, this.scope);
                evalCommand.execute();
                this.updateCounter();
            }

        } else {

            while (this.counter > evalCommand.getEvaluated().intValue()) {
                int index = 0;
                while (index < commandList.size() ) {
                    if (RuntimeManager.getInstance().canExec()) {
                        commandList.get(index).execute();
                        index ++;
                    } 
                }
                evalCommand = new EvaluateCommand(this.simpleExpressionCtx, this.scope);
                evalCommand.execute();
                this.updateCounter();
            }

        }
        
    }

    @Override   
    public void addCommand(Command c) {
        this.commandList.add(c);
    }

    private void updateCounter() {
        if (this.isLessThan) {
            this.counter = Integer.parseInt(pseudoValue.getValue().toString());
            this.counter++;
            this.pseudoValue.setValue(this.counter);
        } else {
            this.counter = Integer.parseInt(pseudoValue.getValue().toString());
            this.counter--;
            this.pseudoValue.setValue(this.counter);
        }
        
    }





    
}
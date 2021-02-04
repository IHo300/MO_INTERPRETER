package model.commands;

import parser.ThanosParser.IterationStatementContext;
import parser.ThanosParser.ForConditionContext;
//import parser.ThanosParser.IterationToStatementContext;
import parser.ThanosParser.StatementContext;

import model.*;
import model.objects.*;
import model.SemCheck.*;
import java.util.*;


public class ForCommand implements ControlledCommand {

    private List<Command> commandList;
    private int counter = 0;
    private Scope scope;
    private PseudoValue pseudoValue;
    private String iteratorIdentifier ;
    private ForConditionContext forConditionContextCtx;
    //private IterationToStatementContext iterationToStatementCtx;
    private StatementContext stmtCtx;
    private boolean isLessThan = false;

    public ForCommand(IterationStatementContext iterationStatementContext) {
        this.commandList = new ArrayList<>();
        this.forConditionContextCtx = iterationStatementContext.forCondition();
        //this.iterationToStatementCtx = forStatementContext.iterationToStatement();
        this.stmtCtx = iterationStatementContext.statement();
        this.iteratorIdentifier = forConditionContextCtx.Identifier().getText();
        
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

        EvaluateCommand evalCommand = new EvaluateCommand(this.stmtCtx, this.scope);
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

                evalCommand = new EvaluateCommand(this.stmtCtx, this.scope);
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

                evalCommand = new EvaluateCommand(this.stmtCtx, this.scope);
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
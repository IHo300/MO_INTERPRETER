package model.commands;

import parser.PSCParser.ForStatementContext;
import parser.PSCParser.LoopDeclarationContext;
import parser.PSCParser.IterationToStatementContext;
import parser.PSCParser.SimpleExpressionContext;

import model.*;
import model.objects.*;
import model.semcheck.*;
import java.util.*;


public class ForCommand implements ControlledCommand {

    private List<Command> commandList;
    private int counter = 0;
    private Scope scope;
    private PseudoValue pseudoValue;
    private String iteratorIdentifier ;
    private LoopDeclarationContext loopDeclarationCtx; 
    private IterationToStatementContext iterationToStatementCtx;
    private SimpleExpressionContext simpleExpressionCtx;
    private boolean isLessThan = false;

    public ForCommand(ForStatementContext forStatementContext) {
        this.commandList = new ArrayList<>();
        this.loopDeclarationCtx = forStatementContext.loopDeclaration();
        this.iterationToStatementCtx = forStatementContext.iterationToStatement();
        this.simpleExpressionCtx = forStatementContext.simpleExpression();
        this.iteratorIdentifier = loopDeclarationCtx.IDENTIFIER().getText();
        
        this.scope = ScopeManager.getInstance().getScope();
        this.pseudoValue = scope.getVariableAllScope(iteratorIdentifier);
        if (iterationToStatementCtx.getText().contains("up")){
            this.isLessThan = true;
        } else {
            this.isLessThan = false;
        } 
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
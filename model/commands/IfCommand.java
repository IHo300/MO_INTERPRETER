package model.commands;

import parser.PSCParser.SelectionStmtContext;
import parser.PSCParser.SimpleExpressionContext;

import model.*;
import model.objects.*;
import model.semcheck.*;
import java.util.*;

public class IfCommand implements ConditionalCommand {

    private List<Command> ifCommands;
    private List<Command> elseCommands;
    private SelectionStmtContext ifCtx;
    private SimpleExpressionContext conditionCtx;
    private String strExpression;
    private Scope scope;

    public IfCommand(SelectionStmtContext ifCtx) {
        this.ifCtx = ifCtx;
        ifCommands = new ArrayList<>();
        elseCommands = new ArrayList<>();
        this.scope = ScopeManager.getInstance().getScope();
    }


    @Override
    public void execute() {
        
        SimpleExpressionContext conditionCtx = ifCtx.simpleExpression();

        if(Util.checkCondition(conditionCtx, scope)) {
            int index = 0;
            while (index < ifCommands.size() ) {
                if (RuntimeManager.getInstance().canExec()) {
                    ifCommands.get(index).execute();
                    index ++;
                } 
            }
        } else {
            int index = 0;
            while (index < elseCommands.size() ) {
                if (RuntimeManager.getInstance().canExec()) {
                    elseCommands.get(index).execute();
                    index ++;
                } 
            }
        }

    }

    @Override
    public void addIfCommand(Command c) {
        this.ifCommands.add(c);
    }

    @Override
    public void addElseCommand(Command c) {
        this.elseCommands.add(c);
    }
    
}
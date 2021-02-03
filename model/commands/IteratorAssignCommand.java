package model.commands;

import java.util.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import parser.PSCParser.SimpleExpressionContext;
import model.*;
import model.objects.*;
import model.semcheck.*;

public class IteratorAssignCommand implements Command {

    private Scope scope; 
    private TerminalNode id;
    private SimpleExpressionContext rhsCtx;
    private EvaluateCommand evalCommand;
    
    public IteratorAssignCommand(TerminalNode id, SimpleExpressionContext rhsCtx) {
        this.rhsCtx = rhsCtx;
        this.id = id;
        this.scope = ScopeManager.getInstance().getScope();

        UndeclaredSemCheck undeclaredSemCheck = new UndeclaredSemCheck(rhsCtx);
        undeclaredSemCheck.check();

        PseudoValue pseudoValue = scope.getVariableAllScope(id.getText());


        if (pseudoValue.getPrimitiveType() != PrimitiveType.ARRAY) {
            ConstantSemCheck constSemCheck = new ConstantSemCheck(this.id);
            constSemCheck.check();
        }
       
        // check if rhs is compatible with id type
        TypeMismatchSemCheck typeMMSemCheck = new TypeMismatchSemCheck(pseudoValue, this.rhsCtx);
        typeMMSemCheck.check();

        evalCommand = new EvaluateCommand(rhsCtx);
    }

    @Override
    public void execute() {
        
        evalCommand.execute();
        PseudoValue pseudoValue = scope.getVariableAllScope(id.getText());
        Util.assignValue(pseudoValue, evalCommand.getEvaluated());
        
    }



}

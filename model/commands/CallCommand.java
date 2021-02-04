package model.commands;

import parser.PSCParser.ArgumentsContext;
import parser.PSCParser.SimpleExpressionContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import model.*;
import model.objects.*;
import model.SemCheck.*;

public class CallCommand implements Command {

    private Scope scope;
    private String funcName;
    private PseudoFunction func;
    private ArgumentsContext argsCtx;
    private PseudoValue returnValue;

    public CallCommand(PseudoFunction func, ArgumentsContext argsCtx) {
        this.func = func;
        this.argsCtx = argsCtx;

        this.scope = ScopeManager.getInstance().getScope();
        this.checkParameters(); // checks fun(x, 1) <-- x and 1 has to be checked if appropriate
    }

    public void checkParameters() {
        if (this.argsCtx.simpleExpression().size() == 0) { // no args, dont check
            return;
        }

        List<SimpleExpressionContext> args = argsCtx.simpleExpression();

        for (int i = 0; i < args.size(); i++) {
            SimpleExpressionContext simpleCtx = args.get(i);

            if (i < this.func.getParameterCount()) {
                TypeMismatchSemCheck typeMMSemCheck = new TypeMismatchSemCheck(this.func.getParamAt(i), simpleCtx);
                typeMMSemCheck.check();
            }             
        }
    }

    public void mapParameters() {
        if (this.argsCtx.simpleExpression().size() == 0) { // no args, do nothing
            return;
        }

        List<SimpleExpressionContext> args = argsCtx.simpleExpression();

        for (int i = 0; i < args.size(); i++) {
            SimpleExpressionContext simpleCtx = args.get(i);


            if (this.func.getParamAt(i).getPrimitiveType() == PrimitiveType.ARRAY) {
                String id = simpleCtx.getText();
                this.func.mapArrayParameter(id, this.scope.getVariableAllScope(id), i);
            } else { // evaluate non-array variable right away
                
                EvaluateCommand evalCommand = new EvaluateCommand(simpleCtx, this.scope);
                evalCommand.execute();
                
                if (i < this.func.getParameterCount()) {
                   PseudoValue paramValue = this.func.getParamAt(i);
                   paramValue.setValue(evalCommand.getEvaluated().toPlainString());
                }
            }          
        }
    }

    @Override
    public void execute() {
        this.mapParameters();
        this.func.execute();
    }
    
}
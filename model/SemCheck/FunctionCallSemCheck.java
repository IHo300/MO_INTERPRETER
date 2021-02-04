package model.SemCheck;

import model.objects.*;
import model.Console;
import parser.PSCParser.SimpleExpressionContext;
import parser.PSCParser.ConstantContext;
import parser.PSCParser.MutableContext;
import parser.PSCParser.CallContext;
import parser.PSCParser.ArgumentsContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

import model.*;
import java.util.*;

public class FunctionCallSemCheck implements SemCheck {


    private CallContext callCtx;
    private int line;

    public FunctionCallSemCheck(CallContext callCtx) {
        this.callCtx = callCtx;

        Token first = callCtx.getStart();
        this.line = first.getLine();
    }

    @Override
	public void check() {

        String id = callCtx.IDENTIFIER().getText();
        PseudoFunction pf = ScopeManager.getInstance().getFunction(id);

        // check if the arguments match the one's declared in func table
        ArgumentsContext callArgsCtx = callCtx.arguments();

        ParameterCountSemCheck paramCountSemCheck = new ParameterCountSemCheck(pf, callArgsCtx);
        paramCountSemCheck.check();

        // check if the arguments exist
        List<SimpleExpressionContext> callArgsList = callArgsCtx.simpleExpression();
        
        for (SimpleExpressionContext exprCtx : callArgsList) {
            UndeclaredSemCheck undeclaredSemCheck = new UndeclaredSemCheck(exprCtx);
            undeclaredSemCheck.check();
        }
		
	}


}
package model.commands;

import java.util.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import javafx.application.Platform;


import parser.ThanosParser.PrintParamsContext;
import parser.ThanosParser.PrintParamsSelectorContext;
import model.*;
import model.objects.*;

public class PrintCommand implements Command, ParseTreeListener {

    private PrintParamsContext paramsCtx;
    private String msg = "";
    private Scope scope; 
    
    public PrintCommand(PrintParamsContext paramsCtx) {
        this.paramsCtx = paramsCtx;
        this.scope = ScopeManager.getInstance().getScope();



        List<PrintParamsSelectorContext> paramsList = paramsCtx.printParamsSelector();

        for (PrintParamsSelectorContext param : paramsList) {
            
            if (param.IDENTIFIER() != null) {
                PseudoValue pseudoValue = ScopeManager.getInstance().searchMyScopeVariable(param.IDENTIFIER().getText());
                if(pseudoValue == null) {
                    Console.log("UndeclaredVariable Error at print statement", paramsCtx.getStart().getLine());
                }
            } else if (param.call() != null) {
                PseudoFunction pseudoFunction = ScopeManager.getInstance().getFunction(param.call().IDENTIFIER().getText());

                if (pseudoFunction == null) {
                    Console.log("UndeclaredFunction error at print statement",  paramsCtx.getStart().getLine());
                }
            }
        }
    }

    @Override
    public void execute() {
        ParseTreeWalker treeWalker = new ParseTreeWalker();
        treeWalker.walk(this, this.paramsCtx);

        final String printMsg = this.msg.replace("\\\\", "\\");

        Platform.runLater(new Runnable() {
            @Override public void run() {
                Printer.getInstance().print(printMsg);
            }
        });

        this.msg = ""; 
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        if (ctx instanceof PrintParamsSelectorContext) {
            PrintParamsSelectorContext printParamsCtx = (PrintParamsSelectorContext) ctx;

            if (printParamsCtx.StringLiteral()!= null) {
                this.msg += printParamsCtx.StringLiteral().getText().replaceAll("^\"+|\"+$", "");
            } else if (printParamsCtx.IDENTIFIER() != null) {
                PseudoValue pseudoValue = scope.getVariableAllScope(printParamsCtx.IDENTIFIER().getText());

                this.msg += pseudoValue.getValue().toString();
            } // else if function call
        } 
    }

    @Override
	public void exitEveryRule(ParserRuleContext ctx) {
		// TODO Auto-generated method stub
		
	}

    @Override
	public void visitTerminal(TerminalNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitErrorNode(ErrorNode node) {
		// TODO Auto-generated method stub
		
	}

}
package model.semcheck;

import model.objects.*;
import model.Console;
import parser.PSCParser.SimpleExpressionContext;
import parser.PSCParser.MutableContext;
import parser.PSCParser.CallContext;
import parser.PSCParser.ConstantContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

import model.*;


public class UndeclaredSemCheck implements SemCheck, ParseTreeListener {

	private SimpleExpressionContext simpleCtx;
	private int line;

	public UndeclaredSemCheck(SimpleExpressionContext simpleCtx) {
		this.simpleCtx = simpleCtx;

		Token first = this.simpleCtx.getStart();
		this.line = first.getLine();
	}


    @Override
	public void check() {
		ParseTreeWalker treeWalker = new ParseTreeWalker();
		treeWalker.walk(this, this.simpleCtx);
	}


	@Override
	public void enterEveryRule(ParserRuleContext ctx) {

		if(ctx instanceof MutableContext) {
			MutableContext mutableCtx = (MutableContext) ctx;

			PseudoValue pseudoValue = ScopeManager.getInstance().searchMyScopeVariable(mutableCtx.IDENTIFIER().getText());

			if(pseudoValue == null) {
				Console.log("UndeclaredVariable Error: '" + mutableCtx.IDENTIFIER().getText() + "' not found.", this.line);
			}

		} else if (ctx instanceof CallContext) {

			CallContext callCtx = (CallContext) ctx;
			PseudoFunction pseudoFunction = ScopeManager.getInstance().getFunction(callCtx.IDENTIFIER().getText());

			if(pseudoFunction == null) {
				Console.log("UndeclaredFunction Error: Function '" + callCtx.IDENTIFIER().getText() + "' not found.", this.line);
			}
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
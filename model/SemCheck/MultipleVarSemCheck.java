package model.SemCheck;

import model.objects.*;
import model.Console;
import parser.ThanosParser.ScopedVariableDeclarationContext;
import parser.ThanosParser.VariableDeclarationInitializeContext;
import parser.ThanosParser.ArrayVariableDeclarationInitializeContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

import model.*;


public class MultipleVarSemCheck implements SemCheck, ParseTreeListener {

	private ScopedVariableDeclarationContext varIdCtx;
	private int line;

	public MultipleVarSemCheck (ScopedVariableDeclarationContext varIdCtx) {
		this.varIdCtx = varIdCtx;

		Token first = this.varIdCtx.getStart();
		this.line = first.getLine();
	}

    @Override
	public void check() {
		ParseTreeWalker treeWalker = new ParseTreeWalker();
		treeWalker.walk(this, this.varIdCtx);
	}


	@Override
	public void enterEveryRule(ParserRuleContext ctx) {
		if(ctx instanceof VariableDeclarationInitializeContext) {
			VariableDeclarationInitializeContext varDecCtx = (VariableDeclarationInitializeContext) ctx;
			
			if (ScopeManager.getInstance().searchMyScopeVariable(varDecCtx.IDENTIFIER().getText()) != null) {
				Console.log("MultipleVarDeclaration Error: '" + varDecCtx.IDENTIFIER().getText() + "' has been declared previously.", this.line);
			}
		} else if(ctx instanceof ArrayVariableDeclarationInitializeContext) {
			ArrayVariableDeclarationInitializeContext varDecCtx = (ArrayVariableDeclarationInitializeContext) ctx;

			if (varDecCtx.IDENTIFIER() != null) {
				if (ScopeManager.getInstance().searchMyScopeVariable(varDecCtx.IDENTIFIER().getText()) != null) {
					Console.log("MultipleVarDeclaration Error: '" + varDecCtx.IDENTIFIER().getText() + "' has been declared previously.",  this.line);
				}
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
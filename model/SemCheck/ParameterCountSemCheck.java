package model.semcheck;

import java.util.*;

import model.objects.*;
import model.Console;
import parser.PSCParser.ArgumentsContext;
import parser.PSCParser.SimpleExpressionContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.ParserRuleContext;

import model.*;


public class ParameterCountSemCheck implements SemCheck {
	
	private PseudoFunction pseudoFunction;
	private List<SimpleExpressionContext> exprCtxList;
	private int line;

	public ParameterCountSemCheck(PseudoFunction pseudoFunction, ArgumentsContext argsCtx) {
		this.pseudoFunction = pseudoFunction;

		if (argsCtx.simpleExpression() != null) {
			this.exprCtxList = argsCtx.simpleExpression();
		}

		this.line = argsCtx.getStart().getLine();
	}

    @Override
	public void check() {
		
		if (this.pseudoFunction == null) {
			return;
		} else if (this.exprCtxList == null && this.pseudoFunction.getParameterCount() != 0) {
			Console.log("ParameterCountMismatch Error: Expected to have same number of parameters.", this.line);
		} else if (this.exprCtxList != null && this.pseudoFunction.getParameterCount() != this.exprCtxList.size() ) {
			Console.log("ParameterCountMismatch Error: Expected to have same number of parameters.", this.line);
		}


	}

}
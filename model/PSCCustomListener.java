// Generated from grammar/PSC.g4 by ANTLR 4.9
package model;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import model.visitors.*;
import parser.*;


public class PSCCustomListener extends PSCBaseListener {

	// Visitors handle semantic errors.
	@Override public void enterProgram(PSCParser.ProgramContext ctx) { 
		ProgramVisitor visitor = new ProgramVisitor();
		visitor.visit(ctx);
	}

}
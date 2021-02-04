// Generated from grammar/Thanos.g4 by ANTLR 4.9
package model;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import model.visitors.*;
import parser.*;


public class ThanosCustomListener extends ThanosBaseListener {

	// Visitors handle semantic errors.
	@Override public void enterProgram(ThanosParser.ProgramContext ctx) { 
		ProgramVisitor visitor = new ProgramVisitor();
		visitor.visit(ctx);
	}

}
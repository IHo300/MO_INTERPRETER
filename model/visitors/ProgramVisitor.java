package model.visitors;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import parser.PSCParser.ProgramContext;
import parser.PSCParser.FunctionDeclarationContext;
import parser.PSCParser.MainProgramContext;
import parser.PSCParser.CompoundStmtContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

import model.ScopeManager;
import model.Scope;

public class ProgramVisitor implements ParseTreeListener {

    public ProgramVisitor() {}

    public void visit(ProgramContext ctx) {
        ScopeManager.getInstance();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, ctx);
    }

    @Override
	public void enterEveryRule(ParserRuleContext ctx) {

        
		if(ctx instanceof FunctionDeclarationContext) { // function dec checker
            FunctionDeclarationContext funcCtx = (FunctionDeclarationContext) ctx;

			FuncDeclaratorVisitor visitor = new FuncDeclaratorVisitor();
            visitor.visit(funcCtx);
		} else if (ctx instanceof MainProgramContext) { // main program checker (just a compound statement)
            MainProgramContext mainCtx = (MainProgramContext) ctx;

            CompoundStmtContext compoundCtx = mainCtx.compoundStmt();


            if (compoundCtx != null) {
                Scope scope = new Scope(ScopeManager.getInstance().getScope());
                ScopeManager.getInstance().setScope(scope);
                System.out.println("Opened main scope");

                CompoundVisitor visitor = new CompoundVisitor();
                visitor.visit(compoundCtx);
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
package model.visitors;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import parser.PSCParser.IterationStmtContext;
import parser.PSCParser.WhileStatementContext;
import parser.PSCParser.ForStatementContext;
import parser.PSCParser.IterationStmtContext;
import parser.PSCParser.CompoundStmtContext;
import parser.PSCParser.LoopDeclarationContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

import model.ScopeManager;
import model.semcheck.*;
import model.Console;
import model.objects.*;
import model.Scope;
import model.commands.*;
import model.*;

public class IterationVisitor {

    public IterationVisitor() {}

    public void visit(IterationStmtContext ctx) {
        
        if (ctx.whileStatement() != null ) {
            WhileStatementContext whileCtx = ctx.whileStatement();

            // to check identifier while i <---
            PseudoValue pseudoValue = ScopeManager.getInstance().searchMyScopeVariable(whileCtx.IDENTIFIER().getText());

			if(pseudoValue == null) {
				Console.log("UndeclaredVariable Error: '" +  whileCtx.IDENTIFIER().getText() + "' cannot be found at while statement", ctx.getStart().getLine());
			} else {
                if (pseudoValue.getPrimitiveType() != PrimitiveType.INT) {
                    Console.log("TypeMismatch Error: Only integers can be used at while statements", ctx.getStart().getLine());
                }
            }

            // to check simple expression up to -> z  
            UndeclaredSemCheck undeclaredSemCheck = new UndeclaredSemCheck(whileCtx.simpleExpression());
            undeclaredSemCheck.check();

            // goal has to be int
            TypeMismatchSemCheck typeMMSemCheck = new TypeMismatchSemCheck(new PseudoValue(null, "int"), whileCtx.simpleExpression());
            typeMMSemCheck.check();

            // to check content { }
            Scope scope = new Scope(ScopeManager.getInstance().getScope());
            ScopeManager.getInstance().setScope(scope);

            WhileCommand whileCommand = new WhileCommand(whileCtx);
            StatementControlTracker.getInstance().enterControlledCommand(whileCommand);

            CompoundVisitor whileCompoundVisitor = new CompoundVisitor();
            whileCompoundVisitor.visit(whileCtx.compoundStmt());

            StatementControlTracker.getInstance().exitControlledCommand();

        } else if (ctx.forStatement() != null) {
            ForStatementContext forCtx = ctx.forStatement();

            LoopDeclarationContext loopDecCtx = forCtx.loopDeclaration();

            // int i = 2
            if (loopDecCtx.Int() != null) { // to check if declared new var

                if (ScopeManager.getInstance().searchMyScopeVariable(loopDecCtx.IDENTIFIER().getText()) != null) {
				    Console.log("MultipleVarDeclaration Error: '" + loopDecCtx.IDENTIFIER().getText() +"' cannot be found at for statement", ctx.getStart().getLine());
			    }  else {
                    ScopeManager.getInstance().getScope().addVariable(loopDecCtx.IDENTIFIER().getText(), new PseudoValue(null, "int"));
                }

                // i = 2
                if (loopDecCtx.Assign() != null ) {
                    IteratorAssignCommand iteratorAssignCommand = new IteratorAssignCommand(loopDecCtx.IDENTIFIER(), loopDecCtx.simpleExpression());

                    StatementControlTracker stmtCtrlTracker = StatementControlTracker.getInstance();

                    if (stmtCtrlTracker.isConditionalCommand()) {
                        ConditionalCommand ifCommand = (ConditionalCommand) stmtCtrlTracker.getCurCommand();

                        if (stmtCtrlTracker.inIf()) {
                            ifCommand.addIfCommand(iteratorAssignCommand);
                        } else {
                            ifCommand.addElseCommand(iteratorAssignCommand);
                        } 

                    } else if (stmtCtrlTracker.isControlledCommand()) {
                        ControlledCommand controlledCommand = (ControlledCommand) stmtCtrlTracker.getCurCommand();
                        controlledCommand.addCommand(iteratorAssignCommand);
                    }  else {
                        RuntimeManager.getInstance().addCommand(iteratorAssignCommand);
                    }
                }

            // i, i = 2
            } else { // to check if variable only

                PseudoValue pseudoValue = ScopeManager.getInstance().searchMyScopeVariable(loopDecCtx.IDENTIFIER().getText());

                if(pseudoValue == null) {
                    Console.log("UndeclaredVariable Error: '" + loopDecCtx.IDENTIFIER().getText()  + "' cannot be found at for statement", ctx.getStart().getLine());
                } else {
                    if (pseudoValue.getPrimitiveType() != PrimitiveType.INT) {
                        Console.log("TypeMismatch Error: Only integers can be used at for statements", ctx.getStart().getLine());
                    } 
                } 

                // i = 2
                if (loopDecCtx.Assign() != null ) {
                    IteratorAssignCommand iteratorAssignCommand = new IteratorAssignCommand(loopDecCtx.IDENTIFIER(), loopDecCtx.simpleExpression());

                    StatementControlTracker stmtCtrlTracker = StatementControlTracker.getInstance();

                    if (stmtCtrlTracker.isConditionalCommand()) {
                        ConditionalCommand ifCommand = (ConditionalCommand) stmtCtrlTracker.getCurCommand();

                        if (stmtCtrlTracker.inIf()) {
                            ifCommand.addIfCommand(iteratorAssignCommand);
                        } else {
                            ifCommand.addElseCommand(iteratorAssignCommand);
                        } 

                    } else if (stmtCtrlTracker.isControlledCommand()) {
                        ControlledCommand controlledCommand = (ControlledCommand) stmtCtrlTracker.getCurCommand();
                        controlledCommand.addCommand(iteratorAssignCommand);
                    }  else {
                        RuntimeManager.getInstance().addCommand(iteratorAssignCommand);
                    }
                }
            }

            // to check simple expression up to -> z  
            UndeclaredSemCheck undeclaredSemCheck = new UndeclaredSemCheck(forCtx.simpleExpression());
            undeclaredSemCheck.check();

            // goal has to be int
            TypeMismatchSemCheck typeMMSemCheck = new TypeMismatchSemCheck(new PseudoValue(null, "int"), forCtx.simpleExpression());
            typeMMSemCheck.check();

            // to check content { }
            Scope scope = new Scope(ScopeManager.getInstance().getScope());
            ScopeManager.getInstance().setScope(scope);

            ForCommand forCommand = new ForCommand(forCtx);
            StatementControlTracker.getInstance().enterControlledCommand(forCommand);

            CompoundVisitor forCompoundVisitor = new CompoundVisitor();
            forCompoundVisitor.visit(forCtx.compoundStmt());

            StatementControlTracker.getInstance().exitControlledCommand();   
        }

    }

}
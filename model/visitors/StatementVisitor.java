package model.visitors;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import parser.PSCParser.StatementContext;
import parser.PSCParser.ScanStmtContext;
import parser.PSCParser.PrintStmtContext;
import parser.PSCParser.PrintParamsSelectorContext;
import parser.PSCParser.IterationStmtContext;
import parser.PSCParser.SelectionStmtContext;
import parser.PSCParser.MutableContext;
import parser.PSCParser.TypeSpecifierContext;
import parser.PSCParser.AssignmentStandaloneExpressionContext;
import parser.PSCParser.ForStatementContext;
import parser.PSCParser.WhileStatementContext;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

import java.util.*;


import model.objects.*;
import model.semcheck.*;
import model.commands.*;
import model.*;

public class StatementVisitor {

    
    public StatementVisitor() { }

	public void visit(ParserRuleContext ctx) {

        if (ctx instanceof StatementContext) {
            StatementContext stmtCtx = (StatementContext) ctx;

            if (stmtCtx.scanStmt() != null) {
                ScanStmtContext scanCtx = stmtCtx.scanStmt();

                if (scanCtx.IDENTIFIER() != null) {
                    PseudoValue pseudoValue = ScopeManager.getInstance().searchMyScopeVariable(scanCtx.IDENTIFIER().getText());

                    if(pseudoValue == null) {
                        Console.log("UndeclaredVariable Error: '" + scanCtx.IDENTIFIER().getText() + "' cannot be found at scan statement", stmtCtx.getStart().getLine());
                    } else {

                        if (pseudoValue.getPrimitiveType() == PrimitiveType.ARRAY) {
                            Console.log("TypeMismatch Error: You cannot scan directly to an array.", stmtCtx.getStart().getLine());
                        } else {
                            ScanCommand scanCommand = new ScanCommand(scanCtx.StringLiteral().getText() , scanCtx.IDENTIFIER().getText());
                            addCommand(scanCommand);
                        }
                    }
                }

                
            } else if (stmtCtx.printStmt() != null) {
                PrintStmtContext printCtx = stmtCtx.printStmt();

                if (printCtx.printParams() != null) {
                    PrintCommand printCommand = new PrintCommand(printCtx.printParams());
                    addCommand(printCommand);
                }

            } else if (stmtCtx.selectionStmt() != null) {
                SelectionStmtContext ifCtx = stmtCtx.selectionStmt();
                analyzeSelection(ifCtx);
            } else if (stmtCtx.iterationStmt() != null) {
                // System.out.println("ENTER ITERATIONS TATEMENT");
                IterationStmtContext iterStmtCtx = stmtCtx.iterationStmt();

                IterationVisitor iterationVisitor = new IterationVisitor();
                iterationVisitor.visit(iterStmtCtx);

            } else if (stmtCtx.expressionStmt() != null) { // assignment standalone or function call

                if (stmtCtx.expressionStmt().assignmentStandaloneExpression() != null) { // assignment
                    analyzeExpressionStmt(stmtCtx.expressionStmt().assignmentStandaloneExpression());
                } else if (stmtCtx.expressionStmt().call() != null) { // call

                    String id = stmtCtx.expressionStmt().call().IDENTIFIER().getText();
                    PseudoFunction pf = ScopeManager.getInstance().getFunction(id);

                    if (pf != null) {
                        FunctionCallSemCheck callSemCheck = new FunctionCallSemCheck(stmtCtx.expressionStmt().call());
                        callSemCheck.check();

                        CallCommand callCommand = new CallCommand(pf, stmtCtx.expressionStmt().call().arguments());
                        addCommand(callCommand);
                    } else {
                        Console.log("UndeclaredFunction Error: Try rearranging your functions.", ctx.getStart().getLine());
                    }
                    
                }
            } else if (stmtCtx.returnStmt() != null) {
                if (FunctionReturnTracker.getInstance().getCurFunction() != null && ScopeManager.getInstance().getScope().getParent() == null) {
                    FunctionReturnTracker.getInstance().setHasReturn(true);
                } 
                
                ReturnCommand callCommand = new ReturnCommand(stmtCtx.returnStmt().simpleExpression(), FunctionReturnTracker.getInstance().getCurFunction());
                addCommand(callCommand);

                UndeclaredSemCheck undeclaredSemCheck = new UndeclaredSemCheck(stmtCtx.returnStmt().simpleExpression());
                undeclaredSemCheck.check();
                
            } 
        } else if (ctx instanceof SelectionStmtContext) {
            SelectionStmtContext ifCtx = (SelectionStmtContext) ctx;
            analyzeSelection(ifCtx);
        } 
    }

    private void analyzeSelection(SelectionStmtContext ifCtx) {

        UndeclaredSemCheck undeclaredSemCheck = new UndeclaredSemCheck(ifCtx.simpleExpression());
        undeclaredSemCheck.check();

        Scope scope = new Scope(ScopeManager.getInstance().getScope());
        ScopeManager.getInstance().setScope(scope);
        System.out.println("Opened if/elseif scope");

        IfCommand ifCommand = new IfCommand(ifCtx);
        StatementControlTracker.getInstance().enterConditionalCommand(ifCommand);

        // if and else if
        CompoundVisitor ifCompoundVisitor = new CompoundVisitor();
        ifCompoundVisitor.visit(ifCtx.compoundStmt());

        StatementControlTracker.getInstance().exitIfCommand();
    
        // else
        if (ifCtx.elseSelector() != null) {
            if (ifCtx.elseSelector().compoundStmt() != null) {
                Scope elseScope = new Scope(ScopeManager.getInstance().getScope());
                ScopeManager.getInstance().setScope(elseScope);
                System.out.println("Opened else scope");
                
                CompoundVisitor elseCompoundVisitor = new CompoundVisitor();
                elseCompoundVisitor.visit(ifCtx.elseSelector().compoundStmt());
            } else {
                StatementVisitor stmtVisitor = new StatementVisitor();
                stmtVisitor.visit(ifCtx.elseSelector().selectionStmt());
            }
        }

        StatementControlTracker.getInstance().exitControlledCommand();
    }   


    private void analyzeExpressionStmt(AssignmentStandaloneExpressionContext ctx) {
        MutableContext mutableCtx = ctx.mutable();

        ConstantSemCheck constSemCheck = new ConstantSemCheck(mutableCtx.IDENTIFIER());
        constSemCheck.check();

        PseudoValue pv = ScopeManager.getInstance().searchMyScopeVariable(mutableCtx.IDENTIFIER().getText());

        if (ctx.simpleExpression() != null) { // x = 2 + 2
            if (pv != null) {
                AssignCommand assignCommand = new AssignCommand(mutableCtx, ctx.simpleExpression());
                addCommand(assignCommand);
            } else {
                Console.log("UndeclaredVariable Error: '" + mutableCtx.IDENTIFIER().getText() + "' cannot be found", ctx.getStart().getLine());
            }
            
        } else if (ctx.createArrayExpression() != null) { //  x= create int[]

            if (pv.getPrimitiveType() != PrimitiveType.ARRAY) {
                Console.log("TypeMismatch Error: '" +  mutableCtx.IDENTIFIER().getText() + "' is not an array", ctx.getStart().getLine());
            } else {
                TypeSpecifierContext typeSpecifier = ctx.createArrayExpression().typeSpecifier();

                PseudoArray pa = (PseudoArray) pv.getValue();

                if ((pa.getPrimitiveType() == PrimitiveType.INT && typeSpecifier.Int() == null) || 
                    (pa.getPrimitiveType() == PrimitiveType.STRING && typeSpecifier.String() == null) || 
                    (pa.getPrimitiveType() == PrimitiveType.BOOLEAN && typeSpecifier.Bool() == null) ||
                    (pa.getPrimitiveType() == PrimitiveType.FLOAT && typeSpecifier.Float() == null) ) {
                    Console.log("TypeMismatch Error: Initialize type is not the same as variable type.", ctx.getStart().getLine() );
                }

                // if size is int int[size] <--
                TypeMismatchSemCheck typeMMSemCheck = new TypeMismatchSemCheck(new PseudoValue(null, "int"), ctx.createArrayExpression().simpleExpression());
                typeMMSemCheck.check();

                CreateArrayCommand createArrCommand = new CreateArrayCommand(ctx.createArrayExpression().simpleExpression(), pa);
                addCommand(createArrCommand);
            }
        } 
    }

    private void addCommand(Command c) {

        StatementControlTracker stmtCtrlTracker = StatementControlTracker.getInstance();

        if (stmtCtrlTracker.isConditionalCommand()) {
            ConditionalCommand ifCommand = (ConditionalCommand) stmtCtrlTracker.getCurCommand();

            if (stmtCtrlTracker.inIf()) {
                ifCommand.addIfCommand(c);
            } else {
                ifCommand.addElseCommand(c);
            } 

        } else if (stmtCtrlTracker.isControlledCommand()) {
            ControlledCommand controlledCommand = (ControlledCommand) stmtCtrlTracker.getCurCommand();
            controlledCommand.addCommand(c);
        }  else {
            RuntimeManager.getInstance().addCommand(c);
        }
    }

}
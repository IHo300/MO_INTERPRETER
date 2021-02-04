package model.commands;

import java.util.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigDecimal;
//import parser.ThanosParser.SimpleExpressionContext; to exprstmtctx
import parser.ThanosParser.ExpressionStatementContext;
//import parser.ThanosParser.MutableContext; to postfixexprctx
import parser.ThanosParser.PostfixExpressionContext;
import model.*;
import model.objects.*;
import model.SemCheck.*;

public class AssignCommand implements Command {

    private Scope scope; 
    private TerminalNode id;
    private PostfixExpressionContext mutableCtx;
    private ExpressionStatementContext rhsCtx;
    private EvaluateCommand evalCommand;
    
    public AssignCommand(PostfixExpressionContext mutableCtx, ExpressionStatementContext rhsCtx) {
        this.rhsCtx = rhsCtx;
        //this.id = mutableCtx.IDENTIFIER();
        this.id = mutableCtx.primaryExpression().Identifier(); //Double check this with the original
        this.mutableCtx = mutableCtx;
        this.scope = ScopeManager.getInstance().getScope();

        UndeclaredSemCheck undeclaredSemCheck = new UndeclaredSemCheck(rhsCtx);
        undeclaredSemCheck.check();

        PseudoValue pseudoValue = scope.getVariableAllScope(id.getText());

        if (mutableCtx.LeftBracket() != null && pseudoValue.getPrimitiveType() != PrimitiveType.ARRAY) {
            Console.log("Variable is not an array.", mutableCtx.getStart().getLine());
        }

        if (pseudoValue.getPrimitiveType() != PrimitiveType.ARRAY) {
            ConstantSemCheck constSemCheck = new ConstantSemCheck(this.id);
            constSemCheck.check();
        }

        if(mutableCtx.LeftBracket() != null && pseudoValue.getPrimitiveType() == PrimitiveType.ARRAY ) {

            PseudoArray pa = (PseudoArray) pseudoValue.getValue();
            
            TypeMismatchSemCheck typeMMSemCheck = new TypeMismatchSemCheck(new PseudoValue(null, pa.getPrimitiveType()), rhsCtx);
            typeMMSemCheck.check();
        } else {
            // check if rhs is compatible with id type
            TypeMismatchSemCheck typeMMSemCheck = new TypeMismatchSemCheck(pseudoValue, rhsCtx);
            typeMMSemCheck.check();
        }
       
        

        evalCommand = new EvaluateCommand(rhsCtx);
    }

    @Override
    public void execute() {
        
        if (mutableCtx.LeftBracket() != null) { // if an array
            evalCommand.execute();  // rhs
            EvaluateCommand arrEvalCommand = new EvaluateCommand(mutableCtx.simpleExpression(), this.scope);
            arrEvalCommand.execute(); // lhs

            int arrIndex = arrEvalCommand.getEvaluated().intValue();

            PseudoValue pseudoValue = scope.getVariableAllScope(id.getText());
            PseudoArray pseudoArray = (PseudoArray) pseudoValue.getValue();

            BigDecimal result = evalCommand.getEvaluated();

            if (pseudoArray.isInitialized()) {
                PseudoValue curValue = pseudoArray.getValueAt(arrIndex);
            
                if (curValue != null) {
                    Util.assignValue(curValue, result);
                    pseudoArray.updateValueAt(curValue, arrIndex);
                } else {
                    Printer.getInstance().print("Array Index is out of bounds.", mutableCtx.getStart().getLine());
                    RuntimeManager.getInstance().killExecution();
                }
                
            } else {
                Printer.getInstance().print("Array is not initialized", mutableCtx.getStart().getLine());
                RuntimeManager.getInstance().killExecution();
            }
            
            
            

        } else { // if normal variable
            evalCommand.execute();
            PseudoValue pseudoValue = scope.getVariableAllScope(id.getText());
		    Util.assignValue(pseudoValue, evalCommand.getEvaluated());
        }
        
    }



}
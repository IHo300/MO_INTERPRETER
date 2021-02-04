package model.commands;

import java.util.*;
import java.util.regex.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigDecimal;
import parser.ThanosParser.SimpleExpressionContext;
import parser.ThanosParser.ArgumentsContext;
import parser.ThanosParser.MutableContext;
import parser.ThanosParser.ConstantContext;
import parser.ThanosParser.CallContext;
import model.*;
import model.objects.*;
import model.SemCheck.*;

public class EvaluateCommand implements Command, ParseTreeListener {

    private SimpleExpressionContext simpleCtx;
    private String strExp;
    private Scope scope; 
    private BigDecimal evaluated;
    
    public EvaluateCommand(SimpleExpressionContext simpleCtx) {
        this.simpleCtx = simpleCtx;
        this.scope = ScopeManager.getInstance().getScope();
    }

    public EvaluateCommand(SimpleExpressionContext simpleCtx, Scope scope) {
        this.simpleCtx = simpleCtx;
        this.scope = scope;
    }

    @Override
    public void execute() {

        this.strExp = simpleCtx.getText();

        if (this.strExp.contains("T")) {
            this.evaluated = new BigDecimal(1);
        } else if (this.strExp.contains("F")) {
            this.evaluated = new BigDecimal(0);
        } else{
            ParseTreeWalker treeWalker = new ParseTreeWalker();
            treeWalker.walk(this, this.simpleCtx);

            Expression evalEx = new Expression(this.strExp);
            this.evaluated = evalEx.eval();
        }
        
    }


    @Override
    public void enterEveryRule(ParserRuleContext ctx) {

        if (ctx instanceof CallContext) {
            CallContext callCtx  = (CallContext) ctx;
            System.out.println(callCtx.getText());
            this.evaluateCall(callCtx);
        } else if (ctx instanceof MutableContext) {
            MutableContext mutableCtx  = (MutableContext) ctx;
            this.evaluateVar(mutableCtx);
        }
        
    }


    public void evaluateCall(CallContext callCtx) {

        String functionName = callCtx.IDENTIFIER().getText();
        PseudoFunction pseudoFunction = ScopeManager.getInstance().getFunction(functionName);

        model.semcheck.ParameterCountSemCheck paramCountSemCheck = new model.semcheck.ParameterCountSemCheck(pseudoFunction, callCtx.arguments());
        paramCountSemCheck.check();

        // params
        if (callCtx.arguments().simpleExpression().size() != 0) {
            List<SimpleExpressionContext> arguments = callCtx.arguments().simpleExpression();

            for(int i = 0; i < arguments.size(); i++) {
                SimpleExpressionContext argCtx = arguments.get(i);

                
                if (pseudoFunction.getParamAt(i).getPrimitiveType() == PrimitiveType.ARRAY) {
                    String id = argCtx.getText();
                    pseudoFunction.mapArrayParameter(id, this.scope.getVariableAllScope(id), i);
                } else if (pseudoFunction.getParamAt(i).getPrimitiveType() == PrimitiveType.STRING){
                    
                    if(argCtx.getText().replaceAll("\".+?\"", "").contains("+")) {
                        Printer.getInstance().print("String concatenation not supported.", callCtx.getStart().getLine());
                        RuntimeManager.getInstance().killExecution();
                    }else if (argCtx.getText().contains("\"")) {
                        String id = argCtx.getText().replaceAll("^\"+|\"+$", "");
                    
                        PseudoValue paramValue = pseudoFunction.getParamAt(i);
                        paramValue.setValue(id);
                    } else {
                        String id = argCtx.getText();

                        PseudoValue paramValue = pseudoFunction.getParamAt(i);
                        paramValue.setValue(scope.getVariableAllScope(id).getValue());
                    }
                    
                } else { // evaluate non-array variable right away
                    
                    EvaluateCommand evalCommand = new EvaluateCommand(argCtx, this.scope);
                    evalCommand.execute();

                    if (i < pseudoFunction.getParameterCount()) {
                        PseudoValue paramValue = pseudoFunction.getParamAt(i);
                        paramValue.setValue(evalCommand.getEvaluated().toPlainString());
                    }
                } 
            }
        }

        PseudoFunction funcInstance = new PseudoFunction(pseudoFunction);
        FunctionControlTracker.getInstance().enterFunction(funcInstance);
        funcInstance.execute();

        System.out.println(callCtx.getText());
        this.strExp = this.strExp.replaceFirst(Pattern.quote(callCtx.getText()), Matcher.quoteReplacement(funcInstance.getReturnValue().getValue().toString()));
        System.out.println(this.strExp);

        FunctionControlTracker.getInstance().exitFunction();
    }

    public void evaluateVar(MutableContext mutableCtx) {
    
        // non-array variable
        if (mutableCtx.IDENTIFIER() != null && mutableCtx.LeftBracket() == null) {
            PseudoValue pseudoValue = this.scope.getVariableAllScope(mutableCtx.IDENTIFIER().getText());
            this.strExp =  this.strExp.replaceFirst(Pattern.quote(mutableCtx.IDENTIFIER().getText()),Matcher.quoteReplacement(pseudoValue.getValue().toString()));

        } 
        else { // access array

            EvaluateCommand evalCommand = new EvaluateCommand(mutableCtx.simpleExpression(), this.scope);
            evalCommand.execute();

            int arrIndex = evalCommand.getEvaluated().intValue();
            this.strExp = this.strExp.replaceFirst(mutableCtx.IDENTIFIER().getText(), "");

            PseudoValue pseudoValue = scope.getVariableAllScope(mutableCtx.IDENTIFIER().getText());
            PseudoArray pseudoArray = (PseudoArray) pseudoValue.getValue();

            if (pseudoArray.getValueAt(arrIndex) != null) { 
                if (pseudoArray.getValueAt(arrIndex).getValue() != null) {
                    this.strExp = this.strExp.replaceFirst("\\[.+?\\]", pseudoArray.getValueAt(arrIndex).getValue().toString());
                } else {  // Value at index is not initialized
                    Printer.getInstance().print("Value at index is null. Might be uninitialized.", mutableCtx.getStart().getLine());
                    RuntimeManager.getInstance().killExecution();
                }
            } else { // Index is out of bounds
                Printer.getInstance().print("Index is out of bounds.", mutableCtx.getStart().getLine());
                RuntimeManager.getInstance().killExecution();
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

    public BigDecimal getEvaluated() {
        return this.evaluated;
    }

}
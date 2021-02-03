package model.visitors;

import java.util.*;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import parser.PSCParser.ParamsContext;
import parser.PSCParser.TypeSpecifierContext;
import parser.PSCParser.ParameterContext;
import parser.PSCParser.TypeSpecifierSelectorContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

import model.objects.*;
import model.ScopeManager;
import model.Scope;
import model.Console;
import model.semcheck.MultipleFuncSemCheck;

public class ParameterVisitor implements ParseTreeListener {

    private PseudoFunction func;

    public ParameterVisitor(PseudoFunction func) {
        this.func = func;
    }

    public void visit(List<ParameterContext> params) {
        
        for (ParameterContext paramCtx : params) {
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(this, paramCtx);
        }
        
    }

    @Override
	public void enterEveryRule(ParserRuleContext ctx) {

        if (ctx instanceof ParameterContext) { // responsible for checking between parameters of function
            ParameterContext parameterCtx = (ParameterContext) ctx;
            TypeSpecifierSelectorContext typeSpecifierSelectorContext= parameterCtx.typeSpecifierSelector();

            if (func.searchParameter(parameterCtx.IDENTIFIER().getText())) {
                Console.log("Duplicate Parameter found", ctx.getStart().getLine());
            } else {
                PseudoValue pseudoValue = null;

                //check if array or not
                if (typeSpecifierSelectorContext.typeSpecifier() != null) { //primitive non-array
                    TypeSpecifierContext typeSpecifierContext= typeSpecifierSelectorContext.typeSpecifier();
                    if (typeSpecifierContext.Int() != null) {
                        pseudoValue = new PseudoValue(null, "int");
                        this.func.addParameter(parameterCtx.IDENTIFIER().getText(), pseudoValue);
                    } else if (typeSpecifierContext.Bool() != null) {
                        pseudoValue = new PseudoValue(null, "float");
                        this.func.addParameter(parameterCtx.IDENTIFIER().getText(), pseudoValue);
                    } else if (typeSpecifierContext.Float() != null) {
                        pseudoValue = new PseudoValue(null, "bool");
                        this.func.addParameter(parameterCtx.IDENTIFIER().getText(), pseudoValue);
                    } else if (typeSpecifierContext.String() != null) {
                        pseudoValue = new PseudoValue(null, "String");
                        this.func.addParameter(parameterCtx.IDENTIFIER().getText(), pseudoValue);
                    }



                                
                } else if (typeSpecifierSelectorContext.arrayTypeSpecifier() != null ) {
                    TypeSpecifierContext typeSpecifierContext= typeSpecifierSelectorContext.arrayTypeSpecifier().typeSpecifier();
                    PseudoArray pseudoArray = null;

                    if (typeSpecifierContext.Int() != null) {
                    pseudoArray = PseudoArray.createArray("int", parameterCtx.IDENTIFIER().getText());
                    } else if (typeSpecifierContext.Bool() != null) {
                    pseudoArray = PseudoArray.createArray("bool", parameterCtx.IDENTIFIER().getText());
                    } else if (typeSpecifierContext.Float() != null) {
                    pseudoArray = PseudoArray.createArray("float", parameterCtx.IDENTIFIER().getText());
                    } else if (typeSpecifierContext.String() != null) {
                    pseudoArray = PseudoArray.createArray("String", parameterCtx.IDENTIFIER().getText());
                    }
                    pseudoValue = new PseudoValue(pseudoArray, "array");
                    this.func.addParameter(parameterCtx.IDENTIFIER().getText(), pseudoValue);

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
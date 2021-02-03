package model.visitors;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import parser.PSCParser.ScopedVariableDeclarationContext;
import parser.PSCParser.VariableDeclarationContext;
import parser.PSCParser.VariableDeclarationInitializeContext;
import parser.PSCParser.ArrayVariableDeclarationInitializeContext;
import parser.PSCParser.ArrayVariableDeclarationContext;
import parser.PSCParser.TypeSpecifierContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

import model.*;
import model.semcheck.*;
import model.objects.*;
import model.commands.*;

public class VarDeclaratorVisitor implements ParseTreeListener {

    public VarDeclaratorVisitor() {}

    public void visit(ScopedVariableDeclarationContext ctx) {
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, ctx);
    }

    @Override
	public void enterEveryRule(ParserRuleContext ctx) {

		if (ctx instanceof ScopedVariableDeclarationContext) {
			ScopedVariableDeclarationContext scopedVarDecCtx = (ScopedVariableDeclarationContext) ctx;
		
			PseudoValue pseudoValue = null;

			MultipleVarSemCheck mulVarSemCheck = new MultipleVarSemCheck(scopedVarDecCtx);
			mulVarSemCheck.check();
			
			if (scopedVarDecCtx.variableDeclaration() != null) {
				VariableDeclarationContext varDecCtx = scopedVarDecCtx.variableDeclaration();
				VariableDeclarationInitializeContext varDecInitCtx = varDecCtx.variableDeclarationInitialize();

				// declaring
				if (varDecCtx.typeSpecifier().Int() != null) {
					pseudoValue = new PseudoValue(null, "int");
				} else if (varDecCtx.typeSpecifier().Bool() != null) {
					pseudoValue = new PseudoValue(null, "bool");
				} else if (varDecCtx.typeSpecifier().String() != null) {
					pseudoValue = new PseudoValue(null, "String");
				} else if (varDecCtx.typeSpecifier().Float() != null) {
					pseudoValue = new PseudoValue(null, "float");
				}

				if (pseudoValue != null && varDecCtx.ConstantKey() != null) {
					pseudoValue.markConst();
				} 

				if (varDecInitCtx.Assign() != null) { // assigning
					// check if rhs is compatible with id type
					TypeMismatchSemCheck typeMMSemCheck = new TypeMismatchSemCheck(pseudoValue, varDecInitCtx.simpleExpression());
					typeMMSemCheck.check();

					InitializeCommand initializeCommand = new InitializeCommand(varDecCtx, varDecInitCtx.simpleExpression());
					addCommand(initializeCommand);
				}  

				Scope scope = ScopeManager.getInstance().getScope();
				scope.addVariable(varDecInitCtx.IDENTIFIER().getText(), pseudoValue);

				
			

			} else if (scopedVarDecCtx.arrayVariableDeclaration() != null) {

				ArrayVariableDeclarationContext arrVarDecCtx = scopedVarDecCtx.arrayVariableDeclaration();
				ArrayVariableDeclarationInitializeContext arrVarDecInitCtx = scopedVarDecCtx.arrayVariableDeclaration().arrayVariableDeclarationInitialize();

				PseudoArray pseudoArray = null;

				if (arrVarDecInitCtx.IDENTIFIER() != null) {
					if (arrVarDecCtx.arrayTypeSpecifier().typeSpecifier().Int() != null) {
						pseudoArray = PseudoArray.createArray("int", arrVarDecInitCtx.IDENTIFIER().getText());
						pseudoArray.setPrimitiveType(PrimitiveType.INT);
					} else if (arrVarDecCtx.arrayTypeSpecifier().typeSpecifier().Bool() != null) {
						pseudoArray = PseudoArray.createArray("bool", arrVarDecInitCtx.IDENTIFIER().getText());
						pseudoArray.setPrimitiveType(PrimitiveType.BOOLEAN);
					} else if (arrVarDecCtx.arrayTypeSpecifier().typeSpecifier().String() != null) {
						pseudoArray = PseudoArray.createArray("String", arrVarDecInitCtx.IDENTIFIER().getText());
						pseudoArray.setPrimitiveType(PrimitiveType.STRING);
					} else if (arrVarDecCtx.arrayTypeSpecifier().typeSpecifier().Float() != null) {
						pseudoArray = PseudoArray.createArray("float", arrVarDecInitCtx.IDENTIFIER().getText());
						pseudoArray.setPrimitiveType(PrimitiveType.FLOAT);
					}
				}

				pseudoValue = new PseudoValue(pseudoArray, "array");
				
				if (pseudoValue != null && arrVarDecCtx.ConstantKey() != null) {
					pseudoValue.markConst();
				} 

				// assigning array to array
				if (arrVarDecInitCtx.mutable() != null) {

					String id = arrVarDecInitCtx.mutable().IDENTIFIER().getText();
					PseudoValue pv = ScopeManager.getInstance().searchMyScopeVariable(id);
					

					if (pv == null) {
						Console.log("UndeclaredVariable error: " + id + " not found." , arrVarDecInitCtx.getStart().getLine());
					} else {
						String errorTemplate = "TypeMismatch Error: ";
						
						if (pv.getPrimitiveType() != PrimitiveType.ARRAY) {
							String msg = "Expected array for variable '" + id + "'";
							Console.log(errorTemplate + msg, arrVarDecInitCtx.getStart().getLine());
						} else {
							PseudoArray pa = (PseudoArray) pseudoValue.getValue();
							PseudoArray pa1 = (PseudoArray) pv.getValue();

							if(pa.getPrimitiveType() == PrimitiveType.BOOLEAN && pa1.getPrimitiveType() != PrimitiveType.BOOLEAN) {
								String msg = "Expected boolean array for variable '" + id + "'";
								Console.log(errorTemplate + msg, arrVarDecInitCtx.getStart().getLine());
							}
							else if(pa.getPrimitiveType() == PrimitiveType.INT && pa1.getPrimitiveType() != PrimitiveType.INT) {
								String msg = "Expected integer array for variable '" + id + "'";
								Console.log(errorTemplate + msg, arrVarDecInitCtx.getStart().getLine());
							}
							else if(pa.getPrimitiveType() == PrimitiveType.FLOAT && pa1.getPrimitiveType() != PrimitiveType.FLOAT) {
								String msg = "Expected float array for variable '" + id + "'";
								Console.log(errorTemplate + msg, arrVarDecInitCtx.getStart().getLine());
							}
							else if(pa.getPrimitiveType() == PrimitiveType.STRING && pa1.getPrimitiveType() != PrimitiveType.STRING) {
								String msg = "Expected String array for variable '" + id + "'";
								Console.log(errorTemplate + msg, arrVarDecInitCtx.getStart().getLine());
							}
						}
						InitializeArrCommand initializeArrCommand = new InitializeArrCommand(arrVarDecInitCtx.IDENTIFIER(), arrVarDecInitCtx.mutable());
						addCommand(initializeArrCommand);
					}

				// array initialize
				} else if (arrVarDecInitCtx.createArrayExpression() != null) {

					TypeSpecifierContext typeSpecifier = arrVarDecInitCtx.createArrayExpression().typeSpecifier();
					
					// if there is create type[]
					if ((arrVarDecCtx.arrayTypeSpecifier().typeSpecifier().Int() != null && typeSpecifier.Int() == null) || 
					(arrVarDecCtx.arrayTypeSpecifier().typeSpecifier().Float() != null && typeSpecifier.Float() == null) || 
					(arrVarDecCtx.arrayTypeSpecifier().typeSpecifier().String() != null && typeSpecifier.String() == null) ||
					(arrVarDecCtx.arrayTypeSpecifier().typeSpecifier().Bool() != null && typeSpecifier.Bool() == null) ) {
						Console.log("TypeMismatch Error: Initialize type is not the same as variable type.", arrVarDecCtx.getStart().getLine() );
					}	 

					// if size is int int[size] <--
					TypeMismatchSemCheck typeMMSemCheck = new TypeMismatchSemCheck(new PseudoValue(null, "int"), arrVarDecInitCtx.createArrayExpression().simpleExpression());
					typeMMSemCheck.check();

					CreateArrayCommand createArrCommand = new CreateArrayCommand(arrVarDecInitCtx.createArrayExpression().simpleExpression(), pseudoArray);
					addCommand(createArrCommand);
				}
				

				if (arrVarDecInitCtx.IDENTIFIER() != null) {
					Scope scope = ScopeManager.getInstance().getScope();
					scope.addVariable(arrVarDecInitCtx.IDENTIFIER().getText(), pseudoValue);
				}
				
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
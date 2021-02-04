package model;

import parser.ThanosParser.SimpleExpressionContext;

import model.objects.*;
import model.commands.*;
import model.*;
import java.math.BigDecimal;


public class Util {

    public static void assignValue(PseudoValue value, BigDecimal evaluated) {

        if(value.getPrimitiveType() == PrimitiveType.INT) {
			value.setValue(Integer.toString(evaluated.intValue()));
		}
		else if(value.getPrimitiveType() == PrimitiveType.FLOAT) {
			value.setValue(Float.toString(evaluated.floatValue()));
		}
		else if(value.getPrimitiveType() == PrimitiveType.BOOLEAN) {
			if(evaluated.intValue() == 1) {
				value.setValue(true);
			} else {
				value.setValue(false);
			}
		}
    }

	public static boolean checkCondition(SimpleExpressionContext conditionCtx, Scope scope) {
        
		if (conditionCtx.getText().contains("(T)")) {
			return true;
		} 

		if (conditionCtx.getText().contains("(F)")) {
			return false;
		}

		EvaluateCommand evalCommand = new EvaluateCommand(conditionCtx, scope);
		evalCommand.execute();

		return (evalCommand.getEvaluated().intValue() == 1);
    }
}
package model.SemCheck;

import model.objects.*;
import model.Console;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import model.*;

public class ConstantSemCheck implements SemCheck {

	private TerminalNode id;
	private int line;

	public ConstantSemCheck(TerminalNode id) {
		this.id = id;

		Token first = this.id.getSymbol();
		this.line = first.getLine();
	}


    @Override
	public void check() {

		String stringId = id.getText();
		PseudoValue pseudoValue = ScopeManager.getInstance().searchMyScopeVariable(stringId);

		if (pseudoValue != null && pseudoValue.isConst()) {
			Console.log("ConstantReassignment Error: Constant variables can only be assigned once.", this.line);
		}
		
	}




} 
package model.commands;

import java.util.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import parser.PSCParser.MutableContext;
import model.*;
import model.objects.*;
import model.semcheck.*;


public class InitializeArrCommand implements Command {

    private Scope scope; 
    private TerminalNode id;
    private MutableContext mutableCtx;

    
    public InitializeArrCommand(TerminalNode id, MutableContext mutableCtx) {
        this.mutableCtx = mutableCtx;
        this.id = id;
        this.scope = ScopeManager.getInstance().getScope();
    }

    @Override
    public void execute() {
        PseudoValue rhsValue = scope.getVariableAllScope(mutableCtx.IDENTIFIER().getText());

		PseudoValue pseudoValue = scope.getVariableAllScope(id.getText());
		pseudoValue.setValue(rhsValue.getValue());
    }

}
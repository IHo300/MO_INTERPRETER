package model.commands;

import parser.ThanosParser.SimpleExpressionContext;

import model.*;
import model.objects.*;
import model.SemCheck.*;

public class CreateArrayCommand implements Command {

    private PseudoArray array;
    private SimpleExpressionContext exprCtx;
    private Scope scope;
    private EvaluateCommand evalCommand;

    public CreateArrayCommand(SimpleExpressionContext exprCtx, PseudoArray array) {
        this.exprCtx = exprCtx;
        this.array = array;
        this.scope = ScopeManager.getInstance().getScope();


        UndeclaredSemCheck undeclaredSemCheck = new UndeclaredSemCheck(exprCtx);
        undeclaredSemCheck.check();

        evalCommand = new EvaluateCommand(this.exprCtx);
    }

    @Override
    public void execute() {
        evalCommand.execute();

       this.array.initSize(evalCommand.getEvaluated().intValue());
    }





    
}
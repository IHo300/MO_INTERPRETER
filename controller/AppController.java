package controller;

import java.util.*;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CharStream;

import model.*;
import view.Gui;

public class AppController {

    private Parser parser;
    private Gui view;

    public AppController(Parser parser, Gui view) {
        this.parser = parser;
        this.view = view;        
        init();
    }

    public void parse(String input) {
        System.out.println("----------- PARSING ----------");

        ScopeManager.getInstance().reset();
        RuntimeManager.getInstance().reset();
        Console.reset();

        CharStream stream = CharStreams.fromString(input);
        parser.setInput(stream);
        parser.parse();

        if (!Console.getErrorList().isEmpty()) {
            Printer.getInstance().setStatus("Program has compiled with errors", "fail");
            view.updateLogs(Console.getErrorList());
        } else {
            view.updateLogs(null);
        }
    }

    public void execute() {

        // execute all in execution manager
        if (Console.getErrorList().size() == 0) {
            System.out.println("----------- EXECUTING ----------");
            Printer.getInstance().setStatus("Program is executing...", "executing");
            RuntimeManager.getInstance().executeAll();
        }
    }

    public void terminate() {

        if (Console.getErrorList().size() == 0) {
            System.out.println("----------- FORCE TERMINATING ----------");
            RuntimeManager.getInstance().forceKillExecution();
        } 
    }

    private void init() {
        System.out.println("----------- INITIALIZING ----------");
        Console.startInstance(); 
        Printer.getInstance();
        Printer.getInstance().addView(this.view);
        IDEScanner.getInstance();
        IDEScanner.getInstance().addView(this.view);
        FunctionReturnTracker.getInstance();
        FunctionControlTracker.getInstance();
        StatementControlTracker.getInstance();
        RuntimeManager.getInstance();
    }

    public void showTree(String input) {
        System.out.println("----------- SHOW TREE ----------");
        CharStream stream = CharStreams.fromString(input);
        parser.setInput(stream);
        parser.showTree();
    }
}
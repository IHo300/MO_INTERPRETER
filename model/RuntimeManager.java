package model;

import java.util.*;
import model.commands.*;
import model.objects.*;


public class RuntimeManager {

    private static RuntimeManager instance = null;
    private static List<Command> commandList;
    private RuntimeThread thread = null;
    private PseudoFunction funcDecl;
    private boolean inFunctionDeclaration;
    

    public static RuntimeManager getInstance() {
        if (instance == null) {
            instance = new RuntimeManager();
            commandList = new ArrayList<>();
            System.out.println("Runtime Manager initialized");
        }

        return instance;
    }


    public void initialize() {
        commandList = new ArrayList<Command>();
    }

    public void reset() {
        if (commandList != null) {
            commandList.clear();
        }

        System.out.println("Runtime was reset");
    }

    public void addCommand(Command c) {

        if (inFunctionDeclaration) {
            System.out.println("Added " + c.getClass() + " to " + funcDecl.getName());
            funcDecl.addCommand(c);
        } else {
            System.out.println("Added " + c.getClass() + " to execution command list");
            commandList.add(c);
        }
    }
    public boolean hasCommands() {
        return this.commandList.size() > 0;
    }

    public void executeAll() {
        thread = new RuntimeThread(commandList);
        thread.start();
    }

    public void pauseExecution() {
        this.thread.setExecFlag(false);
    }

    public void resumeExecution() {
        this.thread.setExecFlag(true);
    }

    public void killExecution() {
        this.thread.kill();
    }

    public void forceKillExecution() {
        if (this.thread != null) { //  if thread has even started
            if (this.thread.isExecuting()) { // if thread is executing
                this.thread.kill();
            }
        }
    }

    public boolean canExec() {
        return this.thread.canExec();
    }

    public void openFunctionDeclaration(PseudoFunction funcDecl) {
        this.funcDecl = funcDecl;
        this.inFunctionDeclaration = true;
    }

    public void closeFunctionDeclaration() {
        this.funcDecl = null;
        this.inFunctionDeclaration = false;
    }

    public PseudoFunction getCurrentFunction() {
        return this.funcDecl;        
    }
}

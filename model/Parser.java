package model;

import java.util.*;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.gui.TreeViewer;

import parser.ThanosLexer;
import parser.ThanosParser;
import model.ThanosCustomListener;
import model.ScopeManager;

public class Parser {

    private CharStream input;

    public Parser() { }

    public Parser(CharStream input) {
        this.input = input;
    }
    
    public void setInput(CharStream input) {
        this.input = input;
    }

    public void parse() {
        Lexer lexer = new ThanosLexer(this.input);

        ThanosSyntaxChecker syntaxListener = new ThanosSyntaxChecker();
        lexer.removeErrorListeners();
        lexer.addErrorListener( syntaxListener );
        
        TokenStream tokenStream = new CommonTokenStream(lexer);
        ThanosParser parser = new ThanosParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener( syntaxListener );

        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        ThanosCustomListener listener = new ThanosCustomListener();
        walker.walk(listener, tree);
   }

   public void showTree() {
       Lexer lexer = new ThanosLexer(this.input);
       lexer.removeErrorListeners();

       TokenStream tokenStream = new CommonTokenStream(lexer);
       ThanosParser parser = new ThanosParser(tokenStream);
       parser.removeErrorListeners();
       
       ParseTree tree = parser.compilationUnit();

        // Tree inspector
        TreeViewer viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()),tree);
        viewer.open();
   }


}

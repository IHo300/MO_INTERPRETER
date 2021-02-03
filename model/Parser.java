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

import parser.PSCLexer;
import parser.PSCParser;
import model.PSCCustomListener;
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
        Lexer lexer = new PSCLexer(this.input);

        PSCSyntaxChecker syntaxListener = new PSCSyntaxChecker();
        lexer.removeErrorListeners();
        lexer.addErrorListener( syntaxListener );
        
        TokenStream tokenStream = new CommonTokenStream(lexer);
        PSCParser parser = new PSCParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener( syntaxListener );

        ParseTree tree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        PSCCustomListener listener = new PSCCustomListener();
        walker.walk(listener, tree);
   }

   public void showTree() {
       Lexer lexer = new PSCLexer(this.input);
       lexer.removeErrorListeners();

       TokenStream tokenStream = new CommonTokenStream(lexer);
       PSCParser parser = new PSCParser(tokenStream);
       parser.removeErrorListeners();
       
       ParseTree tree = parser.program();

        // Tree inspector
        TreeViewer viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()),tree);
        viewer.open();
   }


}

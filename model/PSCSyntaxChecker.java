package model;

import java.util.*;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Parser;

public class PSCSyntaxChecker extends BaseErrorListener {

  @Override
  public void syntaxError( Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                           String msg, RecognitionException e ) {

    PSCErrorGenerator generator = new PSCErrorGenerator();
    
    // List<String> stack = (recognizer).getRuleInvocationStack(); 
    // Collections.reverse(stack);
                            
    Console.log(generator.generateMsg(msg), line);
  }


}
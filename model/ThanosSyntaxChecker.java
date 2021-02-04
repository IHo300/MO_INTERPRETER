package model;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;

public class ThanosSyntaxChecker extends BaseErrorListener {

  @Override
  public void syntaxError( Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                           String msg, RecognitionException e ) {

    ThanosErrorGenerator generator = new ThanosErrorGenerator();
    
    // List<String> stack = (recognizer).getRuleInvocationStack(); 
    // Collections.reverse(stack);
                            
    Console.log(generator.generateMsg(msg), line);
  }


}
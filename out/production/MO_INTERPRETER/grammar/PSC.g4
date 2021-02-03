grammar PSC;

/*keywords*/
ConstantKey : 'constant';
Int : 'int';
Float : 'float';
String : 'String';
Bool : 'bool';

Main: 'main';
Do : 'do';
Else : 'else';
For : 'for';
If : 'if';
Then: 'then';
Return : 'return';
Void : 'void';
While : 'while';
Func : 'func';
Create : 'create';
Print : 'print';
Scan: 'scan';

LeftParen : '(';
RightParen : ')';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';

Less : '<';
LessEqual : '<=';
Greater : '>';
GreaterEqual : '>=';

Plus : '+';
Minus : '-';
Star : '*';
Div : '/';

AndAnd : '&&';
OrOr : '||';
NotEqual : '!=';
Not : '!';

Colon : ':';
Semi : ';';
Comma : ',';

Equal : '==';

Assign : '=';

Dot : '.';
Ellipsis : '...';

Up : 'up';
Down : 'down';
To : 'to';

/* main program */

program
    : functionDeclaration* mainProgram EOF
    ;

mainProgram
    :  Main LeftParen RightParen compoundStmt
    ;


/* ------- */
variableDeclaration 
    : ConstantKey? typeSpecifier variableDeclarationInitialize Semi 
    ;

scopedVariableDeclaration
    : variableDeclaration 
    | arrayVariableDeclaration 
    ;

variableDeclarationInitialize
    : IDENTIFIER 
    | IDENTIFIER Assign simpleExpression 
    ;

typeSpecifier
    : Int
    | Bool
    | String
    | Float
    ;

/* ------- */

arrayTypeSpecifier
    : typeSpecifier LeftBracket RightBracket
    ;

arrayVariableDeclaration
    : ConstantKey? arrayTypeSpecifier arrayVariableDeclarationInitialize Semi
    ;

arrayVariableDeclarationInitialize
    : IDENTIFIER
    | IDENTIFIER Assign createArrayExpression 
    | IDENTIFIER Assign mutable
    ;

/* ------- */

typeSpecifierSelector
    : typeSpecifier
    | arrayTypeSpecifier
    ;

functionDeclaration
    : Func (typeSpecifier | arrayTypeSpecifier | Void) IDENTIFIER LeftParen params RightParen compoundStmt
    ;

params
    : parameter (Comma parameter)*
    | /*epsilon */
    ;

parameter
    : typeSpecifierSelector IDENTIFIER
    ;

/* statements */
statement
    : expressionStmt
    | scanStmt
    | printStmt
    | selectionStmt
    | iterationStmt
    | returnStmt
    ;

expressionStmt
    : (assignmentStandaloneExpression|call) Semi
    ;

compoundStmt
    : LeftBrace (compoundStmtBody)* RightBrace
    ;

compoundStmtBody
    : scopedVariableDeclaration
    | statement
    ;
    
scanStmt
    : Scan LeftParen StringLiteral Comma IDENTIFIER RightParen Semi
    ;

printStmt
    : Print LeftParen printParams RightParen Semi
    | Print LeftParen ((StringLiteral | IDENTIFIER) (StringLiteral | IDENTIFIER)+) {notifyErrorListeners("Missing quotation marks for these print parameters.");} RightParen Semi 
    ;
   
printParams
    : printParamsSelector (Plus printParamsSelector)*
    | printParamsSelector Plus (printParamsSelector Plus)+ {notifyErrorListeners("Extra '+' symbols found.");}
    | /*epsilon */
    ;

    
printParamsSelector
    : StringLiteral
    | call
    | IDENTIFIER
    ;

selectionStmt
    :  If LeftParen simpleExpression RightParen Then compoundStmt elseSelector?
    ;

elseSelector
    : Else Then compoundStmt
    | Else selectionStmt
    ;

iterationStmt
    : whileStatement
    | forStatement
    ;

whileStatement
    : While IDENTIFIER iterationToStatement simpleExpression compoundStmt
    ;

forStatement
    : For loopDeclaration iterationToStatement simpleExpression compoundStmt
    ;

iterationToStatement
    : Upto
    | Downto
    | g=(Up|Down) {notifyErrorListeners("Missing 'to' after " + $g.text + ".");}
    | To {notifyErrorListeners("Missing 'up' or 'down' in iteration statement.");}
    | StringLiteral {notifyErrorListeners("Incorrect iterator. Should be 'up to' or 'down to");}
    ;

DoubleQuote
    : '"'
    ;

loopDeclaration
    : Int IDENTIFIER Assign simpleExpression
    | Int IDENTIFIER {notifyErrorListeners("Newly declared variables in loops needs to be assigned to a value immediately.");}
    | IDENTIFIER Assign simpleExpression
    | IDENTIFIER
    ;

returnStmt
    : Return simpleExpression Semi
    ;


/* expressions */

assignmentStandaloneExpression
    : mutable Assign (simpleExpression | createArrayExpression)
    ;

createArrayExpression
    : Create typeSpecifier LeftBracket simpleExpression RightBracket
    ;

simpleExpression
    : andExpression
    | simpleExpression OrOr andExpression
    | simpleExpression simpleExpression+ {notifyErrorListeners("Missing valid operators.");}
    ;

andExpression
    : unaryRelExpression
    | andExpression AndAnd unaryRelExpression
    ;

unaryRelExpression
    : Not unaryRelExpression 
    | relExpression
    ;

relExpression
    : sumExpression relOperator sumExpression
    | sumExpression Assign {notifyErrorListeners("Wrong relational operator '='. Should be '=='.");} sumExpression 
    | sumExpression 
    ;

relOperator
    : LessEqual
    | Less
    | Greater
    | GreaterEqual
    | Equal
    | NotEqual
    ;

sumExpression
    : sumExpression sumOperator mulExpression 
    | sumExpression sumOperator g=sumOperator+ mulExpression {notifyErrorListeners("An extra '" + $g.text + "' operator is found. Remove this.");}
    | mulExpression
    ;

sumOperator
    : Plus
    | Minus
    ;

mulExpression
    : mulExpression mulOperator unaryExpression
    | mulExpression mulOperator g=mulOperator+ unaryExpression {notifyErrorListeners("An extra '" + $g.text + "' operator is found. Remove this.");}
    | unaryExpression
    ;

mulOperator
    : Star
    | Div
    ;

unaryExpression
    : unaryOperator unaryExpression
    | factor
    ;

unaryOperator
    : Minus
    ;

factor
    : immutable
    | mutable
    ;

mutable
    : IDENTIFIER
    | IDENTIFIER  LeftBracket simpleExpression RightBracket
    ;

immutable
    : call
    | constant
    | LeftParen simpleExpression RightParen
    ;

call
    : IDENTIFIER LeftParen arguments RightParen
    ;


arguments
    : simpleExpression (Comma simpleExpression)*
    | /*epsilon */
    ;



constant
    : BOOLCONSTANT
    | INTEGERCONSTANT
    | StringLiteral
    | FLOATCONSTANT
    ;

/* primitives */
fragment
NonDigit
    : [a-zA-Z_]
    ;

fragment
DoubleQuotation
    : '"'
    ;

fragment
Digit
    : [0-9]
    ;
    
fragment
DigitSequence
    : Digit+
    ;
    
fragment
FloatSuffix
    : 'f'
    ;

fragment
FractionalConstant
    :   DigitSequence? '.' DigitSequence
    |   DigitSequence '.'
    ;

fragment
DecimalFloatingConstant
    :   FractionalConstant FloatSuffix
    ;

fragment
EscapeSequence
    :   SimpleEscapeSequence
    ;

fragment
SimpleEscapeSequence
    :   '\\' ['"?abfnrtv\\]
    ;

fragment
SChar
    :   ~["\\\r\n]
    |   EscapeSequence
    |   '\\\n'   // Added line
    |   '\\\r\n' // Added line
    ;

fragment
SCharSequence
    :   SChar+
    ;

fragment
Space
    : [ ]
    ;

Upto
    : Up Space+ To
    ;

Downto
    : Down Space+ To
    ;

StringLiteral
    :   DoubleQuotation SCharSequence* DoubleQuotation
    ;

/*tokens*/
INTEGERCONSTANT
    : DigitSequence
    ;

FLOATCONSTANT
    : DecimalFloatingConstant
    ;

BOOLCONSTANT
    : 'T'
    | 'F'
    ;

STRINGCONSTANT
    : DoubleQuotation SCharSequence? DoubleQuotation
    ;


IDENTIFIER
    :  NonDigit (NonDigit|Digit)*
    ; 

BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;

WS 
    : [ \t\r\n]+ -> skip 
    ; // skip spaces, tabs, newlines, \r (Windows)
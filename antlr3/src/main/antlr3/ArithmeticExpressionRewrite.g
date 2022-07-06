grammar ArithmeticExpressionRewrite;
options { output=AST; }

@lexer::header {
package org.streamlet.arithmetic.expression.compiler.antlr3;
}
@parser::header {
package org.streamlet.arithmetic.expression.compiler.antlr3;
}

WS : (' ' | '\t' | '\n') { skip(); };
ADD: '+';
SUB: '-';
MUL: '*';
DIV: '/';
MOD: '%';
EXP: '^';
LPAREN: '(';
RPAREN: ')';
COMMA: ',';
E: 'e';
PI: 'pi';
NUM: (('0'..'9')+ | ('0'..'9')* '.' ('0'..'9')+) (('E' | 'e')('+'|'-')? ('0'..'9')+)?;
FUNC: ('A'..'Z' | 'a'..'z' | '_')('A'..'Z' | 'a'..'z' | '_' | '0'..'9')*;

line: expr EOF -> expr
    ;

expr: (ADD | SUB)? term ((ADD | SUB) ^term)*
    ;

term: factor ((MUL | DIV | MOD) ^factor)*
    ;

factor: expee (options{greedy=true;}: EXP ^factor)*
      ;

expee: num
     | LPAREN expr RPAREN -> expr
     | FUNC LPAREN params RPAREN -> ^(FUNC params)
     ;

params: expr (COMMA expr)* -> expr+
      ;

num: E
   | PI
   | NUM
   ;

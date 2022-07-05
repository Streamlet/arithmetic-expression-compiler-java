grammar ArithmeticExpression;

@lexer::header {
package org.streamlet.arithmetic.expression.compiler.antlr3.generated;
}
@parser::header {
package org.streamlet.arithmetic.expression.compiler.antlr3.generated;

import org.streamlet.arithmetic.expression.compiler.shared.ast.*;
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

// line: expr EOF
//     ;
line returns [ASTNode result]
    : expr EOF { $result = $expr.result; }
    ;

// expr: (ADD | SUB)? term ((ADD | SUB) term)*
//     ;
expr returns [ASTNode result]
    : (t1=term { $result = $t1.result; }
      | ADD t2=term { $result = new ASTUnaryOperator(UnaryOperator.POS, $t2.result); }
      | SUB t3=term { $result = new ASTUnaryOperator(UnaryOperator.NEG, $t3.result); }
      )
      ( ADD t4=term { $result = new ASTBinaryOperator(BinaryOperator.ADD, $expr.result, $t4.result); }
      | SUB t5=term { $result = new ASTBinaryOperator(BinaryOperator.SUB, $expr.result, $t5.result); }
      )*
    ;

// term: factor ((MUL | DIV | MOD) factor)*
//     ;
term returns [ASTNode result]
    : f1=factor { $result = $f1.result; }
      ( MUL f2=factor { $result = new ASTBinaryOperator(BinaryOperator.MUL, $term.result, $f2.result); }
      | DIV f3=factor { $result = new ASTBinaryOperator(BinaryOperator.DIV, $term.result, $f3.result); }
      | MOD f4=factor { $result = new ASTBinaryOperator(BinaryOperator.MOD, $term.result, $f4.result); }
      )*
    ;

// factor: expee (options{greedy=true;}: EXP factor)*
//       ;
factor returns [ASTNode result]
      : expee { $result = $expee.result; }
        (options{greedy=true;}:
        EXP f1=factor { $result = new ASTBinaryOperator(BinaryOperator.EXP, $expee.result, $f1.result); }
        )*
      ;

// expee: num
//      | LPAREN expr RPAREN
//      | FUNC LPAREN params RPAREN
//      ;
expee returns [ASTNode result]
     : num { $result = $num.result; }
     | LPAREN expr RPAREN { $result = $expr.result; }
     | FUNC LPAREN params RPAREN { $params.result.assignName($FUNC.getText()); $result = $params.result; }
     ;

// params: expr (COMMA expr)*
//       ;
params returns [ASTFunction result]
      : e1=expr { $result = new ASTFunction(); $result.addArgument($e1.result); }
       (COMMA e2=expr { $result = $params.result; $result.addArgument($e2.result); }
       )*
      ;

// num: E
//    | PI
//    | NUM
//    ;
num returns [ASTNumber result]
   : E { $result = new ASTNumber(Math.E); }
   | PI { $result = new ASTNumber(Math.PI); }
   | NUM { $result = new ASTNumber(Double.parseDouble($NUM.getText())); }
   ;

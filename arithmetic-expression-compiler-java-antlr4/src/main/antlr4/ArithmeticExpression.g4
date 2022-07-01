grammar ArithmeticExpression;

line: expr EOF
    ;

expr: term
    | ADD term
    | SUB term
    | expr ADD term
    | expr SUB term
    ;

term: factor
    | term MUL factor
    | term DIV factor
    | term MOD factor
    ;

factor: expee
      | expee EXP factor
      ;

expee: num
     | LPAREN expr RPAREN
     | FUNC LPAREN params RPAREN
     ;

params: expr
      | params COMMA expr
      ;

num: E
   | PI
   | NUM
   ;

WS : [ \t\n] -> skip;
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
NUM: ([0-9]+|[0-9]*'.'[0-9]+)([Ee][+-]?[0-9]+)?;
FUNC: [A-Za-z_][A-Za-z_0-9]*;

package org.streamlet.arithmetic.expression.compiler.antlr3;

import lombok.*;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.streamlet.arithmetic.expression.compiler.antlr3.ast.*;
import org.streamlet.arithmetic.expression.compiler.antlr3.generated.*;

public class ASTBuilder {
    @SneakyThrows
    public static ASTNode parse(String s) {
        CharStream input = new ANTLRStringStream(s);
        ArithmeticExpressionLexer lexer = new ArithmeticExpressionLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ArithmeticExpressionParser parser = new ArithmeticExpressionParser(tokens);
        return parser.line();
    }
}

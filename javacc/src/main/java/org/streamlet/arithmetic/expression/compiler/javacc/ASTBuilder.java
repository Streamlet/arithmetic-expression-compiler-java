package org.streamlet.arithmetic.expression.compiler.javacc;

import lombok.SneakyThrows;
import org.streamlet.arithmetic.expression.compiler.javacc.ArithmeticExpressionParser;
import org.streamlet.arithmetic.expression.compiler.shared.ast.*;

import java.io.ByteArrayInputStream;

public class ASTBuilder {
    @SneakyThrows
    public static ASTNode parse(String s) {
        ByteArrayInputStream input = new ByteArrayInputStream(s.getBytes());
        ArithmeticExpressionParser parser = new ArithmeticExpressionParser(input);
        return ArithmeticExpressionParser.line();
    }
}

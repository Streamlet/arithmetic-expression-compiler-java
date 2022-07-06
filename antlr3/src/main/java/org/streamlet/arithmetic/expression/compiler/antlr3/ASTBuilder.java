package org.streamlet.arithmetic.expression.compiler.antlr3;

import lombok.*;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.streamlet.arithmetic.expression.compiler.shared.ast.*;

import java.util.HashMap;
import java.util.Map;

public class ASTBuilder {
    @SneakyThrows
    public static ASTNode parse(String s) {
        CharStream input = new ANTLRStringStream(s);
        ArithmeticExpressionLexer lexer = new ArithmeticExpressionLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ArithmeticExpressionParser parser = new ArithmeticExpressionParser(tokens);
        return parser.line();
    }

    @SneakyThrows
    public static ASTNode parseRewrite(String s) {
        CharStream input = new ANTLRStringStream(s);
        ArithmeticExpressionRewriteLexer lexer = new ArithmeticExpressionRewriteLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ArithmeticExpressionRewriteParser parser = new ArithmeticExpressionRewriteParser(tokens);
        ArithmeticExpressionRewriteParser.line_return lineReturn = parser.line();
        CommonTree tree = (CommonTree)lineReturn.getTree();
        return treeToAst(tree);
    }

    private static ASTNode treeToAst(CommonTree tree) {
        switch (tree.getType()) {
            case ArithmeticExpressionRewriteLexer.ADD:
            case ArithmeticExpressionRewriteLexer.SUB:
                if (tree.getChildCount() == 1) {
                    return unaryOperator(tree);
                } else {
                    return binaryOperator(tree);
                }
            case ArithmeticExpressionRewriteLexer.MUL:
            case ArithmeticExpressionRewriteLexer.DIV:
            case ArithmeticExpressionRewriteLexer.MOD:
            case ArithmeticExpressionRewriteLexer.EXP:
                return binaryOperator(tree);
            case ArithmeticExpressionRewriteLexer.E:
            case ArithmeticExpressionRewriteLexer.PI:
            case ArithmeticExpressionRewriteLexer.NUM:
                return number(tree);
            case ArithmeticExpressionRewriteLexer.FUNC:
                return func(tree);
            default:
                assert false;
        }
        return null;
    }

    final static Map<Integer, UnaryOperator> TOKEN_UNARY_OPERATOR_MAP = new HashMap<Integer, UnaryOperator>() {{
        put(ArithmeticExpressionRewriteLexer.ADD, UnaryOperator.POS);
        put(ArithmeticExpressionRewriteLexer.SUB, UnaryOperator.NEG);
    }};

    final static Map<Integer, BinaryOperator> TOKEN_BINARY_OPERATOR_MAP = new HashMap<Integer, BinaryOperator>() {{
        put(ArithmeticExpressionRewriteLexer.ADD, BinaryOperator.ADD);
        put(ArithmeticExpressionRewriteLexer.SUB, BinaryOperator.SUB);
        put(ArithmeticExpressionRewriteLexer.MUL, BinaryOperator.MUL);
        put(ArithmeticExpressionRewriteLexer.DIV, BinaryOperator.DIV);
        put(ArithmeticExpressionRewriteLexer.MOD, BinaryOperator.MOD);
        put(ArithmeticExpressionRewriteLexer.EXP, BinaryOperator.EXP);
    }};

    private static ASTUnaryOperator unaryOperator(CommonTree tree) {
        assert tree.getChildCount() == 1;
        return new ASTUnaryOperator(TOKEN_UNARY_OPERATOR_MAP.get(tree.getType()),
                treeToAst((CommonTree) tree.getChild(0)));
    }
    private static ASTBinaryOperator binaryOperator(CommonTree tree) {
        assert tree.getChildCount() == 2;
        return new ASTBinaryOperator(TOKEN_BINARY_OPERATOR_MAP.get(tree.getType()),
                treeToAst((CommonTree) tree.getChild(0)), treeToAst((CommonTree) tree.getChild(1)));
    }

    private static ASTNumber number(CommonTree tree) {
        switch (tree.getType()) {
            case ArithmeticExpressionRewriteLexer.E:
                return new ASTNumber(Math.E);
            case ArithmeticExpressionRewriteLexer.PI:
                return new ASTNumber(Math.PI);
            case ArithmeticExpressionRewriteLexer.NUM:
                return new ASTNumber(Double.parseDouble(tree.getText()));
            default:
                assert false;
        }
        return null;
    }

    private static ASTFunction func(CommonTree tree) {
        assert tree.getType() == ArithmeticExpressionRewriteLexer.FUNC;
        ASTFunction f = new ASTFunction();
        for (int i = 0; i < tree.getChildCount(); ++i) {
            f.addArgument(treeToAst((CommonTree) tree.getChild(i)));
        }
        f.assignName(tree.getText());
        return f;
    }
}

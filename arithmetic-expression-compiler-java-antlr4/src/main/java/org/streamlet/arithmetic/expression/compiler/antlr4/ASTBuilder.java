package org.streamlet.arithmetic.expression.compiler.antlr4;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.streamlet.arithmetic.expression.compiler.antlr4.ast.*;
import org.streamlet.arithmetic.expression.compiler.antlr4.generated.ArithmeticExpressionLexer;
import org.streamlet.arithmetic.expression.compiler.antlr4.generated.ArithmeticExpressionListener;
import org.streamlet.arithmetic.expression.compiler.antlr4.generated.ArithmeticExpressionParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ASTBuilder implements ArithmeticExpressionListener {
    public static ASTNode parse(String s) {
        CharStream input = CharStreams.fromString(s);
        ArithmeticExpressionLexer lexer = new ArithmeticExpressionLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ArithmeticExpressionParser parser = new ArithmeticExpressionParser(tokens);
        ASTBuilder astBuilder = new ASTBuilder();
        parser.addParseListener(astBuilder);
        ParseTreeWalker.DEFAULT.walk(astBuilder, parser.line());
        return astBuilder.getResult();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Token {
        int type;
        Double dval;
        String sval;
    }

    Stack<Token> tokenStack = new Stack<>();
    Stack<ASTNode> nodeStack = new Stack<>();

    @Getter
    ASTNode result;

    final static Map<Integer, UnaryOperator> TOKEN_UNARY_OPERATOR_MAP = new HashMap<Integer, UnaryOperator>() {{
        put(ArithmeticExpressionLexer.ADD, UnaryOperator.POS);
        put(ArithmeticExpressionLexer.SUB, UnaryOperator.NEG);
    }};

    final static Map<Integer, BinaryOperator> TOKEN_BINARY_OPERATOR_MAP = new HashMap<Integer, BinaryOperator>() {{
        put(ArithmeticExpressionLexer.ADD, BinaryOperator.ADD);
        put(ArithmeticExpressionLexer.SUB, BinaryOperator.SUB);
        put(ArithmeticExpressionLexer.MUL, BinaryOperator.MUL);
        put(ArithmeticExpressionLexer.DIV, BinaryOperator.DIV);
        put(ArithmeticExpressionLexer.MOD, BinaryOperator.MOD);
        put(ArithmeticExpressionLexer.EXP, BinaryOperator.EXP);
    }};

    @Override
    public void enterLine(ArithmeticExpressionParser.LineContext ctx) {

    }

    @Override
    public void exitLine(ArithmeticExpressionParser.LineContext ctx) {
        result = nodeStack.pop();
        assert nodeStack.empty();
        assert tokenStack.empty();
    }

    @Override
    public void enterExpr(ArithmeticExpressionParser.ExprContext ctx) {

    }

    @Override
    public void exitExpr(ArithmeticExpressionParser.ExprContext ctx) {
        if (ctx.getChildCount() == 1) {
            // expr: term
        } else if (ctx.getChildCount() == 2) {
            // expr: ADD term
            //     | SUB term
            Token op = tokenStack.pop();
            ASTNode term = nodeStack.pop();
            ASTUnaryOperator expr = new ASTUnaryOperator(TOKEN_UNARY_OPERATOR_MAP.get(op.getType()), term);
            nodeStack.push(expr);
        } else if (ctx.getChildCount() == 3) {
            // expr: expr ADD term
            //     | expr SUB term
            Token op = tokenStack.pop();
            ASTNode term = nodeStack.pop();
            ASTNode expr = nodeStack.pop();
            ASTBinaryOperator newExpr = new ASTBinaryOperator(TOKEN_BINARY_OPERATOR_MAP.get(op.getType()), expr, term);
            nodeStack.push(newExpr);
        } else {
            assert false;
        }

    }

    @Override
    public void enterTerm(ArithmeticExpressionParser.TermContext ctx) {

    }

    @Override
    public void exitTerm(ArithmeticExpressionParser.TermContext ctx) {
        if (ctx.getChildCount() == 1) {
            // term: factor
        } else if (ctx.getChildCount() == 3) {
            // term: term MUL factor
            //     | term DIV factor
            //     | term MOD factor
            Token op = tokenStack.pop();
            ASTNode factor = nodeStack.pop();
            ASTNode term = nodeStack.pop();
            ASTBinaryOperator newTerm = new ASTBinaryOperator(TOKEN_BINARY_OPERATOR_MAP.get(op.getType()), term, factor);
            nodeStack.push(newTerm);
        } else {
            assert false;
        }
    }

    @Override
    public void enterFactor(ArithmeticExpressionParser.FactorContext ctx) {

    }

    @Override
    public void exitFactor(ArithmeticExpressionParser.FactorContext ctx) {
        if (ctx.getChildCount() == 1) {
            // factor: expee
        } else if (ctx.getChildCount() == 3) {
            // factor: expee EXP factor
            int exp = tokenStack.pop().getType();
            assert exp == ArithmeticExpressionLexer.EXP;
            ASTNode factor = nodeStack.pop();
            ASTNode expee = nodeStack.pop();
            ASTBinaryOperator newFactor = new ASTBinaryOperator(BinaryOperator.EXP, expee, factor);
            nodeStack.push(newFactor);
        } else {
            assert false;
        }
    }

    @Override
    public void enterExpee(ArithmeticExpressionParser.ExpeeContext ctx) {

    }

    @Override
    public void exitExpee(ArithmeticExpressionParser.ExpeeContext ctx) {
        if (ctx.getChildCount() == 1) {
            // expee: num
        } else if (ctx.getChildCount() == 3) {
            // expee: LPAREN expr RPAREN
            int rparen = tokenStack.pop().getType();
            assert rparen == ArithmeticExpressionLexer.RPAREN;
            int lparen = tokenStack.pop().getType();
            assert lparen == ArithmeticExpressionLexer.LPAREN;
        } else if (ctx.getChildCount() == 4) {
            // expee: FUNC LPAREN params RPAREN
            int rparen = tokenStack.pop().getType();
            assert rparen == ArithmeticExpressionLexer.RPAREN;
            int lparen = tokenStack.pop().getType();
            assert lparen == ArithmeticExpressionLexer.LPAREN;
            Token func = tokenStack.pop();
            ASTFunction params = (ASTFunction) nodeStack.pop();
            params.assignName(func.getSval());
            nodeStack.push(params);
        } else {
            assert false;
        }
    }

    @Override
    public void enterParams(ArithmeticExpressionParser.ParamsContext ctx) {

    }

    @Override
    public void exitParams(ArithmeticExpressionParser.ParamsContext ctx) {
        if (ctx.getChildCount() == 1) {
            // params: expr
            ASTNode expr = nodeStack.pop();
            ASTFunction params = new ASTFunction();
            params.addArgument(expr);
            nodeStack.push(params);
        } else if (ctx.getChildCount() == 3) {
            // params: params COMMA expr
            int comma = tokenStack.pop().getType();
            assert comma == ArithmeticExpressionLexer.COMMA;
            ASTNode expr = nodeStack.pop();
            ASTFunction params = (ASTFunction) nodeStack.pop();
            params.addArgument(expr);
            nodeStack.push(params);
        } else {
            assert false;
        }
    }

    @Override
    public void enterNum(ArithmeticExpressionParser.NumContext ctx) {

    }

    @Override
    public void exitNum(ArithmeticExpressionParser.NumContext ctx) {
        nodeStack.push(new ASTNumber(tokenStack.pop().getDval()));
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
        switch (terminalNode.getSymbol().getType()) {
            case ArithmeticExpressionLexer.ADD:
            case ArithmeticExpressionLexer.SUB:
            case ArithmeticExpressionLexer.MUL:
            case ArithmeticExpressionLexer.DIV:
            case ArithmeticExpressionLexer.EXP:
            case ArithmeticExpressionLexer.MOD:
            case ArithmeticExpressionLexer.LPAREN:
            case ArithmeticExpressionLexer.RPAREN:
            case ArithmeticExpressionLexer.COMMA:
                tokenStack.push(new Token(terminalNode.getSymbol().getType(), null, null));
                break;
            case ArithmeticExpressionLexer.E:
                tokenStack.push(new Token(terminalNode.getSymbol().getType(), Math.E, null));
                break;
            case ArithmeticExpressionLexer.PI:
                tokenStack.push(new Token(terminalNode.getSymbol().getType(), Math.PI, null));
                break;
            case ArithmeticExpressionLexer.NUM:
                tokenStack.push(new Token(terminalNode.getSymbol().getType(), Double.parseDouble(terminalNode.toString()), null));
                break;
            case ArithmeticExpressionLexer.FUNC:
                tokenStack.push(new Token(terminalNode.getSymbol().getType(), null, terminalNode.toString()));
                break;
        }
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }
}

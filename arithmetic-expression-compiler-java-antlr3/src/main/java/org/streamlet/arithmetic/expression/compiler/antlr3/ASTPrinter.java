package org.streamlet.arithmetic.expression.compiler.antlr3;

import org.streamlet.arithmetic.expression.compiler.antlr3.ast.*;

import java.text.DecimalFormat;

public class ASTPrinter {
    public static void printResult(ASTNode node) {
        System.out.printf("%s\n", new DecimalFormat().format(node.value()));
    }

    private static void printStructInternal(String indent, ASTNode node) {
        if (node.getClass() == ASTNumber.class) {
            System.out.printf("%s%s\n", indent, new DecimalFormat().format(((ASTNumber) node).getDval()));
        } else if (node.getClass() == ASTUnaryOperator.class) {
            System.out.printf("%s%s = %s\n", indent, ((ASTUnaryOperator) node).getOp().name(),
                    new DecimalFormat().format(node.value()));
            printStructInternal(indent + "  ", ((ASTUnaryOperator) node).getOperand());
        } else if (node.getClass() == ASTBinaryOperator.class) {
            System.out.printf("%s%s = %s\n", indent, ((ASTBinaryOperator) node).getOp().name(),
                    new DecimalFormat().format(node.value()));
            printStructInternal(indent + "  ", ((ASTBinaryOperator) node).getLeft());
            printStructInternal(indent + "  ", ((ASTBinaryOperator) node).getRight());
        } else if (node.getClass() == ASTFunction.class) {
            System.out.printf("%s%s = %s\n", indent, ((ASTFunction) node).getFunc().name(),
                    new DecimalFormat().format(node.value()));
            for (ASTNode arg : ((ASTFunction) node).getArguments()) {
                printStructInternal(indent + "  ", arg);
            }
        } else {
            assert (false);
        }
    }

    public static void printStruct(ASTNode node) {
        printStructInternal("", node);
    }

    private static int printGraphvizInternal(int count, ASTNode node) {
        int next = count + 1;
        if (node.getClass() == ASTNumber.class) {
            System.out.printf("  n%d [ label=\"%s\" shape=circle ];\n", count,
                    new DecimalFormat().format(((ASTNumber) node).getDval()));
        } else if (node.getClass() == ASTUnaryOperator.class) {
            System.out.printf("  n%d [ label=\"%s\\n(=%s)\" shape=doublecircle ];\n", count,
                    ((ASTUnaryOperator) node).getOp().name(), new DecimalFormat().format(node.value()));
            System.out.printf("  n%d->n%d\n", count, next);
            next = printGraphvizInternal(next, ((ASTUnaryOperator) node).getOperand());
        } else if (node.getClass() == ASTBinaryOperator.class) {
            System.out.printf("  n%d [ label=\"%s\\n(=%s)\" shape=doublecircle ];\n", count,
                    ((ASTBinaryOperator) node).getOp().name(), new DecimalFormat().format(node.value()));
            System.out.printf("  n%d->n%d;\n", count, next);
            next = printGraphvizInternal(next, ((ASTBinaryOperator) node).getLeft());
            System.out.printf("  n%d->n%d;\n", count, next);
            next = printGraphvizInternal(next, ((ASTBinaryOperator) node).getRight());
        } else if (node.getClass() == ASTFunction.class) {
            System.out.printf("  n%d [label=\"%s\\n(=%s)\" shape=rect ];\n", count,
                    ((ASTFunction) node).getFunc().name(), new DecimalFormat().format(node.value()));
            for (ASTNode arg : ((ASTFunction) node).getArguments()) {
                System.out.printf("  n%d->n%d;\n", count, next);
                next = printGraphvizInternal(next, arg);
            }
        } else {
            assert (false);
        }
        return next;
    }

    public static void printGraphviz(ASTNode node) {
        System.out.printf("" +
                "digraph {\n" +
                "  rankdir=TD;\n");
        printGraphvizInternal(0, node);
        System.out.printf("" +
                "}\n");
    }
}

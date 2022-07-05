package org.streamlet.arithmetic.expression.compiler.antlr3;

import org.streamlet.arithmetic.expression.compiler.antlr3.ast.ASTNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        for (String s = stdin.readLine(); s != null && !s.isEmpty(); s = stdin.readLine()) {
            ASTNode ast = ASTBuilder.parse(s);
            ASTPrinter.printResult(ast);
            ASTPrinter.printStruct(ast);
            ASTPrinter.printGraphviz(ast);
        }
    }
}

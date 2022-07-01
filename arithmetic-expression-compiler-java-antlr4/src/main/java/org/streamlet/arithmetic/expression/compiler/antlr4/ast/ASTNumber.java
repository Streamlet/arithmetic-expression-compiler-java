package org.streamlet.arithmetic.expression.compiler.antlr4.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ASTNumber extends ASTNode {
    private double dval;

    @Override
    public double value() {
        return dval;
    }
}

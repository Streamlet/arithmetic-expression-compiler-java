package org.streamlet.arithmetic.expression.compiler.shared.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ASTUnaryOperator extends ASTNode {
    private UnaryOperator op;
    private ASTNode operand;

    @Override
    public double value() {
        switch (op) {
            case POS:
                return operand.value();
            case NEG:
                return -operand.value();
        }
        assert false;
        return 0.0;
    }
}

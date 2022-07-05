package org.streamlet.arithmetic.expression.compiler.shared.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ASTBinaryOperator extends ASTNode {
    private BinaryOperator op;
    private ASTNode left;
    private ASTNode right;

    @Override
    public double value() {
        switch (op) {
            case ADD:
                return left.value() + right.value();
            case SUB:
                return left.value() - right.value();
            case MUL:
                return left.value() * right.value();
            case DIV:
                return left.value() / right.value();
            case EXP:
                return Math.pow(left.value(), right.value());
            case MOD:
                return left.value() % right.value();
        }
        assert false;
        return 0.0;
    }
}

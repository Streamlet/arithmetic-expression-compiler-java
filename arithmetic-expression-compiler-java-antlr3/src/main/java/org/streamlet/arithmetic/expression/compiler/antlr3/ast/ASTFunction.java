package org.streamlet.arithmetic.expression.compiler.antlr3.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ASTFunction extends ASTNode {
    Function func;
    List<ASTNode> arguments = new ArrayList<>();

    @Override
    public double value() {
        switch (func) {
            case SQRT:
                return Math.sqrt(arguments.get(0).value());
            case LOG:
                return Math.log(arguments.get(1).value()) / Math.log(arguments.get(0).value());
            case LN:
                return Math.log(arguments.get(0).value());
            case LG:
                return Math.log10(arguments.get(0).value());
            case SIN:
                return Math.sin(arguments.get(0).value());
            case COS:
                return Math.cos(arguments.get(0).value());
            case TAN:
                return Math.tan(arguments.get(0).value());
            case COT:
                return 1 / Math.tan(arguments.get(0).value());
        }
        assert false;
        return 0.0f;
    }

    public void addArgument(ASTNode argument) {
        arguments.add(argument);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class FuncDef {
        Function func;
        String name;
        int args;
    }

    static final FuncDef[] FUNC_DEF = {
            new FuncDef(Function.SQRT, "sqrt", 1),
            new FuncDef(Function.LOG, "log", 2),
            new FuncDef(Function.LN, "ln", 1),
            new FuncDef(Function.LG, "lg", 1),
            new FuncDef(Function.SIN, "sin", 1),
            new FuncDef(Function.COS, "cos", 1),
            new FuncDef(Function.TAN, "tan", 1),
            new FuncDef(Function.TAN, "tg", 1),
            new FuncDef(Function.COT, "cot", 1),
            new FuncDef(Function.COT, "ctg", 1),
    };

    public boolean assignName(String name) {
        for (int i = 0; i < FUNC_DEF.length; ++i) {
            if (FUNC_DEF[i].name.equals(name)) {
                func = FUNC_DEF[i].func;
                if (arguments.size() != FUNC_DEF[i].args) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}

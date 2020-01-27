package gonearewe;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Lisp {
    public static String evalAndPrint(@NotNull ASTTree root) {
        if (root.isAtom()) {
            return root.getAtomName();
        }

        var result = new ASTTree();
        var env = new Environment();
        try {
            for (var node : root.lst) {
                result = eval(node, env); // only print the last value
            }
        } catch (RuntimeException e) {
            return e.getLocalizedMessage() + e.getStackTrace();
        }

        return result.toString();
    }

    @NotNull
    @Contract("_, _ -> param1")
    private static ASTTree eval(@NotNull ASTTree node, Environment env) throws RuntimeException {
        // '#t'
        if (node.isAtom() && node.equals("#t")) {
            return node;
        }

        if (node.isAtom()) {
            if (env.get(node.getAtomName()) == null) {
                throw new RuntimeException("%s undefined variable %s", node.getPosition(), node.getAtomName());
            }
        }

        if (node.isInteger()) {
            return node;
        }

        if (node.isSExpression()) {
            return evalSExpression(node, env);
        }

        throw new RuntimeException("lambda unhandled");
    }

    // evalSExpression eval an ASTTree which is a S-Expression whose form looks like "(head tail1 tail2 ...)".
    public static ASTTree evalSExpression(@NotNull ASTTree node, Environment env) throws RuntimeException {
        if (node.hasHead()) {
            var head = node.getHead();
            var tail = node.getTail();
            var headName = head.getAtomName();

            if (head.isAtom()) {
                // arith
                if (Arrays.asList(new String[]{"+", "-", "*"}).contains(headName)) {
                    return arith(headName, tail, env);
                }

                switch (headName) {
                    case "QUOTE":
                    case "'":
                        return tail.get(0); // TODO: maybe throw an exception when no tail found?
                    case "ATOM":
                        return eval(tail.get(0), env).isAtom() ? new ASTTree(node, "#t") : new ASTTree();
                    case "EQ":
                        return eq(tail);
                    case "CAR":
                        return eval(tail.get(0), env).getHead();
                    case "CDR":
                        return new ASTTree(eval(head.getHead(), env).getTail());
                    case "CONS":
                        var list = eval(tail.get(1), env);
                        list.add(eval(tail.get(0), env));
                        return list;
                    case "LIST":
                        var lst = new ArrayList<ASTTree>();
                        for (var e : tail) {
                            lst.add(eval(e, env));
                        }
                        return new ASTTree(lst);
                    case "COND":
                        return cond(tail, env);
                    case "LET":
                        return let(tail, env);
                    case "DEFUN":
                        defun(tail, env); // update environment
                        return new ASTTree(tail);
                    case "LAMBDA":
                        return lambda(tail, env);
                }
            }

            // apply user-defined function
            var headVal = eval(head, env);
            if (headVal.isLambda()) {
                return apply(head, tail, env);
            } else {
                throw new RuntimeException("%s undefined function", head.getPosition());
            }
        }

        return node; // a nil I suppose?
    }

    @NotNull
    @Contract("_, _, _ -> new")
    private static ASTTree arith(String arithType, @NotNull ArrayList<ASTTree> operands, Environment env) throws RuntimeException {
        if (operands.size() <= 1) { // TODO: add supports later, maybe?
            throw new RuntimeException("too few operands is not supported yet");
        }

        switch (arithType) {
            case "+":
                int sum = 0;
                for (var operand : operands) {
                    sum += eval(operand, env).integerVal;
                }
                return new ASTTree(operands.get(0), String.valueOf(sum));

            case "-":
                int result = 0;
                for (var operand : operands) {
                    result -= eval(operand, env).integerVal;
                }
                return new ASTTree(operands.get(0), String.valueOf(result));

            case "*":
                int ans = 0;
                for (var operand : operands) {
                    ans *= eval(operand, env).integerVal;
                }
                return new ASTTree(operands.get(0), String.valueOf(ans));
        }

        throw new RuntimeException("hey, developer! you shouldn't reach here");
    }

    @NotNull
    private static ASTTree eq(@NotNull ArrayList<ASTTree> tail) throws RuntimeException {
        if (tail.size() != 2) {
            throw new RuntimeException("too few arguments for calling `eq`");
        }

        var op1 = tail.get(0);
        var op2 = tail.get(1);

        if (op1.isAtom() && op2.isAtom()) {
            return op1.getAtomName().equals(op2.getAtomName()) ? new ASTTree(op1, "#T") : new ASTTree();
        }

        if (op1.isInteger() && op2.isInteger()) {
            return (op1.integerVal == op2.integerVal) ? new ASTTree(op1, "#T") : new ASTTree();
        }

        return (op1.isNil() && op2.isNil()) ? new ASTTree(op1, "#T") : new ASTTree();
    }

    private static ASTTree cond(@NotNull ArrayList<ASTTree> sequences, Environment env) throws RuntimeException {
        var result = new ASTTree();
        for (var sequence : sequences) {
            result = eval(sequence.getHead(), env);
            if (!result.isNil()) {
                for (var expr : sequence.getTail()) {
                    result = eval(expr, env);
                }

                return result;
            }
        }

        return new ASTTree();
    }

    // (let (variable_name expr) body)
    @NotNull
    private static ASTTree let(@NotNull ArrayList<ASTTree> tail, Environment env) throws RuntimeException {
        if (tail.size() > 2) {
            throw new RuntimeException("%s too many arguments for calling `let`", tail.get(0).getPosition());
        }

        var constructor = tail.get(0);
        var val = eval(constructor.getTail().get(0), env); // value of the variable
        var newEnv = new Environment(env, constructor.getHead().getAtomName(), val);
        return eval(tail.get(1), newEnv);
    }

    @NotNull
    @Contract("_, _ -> new")
    private static ASTTree lambda(@NotNull ArrayList<ASTTree> tail, Environment env) throws RuntimeException {
        if (tail.size() != 2) {
            throw new RuntimeException("expect 2 arguments for calling `lambda`");
        }
        return new ASTTree(new Closure(eval(tail.get(0), env).toStringList(), eval(tail.get(1), env), env));
    }

    private static void defun(@NotNull ArrayList<ASTTree> tail, Environment env) throws RuntimeException {
        var name = tail.get(0);
        ASTTree value;
        if (tail.size() == 2) {
            value = eval(tail.get(1), env);
        } else {
            var closure = new Closure(tail.get(1).toStringList(), tail.get(2), env);
            value = new ASTTree(closure);
        }

        env.put(name.getAtomName(), value);
    }

    // apply calls func with arguments given by tail.
    @NotNull
    private static ASTTree apply(@NotNull ASTTree func, ArrayList<ASTTree> tail, Environment env) throws RuntimeException {
        Closure closure = func.closure;
        if (tail.size() != closure.params.size()) {
            throw new RuntimeException("%s call closure: parameter number mismatched", tail.get(0).getPosition());
        }
        Environment newEnv = new Environment(env); // a new environment for running closure
        int i = 0;
        for (var param : closure.params) { // eval for closure's param
            newEnv.put(param, eval(tail.get(i), env));
            i++;
        }

        return eval(func.closure.body, newEnv);
    }
}

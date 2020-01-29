package gonearewe;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Lisp {
    public static String evalAndPrint(@NotNull ArrayList<ASTree> roots) {
        //        if (root.isAtom()) {
        //            return root.getAtomName();
        //        }

        var result = new ASTree();
        var env = new Environment();
        try {
            for (var root : roots) {
                result = eval(root, env); // only print the last value
            }
        } catch (RuntimeException e) {
            return e.getLocalizedMessage();
        }

        return result.toString();
    }

    @NotNull
    @Contract("_, _ -> param1")
    private static ASTree eval(@NotNull ASTree node, Environment env) throws RuntimeException {
        // '#T'
        if (node.isAtom() && node.equals("#T")) {
            return node;
        }

        if (node.isAtom()) {
            var variable = env.get(node.getAtomName());
            if (variable == null) {
                throw new RuntimeException("%s undefined variable %s", node.getPosition(), node.getAtomName());
            }

            return variable;
        }

        if (node.isInteger()) {
            return node;
        }

        if (node.isSExpression()) {
            return evalSExpression(node, env);
        }

        //        throw new RuntimeException("lambda unhandled");
        return new ASTree();
    }

    // evalSExpression eval an ASTTree which is a S-Expression whose form looks like "(head tail1 tail2 ...)".
    public static ASTree evalSExpression(@NotNull ASTree node, Environment env) throws RuntimeException {
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
                        return tail.get(0); // TODO: maybe throw an exception when no tail found?
                    case "ATOM":
                        return eval(tail.get(0), env).isAtom() ? new ASTree(head, "#T") : new ASTree();
                    case "EQ":
                        return eq(tail, env);
                    case "CAR":
                        return eval(tail.get(0), env).getHead();
                    case "CDR":
                        return new ASTree(eval(tail.get(0), env).getTail());
                    case "CONS":
                        var list = eval(tail.get(1), env);
                        list.add(0, eval(tail.get(0), env));
                        return list;
                    case "LIST":
                        var lst = new ArrayList<ASTree>();
                        for (var e : tail) {
                            lst.add(eval(e, env));
                        }
                        return new ASTree(lst);
                    case "COND":
                        return cond(tail, env);
                    case "LET":
                        return let(tail, env);
                    case "DEFUN":
                        defun(tail, env); // update environment
                        return new ASTree(tail);
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
    private static ASTree arith(String arithType, @NotNull ArrayList<ASTree> operands, Environment env) throws RuntimeException {
        if (operands.size() <= 1) { // TODO: add supports later, maybe?
            throw new RuntimeException("too few operands is not supported yet");
        }

        switch (arithType) {
            case "+":
                int sum = 0;
                for (var operand : operands) {
                    sum += eval(operand, env).integerVal;
                }
                return new ASTree(operands.get(0), String.valueOf(sum));

            case "-":
                int result = 0;
                for (var operand : operands) {
                    result -= eval(operand, env).integerVal;
                }
                return new ASTree(operands.get(0), String.valueOf(result));

            case "*":
                int ans = 0;
                for (var operand : operands) {
                    ans *= eval(operand, env).integerVal;
                }
                return new ASTree(operands.get(0), String.valueOf(ans));
        }

        throw new RuntimeException("hey, developer! you shouldn't reach here");
    }

    @NotNull
    private static ASTree eq(@NotNull ArrayList<ASTree> tail, Environment env) throws RuntimeException {
        if (tail.size() != 2) {
            throw new RuntimeException("too few arguments for calling `eq`");
        }

        var op1 = eval(tail.get(0), env);
        var op2 = eval(tail.get(1), env);

        if (op1.isAtom() && op2.isAtom()) {
            return op1.getAtomName().equals(op2.getAtomName()) ? new ASTree(op1, "#T") : new ASTree();
        }

        if (op1.isInteger() && op2.isInteger()) {
            return (op1.integerVal == op2.integerVal) ? new ASTree(op1, "#T") : new ASTree();
        }

        // Oh, how stupid I am, S-Expressions are NOT COMPARABLE, we merely should eval them separately before compare.
        // recursively compare their children
        //        if (op1.isSExpression() && op2.isSExpression()) {
        //            if (op1.lst.size() != op2.lst.size()) {
        //                return new ASTree();
        //            }
        //
        //            for (int i = 0; i < op1.lst.size(); i++) {
        //                var ops = new ArrayList<ASTree>();
        //                ops.add(op1.lst.get(i));
        //                ops.add(op2.lst.get(i));
        //                if (!eq(ops).atom.isNil()) {
        //                    return new ASTree();
        //                }
        //            }
        //            return new ASTree(op1, "#T");
        //        }

        return (op1.isNil() && op2.isNil()) ? new ASTree(op1, "#T") : new ASTree();
    }

    private static ASTree cond(@NotNull ArrayList<ASTree> sequences, Environment env) throws RuntimeException {
        var result = new ASTree();
        for (var sequence : sequences) {
            result = eval(sequence.getHead(), env);
            if (!result.isNil()) {
                for (var expr : sequence.getTail()) {
                    result = eval(expr, env);
                }

                return result;
            }
        }

        return new ASTree();
    }

    // (let (variable_name expr) body)
    @NotNull
    private static ASTree let(@NotNull ArrayList<ASTree> tail, Environment env) throws RuntimeException {
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
    private static ASTree lambda(@NotNull ArrayList<ASTree> tail, Environment env) throws RuntimeException {
        if (tail.size() != 2) {
            throw new RuntimeException("expect 2 arguments for calling `lambda`");
        }
        return new ASTree(new Closure(eval(tail.get(0), env).toStringList(), eval(tail.get(1), env), env));
    }

    private static void defun(@NotNull ArrayList<ASTree> tail, Environment env) throws RuntimeException {
        var name = tail.get(0);
        ASTree value;
        if (tail.size() == 2) {
            value = eval(tail.get(1), env);
        } else {
            var closure = new Closure(tail.get(1).toStringList(), tail.get(2), env);
            value = new ASTree(closure);
        }

        env.put(name.getAtomName(), value);
    }

    // apply calls func with arguments given by tail.
    @NotNull
    private static ASTree apply(@NotNull ASTree func, @NotNull ArrayList<ASTree> tail, Environment env) throws RuntimeException {
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

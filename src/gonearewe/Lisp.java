package gonearewe;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Lisp {
    @NotNull
    @Contract("_, _ -> param1")
    public static ASTTree eval(@NotNull ASTTree root, Environment env) throws RuntimeException {
        // '#t'
        if (root.isAtom() && root.equals("#t")) {
            return root;
        }

        if (root.isAtom()) {
            if (env.get(root.getAtomName()) == null) {
                throw new RuntimeException("%s undefined variable %s", root.getPosition(), root.getAtomName());
            }
        }
    }
}

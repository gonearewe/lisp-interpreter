package gonearewe;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class Parser {
    @Contract(pure = true)
    public static ASTTree parse(@NotNull ArrayList<Token> tokens) {
        ASTTree cur = new ASTTree(); // top level
        cur.parent = cur; // point to self

        for (var token : tokens) {
            if (token.isLeftBracket()) {
                var node = new ASTTree(cur);
                cur.add(node);
                cur = node;
            } else if (token.isRightBracket()) {
                if (cur.parent != cur) {
                    cur = cur.parent; // backtrace
                }
            } else {
                cur.add(new ASTTree(token));
            }
        }

        return cur;
    }
}

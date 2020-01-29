package gonearewe;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class Parser {
    @NotNull
    @Contract(pure = true)
    public static ArrayList<ASTree> parse(@NotNull ArrayList<Token> tokens) {
        ArrayList<ASTree> top = new ArrayList<>(); // top level
        ASTree cur = null;

        tokens = handleQuoteAbbr(tokens);

        for (var token : tokens) {
            if (token.isLeftBracket()) {
                if (cur == null) { // at top level
                    cur = new ASTree();
                } else {
                    var node = new ASTree(cur);
                    cur.add(node);
                    cur = node;
                }
            } else if (token.isRightBracket()) {
                if (cur.parent != cur) {
                    cur = cur.parent; // backtrace
                } else { // back to the root node
                    top.add(cur);
                    cur = null;
                }
            } else {
                if (cur == null) {
                    top.add(new ASTree(token));
                } else {
                    cur.add(new ASTree(token));
                }
            }
        }


        return top;
    }

    // handleQuoteAbbr handle syntax sugar("'a" equals "(quote a)") in the given tokens.
    @NotNull
    @Contract(pure = true)
    private static ArrayList<Token> handleQuoteAbbr(@NotNull ArrayList<Token> tokens) {
        var result = new ArrayList<Token>();
        // these three are for supporting syntax
        boolean inQuoteScope = false;
        int quoteDepth = 0; // record depth
        for (var token : tokens) {
            if (token.isQuoteAbbr()) {
                result.add(Token.LeftBracketToken());
                result.add(Token.QuoteToken(token.getRow(), token.getColumn()));
                inQuoteScope = true;
                continue;
            }

            result.add(token);
            if (inQuoteScope) {
                if (token.isLeftBracket()) {
                    quoteDepth++;
                } else if (token.isRightBracket()) {
                    quoteDepth--;
                }

                if (quoteDepth == 0) {
                    result.add(Token.RightBracketToken()); // supply an extra right bracket
                    inQuoteScope = false; // exit quote scope
                }
            }
        }

        return result;
    }
}

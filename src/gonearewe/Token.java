package gonearewe;


public class Token {
    private String token;
    private TokenKind kind;

    Token(TokenKind kind) {
        this.kind = kind;
    }

    Token(TokenKind kind, String token) {
        this.kind = kind;
        this.token = token;
    }

    // isSeparator tells if given char is a space or a bracket.
    public static boolean isSeparator(char c) {
        boolean ans = false;

        if (Character.isWhitespace(c)) {
            ans = true;
        } else if (c == '(' || c == ')') {
            ans = true;
        }

        return ans;
    }

    public String toString() {
        return token;
    }
}

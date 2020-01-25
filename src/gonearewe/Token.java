package gonearewe;


public class Token {
    private String token;
    private TokenKind kind;
    private int row; // row index
    private int column; // column index

    Token(TokenKind kind, String token, int row, int column) {
        this.kind = kind;
        this.token = token;
        this.row = row;
        this.column = column;
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

    // isValidChar tells if given char is valid as part of a word.
    public static boolean isValidChar(char c) {
        return !isSeparator(c) && c != '\'' && c != '`';
    }

    public void setToken(String s) {
        token = s;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isNil() {
        return token.equals("nil");
    }

    public boolean isInteger() {
        return TokenKind.isINTEGER(token);
    }

    public int integerValue() {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            // do nothing
            // after checking with isInteger(), it's safe to parse
        }

        return 0; // never reach here
    }

    public boolean isLeftBracket() {
        return kind == TokenKind.LEFT_BRACKET;
    }

    public boolean isRightBracket() {
        return kind == TokenKind.RIGHT_BRACKET;
    }

    public String toString() {
        return token;
    }
}

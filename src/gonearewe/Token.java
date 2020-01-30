package gonearewe;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    // LeftBracketToken generates a default LEFT_BRACKET without position information.
    @NotNull
    @Contract(value = " -> new", pure = true)
    public static Token LeftBracketToken() {
        return new Token(TokenKind.LEFT_BRACKET, "(", -1, -1);
    }

    // RightBracketToken generates a default RIGHT_BRACKET without position information.
    @NotNull
    @Contract(value = " -> new", pure = true)
    public static Token RightBracketToken() {
        return new Token(TokenKind.RIGHT_BRACKET, ")", -1, -1);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static Token positionToken(int row, int column) {
        return new Token(TokenKind.WORD, "PositionToken", row, column);
    }

    // QuoteBracketToken generates a default token 'quote' with provided position information.
    @NotNull
    @Contract(value = " -> new", pure = true)
    public static Token QuoteToken(int row, int column) {
        return new Token(TokenKind.WORD, "QUOTE", row, column);
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

    public boolean isQuoteAbbr() {
        return token == "'" || token == "`";
    }

    @Override
    public String toString() {
        return token;
    }
}

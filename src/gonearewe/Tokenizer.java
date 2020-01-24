package gonearewe;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;

public class Tokenizer {
    private final char lineSeparator = '\n';
    private PushbackInputStream src; // source code
    private ArrayList<Token> tokens;
    private int row; // current tokenizer row index
    private int column; // current tokenizer column index

    Tokenizer(InputStream source) {
        src = new PushbackInputStream(source);
        tokens = new ArrayList<>();
        row = 1;
    }

    public ArrayList<Token> tokenize() throws IOException, TokenException {
        while (true) { // -1 means EOF
            column++; // update column row,column
            System.out.println(tokens);
            int ci = src.read();
            if (ci == -1) {
                break;
            }

            char c = (char) ci;
            if (Character.isWhitespace(c)) { // skip
                if (c == lineSeparator) { // new line
                    row++; // update row index
                    column = 0; // reset column index
                }
            } else if (c == '(') {
                addToken(TokenKind.LEFT_BRACKET);
            } else if (c == ')') {
                addToken(TokenKind.RIGHT_BRACKET);
            } else if (c == '`' || c == '\'') {
                column = +handleQuote();
                expectSeparator();
            } else {
                src.unread(c); // put back the first letter
                column = +handleWordOrInteger();
                expectSeparator();
            }
        }

        return tokens;
    }

    private void addToken(TokenKind kind) {
        if (kind == TokenKind.LEFT_BRACKET) {
            tokens.add(new Token(kind, "(", row, column));
        } else {
            tokens.add(new Token(kind, ")", row, column));
        }

    }

    private void addToken(TokenKind kind, String token) {
        tokens.add(new Token(kind, token, row, column));
    }

    // handleQuote is called when quote's abbr. is found, it adds quote expression to tokens
    // and returns the length of the token, the word either is a single "'" or starts with a "'".
    private int handleQuote() throws IOException, TokenException {
        var word = new StringBuilder();

        while (true) {
            var next = (char) src.read();
            if (Token.isValidChar(next)) { // unfinished word
                word.append(next);
            } else if (!TokenKind.isINTEGER(word.toString())) { // finished, add to tokens
                // '\'a' or '`a' is just a syntax sugar for '(quote a)'
                addToken(TokenKind.LEFT_BRACKET);
                addToken(TokenKind.WORD, "quote");
                addToken(TokenKind.WORD, word.toString().toUpperCase());
                addToken(TokenKind.RIGHT_BRACKET);
                return word.length();
            } else {
                throw new TokenException("%d:%d: invalid or empty word after a quote", row, column);
            }
        }
    }

    // handleWordOrInteger reads an integer or a word and add it to tokens.
    private int handleWordOrInteger() throws IOException {
        var word = new StringBuilder(); // just a name, assume it' a word

        while (true) {
            var next = (char) src.read();
            if (!Token.isSeparator(next)) { // unfinished word
                word.append(next);
            } else { // finished, add to tokens
                src.unread(next); // put back
                if (TokenKind.isINTEGER(word.toString())) {
                    addToken(TokenKind.INTEGER, word.toString());
                } else {
                    addToken(TokenKind.WORD, word.toString().toUpperCase());
                }

                return word.length();
            }
        }
    }

    // expectSeparator looks ahead and throws TokenException if following
    // char is not a space or a bracket.
    private void expectSeparator() throws IOException, TokenException {
        var next = src.read();

        if (Token.isSeparator((char) next)) {
            src.unread(next); // put back
            return;
        }

        throw new TokenException("%d:%d: expect a '(', ')' or space", row, column);
    }
}

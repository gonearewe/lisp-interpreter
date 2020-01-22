package gonearewe;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;

public class Tokenizer {
    private PushbackInputStream src; // source code
    private ArrayList<Token> tokens;

    Tokenizer(InputStream source) {
        src = new PushbackInputStream(source);
        tokens = new ArrayList<Token>();
    }

    public ArrayList<Token> tokenize() throws IOException, TokenException {
        while (true) { // -1 means EOF
            int ci = src.read();
            if (ci == -1) {
                break;
            }

            char c = (char) ci;
            if (Character.isWhitespace(c)) {
                continue;
            } else if (c == '(') {
                addToken(TokenKind.LEFT_BRACKET);
            } else if (c == ')') {
                addToken(TokenKind.RIGHT_BRACKET);
            } else if (c == '`' || c == '\'') {
                handleQuote();
                expectSeparator();
            } else if (Character.isDigit(c)) {
                src.unread(c); // put back the first number
                handleInteger();
                expectSeparator();
            } else if (Character.isLetter(c)) {
                src.unread(c); // put back the first letter
                handleWord();
                expectSeparator();
            } else {
                throw new TokenException(String.format("unknown character: '%c'", c));
            }
        }

        return tokens;
    }

    private void addToken(TokenKind kind) {
        tokens.add(new Token(kind));
    }

    private void addToken(TokenKind kind, String token) {
        tokens.add(new Token(kind, token));
    }

    // handleQuote is called when quote's abbr. is found, it adds one word to tokens,
    // the word either is a single "'" or starts with a "'".
    private void handleQuote() throws IOException {
        var next = (char) src.read();
        var word = new StringBuilder("'"); // choose "'" between two abbr.

        if (Character.isWhitespace(next)) { // a single '`' or '\''
            addToken(TokenKind.WORD, word.toString());
            src.unread(next); // put back the space for separator check
        } else {
            src.unread(next); // put back the first letter
            while (true) {
                next = (char) src.read();
                if (Character.isLetter(next)) { // unfinished word
                    word.append(next);
                } else { // finished, add to tokens
                    addToken(TokenKind.WORD, word.toString().toUpperCase());
                    return;
                }
            }
        }
    }

    // handleInteger reads an integer and add it to tokens.
    private void handleInteger() throws IOException {
        var integer = new StringBuilder();

        while (true) {
            var next = (char) src.read();
            if (Character.isDigit(next)) { // unfinished integer
                integer.append(next);
            } else { // finished, add to tokens
                src.unread(next); // put back
                addToken(TokenKind.INTEGER, integer.toString());
                return;
            }
        }
    }

    private void handleWord() throws IOException {
        var word = new StringBuilder();

        while (true) {
            var next = (char) src.read();
            if (Character.isLetter(next)) { // unfinished word
                word.append(next);
            } else { // finished, add to tokens
                src.unread(next); // put back
                addToken(TokenKind.WORD, word.toString().toUpperCase());
                return;
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

        throw new TokenException("expect a '(', ')' or space");
    }
}

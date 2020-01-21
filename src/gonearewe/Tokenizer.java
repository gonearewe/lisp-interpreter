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
        tokens = new ArrayList<Token>;
    }

    public ArrayList<Token> tokenize() {
        try {
            int ci = src.read();
            while (ci != -1) { // -1 means EOF
                char c = (char) ci;
                if (c == '(') {
                    addToken(TokenKind.LEFT_BRACKET););

                } else if (c == ')') {
                    addToken(TokenKind.RIGHT_BRACKET););
                } else if (c == '`' || c == '\'') {
                    var next = (char) src.read();
                    var word = new StringBuilder(next);

                    if (Character.isWhitespace(next)) {
                        addToken(TokenKind.WORD, word.toString()););
                        continue;
                    } else {
                        while (true) {
                            next = (char) src.read();
                            if (Character.isLetter(next)) {
                                word.append(next);
                            } else {
                                addToken(TokenKind.WORD, word.toString().toUpperCase());
                                break;
                            }
                        }
                    }
                }

            }
        } catch (IOException e) {
            throw e;
        }
    }

    private void addToken(TokenKind kind) {
        tokens.add(new Token(kind));
    }

    private void addToken(TokenKind kind, String token) {
        tokens.add(new Token(kind, token));
    }


}

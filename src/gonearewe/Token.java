package gonearewe;

private enum TokenKind {
  LEFT_BRACKET,
  RIGHT_BRACKET,
  INTEGER,
  WORD,
}

public class Token {
  private String token;
  private TokenKind kind;

  Token(TokenKind kind) {
    this.kind = kind;
  }

  Token(TokenKind kind, String token) {
    this.kind = tokenKind;
    this.token = token;
  }
}

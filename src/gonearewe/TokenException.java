package gonearewe;

public class TokenException extends Exception {
    TokenException(String message) {
        super(message);
    }

    TokenException(String format, Object... args) {
        super(String.format(format, args));
    }
}

package gonearewe;

// RuntimeException is thrown when an error occurs during execution(eval).
public class RuntimeException extends Exception {
    RuntimeException(String format, Object... args) {
        super(String.format(format, args));
    }
}

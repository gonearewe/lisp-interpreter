package gonearewe;

public enum TokenKind {
    LEFT_BRACKET, RIGHT_BRACKET, INTEGER, WORD;

    public static boolean isINTEGER(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}

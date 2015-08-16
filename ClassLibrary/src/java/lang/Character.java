package java.lang;

import vm.VMClass;

public final class Character {

    public static final Class TYPE = VMClass.CHAR.getRealClass();
    
    public static final int MIN_RADIX = 2;
    public static final int MAX_RADIX = 36;
    public static final char MIN_VALUE = '\u0000';
    public static final char MAX_VALUE = '\uffff';
    private final char value;

    public Character(char value) {
        this.value = value;
    }

    public char charValue() {
        return value;
    }

    public int hashCode() {
        return value;
    }

    public boolean equals(Object o) {
        return (o instanceof Character) && (((Character) o).value == value);
    }

    public String toString() {
        return new String(new char[]{value}, 0, 1, true);
    }

    public static boolean isLowerCase(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= '\u00DF' && ch <= '\u00FF' && ch != '\u00F7');
    }

    public static boolean isUpperCase(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= '\u00C0' && ch <= '\u00DE' && ch != '\u00D7');
    }

    // TODO: Check the implementations of these methods
    public static boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9');
    }

    public static char toLowerCase(char ch) {
        if ((ch >= 'A' && ch <= 'Z') || (ch >= '\u00C0' && ch <= '\u00DE' && ch != '\u00D7')) {
            return (char) (ch + 32);
        } else {
            return ch;
        }
    }

    public static char toUpperCase(char ch) {
        if ((ch >= 'a' && ch <= 'z') || (ch >= '\u00E0' && ch <= '\u00FE' && ch != '\u00F7')) {
            return (char) (ch - 32);
        } else {
            return ch;
        }
    }
    
    public static int digit(char c, int radix) {
        if (radix < MIN_RADIX || radix > MAX_RADIX) {
            return -1;
        }
        int out;
        if (c >= '0' && c <= '9') {
            out = c - '0';
        } else if (c >= 'a' && c <= 'z') {
            out = c - 'a' + 10;
        } else if (c >= 'A' && c <= 'Z') {
            out = c - 'A' + 10;
        } else {
            return -1;
        }
        return out < radix ? out : -1;
    }
}

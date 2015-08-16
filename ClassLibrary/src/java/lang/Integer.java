package java.lang;

import vm.VMClass;

public final class Integer {

    public static final int MIN_VALUE = -2147483648;
    public static final int MAX_VALUE = 2147483647;
    public static final Class TYPE = VMClass.INT.getRealClass();
    private final int value;

    public Integer(int value) {
        this.value = value;
    }
    private static String digstr = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static char[] digits = digstr.toCharArray();

    public static String toString(int i, int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            radix = 10;
        }
        boolean negative;
        if (i == 0) {
            return "0";
        } else if (i < 0) {
            negative = true;
            i = -i;
        } else {
            negative = false;
        }
        char[] out = new char[12];
        int ci = 12;
        while (i != 0) {
            int mod = i % radix;
            i = i / radix;
            out[--ci] = digits[mod];
        }
        if (negative) {
            out[--ci] = '-';
        }
        return new String(out, ci, 12 - ci, true);
    }

    public static String toHexString(int i) {
        if (i == 0) {
            return "0";
        }
        char[] out = new char[12];
        int ci = 12;
        while (i != 0) {
            out[--ci] = digits[i & 15];
            i = i >> 4;
        }
        return new String(out, ci, 12 - ci, true);
    }

    public static String toOctalString(int i) {
        if (i == 0) {
            return "0";
        }
        StringBuffer out = new StringBuffer();
        while (i != 0) {
            out.append(digits[i & 7]);
            i = i >>> 3;
        }
        return out.reverse().toString();
    }

    public static String toBinaryString(int i) {
        if (i == 0) {
            return "0";
        }
        StringBuffer out = new StringBuffer();
        while (i != 0) {
            out.append(digits[i & 1]);
            i = i >>> 1;
        }
        return out.reverse().toString();
    }

    public static String toString(int i) {
        return toString(i, 10);
    }

    public static int parseInt(String s, int radix) throws NumberFormatException {
        if (s == null || s.length() == 0 || s.equals("-") || radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            throw new NumberFormatException();
        }
        boolean neg = false;
        int i = 0;
        if (s.charAt(0) == '-') {
            i = 1;
            neg = true;
        }
        int out = 0;
        for (; i < s.length(); i++) {
            int val = Character.digit(s.charAt(i), radix);
            if (val < 0 || val >= radix) {
                throw new NumberFormatException();
            }
            out *= radix;
            out += val;
        }
        return neg ? -out : out;
    }

    public static int parseInt(String s) throws NumberFormatException {
        return parseInt(s, 10);
    }

    public static Integer valueOf(String s, int radix) throws NumberFormatException {
        return new Integer(parseInt(s, radix));
    }

    public static Integer valueOf(String s) throws NumberFormatException {
        return new Integer(parseInt(s));
    }

    public byte byteValue() {
        return (byte) value;
    }

    public short shortValue() {
        return (short) value;
    }

    public int intValue() {
        return value;
    }

    // TODO: Implement these when longs and doubles and floats are implemented
    // public long longValue()
    // public float floatValue()
    // public double doubleValue()
    public String toString() {
        return toString(value);
    }

    public int hashCode() {
        return value;
    }

    public boolean equals(Object o) {
        return (o instanceof Integer) && (((Integer) o).value == value);
    }
}

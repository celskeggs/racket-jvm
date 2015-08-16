package java.lang;

import vm.VMClass;

public final class Short {

    public static final short MIN_VALUE = -32768;
    public static final short MAX_VALUE = 32767;
    public static final Class TYPE = VMClass.SHORT.getRealClass();
    private final short val;

    public Short(short val) {
        this.val = val;
    }

    public static short parseShort(String s) throws NumberFormatException {
        return parseShort(s, 10);
    }

    public static short parseShort(String s, int radix) throws NumberFormatException {
        int out = Integer.parseInt(s, radix);
        if (out < MIN_VALUE || out > MAX_VALUE) {
            throw new NumberFormatException("Value out of range!");
        }
        return (short) out;
    }

    public short shortValue() {
        return val;
    }

    public String toString() {
        return Integer.toString(val);
    }

    public int hashCode() {
        return val;
    }

    public boolean equals(Object o) {
        return (o instanceof Short) && (((Short) o).val == val);
    }
}

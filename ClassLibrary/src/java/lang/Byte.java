package java.lang;

import vm.VMClass;

public final class Byte {

    public static final Class TYPE = VMClass.BYTE.getRealClass();
    public static final byte MIN_VALUE = (byte) -128;
    public static final byte MAX_VALUE = (byte) 127;
    private final byte val;

    public Byte(byte value) {
        val = value;
    }

    public static byte parseByte(String s) throws NumberFormatException {
        return parseByte(s, 10);
    }

    public static byte parseByte(String s, int radix) throws NumberFormatException {
        int out = Integer.parseInt(s, radix);
        if (out < MIN_VALUE || out > MAX_VALUE) {
            throw new NumberFormatException("Value out of range!");
        }
        return (byte) out;
    }

    public byte byteValue() {
        return val;
    }

    public String toString() {
        return Integer.toString(val);
    }

    public int hashCode() {
        return val;
    }

    public boolean equals(Object o) {
        return (o instanceof Byte) && (((Byte) o).val == val);
    }
}

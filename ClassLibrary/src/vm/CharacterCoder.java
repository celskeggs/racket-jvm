package vm;

import java.io.UnsupportedEncodingException;

public class CharacterCoder {

    public static final String DEFAULT_ENCODING = "US-ASCII";

    public static char[] decode(byte[] bytes, int offset, int length, String encoding) throws UnsupportedEncodingException {
        if (encoding.equals("US-ASCII")) {
            return decodeDefault(bytes, offset, length);
        } else {
            throw new UnsupportedEncodingException();
        }
    }

    public static char[] decodeDefault(byte[] bytes, int offset, int length) {
        char[] out = new char[length];
        for (int i = 0; i < length; i++) {
            byte b = bytes[i + offset];
            out[i] = (b & 0x7f) != b ? '\ufffd' : (char) b;
        }
        return out;
    }

    public static byte[] encode(char[] chars, int offset, int length, String encoding) throws UnsupportedEncodingException {
        if (encoding.equals("US-ASCII")) {
            return encodeDefault(chars, offset, length);
        } else {
            throw new UnsupportedEncodingException();
        }
    }

    public static byte[] encodeDefault(char[] bytes, int offset, int length) {
        byte[] out = new byte[length];
        for (int i = 0; i < length; i++) {
            char c = bytes[i + offset];
            out[i] = (c & 0x7f) != c ? 63 : (byte) c;
        }
        return out;
    }
}

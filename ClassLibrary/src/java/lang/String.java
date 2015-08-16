package java.lang;

import java.io.UnsupportedEncodingException;
import vm.CharacterCoder;

public final class String {

    final char[] data;
    final int offset;
    private final int length;
    private int hashCode;

    public String() {
        offset = 0;
        length = 0;
        data = null;
    }

    public String(String value) {
        data = value.data;
        offset = value.offset;
        length = value.length;
    }

    public String(char[] value) {
        data = new char[value.length];
        System.arraycopy(value, 0, data, 0, value.length);
        offset = 0;
        length = value.length;
    }

    public String(char[] value, int offset, int count) {
        data = new char[count];
        System.arraycopy(value, offset, data, 0, count);
        this.offset = 0;
        length = count;
    }

    String(char[] value, int offset, int count, boolean directcopymarker) {
        data = value;
        this.offset = offset;
        length = count;
    }

    public String(byte[] bytes, int offset, int length, String encoding) throws UnsupportedEncodingException {
        data = CharacterCoder.decode(bytes, offset, length, encoding);
        this.offset = 0;
        this.length = data.length;
    }

    public String(byte[] bytes, String encoding) throws UnsupportedEncodingException {
        this(bytes, 0, bytes.length, encoding);
    }

    public String(byte[] bytes, int offset, int length) {
        try {
            data = CharacterCoder.decode(bytes, offset, length, CharacterCoder.DEFAULT_ENCODING);
            this.offset = 0;
            this.length = data.length;
        } catch (UnsupportedEncodingException ex) {
            throw new VirtualMachineError("Default encoding is not supported!");
        }
    }

    public String(byte[] bytes) {
        this(bytes, 0, bytes.length);
    }

    public String(StringBuffer buf) {
        synchronized (buf) {
            length = buf.length();
            data = new char[length];
            buf.getChars(0, length, data, 0);
            offset = 0;
        }
    }

    public int length() {
        return length;
    }

    public char charAt(int i) {
        if (i < 0 || i >= length) {
            throw new IndexOutOfBoundsException();
        }
        return data[i + offset];
    }

    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        if (srcBegin < 0 || srcBegin > srcEnd || srcEnd > length || dstBegin < 0 || dstBegin + srcEnd - srcBegin > dst.length) {
            throw new IndexOutOfBoundsException();
        }
        srcBegin += offset;
        srcEnd += offset;
        for (int i = srcBegin, j = dstBegin; i < srcEnd; i++, j++) {
            dst[j] = data[i];
        }
    }

    public byte[] getBytes(String encoding) throws UnsupportedEncodingException {
        return CharacterCoder.encode(data, offset, length, encoding);
    }

    public byte[] getBytes() {
        return CharacterCoder.encodeDefault(data, offset, length);
    }

    public boolean equals(Object o) {
        if (o instanceof String) {
            if (this == o) {
                return true;
            } else {
                String str = (String) o;
                if (length != str.length) {
                    return false;
                }
                for (int i = 0; i < length; i++) {
                    if (str.data[i + str.offset] != data[i + offset]) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean equalsIgnoreCase(String o) {
        if (this == o) {
            return true;
        } else {
            String str = (String) o;
            if (length != str.length) {
                return false;
            }
            for (int i = 0; i < length; i++) {
                char a = str.data[i + str.offset];
                char b = data[i + offset];
                if (a != b && Character.toUpperCase(a) != Character.toUpperCase(b) && Character.toLowerCase(a) != Character.toLowerCase(b)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int compareTo(String other) {
        int mlen = length <= other.length ? length : other.length;
        for (int i = 0; i < mlen; i++) {
            char thisC = data[i + offset];
            char otherC = other.data[i + other.offset];
            if (thisC != otherC) {
                return thisC - otherC;
            }
        }
        return length - other.length;
    }

    public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
        if (toffset < 0 || ooffset < 0 || toffset + len > length || ooffset + len > other.length) {
            return false;
        }
        toffset += offset;
        ooffset += other.offset;
        for (int i = 0; i < len; i++) {
            char thisC = data[i + toffset];
            char otherC = other.data[i + ooffset];
            if (ignoreCase ? (Character.toUpperCase(thisC) != Character.toUpperCase(otherC) && Character.toLowerCase(thisC) != Character.toLowerCase(otherC)) : (thisC != otherC)) {
                return false;
            }
        }
        return true;
    }

    public boolean startsWith(String prefix, int toffset) {
        if (toffset < 0 || toffset > length) {
            return false;
        } else {
            if (prefix.length > length - toffset) {
                return false;
            }
            return regionMatches(false, toffset, prefix, 0, prefix.length);
        }
    }

    public boolean startsWith(String prefix) {
        if (prefix.length > length) {
            return false;
        }
        return regionMatches(false, 0, prefix, 0, prefix.length);
    }

    public boolean endsWith(String suffix) {
        if (suffix.length > length) {
            return false;
        }
        return regionMatches(false, length - suffix.length, suffix, 0, suffix.length);
    }

    public int hashCode() {
        if (hashCode == 0) { // TODO: What if the hashCode _is_ 0?
            computeHashCode();
        }
        return hashCode;
    }

    private void computeHashCode() {
        int exp = 1;
        int digest = 0;
        for (int i = offset + length - 1; i >= offset; i--) {
            digest += data[i] * exp;
            exp *= 31;
        }
        hashCode = digest;
    }

    public int indexOf(int ch) {
        return indexOf(ch, 0);
    }

    public int indexOf(int ch, int fromIndex) {
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        for (int i = fromIndex; i < length; i++) {
            if (data[i + offset] == ch) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(int ch) {
        return lastIndexOf(ch, length - 1);
    }

    public int lastIndexOf(int ch, int fromIndex) {
        if (fromIndex >= length) {
            fromIndex = length - 1;
        }
        for (int i = fromIndex; i >= 0; i--) {
            if (data[i + offset] == ch) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    public int indexOf(String str, int fromIndex) {
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        for (int i = fromIndex; i < length; i++) {
            if (startsWith(str, i)) {
                return i;
            }
        }
        return -1;
    }

    public String substring(int begin) {
        if (begin < 0 || begin > length) {
            throw new IndexOutOfBoundsException();
        } else if (begin == 0) {
            return this;
        }
        return new String(data, offset + begin, length - begin, true);
    }

    public String substring(int begin, int end) {
        if (begin < 0 || end > length || begin > end) {
            throw new IndexOutOfBoundsException();
        }
        return new String(data, offset + begin, end - begin, true);
    }

    public String concat(String str) {
        if (str.length == 0) {
            return this;
        } else if (length == 0) {
            return str;
        }
        char[] out = new char[length + str.length];
        System.arraycopy(data, offset, out, 0, length);
        System.arraycopy(str.data, str.offset, out, length, str.length);
        return new String(out, 0, out.length, true);
    }

    public String replace(char old, char nchar) {
        if (indexOf(old) == -1) {
            return this;
        } else {
            char[] narr = new char[length];
            for (int i = 0; i < length; i++) {
                char c = data[i + offset];
                narr[i] = c == old ? nchar : c;
            }
            return new String(narr, 0, length, true);
        }
    }

    public String toLowerCase() {
        boolean found = false;
        for (int i = 0; i < length; i++) {
            char c = data[i + offset];
            if (Character.toLowerCase(c) != c) {
                found = true;
                break;
            }
        }
        if (!found) {
            return this;
        }
        char[] narr = new char[length];
        for (int i = 0; i < length; i++) {
            narr[i] = Character.toLowerCase(data[i + offset]);
        }
        return new String(narr, 0, length, true);
    }

    public String toUpperCase() {
        boolean found = false;
        for (int i = 0; i < length; i++) {
            char c = data[i + offset];
            if (Character.toUpperCase(c) != c) {
                found = true;
                break;
            }
        }
        if (!found) {
            return this;
        }
        char[] narr = new char[length];
        for (int i = 0; i < length; i++) {
            narr[i] = Character.toUpperCase(data[i + offset]);
        }
        return new String(narr, 0, length, true);
    }

    public String trim() {
        int start = offset;
        int end = offset + length;
        while (data[start] <= ' ') {
            start++;
            if (start == end) {
                return "";
            }
        }
        while (data[--end] <= ' ');
        return substring(start, end + 1);
    }

    public String toString() {
        return this;
    }

    public char[] toCharArray() {
        char[] out = new char[length];
        System.arraycopy(data, offset, out, 0, length);
        return out;
    }

    public static String valueOf(Object o) {
        return o == null ? "null" : o.toString();
    }

    public static String valueOf(char[] data) {
        return data.length == 0 ? "" : new String(data);
    }

    public static String valueOf(char[] data, int offset, int count) {
        return count == 0 ? "" : new String(data, offset, count);
    }

    public static String valueOf(boolean b) {
        return b ? "true" : "false";
    }

    public static String valueOf(char c) {
        return new String(new char[]{c}, 0, 1, true);
    }

    public static String valueOf(int i) {
        return Integer.toString(i);
    }

    // TODO: When implementing longs, floats, and doubles
    // public static String valueOf(long l)
    // public static String valueOf(float f)
    // public static String valueOf(double d)
    public native String intern();
}

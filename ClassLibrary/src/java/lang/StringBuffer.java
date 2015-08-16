package java.lang;

public final class StringBuffer {

    private char[] data;
    private int length;

    public StringBuffer() {
        data = new char[16];
        length = 0;
    }

    public StringBuffer(int length) {
        if (length < 0) {
            throw new NegativeArraySizeException();
        }
        data = new char[length];
        this.length = 0;
    }

    public StringBuffer(String base) {
        int ln = base.length();
        data = new char[ln + 16];
        base.getChars(0, ln, data, 0);
    }

    public synchronized int length() {
        return length;
    }

    public synchronized int capacity() {
        return data.length;
    }

    public synchronized void ensureCapacity(int minimumCapacity) {
        if (data.length < minimumCapacity) {
            if (minimumCapacity < data.length * 2) {
                minimumCapacity = data.length * 2;
            }
            char[] ndata = new char[minimumCapacity];
            System.arraycopy(data, 0, ndata, 0, length);
            data = ndata;
        }
    }

    public synchronized void setLength(int newLength) {
        if (newLength < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (newLength > length) {
            for (int i = length; i < newLength; i++) {
                data[i] = '\u0000';
            }
        }
        length = newLength;
    }

    public synchronized char charAt(int index) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException();
        }
        return data[index];
    }

    public synchronized void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        if (srcBegin < 0 || dstBegin < 0 || srcBegin > srcEnd || srcEnd > length || dstBegin + srcEnd - srcBegin > dst.length) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = srcBegin, j = dstBegin; i < srcEnd; i++, j++) {
            dst[j] = data[i];
        }
    }

    public synchronized void setCharAt(int index, char ch) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException();
        }
        data[index] = ch;
    }

    public synchronized StringBuffer append(Object obj) {
        return append(String.valueOf(obj));
    }

    public synchronized StringBuffer append(String str) {
        if (str == null) {
            str = "null";
        }
        int ln = str.length();
        ensureCapacity(length + ln);
        str.getChars(0, ln, data, length);
        length += ln;
        return this;
    }

    public synchronized StringBuffer append(char[] moredata) {
        return append(String.valueOf(moredata));
    }

    public synchronized StringBuffer append(char[] moredata, int off, int len) {
        return append(String.valueOf(moredata, off, len));
    }

    public synchronized StringBuffer append(boolean b) {
        return append(String.valueOf(b));
    }

    public synchronized StringBuffer append(char c) {
        return append(String.valueOf(c));
    }

    public synchronized StringBuffer append(int i) {
        return append(String.valueOf(i));
    }

    // TODO: Implement these along with longs, doubles, and floats
    // public synchronized StringBuffer append(long l)
    // public synchronized StringBuffer append(float f)
    // public synchronized StringBuffer append(double d)
    public synchronized StringBuffer delete(int start, int end) { // TODO: Check that this method works
        if (start < 0 || start > length || start > end) {
            throw new StringIndexOutOfBoundsException();
        }
        if (end > length) {
            // remove all
            length = start;
            return this;
        }
        int shift = end - start;
        if (shift == 0) {
            return this;
        }
        for (int i = start; i < length - shift; i++) {
            data[i] = data[i + shift];
        }
        length -= shift;
        return this;
    }

    public synchronized StringBuffer deleteCharAt(int index) {
        if (index < 0 || index >= length) {
            throw new StringIndexOutOfBoundsException();
        }
        for (int i = index + 1; i < length; i++) {
            data[i - 1] = data[i];
        }
        length -= 1;
        return this;
    }

    public synchronized StringBuffer insert(int index, Object o) {
        return insert(index, String.valueOf(o));
    }

    public synchronized StringBuffer insert(int index, String str) {
        if (index < 0 || index > length) {
            throw new StringIndexOutOfBoundsException();
        }
        if (str == null) {
            str = "null";
        }
        int ln = str.length();
        ensureCapacity(length + ln);
        for (int i = length; i < length + ln; i++) {
            data[i] = data[i - ln];
        }
        str.getChars(0, ln, data, length - ln);
        length += ln;
        return this;
    }

    public synchronized StringBuffer insert(int index, char[] o) {
        return insert(index, String.valueOf(o));
    }

    public synchronized StringBuffer insert(int index, boolean o) {
        return insert(index, String.valueOf(o));
    }

    public synchronized StringBuffer insert(int index, int o) {
        return insert(index, String.valueOf(o));
    }

    // TODO: Implement these when longs, floats, and doubles are implemented.
    // public synchronized StringBuffer insert(int index, long o)
    // public synchronized StringBuffer insert(int index, float o)
    // public synchronized StringBuffer insert(int index, double o)
    public synchronized StringBuffer reverse() {
        for (int i = 0; i < length / 2; i++) {
            char c = data[i];
            int oi = length - i - 1;
            data[i] = data[oi];
            data[oi] = c;
        }
        return this;
    }

    public synchronized String toString() {
        return new String(this);
    }
}

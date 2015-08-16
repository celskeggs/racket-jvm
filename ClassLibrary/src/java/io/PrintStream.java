package java.io;

public class PrintStream extends OutputStream {

    // TODO: Add a buffer
    private final OutputStream out;
    private boolean hasError = false;

    public PrintStream(OutputStream out) {
        if (out == null) {
            throw new NullPointerException();
        }
        this.out = out;
    }

    public void flush() {
        try {
            out.flush();
        } catch (IOException ex) {
            hasError = true;
        }
    }

    public void close() {
        flush();
        try {
            out.close();
        } catch (IOException ex) {
            hasError = true;
        }
    }

    public boolean checkError() {
        return hasError;
    }

    public void setError() {
        hasError = true;
    }

    public void write(int b) {
        try {
            out.write(b);
        } catch (IOException ex) {
            hasError = true;
        }
    }

    public void write(byte[] b) {
        try {
            out.write(b);
        } catch (IOException ex) {
            hasError = true;
        }
    }

    public void write(byte[] b, int start, int count) {
        try {
            out.write(b, start, count);
        } catch (IOException ex) {
            hasError = true;
        }
    }

    public void print(boolean b) {
        print(String.valueOf(b));
    }

    public void print(char c) {
        print(String.valueOf(c));
    }

    public void print(int i) {
        print(String.valueOf(i));
    }
    // TODO: Implement these along with longs, doubles, and floats
    // public void print(long l)
    // public void print(double d)
    // public void print(float f)

    public void print(char[] c) {
        print(String.valueOf(c));
    }

    public void print(String str) {
        if (str == null) {
            str = "null";
        }
        write(str.getBytes());
    }

    public void print(Object o) {
        print(String.valueOf(o));
    }

    public void println() {
        write('\n');
    }

    public void println(boolean v) {
        print(v);
        println();
    }

    public void println(char v) {
        print(v);
        println();
    }

    public void println(int v) {
        print(v);
        println();
    }

    // TODO: Implement these along with longs, doubles, and floats
    // public void println(long v)
    // public void println(double v)
    // public void println(float v)
    public void println(char[] v) {
        print(v);
        println();
    }

    public void println(String v) {
        print(v);
        println();
    }

    public void println(Object v) {
        print(v);
        println();
    }
}

package java.io;

public abstract class OutputStream {
    
    public OutputStream() {
    }

    public abstract void write(int b) throws IOException;

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte[] b, int start, int count) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (start < 0 || count < 0 || start + count > b.length) {
            throw new IndexOutOfBoundsException();
        }
        int end = start + count;
        for (int i = start; i < end; i++) {
            write(b[i]);
        }
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
    }
}

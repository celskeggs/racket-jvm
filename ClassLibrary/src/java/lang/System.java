package java.lang;

import java.io.PrintStream;

import vm.VMStandardOutputStream;

public final class System {

    public static final PrintStream out = new PrintStream(new VMStandardOutputStream(false));
    public static final PrintStream err = new PrintStream(new VMStandardOutputStream(true));

    public static native int identityHashCode(Object aThis);

    private System() {
    }

    public static native long currentTimeMillis();
    public static native void arraycopy(Object src, int srcOffset, Object dst, int dstOffset, int length);

    public static String getProperty(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (key.length() == 0) {
            throw new IllegalArgumentException();
        }
        if (key.equals("line.separator")) {
            return "\n"; // duplicated in PrintStream
        }
        // TODO: Implement this better
        return null;
    }

    public static void exit(int status) {
        Runtime.getRuntime().exit(status);
    }

    public static void gc() {
        Runtime.getRuntime().gc();
    }
}

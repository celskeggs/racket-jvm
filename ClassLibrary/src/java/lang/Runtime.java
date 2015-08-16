package java.lang;

import vm.VMRuntime;

public abstract class Runtime {

    private static Runtime rt;

    public static Runtime getRuntime() {
        if (rt == null) {
            rt = new VMRuntime();
        }
        return rt;
    }

    public abstract void exit(int status);

    public abstract long freeMemory();

    public abstract long totalMemory();

    public abstract void gc();
}

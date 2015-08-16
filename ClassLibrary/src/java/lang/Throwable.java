package java.lang;

import vm.VMNatives;

public class Throwable {
    public static boolean traceEnabled = true;

    private String message;
    private StackTraceElement[] trace;
    private String toStr;
    private byte[] bytStr;

    public Throwable() {
        if (traceEnabled) {
        	trace = new StackTraceElement[VMNatives.getStackDepth()];
        	for (int i=0; i<trace.length; i++) {
        		trace[i] = new StackTraceElement(VMNatives.getStackClass(i), VMNatives.getStackMethod(i), VMNatives.getStackFile(i), VMNatives.getStackLine(i));
        	}
        }
        toStr = (message != null) ? getClass().getName() + ": " + message : getClass().getName();
        bytStr = toStr.concat("\n").getBytes();
    }

    public Throwable(String message) {
        this();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return toStr;
    }
    
    private static byte[] ntrc = " no traceback.\n".getBytes();

    public void printStackTrace() {
        System.err.write(bytStr);
        if (trace != null) {
            int len = trace.length;
            for (int i=0; i<len; i++) {
                System.err.println(" at " + trace[i]);
            }
        } else {
            System.err.write(ntrc);
        }
    }
}

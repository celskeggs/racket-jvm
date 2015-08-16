package vm;

public class VMSandbox {
	public static final int OP_PRINT_STDOUT = 0x1;
	public static final int OP_PRINT_STDERR = 0x2;
	
	public static native void doSandboxIO(int operation, byte[] data);
	
	public static void writeStandardOutput(byte[] data) {
		doSandboxIO(OP_PRINT_STDOUT, data);
	}

	public static void writeStandardError(byte[] data) {
		doSandboxIO(OP_PRINT_STDERR, data);
	}
}

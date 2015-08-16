package vm;

public class VMRuntime extends Runtime {

	@Override
	public native void exit(int status);

	@Override
	public native long freeMemory();

	@Override
	public native long totalMemory();

	@Override
	public native void gc();
}

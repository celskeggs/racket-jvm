package vm;

public class VMNatives {

	public static native int call0(int cid);

	public static native int call1(int cid, int argument1);

	public static native byte getCodeByte(int ptr);

	public static native short getCodeShort(int ptr);

	public static native int getCodeInt(int ptr);

	public static native long getCodeLong(int ptr);

	public static native Object idToObject(int id);

	public static native int objectToID(Object obj);

	public static float getCodeFloat(int ptr) {
		return Float.intBitsToFloat(getCodeInt(ptr));
	}

	public static double getCodeDouble(int ptr) {
		return Double.longBitsToDouble(getCodeLong(ptr));
	}

	public static boolean getCodeBoolean(int ptr) {
		return getCodeByte(ptr) != 0;
	}

	public static native Object allocateStructure(int classEntity);

	public static native Object allocateChunk(int size);

	public static native int getStackDepth();

	public static native String getStackClass(int i);

	public static native String getStackMethod(int i);

	public static native String getStackFile(int i);

	public static native int getStackLine(int i);
}

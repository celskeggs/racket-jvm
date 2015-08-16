package vm;

/* Code format:
 * [0-7] MAGIC NUMBER: 0x70A27E52 (toaster)
 * [8-11] offset to string table
 * [12-15] length of string table
 * [16-19] offset to class table
 * [20-23] length of class table
 * 
 * [stringtab+0] pointer to first string array
 * [stringtab+4] pointer to second string array
 * [stringtab+n] etc.
 * 
 * [strarray+0] string length
 * [strarray+4] first byte
 * [strarray+5] second byte
 * [strarray+6] third byte
 * [strarray+n] etc.
 * 
 * [classtab+0] pointer to first class entry
 * [classtab+4] pointer to second class entry
 * [classtab+n] etc.
 * 
 * [class+0] length of class instance (not including type pointer, and in bytes)
 * [class+4] string ID for class name
 * [class+8] null constructor ID
 * [class+12] flags (0x1: INTERFACE)
 * [class+16] interface count
 * [class+20] interface list (of class IDs)
 * [class+24] superclass
 * [class+28] etc...
 */

public class VMDispatch {
	
	static final int FLAG_INTERFACE = 0x1;

	static final int STRING_TABLE_POINTER_OFFSET = 8;
	static final int STRING_TABLE_LENGTH_OFFSET = 12;
	static final int CLASS_TABLE_POINTER_OFFSET = 16;
	static final int CLASS_TABLE_LENGTH_OFFSET = 20;
	
	public static Class getClassByID(int id) {
		return VMAccess.getVMClassByID(id).getRealClass();
	}

	private static String[] globalStrings;
	
	public static String getStringByID(int id) {
		if (globalStrings == null) {
			globalStrings = new String[VMNatives.getCodeInt(STRING_TABLE_LENGTH_OFFSET)];
		} else if (globalStrings[id] != null) {
			return globalStrings[id];
		}
		int sptr = VMNatives.getCodeInt(VMNatives.getCodeInt(STRING_TABLE_POINTER_OFFSET) + 4 * id);
		byte[] bytes = new byte[VMNatives.getCodeInt(sptr)];
		sptr += 4;
		for (int i=0; i<bytes.length; i++) {
			bytes[i] = VMNatives.getCodeByte(sptr + i);
		}
		return globalStrings[id] = new String(bytes);
	}

	public static int resolveVirtual(int classid, int namedesc) {
		
	}
	
	public static Object rawNewObject(int classid) {
		return VMNatives.allocateStructure(VMAccess.getClassEntity(classid));
	}
}

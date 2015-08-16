package vm;

public abstract class VMClass {
	// TODO: find this somehow
	private static final int ALLOCATE_CLASS_FOR_VMCLASS_METHOD_ID = -1;

	public static final VMClass BOOLEAN = null;
	public static final VMClass BYTE = null;
	public static final VMClass CHAR = null;
	public static final VMClass INT = null;
	public static final VMClass SHORT = null;

	private Class realClass;

	VMClass() {
	}

	public Class getRealClass() {
		if (realClass == null) {
			realClass = allocateClassForVMClass(this);
		}
		return realClass;
	}

	public abstract boolean isPrimitive();
	
	public abstract Object newInstance() throws InstantiationException;

	public abstract String getName();

	public abstract boolean isArray();

	public abstract boolean isInterface();

	public abstract boolean isAssignableFrom(VMClass cls);

	public abstract int getInterfaceCount();

	public abstract VMClass getInterfaceN(int i);

	public abstract VMClass getSuperClass();

	static final class Java extends VMClass {

		private final int id;

		Java(int id) {
			this.id = id;
		}

		@Override
		public boolean isPrimitive() {
			return false;
		}

		public Object newInstance() throws InstantiationException {
			Object rawInstance = VMDispatch.rawNewObject(id);
			int ncu = VMAccess.getNullConstructor(id);
			if (ncu == 0) {
				throw new InstantiationException("No nullary constructor!");
			}
			// TODO: check access
			try {
				VMNatives.call1(ncu, VMNatives.objectToID(rawInstance));
			} catch (Throwable thr) {
				throw new InstantiationException("Exception while instantiating class: " + thr);
			}
			return rawInstance;
		}

		@Override
		public String getName() {
			return VMAccess.getVMClassName(id);
		}

		@Override
		public boolean isArray() {
			return false;
		}

		@Override
		public boolean isInterface() {
			return (VMAccess.getVMClassFlags(id) & VMDispatch.FLAG_INTERFACE) != 0;
		}

		@Override
		public boolean isAssignableFrom(VMClass cls) {
			if (cls == this) {
				return true;
			} else if (cls.isPrimitive()) {
				return false;
			} else if (cls.isArray() || cls.isInterface()) {
				return "java/lang/Object".equals(getName());
			} else if (this.isAssignableFrom(cls.getSuperClass())) {
				return true;
			} else {
				int ic = cls.getInterfaceCount();
				for (int i=0; i<ic; i++) {
					VMClass vmc = cls.getInterfaceN(i);
					if (this.isAssignableFrom(vmc)) {
						return true;
					}
				}
				return false;
			}
		}

		@Override
		public int getInterfaceCount() {
			return VMAccess.getInterfaceCount(id);
		}

		@Override
		public VMClass getInterfaceN(int i) {
			return VMAccess.getVMClassByID(VMAccess.getInterfaceN(id, i));
		}

		@Override
		public VMClass getSuperClass() {
			return VMAccess.getSuperClass(id);
		}
	}

	private static Class allocateClassForVMClass(VMClass id) {
		return (Class) VMNatives.idToObject(VMNatives.call1(ALLOCATE_CLASS_FOR_VMCLASS_METHOD_ID, VMNatives.objectToID(id)));
	}
}

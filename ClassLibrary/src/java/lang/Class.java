package java.lang;

import java.io.InputStream;

import vm.VMAccess;
import vm.VMClass;

public final class Class {
	
	// Called dynamically by VMDispatch.
	static Class allocateClassForVMClass(VMClass vmClass) {
		return new Class(vmClass);
	}
	
	private final VMClass cls;

	private Class(VMClass cls) {
        this.cls = cls;
    }

    public String toString() {
        if (cls.isPrimitive()) {
            return getName();
        } else {
            if (isInterface()) {
                return "interface " + getName();
            } else {
                return "class " + getName();
            }
        }
    }

    public static Class forName(String className) throws ClassNotFoundException {
    	if (className.indexOf('/') != -1) {
    		throw new ClassNotFoundException();
    	}
    	Class cls = VMAccess.getClassByName(className.replace('.', '/'));
        if (cls == null) {
            throw new ClassNotFoundException();
        }
        return cls;
    }

    public Object newInstance() throws InstantiationException, IllegalAccessException {
        return cls.newInstance();
    }

    public boolean isInstance(Object o) {
        return o != null && cls.isAssignableFrom(o.getClass().cls);
    }

    public boolean isAssignableFrom(Class o) {
        return cls.isAssignableFrom(o.cls);
    }

    public boolean isInterface() {
        return cls.isInterface();
    }

    public boolean isArray() {
        return cls.isArray();
    }

    public String getName() {
        return cls.getName().replace('/', '.');
    }

    // TODO: Implement InputStream
    public InputStream getResourceAsStream(String name) {
    	return null; // TODO: Allow reading of resources
    }
}

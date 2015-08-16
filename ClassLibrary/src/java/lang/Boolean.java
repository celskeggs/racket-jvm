package java.lang;

import vm.VMClass;

public final class Boolean {

    public static final Boolean FALSE = new Boolean(false);
    public static final Boolean TRUE = new Boolean(true);
    public static final Class TYPE = VMClass.BOOLEAN.getRealClass();
    private final boolean val;

    public Boolean(boolean val) {
        this.val = val;
    }

    public boolean booleanValue() {
        return val;
    }

    public String toString() {
        return val ? "true" : "false";
    }

    public int hashCode() {
        return val ? 1231 : 1237;
    }

    public boolean equals(Object o) {
        return (o instanceof Boolean) && (((Boolean) o).val == val);
    }
}

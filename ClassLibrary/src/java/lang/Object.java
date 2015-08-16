package java.lang;

public class Object {

    int classpointer; // required to be here by C code
    int gcpointer;
    int hashcode;

    // Any initalizer will not be called for arrays!
    public Object() {
    }

    public final native Class getClass();

    public int hashCode() {
        return System.identityHashCode(this);
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public String toString() {
        return getClass().getName() + '@' + Integer.toHexString(hashCode());
    }

    public final void notify() {
        throw new IncompleteImplementationError();
    }

    public final void notifyAll() {
        throw new IncompleteImplementationError();
    }

    public final void wait(long timeout) throws InterruptedException {
        throw new IncompleteImplementationError();
    }

    public final void wait(long timeout, int nanos) throws InterruptedException {
        throw new IncompleteImplementationError();
    }

    public final void wait() throws InterruptedException {
        throw new IncompleteImplementationError();
    }
}

package org.sugarcubes.cloner;

/**
 * @author Maxim Butov
 */
public final class NoopCloner<T> implements ObjectCloner<T> {

    @Override
    public boolean isTrivial() {
        return true;
    }

    @Override
    public T clone(T original, LaterSupport laterSupport) throws Throwable {
        return original;
    }

}

package org.sugarcubes.cloner;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.sugarcubes.cloner.ObjectAllocator;

/**
 * Allocator which uses Objenesis library to create objects.
 *
 * @author Maxim Butov
 */
public class ObjenesisAllocator implements ObjectAllocator {

    private final Objenesis objenesis;

    /**
     * Default constructor.
     */
    public ObjenesisAllocator() {
        this(new ObjenesisStd());
    }

    /**
     * Constructor with an {@link Objenesis} instance.
     */
    public ObjenesisAllocator(Objenesis objenesis) {
        this.objenesis = objenesis;
    }

    @Override
    public <T> T newInstance(Class<T> clazz) throws Throwable {
        return objenesis.newInstance(clazz);
    }

}
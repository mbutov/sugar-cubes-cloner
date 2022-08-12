package org.sugarcubes.cloner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * Builder for the {@link  SequentialReflectionCloner}.
 *
 * @author Maxim Butov
 */
public final class ReflectionClonerBuilder {

    private ObjectAllocator allocator;
    private CloningPolicy policy;
    private FieldCopierFactory fieldCopierFactory;
    private TraversalAlgorithm traversalAlgorithm;
    private ExecutorService executor;
    private final Map<Class<?>, ObjectCopier<?>> copiers = new HashMap<>();

    /**
     * Sets object allocator.
     *
     * @param allocator object allocator
     * @return same builder instance
     */
    public ReflectionClonerBuilder setAllocator(ObjectAllocator allocator) {
        Check.isNull(this.allocator, "Allocator already set");
        this.allocator = allocator;
        return this;
    }

    /**
     * Sets cloning policy.
     *
     * @param policy cloning policy
     * @return same builder instance
     */
    public ReflectionClonerBuilder setPolicy(CloningPolicy policy) {
        Check.isNull(this.policy, "Policy already set");
        this.policy = policy;
        return this;
    }

    /**
     * Sets field copier factory.
     *
     * @param fieldCopierFactory field copier factory
     * @return same builder instance
     */
    public ReflectionClonerBuilder setFieldCopierFactory(FieldCopierFactory fieldCopierFactory) {
        Check.isNull(this.fieldCopierFactory, "Field copier factory already set");
        this.fieldCopierFactory = fieldCopierFactory;
        return this;
    }

    /**
     * Sets allocator and field copier factory which use {@link sun.misc.Unsafe}.
     *
     * @return same builder instance
     */
    public ReflectionClonerBuilder setUnsafeEnabled() {
        return setAllocator(new UnsafeAllocator()).setFieldCopierFactory(new UnsafeFieldCopierFactory());
    }

    /**
     * Sets traversal algorithm for objects graph.
     *
     * @param traversalAlgorithm traversal algorithm
     * @return same builder instance
     */
    public ReflectionClonerBuilder setTraversalAlgorithm(TraversalAlgorithm traversalAlgorithm) {
        Check.isNull(this.traversalAlgorithm, "Traversal algorithm already set");
        this.traversalAlgorithm = traversalAlgorithm;
        return this;
    }

    /**
     * Registers set of custom copiers.
     *
     * @param copiers custom copiers
     * @return same builder instance
     */
    public ReflectionClonerBuilder setCopiers(Map<Class<?>, ObjectCopier<?>> copiers) {
        this.copiers.putAll(copiers);
        return this;
    }

    /**
     * Registers custom copier for type.
     *
     * @param type object type
     * @param copier custom copier
     * @return same builder instance
     */
    public ReflectionClonerBuilder setCopier(Class<?> type, ObjectCopier<?> copier) {
        this.copiers.put(type, copier);
        return this;
    }

    /**
     * Enables parallel mode with given executor service.
     *
     * @param executor executor service
     * @return same builder instance
     */
    public ReflectionClonerBuilder setExecutor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    /**
     * Enables parallel mode with common fork-join pool.
     *
     * @return same builder instance
     */
    public ReflectionClonerBuilder setDefaultExecutor() {
        return setExecutor(ForkJoinPool.commonPool());
    }

    /**
     * Creates an instance of the cloner on the basis of the configuration.
     *
     * @return cloner
     */
    public Cloner build() {
        ObjectAllocator allocator = Optional.ofNullable(this.allocator).orElseGet(ObjectAllocator::defaultAllocator);
        CloningPolicy policy = Optional.ofNullable(this.policy).orElseGet(CustomCloningPolicy::new);
        FieldCopierFactory fieldCopierFactory = Optional.ofNullable(this.fieldCopierFactory)
            .orElseGet(ReflectionFieldCopierFactory::new);
        if (executor != null) {
            return new ParallelReflectionCloner(allocator, policy, copiers, fieldCopierFactory, executor);
        }
        else {
            return new SequentialReflectionCloner(allocator, policy, copiers, fieldCopierFactory,
                traversalAlgorithm != null ? traversalAlgorithm : TraversalAlgorithm.DEPTH_FIRST);
        }
    }

}
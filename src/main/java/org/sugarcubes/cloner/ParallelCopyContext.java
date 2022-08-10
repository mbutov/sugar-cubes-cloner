package org.sugarcubes.cloner;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Copy context for parallel copying.
 *
 * @author Maxim Butov
 */
public class ParallelCopyContext extends AbstractCopyContext {

    /**
     * Executor service.
     */
    private final ExecutorService executor;

    /**
     * Queue of the futures.
     */
    private final Queue<Future<?>> futures = new ConcurrentLinkedDeque<>();

    /**
     * Running flag.
     */
    private volatile boolean running = true;

    /**
     * Creates an instance.
     *
     * @param registry copier registry
     * @param executor executor service
     */
    public ParallelCopyContext(CopierRegistry registry, ExecutorService executor) {
        super(registry);
        this.executor = executor;
    }

    @Override
    protected <T> T nonTrivialCopy(T original, ObjectCopier<T> copier) throws Exception {
        T clone;
        CopyResult<T> result;
        synchronized (original) {
            clone = (T) clones.get(original);
            if (clone != null) {
                return clone;
            }
            result = copier.copy(original, this);
            clone = result.getObject();
            synchronized (clones) {
                clones.put(original, clone);
            }
        }
        if (running) {
            result.ifHasNext(next -> futures.offer(executor.submit(next)));
        }
        return clone;
    }

    @Override
    public void complete() throws Exception {
        for (Future<?> future; (future = futures.poll()) != null; ) {
            try {
                future.get();
            }
            catch (ExecutionException e) {
                running = false;
                try {
                    throw e.getCause();
                }
                catch (Error | Exception ex) {
                    throw ex;
                }
                catch (Throwable ex) {
                    throw new UndeclaredThrowableException(ex);
                }
            }
        }
    }

}

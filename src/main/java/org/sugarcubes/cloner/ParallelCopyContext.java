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
    private volatile boolean running;

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
        CopyResult<T> result;
        synchronized (original) {
            T clone = (T) clones.get(original);
            if (clone != null) {
                return clone;
            }
            result = copier.copy(original, this);
            synchronized (clones) {
                clones.put(original, result.getObject());
            }
        }
        if (running) {
            result.ifHasNext(next -> futures.offer(executor.submit(next)));
        }
        return result.getObject();
    }

    @Override
    public void complete() throws Exception {
        for (Future<?> task; (task = futures.poll()) != null; ) {
            try {
                task.get();
            }
            catch (ExecutionException e) {
                running = false;
                try {
                    throw e.getCause();
                }
                catch (Error | Exception ex) {
                    throw new RuntimeException(ex);
                }
                catch (Throwable ex) {
                    throw new UndeclaredThrowableException(ex);
                }
            }
        }
    }

}
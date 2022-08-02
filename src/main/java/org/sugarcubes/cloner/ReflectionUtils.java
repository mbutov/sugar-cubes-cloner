package org.sugarcubes.cloner;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Shortcuts for Java Reflection API.
 *
 * @author Maxim Butov
 */
public class ReflectionUtils {

    @FunctionalInterface
    public interface ReflectionAction<T> {

        T get() throws ReflectiveOperationException;

    }

    @FunctionalInterface
    public interface VoidReflectionAction extends ReflectionAction<Object> {

        @Override
        default Object get() throws ReflectiveOperationException {
            run();
            return null;
        }

        void run() throws ReflectiveOperationException;

    }

    /**
     * Executes call to Reflection API and replaces {@link ReflectiveOperationException} with {@link ClonerException}.
     */
    public static <T> T execute(ReflectionAction<T> action) {
        try {
            return action.get();
        }
        catch (ReflectiveOperationException e) {
            throw new ClonerException(e);
        }
    }

    /**
     * Returns accessible field.
     */
    public static Field getField(Class<?> type, String name) {
        return execute(() -> makeAccessible(type.getDeclaredField(name)));
    }

    /**
     * Returns accessible method.
     */
    public static Method getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
        return execute(() -> makeAccessible(type.getDeclaredMethod(name, parameterTypes)));
    }

    /**
     * Makes object accessible.
     */
    public static <T extends AccessibleObject> T makeAccessible(T object) {
        object.setAccessible(true);
        return object;
    }

}
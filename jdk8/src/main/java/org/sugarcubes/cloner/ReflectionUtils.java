/*
 * Copyright 2017-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sugarcubes.cloner;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Shortcuts for Java Reflection API.
 *
 * @author Maxim Butov
 */
public class ReflectionUtils {

    /**
     * Variant of the {@link java.util.function.Supplier} with declared exception.
     */
    @FunctionalInterface
    public interface ReflectionAction<T> {

        /**
         * Actual work.
         *
         * @return work result
         * @throws ReflectiveOperationException in case of reflection error
         */
        T get() throws ReflectiveOperationException;

    }

    /**
     * Executes call to Reflection API and replaces {@link ReflectiveOperationException} with {@link ClonerException}.
     *
     * @param <T> object type
     * @param action code to execute
     * @return execution result
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
     * Checks the availability of the class in current class loader.
     *
     * @param className name of the class
     * @return {@code true} if class is available
     */
    public static boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Version of {@link Class#forName(String)} with unchecked exception.
     *
     * @param className class name
     * @return {@link Class} instance
     */
    public static Class<?> classForName(String className) {
        return execute(() -> Class.forName(className));
    }

    /**
     * Returns accessible field.
     * Same as {@link Class#getDeclaredField(String)}.
     *
     * @param type {@link Class} instance
     * @param name field name
     * @return field
     */
    public static Field getField(Class<?> type, String name) {
        return makeAccessible(execute(() -> type.getDeclaredField(name)));
    }

    /**
     * Returns accessible method.
     * Same as {@link Class#getDeclaredMethod(String, Class[])}.
     *
     * @param type {@link Class} instance
     * @param name method name
     * @param parameterTypes parameter types
     * @return method
     */
    public static Method getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
        return makeAccessible(execute(() -> type.getDeclaredMethod(name, parameterTypes)));
    }

    /**
     * Returns accessible constructor.
     * Same as {@link Class#getDeclaredConstructor(Class[])}.
     *
     * @param type {@link Class} instance
     * @param parameterTypes parameter types
     * @param <T> type
     * @return method
     */
    public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... parameterTypes) {
        return makeAccessible(execute(() -> type.getDeclaredConstructor(parameterTypes)));
    }

    /**
     * Makes object accessible.
     *
     * @param <T> can be field or method or constructor
     * @param object field or method or constructor
     * @return same object
     */
    public static <T extends AccessibleObject> T makeAccessible(T object) {
        object.setAccessible(true);
        return object;
    }

    /**
     * Returns {@code true} if the member of class is non-static.
     *
     * @param member field or method or constructor
     *
     * @return {@code true} if the member is non-static
     */
    public static boolean isNonStatic(Member member) {
        return !Modifier.isStatic(member.getModifiers());
    }

    /**
     * Utility class.
     */
    private ReflectionUtils() {
    }

}

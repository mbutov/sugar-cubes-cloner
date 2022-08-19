package org.sugarcubes.cloner.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.sugarcubes.cloner.ObjectCopier;

/**
 * Annotation for applying {@link ObjectCopier} to type.
 *
 * @author Maxim Butov
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TypeCopier {

    /**
     * Copier for the type.
     *
     * @return copier class
     */
    Class<ObjectCopier<?>> value();

}
package org.sugarcubes.cloner;

import java.lang.reflect.Field;

/**
 * Set of rules for objects cloning.
 *
 * @author Maxim Butov
 */
public interface CopyPolicy {

    /**
     * Returns action to apply to a field value.
     *
     * @param field field
     * @return action
     */
    FieldCopyAction getFieldAction(Field field);

    /**
     * Returns action to apply to an instance of the type.
     *
     * @param type type
     * @return action
     */
    default CopyAction getTypeAction(Class<?> type) {
        return type.isEnum() ? CopyAction.ORIGINAL : CopyAction.DEFAULT;
    }

}

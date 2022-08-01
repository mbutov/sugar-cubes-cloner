package org.sugarcubes.cloner.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sugarcubes.cloner.CloningPolicy;
import org.sugarcubes.cloner.CopyAction;

public class FieldCache {

    private final CloningPolicy policy;
    private final Map<Class<?>, List<Map.Entry<Field, CopyAction>>> cache = new HashMap<>();

    public FieldCache(CloningPolicy policy) {
        this.policy = policy;
        this.cache.put(Object.class, Collections.emptyList());
    }

    public List<Map.Entry<Field, CopyAction>> getFields(Class<?> type) {
        List<Map.Entry<Field, CopyAction>> fields = cache.get(type);
        if (fields == null) {
            fields = Collections.unmodifiableList(
                Stream.concat(getFields(type.getSuperclass()).stream(), findFields(type))
                    .collect(Collectors.toList())
            );
            cache.put(type, fields);
        }
        return fields;
    }

    private Stream<Map.Entry<Field, CopyAction>> findFields(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            .peek(ReflectionUtils::makeAccessible)
            .collect(Collectors.toMap(Function.identity(), policy::getFieldAction))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() != CopyAction.SKIP);
    }

}
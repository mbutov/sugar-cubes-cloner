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

/**
 * Allocator which uses no-arg constructor to create object.
 *
 * @see ObjenesisAllocator
 *
 * @author Maxim Butov
 */
public class ReflectionAllocator implements ObjectAllocator {

    @Override
    public <T> T newInstance(Class<T> type) throws Exception {
        return getFactory(type).newInstance();
    }

    @Override
    public <T> ObjectFactory<T> getFactory(Class<T> type) {
        return ReflectionUtils.getConstructor(type)::newInstance;
    }

}
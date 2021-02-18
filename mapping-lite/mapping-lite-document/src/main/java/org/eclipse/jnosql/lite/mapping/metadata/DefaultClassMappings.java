/*
 *  Copyright (c) 2020 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.eclipse.jnosql.lite.mapping.metadata;

import java.util.Optional;

public class DefaultClassMappings implements ClassMappings{
    @Override
    public EntityMetadata get(Class<?> classEntity) {
        return null;
    }

    @Override
    public EntityMetadata findByName(String name) {
        return null;
    }

    @Override
    public Optional<EntityMetadata> findBySimpleName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityMetadata> findByClassName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityMetadata> findByClass(Class<?> classEntity) {
        return Optional.empty();
    }
}

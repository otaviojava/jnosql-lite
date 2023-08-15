/*
 *  Copyright (c) 2023 Otávio Santana and others
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
package org.eclipse.jnosql.lite.mapping;

import jakarta.data.repository.DataRepository;
import org.eclipse.jnosql.mapping.metadata.ClassScanner;

import java.util.Collections;
import java.util.Set;

/**
 * A class that implements the {@link ClassScanner} interface using the ClassGraph library for class scanning.
 * This class provides methods to scan for different types of classes such as entities, repositories, and embeddables.
 */
public final class LiteClassScanner implements ClassScanner {
    @Override
    public Set<Class<?>> entities() {
        return Collections.emptySet();
    }

    @Override
    public Set<Class<?>> repositories() {
        return Collections.emptySet();
    }

    @Override
    public Set<Class<?>> embeddables() {
        return Collections.emptySet();
    }

    @Override
    public <T extends DataRepository<?, ?>> Set<Class<?>> repositories(Class<T> filter) {
        return Collections.emptySet();
    }

    @Override
    public Set<Class<?>> repositoriesStandard() {
        return Collections.emptySet();
    }
}

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

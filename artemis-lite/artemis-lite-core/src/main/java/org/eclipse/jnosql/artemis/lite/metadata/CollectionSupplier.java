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
package org.eclipse.jnosql.artemis.lite.metadata;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A collection supplier to create an {@link Collection}
 *
 * @param <T> the collection instance
 */
public interface CollectionSupplier<T extends Collection<?>> extends Supplier<T>, Predicate<Class<?>> {
}

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

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Function;

enum CollectionUtil implements Function<Class<?>, String> {
    INSTANCE;

    static final String NEW_LIST = "new java.util.ArrayList<>()";
    static final String NEW_SET = "new java.util.HashSet<>()";
    static final String NEW_DEQUE = "new java.util.LinkedList<>()";
    static final String NEW_TREE_SET = "new java.util.TreeSet<>()";

    static final String DEFAULT = "null";

    @Override
    public String apply(Class<?> type) {
        if (isCollection(type)) {
            return NEW_LIST;
        } else if (isSet(type)) {
            return NEW_SET;
        } else if (isDeque(type)) {
            return NEW_DEQUE;
        } else if (isTreeSet(type)) {
            return NEW_TREE_SET;
        }
        return DEFAULT;
    }

    private boolean isCollection(Class<?> type) {
        return List.class.equals(type) ||
                Iterable.class.equals(type)
                || Collection.class.equals(type);
    }

    private boolean isSet(Class<?> type) {
        return Set.class.equals(type);
    }

    private boolean isDeque(Class<?> type) {
        return Deque.class.equals(type) || Queue.class.equals(type);
    }

    private boolean isTreeSet(Class<?> type) {
        return NavigableSet.class.equals(type)
                || SortedSet.class.equals(type);
    }
}

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

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionUtilTest {


    private final CollectionUtil collectionUtil= CollectionUtil.INSTANCE;
    @Test
    public void shouldReturnList(){
        assertThat(collectionUtil.apply(List.class.getName())).isEqualTo(CollectionUtil.NEW_LIST);
        assertThat(collectionUtil.apply(Iterable.class.getName())).isEqualTo(CollectionUtil.NEW_LIST);
        assertThat(collectionUtil.apply(Collection.class.getName())).isEqualTo(CollectionUtil.NEW_LIST);
    }

    @Test
    public void shouldReturnSet(){
        assertThat(collectionUtil.apply(Set.class.getName())).isEqualTo(CollectionUtil.NEW_SET);
    }

    @Test
    public void shouldReturnDeque(){
        assertThat(collectionUtil.apply(Deque.class.getName())).isEqualTo(CollectionUtil.NEW_DEQUE);
        assertThat(collectionUtil.apply(Queue.class.getName())).isEqualTo(CollectionUtil.NEW_DEQUE);
    }

    @Test
    public void shouldReturnTreeSet(){
        assertThat(collectionUtil.apply(NavigableSet.class.getName())).isEqualTo(CollectionUtil.NEW_TREE_SET);
        assertThat(collectionUtil.apply(SortedSet.class.getName())).isEqualTo(CollectionUtil.NEW_TREE_SET);
    }

    @Test
    public void shouldReturnDefault(){
        assertThat(collectionUtil.apply(String.class.getName())).isEqualTo(CollectionUtil.DEFAULT);
    }
}
/*
 *  Copyright (c) 2021 Otávio Santana and others
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
package org.eclipse.jnosql.lite.mapping.repository;

import jakarta.nosql.mapping.Repository;
import jakarta.nosql.mapping.keyvalue.KeyValueTemplate;

import javax.annotation.processing.Generated;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Generated(value = "JNoSQL Lite KeyValue Repository Generator", date = "{{now}}")
@javax.enterprise.context.ApplicationScoped
public class KeyValueTemp<K, V> implements Repository<K, V> {

    private final KeyValueTemplate template;

    @Inject
    public KeyValueTemp(KeyValueTemplate template) {
        this.template = template;
    }

    @Override
    public <S extends K> S save(S entity) {
        Objects.requireNonNull(entity, "entity is required");
        this.template.put(entity);
        return null;
    }

    @Override
    public <S extends K> Iterable<S> save(Iterable<S> entities) {
        Objects.requireNonNull(entities, "entities is required");
        entities.forEach(this::save);
        return entities;
    }

    @Override
    public void deleteById(V id) {
        Objects.requireNonNull(id, "id is required");
        this.template.delete(id);
    }

    @Override
    public void deleteById(Iterable<V> ids) {
        Objects.requireNonNull(ids, "ids is required");
        ids.forEach(this::deleteById);
    }

    @Override
    public Optional<K> findById(V id) {
        Objects.requireNonNull(id, "id is required");
        return this.template.get(id, Long.class);
    }

    @Override
    public Iterable<K> findById(Iterable<V> ids) {
        Objects.requireNonNull(ids, "ids is required");
        return this.template.get(ids, Long.class);
    }

    @Override
    public boolean existsById(V id) {
        Objects.requireNonNull(id, "id is required");
        return this.template.get(id, Long.class).isPresent();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Key-Value does not support count method");
    }
}

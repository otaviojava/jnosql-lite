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
package org.eclipse.jnosql.lite.mapping.keyvalue;

import jakarta.nosql.NonUniqueResultException;
import jakarta.nosql.Value;
import jakarta.nosql.keyvalue.BucketManager;
import jakarta.nosql.keyvalue.KeyValueEntity;
import jakarta.nosql.mapping.PreparedStatement;
import jakarta.nosql.mapping.keyvalue.KeyValueEntityConverter;
import jakarta.nosql.mapping.keyvalue.KeyValueTemplate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

@ApplicationScoped
public class LiteKeyValueTemplate implements KeyValueTemplate {

    private BucketManager manager;

    private final KeyValueEntityConverter converter;

    @Inject
    public LiteKeyValueTemplate(BucketManager manager) {
        this.manager = manager;
        this.converter = new LiteKeyValueEntityConverter();
    }

    @Override
    public <T> T put(T entity) {
        requireNonNull(entity, "entity is required");
        this.manager.put(this.converter.toKeyValue(entity));
        return entity;
    }

    @Override
    public <T> T put(T entity, Duration ttl) {
        requireNonNull(entity, "entity is required");
        requireNonNull(ttl, "ttl class is required");
        this.manager.put(this.converter.toKeyValue(entity), ttl);
        return entity;
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities, Duration ttl) {
        return put(entities, ttl);
    }

    @Override
    public <T> T update(T entity) {
        return put(entity);
    }

    @Override
    public <T> Iterable<T> update(Iterable<T> entities) {
        return put(entities);
    }

    @Override
    public <T> T insert(T entity) {
        return put(entity);
    }

    @Override
    public <T> T insert(T entity, Duration ttl) {
        return put(entity, ttl);
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities) {
        return put(entities);
    }

    @Override
    public <K, T> Optional<T> get(K key, Class<T> entityClass) {
        requireNonNull(key, "key is required");
        requireNonNull(entityClass, "entity class is required");

        Optional<Value> value = getManager().get(key);
        return value.map(v -> converter.toEntity(entityClass, KeyValueEntity.of(key, v)))
                .filter(Objects::nonNull);
    }

    @Override
    public <K, T> Iterable<T> get(Iterable<K> keys, Class<T> entityClass) {
        requireNonNull(keys, "keys is required");
        requireNonNull(entityClass, "entity class is required");
        return StreamSupport.stream(keys.spliterator(), false)
                .map(k -> getManager().get(k)
                        .map(v -> KeyValueEntity.of(k, v)))
                .filter(Optional::isPresent)
                .map(e -> converter.toEntity(entityClass, e.get()))
                .collect(Collectors.toList());
    }

    @Override
    public <K> void delete(K key) {
        requireNonNull(key, "key is required");
        getManager().delete(key);
    }

    @Override
    public <K> void delete(Iterable<K> keys) {
        requireNonNull(keys, "keys is required");
        getManager().delete(keys);
    }

    @Override
    public <T> Stream<T> query(String query, Class<T> entityClass) {
        requireNonNull(query, "query is required");
        requireNonNull(entityClass, "entityClass is required");
        Stream<Value> values = getManager().query(query);
        return values.map(v -> v.get(entityClass));
    }

    @Override
    public <T> Optional<T> getSingleResult(String query, Class<T> entityClass) {
        requireNonNull(query, "query is required");
        requireNonNull(entityClass, "entityClass is required");

        Stream<T> entities = query(query, entityClass);
        final Iterator<T> iterator = entities.iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        }
        final T entity = iterator.next();
        if (!iterator.hasNext()) {
            return Optional.of(entity);
        }
        throw new NonUniqueResultException("No Unique result found to the query: " + query);
    }

    @Override
    public void query(String query) {
        requireNonNull(query, "query is required");
        getManager().query(query);
    }

    @Override
    public <T> PreparedStatement prepare(String query, Class<T> entityClass) {
        requireNonNull(query, "query is required");
        return new KeyValuePreparedStatement(getManager().prepare(query), entityClass);
    }

    @Override
    public <T, K> Optional<T> find(Class<T> entityClass, K id) {
        return this.get(id, entityClass);
    }

    @Override
    public <T, K> void delete(Class<T> entityClass, K id) {
        this.delete(id);
    }

    private BucketManager getManager() {
        return manager;
    }
}

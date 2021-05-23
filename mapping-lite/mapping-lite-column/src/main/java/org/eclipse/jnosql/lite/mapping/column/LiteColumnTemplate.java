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
package org.eclipse.jnosql.lite.mapping.column;

import jakarta.nosql.NonUniqueResultException;
import jakarta.nosql.Value;
import jakarta.nosql.column.ColumnDeleteQuery;
import jakarta.nosql.column.ColumnEntity;
import jakarta.nosql.column.ColumnFamilyManager;
import jakarta.nosql.column.ColumnObserverParser;
import jakarta.nosql.column.ColumnQuery;
import jakarta.nosql.column.ColumnQueryParser;
import jakarta.nosql.mapping.AttributeConverter;
import jakarta.nosql.mapping.IdNotFoundException;
import jakarta.nosql.mapping.Page;
import jakarta.nosql.mapping.PreparedStatement;
import jakarta.nosql.mapping.column.ColumnEntityConverter;
import jakarta.nosql.mapping.column.ColumnQueryPagination;
import jakarta.nosql.mapping.column.ColumnTemplate;
import org.eclipse.jnosql.communication.column.query.DefaultColumnQueryParser;
import org.eclipse.jnosql.lite.mapping.metadata.ClassMappings;
import org.eclipse.jnosql.lite.mapping.metadata.DefaultClassMappings;
import org.eclipse.jnosql.lite.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldMetadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

@ApplicationScoped
public class LiteColumnTemplate implements ColumnTemplate {

    private static final ColumnQueryParser PARSER = new DefaultColumnQueryParser();

    private final ColumnEntityConverter converter;

    private final ColumnFamilyManager manager;

    private final ClassMappings mappings;

    private final ColumnObserverParser observerParser;

    @Inject
    public LiteColumnTemplate(ColumnFamilyManager manager) {
        this.manager = Objects.requireNonNull(manager, "manager is required");
        this.converter = new LiteColumnEntityConverter();
        this.mappings = new DefaultClassMappings();
        this.observerParser = new LiteColumnMapperObserver(this.mappings);
    }

    @Override
    public <T> T insert(T entity) {
        requireNonNull(entity, "entity is required");
        ColumnEntity columnEntity = this.converter.toColumn(entity);
        return converter.toEntity(entity, manager.insert(columnEntity));
    }

    @Override
    public <T> T insert(T entity, Duration ttl) {
        requireNonNull(entity, "entity is required");
        requireNonNull(ttl, "ttl is required");
        ColumnEntity columnEntity = this.converter.toColumn(entity);
        return converter.toEntity(entity, manager.insert(columnEntity, ttl));
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities) {
        requireNonNull(entities, "entity is required");
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::insert).collect(Collectors.toList());
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> entities, Duration ttl) {
        requireNonNull(entities, "entities is required");
        requireNonNull(ttl, "ttl is required");
        return StreamSupport.stream(entities.spliterator(), false)
                .map(e -> insert(e, ttl))
                .collect(Collectors.toList());
    }

    @Override
    public <T> T update(T entity) {
        requireNonNull(entity, "entity is required");
        ColumnEntity columnEntity = this.converter.toColumn(entity);
        return converter.toEntity(entity, manager.update(columnEntity));
    }

    @Override
    public <T> Iterable<T> update(Iterable<T> entities) {
        requireNonNull(entities, "entity is required");
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::update).collect(Collectors.toList());
    }

    @Override
    public void delete(ColumnDeleteQuery query) {
        requireNonNull(query, "query is required");
        this.manager.delete(query);
    }

    @Override
    public <T> Stream<T> select(ColumnQuery query) {
        Objects.requireNonNull(query, "query is required");
        return executeQuery(query);
    }

    @Override
    public <T> Page<T> select(ColumnQueryPagination query) {
        Objects.requireNonNull(query, "query is required");
        Stream<T> entities = executeQuery(query);
        return new ColumnPage<>(this, entities, query);
    }

    @Override
    public <T> Optional<T> singleResult(ColumnQuery query) {
        Objects.requireNonNull(query, "query is required");
        final Stream<T> entities = select(query);
        final Iterator<T> iterator = entities.iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        }
        final T entity = iterator.next();
        if (!iterator.hasNext()) {
            return Optional.of(entity);
        }
        throw new NonUniqueResultException("No unique result found to the query: " + query);
    }

    @Override
    public <T, K> Optional<T> find(Class<T> entityClass, K id) {
        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(id, "id is required");
        EntityMetadata entityMetadata = mappings.get(entityClass);
        FieldMetadata idField = entityMetadata.getId()
                .orElseThrow(() -> IdNotFoundException.newInstance(entityClass));
        Optional<AttributeConverter<K, Object>> converter = idField.getConverter();
        Object value = converter.map(c -> c.convertToDatabaseColumn(id))
                .orElse(Value.of(id).get(idField.getType()));
        ColumnQuery query = ColumnQuery.select().from(entityMetadata.getName())
                .where(idField.getName()).eq(value).build();
        return singleResult(query);
    }

    @Override
    public <T, K> void delete(Class<T> entityClass, K id) {
        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(id, "id is required");
        EntityMetadata entityMetadata = mappings.get(entityClass);
        FieldMetadata idField = entityMetadata.getId()
                .orElseThrow(() -> IdNotFoundException.newInstance(entityClass));
        Optional<AttributeConverter<K, Object>> converter = idField.getConverter();
        Object value = converter.map(c -> c.convertToDatabaseColumn(id))
                .orElse(Value.of(id).get(idField.getType()));
        ColumnDeleteQuery query = ColumnDeleteQuery.delete().from(entityMetadata.getName())
                .where(idField.getName()).eq(value).build();
        delete(query);
    }

    @Override
    public <T> Stream<T> query(String query) {
        requireNonNull(query, "query is required");
        return PARSER.query(query, this.manager, this.observerParser).map(this.converter::toEntity);
    }

    @Override
    public <T> Optional<T> singleResult(String query) {
        requireNonNull(query, "query is required");
        Stream<T> entities = query(query);
        final Iterator<T> iterator = entities.iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        }
        final T entity = iterator.next();
        if (!iterator.hasNext()) {
            return Optional.of(entity);
        }
        throw new NonUniqueResultException("No unique result found to the query: " + query);
    }

    @Override
    public PreparedStatement prepare(String query) {
        return new ColumnPreparedStatement(PARSER.prepare(query, this.manager, this.observerParser),
                this.converter);
    }

    @Override
    public long count(String documentCollection) {
        return this.manager.count(documentCollection);
    }

    @Override
    public <T> long count(Class<T> entityClass) {
        requireNonNull(entityClass, "entityClass is required");
        EntityMetadata entityMetadata = mappings.get(entityClass);
        return this.manager.count(entityMetadata.getName());
    }

    private <T> Stream<T> executeQuery(ColumnQuery query) {
        requireNonNull(query, "query is required");
        Stream<ColumnEntity> entities = this.manager.select(query);
        Function<ColumnEntity, T> function = this.converter::toEntity;
        return entities.map(function);
    }

}

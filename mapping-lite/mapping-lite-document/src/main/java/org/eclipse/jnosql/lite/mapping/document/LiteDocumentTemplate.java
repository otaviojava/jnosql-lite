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
package org.eclipse.jnosql.lite.mapping.document;

import jakarta.data.exceptions.NonUniqueResultException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.nosql.PreparedStatement;
import jakarta.nosql.QueryMapper;
import jakarta.nosql.document.DocumentTemplate;
import org.eclipse.jnosql.communication.Value;
import org.eclipse.jnosql.communication.document.DocumentDeleteQuery;
import org.eclipse.jnosql.communication.document.DocumentEntity;
import org.eclipse.jnosql.communication.document.DocumentManager;
import org.eclipse.jnosql.communication.document.DocumentObserverParser;
import org.eclipse.jnosql.communication.document.DocumentQuery;
import org.eclipse.jnosql.communication.document.DocumentQueryParser;
import org.eclipse.jnosql.lite.mapping.metadata.DefaultEntitiesMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.EntitiesMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.mapping.AttributeConverter;
import org.eclipse.jnosql.mapping.IdNotFoundException;

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
public class LiteDocumentTemplate implements DocumentTemplate {

    private static final DocumentQueryParser PARSER = new DocumentQueryParser();

    private final LiteDocumentEntityConverter converter;

    private final DocumentManager manager;

    private final EntitiesMetadata mappings;

    private final DocumentObserverParser observerParser;

    @Inject
    public LiteDocumentTemplate(DocumentManager manager) {
        this.manager = Objects.requireNonNull(manager, "manager is required");
        this.converter = new LiteDocumentEntityConverter();
        this.mappings = new DefaultEntitiesMetadata();
        this.observerParser = new LiteDocumentMapperObserver(this.mappings);
    }

    @Override
    public <T> T insert(T entity) {
        requireNonNull(entity, "entity is required");
        DocumentEntity documentEntity = this.converter.toDocument(entity);
        return converter.toEntity(entity, manager.insert(documentEntity));
    }

    @Override
    public <T> T insert(T entity, Duration ttl) {
        requireNonNull(entity, "entity is required");
        requireNonNull(ttl, "ttl is required");
        DocumentEntity documentEntity = this.converter.toDocument(entity);
        return converter.toEntity(entity, manager.insert(documentEntity, ttl));
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
        DocumentEntity documentEntity = this.converter.toDocument(entity);
        return converter.toEntity(entity, manager.update(documentEntity));
    }

    @Override
    public <T> Iterable<T> update(Iterable<T> entities) {
        requireNonNull(entities, "entity is required");
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::update).collect(Collectors.toList());
    }

    public void delete(DocumentDeleteQuery query) {
        requireNonNull(query, "query is required");
        this.manager.delete(query);
    }

    public <T> Stream<T> select(DocumentQuery query) {
        Objects.requireNonNull(query, "query is required");
        return executeQuery(query);
    }


    public <T> Optional<T> singleResult(DocumentQuery query) {
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
        DocumentQuery query = DocumentQuery.select().from(entityMetadata.getName())
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
        DocumentDeleteQuery query = DocumentDeleteQuery.delete().from(entityMetadata.getName())
                .where(idField.getName()).eq(value).build();
        delete(query);
    }

    @Override
    public <T> QueryMapper.MapperFrom select(Class<T> type) {
        return null;
    }

    @Override
    public <T> QueryMapper.MapperDeleteFrom delete(Class<T> type) {
        return null;
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

      public PreparedStatement prepare(String query) {
        return new DocumentPreparedStatement(PARSER.prepare(query, this.manager, this.observerParser),
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

    private <T> Stream<T> executeQuery(DocumentQuery query) {
        requireNonNull(query, "query is required");
        Stream<DocumentEntity> entities = this.manager.select(query);
        Function<DocumentEntity, T> function = this.converter::toEntity;
        return entities.map(function);
    }

}

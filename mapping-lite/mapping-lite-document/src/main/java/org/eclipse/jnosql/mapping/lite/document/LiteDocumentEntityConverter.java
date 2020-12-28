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
package org.eclipse.jnosql.mapping.lite.document;

import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import org.eclipse.jnosql.mapping.lite.document.DocumentFieldConverters.DocumentFieldConverterFactory;
import org.eclipse.jnosql.mapping.lite.metadata.ClassMappings;
import org.eclipse.jnosql.artemis.lite.metadata.DefaultClassMappings;
import org.eclipse.jnosql.mapping.lite.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.lite.metadata.FieldMetadata;
import org.eclipse.jnosql.mapping.lite.metadata.FieldType;
import org.eclipse.jnosql.mapping.lite.metadata.FieldTypeUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class LiteDocumentEntityConverter implements DocumentEntityConverter {

    private final ClassMappings mappings;

    private final DocumentFieldConverterFactory converterFactory;

    public LiteDocumentEntityConverter() {
        this.mappings = new DefaultClassMappings();
        this.converterFactory = new DocumentFieldConverterFactory();
    }

    @Override
    public DocumentEntity toDocument(Object entityInstance) {
        requireNonNull(entityInstance, "Object is required");
        EntityMetadata mapping = mappings.get(entityInstance.getClass());
        DocumentEntity entity = DocumentEntity.of(mapping.getName());
        mapping.getFields().stream()
                .map(f -> DocumentFieldMetadata.of(f, entityInstance))
                .filter(DocumentFieldMetadata::isNotEmpty)
                .map(f -> f.toDocument(this, this.mappings))
                .flatMap(List::stream)
                .forEach(entity::add);
        return entity;
    }

    @Override
    public <T> T toEntity(Class<T> entityClass, DocumentEntity entity) {
        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(entity, "entity is required");
        return toEntity(entityClass, entity.getDocuments());
    }

    @Override
    public <T> T toEntity(T entityInstance, DocumentEntity entity) {
        requireNonNull(entityInstance, "entityInstance is required");
        requireNonNull(entity, "entity is required");
        EntityMetadata mapping = mappings.get(entityInstance.getClass());
        return convertEntity(entity.getDocuments(), mapping, entityInstance);
    }

    @Override
    public <T> T toEntity(DocumentEntity entity) {
        requireNonNull(entity, "entity is required");
        EntityMetadata mapping = mappings.findByName(entity.getName());
        T instance = mapping.newInstance();
        return convertEntity(entity.getDocuments(), mapping, instance);
    }

    <T> T toEntity(Class<T> entityClass, List<Document> documents) {
        EntityMetadata mapping = mappings.get(entityClass);
        T instance = mapping.newInstance();
        return convertEntity(documents, mapping, instance);
    }

    private <T> T convertEntity(List<Document> documents, EntityMetadata mapping, T instance) {
        final Map<String, FieldMetadata> fieldsGroupByName = mapping.getFieldsGroupByName();
        final List<String> names = documents.stream().map(Document::getName).sorted().collect(Collectors.toList());
        final Predicate<String> existField = k -> Collections.binarySearch(names, k) >= 0;
        final Predicate<String> isElementType = k -> {
            FieldMetadata fieldMetadata = fieldsGroupByName.get(k);
            FieldType type = FieldTypeUtil.of(fieldMetadata, mappings);
            return FieldType.EMBEDDED.equals(type) || FieldType.SUB_ENTITY.equals(type);
        };

        fieldsGroupByName.keySet().stream()
                .filter(existField.or(isElementType))
                .forEach(feedObject(instance, documents, fieldsGroupByName));

        return instance;
    }

    private <T> Consumer<String> feedObject(T instance, List<Document> documents, Map<String, FieldMetadata> fieldsGroupByName) {
        return k -> {
            Optional<Document> document = documents.stream().filter(c -> c.getName().equals(k)).findFirst();

            FieldMetadata field = fieldsGroupByName.get(k);
            FieldType type = FieldTypeUtil.of(field, mappings);
            DocumentFieldConverter fieldConverter = converterFactory.get(field, type, mappings);
            fieldConverter.convert(instance, documents, document, field, this, mappings);
        };
    }


}

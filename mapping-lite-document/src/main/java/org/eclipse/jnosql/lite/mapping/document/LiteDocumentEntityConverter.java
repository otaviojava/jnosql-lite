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

import jakarta.data.exceptions.MappingException;
import org.eclipse.jnosql.communication.document.Document;
import org.eclipse.jnosql.communication.document.DocumentEntity;
import org.eclipse.jnosql.lite.mapping.document.DocumentFieldConverters.DocumentFieldConverterFactory;
import org.eclipse.jnosql.lite.mapping.metadata.EntitiesMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldType;
import org.eclipse.jnosql.lite.mapping.metadata.FieldTypeUtil;
import org.eclipse.jnosql.lite.mapping.metadata.InheritanceMetadata;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static org.eclipse.jnosql.lite.mapping.metadata.FieldType.SUB_ENTITY;

public class LiteDocumentEntityConverter  {

    private final EntitiesMetadata mappings;

    private final DocumentFieldConverterFactory converterFactory;

    public LiteDocumentEntityConverter() {
        this.mappings = EntitiesMetadata.get();
        this.converterFactory = new DocumentFieldConverterFactory();
    }

    public DocumentEntity toDocument(Object entityInstance) {
        requireNonNull(entityInstance, "Object is required");
        EntityMetadata mapping = mappings.get(entityInstance.getClass());
        DocumentEntity entity = DocumentEntity.of(mapping.name());
        mapping.fields().stream()
                .map(f -> DocumentFieldMetadata.of(f, entityInstance))
                .filter(DocumentFieldMetadata::isNotEmpty)
                .map(f -> f.toDocument(this, this.mappings))
                .flatMap(List::stream)
                .forEach(entity::add);

        mapping.inheritance().ifPresent(i -> entity.add(i.discriminatorColumn(),
                i.discriminatorValue()));

        return entity;
    }

    public <T> T toEntity(Class<T> entityClass, DocumentEntity entity) {
        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(entity, "entity is required");
        return toEntity(entityClass, entity.documents());
    }

    public <T> T toEntity(T entityInstance, DocumentEntity entity) {
        requireNonNull(entityInstance, "entityInstance is required");
        requireNonNull(entity, "entity is required");
        EntityMetadata mapping = mappings.get(entityInstance.getClass());
        return convertEntity(entity.documents(), mapping, entityInstance);
    }

    public <T> T toEntity(DocumentEntity entity) {
        requireNonNull(entity, "entity is required");
        EntityMetadata mapping = mappings.findByName(entity.name());
        if (mapping.isInheritance()) {
            return mapInheritanceEntity(entity, mapping.type());
        }
        T instance = mapping.newInstance();
        return convertEntity(entity.documents(), mapping, instance);
    }

    <T> T toEntity(Class<T> entityClass, List<Document> documents) {
        EntityMetadata mapping = mappings.get(entityClass);
        T instance = mapping.newInstance();
        return convertEntity(documents, mapping, instance);
    }

    private <T> T mapInheritanceEntity(DocumentEntity entity, Class<?> entityClass) {
        Map<String, InheritanceMetadata> group = mappings
                .findByParentGroupByDiscriminatorValue(entityClass);

        if (group.isEmpty()) {
            throw new MappingException("There is no discriminator inheritance to the document collection "
                    + entity.name());
        }
        String column = group.values()
                .stream()
                .findFirst()
                .map(InheritanceMetadata::discriminatorColumn)
                .orElseThrow();

        String discriminator = entity.find(column, String.class)
                .orElseThrow(
                        () -> new MappingException("To inheritance there is the discriminator column missing" +
                                " on the Document Collection, the document name: " + column));

        InheritanceMetadata inheritance = Optional.ofNullable(group.get(discriminator))
                .orElseThrow(() -> new MappingException("There is no inheritance map to the discriminator" +
                        " column value " + discriminator));

        EntityMetadata mapping = mappings.get(inheritance.entity());
        T instance = mapping.newInstance();
        return convertEntity(entity.documents(), mapping, instance);
    }

    private <T> T convertEntity(List<Document> documents, EntityMetadata mapping, T instance) {
        final Map<String, FieldMetadata> fieldsGroupByName = mapping.fieldsGroupByName();
        final List<String> names = documents.stream().map(Document::name).sorted().toList();
        final Predicate<String> existField = k -> Collections.binarySearch(names, k) >= 0;
        final Predicate<String> isElementType = k -> {
            FieldMetadata fieldMetadata = fieldsGroupByName.get(k);
            FieldType type = FieldTypeUtil.of(fieldMetadata, mappings);
            return FieldType.EMBEDDED.equals(type) || SUB_ENTITY.equals(type);
        };

        fieldsGroupByName.keySet().stream()
                .filter(existField.or(isElementType))
                .forEach(feedObject(instance, documents, fieldsGroupByName));

        return instance;
    }

    private <T> Consumer<String> feedObject(T instance, List<Document> documents, Map<String, FieldMetadata> fieldsGroupByName) {
        return k -> {
            Optional<Document> document = documents.stream().filter(c -> c.name().equals(k)).findFirst();

            FieldMetadata field = fieldsGroupByName.get(k);
            FieldType type = FieldTypeUtil.of(field, mappings);
            DocumentFieldConverter fieldConverter = converterFactory.get(field, type, mappings);
            if (SUB_ENTITY.equals(type)) {
                if (document.isPresent()) {
                    fieldConverter.convert(instance, null, document.orElse(null),
                            field, this, mappings);
                }
            } else {
                fieldConverter.convert(instance, documents, document.orElse(null),
                        field, this, mappings);
            }
        };
    }


}

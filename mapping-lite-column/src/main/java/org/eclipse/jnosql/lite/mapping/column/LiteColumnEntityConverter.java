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

import jakarta.data.exceptions.MappingException;
import org.eclipse.jnosql.communication.column.Column;
import org.eclipse.jnosql.communication.column.ColumnEntity;
import org.eclipse.jnosql.lite.mapping.column.ColumnFieldConverters.DocumentFieldConverterFactory;
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
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.eclipse.jnosql.lite.mapping.metadata.FieldType.SUB_ENTITY;

public class LiteColumnEntityConverter  {

    private final EntitiesMetadata mappings;

    private final DocumentFieldConverterFactory converterFactory;

    public LiteColumnEntityConverter() {
        this.mappings = EntitiesMetadata.get();
        this.converterFactory = new DocumentFieldConverterFactory();
    }

    public ColumnEntity toColumn(Object entityInstance) {
        requireNonNull(entityInstance, "Object is required");
        EntityMetadata mapping = mappings.get(entityInstance.getClass());
        ColumnEntity entity = ColumnEntity.of(mapping.name());
        mapping.fields().stream()
                .map(f -> ColumnFieldMetadata.of(f, entityInstance))
                .filter(ColumnFieldMetadata::isNotEmpty)
                .map(f -> f.toColumn(this, this.mappings))
                .flatMap(List::stream)
                .forEach(entity::add);
        mapping.inheritance().ifPresent(i -> entity.add(i.discriminatorColumn(), i.discriminatorValue()));
        return entity;
    }

    public <T> T toEntity(Class<T> entityClass, ColumnEntity entity) {
        requireNonNull(entityClass, "entityClass is required");
        requireNonNull(entity, "entity is required");
        return toEntity(entityClass, entity.columns());
    }

    public <T> T toEntity(T entityInstance, ColumnEntity entity) {
        requireNonNull(entityInstance, "entityInstance is required");
        requireNonNull(entity, "entity is required");
        EntityMetadata mapping = mappings.get(entityInstance.getClass());
        return convertEntity(entity.columns(), mapping, entityInstance);
    }

    public <T> T toEntity(ColumnEntity entity) {
        requireNonNull(entity, "entity is required");
        EntityMetadata mapping = mappings.findByName(entity.name());
        if (mapping.isInheritance()) {
            return mapInheritanceEntity(entity, mapping.type());
        }
        T instance = mapping.newInstance();
        return convertEntity(entity.columns(), mapping, instance);
    }

    <T> T toEntity(Class<T> entityClass, List<Column> documents) {
        EntityMetadata mapping = mappings.get(entityClass);
        T instance = mapping.newInstance();
        return convertEntity(documents, mapping, instance);
    }

    private <T> T mapInheritanceEntity(ColumnEntity entity, Class<?> entityClass) {
        Map<String, InheritanceMetadata> group = mappings.findByParentGroupByDiscriminatorValue(entityClass);

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
        return convertEntity(entity.columns(), mapping, instance);
    }
    private <T> T convertEntity(List<Column> documents, EntityMetadata mapping, T instance) {
        final Map<String, FieldMetadata> fieldsGroupByName = mapping.fieldsGroupByName();
        final List<String> names = documents.stream().map(Column::name).sorted().collect(Collectors.toList());
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

    private <T> Consumer<String> feedObject(T instance, List<Column> columns, Map<String, FieldMetadata> fieldsGroupByName) {
        return k -> {
            Optional<Column> column = columns.stream().filter(c -> c.name().equals(k)).findFirst();

            FieldMetadata field = fieldsGroupByName.get(k);
            FieldType type = FieldTypeUtil.of(field, mappings);
            ColumnFieldConverter fieldConverter = converterFactory.get(field, type, mappings);
            if (SUB_ENTITY.equals(type)) {
                if (column.isPresent()) {
                    fieldConverter.convert(instance, null, column.orElse(null),
                            field, this, mappings);
                }
            } else {
                fieldConverter.convert(instance, columns, column.orElse(null),
                        field, this, mappings);
            }
        };
    }


}

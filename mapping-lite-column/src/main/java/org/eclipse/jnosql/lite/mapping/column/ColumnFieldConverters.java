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
import org.eclipse.jnosql.communication.TypeReference;
import org.eclipse.jnosql.communication.Value;
import org.eclipse.jnosql.communication.column.Column;
import org.eclipse.jnosql.lite.mapping.metadata.EntitiesMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.CollectionSupplier;
import org.eclipse.jnosql.lite.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldType;
import org.eclipse.jnosql.lite.mapping.metadata.FieldTypeUtil;
import org.eclipse.jnosql.mapping.AttributeConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

class ColumnFieldConverters {

    static class DocumentFieldConverterFactory {

        private final EmbeddedFieldConverter embeddedFieldConverter = new EmbeddedFieldConverter();
        private final DefaultConverter defaultConverter = new DefaultConverter();
        private final CollectionEmbeddableConverter embeddableConverter = new CollectionEmbeddableConverter();
        private final SubEntityConverter subEntityConverter = new SubEntityConverter();

        ColumnFieldConverter get(FieldMetadata field, FieldType type, EntitiesMetadata mappings) {
            if (FieldType.EMBEDDED.equals(type)) {
                return embeddedFieldConverter;
            } else if (FieldType.SUB_ENTITY.equals(type)) {
                return subEntityConverter;
            } else if (isCollectionEmbeddable(field, type, mappings)) {
                return embeddableConverter;
            } else {
                return defaultConverter;
            }
        }

        private boolean isCollectionEmbeddable(FieldMetadata field, FieldType type, EntitiesMetadata mappings) {
            return FieldType.COLLECTION.equals(type) &&
                    mappings.findByClass(field.arguments().get(0)).isPresent();
        }
    }

    private static class SubEntityConverter implements ColumnFieldConverter {

        @Override
        public <X, Y, T> void convert(T instance, List<Column> columns, Column column,
                                      FieldMetadata field, LiteColumnEntityConverter converter, EntitiesMetadata mappings) {


            if (Objects.nonNull(column)) {
                Object value = column.get();
                if (value instanceof Map) {
                    Map map = (Map) value;
                    List<Column> embeddedColumn = new ArrayList<>();

                    for (Map.Entry entry : (Set<Map.Entry>) map.entrySet()) {
                        embeddedColumn.add(Column.of(entry.getKey().toString(), entry.getValue()));
                    }
                    field.write(instance, converter.toEntity(field.type(), embeddedColumn));

                } else {
                    field.write(instance, converter.toEntity(field.type(),
                            column.get(new TypeReference<List<Column>>() {})));
                }

            } else {
                field.write(instance, converter.toEntity(field.type(), columns));
            }
        }
    }

    private static class EmbeddedFieldConverter implements ColumnFieldConverter {


        @Override
        public <X, Y, T> void convert(T instance, List<Column> columns, Column column,
                                      FieldMetadata field, LiteColumnEntityConverter converter, EntitiesMetadata mappings) {

            Object subEntity = converter.toEntity(field.type(), columns);
            EntityMetadata mapping = mappings.get(field.type());
            boolean areAllFieldsNull = mapping.getFields()
                    .stream()
                    .map(f -> f.read(subEntity))
                    .allMatch(Objects::isNull);
            if (!areAllFieldsNull) {
                field.write(instance, subEntity);
            }
        }
    }

    private static class DefaultConverter implements ColumnFieldConverter {

        @Override
        public <X, Y, T> void convert(T instance, List<Column> columns, Column column,
                                      FieldMetadata field, LiteColumnEntityConverter converter, EntitiesMetadata mappings) {

            Value value = column.value();
            Optional<AttributeConverter<X, Y>> optionalConverter = field.converter();
            if (optionalConverter.isPresent()) {
                AttributeConverter<X, Y> attributeConverter = optionalConverter.get();
                Object attributeConverted = attributeConverter.convertToEntityAttribute((Y) value.get());
                field.write(instance, Value.of(attributeConverted).get(field.type()));
            } else {
                switch (FieldTypeUtil.of(field, mappings)) {
                    case COLLECTION:
                        field.write(instance, value.get(() -> ColumnLiteParameterizedType.of(field)));
                        return;
                    case MAP:
                        field.write(instance, getMapValue(value).get(() -> ColumnLiteParameterizedType.of(field)));
                        return;
                    default:
                        field.write(instance, value.get(field.type()));
                }

            }
        }

        private Value getMapValue(Value value) {
            Object object = value.get();
            if (object instanceof Iterable) {
                return value;
            } else {
                return Value.of(Collections.singletonList(object));
            }
        }
    }

    private static class CollectionEmbeddableConverter implements ColumnFieldConverter {

        @Override
        public <X, Y, T> void convert(T instance, List<Column> columns, Column column,
                                      FieldMetadata field, LiteColumnEntityConverter converter, EntitiesMetadata mappings) {
            if (Objects.nonNull(column)) {
                CollectionSupplier<?> supplier = CollectionSupplier.find(field.type());
                Collection collection = supplier.get();
                List<List<Column>> embeddable = (List<List<Column>>) column.get();
                for (List<Column> embeddableColumns : embeddable) {
                    List<Class<?>> arguments = field.arguments();
                    Class<?> type = arguments.stream().findFirst()
                            .orElseThrow(() -> new MappingException("There is an issue in the field: "
                                    + field.name() + " in the class " + instance.getClass()));
                    Object element = converter.toEntity(type, embeddableColumns);
                    collection.add(element);
                }
                field.write(instance, collection);
            }
        }
    }


}

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


import jakarta.nosql.TypeReference;
import jakarta.nosql.Value;
import jakarta.nosql.document.Document;
import jakarta.nosql.mapping.AttributeConverter;
import jakarta.nosql.mapping.MappingException;
import org.eclipse.jnosql.lite.mapping.metadata.ClassMappings;
import org.eclipse.jnosql.lite.mapping.metadata.CollectionSupplier;
import org.eclipse.jnosql.lite.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldType;
import org.eclipse.jnosql.lite.mapping.metadata.FieldTypeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

class DocumentFieldConverters {

    static class DocumentFieldConverterFactory {

        private final EmbeddedFieldConverter embeddedFieldConverter = new EmbeddedFieldConverter();
        private final DefaultConverter defaultConverter = new DefaultConverter();
        private final CollectionEmbeddableConverter embeddableConverter = new CollectionEmbeddableConverter();
        private final SubEntityConverter subEntityConverter = new SubEntityConverter();

        DocumentFieldConverter get(FieldMetadata field, FieldType type, ClassMappings mappings) {
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

        private boolean isCollectionEmbeddable(FieldMetadata field, FieldType type, ClassMappings mappings) {
            return FieldType.COLLECTION.equals(type) &&
                    !mappings.findByClass(field.getArguments().get(0)).isEmpty();
        }
    }

    private static class SubEntityConverter implements DocumentFieldConverter {

        @Override
        public <X, Y, T> void convert(T instance, List<Document> documents, Optional<Document> document,
                                      FieldMetadata field, LiteDocumentEntityConverter converter, ClassMappings mappings) {

            if (document.isPresent()) {
                Document sudDocument = document.get();
                Object value = sudDocument.get();
                if (value instanceof Map) {
                    Map map = (Map) value;
                    List<Document> embeddedDocument = new ArrayList<>();

                    for (Map.Entry entry : (Set<Map.Entry>) map.entrySet()) {
                        embeddedDocument.add(Document.of(entry.getKey().toString(), entry.getValue()));
                    }
                    field.write(instance, converter.toEntity(field.getType(), embeddedDocument));

                } else {
                    field.write(instance, converter.toEntity(field.getType(),
                            sudDocument.get(new TypeReference<List<Document>>() {
                            })));
                }

            } else {
                field.write(instance, converter.toEntity(field.getType(), documents));
            }
        }
    }

    private static class EmbeddedFieldConverter implements DocumentFieldConverter {


        @Override
        public <X, Y, T> void convert(T instance, List<Document> documents, Optional<Document> document,
                                      FieldMetadata field, LiteDocumentEntityConverter converter, ClassMappings mappings) {

            Object subEntity = converter.toEntity(field.getType(), documents);
            field.write(instance, subEntity);

        }
    }

    private static class DefaultConverter implements DocumentFieldConverter {

        @Override
        public <X, Y, T> void convert(T instance, List<Document> documents, Optional<Document> document,
                                      FieldMetadata field, LiteDocumentEntityConverter converter, ClassMappings mappings) {
            Value value = document.get().getValue();

            Optional<AttributeConverter<X, Y>> optionalConverter = field.getConverter();
            if (optionalConverter.isPresent()) {
                AttributeConverter<X, Y> attributeConverter = optionalConverter.get();
                Object attributeConverted = attributeConverter.convertToEntityAttribute((Y) value.get());
                field.write(instance, Value.of(attributeConverted).get(field.getType()));
            } else {
                switch (FieldTypeUtil.of(field, mappings)) {
                    case COLLECTION:
                        field.write(instance, value.get(() -> DocumentLiteParameterizedType.of(field)));
                        return;
                    case MAP:
                        field.write(instance, getMapValue(value).get(() -> DocumentLiteParameterizedType.of(field)));
                        return;
                    default:
                        field.write(instance, value.get(field.getType()));
                        return;
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

    private static class CollectionEmbeddableConverter implements DocumentFieldConverter {

        @Override
        public <X, Y, T> void convert(T instance, List<Document> documents, Optional<Document> document,
                                      FieldMetadata field, LiteDocumentEntityConverter converter, ClassMappings mappings) {
            document.ifPresent(convertDocument(instance, field, converter));
        }

        private <T> Consumer<Document> convertDocument(T instance, FieldMetadata field, LiteDocumentEntityConverter converter) {
            return document -> {

                CollectionSupplier<?> supplier = CollectionSupplier.find(field.getType());
                Collection collection = supplier.get();
                List<List<Document>> embeddable = (List<List<Document>>) document.get();
                for (List<Document> documents : embeddable) {
                    List<Class<?>> arguments = field.getArguments();
                    Class<?> type = arguments.stream().findFirst()
                            .orElseThrow(() -> new MappingException("There is an issue in the field: "
                                    + field.getName() + " in the class " + instance.getClass()));
                    Object element = converter.toEntity(type, documents);
                    collection.add(element);
                }
                field.write(instance, collection);
            };
        }
    }


}

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


import jakarta.nosql.document.Document;
import jakarta.nosql.mapping.AttributeConverter;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import org.eclipse.jnosql.lite.mapping.metadata.EntitiesMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldType;
import org.eclipse.jnosql.lite.mapping.metadata.FieldTypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

class DocumentFieldMetadata implements FieldMetadata {

    private final FieldMetadata field;

    private final Object entity;

    private DocumentFieldMetadata(FieldMetadata field, Object entity) {
        this.field = field;
        this.entity = entity;
    }

    @Override
    public boolean isId() {
        return field.isId();
    }

    @Override
    public <X, Y, T extends AttributeConverter<X, Y>> Optional<X> getConverter() {
        return field.getConverter();
    }

    @Override
    public String getFieldName() {
        return field.getFieldName();
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public void write(Object bean, Object value) {
        throw new UnsupportedOperationException("There is not support to write field");
    }

    public void write(Object value) {
        field.write(this.entity, value);
    }

    @Override
    public Object read(Object bean) {
        throw new UnsupportedOperationException("There is not support to write field");
    }

    public Object read() {
        return this.field.read(this.entity);
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public List<Class<?>> getArguments() {
        return field.getArguments();
    }

    public boolean isNotEmpty() {
        return this.read() != null;
    }

    public <X, Y> List<Document> toDocument(DocumentEntityConverter converter, EntitiesMetadata mappings) {
        FieldType fieldType = FieldTypeUtil.of(this, mappings);

        if (FieldType.EMBEDDED.equals(fieldType)) {
            return converter.toDocument(read()).getDocuments();
        } else if (FieldType.SUB_ENTITY.equals(fieldType)) {
            return singletonList(Document.of(getName(), converter.toDocument(this.read()).getDocuments()));
        } else if (isEmbeddableCollection(fieldType, mappings)) {
            return singletonList(Document.of(getName(), getDocuments(converter)));
        }
        Optional<AttributeConverter<X, Y>> optionalConverter = this.getConverter();
        if (optionalConverter.isPresent()) {
            AttributeConverter<X, Y> attributeConverter = optionalConverter.get();
            return singletonList(Document.of(getName(), attributeConverter.convertToDatabaseColumn((X) this.read())));
        }
        return singletonList(Document.of(getName(), this.read()));
    }

    private boolean isEmbeddableCollection(FieldType fieldType, EntitiesMetadata mappings) {
        return FieldType.COLLECTION.equals(fieldType) && isEmbeddableElement(mappings);
    }

    private boolean isEmbeddableElement(EntitiesMetadata mappings) {
        List<Class<?>> arguments = getArguments();
        if (!arguments.isEmpty()) {
            Class<?> entity = arguments.stream().findFirst().get();
            return mappings.findByClass(entity).isPresent();
        }
        return false;
    }

    private List<List<Document>> getDocuments(DocumentEntityConverter converter) {
        List<List<Document>> documents = new ArrayList<>();
        for (Object element : (Iterable) this.read()) {
            documents.add(converter.toDocument(element).getDocuments());
        }
        return documents;
    }

    @Override
    public String toString() {
        return "DocumentFieldMetadata{" +
                "field=" + field +
                ", entity=" + entity +
                '}';
    }

    static DocumentFieldMetadata of(FieldMetadata field, Object entity) {
        return new DocumentFieldMetadata(field, entity);
    }


}
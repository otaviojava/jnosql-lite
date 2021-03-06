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


import jakarta.nosql.column.Column;
import jakarta.nosql.mapping.AttributeConverter;
import jakarta.nosql.mapping.column.ColumnEntityConverter;
import org.eclipse.jnosql.lite.mapping.metadata.ClassMappings;
import org.eclipse.jnosql.lite.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldType;
import org.eclipse.jnosql.lite.mapping.metadata.FieldTypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

class ColumnFieldMetadata implements FieldMetadata {

    private final FieldMetadata field;

    private final Object entity;

    private ColumnFieldMetadata(FieldMetadata field, Object entity) {
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

    public <X, Y> List<Column> toColumn(ColumnEntityConverter converter, ClassMappings mappings) {
        FieldType fieldType = FieldTypeUtil.of(this, mappings);

        if (FieldType.EMBEDDED.equals(fieldType)) {
            return converter.toColumn(read()).getColumns();
        } else if (FieldType.SUB_ENTITY.equals(fieldType)) {
            return singletonList(Column.of(getName(), converter.toColumn(this.read()).getColumns()));
        } else if (isEmbeddableCollection(fieldType, mappings)) {
            return singletonList(Column.of(getName(), getColumns(converter)));
        }
        Optional<AttributeConverter<X, Y>> optionalConverter = this.getConverter();
        if (optionalConverter.isPresent()) {
            AttributeConverter<X, Y> attributeConverter = optionalConverter.get();
            return singletonList(Column.of(getName(), attributeConverter.convertToDatabaseColumn((X) this.read())));
        }
        return singletonList(Column.of(getName(), this.read()));
    }

    private boolean isEmbeddableCollection(FieldType fieldType, ClassMappings mappings) {
        return FieldType.COLLECTION.equals(fieldType) && isEmbeddableElement(mappings);
    }

    private boolean isEmbeddableElement(ClassMappings mappings) {
        List<Class<?>> arguments = getArguments();
        if (!arguments.isEmpty()) {
            Class<?> entity = arguments.stream().findFirst().get();
            return mappings.findByClass(entity).isPresent();
        }
        return false;
    }

    private List<List<Column>> getColumns(ColumnEntityConverter converter) {
        List<List<Column>> columns = new ArrayList<>();
        for (Object element : (Iterable) this.read()) {
            columns.add(converter.toColumn(element).getColumns());
        }
        return columns;
    }

    @Override
    public String toString() {
        return "ColumnFieldMetadata{" +
                "field=" + field +
                ", entity=" + entity +
                '}';
    }

    static ColumnFieldMetadata of(FieldMetadata field, Object entity) {
        return new ColumnFieldMetadata(field, entity);
    }


}
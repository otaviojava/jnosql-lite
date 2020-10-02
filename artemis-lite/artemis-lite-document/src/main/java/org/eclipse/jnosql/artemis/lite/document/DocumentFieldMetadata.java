
/*
 *  Copyright (c) 2017 Otávio Santana and others
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
package org.eclipse.jnosql.artemis.lite.document;


import jakarta.nosql.mapping.AttributeConverter;
import org.eclipse.jnosql.artemis.lite.metadata.FieldMetadata;

import java.util.Optional;
import java.util.Set;

class DocumentFieldMetadata implements FieldMetadata {

    private final FieldMetadata field;

    private final Object entity;

    DocumentFieldMetadata(FieldMetadata field, Object entity) {
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
    public Set<Class<?>> getArguments() {
        return field.getArguments();
    }
}
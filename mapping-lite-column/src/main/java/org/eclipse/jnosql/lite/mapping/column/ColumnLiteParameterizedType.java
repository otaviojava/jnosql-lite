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

import org.eclipse.jnosql.lite.mapping.metadata.FieldMetadata;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

final class ColumnLiteParameterizedType implements ParameterizedType {

    private final FieldMetadata field;

    private ColumnLiteParameterizedType(FieldMetadata field) {
        this.field = field;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return field.getArguments().toArray(Type[]::new);
    }

    @Override
    public Type getRawType() {
        return field.getType();
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    static ParameterizedType of(FieldMetadata fieldMetadata) {
        return new ColumnLiteParameterizedType(fieldMetadata);
    }
}

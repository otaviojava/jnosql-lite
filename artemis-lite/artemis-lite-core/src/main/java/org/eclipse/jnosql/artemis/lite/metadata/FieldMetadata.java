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
package org.eclipse.jnosql.artemis.lite.metadata;


import jakarta.nosql.mapping.AttributeConverter;

import java.util.Optional;
import java.util.Set;

/**
 * This class represents the information from
 * {@link jakarta.nosql.mapping.Column} or {@link jakarta.nosql.mapping.Id}.
 * The strategy is do cache in all fields in a class to either read and writer faster from Field
 */
public interface FieldMetadata {

    /**
     * Returns true is the field is annotated with {@link jakarta.nosql.mapping.Id}
     *
     * @return true is annotated with {@link jakarta.nosql.mapping.Id}
     */
    boolean isId();

    /**
     * Returns the converter class
     * @param <X> the type of the entity attribute
     * @param <Y> the type of the database column
     * @param <T> the Converter
     * @return the converter if present
     */
    <X, Y, T extends AttributeConverter<X, Y>> Optional<Class<? extends AttributeConverter<X, Y>>> getConverter();
    /**
     * Returns the Java Fields name.
     * {@link java.lang.reflect.Field#getName()}
     *
     * @return The Java Field name {@link java.lang.reflect.Field#getName()}
     */
    String getFieldName();

    /**
     * Returns the name of the field that can be either the field name
     * or {@link jakarta.nosql.mapping.Column#value()}
     *
     * @return the name
     */
    String getName();

    /**
     * Writes the bean from the value
     *
     * @param bean  the entity
     * @param value the value
     */
    void write(Object bean, Object value);

    /**
     * Reads the value from the entity bean
     *
     * @param bean the bean
     * @return the bean getter
     */
    Object read(Object bean);

    /**
     * @return returns the Field type
     */
    Class<?> getType();

    /**
     * Returns the parameters in the field.
     * @return the field arguments
     */
    Set<Class<?>> getArguments();
}

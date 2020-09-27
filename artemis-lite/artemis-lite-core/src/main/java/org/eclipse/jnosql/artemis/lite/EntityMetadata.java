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
package org.eclipse.jnosql.artemis.lite;



import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This instance is the meta-info of a loaded class that used to be annotated with {@link jakarta.nosql.mapping.Entity}.
 */
public interface EntityMetadata {

    /**
     * @return the {@link jakarta.nosql.mapping.Entity#value()}  value
     */
    String getName();

    /**
     * @return the {@link Class#getSimpleName()}
     */
    String getSimpleName();

    /**
     * @return the {@link Class#getName()} ()}
     */
    String getClassName();

    List<String> getFieldsName();
    /**
     * @return The class
     */
    Class<?> getClassInstance();

    List<FieldMetadata> getFields();

    <T> T newInstance();

    String getColumnField(String javaField);

    Optional<FieldMetadata> getFieldMapping(String javaField);

    Map<String, FieldMetadata> getFieldsGroupByName();

    Optional<FieldMetadata> getId();
}

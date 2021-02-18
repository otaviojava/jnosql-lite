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
package org.eclipse.jnosql.lite.mapping.metadata;




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

    /**
     * @return the fields name
     */
    List<String> getFieldsName();

    /**
     * @return The class
     */
    Class<?> getClassInstance();

    /**
     * @return The fields from this class
     */
    List<FieldMetadata> getFields();

    /**
     * Creates a new instance
     * @param <T> the instance type
     * @return a new instance of this class
     */
    <T> T newInstance();

    /**
     * Gets the native column name from the Java field name
     *
     * @param javaField the java field
     * @return the column name or column
     * @throws NullPointerException when javaField is null
     */
    String getColumnField(String javaField);

    /**
     * Gets the {@link FieldMetadata} from the java field name
     * @param javaField the java field
     * @return the field otherwise {@link Optional#empty()}
     * @throws NullPointerException when the javaField is null
     */
    Optional<FieldMetadata> getFieldMapping(String javaField);

    /**
     * Returns a Fields grouped by the name
     *
     * @return a {@link FieldMetadata} grouped by
     * {@link FieldMetadata#getName()}
     * @see FieldMetadata#getName()
     */
    Map<String, FieldMetadata> getFieldsGroupByName();

    /**
     * Returns the field that has {@link jakarta.nosql.mapping.Id} annotation
     * @return the field with ID annotation
     */
    Optional<FieldMetadata> getId();

    /**
     * Returns true if the class has the {@link jakarta.nosql.mapping.Embeddable} annotation
     * @return true if has {@link jakarta.nosql.mapping.Embeddable} annotation
     */
    boolean isEmbedded();
}

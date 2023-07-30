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
 * This instance is the meta-info of a loaded class that has the  {@link jakarta.nosql.Entity} annotation.
 */
public interface EntityMetadata {

    /**
     * @return the {@link jakarta.nosql.Entity#value()}  value
     */
    String name();

    /**
     * @return the {@link Class#getSimpleName()}
     */
    String simpleName();

    /**
     * @return the {@link Class#getName()} ()}
     */
    String className();

    /**
     * @return the fields name
     */
    List<String> fieldsName();

    /**
     * @return The class
     */
    Class<?> type();

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
    String columnField(String javaField);

    /**
     * Gets the {@link FieldMetadata} from the java field name
     * @param javaField the java field
     * @return the field otherwise {@link Optional#empty()}
     * @throws NullPointerException when the javaField is null
     */
    Optional<FieldMetadata> fieldMapping(String javaField);

    /**
     * Returns a Fields grouped by the name
     *
     * @return a {@link FieldMetadata} grouped by
     * {@link FieldMetadata#name()}
     * @see FieldMetadata#name()
     */
    Map<String, FieldMetadata> fieldsGroupByName();

    /**
     * Returns the field that has {@link jakarta.nosql.Id} annotation
     * @return the field with ID annotation
     */
    Optional<FieldMetadata> id();

    /**
     * Returns true if the class has the {@link org.eclipse.jnosql.mapping.Embeddable} annotation
     * @return true if it has {@link org.eclipse.jnosql.mapping.Embeddable} annotation
     */
    boolean isEmbedded();

    /**
     * Return the parent class of this class mapping.
     * It will check the parent class has the {@link org.eclipse.jnosql.mapping.Inheritance} annotation.
     *
     * @return the parent annotation otherwise {@link  Optional#empty()}
     */
    Optional<InheritanceMetadata> inheritance();

    /**
     * A class that has a parent with {@link org.eclipse.jnosql.mapping.Inheritance} annotation
     * won't use the name. It will use the parent name instead.
     *
     * @return true if it has not parent class with {@link org.eclipse.jnosql.mapping.Inheritance}
     */
    boolean hasEntityName();

    /**
     * @return true if the entity class has the {@link org.eclipse.jnosql.mapping.Inheritance} annotation
     */
    boolean isInheritance();

}

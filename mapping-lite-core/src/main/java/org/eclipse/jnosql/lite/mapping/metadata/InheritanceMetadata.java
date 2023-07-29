/*
 *  Copyright (c) 2022 Otávio Santana and others
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

import java.util.Objects;

/**
 * The mapping information about {@link org.eclipse.jnosql.mapping.Inheritance}
 */
public final class InheritanceMetadata {

    private final String discriminatorValue;

    private final String discriminatorColumn;

    private final Class<?> parent;

    private final Class<?> entity;

    public InheritanceMetadata(String discriminatorValue, String discriminatorColumn, Class<?> parent
            , Class<?> entity) {
        this.discriminatorValue = discriminatorValue;
        this.discriminatorColumn = discriminatorColumn;
        this.parent = parent;
        this.entity = entity;
    }

    /**
     * Return the information from the class the annotation {@link org.eclipse.jnosql.mapping.DiscriminatorValue}
     * or the {@link Class#getSimpleName()}.
     *
     * @return the {@link org.eclipse.jnosql.mapping.DiscriminatorValue} from entity
     */
    public String discriminatorValue() {
        return discriminatorValue;
    }

    /**
     * Return the information parent from the annotation {@link org.eclipse.jnosql.mapping.DiscriminatorValue}
     * or the "type".
     *
     * @return the {@link org.eclipse.jnosql.mapping.DiscriminatorValue} from entity
     */
    public String discriminatorColumn() {
        return discriminatorColumn;
    }

    /**
     * @return The parent class
     */
    public Class<?> parent() {
        return parent;
    }

    /**
     * @return the entity class
     */
    public Class<?> entity() {
        return entity;
    }

    /**
     * Checks if the parent is equals to the parameter
     *
     * @param parent the parameter
     * @return if the parent is equals or not
     * @throws NullPointerException if the parent is null
     */
    public boolean isParent(Class<?> parent) {
        Objects.requireNonNull(parent, "parent is required");
        return this.parent.equals(parent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InheritanceMetadata that = (InheritanceMetadata) o;
        return Objects.equals(discriminatorValue, that.discriminatorValue)
                && Objects.equals(discriminatorColumn, that.discriminatorColumn)
                && Objects.equals(parent, that.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discriminatorValue, discriminatorColumn, parent);
    }

    @Override
    public String toString() {
        return "InheritanceMetadata{" +
                "discriminatorValue='" + discriminatorValue + '\'' +
                ", discriminatorColumn='" + discriminatorColumn + '\'' +
                ", parent=" + parent +
                '}';
    }
}

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

import jakarta.nosql.mapping.AttributeConverter;
import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Convert;
import jakarta.nosql.mapping.Entity;

@Entity("table")
public class Person9 {

    @Column
    private String name;

    @Convert(StringIntegerConverter.class)
    private Integer age;

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                '}';
    }

    public static class StringIntegerConverter implements AttributeConverter<Integer, String> {

        @Override
        public String convertToDatabaseColumn(Integer attribute) {
            return null;
        }

        @Override
        public Integer convertToEntityAttribute(String dbData) {
            return null;
        }
    }

}

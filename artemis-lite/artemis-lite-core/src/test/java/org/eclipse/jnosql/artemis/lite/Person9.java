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

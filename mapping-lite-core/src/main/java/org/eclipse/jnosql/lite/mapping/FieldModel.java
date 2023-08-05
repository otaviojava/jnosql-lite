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
package org.eclipse.jnosql.lite.mapping;

import org.eclipse.jnosql.mapping.Convert;

import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FieldModel extends BaseMappingModel {

    private final String packageName;
    private final String name;
    private final String type;
    private final String entity;
    private final String reader;
    private final String writer;
    private final String fieldName;
    private final boolean id;
    private final List<String> arguments;
    private final String converter;

    private final String mappingType;

    private final String typeConverter;

    private final List<ValueAnnotationModel> valueByAnnotation;

    FieldModel(String packageName, String name,
               String type, String entity,
               String reader, String writer, String fieldName,
               boolean id,
               List<String> arguments,
               String converter, String mappingType,
               String typeConverter,
               List<ValueAnnotationModel> valueByAnnotation) {
        this.packageName = packageName;
        this.name = name;
        this.type = type;
        this.entity = entity;
        this.reader = reader;
        this.writer = writer;
        this.fieldName = fieldName;
        this.id = id;
        this.arguments = arguments;
        this.converter = converter;
        this.mappingType = mappingType;
        this.typeConverter = typeConverter;
        this.valueByAnnotation = valueByAnnotation;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getEntity() {
        return entity;
    }

    public String getReader() {
        return reader;
    }

    public String getWriter() {
        return writer;
    }

    public String getQualified() {
        return packageName + "." + getClassName();
    }

    public String getClassName() {
        return entity + ProcessorUtil.capitalize(fieldName) + "FieldMetaData";
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isId() {
        return id;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getConverter() {
        return converter;
    }

    @Override
    public String toString() {
        return "FieldModel{" +
                "packageName='" + packageName + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", entity='" + entity + '\'' +
                ", reader='" + reader + '\'' +
                ", writer='" + writer + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", id=" + id +
                ", arguments=" + arguments +
                ", converter='" + converter + '\'' +
                '}';
    }

    public static FieldMetaDataBuilder builder() {
        return new FieldMetaDataBuilder();
    }


    public static class FieldMetaDataBuilder {
        private String packageName;
        private String name;
        private String type;
        private String entity;
        private String reader;
        private String writer;
        private String fieldName;
        private boolean id;
        private List<String> arguments;
        private boolean embedded;
        private String converter = "null";

        private String mappingType= "null";

        private String typeConverter=  "null";
        private List<ValueAnnotationModel> valueByAnnotation;


        private FieldMetaDataBuilder() {
        }

        public FieldMetaDataBuilder withPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public FieldMetaDataBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public FieldMetaDataBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public FieldMetaDataBuilder withEntity(String entity) {
            this.entity = entity;
            return this;
        }

        public FieldMetaDataBuilder withReader(String getName) {
            this.reader = getName;
            return this;
        }

        public FieldMetaDataBuilder withWriter(String setName) {
            this.writer = setName;
            return this;
        }

        public FieldMetaDataBuilder withFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public FieldMetaDataBuilder withId(boolean id) {
            this.id = id;
            return this;
        }

        public FieldMetaDataBuilder withArguments(List<String> arguments) {
            this.arguments = arguments;
            return this;
        }


        public FieldMetaDataBuilder withConverter(Convert converter) {
            if (Objects.nonNull(converter)) {
                try {
                    this.converter = String.format("new %s();", converter.value().getName());
                } catch (MirroredTypeException exception) {
                    TypeMirror typeMirror = exception.getTypeMirror();
                    this.converter = String.format("new %s()", typeMirror);
                }

            }
            return this;
        }

        public FieldMetaDataBuilder withMappingType(String mappingType) {
            this.mappingType = mappingType;
            return this;
        }

        public FieldMetaDataBuilder withTypeConverter(String typeConverter) {
            this.typeConverter = typeConverter;
            return this;
        }

        public FieldMetaDataBuilder withValueByAnnotation(List<ValueAnnotationModel> valueByAnnotation) {
            this.valueByAnnotation = valueByAnnotation;
            return this;
        }

        public FieldModel build() {
            return new FieldModel(packageName, name, type, entity, reader, writer, fieldName,
                    id, arguments, converter, mappingType, typeConverter, valueByAnnotation);
        }
    }
}

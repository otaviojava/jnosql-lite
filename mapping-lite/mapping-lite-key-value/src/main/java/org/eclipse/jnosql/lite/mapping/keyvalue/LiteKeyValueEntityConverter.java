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
package org.eclipse.jnosql.lite.mapping.keyvalue;

import jakarta.nosql.Value;
import jakarta.nosql.keyvalue.KeyValueEntity;
import jakarta.nosql.mapping.AttributeConverter;
import jakarta.nosql.mapping.IdNotFoundException;
import jakarta.nosql.mapping.MappingException;
import jakarta.nosql.mapping.keyvalue.KeyValueEntityConverter;
import org.eclipse.jnosql.lite.mapping.metadata.ClassMappings;
import org.eclipse.jnosql.lite.mapping.metadata.DefaultClassMappings;
import org.eclipse.jnosql.lite.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldMetadata;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class LiteKeyValueEntityConverter implements KeyValueEntityConverter {

    private final ClassMappings mappings;


    public LiteKeyValueEntityConverter() {
        this.mappings = new DefaultClassMappings();
    }


    @Override
    public KeyValueEntity toKeyValue(Object entity) {
        requireNonNull(entity, "entity is required");
        EntityMetadata metadata = this.mappings.get(entity.getClass());
        FieldMetadata id = metadata.getId().orElseThrow(() -> new MappingException("The @Id annotations is required in the class: "
                + entity.getClass()));
        Object key = id.read(entity);
        requireNonNull(key, String.format("The key field %s is required", id.getName()));
        return KeyValueEntity.of(getKey(key, entity.getClass(), false), entity);
    }

    @Override
    public <T> T toEntity(Class<T> type, KeyValueEntity entity) {
        requireNonNull(type, "entity is required");
        requireNonNull(entity, "entity is required");
        T bean = entity.getValue(type);
        if (Objects.isNull(bean)) {
            return null;
        }

        Object key = getKey(entity.getKey(), type, true);
        FieldMetadata id = getId(type);
        id.write(bean, key);
        return bean;
    }

    private <T> Object getKey(Object key, Class<T> entityClass, boolean toEntity) {
        FieldMetadata id = getId(entityClass);
        if (id.getConverter().isPresent()) {
            AttributeConverter<Object, Object> attributeConverter = (AttributeConverter<Object, Object>)
                    id.getConverter().get();
            if (toEntity) {
                return attributeConverter.convertToEntityAttribute(key);
            } else {
                return attributeConverter.convertToDatabaseColumn(key);
            }
        } else {
            return Value.of(key).get(id.getType());
        }
    }

    private FieldMetadata getId(Class<?> type) {
        EntityMetadata metadata = this.mappings.get(type);
        return metadata.getId().orElseThrow(() -> IdNotFoundException.newInstance(type));
    }
}

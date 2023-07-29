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

import jakarta.data.exceptions.MappingException;
import org.eclipse.jnosql.communication.Value;
import org.eclipse.jnosql.communication.keyvalue.KeyValueEntity;
import org.eclipse.jnosql.lite.mapping.metadata.DefaultEntitiesMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.EntitiesMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.mapping.AttributeConverter;
import org.eclipse.jnosql.mapping.IdNotFoundException;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class LiteKeyValueEntityConverter  {

    private final EntitiesMetadata mappings;


    public LiteKeyValueEntityConverter() {
        this.mappings = new DefaultEntitiesMetadata();
    }

    public KeyValueEntity toKeyValue(Object entity) {
        requireNonNull(entity, "entity is required");
        EntityMetadata metadata = this.mappings.get(entity.getClass());
        FieldMetadata id = metadata.id()
                .orElseThrow(() -> new MappingException("The @Id annotations is required in the class: "
                + entity.getClass()));
        Object key = id.read(entity);
        requireNonNull(key, String.format("The key field %s is required", id.name()));
        return KeyValueEntity.of(getKey(key, entity.getClass(), false), entity);
    }

    public <T> T toEntity(Class<T> type, KeyValueEntity entity) {
        requireNonNull(type, "entity is required");
        requireNonNull(entity, "entity is required");
        T bean = entity.value(type);
        if (Objects.isNull(bean)) {
            return null;
        }

        Object key = getKey(entity.key(), type, true);
        FieldMetadata id = getId(type);
        id.write(bean, key);
        return bean;
    }

    private <T> Object getKey(Object key, Class<T> entityClass, boolean toEntity) {
        FieldMetadata id = getId(entityClass);
        if (id.converter().isPresent()) {
            AttributeConverter<Object, Object> attributeConverter = (AttributeConverter<Object, Object>)
                    id.converter().get();
            if (toEntity) {
                return attributeConverter.convertToEntityAttribute(key);
            } else {
                return attributeConverter.convertToDatabaseColumn(key);
            }
        } else {
            return Value.of(key).get(id.type());
        }
    }

    private FieldMetadata getId(Class<?> type) {
        EntityMetadata metadata = this.mappings.get(type);
        return metadata.id().orElseThrow(() -> IdNotFoundException.newInstance(type));
    }
}

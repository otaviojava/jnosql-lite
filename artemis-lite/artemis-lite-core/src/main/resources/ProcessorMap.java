package org.soujava.metadata.processor;

import org.eclipse.jnosql.artemis.lite.metadata.ClassMappings;
import org.eclipse.jnosql.artemis.lite.metadata.EntityMetadata;
import org.eclipse.jnosql.artemis.lite.metadata.FieldMetadata;
import org.eclipse.jnosql.artemis.lite.ValidationException;
import org.soujava.medatadata.api.Mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.processing.Generated;

@Generated("Soujava Mapper Generator")
public class ProcessorMap implements Mapper {

    private final ClassMappings mappings;

    public ProcessorMap() {
        this.mappings = new DefaultClassMappings();
    }

    @Override
    public <T> T toEntity(Map<String, Object> map, Class<T> type) {
        Objects.requireNonNull(map, "map is required");
        Objects.requireNonNull(type, "type is required");
        final EntityMetadata entityMetadata = Optional.ofNullable(mappings.get(type))
                .orElseThrow(() -> new ValidationException("The entity has not Entity annotation"));
        final Map<String, FieldMetadata> groupByName = entityMetadata.getFieldsGroupByName();
        T instance = entityMetadata.newInstance();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            final FieldMetadata fieldMetadata = groupByName.get(entry.getKey());
            if (fieldMetadata != null) {
                fieldMetadata.write(instance, entry.getValue());
            }
        }
        return instance;
    }

    @Override
    public <T> Map<String, Object> toMap(T entity) {
        Objects.requireNonNull(entity, "entity is required");
        Map<String, Object> map = new HashMap<>();
        final EntityMetadata entityMetadata = Optional.ofNullable(mappings.get(entity.getClass()))
                .orElseThrow(() -> new ValidationException("The entity has not Entity annotation"));
        map.put("entity", entityMetadata.getName());
        for (FieldMetadata field : entityMetadata.getFields()) {
            final Object value = field.read(entity);
            if (value != null) {
                map.put(field.getName(), value);
            }
        }
        return map;
    }
}

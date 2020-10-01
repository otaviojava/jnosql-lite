package org.eclipse.jnosql.artemis.lite;

import jakarta.nosql.mapping.AttributeConverter;
import org.eclipse.jnosql.artemis.lite.metadata.FieldMetadata;

import java.util.Optional;
import java.util.Set;

public class Atemp implements FieldMetadata {

    private
    @Override
    public boolean isId() {
        return false;
    }

    @Override
    public <X, Y, T extends AttributeConverter<X, Y>> Optional<X> getConverter() {
        return Optional.empty();
    }


    @Override
    public String getFieldName() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void write(Object bean, Object value) {

    }

    @Override
    public Object read(Object bean) {
        return null;
    }

    @Override
    public Class<?> getType() {
        return null;
    }

    @Override
    public Set<Class<?>> getArguments() {
        return null;
    }
}

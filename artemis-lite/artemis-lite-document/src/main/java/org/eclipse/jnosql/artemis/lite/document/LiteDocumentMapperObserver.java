package org.eclipse.jnosql.artemis.lite.document;

import jakarta.nosql.document.DocumentObserverParser;
import org.eclipse.jnosql.artemis.lite.metadata.ClassMappings;
import org.eclipse.jnosql.artemis.lite.metadata.EntityMetadata;

import java.util.Optional;

final class LiteDocumentMapperObserver implements DocumentObserverParser {

    private final ClassMappings mappings;

    LiteDocumentMapperObserver(ClassMappings mappings) {
        this.mappings = mappings;
    }

    @Override
    public String fireEntity(String entity) {
        Optional<EntityMetadata> mapping = getClassMapping(entity);
        return mapping.map(EntityMetadata::getName).orElse(entity);
    }

    @Override
    public String fireField(String entity, String field) {
        Optional<EntityMetadata> mapping = getClassMapping(entity);
        return mapping.map(c -> c.getColumnField(field)).orElse(field);
    }

    private Optional<EntityMetadata> getClassMapping(String entity) {
        Optional<EntityMetadata> bySimpleName = mappings.findBySimpleName(entity);
        if (bySimpleName.isPresent()) {
            return bySimpleName;
        }
        return mappings.findByClassName(entity);
    }

}

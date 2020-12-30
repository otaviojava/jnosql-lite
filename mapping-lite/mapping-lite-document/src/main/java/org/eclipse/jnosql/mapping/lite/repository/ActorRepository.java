package org.eclipse.jnosql.mapping.lite.repository;

import jakarta.nosql.mapping.Repository;
import jakarta.nosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.lite.entities.Actor;
import org.eclipse.jnosql.mapping.lite.metadata.ClassMappings;
import org.eclipse.jnosql.mapping.lite.metadata.DefaultClassMappings;
import org.eclipse.jnosql.mapping.lite.metadata.EntityMetadata;

public class ActorRepository extends AbstractLiteDocumentRepository<Actor, Long>
        implements Repository<Actor, Long> {

    private final DocumentTemplate template;

    private final EntityMetadata metadata;

    public ActorRepository(DocumentTemplate template) {
        this.template = template;
        ClassMappings mappings = new DefaultClassMappings();
        this.metadata = mappings.get(Actor.class);
    }

    @Override
    protected DocumentTemplate getTemplate() {
        return template;
    }

    @Override
    protected EntityMetadata getClassMapping() {
        return null;
    }
}

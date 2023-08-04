package org.eclipse.jnosql.lite.mapping;


import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.jnosql.mapping.metadata.ClassInformationNotFoundException;
import org.eclipse.jnosql.mapping.metadata.EntitiesMetadata;
import org.eclipse.jnosql.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.metadata.InheritanceMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.processing.Generated;

@Generated(value= "JNoSQL Lite EntitiesMetadata Generator", date = "2023-08-04T06:34:27.488447")
@ApplicationScoped
public final class DefaultEntitiesMetadata implements EntitiesMetadata {

    private static final Predicate<EntityMetadata> HAS_NAME = EntityMetadata::hasEntityName;

    private final List<EntityMetadata> entities;
    private final Map<String, EntityMetadata> findByClassName;
    private final Map<String, EntityMetadata> findBySimpleName;

    private final Map<Class<?>, EntityMetadata> classes;

    private final Map<String, EntityMetadata> mappings;


    public DefaultEntitiesMetadata() {
        this.findByClassName = new HashMap<>();
        this.findBySimpleName = new HashMap<>();
        this.classes = new HashMap<>();
        this.mappings = new HashMap<>();
        this.entities = new ArrayList<>();

        for (EntityMetadata entity : entities) {
            this.mappings.put(entity.name().toUpperCase(Locale.US), entity);
            this.classes.put(entity.getClass(), entity);
            this.findBySimpleName.put(entity., entity);
        }
    }


    @Override
    public EntityMetadata get(Class<?> entity) {
        Objects.requireNonNull(entity, "entity is required");
        EntityMetadata metadata = classes.get(entity);
        if (metadata == null) {
            throw new ClassInformationNotFoundException("The entity " + entity + " was not found");
        }
        return metadata;
    }

    @Override
    public Map<String, InheritanceMetadata> findByParentGroupByDiscriminatorValue(Class<?> parent) {
        Objects.requireNonNull(parent, "parent is required");
        return this.entities.stream()
                .flatMap(c -> c.inheritance().stream())
                .filter(p -> p.isParent(parent))
                .collect(Collectors.toMap(InheritanceMetadata::discriminatorValue, Function.identity()));
    }

    @Override
    public EntityMetadata findByName(String name) {
        Objects.requireNonNull(name, "name is required");
        return Optional.ofNullable(mappings.get(name.toUpperCase(Locale.US)))
                .orElseThrow(() -> new ClassInformationNotFoundException("There is not entity found with the name: " + name));

    }

    @Override
    public Optional<EntityMetadata> findBySimpleName(String name) {
        Objects.requireNonNull(name, "name is required");
        return Optional.ofNullable(findBySimpleName.get(name));
    }

    @Override
    public Optional<EntityMetadata> findByClassName(String name) {
        Objects.requireNonNull(name, "name is required");
        return Optional.ofNullable(findByClassName.get(name));
    }

    @Override
    public String toString() {
        return "DefaultEntitiesMetadata{" +
                "entities=" + entities.size() +
                ", findByClassName=" + findByClassName.size() +
                ", findBySimpleName=" + findBySimpleName.size() +
                ", classes=" + classes.size() +
                ", mappings=" + mappings.size() +
                '}';
    }
}

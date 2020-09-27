package org.eclipse.jnosql.artemis.lite;

import java.util.List;

public class ClassMappingsModel extends BaseMappingModel {

    private final List<String> entities;

    public ClassMappingsModel(List<String> entities) {
        this.entities = entities;
    }

    public List<String> getEntities() {
        return entities;
    }

    public String getQualified() {
        return "org.soujava.metadata.processor.DefaultClassMappings";
    }
}

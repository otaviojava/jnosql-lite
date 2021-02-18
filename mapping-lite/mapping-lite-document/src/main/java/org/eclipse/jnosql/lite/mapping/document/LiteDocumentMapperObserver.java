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
package org.eclipse.jnosql.lite.mapping.document;

import jakarta.nosql.document.DocumentObserverParser;
import org.eclipse.jnosql.lite.mapping.metadata.ClassMappings;
import org.eclipse.jnosql.lite.mapping.metadata.EntityMetadata;

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

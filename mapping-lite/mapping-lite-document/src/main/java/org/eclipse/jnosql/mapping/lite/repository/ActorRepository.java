/*
 *  Copyright (c) 2021 Otávio Santana and others
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
package org.eclipse.jnosql.mapping.lite.repository;

import jakarta.nosql.mapping.Repository;
import jakarta.nosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.lite.document.AbstractLiteDocumentRepository;
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

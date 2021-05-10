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
package org.eclipse.jnosql.lite.mapping.keyvalue;

import jakarta.nosql.column.ColumnObserverParser;
import org.eclipse.jnosql.lite.mapping.metadata.EntityMetadata;

import java.util.Optional;

public class RepositoryColumnObserverParser implements ColumnObserverParser {

    private final EntityMetadata entityMetadata;

    public RepositoryColumnObserverParser(EntityMetadata entityMetadata) {
        this.entityMetadata = entityMetadata;
    }

    @Override
    public String fireEntity(String entity) {
        return entityMetadata.getName();
    }

    @Override
    public String fireField(String entity, String field) {
        return Optional.ofNullable(entityMetadata.getColumnField(field)).orElse(field);
    }
}
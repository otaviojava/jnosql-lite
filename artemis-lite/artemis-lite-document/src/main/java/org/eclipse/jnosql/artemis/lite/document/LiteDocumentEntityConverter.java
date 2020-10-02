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
package org.eclipse.jnosql.artemis.lite.document;

import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.mapping.document.DocumentEntityConverter;

public class LiteDocumentEntityConverter implements DocumentEntityConverter {

    @Override
    public DocumentEntity toDocument(Object entityInstance) {
        return null;
    }

    @Override
    public <T> T toEntity(Class<T> entityClass, DocumentEntity entity) {
        return null;
    }

    @Override
    public <T> T toEntity(T entityInstance, DocumentEntity entity) {
        return null;
    }

    @Override
    public <T> T toEntity(DocumentEntity entity) {
        return null;
    }
}
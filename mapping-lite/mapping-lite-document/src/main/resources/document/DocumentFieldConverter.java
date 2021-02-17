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

import jakarta.nosql.document.Document;
import org.eclipse.jnosql.mapping.lite.metadata.ClassMappings;
import org.eclipse.jnosql.mapping.lite.metadata.FieldMetadata;

import java.util.List;
import java.util.Optional;

interface DocumentFieldConverter {

    <X, Y, T> void convert(T instance, List<Document> documents, Optional<Document> document, FieldMetadata field,
                           LiteDocumentEntityConverter converter, ClassMappings mappings);
}

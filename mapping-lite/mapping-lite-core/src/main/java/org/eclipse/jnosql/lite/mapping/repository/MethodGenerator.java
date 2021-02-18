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
package org.eclipse.jnosql.lite.mapping.repository;

import jakarta.nosql.mapping.DatabaseType;

import java.util.List;

public interface MethodGenerator {

    List<String> getLines();

    static MethodGenerator of(MethodMetadata metadata) {
        DatabaseType type = metadata.getType();
        switch (type) {
            case DOCUMENT:
                return new DocumentMethodGenerator(metadata);
            default:
                throw new UnsupportedOperationException("There is not support to the type " + type);
        }
    }
}
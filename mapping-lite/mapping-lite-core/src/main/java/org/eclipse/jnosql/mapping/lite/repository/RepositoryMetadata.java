/*
 *  Copyright (c) 2021 Otavio Santana and others
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

import java.time.LocalDateTime;

final class RepositoryMetadata {

    private final RepositoryElement element;

    public RepositoryMetadata(RepositoryElement element) {
        this.element = element;
    }

    public String getQualified() {
        return this.getPackage() + '.' + getClassName();
    }

    public String getClassName() {
        return this.element.getSimpleName() + "LiteDocument";
    }

    public String getPackage() {
        return element.getPackage();
    }

    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    public String getEntityType() {
        return this.element.getEntityType();
    }

    public String getKeyType() {
        return this.element.getKeyType();
    }
}

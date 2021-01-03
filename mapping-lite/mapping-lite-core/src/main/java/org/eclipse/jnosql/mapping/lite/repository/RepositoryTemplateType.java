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

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import jakarta.nosql.mapping.DatabaseType;

import java.util.function.Supplier;

enum RepositoryTemplateType implements Supplier<Mustache> {
    DOCUMENT("repository_document.mustache");

    private final String fileName;

    private final Mustache template;

    RepositoryTemplateType(String fileName) {
        this.fileName = fileName;
        MustacheFactory factory = new DefaultMustacheFactory();
        this.template = factory.compile(fileName);
    }

    static RepositoryTemplateType of(DatabaseType type) {
        switch (type) {
            case DOCUMENT:
                return DOCUMENT;
            default:
                throw new UnsupportedOperationException("There is not template to this database type: " + type);
        }
    }

    @Override
    public Mustache get() {
        return template;
    }
}

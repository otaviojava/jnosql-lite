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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.function.Supplier;

final class RepositoryAnalyzer implements Supplier<String> {

    private final Element entity;

    private final ProcessingEnvironment processingEnv;

    RepositoryAnalyzer(Element repositoryType, ProcessingEnvironment processingEnv) {
        this.entity = repositoryType;
        this.processingEnv = processingEnv;
    }

    @Override
    public String get() {
        RepositoryElement element = RepositoryElement.of(entity, processingEnv);

        return element.getClassName();
    }

}

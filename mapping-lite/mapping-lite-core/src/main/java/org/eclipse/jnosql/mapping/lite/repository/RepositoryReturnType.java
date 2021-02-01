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

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.eclipse.jnosql.mapping.lite.ProcessorUtil.extractFromType;

enum RepositoryReturnType implements Function<MethodMetadata, List<String>> {
    STREAM {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            String line = "Stream<" + extractFromType(metadata.getReturnType()) + "> result = this.template.select(query)";
            return singletonList(line);
        }
    };

    static RepositoryReturnType of(TypeElement returnElement) {
        String returnType = returnElement.getQualifiedName().toString();
        switch (returnType) {
            case "java.util.stream.Stream":
                return STREAM;
            default:
                throw new UnsupportedOperationException("This return is not supported: " + returnType);
        }

    }
}
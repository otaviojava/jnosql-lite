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

import org.eclipse.jnosql.mapping.lite.ProcessorUtil;

import java.util.ArrayList;
import java.util.List;

class DocumentMethodGenerator implements MethodGenerator {

    private final MethodMetadata metadata;

    DocumentMethodGenerator(MethodMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public List<String> getLines() {
        List<String> lines = new ArrayList<>();
        lines.add("jakarta.nosql.query.SelectQuery selectQuery = selectProvider.apply(\"" + metadata.getMethodName() + "\", metadata.getName())");
        lines.add("jakarta.nosql.document.DocumentQueryParams queryParams = converter.apply(selectQuery, parser)");
        lines.add("jakarta.nosql.Params params = queryParams.getParams()");
        for (Parameter parameter : this.metadata.getParameters()) {
            lines.add("params.bind(\"" + parameter.getName() + "\"," + parameter.getName() + ")");
        }
        lines.add("jakarta.nosql.document.DocumentQuery query = queryParams.getQuery()");
        RepositoryReturnType returnType = RepositoryReturnType.of(metadata.getReturnElement());
        lines.addAll(returnType.apply(this.metadata));
        return lines;
    }
}

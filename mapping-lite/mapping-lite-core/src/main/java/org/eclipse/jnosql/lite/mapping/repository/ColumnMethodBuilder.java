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

import jakarta.data.repository.Param;
import jakarta.data.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

enum ColumnMethodBuilder implements Function<MethodMetadata, List<String>> {

    METHOD_QUERY {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            lines.add("jakarta.nosql.query.SelectQuery selectQuery = selectProvider.apply(\"" + metadata.getMethodName() + "\", metadata.getName())");
            lines.add("jakarta.nosql.column.ColumnQueryParams queryParams = converter.apply(selectQuery, parser)");
            lines.add("jakarta.nosql.Params params = queryParams.getParams()");
            for (Parameter parameter : metadata.getParameters()) {
                lines.add("params.bind(\"" + parameter.getName() + "\"," + parameter.getName() + ")");
            }
            lines.add("jakarta.nosql.column.ColumnQuery query = queryParams.getQuery()");
            MethodQueryRepositoryReturnType returnType = MethodQueryRepositoryReturnType.of(metadata);
            lines.addAll(returnType.apply(metadata));
            return lines;
        }
    }, ANNOTATION_QUERY {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            Query query = metadata.getQuery();
            lines.add("jakarta.nosql.PreparedStatement prepare = template.prepare(\"" + query.value() + "\")");
            for (Parameter parameter : metadata.getParameters()) {
                if (parameter.hasParam()) {
                    Param param = parameter.getParam();
                    lines.add("prepare.bind(\"" + param.value() + "\"," + parameter.getName() + ")");
                }
            }
            AnnotationQueryRepositoryReturnType returnType = AnnotationQueryRepositoryReturnType.of(metadata);
            lines.addAll(returnType.apply(metadata));
            return lines;
        }
    };

    static ColumnMethodBuilder of(MethodMetadata metadata) {
        if (metadata.hasQuery()) {
            return ANNOTATION_QUERY;
        } else {
            return METHOD_QUERY;
        }
    }
}

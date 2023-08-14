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
            lines.add("org.eclipse.jnosql.communication.query.method.SelectMethodQueryProvider supplier = \n\t\t\t\t" +
                    "new org.eclipse.jnosql.communication.query.method.SelectMethodQueryProvider()");
            lines.add("org.eclipse.jnosql.communication.query.SelectQuery selectQuery = \n\t\t\t\t" +
                    "supplier.apply(\"" + metadata.getMethodName() + "\", metadata.name())");
            lines.add("org.eclipse.jnosql.communication.column.ColumnObserverParser parser = \n\t\t\t\t" +
                    "org.eclipse.jnosql.mapping.column.query.RepositoryColumnObserverParser.of(metadata)");
            lines.add("org.eclipse.jnosql.communication.column.ColumnQueryParams queryParams = \n\t\t\t\t" +
                    "SELECT_PARSER.apply(selectQuery, parser)");
            lines.add("org.eclipse.jnosql.communication.column.ColumnQuery query = queryParams.query()");
            lines.add("org.eclipse.jnosql.communication.Params params = queryParams.params()");
            for (Parameter parameter : metadata.getParameters()) {
                lines.add("params.bind(\"" + parameter.getName() + "\"," + parameter.getName() + ")");
            }
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
    }, NOT_SUPPORTED {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            return List.of("There is no support for this method type yet.");
        }
    };

    static ColumnMethodBuilder of(MethodMetadata metadata) {
        if (metadata.getMethodName().startsWith("findBy")) {
            return METHOD_QUERY;
        } else if (metadata.hasQuery()) {
            return ANNOTATION_QUERY;
        }
        return NOT_SUPPORTED;
    }
}

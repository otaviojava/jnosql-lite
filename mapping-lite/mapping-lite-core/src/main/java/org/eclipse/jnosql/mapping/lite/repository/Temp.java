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

import jakarta.nosql.Params;
import jakarta.nosql.document.DocumentQuery;
import jakarta.nosql.document.DocumentQueryParams;
import jakarta.nosql.document.SelectQueryConverter;
import jakarta.nosql.mapping.document.DocumentTemplate;
import jakarta.nosql.query.SelectQuery;
import org.eclipse.jnosql.communication.query.method.FindByMethodQueryProvider;
import org.eclipse.jnosql.mapping.lite.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.lite.metadata.RepositoryDocumentObserverParser;

import java.util.stream.Stream;

public class Temp {

    private DocumentTemplate template;

    private EntityMetadata metadata;

    private FindByMethodQueryProvider selectProvider;

    private RepositoryDocumentObserverParser parser;

    private SelectQueryConverter converter;

    public Stream<Object> test(MethodMetadata method) {
        SelectQuery selectQuery = selectProvider.apply(method.getMethodName(), metadata.getName());
        DocumentQueryParams queryParams = converter.apply(selectQuery, parser);
        Params params = queryParams.getParams();
        params.bind("", "");
        DocumentQuery documentQuery = queryParams.getQuery();
        return this.template.select(documentQuery);

    }
}

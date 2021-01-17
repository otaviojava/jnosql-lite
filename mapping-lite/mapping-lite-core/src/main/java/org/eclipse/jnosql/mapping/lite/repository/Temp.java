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

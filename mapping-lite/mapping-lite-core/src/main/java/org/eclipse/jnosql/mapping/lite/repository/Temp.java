package org.eclipse.jnosql.mapping.lite.repository;

import jakarta.nosql.Params;
import jakarta.nosql.document.DocumentQueryParams;
import jakarta.nosql.document.SelectQueryConverter;
import jakarta.nosql.query.SelectQuery;
import org.eclipse.jnosql.communication.document.query.SelectQueryParser;
import org.eclipse.jnosql.communication.query.method.FindByMethodQueryProvider;
import org.eclipse.jnosql.mapping.lite.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.lite.metadata.RepositoryDocumentObserverParser;

public class Temp {


    public void test(MethodMetadata method, EntityMetadata entity) {

        FindByMethodQueryProvider findByMethodQueryProvider = new FindByMethodQueryProvider();
        SelectQuery query = findByMethodQueryProvider.apply(method.getMethodName(), entity.getName());
        RepositoryDocumentObserverParser parser = new RepositoryDocumentObserverParser(null);
        SelectQueryConverter converter = new SelectQueryParser();
        DocumentQueryParams queryParams = converter.apply(query, parser);
        Params params = queryParams.getParams();
        params.bind("", "");

    }
}

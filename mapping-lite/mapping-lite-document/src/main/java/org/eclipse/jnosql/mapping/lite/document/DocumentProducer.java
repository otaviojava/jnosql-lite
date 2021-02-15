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
package org.eclipse.jnosql.mapping.lite.document;

import jakarta.nosql.document.DocumentCollectionManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.function.Supplier;

@ApplicationScoped
public class DocumentProducer implements Supplier<DocumentCollectionManager> {

    private static final String KEY_DOCUMENT = "document";

    @Override
    @Produces
    public DocumentCollectionManager get() {
        DocumentCollectionConverter converter = new DocumentCollectionConverter();
        return converter.convert(KEY_DOCUMENT);
    }
}

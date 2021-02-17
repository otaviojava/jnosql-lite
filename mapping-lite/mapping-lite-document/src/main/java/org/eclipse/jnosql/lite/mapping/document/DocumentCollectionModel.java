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
package org.eclipse.jnosql.lite.mapping.document;

import jakarta.nosql.mapping.MappingException;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.Optional;

class DocumentCollectionModel {

    private static final String PROVIDER = "document.provider";

    public String getProvider() {
        Config config = ConfigProvider.getConfig();
        Optional<String> provider = config.getOptionalValue(PROVIDER, String.class);
        return provider
                .orElseThrow(() -> new MappingException("Define the provider class with MicroProfile Config settings the " +
                        PROVIDER + " value"));
    }

    public String getQualified() {
        return "org.eclipse.jnosql.lite.mapping.document.DocumentCollectionFactoryConverter";
    }
}

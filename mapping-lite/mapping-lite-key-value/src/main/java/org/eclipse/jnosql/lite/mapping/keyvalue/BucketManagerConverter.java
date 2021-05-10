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
package org.eclipse.jnosql.lite.mapping.keyvalue;

import jakarta.nosql.keyvalue.BucketManager;
import jakarta.nosql.keyvalue.BucketManagerFactory;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.Converter;

public class BucketManagerConverter implements Converter<BucketManager> {

    private static final String DATABASE_KEY = ".database";

    @Override
    public BucketManager convert(String value) {
        Config config = ConfigProvider.getConfig();
        BucketManagerFactoryConverter converter = new BucketManagerFactoryConverter();
        BucketManagerFactory factory = converter.convert(value);
        String database = config.getValue(value + DATABASE_KEY, String.class);
        return factory.getBucketManager(database);
    }
}

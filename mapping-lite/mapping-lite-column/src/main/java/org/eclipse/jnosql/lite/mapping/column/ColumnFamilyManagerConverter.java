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
package org.eclipse.jnosql.lite.mapping.column;

import jakarta.nosql.column.ColumnFamilyManager;
import jakarta.nosql.column.ColumnFamilyManagerFactory;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.Converter;

public class ColumnFamilyManagerConverter implements Converter<ColumnFamilyManager> {

    private static final String DATABASE_KEY = ".database";

    @Override
    public ColumnFamilyManager convert(String value) {
        Config config = ConfigProvider.getConfig();
        ColumnFamilyManagerFactoryConverter converter = new ColumnFamilyManagerFactoryConverter();
        ColumnFamilyManagerFactory factory = converter.convert(value);
        String database = config.getValue(value + DATABASE_KEY, String.class);
        return factory.get(database);
    }
}

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

import jakarta.nosql.Settings;
import jakarta.nosql.column.ColumnConfiguration;
import jakarta.nosql.column.ColumnFamilyManagerFactory;
import org.eclipse.jnosql.lite.mapping.metadata.SettingsConverter;
import org.eclipse.microprofile.config.spi.Converter;

public class ColumnFamilyManagerFactoryConverter implements Converter<ColumnFamilyManagerFactory> {

    @Override
    public ColumnFamilyManagerFactory convert(String value) {
        SettingsConverter settingsConverter = new SettingsConverter();
        ColumnConfiguration configuration = ColumnConfiguration.getConfiguration();
        Settings settings = settingsConverter.convert(value);
        return configuration.get(settings);
    }
}

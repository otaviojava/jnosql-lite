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

import jakarta.nosql.Settings;
import jakarta.nosql.keyvalue.BucketManagerFactory;
import jakarta.nosql.keyvalue.KeyValueConfiguration;
import org.eclipse.jnosql.lite.mapping.metadata.SettingsConverter;
import org.eclipse.microprofile.config.spi.Converter;

public class BucketManagerFactoryConverter implements Converter<BucketManagerFactory> {

    @Override
    public BucketManagerFactory convert(String value) {
        SettingsConverter settingsConverter = new SettingsConverter();
        KeyValueConfiguration configuration = KeyValueConfiguration.getConfiguration();
        Settings settings = settingsConverter.convert(value);
        return configuration.get(settings);
    }
}

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
package document;

import jakarta.nosql.Settings;
import jakarta.nosql.document.DocumentCollectionManagerFactory;
import jakarta.nosql.document.DocumentConfiguration;
import org.eclipse.jnosql.mapping.lite.metadata.SettingsConverter;
import org.eclipse.microprofile.config.spi.Converter;

public class DocumentCollectionFactoryConverter implements Converter<DocumentCollectionManagerFactory> {

    @Override
    public DocumentCollectionManagerFactory convert(String value) {
        SettingsConverter settingsConverter = new SettingsConverter();
        DocumentConfiguration configuration = null;
        Settings settings = settingsConverter.convert(value);
        return configuration.get(settings);
    }
}

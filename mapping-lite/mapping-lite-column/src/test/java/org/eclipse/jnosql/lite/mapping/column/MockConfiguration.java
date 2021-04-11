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
import jakarta.nosql.document.DocumentCollectionManager;
import jakarta.nosql.document.DocumentCollectionManagerFactory;
import jakarta.nosql.document.DocumentConfiguration;

public class MockConfiguration implements DocumentConfiguration {

    @Override
    public DocumentCollectionManagerFactory get() {
        return new MockFactory(Settings.of());
    }

    @Override
    public DocumentCollectionManagerFactory get(Settings settings) {
        return new MockFactory(settings);
    }

    public static class MockFactory implements DocumentCollectionManagerFactory {

        private final Settings settings;

        public MockFactory(Settings settings) {
            this.settings = settings;
        }

        @Override
        public <T extends DocumentCollectionManager> T get(String database) {
            return null;
        }

        @Override
        public void close() {

        }
    }
}

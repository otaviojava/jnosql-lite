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
import jakarta.nosql.column.ColumnConfiguration;
import jakarta.nosql.column.ColumnFamilyManager;
import jakarta.nosql.column.ColumnFamilyManagerFactory;

public class MockConfiguration implements ColumnConfiguration {

    @Override
    public ColumnFamilyManagerFactory get() {
        return new MockFactory(Settings.of());
    }

    @Override
    public ColumnFamilyManagerFactory get(Settings settings) {
        return new MockFactory(settings);
    }

    public static class MockFactory implements ColumnFamilyManagerFactory {

        private final Settings settings;

        public MockFactory(Settings settings) {
            this.settings = settings;
        }

        @Override
        public <T extends ColumnFamilyManager> T get(String database) {
            return null;
        }

        @Override
        public void close() {

        }
    }
}

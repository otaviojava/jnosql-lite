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


import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.document.DocumentConfiguration;
import org.eclipse.jnosql.communication.document.DocumentManager;
import org.eclipse.jnosql.communication.document.DocumentManagerFactory;

public class MockConfiguration implements DocumentConfiguration {


    @Override
    public DocumentManagerFactory apply(Settings settings) {
        return new MockFactory(settings);
    }

    public static class MockFactory implements DocumentManagerFactory {

        private final Settings settings;

        public MockFactory(Settings settings) {
            this.settings = settings;
        }

        @Override
        public void close() {

        }

        @Override
        public DocumentManager apply(String s) {
            return null;
        }
    }
}
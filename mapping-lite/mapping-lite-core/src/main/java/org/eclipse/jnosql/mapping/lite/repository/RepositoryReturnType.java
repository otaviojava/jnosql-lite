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
package org.eclipse.jnosql.mapping.lite.repository;

import org.eclipse.jnosql.mapping.lite.ProcessorUtil;

import java.util.List;
import java.util.function.BiConsumer;

enum RepositoryReturnType implements BiConsumer<MethodMetadata, List<String>> {
    STREAM {
        @Override
        public void accept(MethodMetadata metadata, List<String> lines) {
            lines.add("Stream<" + ProcessorUtil.extractFromType(metadata.getReturnType()) + "> result = this.template.select(query)");
        }
    };
}

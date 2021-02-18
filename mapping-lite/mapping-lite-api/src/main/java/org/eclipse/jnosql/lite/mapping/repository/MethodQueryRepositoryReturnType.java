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
package org.eclipse.jnosql.lite.mapping.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.singletonList;
import static org.eclipse.jnosql.lite.mapping.ProcessorUtil.extractFromType;

enum MethodQueryRepositoryReturnType implements Function<MethodMetadata, List<String>> {
    STREAM {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            String line = "Stream<" + getEntity(metadata) + "> result = this.template.select(query)";
            return singletonList(line);
        }
    }, LIST {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            lines.add("Stream<" + getEntity(metadata) + "> entities = this.template.select(query)");
            lines.add("java.util.List<" + getEntity(metadata) + "> result = entities.collect(java.util.stream.Collectors.toList())");
            return lines;
        }
    }, SET {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            lines.add("Stream<" + getEntity(metadata) + "> entities = this.template.select(query)");
            lines.add("java.util.Set<" + getEntity(metadata) + "> result = entities.collect(java.util.stream.Collectors.toSet())");
            return lines;
        }
    }, QUEUE {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            lines.add("Stream<" + getEntity(metadata) + "> entities = this.template.select(query)");
            lines.add("java.util.Queue<" + getEntity(metadata) + "> result = entities.collect(java.util.stream" +
                    ".Collectors.toCollection(java.util.LinkedList::new)");
            return lines;
        }
    }, SORTED_SET {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            lines.add("Stream<" + getEntity(metadata) + "> entities = this.template.select(query)");
            lines.add("java.util.Queue<" + getEntity(metadata) + "> result = entities.collect(java.util.stream" +
                    ".Collectors.toCollection(java.util.TreeSet::new)");
            return lines;
        }
    }, OPTIONAL {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            lines.add("java.util.Optional<" + getEntity(metadata) + "> result = this.template.singleResult(query)");
            return lines;
        }
    },
    ENTITY_TYPE {
        @Override
        public List<String> apply(MethodMetadata metadata) {
            List<String> lines = new ArrayList<>();
            lines.add("java.util.Optional<" + getEntity(metadata) + "> entityResult = this.template.singleResult(query)");
            lines.add(getEntity(metadata) + " result = entityResult.orElse(null)");
            return lines;
        }
    };

    private static String getEntity(MethodMetadata metadata) {
        return extractFromType(metadata.getReturnType());
    }

    static MethodQueryRepositoryReturnType of(MethodMetadata metadata) {
        String returnType = metadata.getReturnElement().getQualifiedName().toString();
        if (returnType.equals(getEntity(metadata))) {
            return ENTITY_TYPE;
        }
        switch (returnType) {
            case "java.util.stream.Stream":
                return STREAM;
            case "java.util.List":
            case "java.util.Collection":
            case "java.lang.Iterable":
                return LIST;
            case "java.util.Set":
                return SET;
            case "java.util.Queue":
            case "java.util.Deque":
                return QUEUE;
            case "java.util.SortedSet":
            case "java.util.TreeSet":
                return SORTED_SET;
            case "java.util.Optional":
                return OPTIONAL;
            default:
                throw new UnsupportedOperationException("This return is not supported: " + returnType);
        }

    }
}
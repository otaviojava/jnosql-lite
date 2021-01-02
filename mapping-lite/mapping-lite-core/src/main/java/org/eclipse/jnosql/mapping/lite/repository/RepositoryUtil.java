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

import jakarta.nosql.mapping.Repository;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;

final class RepositoryUtil {

    static Optional<TypeMirror> findRepository(List<? extends TypeMirror> interfaces,
                                               ProcessingEnvironment processingEnv) {

        for (TypeMirror mirror : interfaces) {
            TypeElement element = (TypeElement) processingEnv.getTypeUtils().asElement(mirror);
            if (Repository.class.getName().equals(element.getQualifiedName().toString())) {
                return Optional.of(mirror);
            }
        }

        return Optional.empty();
    }
}

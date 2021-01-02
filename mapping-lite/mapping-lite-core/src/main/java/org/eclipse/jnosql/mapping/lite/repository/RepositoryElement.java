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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.eclipse.jnosql.mapping.lite.ProcessorUtil.isTypeElement;

class RepositoryElement {


    static RepositoryElement of(Element element, ProcessingEnvironment processingEnv) {
        if (isTypeElement(element)) {
            TypeElement typeElement = (TypeElement) element;
            Optional<TypeMirror> mirror = RepositoryUtil.findRepository(typeElement.getInterfaces(), processingEnv);
            if (!mirror.isEmpty()) {
                TypeMirror typeMirror = mirror.get();
                if (typeMirror instanceof DeclaredType) {
                    DeclaredType declaredType = (DeclaredType) typeMirror;
                    List<String> collect = declaredType.getTypeArguments().stream()
                            .map(TypeMirror::toString)
                            .collect(Collectors.toList());
                    System.out.println("" + collect);
                }
            }
        }
        throw new RuntimeException("");
    }
}

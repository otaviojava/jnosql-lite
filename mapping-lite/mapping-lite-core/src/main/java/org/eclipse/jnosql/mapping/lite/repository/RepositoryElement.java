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
import org.eclipse.jnosql.mapping.lite.ValidationException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;

import static org.eclipse.jnosql.mapping.lite.ProcessorUtil.isTypeElement;
import static org.eclipse.jnosql.mapping.lite.repository.RepositoryUtil.findRepository;

class RepositoryElement {

    private final ProcessingEnvironment processingEnv;

    private final Element element;

    private final String entityType;

    private final String keyType;

    public RepositoryElement(ProcessingEnvironment processingEnv, Element element,
                             String entityType, String keyType) {
        this.processingEnv = processingEnv;
        this.element = element;
        this.entityType = entityType;
        this.keyType = keyType;
    }

    public String getClassName() {
        return element.getKind().name();
    }

    static RepositoryElement of(Element element, ProcessingEnvironment processingEnv) {
        if (isTypeElement(element)) {
            TypeElement typeElement = (TypeElement) element;
            Optional<TypeMirror> mirror = findRepository(typeElement.getInterfaces(), processingEnv);
            if (!mirror.isEmpty()) {
                TypeMirror typeMirror = mirror.get();
                List<String> parameters = RepositoryUtil.findParameters(typeMirror);
                String entityType = parameters.get(0);
                String keyType = parameters.get(1);
                return new RepositoryElement(processingEnv, element, entityType, keyType);
            }
        }
        throw new ValidationException("The interface " + element.toString() + "must extends " + Repository.class.getName());
    }

}

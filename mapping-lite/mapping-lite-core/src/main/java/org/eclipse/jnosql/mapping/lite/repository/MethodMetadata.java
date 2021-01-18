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

import jakarta.nosql.mapping.Query;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

class MethodMetadata {

    private final String methodName;
    private final TypeElement returnElement;
    private final String returnType;
    private final List<Parameter> parameters;
    private final Query query;

    public MethodMetadata(String methodName, TypeElement returnElement, String returnType,
                          List<Parameter> parameters, Query query) {

        this.methodName = methodName;
        this.returnElement = returnElement;
        this.returnType = returnType;
        this.parameters = parameters;
        this.query = query;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getParametersSignature() {
        return parameters.stream().map(p -> p.getType().toString() + " " + p.getName())
                .collect(joining(","));
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<String> getSourceCode() {
        List<String> lines = new ArrayList<>();
        lines.add("SelectQuery selectQuery = selectProvider.apply(" + methodName + ", metadata.getName())");
        lines.add("DocumentQueryParams queryParams = converter.apply(selectQuery, parser);");
        lines.add("Params params = queryParams.getParams();");
        for (Parameter parameter : this.parameters) {
            lines.add("params.bind(\"" + parameter.getName() + "\"," + parameter.getName() + ")");
        }
        return lines;
    }

    public static MethodMetadata of(Element element, ProcessingEnvironment processingEnv) {
        ElementKind kind = element.getKind();
        if (ElementKind.METHOD.equals(kind)) {
            ExecutableElement method = (ExecutableElement) element;
            String methodName = method.getSimpleName().toString();
            TypeElement returnElement = (TypeElement) processingEnv.getTypeUtils().asElement(method.getReturnType());
            String returnType = method.getReturnType().toString();
            List<Parameter> parameters = method.getParameters()
                    .stream()
                    .map(e -> Parameter.of(e, processingEnv))
                    .collect(Collectors.toList());

            Query query = method.getAnnotation(Query.class);
            return new MethodMetadata(methodName, returnElement, returnType, parameters, query);
        }
        return null;
    }


}

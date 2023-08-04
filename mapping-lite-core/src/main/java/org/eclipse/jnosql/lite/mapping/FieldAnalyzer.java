/*
 *  Copyright (c) 2020 Otávio Santana and others
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
package org.eclipse.jnosql.lite.mapping;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import jakarta.nosql.Column;
import jakarta.nosql.Id;
import org.eclipse.jnosql.mapping.Convert;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FieldAnalyzer implements Supplier<String> {

    private static final String TEMPLATE = "fieldmetadata.mustache";
    private static final Predicate<Element> IS_METHOD = el -> el.getKind() == ElementKind.METHOD;
    public static final Function<Element, String> ELEMENT_TO_STRING = el -> el.getSimpleName().toString();
    private final Element field;
    private final Mustache template;
    private final ProcessingEnvironment processingEnv;
    private final TypeElement entity;

    FieldAnalyzer(Element field, ProcessingEnvironment processingEnv,
                  TypeElement entity) {
        this.field = field;
        this.processingEnv = processingEnv;
        this.entity = entity;
        this.template = createTemplate();
    }

    @Override
    public String get() {
        FieldModel metadata = getMetaData();
        Filer filer = processingEnv.getFiler();
        JavaFileObject fileObject = getFileObject(metadata, filer);
        try (Writer writer = fileObject.openWriter()) {
            template.execute(writer, metadata);
        } catch (IOException exception) {
            throw new ValidationException("An error to compile the class: " +
                    metadata.getQualified(), exception);
        }
        return metadata.getQualified();
    }

    private JavaFileObject getFileObject(FieldModel metadata, Filer filer) {
        try {
            return filer.createSourceFile(metadata.getQualified(), entity);
        } catch (IOException exception) {
            throw new ValidationException("An error to create the class: " +
                    metadata.getQualified(), exception);
        }

    }

    private FieldModel getMetaData() {
        final String fieldName = field.getSimpleName().toString();
        final Predicate<Element> validName = el -> el.getSimpleName().toString()
                .contains(ProcessorUtil.capitalize(fieldName));
        final Predicate<String> hasGetterName = el -> el.equals("get" + ProcessorUtil.capitalize(fieldName));
        final Predicate<String> hasSetterName = el -> el.equals("set" + ProcessorUtil.capitalize(fieldName));
        final Predicate<String> hasIsName = el -> el.equals("is" + ProcessorUtil.capitalize(fieldName));

        final List<Element> accessors = processingEnv.getElementUtils()
                .getAllMembers(entity).stream()
                .filter(validName.and(IS_METHOD).and(EntityProcessor.HAS_ACCESS))
                .collect(Collectors.toList());

        final TypeMirror typeMirror = field.asType();
        String className;
        final List<String> arguments;
        if (typeMirror instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            arguments = declaredType.getTypeArguments().stream()
                    .map(TypeMirror::toString)
                    .collect(Collectors.toList());
            className = declaredType.asElement().toString();

        } else {
            className = typeMirror.toString();
            arguments = Collections.emptyList();
        }

        Column column = field.getAnnotation(Column.class);
        Id id = field.getAnnotation(Id.class);
        for (AnnotationMirror annotationMirror : field.getAnnotationMirrors()) {
            DeclaredType annotationType = annotationMirror.getAnnotationType();
            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
            System.out.println(annotationType);
            System.out.println(elementValues);
        }
        Convert convert = field.getAnnotation(Convert.class);
        final boolean isId = id != null;
        final String packageName = ProcessorUtil.getPackageName(entity);
        final String entityName = ProcessorUtil.getSimpleNameAsString(this.entity);
        final String name = getName(fieldName, column, id);


        final String getMethod = accessors.stream()
                .map(ELEMENT_TO_STRING)
                .filter(hasGetterName)
                .findFirst().orElseThrow(generateGetterError(fieldName, packageName, entityName,
                        "There is not valid getter method to the field: "));
        final String setMethod = accessors.stream()
                .map(ELEMENT_TO_STRING)
                .filter(hasSetterName.or(hasIsName))
                .findFirst().orElseThrow(generateGetterError(fieldName, packageName, entityName,
                        "There is not valid setter method to the field: "));

        return FieldModel.builder()
                .withPackageName(packageName)
                .withName(name)
                .withType(className)
                .withEntity(entityName)
                .withReader(getMethod)
                .withWriter(setMethod)
                .withFieldName(fieldName)
                .withId(isId)
                .withArguments(arguments)
                .withConverter(convert)
                .build();
    }

    private String getName(String fieldName, Column column, Id id) {
        if (id == null) {
            return column.value().isBlank() ? fieldName : column.value();
        } else {
            return id.value().isBlank() ? fieldName : id.value();
        }
    }

    private Supplier<ValidationException> generateGetterError(String fieldName, String packageName, String entity, String s) {
        return () -> new ValidationException(s + fieldName + " in the class: " + packageName + "." + entity);
    }

    private Mustache createTemplate() {
        MustacheFactory factory = new DefaultMustacheFactory();
        return factory.compile(TEMPLATE);
    }

}

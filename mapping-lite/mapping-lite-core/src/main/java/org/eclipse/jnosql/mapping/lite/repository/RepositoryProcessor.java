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

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@SupportedAnnotationTypes("org.eclipse.jnosql.mapping.lite.metadata.RepositoryLite")
public class RepositoryProcessor extends AbstractProcessor {

    private static final Logger LOGGER = Logger.getLogger(RepositoryProcessor.class.getName());

    private static final String TEMPLATE = "repository_document.mustache";
    private final Mustache template;

    public RepositoryProcessor() {
        this.template = createTemplate();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        LOGGER.info("Starting the repository processor");
        final List<String> repositories = new ArrayList<>();
        try {
            for (TypeElement annotation : annotations) {
                roundEnv.getElementsAnnotatedWith(annotation)
                        .stream().map(e -> new RepositoryAnalyzer(e, processingEnv))
                        .map(RepositoryAnalyzer::get)
                        .forEach(repositories::add);
            }
        } catch (Exception exception) {
            error(exception);
        }
        LOGGER.info("The repository processor has finished with " + repositories
                + " total of " + repositories.size() + " repositories");
        return false;
    }

    private Mustache createTemplate() {
        MustacheFactory factory = new DefaultMustacheFactory();
        return factory.compile(TEMPLATE);
    }

    private void error(Exception exception) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "failed to write extension file: "
                + exception.getMessage());
    }
}

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
package org.eclipse.jnosql.artemis.lite;

import jakarta.nosql.mapping.MappingException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class MetadataAppender {

    private static final String PACKAGE = "org.eclipse.jnosql.artemis.lite.metadata.";
    private static final String METADATA = "metadata";
    private final ProcessingEnvironment processingEnv;

    private MetadataAppender(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    void append() throws IOException, URISyntaxException {
        URL url = EntityProcessor.class.getClassLoader().getResource(METADATA);
        Stream<Path> walk = Files.walk(Paths.get(url.toURI()));
        walk.map(Path::getFileName)
                .map(Path::toString)
                .filter(s -> s.contains(".java"))
                .map(s -> s.substring(0, s.lastIndexOf(".")))
                .forEach(this::loadClass);
    }

    private void loadClass(String file) {
        try {
            Filer filer = processingEnv.getFiler();
            JavaFileObject fileObject = filer.createSourceFile(PACKAGE + file);
            try (Writer writer = fileObject.openWriter()) {
                final InputStream stream = MetadataAppender.class
                        .getClassLoader()
                        .getResourceAsStream(METADATA + "/" + file + ".java");
                String source = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines()
                        .collect(Collectors.joining("\n"));
                writer.append(source);
            }
        } catch (IOException exp) {
            throw new MappingException("There is an issue while it is loading the class: " + file, exp);
        }
    }

    public static void append(ProcessingEnvironment processingEnv) throws IOException, URISyntaxException {
        MetadataAppender appender = new MetadataAppender(processingEnv);
        appender.append();
    }
}

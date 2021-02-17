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
package org.eclipse.jnosql.mapping.document;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import jakarta.nosql.mapping.MappingException;
import org.eclipse.jnosql.mapping.lite.EntityProcessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SupportedAnnotationTypes("jakarta.nosql.mapping.Entity")
public class DocumentLiteProcessor extends AbstractProcessor {

    private static final Logger LOGGER = Logger.getLogger(DocumentLiteProcessor.class.getName());
    private static final String PACKAGE = "org.eclipse.jnosql.mapping.lite.metadata.document.";
    private static final String METADATA = "document";
    private static final String TEMPLATE = "DocumentCollectionFactoryConverter.mustache";

    private final AtomicBoolean needToExecute = new AtomicBoolean(true);

    private final Mustache template;

    public DocumentLiteProcessor() {
        this.template = createTemplate();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        synchronized (this) {
            if (needToExecute.get()) {
                long start = System.currentTimeMillis();
                LOGGER.info("Starting the document Lite Processor");
                copyDocumentLiteClasses();
                needToExecute.set(false);
                long end = System.currentTimeMillis() - start;
                LOGGER.info("Document Lite Processor has finished " + end + " ms");
            }
        }
        return false;
    }

    private void copyDocumentLiteClasses() {
        try {
            URL url = EntityProcessor.class.getClassLoader().getResource(METADATA);
            LOGGER.info("URL folder: " + url.toString());
            LOGGER.info("URI folder: " + url.toURI().toString());
            Stream<Path> path = Files.walk(getPath(url));
            path.map(Path::getFileName)
                    .map(Path::toString)
                    .filter(s -> s.contains(".java"))
                    .map(s -> s.substring(0, s.lastIndexOf(".")))
                    .distinct().forEach(this::loadClass);
        } catch (URISyntaxException | IOException exp) {
            throw new MappingException("There is an issue while it is loading the class", exp);
        }
    }

    private void loadClass(String file) {
        try {
            Filer filer = processingEnv.getFiler();
            JavaFileObject fileObject = filer.createSourceFile(PACKAGE + file);
            try (Writer writer = fileObject.openWriter()) {
                final InputStream stream = DocumentLiteProcessor.class
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

    private Path getPath(URL url) throws IOException, URISyntaxException {
        URI uri = url.toURI();
        if ("jar".equals(uri.getScheme())) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap(), null);
            return fileSystem.getPath("/" + METADATA);
        } else {
            return Paths.get(uri);
        }
    }

    private Mustache createTemplate() {
        MustacheFactory factory = new DefaultMustacheFactory();
        return factory.compile(TEMPLATE);
    }
}

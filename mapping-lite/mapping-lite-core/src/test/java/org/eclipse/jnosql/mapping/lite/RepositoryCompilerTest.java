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
package org.eclipse.jnosql.mapping.lite;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.io.IOException;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class RepositoryCompilerTest {

    @Test
    public void shouldCompile() throws IOException {

        final JavaFileObject javaFileObject = JavaFileObjects.forResource("Person.java");

        Compilation compilation = javac()
                .withClasspathFrom(this.getClass().getClassLoader())
                .withOptions()
                .withProcessors(new EntityProcessor())
                .withProcessors(new RepositoryProcessor())
                .compile(javaFileObject);
        assertThat(compilation).succeeded();
    }

    @Test
    public void shouldReturnConstructorIssue() throws IOException {
        final JavaFileObject javaFileObject = JavaFileObjects.forResource("Person2.java");
        Assertions.assertThrows(RuntimeException.class, () ->
                javac()
                        .withClasspathFrom(this.getClass().getClassLoader())
                        .withOptions()
                        .withProcessors(new EntityProcessor())
                        .compile(javaFileObject));
    }

}

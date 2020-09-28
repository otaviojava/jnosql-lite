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

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.soujava.medatadata.api.Entity;

import javax.tools.JavaFileObject;
import java.io.IOException;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class CompilerTest {


    @Test
    public void shouldCompile() throws IOException {

        final JavaFileObject javaFileObject = JavaFileObjects.forResource("Person.java");

        Compilation compilation = javac()
                .withClasspathFrom(Entity.class.getClassLoader())
                .withOptions()
                .withProcessors(new EntityProcessor())
                .compile(javaFileObject);
        assertThat(compilation).succeeded();
    }

    @Test
    public void shouldReturnConstructorIssue() throws IOException {
        final JavaFileObject javaFileObject = JavaFileObjects.forResource("Person2.java");
        Assertions.assertThrows(RuntimeException.class, () ->
                javac()
                        .withClasspathFrom(Entity.class.getClassLoader())
                        .withOptions()
                        .withProcessors(new EntityProcessor())
                        .compile(javaFileObject));
    }

    @Test
    public void shouldCheckColumnCompile() {

        final JavaFileObject javaFileObject = JavaFileObjects.forResource("Person3.java");

        Compilation compilation = javac()
                .withClasspathFrom(Entity.class.getClassLoader())
                .withOptions()
                .withProcessors(new EntityProcessor())
                .compile(javaFileObject);
        assertThat(compilation).succeeded();
    }

    @Test
    public void shouldUseDefaultMethod() {

        final JavaFileObject javaFileObject = JavaFileObjects.forResource("Person5.java");

        Compilation compilation = javac()
                .withClasspathFrom(Entity.class.getClassLoader())
                .withOptions()
                .withProcessors(new EntityProcessor())
                .compile(javaFileObject);
        assertThat(compilation).succeeded();
    }

    @Test
    public void shouldGetTheGenericInformation() {

        final JavaFileObject javaFileObject = JavaFileObjects.forResource("Person6.java");

        Compilation compilation = javac()
                .withClasspathFrom(Entity.class.getClassLoader())
                .withOptions()
                .withProcessors(new EntityProcessor())
                .compile(javaFileObject);
        assertThat(compilation).succeeded();
    }


    @Test
    public void shouldReturnAccessorIssue()  {
        final JavaFileObject javaFileObject = JavaFileObjects.forResource("Person4.java");
        Assertions.assertThrows(RuntimeException.class, () ->
                javac()
                        .withClasspathFrom(Entity.class.getClassLoader())
                        .withOptions()
                        .withProcessors(new EntityProcessor())
                        .compile(javaFileObject));
    }

    @Test
    public void shouldAddTwoClass()  {
        final JavaFileObject javaFileObject = JavaFileObjects.forResource("Person.java");
        final JavaFileObject javaFileObject2 = JavaFileObjects.forResource("Person5.java");

        Compilation compilation = javac()
                .withClasspathFrom(Entity.class.getClassLoader())
                .withOptions()
                .withProcessors(new EntityProcessor())
                .compile(javaFileObject, javaFileObject2);
        assertThat(compilation).succeeded();
    }

    @Test
    public void shouldCompileDefaultPackage() throws IOException {
        final JavaFileObject javaFileObject = JavaFileObjects.forResource("Person7.java");
        Compilation compilation = javac()
                .withClasspathFrom(Entity.class.getClassLoader())
                .withOptions()
                .withProcessors(new EntityProcessor())
                .compile(javaFileObject);
        assertThat(compilation).succeeded();
    }

    @Test
    public void shouldCompileProtected() throws IOException {
        final JavaFileObject javaFileObject = JavaFileObjects.forResource("Person8.java");
        Compilation compilation = javac()
                .withClasspathFrom(Entity.class.getClassLoader())
                .withOptions()
                .withProcessors(new EntityProcessor())
                .compile(javaFileObject);
        assertThat(compilation).succeeded();
    }


}

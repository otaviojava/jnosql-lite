package org.eclipse.jnosql.mapping.lite.document;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.eclipse.jnosql.mapping.document.DocumentLiteProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.io.IOException;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


public class CompilerTest {

    @Test
    public void shouldCompile() throws IOException {

        final JavaFileObject javaFileObject = JavaFileObjects.forResource("Person.java");

        Compilation compilation = javac()
                .withClasspathFrom(this.getClass().getClassLoader())
                .withOptions()
                .withProcessors(new DocumentLiteProcessor())
                .compile(javaFileObject);
        assertThat(compilation).succeeded();
    }
}

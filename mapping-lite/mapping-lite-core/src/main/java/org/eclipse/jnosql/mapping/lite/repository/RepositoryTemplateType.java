package org.eclipse.jnosql.mapping.lite.repository;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.util.function.Supplier;

public enum RepositoryTemplateType implements Supplier<Mustache> {
    DOCUMENT("repository_document.mustache");

    private final String fileName;

    private final Mustache template;

    RepositoryTemplateType(String fileName) {
        this.fileName = fileName;
        MustacheFactory factory = new DefaultMustacheFactory();
        this.template = factory.compile(fileName);
    }

    @Override
    public Mustache get() {
        return template;
    }
}

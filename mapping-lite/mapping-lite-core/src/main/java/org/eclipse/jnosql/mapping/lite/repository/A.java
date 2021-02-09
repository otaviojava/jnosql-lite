package org.eclipse.jnosql.mapping.lite.repository;


import java.util.stream.Stream;

public class A {

    public void sas() {
        jakarta.nosql.mapping.document.DocumentTemplate template = null;
        jakarta.nosql.mapping.PreparedStatement prepare = template.prepare("");
        prepare.bind("name", "name");
        Stream<Object> result = prepare.getResult();
    }
}

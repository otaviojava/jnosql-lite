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
package org.eclipse.jnosql.mapping.lite;

import org.eclipse.jnosql.lite.mapping.metadata.ClassMappings;
import org.eclipse.jnosql.lite.mapping.metadata.DefaultClassMappings;
import org.eclipse.jnosql.lite.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AnimalTest {


    private ClassMappings mappings;

    private EntityMetadata entityMetadata;

    @BeforeEach
    public void setUp() {
        this.mappings = new DefaultClassMappings();
        this.entityMetadata = this.mappings.get(Animal.class);
    }

    @Test
    public void shouldGetName() {
        Assertions.assertEquals("kind", entityMetadata.getName());
    }

    @Test
    public void shouldGetSimpleName() {
        Assertions.assertEquals(Animal.class.getSimpleName(), entityMetadata.getSimpleName());
    }

    @Test
    public void shouldGetClassName() {
        Assertions.assertEquals(Animal.class.getName(), entityMetadata.getClassName());
    }

    @Test
    public void shouldGetClassInstance() {
        Assertions.assertEquals(Animal.class, entityMetadata.getClassInstance());
    }

    @Test
    public void shouldGetId() {
        Optional<FieldMetadata> id = this.entityMetadata.getId();
        Assertions.assertTrue(id.isPresent());
    }

    @Test
    public void shouldCreateNewInstance() {
        Animal animal = entityMetadata.newInstance();
        Assertions.assertNotNull(animal);
        Assertions.assertTrue(animal instanceof Animal);
    }

    @Test
    public void shouldGetFieldsName() {
        List<String> fields = entityMetadata.getFieldsName();
        Assertions.assertEquals(2, fields.size());
        Assertions.assertTrue(fields.contains("name"));
        Assertions.assertTrue(fields.contains("color"));
    }

    @Test
    public void shouldGetFieldsGroupByName() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.getFieldsGroupByName();
        Assertions.assertNotNull(groupByName);
        Assertions.assertNotNull(groupByName.get("_id"));
        Assertions.assertNotNull(groupByName.get("color"));
    }

    @Test
    public void shouldGetter() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.getFieldsGroupByName();
        Animal animal = new Animal();
        animal.setColor("blue");
        animal.setName("dog");

        String name = this.entityMetadata.getColumnField("name");
        String color = this.entityMetadata.getColumnField("color");
        FieldMetadata fieldName = groupByName.get(name);
        FieldMetadata fieldColor = groupByName.get(color);

        Assertions.assertEquals("blue", fieldColor.read(animal));
        Assertions.assertEquals("dog", fieldName.read(animal));
    }

    @Test
    public void shouldSetter() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.getFieldsGroupByName();
        Animal animal = new Animal();

        String name = this.entityMetadata.getColumnField("name");
        String color = this.entityMetadata.getColumnField("color");
        FieldMetadata fieldName = groupByName.get(name);
        FieldMetadata fieldColor = groupByName.get(color);


        fieldColor.write(animal, "blue");
        fieldName.write(animal, "ada");
        Assertions.assertEquals("blue", fieldColor.read(animal));
        Assertions.assertEquals("ada", fieldName.read(animal));

    }

}

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

import org.eclipse.jnosql.artemis.lite.metadata.ClassMappings;
import org.eclipse.jnosql.artemis.lite.metadata.DefaultClassMappings;
import org.eclipse.jnosql.artemis.lite.metadata.EntityMetadata;
import org.eclipse.jnosql.artemis.lite.metadata.FieldMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PersonTest {


    private ClassMappings mappings;

    private EntityMetadata entityMetadata;

    @BeforeEach
    public void setUp() {
        this.mappings = new DefaultClassMappings();
        this.entityMetadata = this.mappings.get(Person.class);
    }

    @Test
    public void shouldGetName() {
        Assertions.assertEquals("Person", entityMetadata.getName());
    }

    @Test
    public void shouldGetSimpleName() {
        Assertions.assertEquals(Person.class.getSimpleName(), entityMetadata.getSimpleName());
    }

    @Test
    public void shouldGetClassName() {
        Assertions.assertEquals(Person.class.getName(), entityMetadata.getClassName());
    }

    @Test
    public void shouldGetClassInstance() {
        Assertions.assertEquals(Person.class, entityMetadata.getClassInstance());
    }

    @Test
    public void shouldGetId() {
        Optional<FieldMetadata> id = this.entityMetadata.getId();
        Assertions.assertTrue(id.isPresent());
    }

    @Test
    public void shouldCreateNewInstance() {
        Person person = entityMetadata.newInstance();
        Assertions.assertNotNull(person);
        Assertions.assertTrue(person instanceof Person);
    }

    @Test
    public void shouldGetFieldsName() {
        List<String> fields = entityMetadata.getFieldsName();
        Assertions.assertEquals(5, fields.size());
        Assertions.assertTrue(fields.contains("id"));
        Assertions.assertTrue(fields.contains("username"));
        Assertions.assertTrue(fields.contains("email"));
        Assertions.assertTrue(fields.contains("contacts"));
        Assertions.assertTrue(fields.contains("pet"));
    }

    @Test
    public void shouldGetFieldsGroupByName() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.getFieldsGroupByName();
        Assertions.assertNotNull(groupByName);
        Assertions.assertNotNull(groupByName.get("_id"));
        Assertions.assertNotNull(groupByName.get("native"));
        Assertions.assertNotNull(groupByName.get("email"));
        Assertions.assertNotNull(groupByName.get("contacts"));
        Assertions.assertNotNull(groupByName.get("pet"));
    }

    @Test
    public void shouldGetter() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.getFieldsGroupByName();
        Person person = new Person();
        person.setId(1L);
        person.setUsername("otaviojava");
        person.setEmail("otavio@java.com");
        person.setContacts(List.of("Poliana", "Maria"));
        Animal ada = new Animal();
        ada.setName("Ada");
        ada.setColor("black");
        person.setPet(ada);

        FieldMetadata id = groupByName.get("_id");
        FieldMetadata username = groupByName.get("native");
        FieldMetadata email = groupByName.get("email");
        FieldMetadata contacts = groupByName.get("contacts");
        FieldMetadata pet = groupByName.get("pet");

        Assertions.assertEquals(1L, id.read(person));
        Assertions.assertEquals("otaviojava", username.read(person));
        Assertions.assertEquals("otavio@java.com", email.read(person));
        Assertions.assertEquals(List.of("Poliana", "Maria"), contacts.read(person));
        Assertions.assertEquals(ada, pet.read(person));
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

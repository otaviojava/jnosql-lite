/*
 *  Copyright (c) 2022 Otávio Santana and others
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
package org.eclipse.jnosql.mapping.lite.inheritance;

import jakarta.nosql.mapping.DiscriminatorColumn;
import org.eclipse.jnosql.lite.mapping.metadata.ClassMappings;
import org.eclipse.jnosql.lite.mapping.metadata.DefaultClassMappings;
import org.eclipse.jnosql.lite.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.lite.mapping.metadata.InheritanceMetadata;
import org.eclipse.jnosql.mapping.lite.inheritance.EmailNotification;
import org.eclipse.jnosql.mapping.lite.inheritance.Notification;
import org.eclipse.jnosql.mapping.lite.inheritance.SmsNotification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SMSNotificationTest {


    private ClassMappings mappings;

    private EntityMetadata entityMetadata;

    @BeforeEach
    public void setUp() {
        this.mappings = new DefaultClassMappings();
        this.entityMetadata = this.mappings.get(SmsNotification.class);
    }

    @Test
    public void shouldGetName() {
        Assertions.assertEquals("Notification", entityMetadata.getName());
    }

    @Test
    public void shouldGetSimpleName() {
        Assertions.assertEquals(SmsNotification.class.getSimpleName(), entityMetadata.getSimpleName());
    }

    @Test
    public void shouldGetClassName() {
        Assertions.assertEquals(SmsNotification.class.getName(), entityMetadata.getClassName());
    }

    @Test
    public void shouldGetClassInstance() {
        Assertions.assertEquals(SmsNotification.class, entityMetadata.getClassInstance());
    }

    @Test
    public void shouldGetId() {
        Optional<FieldMetadata> id = this.entityMetadata.getId();
        Assertions.assertTrue(id.isPresent());
    }

    @Test
    public void shouldCreateNewInstance() {
        SmsNotification notification = entityMetadata.newInstance();
        Assertions.assertNotNull(notification);
        Assertions.assertTrue(notification instanceof SmsNotification);
    }

    @Test
    public void shouldGetFieldsName() {
        List<String> fields = entityMetadata.getFieldsName();
        Assertions.assertEquals(4, fields.size());
        Assertions.assertTrue(fields.contains("id"));
        Assertions.assertTrue(fields.contains("name"));
        Assertions.assertTrue(fields.contains("phone"));
    }

    @Test
    public void shouldGetFieldsGroupByName() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.getFieldsGroupByName();
        Assertions.assertNotNull(groupByName);
        Assertions.assertNotNull(groupByName.get("_id"));
        Assertions.assertNotNull(groupByName.get("phone"));
    }

    @Test
    public void shouldGetInheritanceMetadata() {
        InheritanceMetadata inheritance = this.entityMetadata.getInheritance()
                .orElseThrow();
        Assertions.assertEquals("SMS", inheritance.getDiscriminatorValue());
        Assertions.assertEquals(DiscriminatorColumn.DEFAULT_DISCRIMINATOR_COLUMN, inheritance.getDiscriminatorColumn());
        Assertions.assertEquals(SmsNotification.class, inheritance.getEntity());
        Assertions.assertEquals(Notification.class, inheritance.getParent());
    }

}
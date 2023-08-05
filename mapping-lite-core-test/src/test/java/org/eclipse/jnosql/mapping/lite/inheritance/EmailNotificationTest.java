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

import org.eclipse.jnosql.mapping.metadata.EntitiesMetadata;
import org.eclipse.jnosql.lite.mapping.LiteEntitiesMetadata;
import org.eclipse.jnosql.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.mapping.metadata.InheritanceMetadata;
import org.eclipse.jnosql.mapping.DiscriminatorColumn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmailNotificationTest {


    private EntitiesMetadata mappings;

    private EntityMetadata entityMetadata;

    @BeforeEach
    public void setUp() {
        this.mappings = new LiteEntitiesMetadata();
        this.entityMetadata = this.mappings.get(EmailNotification.class);
    }

    @Test
    public void shouldGetName() {
        Assertions.assertEquals("Notification", entityMetadata.name());
    }

    @Test
    public void shouldGetSimpleName() {
        Assertions.assertEquals(EmailNotification.class.getSimpleName(), entityMetadata.simpleName());
    }

    @Test
    public void shouldGetClassName() {
        Assertions.assertEquals(EmailNotification.class.getName(), entityMetadata.className());
    }

    @Test
    public void shouldGetClassInstance() {
        Assertions.assertEquals(EmailNotification.class, entityMetadata.type());
    }

    @Test
    public void shouldGetId() {
        Optional<FieldMetadata> id = this.entityMetadata.id();
        Assertions.assertTrue(id.isPresent());
    }

    @Test
    public void shouldCreateNewInstance() {
        EmailNotification notification = entityMetadata.newInstance();
        Assertions.assertNotNull(notification);
        Assertions.assertTrue(notification instanceof EmailNotification);
    }

    @Test
    public void shouldGetFieldsName() {
        List<String> fields = entityMetadata.fieldsName();
        Assertions.assertEquals(4, fields.size());
        Assertions.assertTrue(fields.contains("id"));
        Assertions.assertTrue(fields.contains("name"));
        Assertions.assertTrue(fields.contains("email"));
    }

    @Test
    public void shouldGetFieldsGroupByName() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.fieldsGroupByName();
        Assertions.assertNotNull(groupByName);
        Assertions.assertNotNull(groupByName.get("_id"));
        Assertions.assertNotNull(groupByName.get("name"));
    }

    @Test
    public void shouldGetInheritanceMetadata() {
        InheritanceMetadata inheritance = this.entityMetadata.inheritance()
                .orElseThrow();
        Assertions.assertEquals("Email", inheritance.discriminatorValue());
        Assertions.assertEquals(DiscriminatorColumn.DEFAULT_DISCRIMINATOR_COLUMN, inheritance.discriminatorColumn());
        Assertions.assertEquals(EmailNotification.class, inheritance.entity());
        Assertions.assertEquals(Notification.class, inheritance.parent());
    }

}

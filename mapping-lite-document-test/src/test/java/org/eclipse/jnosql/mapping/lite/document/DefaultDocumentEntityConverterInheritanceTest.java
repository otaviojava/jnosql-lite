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
package org.eclipse.jnosql.mapping.lite.document;

import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.mapping.MappingException;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import org.eclipse.jnosql.lite.mapping.document.LiteDocumentEntityConverter;
import org.eclipse.jnosql.lite.mapping.entities.inheritance.EmailNotification;
import org.eclipse.jnosql.lite.mapping.entities.inheritance.LargeProject;
import org.eclipse.jnosql.lite.mapping.entities.inheritance.Project;
import org.eclipse.jnosql.lite.mapping.entities.inheritance.SmallProject;
import org.eclipse.jnosql.lite.mapping.entities.inheritance.SmsNotification;
import org.eclipse.jnosql.lite.mapping.entities.inheritance.SocialMediaNotification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DefaultDocumentEntityConverterInheritanceTest {

    private DocumentEntityConverter converter;

    @BeforeEach
    public void init() {
        this.converter = new LiteDocumentEntityConverter();
    }

    @Test
    public void shouldConvertProjectToSmallProject() {
        DocumentEntity entity = DocumentEntity.of("Project");
        entity.add("_id", "Small Project");
        entity.add("investor", "Otavio Santana");
        entity.add("size", "Small");
        Project project = this.converter.toEntity(entity);
        assertEquals("Small Project", project.getName());
        assertEquals(SmallProject.class, project.getClass());
        SmallProject smallProject = SmallProject.class.cast(project);
        assertEquals("Otavio Santana", smallProject.getInvestor());
    }

    @Test
    public void shouldConvertProjectToLargeProject() {
        DocumentEntity entity = DocumentEntity.of("Project");
        entity.add("_id", "Large Project");
        entity.add("budget", BigDecimal.TEN);
        entity.add("size", "Large");
        Project project = this.converter.toEntity(entity);
        assertEquals("Large Project", project.getName());
        assertEquals(LargeProject.class, project.getClass());
        LargeProject smallProject = LargeProject.class.cast(project);
        assertEquals(BigDecimal.TEN, smallProject.getBudget());
    }

    @Test
    public void shouldConvertLargeProjectToCommunicationEntity() {
        LargeProject project = new LargeProject();
        project.setName("Large Project");
        project.setBudget(BigDecimal.TEN);
        DocumentEntity entity = this.converter.toDocument(project);
        assertNotNull(entity);
        assertEquals("Project", entity.getName());
        assertEquals(project.getName(), entity.find("_id", String.class).get());
        assertEquals(project.getBudget(), entity.find("budget", BigDecimal.class).get());
        assertEquals("Large", entity.find("size", String.class).get());
    }

    @Test
    public void shouldConvertSmallProjectToCommunicationEntity() {
        SmallProject project = new SmallProject();
        project.setName("Small Project");
        project.setInvestor("Otavio Santana");
        DocumentEntity entity = this.converter.toDocument(project);
        assertNotNull(entity);
        assertEquals("Project", entity.getName());
        assertEquals(project.getName(), entity.find("_id", String.class).get());
        assertEquals(project.getInvestor(), entity.find("investor", String.class).get());
        assertEquals("Small", entity.find("size", String.class).get());
    }


    @Test
    public void shouldConvertProject() {
        DocumentEntity entity = DocumentEntity.of("Project");
        entity.add("_id", "Project");
        entity.add("size", "Project");
        Project project = this.converter.toEntity(entity);
        assertEquals("Project", project.getName());
    }

    @Test
    public void shouldConvertProjectToCommunicationEntity() {
        Project project = new Project();
        project.setName("Large Project");
        DocumentEntity entity = this.converter.toDocument(project);
        assertNotNull(entity);
        assertEquals("Project", entity.getName());
        assertEquals(project.getName(), entity.find("_id", String.class).get());
        assertEquals("Project", entity.find("size", String.class).get());
    }

    @Test
    public void shouldConvertDocumentEntityToSocialMedia(){
        LocalDate date = LocalDate.now();
        DocumentEntity entity = DocumentEntity.of("Notification");
        entity.add("_id", 100L);
        entity.add("name", "Social Media");
        entity.add("nickname", "otaviojava");
        entity.add("createdOn",date);
        entity.add("dtype", SocialMediaNotification.class.getSimpleName());
        SocialMediaNotification notification = this.converter.toEntity(entity);
        assertEquals(100L, notification.getId());
        assertEquals("Social Media", notification.getName());
        assertEquals("otaviojava", notification.getNickname());
        assertEquals(date, notification.getCreatedOn());
    }

    @Test
    public void shouldConvertDocumentEntityToSms(){
        LocalDate date = LocalDate.now();
        DocumentEntity entity = DocumentEntity.of("Notification");
        entity.add("_id", 100L);
        entity.add("name", "SMS Notification");
        entity.add("phone", "+351987654123");
        entity.add("createdOn", date);
        entity.add("dtype", "SMS");
        SmsNotification notification = this.converter.toEntity(entity);
        Assertions.assertEquals(100L, notification.getId());
        Assertions.assertEquals("SMS Notification", notification.getName());
        Assertions.assertEquals("+351987654123", notification.getPhone());
        assertEquals(date, notification.getCreatedOn());
    }

    @Test
    public void shouldConvertDocumentEntityToEmail(){
        LocalDate date = LocalDate.now();
        DocumentEntity entity = DocumentEntity.of("Notification");
        entity.add("_id", 100L);
        entity.add("name", "Email Notification");
        entity.add("email", "otavio@otavio.test");
        entity.add("createdOn", date);
        entity.add("dtype", "Email");
        EmailNotification notification = this.converter.toEntity(entity);
        Assertions.assertEquals(100L, notification.getId());
        Assertions.assertEquals("Email Notification", notification.getName());
        Assertions.assertEquals("otavio@otavio.test", notification.getEmail());
        assertEquals(date, notification.getCreatedOn());
    }

    @Test
    public void shouldConvertSocialMediaToCommunicationEntity(){
        SocialMediaNotification notification = new SocialMediaNotification();
        notification.setId(100L);
        notification.setName("Social Media");
        notification.setCreatedOn(LocalDate.now());
        notification.setNickname("otaviojava");
        DocumentEntity entity = this.converter.toDocument(notification);
        assertNotNull(entity);
        assertEquals("Notification", entity.getName());
        assertEquals(notification.getId(), entity.find("_id", Long.class).get());
        assertEquals(notification.getName(), entity.find("name", String.class).get());
        assertEquals(notification.getNickname(), entity.find("nickname", String.class).get());
        assertEquals(notification.getCreatedOn(), entity.find("createdOn", LocalDate.class).get());
    }

    @Test
    public void shouldConvertSmsToCommunicationEntity(){
        SmsNotification notification = new SmsNotification();
        notification.setId(100L);
        notification.setName("SMS");
        notification.setCreatedOn(LocalDate.now());
        notification.setPhone("+351123456987");
        DocumentEntity entity = this.converter.toDocument(notification);
        assertNotNull(entity);
        assertEquals("Notification", entity.getName());
        assertEquals(notification.getId(), entity.find("_id", Long.class).get());
        assertEquals(notification.getName(), entity.find("name", String.class).get());
        assertEquals(notification.getPhone(), entity.find("phone", String.class).get());
        assertEquals(notification.getCreatedOn(), entity.find("createdOn", LocalDate.class).get());
    }

    @Test
    public void shouldConvertEmailToCommunicationEntity(){
        EmailNotification notification = new EmailNotification();
        notification.setId(100L);
        notification.setName("Email Media");
        notification.setCreatedOn(LocalDate.now());
        notification.setEmail("otavio@otavio.test.com");
        DocumentEntity entity = this.converter.toDocument(notification);
        assertNotNull(entity);
        assertEquals("Notification", entity.getName());
        assertEquals(notification.getId(), entity.find("_id", Long.class).get());
        assertEquals(notification.getName(), entity.find("name", String.class).get());
        assertEquals(notification.getEmail(), entity.find("email", String.class).get());
        assertEquals(notification.getCreatedOn(), entity.find("createdOn", LocalDate.class).get());
    }

    @Test
    public void shouldReturnErrorWhenConvertMissingColumn(){
        LocalDate date = LocalDate.now();
        DocumentEntity entity = DocumentEntity.of("Notification");
        entity.add("_id", 100L);
        entity.add("name", "SMS Notification");
        entity.add("phone", "+351987654123");
        entity.add("createdOn", date);
        Assertions.assertThrows(MappingException.class, ()-> this.converter.toEntity(entity));
    }

    @Test
    public void shouldReturnErrorWhenMismatchField() {
        LocalDate date = LocalDate.now();
        DocumentEntity entity = DocumentEntity.of("Notification");
        entity.add("_id", 100L);
        entity.add("name", "Email Notification");
        entity.add("email", "otavio@otavio.test");
        entity.add("createdOn", date);
        entity.add("type", "Wrong");
        Assertions.assertThrows(MappingException.class, ()-> this.converter.toEntity(entity));
    }
}

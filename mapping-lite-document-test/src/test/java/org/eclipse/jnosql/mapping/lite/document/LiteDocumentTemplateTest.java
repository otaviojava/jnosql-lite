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
package org.eclipse.jnosql.mapping.lite.document;

import jakarta.data.exceptions.NonUniqueResultException;
import jakarta.enterprise.inject.Instance;
import org.eclipse.jnosql.communication.document.Document;
import org.eclipse.jnosql.communication.document.DocumentDeleteQuery;
import org.eclipse.jnosql.communication.document.DocumentEntity;
import org.eclipse.jnosql.communication.document.DocumentManager;
import org.eclipse.jnosql.communication.document.DocumentQuery;
import org.eclipse.jnosql.lite.mapping.document.LiteDocumentTemplate;
import org.eclipse.jnosql.lite.mapping.entities.Job;
import org.eclipse.jnosql.lite.mapping.entities.Movie;
import org.eclipse.jnosql.lite.mapping.entities.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.eclipse.jnosql.communication.document.DocumentDeleteQuery.delete;
import static org.eclipse.jnosql.communication.document.DocumentQuery.select;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LiteDocumentTemplateTest {

    private final Person person = Person.builder().
            withAge().
            withPhones(Arrays.asList("234", "432")).
            withName("Name")
            .withId(19)
            .withIgnore().build();

    private final Document[] documents = new Document[]{
            Document.of("age", 10),
            Document.of("phones", Arrays.asList("234", "432")),
            Document.of("name", "Name"),
            Document.of("id", 19L),
    };

    private DocumentManager managerMock;

    private LiteDocumentTemplate subject;

    private ArgumentCaptor<DocumentEntity> captor;

    @BeforeEach
    public void setUp() {
        managerMock = Mockito.mock(DocumentManager.class);
        captor = ArgumentCaptor.forClass(DocumentEntity.class);
        Instance<DocumentManager> instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(managerMock);
        this.subject = new LiteDocumentTemplate(managerMock);
    }

    @Test
    public void shouldInsert() {
        DocumentEntity document = DocumentEntity.of("Person");
        document.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock
                .insert(any(DocumentEntity.class)))
                .thenReturn(document);

        subject.insert(this.person);
        verify(managerMock).insert(captor.capture());
        DocumentEntity value = captor.getValue();
        assertEquals("Person", value.name());
        assertEquals(4, value.documents().size());
    }

    @Test
    public void shouldMergeOnInsert() {
        DocumentEntity document = DocumentEntity.of("Person");
        document.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock
                .insert(any(DocumentEntity.class)))
                .thenReturn(document);

        Person person = Person.builder().build();
        Person result = subject.insert(person);
        verify(managerMock).insert(captor.capture());
        DocumentEntity value = captor.getValue();
        assertSame(person, result);
        assertEquals(10, person.getAge());
    }


    @Test
    public void shouldSaveTTL() {

        Duration twoHours = Duration.ofHours(2L);

        DocumentEntity document = DocumentEntity.of("Person");
        document.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock.insert(any(DocumentEntity.class),
                Mockito.eq(twoHours)))
                .thenReturn(document);

        subject.insert(this.person, twoHours);
        verify(managerMock).insert(captor.capture(), Mockito.eq(twoHours));
        DocumentEntity value = captor.getValue();
        assertEquals("Person", value.name());
        assertEquals(4, value.documents().size());
    }


    @Test
    public void shouldUpdate() {
        DocumentEntity document = DocumentEntity.of("Person");
        document.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock
                .update(any(DocumentEntity.class)))
                .thenReturn(document);

        subject.update(this.person);
        verify(managerMock).update(captor.capture());
        DocumentEntity value = captor.getValue();
        assertEquals("Person", value.name());
        assertEquals(4, value.documents().size());
    }

    @Test
    public void shouldMergeOnUpdate() {
        DocumentEntity document = DocumentEntity.of("Person");
        document.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock
                .update(any(DocumentEntity.class)))
                .thenReturn(document);

        Person person = Person.builder().build();
        Person result = subject.update(person);
        verify(managerMock).update(captor.capture());
        DocumentEntity value = captor.getValue();
        assertSame(person, result);
        assertEquals(10, person.getAge());
    }

    @Test
    public void shouldInsertEntitiesTTL() {
        DocumentEntity documentEntity = DocumentEntity.of("Person");
        documentEntity.addAll(Stream.of(documents).collect(Collectors.toList()));
        Duration duration = Duration.ofHours(2);

        Mockito.when(managerMock
                .insert(any(DocumentEntity.class), Mockito.eq(duration)))
                .thenReturn(documentEntity);

        subject.insert(Arrays.asList(person, person), duration);
        verify(managerMock, times(2)).insert(any(DocumentEntity.class), any(Duration.class));
    }

    @Test
    public void shouldInsertEntities() {
        DocumentEntity documentEntity = DocumentEntity.of("Person");
        documentEntity.addAll(Stream.of(documents).collect(Collectors.toList()));

        Mockito.when(managerMock
                .insert(any(DocumentEntity.class)))
                .thenReturn(documentEntity);

        subject.insert(Arrays.asList(person, person));
        verify(managerMock, times(2)).insert(any(DocumentEntity.class));
    }

    @Test
    public void shouldUpdateEntities() {
        DocumentEntity documentEntity = DocumentEntity.of("Person");
        documentEntity.addAll(Stream.of(documents).collect(Collectors.toList()));

        Mockito.when(managerMock
                .update(any(DocumentEntity.class)))
                .thenReturn(documentEntity);

        subject.update(Arrays.asList(person, person));
        verify(managerMock, times(2)).update(any(DocumentEntity.class));
    }


    @Test
    public void shouldDelete() {

        DocumentDeleteQuery query = delete().from("delete").build();
        subject.delete(query);
        verify(managerMock).delete(query);
    }

    @Test
    public void shouldSelect() {
        DocumentQuery query = select().from("delete").build();
        subject.select(query);
        verify(managerMock).select(query);
    }


    @Test
    public void shouldReturnSingleResult() {
        DocumentEntity documentEntity = DocumentEntity.of("Person");
        documentEntity.addAll(Stream.of(documents).collect(Collectors.toList()));

        Mockito.when(managerMock
                .select(any(DocumentQuery.class)))
                .thenReturn(Stream.of(documentEntity));

        DocumentQuery query = select().from("person").build();

        Optional<Person> result = subject.singleResult(query);
        assertTrue(result.isPresent());
    }

    @Test
    public void shouldReturnSingleResultIsEmpty() {
        Mockito.when(managerMock
                .select(any(DocumentQuery.class)))
                .thenReturn(Stream.empty());

        DocumentQuery query = select().from("person").build();

        Optional<Person> result = subject.singleResult(query);
        assertFalse(result.isPresent());
    }

    @Test
    public void shouldReturnErrorWhenThereMoreThanASingleResult() {
        Assertions.assertThrows(NonUniqueResultException.class, () -> {
            DocumentEntity documentEntity = DocumentEntity.of("Person");
            documentEntity.addAll(Stream.of(documents).collect(Collectors.toList()));

            Mockito.when(managerMock
                    .select(any(DocumentQuery.class)))
                    .thenReturn(Stream.of(documentEntity, documentEntity));

            DocumentQuery query = select().from("person").build();

            subject.singleResult(query);
        });
    }

    @Test
    public void shouldReturnErrorWhenFindIdHasIdNull() {
        Assertions.assertThrows(NullPointerException.class, () -> subject.find(Person.class, null));
    }

    @Test
    public void shouldReturnErrorWhenFindIdHasClassNull() {
        Assertions.assertThrows(NullPointerException.class, () -> subject.find(null, "10"));
    }

    @Test
    public void shouldReturnErrorWhenThereIsNotIdInFind() {
        Assertions.assertThrows(IdNotFoundException.class, () -> subject.find(Job.class, "10"));
    }

    @Test
    public void shouldReturnFind() {
        subject.find(Person.class, "10");
        ArgumentCaptor<DocumentQuery> queryCaptor = ArgumentCaptor.forClass(DocumentQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        DocumentQuery query = queryCaptor.getValue();
        DocumentCondition condition = query.getCondition().get();

        assertEquals("Person", query.getDocumentCollection());
        assertEquals(DocumentCondition.eq(Document.of("_id", 10L)), condition);

    }

    @Test
    public void shouldDeleteEntity() {
        subject.delete(Person.class, "10");
        ArgumentCaptor<DocumentDeleteQuery> queryCaptor = ArgumentCaptor.forClass(DocumentDeleteQuery.class);
        verify(managerMock).delete(queryCaptor.capture());
        DocumentDeleteQuery query = queryCaptor.getValue();
        DocumentCondition condition = query.getCondition().get();

        assertEquals("Person", query.getDocumentCollection());
        assertEquals(DocumentCondition.eq(Document.of("_id", 10L)), condition);

    }

    @Test
    public void shouldExecuteQuery() {
        Stream<Person> people = subject.query("select * from Person");
        ArgumentCaptor<DocumentQuery> queryCaptor = ArgumentCaptor.forClass(DocumentQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        DocumentQuery query = queryCaptor.getValue();
        assertEquals("Person", query.getDocumentCollection());
    }

    @Test
    public void shouldConvertEntity() {
        Stream<Movie> movies = subject.query("select * from Movie");
        ArgumentCaptor<DocumentQuery> queryCaptor = ArgumentCaptor.forClass(DocumentQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        DocumentQuery query = queryCaptor.getValue();
        assertEquals("movie", query.getDocumentCollection());
    }

    @Test
    public void shouldPreparedStatement() {
        PreparedStatement preparedStatement = subject.prepare("select * from Person where name = @name");
        preparedStatement.bind("name", "Ada");
        preparedStatement.getResult();
        ArgumentCaptor<DocumentQuery> queryCaptor = ArgumentCaptor.forClass(DocumentQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        DocumentQuery query = queryCaptor.getValue();
        assertEquals("Person", query.getDocumentCollection());
    }

    @Test
    public void shouldCount() {
        subject.count("Person");
        verify(managerMock).count("Person");
    }

    @Test
    public void shouldCountFromEntityClass() {
        subject.count(Person.class);
        verify(managerMock).count("Person");
    }

}
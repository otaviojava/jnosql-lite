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
package org.eclipse.jnosql.mapping.lite.column;

import jakarta.nosql.NonUniqueResultException;
import jakarta.nosql.column.Column;
import jakarta.nosql.column.ColumnCondition;
import jakarta.nosql.column.ColumnDeleteQuery;
import jakarta.nosql.column.ColumnEntity;
import jakarta.nosql.column.ColumnFamilyManager;
import jakarta.nosql.column.ColumnQuery;
import jakarta.nosql.mapping.IdNotFoundException;
import jakarta.nosql.mapping.PreparedStatement;
import jakarta.nosql.mapping.column.ColumnTemplate;
import org.eclipse.jnosql.lite.mapping.column.LiteColumnTemplate;
import org.eclipse.jnosql.lite.mapping.entities.Job;
import org.eclipse.jnosql.lite.mapping.entities.Movie;
import org.eclipse.jnosql.lite.mapping.entities.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.enterprise.inject.Instance;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jakarta.nosql.column.ColumnDeleteQuery.delete;
import static jakarta.nosql.column.ColumnQuery.select;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LiteColumnTemplateTest {

    private final Person person = Person.builder().
            withAge().
            withPhones(Arrays.asList("234", "432")).
            withName("Name")
            .withId(19)
            .withIgnore().build();

    private final Column[] documents = new Column[]{
            Column.of("age", 10),
            Column.of("phones", Arrays.asList("234", "432")),
            Column.of("name", "Name"),
            Column.of("id", 19L),
    };

    private ColumnFamilyManager managerMock;

    private ColumnTemplate subject;

    private ArgumentCaptor<ColumnEntity> captor;

    @BeforeEach
    public void setUp() {
        managerMock = Mockito.mock(ColumnFamilyManager.class);
        captor = ArgumentCaptor.forClass(ColumnEntity.class);
        Instance<ColumnFamilyManager> instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(managerMock);
        this.subject = new LiteColumnTemplate(managerMock);
    }

    @Test
    public void shouldInsert() {
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock
                .insert(any(ColumnEntity.class)))
                .thenReturn(entity);

        subject.insert(this.person);
        verify(managerMock).insert(captor.capture());
        ColumnEntity value = captor.getValue();
        assertEquals("Person", value.getName());
        assertEquals(4, value.getColumns().size());
    }

    @Test
    public void shouldMergeOnInsert() {
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock
                .insert(any(ColumnEntity.class)))
                .thenReturn(entity);

        Person person = Person.builder().build();
        Person result = subject.insert(person);
        verify(managerMock).insert(captor.capture());
        ColumnEntity value = captor.getValue();
        assertSame(person, result);
        assertEquals(10, person.getAge());
    }


    @Test
    public void shouldSaveTTL() {

        Duration twoHours = Duration.ofHours(2L);

        ColumnEntity document = ColumnEntity.of("Person");
        document.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock.insert(any(ColumnEntity.class),
                Mockito.eq(twoHours)))
                .thenReturn(document);

        subject.insert(this.person, twoHours);
        verify(managerMock).insert(captor.capture(), Mockito.eq(twoHours));
        ColumnEntity value = captor.getValue();
        assertEquals("Person", value.getName());
        assertEquals(4, value.getColumns().size());
    }


    @Test
    public void shouldUpdate() {
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock
                .update(any(ColumnEntity.class)))
                .thenReturn(entity);

        subject.update(this.person);
        verify(managerMock).update(captor.capture());
        ColumnEntity value = captor.getValue();
        assertEquals("Person", value.getName());
        assertEquals(4, value.getColumns().size());
    }

    @Test
    public void shouldMergeOnUpdate() {
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.addAll(Stream.of(documents).collect(Collectors.toList()));

        when(managerMock
                .update(any(ColumnEntity.class)))
                .thenReturn(entity);

        Person person = Person.builder().build();
        Person result = subject.update(person);
        verify(managerMock).update(captor.capture());
        ColumnEntity value = captor.getValue();
        assertSame(person, result);
        assertEquals(10, person.getAge());
    }

    @Test
    public void shouldInsertEntitiesTTL() {
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.addAll(Stream.of(documents).collect(Collectors.toList()));
        Duration duration = Duration.ofHours(2);

        Mockito.when(managerMock
                .insert(any(ColumnEntity.class), Mockito.eq(duration)))
                .thenReturn(entity);

        subject.insert(Arrays.asList(person, person), duration);
        verify(managerMock, times(2)).insert(any(ColumnEntity.class), any(Duration.class));
    }

    @Test
    public void shouldInsertEntities() {
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.addAll(Stream.of(documents).collect(Collectors.toList()));

        Mockito.when(managerMock
                .insert(any(ColumnEntity.class)))
                .thenReturn(entity);

        subject.insert(Arrays.asList(person, person));
        verify(managerMock, times(2)).insert(any(ColumnEntity.class));
    }

    @Test
    public void shouldUpdateEntities() {
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.addAll(Stream.of(documents).collect(Collectors.toList()));

        Mockito.when(managerMock
                .update(any(ColumnEntity.class)))
                .thenReturn(entity);

        subject.update(Arrays.asList(person, person));
        verify(managerMock, times(2)).update(any(ColumnEntity.class));
    }


    @Test
    public void shouldDelete() {

        ColumnDeleteQuery query = delete().from("delete").build();
        subject.delete(query);
        verify(managerMock).delete(query);
    }

    @Test
    public void shouldSelect() {
        ColumnQuery query = select().from("delete").build();
        subject.select(query);
        verify(managerMock).select(query);
    }


    @Test
    public void shouldReturnSingleResult() {
        ColumnEntity entity = ColumnEntity.of("Person");
        entity.addAll(Stream.of(documents).collect(Collectors.toList()));

        Mockito.when(managerMock
                .select(any(ColumnQuery.class)))
                .thenReturn(Stream.of(entity));

        ColumnQuery query = select().from("person").build();

        Optional<Person> result = subject.singleResult(query);
        assertTrue(result.isPresent());
    }

    @Test
    public void shouldReturnSingleResultIsEmpty() {
        Mockito.when(managerMock
                .select(any(ColumnQuery.class)))
                .thenReturn(Stream.empty());

        ColumnQuery query = select().from("person").build();

        Optional<Person> result = subject.singleResult(query);
        assertFalse(result.isPresent());
    }

    @Test
    public void shouldReturnErrorWhenThereMoreThanASingleResult() {
        Assertions.assertThrows(NonUniqueResultException.class, () -> {
            ColumnEntity documentEntity = ColumnEntity.of("Person");
            documentEntity.addAll(Stream.of(documents).collect(Collectors.toList()));

            Mockito.when(managerMock
                    .select(any(ColumnQuery.class)))
                    .thenReturn(Stream.of(documentEntity, documentEntity));

            ColumnQuery query = select().from("person").build();

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
        ArgumentCaptor<ColumnQuery> queryCaptor = ArgumentCaptor.forClass(ColumnQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        ColumnQuery query = queryCaptor.getValue();
        ColumnCondition condition = query.getCondition().get();

        assertEquals("Person", query.getColumnFamily());
        assertEquals(ColumnCondition.eq(Column.of("_id", 10L)), condition);

    }

    @Test
    public void shouldDeleteEntity() {
        subject.delete(Person.class, "10");
        ArgumentCaptor<ColumnDeleteQuery> queryCaptor = ArgumentCaptor.forClass(ColumnDeleteQuery.class);
        verify(managerMock).delete(queryCaptor.capture());
        ColumnDeleteQuery query = queryCaptor.getValue();
        ColumnCondition condition = query.getCondition().get();

        assertEquals("Person", query.getColumnFamily());
        assertEquals(ColumnCondition.eq(Column.of("_id", 10L)), condition);

    }

    @Test
    public void shouldExecuteQuery() {
        Stream<Person> people = subject.query("select * from Person");
        ArgumentCaptor<ColumnQuery> queryCaptor = ArgumentCaptor.forClass(ColumnQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        ColumnQuery query = queryCaptor.getValue();
        assertEquals("Person", query.getColumnFamily());
    }

    @Test
    public void shouldConvertEntity() {
        Stream<Movie> movies = subject.query("select * from Movie");
        ArgumentCaptor<ColumnQuery> queryCaptor = ArgumentCaptor.forClass(ColumnQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        ColumnQuery query = queryCaptor.getValue();
        assertEquals("movie", query.getColumnFamily());
    }

    @Test
    public void shouldPreparedStatement() {
        PreparedStatement preparedStatement = subject.prepare("select * from Person where name = @name");
        preparedStatement.bind("name", "Ada");
        preparedStatement.getResult();
        ArgumentCaptor<ColumnQuery> queryCaptor = ArgumentCaptor.forClass(ColumnQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        ColumnQuery query = queryCaptor.getValue();
        assertEquals("Person", query.getColumnFamily());
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
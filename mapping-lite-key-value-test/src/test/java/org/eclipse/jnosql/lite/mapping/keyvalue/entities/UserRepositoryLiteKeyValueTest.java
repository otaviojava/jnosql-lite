/*
 *   Copyright (c) 2023 Contributors to the Eclipse Foundation
 *    All rights reserved. This program and the accompanying materials
 *    are made available under the terms of the Eclipse Public License v1.0
 *    and Apache License v2.0 which accompanies this distribution.
 *    The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *    and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *    You may elect to redistribute this code under either of these licenses.
 *
 *    Contributors:
 *
 *    Otavio Santana
 */
package org.eclipse.jnosql.lite.mapping.keyvalue.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.nosql.keyvalue.KeyValueTemplate;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserRepositoryLiteKeyValueTest {

    @Mock
    private KeyValueTemplate template;

    @InjectMocks
    private UserRepositoryLiteKeyValue userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldSaveEntity() {
        User user = new User();
        userRepository.save(user);
        verify(template, times(1)).put(eq(user));
    }

    @Test
    public void shouldSaveAllEntities() {
        User user1 = new User();
        User user2 = new User();
        Iterable<User> entities = Arrays.asList(user1, user2);

        userRepository.saveAll(entities);

        verify(template, times(1)).insert(eq(entities));
    }

    @Test
    public void shouldDeleteById() {
        String id = "123";

        userRepository.deleteById(id);

        verify(template, times(1)).deleteByKey(eq(id));
    }

    @Test
    public void shouldFindById() {
        String id = "123";
        when(template.get(eq(id), eq(User.class))).thenReturn(Optional.of(new User()));

        userRepository.findById(id);

        verify(template, times(1)).get(eq(id), eq(User.class));
    }

    @Test
    public void shouldFindAllByIds() {
        String id1 = "123";
        String id2 = "456";
        Iterable<String> ids = Arrays.asList(id1, id2);
        when(template.get(eq(id1), eq(User.class))).thenReturn(Optional.of(new User()));
        when(template.get(eq(id2), eq(User.class))).thenReturn(Optional.of(new User()));

        userRepository.findAllById(ids);

        verify(template, times(2)).get(anyString(), eq(User.class));
    }

    // Add more tests for the remaining methods

}

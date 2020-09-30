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
import org.eclipse.jnosql.artemis.lite.metadata.EntityMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassMappingsTest {

    private ClassMappings mappings;

    @BeforeEach
    public void setUp() {
        this.mappings = new DefaultClassMappings();
    }

    @Test
    public void shouldReturnNPEWhenIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> this.mappings.get(null));
    }

    @Test
    public void shouldReturnFromClass() {
        EntityMetadata entityMetadata = this.mappings.get(Animal.class);
        Assertions.assertNotNull(entityMetadata);
        Assertions.assertEquals(Animal.class, entityMetadata.getClassInstance());
        Assertions.assertEquals(Car.class, mappings.get(Car.class).getClassInstance());
        Assertions.assertEquals(Car.class, mappings.get(Car.class).getClassInstance());
    }
}

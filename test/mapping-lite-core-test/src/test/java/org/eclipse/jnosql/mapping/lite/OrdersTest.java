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

import jakarta.nosql.mapping.AttributeConverter;
import org.eclipse.jnosql.artemis.lite.metadata.ClassMappings;
import org.eclipse.jnosql.artemis.lite.metadata.DefaultClassMappings;
import org.eclipse.jnosql.artemis.lite.metadata.EntityMetadata;
import org.eclipse.jnosql.artemis.lite.metadata.FieldMetadata;
import org.eclipse.jnosql.mapping.lite.Money;
import org.eclipse.jnosql.mapping.lite.Orders;
import org.eclipse.jnosql.mapping.lite.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrdersTest {


    private ClassMappings mappings;

    private EntityMetadata entityMetadata;

    @BeforeEach
    public void setUp() {
        this.mappings = new DefaultClassMappings();
        this.entityMetadata = this.mappings.get(Orders.class);
    }

    @Test
    public void shouldGetName() {
        Assertions.assertEquals("Orders", entityMetadata.getName());
    }

    @Test
    public void shouldGetSimpleName() {
        Assertions.assertEquals(Orders.class.getSimpleName(), entityMetadata.getSimpleName());
    }

    @Test
    public void shouldGetClassName() {
        Assertions.assertEquals(Orders.class.getName(), entityMetadata.getClassName());
    }

    @Test
    public void shouldGetClassInstance() {
        Assertions.assertEquals(Orders.class, entityMetadata.getClassInstance());
    }

    @Test
    public void shouldGetId() {
        Optional<FieldMetadata> id = this.entityMetadata.getId();
        Assertions.assertFalse(id.isPresent());
    }

    @Test
    public void shouldCreateNewInstance() {
        Orders orders = entityMetadata.newInstance();
        Assertions.assertNotNull(orders);
        Assertions.assertTrue(orders instanceof Orders);
    }

    @Test
    public void shouldGetFieldsName() {
        List<String> fields = entityMetadata.getFieldsName();
        Assertions.assertEquals(2, fields.size());
        Assertions.assertTrue(fields.contains("user"));
        Assertions.assertTrue(fields.contains("items"));
    }

    @Test
    public void shouldGetFieldsGroupByName() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.getFieldsGroupByName();
        Assertions.assertNotNull(groupByName);
        Assertions.assertNotNull(groupByName.get("user"));
        Assertions.assertNotNull(groupByName.get("items"));
    }

    @Test
    public void shouldGetter() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.getFieldsGroupByName();
        Orders orders = new Orders();
        orders.setUser("Poliana");

        Money money = new Money("USD", BigDecimal.TEN);
        Product product = new Product();
        product.setName("table");
        product.setValue(money);
        orders.setItems(Collections.singletonList(product));


        FieldMetadata user = groupByName.get("user");
        FieldMetadata items = groupByName.get("items");

        Assertions.assertEquals("Poliana", user.read(orders));
        Assertions.assertEquals(Collections.singletonList(product), items.read(orders));
    }

    @Test
    public void shouldSetter() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.getFieldsGroupByName();
        Orders orders = new Orders();

        Money money = new Money("USD", BigDecimal.TEN);
        Product product = new Product();
        product.setName("table");
        product.setValue(money);

        FieldMetadata user = groupByName.get("user");
        FieldMetadata items = groupByName.get("items");

        user.write(orders, "Poliana");
        items.write(orders, Collections.singletonList(product));

        Assertions.assertEquals("Poliana", user.read(orders));
        Assertions.assertEquals(Collections.singletonList(product), items.read(orders));
    }

    @Test
    public void shouldReturnGenerics() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.getFieldsGroupByName();
        FieldMetadata items = groupByName.get("items");
        List<Class<?>> arguments = items.getArguments();
        Assertions.assertArrayEquals(new Class<?>[]{Product.class}, arguments.toArray());
    }

    @Test
    public void shouldReturnConverter() {
        Map<String, FieldMetadata> groupByName = this.entityMetadata.getFieldsGroupByName();
        FieldMetadata items = groupByName.get("items");
        List<Class<?>> arguments = items.getArguments();
        Class<?> argument = arguments.stream().findFirst().get();
        EntityMetadata product = this.mappings.get(argument);
        FieldMetadata value = product.getFieldMapping("value").get();
        Optional<AttributeConverter<Money, String>> converter = value.getConverter();
        Assertions.assertNotNull(converter);
        Assertions.assertTrue(converter.isPresent());

        AttributeConverter<Money, String> attributeConverter =  converter.get();
        Assertions.assertNotNull(converter);
        Money money = new Money("USD", BigDecimal.TEN);
        String test = attributeConverter.convertToDatabaseColumn(money);
        Assertions.assertNotNull(test);
    }
}

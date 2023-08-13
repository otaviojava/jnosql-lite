package org.eclipse.jnosql.mapping.lite.keyvalue;

import jakarta.inject.Inject;
import org.eclipse.jnosql.communication.Value;
import org.eclipse.jnosql.communication.keyvalue.KeyValueEntity;
import org.eclipse.jnosql.lite.mapping.entities.Car;
import org.eclipse.jnosql.lite.mapping.entities.Person;
import org.eclipse.jnosql.lite.mapping.entities.Plate;
import org.eclipse.jnosql.lite.mapping.entities.User;
import org.eclipse.jnosql.lite.mapping.entities.Worker;
import org.eclipse.jnosql.mapping.Converters;
import org.eclipse.jnosql.mapping.IdNotFoundException;
import org.eclipse.jnosql.mapping.keyvalue.KeyValueEntityConverter;
import org.eclipse.jnosql.mapping.keyvalue.spi.KeyValueExtension;
import org.eclipse.jnosql.mapping.spi.EntityMetadataExtension;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableAutoWeld
@AddPackages(value = {Converters.class, KeyValueEntityConverter.class})
@AddExtensions({EntityMetadataExtension.class, KeyValueExtension.class})
public class KeyValueEntityConverterTest {

    @Inject
    private KeyValueEntityConverter converter;

    @Test
    public void shouldReturnNPEWhenEntityIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> converter.toKeyValue(null));
    }

    @Test
    public void shouldReturnErrorWhenThereIsNotKeyAnnotation() {
        Assertions.assertThrows(IdNotFoundException.class, () -> converter.toKeyValue(new Worker()));
    }

    @Test
    public void shouldReturnErrorWhenTheKeyIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            User user = new User(null, "name", 24);
            converter.toKeyValue(user);
        });
    }

    @Test
    public void shouldConvertToKeyValue() {
        User user = new User("nickname", "name", 24);
        KeyValueEntity keyValueEntity = converter.toKeyValue(user);
        assertEquals("nickname", keyValueEntity.key());
        assertEquals(user, keyValueEntity.value());
    }

    @Test
    public void shouldReturnNPEWhenKeyValueIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> converter.toEntity(User.class, null));
    }

    @Test
    public void shouldReturnNPEWhenClassIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> converter.toEntity(null,
                KeyValueEntity.of("user", new User("nickname", "name", 21))));
    }

    @Test
    public void shouldReturnErrorWhenTheKeyIsMissing() {
        Assertions.assertThrows(IdNotFoundException.class, () -> converter.toEntity(Worker.class,
                KeyValueEntity.of("worker", new Worker())));
    }

    @Test
    public void shouldConvertToEntity() {
        User expectedUser = new User("nickname", "name", 21);
        User user = converter.toEntity(User.class,
                KeyValueEntity.of("user", expectedUser));
        assertEquals(expectedUser, user);
    }

    @Test
    public void shouldConvertAndFeedTheKeyValue() {
        User expectedUser = new User("nickname", "name", 21);
        User user = converter.toEntity(User.class,
                KeyValueEntity.of("nickname", new User(null, "name", 21)));
        assertEquals(expectedUser, user);
    }

    @Test
    public void shouldConvertAndFeedTheKeyValueIfKeyAndFieldAreDifferent() {
        User expectedUser = new User("nickname", "name", 21);
        User user = converter.toEntity(User.class,
                KeyValueEntity.of("nickname", new User("newName", "name", 21)));
        assertEquals(expectedUser, user);
    }

    @Test
    public void shouldConvertValueToEntity() {
        User expectedUser = new User("nickname", "name", 21);
        User user = converter.toEntity(User.class, KeyValueEntity.of("nickname", Value.of(expectedUser)));
        assertEquals(expectedUser, user);
    }

    @Test
    public void shouldConvertToEntityKeyWhenThereIsConverterAnnotation() {
        Car car = new Car();
        car.setName("Ferrari");

        Car ferrari = converter.toEntity(Car.class, KeyValueEntity.of("123-BRL", car));
        assertEquals(Plate.of("123-BRL"), ferrari.getPlate());
        assertEquals(car.getName(), ferrari.getName());
    }

    @Test
    public void shouldConvertToKeyWhenThereIsConverterAnnotation() {
        Car car = new Car();
        car.setPlate(Plate.of("123-BRL"));
        car.setName("Ferrari");
        KeyValueEntity entity = converter.toKeyValue(car);

        Assertions.assertEquals("123-BRL", entity.key());
        Assertions.assertEquals(car, entity.value());
    }

    @Test
    public void shouldConvertToEntityKeyWhenKeyTypeIsDifferent() {

        Person person = Person.builder().withName("Ada").build();
        Person ada = converter.toEntity(Person.class, KeyValueEntity.of("123", person));

        Assertions.assertEquals(123L, ada.getId());
        Assertions.assertEquals(ada.getName(), person.getName());
    }

    @Test
    public void shouldConvertToKeyWhenKeyTypeIsDifferent() {
        Person person = Person.builder().withId(123L).withName("Ada").build();
        KeyValueEntity entity = converter.toKeyValue(person);
        Assertions.assertEquals(123L, entity.key());
    }
}

package org.eclipse.jnosql.artemis.lite;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Convert;
import jakarta.nosql.mapping.Entity;

@Entity
public class Worker extends Person {

    @Column
    @Convert(MoneyConverter.class)
    private Money salary;

    public Money getSalary() {
        return salary;
    }

    public void setSalary(Money salary) {
        this.salary = salary;
    }
}

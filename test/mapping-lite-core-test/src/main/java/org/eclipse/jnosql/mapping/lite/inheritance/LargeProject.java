package org.eclipse.jnosql.mapping.lite.inheritance;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.DiscriminatorValue;
import jakarta.nosql.mapping.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("Large")
public class LargeProject extends Project {

    @Column
    private BigDecimal budget;

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public BigDecimal getBudget() {
        return budget;
    }
}
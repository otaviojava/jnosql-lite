package org.eclipse.jnosql.mapping.lite.inheritance;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.DiscriminatorValue;
import jakarta.nosql.mapping.Entity;

@Entity
@DiscriminatorValue("Small")
public class SmallProject extends Project {

    @Column
    private String investor;

    public String getInvestor() {
        return investor;
    }

    public void setInvestor(String investor) {
        this.investor = investor;
    }
}
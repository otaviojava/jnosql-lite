package org.eclipse.jnosql.artemis.lite;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;

@Entity
public class ZipCode {

    @Column
    private String zip;

    @Column
    private String plusFour;

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPlusFour() {
        return plusFour;
    }

    public void setPlusFour(String plusFour) {
        this.plusFour = plusFour;
    }
}

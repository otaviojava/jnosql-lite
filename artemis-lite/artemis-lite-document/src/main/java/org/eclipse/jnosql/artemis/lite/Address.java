package org.eclipse.jnosql.artemis.lite;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;

@Entity
public class Address {

    @Column
    private String street;
    @Column
    private String city;
    @Column
    private String state;
    @Column
    private ZipCode zipCode;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ZipCode getZipCode() {
        return zipCode;
    }

    public void setZipCode(ZipCode zipCode) {
        this.zipCode = zipCode;
    }
}

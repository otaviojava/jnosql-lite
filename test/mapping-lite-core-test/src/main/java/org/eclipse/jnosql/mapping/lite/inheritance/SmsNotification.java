package org.eclipse.jnosql.mapping.lite.inheritance;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.DiscriminatorValue;
import jakarta.nosql.mapping.Entity;

@Entity
@DiscriminatorValue("SMS")
public class SmsNotification extends Notification {

    @Column
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String send() {
        return "Sending message to via sms to the phone: " + phone;
    }
}